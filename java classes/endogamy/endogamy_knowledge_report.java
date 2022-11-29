/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.endogamy;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class endogamy_knowledge_report {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String endogamy_knowledge(
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
        String cq = "MATCH (p:Person) where p.coi is not null RETURN gen.gedcom.person_from_rn(p.RN,true) as name ,p.coi as coi order by coi desc, name";
        String      excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_knowledge_graph", "person_COIs", ct, "", "0:#####;1:0.##########", "", false,"UDF:\nreturn gen.endogamy.endogamy_knowledge()\n\ncypher query:\n" +  cq + "\n\nShown are persons with endogamy and their coefficients of inbreeding.\nSorted by COI in descending order.\n\n", false);
   
        cq = "match (u:Union) where u.cor is not null with u match(p:Person) where p.RN=u.U1 or p.RN=u.U2 return collect(gen.gedcom.person_from_rn(p.RN,true)) as union_pair,u.uid as uid, u.cor as cor,u.rel as rel order by cor desc";
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_knowledge_graph", "unions_of_relatives", ct, "", "1:#####;2:0.##########", excelFile, true,"UDF:\nreturn gen.endogamy.endogamy_knowledge_graph()\n\ncypher query:\n" +  cq + "\n\nShown are unions in which the people are related.", false);
        
        
        
        return "completed";
    }
}
