/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;

import gen.genlib.current_date_time;
import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
import gen.neo4jlib.neo4j_info;
import gen.neo4jlib.neo4j_qry;
import gen.tgs.create_segment_sequence_edges;
import gen.rel.mrca_set_link_property;
import java.awt.Desktop;
import java.io.File;

public class set_ancestor_rn_seg_seq {
    @UserFunction
    @Description("Creates ancestor_rn property in person, DNA_match and kit nodes and then the sequence of segments mapping to descendants.. This facilitates queries in triangulation group reporting. The seq-seq edge enables rapid queries without directly computing overlaps.")

    public  String setup_tg_environment(
        @Name("ancestor_rn") 
            Long ancestor_rn
  )
         { 
             
         String s = setup(ancestor_rn);
         return s;
            }
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String setup(Long ancestor_rn) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();  //initialize variables
        
        //add ancestor_rn property to Person, Kit and DNA_Match nodes after erasing prior properties
        mrca_set_link_property s = new mrca_set_link_property();
        s.mrca_link_property(ancestor_rn );

        //create seqment sequences for all segments linked to descendants of the specified ancestor after removing previously created edges
        create_segment_sequence_edges e = new create_segment_sequence_edges();
        e.create_seg_seq_edges(ancestor_rn);
        
        //create at-haplotree Excel
        String  SaveFileNm = "at_haplotree_anc_"  + "_anc_" + ancestor_rn  + "_" + current_date_time.getDateTime() + ".csv" ;
        String cq = "match p=(m:DNA_Match{ancestor_rn:" + ancestor_rn + "})-[rm:match_segment{p_anc_rn:" + ancestor_rn + ",m_anc_rn:" + ancestor_rn + "}]-(s:Segment)-[rs:seg_seq]-(s2:Segment) where rm.cm>=7 and rm.snp_ct>=500 with rs.tgid as tg,m order by rs.tgid with m.fullname + ' [' + m.RN + ']' as match,collect(distinct tg) as tgs return match,size(tgs) as tg_ct,tgs order by match";
        String c = gen.neo4jlib.neo4j_qry.qry_to_csv(cq, SaveFileNm);

         try {
             Desktop.getDesktop().open(new File(gen.neo4jlib.neo4j_info.Import_Dir + SaveFileNm ));
         }
         catch (Exception ee) {}
         
        return "completed";
    }
}
