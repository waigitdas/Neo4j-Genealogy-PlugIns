/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;

import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
import gen.tgs.create_segment_sequence_edges;
import gen.rel.mrca_set_link_property;

public class tg_environment {
    @UserFunction
    @Description("Creates ancestor_rn property in Person, DNA_match and Kit nodes and then the sequence of segments mapping to descendants of the ancestor. This facilitates queries in triangulation group reporting. The seq_seq edge enables rapid traversals to the segments shared by multiple persons without directly computing overlaps.")

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
        
        //set up tracking file
//        FileWriter fwtrack = null;
//        File tracking_rept = new File (gen.neo4jlib.neo4j_info.Import_Dir + "enchancement_tracking.txt");
//           try {
//                fwtrack = new FileWriter(tracking_rept);
//            } catch (Exception ex) {
//                Logger.getLogger(load_ftdna_files.class.getName()).log(Level.SEVERE, null, ex);
//            }
           
        try{
        //add ancestor_rn property to Person, Kit and DNA_Match nodes after erasing prior properties
        mrca_set_link_property s = new mrca_set_link_property();
        s.mrca_link_property(ancestor_rn );
        } 
        catch (Exception e1) {
           // fwtrack.write("ancestor rn  MRCA had error\n" + e1.getMessage() + "\n\n");
            
}
        
        try{
        //create seqment sequences for all segments linked to descendants of the specified ancestor after removing previously created edges
        create_segment_sequence_edges e = new create_segment_sequence_edges();
        e.create_seg_seq_edges(ancestor_rn);
        }catch (Exception e2){
                      
        }
      
        try{
        //create seqment sequences for all segments linked to descendants of the specified ancestor after removing previously created edges
        gen.dna.ancestor_seg_property asp = new gen.dna.ancestor_seg_property();
        asp.overlap_segments();
        }catch (Exception e2){
           
        }
      
        
         
//      try{ 
//        gen.rel.add_rel_property rp = new gen.rel.add_rel_property();
//        rp.add_rel();
//      }
//      catch (Exception ex3) {
//            try {
//                fwtrack.write("rel property creation had error\n" + ex3.getMessage() + "\n\n");
//            } catch (IOException ex) {
//                Logger.getLogger(tg_environment.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//      }
    
      
       try{
           
        gen.mss.create_mss_entities mss = new gen.mss.create_mss_entities();
        mss.create_mss();
           
              
       }
       catch (Exception ex4){
            
                }
      
 
 

      
      
    
//return gen.tgs.matches_tgs()
        
//        //create at-haplotree Excel
//        String  SaveFileNm = "at_haplotree_anc_"  + "_anc_" + ancestor_rn  + "_" + current_date_time.getDateTime() + ".csv" ;
//        String cq = "match p=(m:DNA_Match{ancestor_rn:" + ancestor_rn + "})-[rm:match_segment{p_anc_rn:" + ancestor_rn + ",m_anc_rn:" + ancestor_rn + "}]-(s:Segment)-[rs:seg_seq]-(s2:Segment) where rm.cm>=7 and rm.snp_ct>=500 with rs.tgid as tg,m order by rs.tgid with m.fullname + ' [' + m.RN + ']' as match,collect(distinct tg) as tgs return match,size(tgs) as tg_ct,tgs order by match";
//        String c = gen.neo4jlib.neo4j_qry.qry_to_csv(cq, SaveFileNm);
//
//         try {
//             Desktop.getDesktop().open(new File(gen.neo4jlib.neo4j_info.Import_Dir + SaveFileNm ));
//         }
//         catch (Exception ee) {}
    

//       //prepare summary of data
//        gen.quality.Data_Summary ds = new gen.quality.Data_Summary();
//        ds.understand_your_data();


        return "completed";
    }
}
