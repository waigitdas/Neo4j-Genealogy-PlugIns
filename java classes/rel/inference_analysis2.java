/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class inference_analysis2{
    @UserFunction
    @Description("Template used in creating new functions.")

    public String inference_close_compare(
        @Name("close_rn") 
            Long close_rn,
        @Name("compare_rn") 
            Long compare_rn,
        @Name("mrca2") 
            String mrca2
  
  )
   
         { 
             
        String s = compute_infer(close_rn,compare_rn,mrca2);
         return s;
            }

    
    
    public static void main(String args[]) {
        compute_infer(343L,582L,"13, 14");
    }
    
     public static String compute_infer(Long close_rn,Long compare_rn, String mrca2) 
    {
        String cq="";
        String base_rn ="";
        String infer_rns = "";
        String path_grandparent = "";
        String infer_grandparent = "";
        String q = "\"";
        
//    //first, find relevant base_rn
//    cq = "MATCH p=(d:DNA_Match)-[r:match_segment]->() where (r.cor <0.125 or r.rel='Nibling') and r.rel=replace(r.rel,';','') and (r.p_rn=" + close_rn + " or r.m_rn=" + close_rn + ") with r, d, case when d.RN=r.p_rn then r.m else r.p end as close, case when d.RN=r.p_rn then r.m_rn else r.p_rn end as close_rn, case when d.RN=r.p_rn then r.p else r.m end as compare, case when d.RN=r.p_rn then r.p_rn else r.m_rn end as compare_rn RETURN close, close_rn,compare,compare_rn,r.cor,r.rel,r.mrca_rn, count(*)";
//    base_rn = gen.neo4jlib.neo4j_qry.qry_str(cq).replace("[","").replace("]","");

        
//         //get parent in path between base and compare; item 1 in path
//        try{
//           cq = "match path = (p:Person)-[r:father|mother*0..3]->(a:Person) where p.RN =" + base_rn + " and a.RN in [" + mrca2 + "] return distinct nodes(path)[1].RN + ';' + case when nodes(path)[1].sex='F' then 'maternal' else 'paternal' end as parent";
//        path_parent = gen.neo4jlib.neo4j_qry.qry_str(cq).replace("[","").replace("]","").replace("\"","");
//        }
//        catch(Exception e){}
//  
//        

       //find infere r to whom segment is assigned
        try{
            cq = "with " + close_rn + " as b, " + compare_rn + " as m, [" + mrca2 + "] as c match path = (p:Person)-[r:father|mother*0..6]->(a:Person) where p.RN = b and a.RN in c with distinct b,m,c,nodes(path)[2].RN as rtn where not rtn = b match path2=(p2:Person)-[r:father|mother*0..3]->(a2:Person) where p2.RN in c with rtn,a2.RN as arn where (a2.RN=nodes(path2)[length(path2)].RN or a2.RN=nodes(path2)[1].RN) and not rtn = m return distinct rtn";
        infer_grandparent = gen.neo4jlib.neo4j_qry.qry_str(cq).replace("[","").replace("]","");
    }
    catch(Exception e)
    {
    return "~";    
    }
     
     
//            //find infere rn to whom segment is assigned
//        try{
//            cq = "match (p:Person)-[rf:father|mother*2]->(a:Person{RN:" + infer_grandparent + "}) with p.RN as rn MATCH (d:DNA_Match)-[r:match_segment]->() where (r.cor >0.25 or r.rel='Nibling') and r.rel=replace(r.rel,';','') and (r.p_rn=rn or r.m_rn=rn) with distinct rn match path=(p2:Person{RN:rn})-[rf2:father|mother*2]->(a2:Person{RN:" + infer_grandparent + "}) return collect(distinct rn + ':' + case when nodes(path)[1].sex='F' then 'materal' else 'paternal' end ) + replace('" + infer_grandparent + ":'+ case when nodes(path)[3].sex = 'F' then 'maternal' else 'paternal' end," + q + ",'') as inferred";
//        path_grandparent = gen.neo4jlib.neo4j_qry.qry_str(cq).replace("[","").replace("]","");
//    }
//    catch(Exception e)
//    {
//    return "~";    
//    }
// 
//        //get grandparent above spouse who gets infered segments
//        if (path_grandparent!="") {
//       try{
//       cq="match (u:Union) where u.U1=" + path_grandparent + " or u.U2=" + path_grandparent + " with case when u.U1=" + path_grandparent + " then u.U2 else u.U1 end as rn,case when u.U1=" + path_grandparent + " then 'maternal' else 'paternal' end as side with rn,side match (p:Person)-[r:father|mother*0..3]->(a:Person) where p.RN=1 and a.RN=rn return a.RN as rn"; //' + ';' + side";
//        infer_grandparent = gen.neo4jlib.neo4j_qry.qry_str(cq).replace("[","").replace("]","").replace("\"", "");
//        }
//        catch(Exception e){}
//       }
        
        try{
         cq = "with " + infer_grandparent  + " as infer_grandparent match (p:Person)-[rf:father|mother*2]->(a:Person{RN:infer_grandparent}) with p.RN as rn, infer_grandparent MATCH (d:DNA_Match)-[r:match_segment]->() where r.rel=replace(r.rel,';','') and (r.p_rn=rn or r.m_rn=rn) return distinct rn, infer_grandparent";
               
         //cq = "match (p:Person)-[rf:father|mother*2]->(a:Person{RN:" + infer_grandparent  + "}) with p.RN as rn MATCH (d:DNA_Match)-[r:match_segment]->() where (r.cor >0.25 or r.rel='Nibling') and r.rel=replace(r.rel,';','') and (r.p_rn=rn or r.m_rn=rn) with distinct rn match path=(p2:Person{RN:rn})-[rf2:father|mother*2]->(a2:Person{RN:" + infer_grandparent  + "}) with path,rn,'" + infer_grandparent  + ":'+ case when nodes(path)[3].sex = 'F' then 'maternal' else 'paternal' end as side return collect(distinct rn + ':' + case when nodes(path)[1].sex='F' then 'materal' else 'paternal' end ) + side as inferred";
           //"match (p:Person)-[rf:father|mother*2]->(a:Person{RN:8}) with p.RN as rn MATCH (d:DNA_Match)-[r:match_segment]->() where (r.cor >0.25 or r.rel='Nibling') and r.rel=replace(r.rel,';','') and (r.p_rn=rn or r.m_rn=rn) return distinct collect(distinct rn)";
        //"match (p:Person)-[rf:father|mother*2]->(a:Person{RN:8}) with p.RN as rn MATCH (d:DNA_Match)-[r:match_segment]->() where (r.cor >0.25 or r.rel='Nibling') and r.rel=replace(r.rel,';','') and (r.p_rn=rn or r.m_rn=rn) return distinct collect(distinct rn)";
     infer_rns = gen.neo4jlib.neo4j_qry.qry_str(cq).replace("[","").replace("]","");
        }
        catch (Exception e){}


        String x = infer_rns + ";" + infer_grandparent;
        System.out.println(x);
        return x; 
    }
     
}
