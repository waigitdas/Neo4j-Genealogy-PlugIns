/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class chr_cm {
    @UserFunction
    @Description("cm in chromosome in this project dataset")

    public Double total_chr_cm(
        @Name("chr") 
            String chr
  )
   
         { 
             
        Double c = get_cm(chr);
         return c;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public Double get_cm(String chr) 
    {
        Double c = Double.parseDouble(gen.neo4jlib.neo4j_qry.qry_to_csv("match (c:chr_cm{chr:'" + chr + "'}) return c.cm").split("\n")[0]);
        return c;
    }
}
