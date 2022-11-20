/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.avatar;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class infer_segments_missing {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String udf_name_seen_in_listing(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
   
         { 
             
        compute_infer("");
         return "";
            }

    
    
    public static void main(String args[]) {
        compute_infer("");
    }
    
     public static String compute_infer(String cq) 
    {
        return "";
    }
}
