/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class is_dir_ancestor {
    @UserFunction
    @Description("Template used in creating new functions.")

    public Boolean is_direct_ancestor(
        @Name("rn") 
            Long rn,
        @Name("anc_rn") 
            Long anc_rn
  )
   
         { 
             
        Boolean b = is_anc(rn, anc_rn);
         return b;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public Boolean is_anc (Long rn, Long anc_rn) 
    {  Boolean b = false;
       String cq = "match (p:Person{RN:" + rn + "})-[r:father|mother*0..20]->(a:Person{RN:" + anc_rn + "}) return a.fullname as name";
       String r = "";
       try{
           r = gen.neo4jlib.neo4j_qry.qry_str(cq);
           b = true;
       }
       catch (Exception e){}
        
       return b;
    }
}
