/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.js;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class chr_canvas {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String chr_map(
//        @Name("rn1") 
//            Long rn1,
//        @Name("rn2") 
//            Long rn2
  )
   
         { 
             
        draw_map();
         return "";
            }

    
    
    public static void main(String args[]) {
        draw_map();
    }
    
     public static String draw_map() 
    {
        
        return "";
    }
}
