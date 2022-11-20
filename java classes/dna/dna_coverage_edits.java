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


public class dna_coverage_edits {
    @UserFunction
    @Description("Adds at-DNA_tester property to Person nodes. Enables inclusion in Coverage reports.")

    public String add_dna_testers(
        @Name("rnlist") 
            String rnlist
  )
   
         { 
             
        update_property(rnlist);
         return "";
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String update_property(String rnlist) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        String cq = "Match (p:Person) remove p.at_DNA_tester";
        gen.neo4jlib.neo4j_qry.qry_write(cq);
        
        cq = "with [" + rnlist + "] as new_RN match (p:Person) where p.RN in new_RN set p.at_DNA_tester='A'";
       gen.neo4jlib.neo4j_qry.qry_write(cq);
        
       //recreate property from GEDCOM; these persons have DNA data in the project
        cq = "MATCH (p:Person)-[r:Gedcom_DNA]->() set p.at_DNA_tester='Y'";
        gen.neo4jlib.neo4j_qry.qry_write(cq);
        
        
         return "DNA testers updated.";
    }
}
