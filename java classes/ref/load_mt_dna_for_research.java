/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.ref;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class load_mt_dna_for_research {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String load_multiplr_mt_dna_ref(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
   
         { 
             
        load_res_mt();
         return "";
            }

    
    
    public static void main(String args[]) {
        load_res_mt();
    }
    
     public static String load_res_mt() 
    {
        
        return "";
    }
}
