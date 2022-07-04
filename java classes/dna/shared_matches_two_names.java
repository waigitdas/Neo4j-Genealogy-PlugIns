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


public class shared_matches_two_names {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String shared_matches_two_fullnames(
        @Name("match1") 
            String match1,
        @Name("match2") 
            String match2
  )
   
         { 
             
        String s = get_matches(match1,match2);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_matches(String match1,String match2) 
    {
        String cq = "MATCH (m1:DNA_Match{fullname:'"  + match1 + "'})-[r1:shared_match]-(c:DNA_Match)-[r2:shared_match]-(m2:DNA_Match{fullname:'" +  match2 + "'}) RETURN apoc.coll.sort(collect (distinct case when c.fullname is null then '~' else c.fullname end)) as matches";
        String s = "";
        try{
         s = gen.neo4jlib.neo4j_qry.qry_str(cq);
        }
        catch (Exception e) {s = "error"; }
        return s;
    }
}
