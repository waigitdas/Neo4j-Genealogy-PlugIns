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
        @Name("cypher_query") 
            String cypher_query,
        @Name("d3_template") 
            String d3_template
  )
   
         { 
             
        create_dendrogram(cypher_query, d3_template);
         return "";
            }

    
    
    public static void main(String args[]) {
        create_dendrogram("", "");
    }
    
     public static String create_dendrogram(String cq, String d3t) 
    {
        return "";
    }
}
