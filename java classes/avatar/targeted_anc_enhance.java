/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.avatar;

import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
import gen.tgs.create_segment_sequence_edges;
import gen.rel.mrca_set_link_property;
import gen.tgs.create_segment_sequence_edges;

public class targeted_anc_enhance {
    @UserFunction
    @Description("Creates ancestor_rn property in Person, DNA_match and Kit nodes and then the sequence of segments mapping to descendants of the ancestor. This facilitates queries in triangulation group reporting. The seq_seq edge enables rapid traversals to the segments shared by multiple persons without directly computing overlaps.")

    public  String add_enhancements(
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
        
           
        try{
        //add ancestor_rn property to Person, Kit and DNA_Match nodes after erasing prior properties
        mrca_set_link_property s = new mrca_set_link_property();
        s.mrca_link_property(ancestor_rn );
        } 
        catch (Exception e1) {
            
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
      
      
       try{
           
        gen.mss.create_mss_entities mss = new gen.mss.create_mss_entities();
        mss.create_mss();
           
              
       }
       catch (Exception ex4){
            
                }

        return "completed";
    }
}
