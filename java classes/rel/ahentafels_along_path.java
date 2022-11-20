/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.driver.types.Path;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class ahentafels_along_path {
    @UserFunction
    @Description("creates a list of ahnentafelfs at each hop in a path traversal.")

    public String path_ahnentafels(
        @Name("path") 
            String path
  )
   
         { 
             
        get_ahn(path);
         return "";
            }

    
    
    public static void main(String args[]) {
        
    }
    
     public static String get_ahn(String p) 
    {
        return "";
    }
}
