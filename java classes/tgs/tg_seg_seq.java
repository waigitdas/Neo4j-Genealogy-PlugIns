/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.graphdb.Path;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class tg_seg_seq {
    @UserFunction
    @Description("not yet implemented.")

    public Path tg_seg_seq_graph(
        @Name("tgid") 
            Long tgid
  )
   
         { 
             
        Path p = show_seq(tgid);
         return p;
            }

    
    
      
     public Path show_seq(Long tgid) 
    {
        String cq = "match p=(m:DNA_Match)-[rm:match_segment]-(s:Segment)-[rs:seg_seq{tgid:" + tgid + "}]-() where m.ancestor_rn is not null and rm.p_anc_rn is not null and rm.m_anc_rn is not null and rm.cm>=7 and rm.snp_ct>=500 and s.mbp<200 return p";
        Path p = gen.neo4jlib.neo4j_qry.qry_path(cq);
        return p;
    }
}
