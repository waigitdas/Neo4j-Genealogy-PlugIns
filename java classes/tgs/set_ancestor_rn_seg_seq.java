/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
import gen.neo4jlib.neo4j_info;
import gen.neo4jlib.neo4j_qry;
import gen.tgs.create_segment_sequence_edges;
import gen.rel.mrca_set_link_property;

public class set_ancestor_rn_seg_seq {
    @UserFunction
    @Description("Creates ancestor_rn property in person, DNA_match and kit nodes and then the sequence of segments mapping to descendants.. This facilitates queries in triangulation group reporting. The seq-seq edge enables rapid queries without directly computing overlaps.")

    public static String setup_tg_environment(
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
    
     public static String setup(Long ancestor_rn) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();  //initialize variables
        gen.rel.mrca_set_link_property.mrca_link_property(ancestor_rn );
        gen.tgs.create_segment_sequence_edges.create_seg_seq_edges(ancestor_rn);
        
        return "completed";
    }
}
