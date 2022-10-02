/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class FTDNA_Y_Summary_Import {
    @UserFunction
    @Description("Imports uncolored printer ready FTDNA project Y-summary with names, STRs, ancestor, and location.")

    public String import_y_summary(
        @Name("file_name") 
            String file_name
  )
   
         { 
             
        import_file(file_name);
         return "";
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String import_file(String file) 
    {
        return "";
    }
}
