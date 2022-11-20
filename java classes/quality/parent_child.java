/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.quality;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class parent_child {
    @UserFunction
    @Description("Quality check: did a child have a segment not inheited from their parent?")

    public String parent_child_inheritance(
  )
   
         { 
             
        String s = get_rept();
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_rept() 
    {
        String cq = "MATCH (m:DNA_Match)-[r:match_segment]->(s:Segment) where r.rel='Parent/Child' and r.cm>=7 and r.snp_ct>=500 and r.p_rn>0 and r.m_rn>0    match (p1:Person)<-[rf:father|mother]-(p2:Person) where p1.RN in [r.p_rn,r.m_rn] and p2.RN in [r.p_rn, r.m_rn] return parent,seg,match_pair,ct ,pos ";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "parent_child", "check", 1, "", "3:####;4:####", "", true,"UDF:\nreturn gen.quality.parent_child_inheritance()\n\nCypher query:\n" + cq +"\n\nChildren should only have segments on this report that the inherited from their parents. ", true);
        return "completed";
    }
}
