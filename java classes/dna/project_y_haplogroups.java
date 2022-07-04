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


public class project_y_haplogroups {
    @UserFunction
    @Description("Y-haplogrops of descendants of the ancestor whose surname is the same as the project name")

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
        String cq ="MATCH p=(m:DNA_Match{surname:'" + gen.neo4jlib.neo4j_info.project + "'})-[r:match_block]->(b:block) where m.ancestor_rn>0 with m,b match bp=(b)<-[rb:block_child*0..99]-() with m,b,count(length(bp)) as hops_to_adam match (b)-[:block_snp]->(v:variant) with m,b,hops_to_adam,v order by v.name with m.fullname as match,b.name as block,hops_to_adam,collect(v.name) as variants return match, block, hops_to_adam, size(variants) as variant_ct, variants order by block,hops_to_adam desc";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "y_hg_desc", "Y-HG", 1, "", "2:###;3:###", "", true,"UDF:\nreturn gen.dna.project_y_hgs()\n\ncypher query:\n" + cq, true);
        return "completed";
    }
}
