/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.unknown_parent;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class initial_analysis {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String udf_name_seen_in_listing(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
   
         { 
             
        call_code_here("");
         return "";
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String call_code_here(String cq) 
    {
        return "";
    }
}
