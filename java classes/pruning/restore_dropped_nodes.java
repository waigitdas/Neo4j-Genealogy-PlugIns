/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.pruning;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class restore_dropped_nodes {
    @UserFunction
    @Description("restores DNA_Match nodes dropped because of degree<2.")

    public String restore_dropped_match_nodes(
  )
   
         { 
             
        String s = restore_nodes();
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String restore_nodes() 
    {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String cq = "MATCH (m:DNA_Match_dud) with collect(m) as mc  CALL apoc.refactor.rename.label('DNA_Match_dud','DNA_Match',mc) yield errorMessages as eMessages return eMessages";
        gen.neo4jlib.neo4j_qry.qry_write(cq);
        return "restrored";
    }
}
