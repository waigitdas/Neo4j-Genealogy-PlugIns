/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class y_hg_anc_descendants {
    @UserFunction
    @Description("Y-haplogrops f descendants of the ancestor whose surname is the same as the project name")

    public String project_y_hgs(
  
  )
   
         { 
             
        String s = get_hgs();
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_hgs() 
    {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String cq ="MATCH p=(m:DNA_Match{surname:'" + gen.neo4jlib.neo4j_info.project + "'})-[r:match_block]->(b:block) where m.ancestor_rn>0 with m,b match bp=(b)<-[rb:block_child*0..99]-() return m.fullname as match,b.name as block,count(length(bp)) as hops_to_adam order by block,hops_to_adam desc";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "y_hg_desc", "Y-HG", 1, "", "2:###;2:###", "", true,"cypher query:\n" + cq, true);
        return "completed";
    }
}
