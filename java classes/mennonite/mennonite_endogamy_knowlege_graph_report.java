/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.mennonite;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class mennonite_endogamy_knowlege_graph_report {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String mennonite_endogamy_knowledge(
  )
   
         { 
             
        String s = create_report();
         return s;
            }

    
    
    public static void main(String args[]) {
        create_report();
    }
    
     public static String create_report() 
    {
        int ct = 1 ;
        
        //persons with coi
        //maximum number of rows exceeded
        
        String cq = "MATCH (p:Person) where p.coi >0 RETURN p.fullname as name,p.RN as RN,p.BD as BD, p.DD as DD, p.coi as coi, p.coi_gen as coi_gen order by coi desc, name"; 
                //"MATCH (p:Person) where p.coi >0 RETURN p.fullname as name,p.RN as RN,p.BD as BD, p.DD as DD, p.coi as coi, p.coi_gen as coi_gen order by coi desc, name";
        //String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "mennonite_knowledge_report", "cq", ct, "", "", "", true, cq, false);
        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "mennonite_knowledge_graph", "person_COIs", ct, "", "1:######;2:########;3:########;4:#.#########", "", false,"", false);
        ct = ct + 1;
        
//        //unions where members are related
        cq = "match (u:Union) where u.cor>0 with u match(p:Person) where p.RN=u.U1 or p.RN=u.U2 return collect(p.fullname  + ' [' + p.RN + '] (' + left(p.BD,4)  + '-' +  left(p.DD,4) + ') {' + p.coi + '}') as union_pair,u.uid as uid, u.cor as cor,u.rel as rel order by cor desc";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "mennonite_knowledge_graph", "unions_of_relatives", ct, "", "1:#####;2:0.##########", excelFile, true,"UDF:\nreturn gen.endogamy.endogamy_knowledge_graph()\n\ncypher query:\n" +  cq + "\n\nShown are unions in which the people are related.\nSorted by COR in descendding order.", false);
//        ct = ct + 1;
//      
  
        
        return "completed";
    }
}
