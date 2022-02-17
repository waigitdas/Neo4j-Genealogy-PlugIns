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


public class parent_dna_from_two_children {
    @UserFunction
    @Description("In development.")

    public String udf_name_seen_in_listing(
        @Name("Sib1_rn") 
            Long sib1_rn,
        @Name("sib2_rn") 
            Long sib2_rn,
        @Name("line_rel_rn")
            Long line_rel_rn
  )
   
         { 
        //https://dnapainter.com/blog/put-your-sibling-tests-to-work-with-the-shared-cm-investigator/?fbclid=IwAR3XKfhkeAr7_b6Oq8EIq8GNFkTHXtss-zszp2CQWtCpOOPANHr4F33T3cU
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
