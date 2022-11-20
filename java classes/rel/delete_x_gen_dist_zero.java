/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class delete_x_gen_dist_zero {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String remove_zero_x_gen_dist(
  )
   
         { 
             
        fix_x_gen_dist();
         return "done";
            }

    
    
    public static void main(String args[]) {
        fix_x_gen_dist();
    }
    
     public static String fix_x_gen_dist()
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        //get x segments involved so they can be deleted in 2nd step
        String cq = "MATCH p=(m:DNA_Match)-[r:match_segment]->(s:Segment) where r.x_gen_dist = 0 with r,s with collect(distinct s.Indx) as segs return segs";
        String c = gen.neo4jlib.neo4j_qry.qry_to_csv(cq);
        
        //delete match_seg relationship where x_gen_dist = 0
        gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(m:DNA_Match)-[r:match_segment]->(s:Segment) where r.x_gen_dist = 0 delete r");

        try{
        //delete segments; causes error for segments with other correct x relationship. But it still deletes those who do not.
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (s:Segment) where s.Indx in " + c + " delete s");
        }
        catch(Exception e){}
        return "done";
    }
}
