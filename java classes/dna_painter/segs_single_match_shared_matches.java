/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna_painter;

import gen.neo4jlib.neo4j_qry;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class segs_single_match_shared_matches {
    @UserFunction
    @Description("finds segments a specific match shares with his/her matches.")

    public String DNA_Painter_shared_segs_matches_tomatch(
        @Name("names") 
            List<String> names
  )
   
         { 
             
        String s = get_segments(names);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_segments(List<String> names)
    {
        String s="[[";
        String Q = "'";
        for (int i=0; i<names.size(); i++){
            s = s + Q + names.get(i) + Q ;
            if (i < names.size()-1) {s = s + ",";}
            else {s = s + "]]";}   
        }
        
        String cq = "with " + s + " as names match (m:DNA_Match)-[[r:match_segment]]-(s:Segment) where r.p in names and r.m in names with s,r,names where gen.dna.chr_portion_of_segment(s.chr,id(r))<0.5 and (r.cor<0.5 or r.cor is null) with s as seg,r,names, collect(distinct case when r.p=names[[0]] or r.m=names[[0]] then case when r.p=names[[0]] then r.p + ':' + r.m else r.m + ':' + r.p end else null end) as match_pair with seg,match_pair,r,names where match_pair>[[]] return toInteger(case when seg.chr='0X'then 23 else seg.chr end) as chr,seg.strt_pos as start,seg.end_pos as end_pos,r.cm as cm,r.snp_ct as snps,match_pair as match, 'good' as confidence,names[[0]] as group,'maternal' as side,'' as notes,'green' as color order by seg.Indx";
        return cq;
  
    }
}
