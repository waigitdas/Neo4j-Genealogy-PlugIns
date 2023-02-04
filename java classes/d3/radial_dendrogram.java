/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.d3;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class radial_dendrogram {
    @UserFunction
    @Description("in development.")

    public String dendrogram(
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
