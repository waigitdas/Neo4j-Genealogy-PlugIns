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


public class pedigree_complete {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String pedigree_completeness(
        @Name("rn") 
            Long rn
  )
   
         { 
             
        String s = create_report(rn);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String create_report(Long rn) 
    {
        String cq = "match p=(n:Person{RN:" + rn + "})-[[:father|mother*..99]]->(x) with reduce(n=0,q in nodes(p)|n+1) as gen RETURN gen, count(*) as Observed,2^(gen-1) as Expected,count(*)/(2^(gen-1)) as Ratio order by gen";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "pedigree_completeness", "data", 1, "", "0:##;1:###,###,###;2:###,###,###;3:#.#######;", "", true, cq, false);
        return "pedigree completeness report done";
    }
}
