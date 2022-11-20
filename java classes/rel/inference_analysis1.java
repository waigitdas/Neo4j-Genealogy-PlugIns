/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class inference_analysis1 {
    @UserFunction
    @Description("Evaluates segment inference opportunity")
    
    public String compute_inference(
        @Name("base_rn") 
            Long base_rn,
        @Name("close_rn") 
            Long close_rn,
        @Name("compare_rn") 
            Long compare_rn,
        @Name("mrca2") 
            String mrca2
   
  )
   
         { 
             
        String grandparents = evaluate_infer(base_rn,close_rn,compare_rn, mrca2);
         return grandparents;
         }

    
    public static void main(String args[]) {
//      evaluate_infer(210L,1L,27924L, "25, 26");
//       evaluate_infer(1L,209L,600L, "15, 16");
//        evaluate_infer(341L,334L,582L, "15, 16");
//        evaluate_infer(349L,216L,2044L, "9, 10");
//       evaluate_infer(209L,1L,2044L, "9, 10");
     }
    
     public String evaluate_infer(Long base_rn, Long close_rn, Long compare_rn, String mrca2)
    {
        //the close_rn is not relevant to these analytics other than thefact that he/she defines the MRCAs with the base.
        
        String path_parent = "";
        String path_grandparent = "" ;
        String infer_grandparent ="";
        //String base_close_rn = "[" + base_rn + "," + close_rn + "]";
         String cq1="";
        String cq2="";
        String cq3="";
         
        //get parent in path between base and compare; item 1 in path
        try{
           cq1 = "match path = (p:Person)-[r:father|mother*0..6]->(a:Person) where p.RN =" + base_rn + " and a.RN in [" + mrca2 + "] return distinct nodes(path)[1].RN + ':' + case when nodes(path)[1].sex='F' then 'maternal' else 'paternal' end as parent";
        path_parent = gen.neo4jlib.neo4j_qry.qry_str(cq1).replace("[","").replace("]","").replace("\"","");
        }
        catch(Exception e){}
        
        //get grandparent in path between base and compare, item 2 in path
        try{
            cq2 = "with [" + base_rn + "] as b, [" + mrca2 + "] as m, " + compare_rn + " as c  match path = (p:Person)-[r:father|mother*0..6]->(a:Person) where p.RN in b and a.RN in m with distinct b,m,c,nodes(path)[2].RN as rtn where not rtn in b match path2=(p2:Person)-[r:father|mother*0..3]->(a2:Person) where p2.RN=c with rtn,a2.RN as arn where a2.RN=nodes(path2)[length(path2)].RN and not rtn in m return distinct rtn";
        path_grandparent = gen.neo4jlib.neo4j_qry.qry_str(cq2).replace("[","").replace("]","");
        }
        catch(Exception e){}
        
        //get grandparent above spouse who gets infered segments
        if (path_grandparent!="") {
       try{
       cq3="match (u:Union) where u.U1=" + path_grandparent + " or u.U2=" + path_grandparent + " with case when u.U1=" + path_grandparent + " then u.U2 else u.U1 end as rn,case when u.U1=" + path_grandparent + " then 'maternal' else 'paternal' end as side with rn,side match (p:Person)-[r:father|mother*0..3]->(a:Person) where p.RN=" + base_rn + " and a.RN=rn return a.RN + ':' + side";
        infer_grandparent = gen.neo4jlib.neo4j_qry.qry_str(cq3).replace("[","").replace("]","").replace("\"", "");
        }
        catch(Exception e){}
       }
        
        String ip[] = path_parent.split(Pattern.quote(":"));
        String ig[] = infer_grandparent.split(Pattern.quote(":"));
        

String x="";
        
try{         
        x = base_rn +":" + close_rn +":" + compare_rn + ":" + mrca2.replace(" ","").replace(",",";") + ",  "  + base_rn + ":" + ip[1] + ";" + ip[0] + ":" + ig[1]  + ";" + ig[0] + ":" + "unknown"; 
        //System.out.println(x);
}
    catch(Exception e){x = "~";}
        return x; 
    }
}
