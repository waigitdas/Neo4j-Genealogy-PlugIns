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


public class project_surnames_Y_haplogroups {
    @UserFunction
    @Description("Y-HG for a surname.")

    public String surname_Y_haplogroups(
        @Name("search_term") 
            String search_term

  )
   
         { 
             
        get_hg_listing(search_term);
         return "";
            }

    
    
  
     public String get_hg_listing(String search_term) 
    {
         gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String[] ss = search_term.split(",");
        String label = ss[0];
        String s = "";
        for (int i=0; i<ss.length; i++)
        {
            s = s + "'" + ss[i].strip() + "'";
            if (i<ss.length-1){s = s + ",";}
        }

        String cq ="MATCH p=(m:DNA_Match)-[[r:match_block]]->(b:block) where toUpper(m.surname) in [[" + s.toUpperCase() + "]] with m,b match bp=(b)<-[[rb:block_child*0..99]]-() with m,b,count(length(bp)) as hops_to_adam match (b)-[[:block_snp]]->(v:variant) with m,b,hops_to_adam,v order by v.name with m,m.fullname as match,b.name as block,hops_to_adam,collect(v.name) as variants return match, block, hops_to_adam, size(variants) as variant_ct, variants order by m.surname, block,hops_to_adam desc";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "y_hg_desc", "Y-HG", 1, "", "2:###;3:###", "", true,"UDF:\nreturn gen.dna.project_y_hgs()\n\ncypher query:\n" + cq, true);
        
        
        return "completed";
    }
}
