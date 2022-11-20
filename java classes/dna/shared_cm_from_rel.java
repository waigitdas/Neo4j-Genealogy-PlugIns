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


public class shared_cm_from_rel {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String shared_cm_from_relationship(
        @Name("rel") 
            String rel
  )
   
         { 
             
        String s = getCM(rel);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String getCM(String rel)
    {
        String s = "";
        try{
            s = gen.neo4jlib.neo4j_qry.qry_str("MATCH (f:fam_rel) where f.relationship='" + rel + "'  RETURN f.MeanSharedCM + ' (' + f.LowSharedCM + \" to \" + f.HighSharedCM + ')' as cm");
        }
        catch (Exception e) {s = "unknown";}
        return s;
    }
}
