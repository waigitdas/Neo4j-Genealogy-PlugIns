/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class anc_rn {
    @UserFunction
    @Description("returns ancestor_rn if this enhancement has been set.")

    public Long get_ancestor_rn(
  
  )
   
         { 
             
        Long rn = get_rn();
         return rn;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public Long get_rn() 
    {
        Long rn=Long.valueOf(0L);
        try{
            rn = Long.valueOf(gen.neo4jlib.neo4j_qry.qry_str("MATCH p=()-[r:match_segment]->() where r.p_anc_rn is not null RETURN r.p_anc_rn as anc_rn limit 1").replace("[","").replace("]",""));
        }
        catch (Exception e) {}
        return rn;
    }
}
