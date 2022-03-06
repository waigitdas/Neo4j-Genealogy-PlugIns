/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class segment_desc_matches {
    @UserFunction
    @Description("Finds matches descended from ancestot and mapping to the specified segment.")

    public String segment_ancestor_descendants(
       @Name("chr")
            String chr,
        @Name("strt") 
            Long strt,
        @Name("end") 
            Long end
   )
   
         { 
             
        String matches = get_matches(chr,strt,end);
         return matches;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_matches(String chr,Long strt,Long end) 
    {
        gen.rel.anc_rn arn = new gen.rel.anc_rn();
        Long anc_rn = arn.get_ancestor_rn();
  
    String cq = "match (s:Segment)-[[r:match_segment{m_anc_rn:" + anc_rn + ",p_anc_rn:" + anc_rn + ",p:m.fullname}]]-(m:DNA_Match{ancestor_rn:" + anc_rn + "}) where s.chr='" + chr + "' and s.end_pos>=" + strt + " and s.strt_pos<= " + end + " with m order by m.fullname with collect (distinct m.fullname + ' [' + m.RN + ']') as match return match";
    //match (s:Segment)-[r:match_segment{m_anc_rn:41,p_anc_rn:41,p:m.fullname}]-(m:DNA_Match{ancestor_rn:41}) where s.chr='" + chr + "' and s.end_pos>=" + strt + " and s.strt_pos<= " + end + " with m order by m.fullname with collect (distinct m.fullname + ' [' + m.RN + ']') as match,collect(distinct m.RN) as rns with match, reduce(s=\"\",r in rns|s + ',' + r) as rns2 with match,gen.rel.mrca_from_list(rns2,10) as mrcas return match + '|' + mrcas
     String m = gen.neo4jlib.neo4j_qry.qry_str(cq).replace("[[", "").replace("]]", ",").replace("\"", "").replace(",",";");
     return m ;
  
        
    }
}
