/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class matches_at_seg {
    @UserFunction
    @Description("Returns list of matches at a segment whose boundaries are submitted")

    public String matches_with_anc_rn_at_segment(
        @Name("chr") 
            String chr,
        @Name("strt") 
            Long strt,
        @Name("end") 
            Long end
  )
   
         { 
             
        String m = get_matches(chr,strt,end);
         return m;
            }

    
    
    public static void main(String args[]) {
       //System.out.println(get_matches("21",Long.valueOf(9990360),Long.valueOf(31384543)));
    }
    
     public String get_matches(String chr,Long strt, Long end)
    {
        String cq=""; 
        String r = "";
        try{
            cq= "match (m:DNA_Match)-[r:match_segment]-(s:Segment) where m.ancestor_rn is not null and s.chr='" + chr + "' and s.strt_pos>=" + strt + " and s.end_pos<=" + end + " with m order by m.fullname with distinct m.fullname + ' ⦋' + m.RN + '⦌' as match return match";
        
        r = gen.genlib.listStrToStr.list_to_string(gen.neo4jlib.neo4j_qry.qry_str_list(cq));
    }
    catch (Exception e) {return "None";} 

    return r;
    }
}
