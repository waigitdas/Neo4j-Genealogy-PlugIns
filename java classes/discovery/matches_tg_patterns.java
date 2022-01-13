/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.discovery;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class matches_tg_patterns {
    @UserFunction
    @Description("Triangulation group patterns can be leveraged to discover higher value matches for future research because they are persons sharing multiple TGs with other in the family branch. The function returns clusters with or exceeding the number of TGs specified.")

    public String matches_by_tg_pattern(
        @Name("min_tg_cluster_size") 
            Long min_tg_cluster_size
  )
   
         { 
             
        get_matches(min_tg_cluster_size);
         return "";
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_matches(Long min_tg_cluster_size) 
    {
        String cq = "match (m:DNA_Match)-[[r:match_tg]]-(t:tg) with m,t order by t.tgid with m,collect(distinct t.tgid) as tc with m, tc where size(tc)>" + min_tg_cluster_size + "-1 return case when m.RN is null then m.fullname else m.fullname + ' ⦋' + m.RN + '⦌' end as fullname,size(tc) as ct,tc order by ct desc";
        gen.excelLib.queries_to_excel.qry_to_excel(cq,"matches from tg clusters", "matches", 1, "", "1:######", "", true, "UDF: return matches_by_tg_patterns(" + min_tg_cluster_size + ")\n\nquery:\n" + cq,true);
        return "completed";
    }
}
