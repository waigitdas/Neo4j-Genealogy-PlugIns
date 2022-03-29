/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna_painter;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class run_dna_painter_query {
    @UserFunction
    @Description("Renders csv file from query which can be uploadedto DNA Painter.")

    public String dna_painter_query(
        @Name("cq") 
            String cq
  )
   
         { 
             
        run_cq(cq);
         return "";
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String run_cq(String cq) 
    {
        gen.neo4jlib.neo4j_qry.qry_to_csv(cq, gen.neo4jlib.neo4j_info.project + "_dna_painter_cluster.csv");
        return "";
    }
}
