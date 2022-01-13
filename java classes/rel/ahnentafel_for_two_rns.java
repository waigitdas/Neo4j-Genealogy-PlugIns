/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class ahnentafel_for_two_rns {
    @UserFunction
    @Description("Calculates the ahnentafel for the ancestor of a specified person, the propositus.")

    public String ahnentafel_for_ancestor(
        @Name("propositus_rn") 
            Long propositus_rn,
        @Name("ancestor_rn") 
            Long ancestor_rn
  )
   
         { 
             
        String ahn = get_ahn(propositus_rn,ancestor_rn);
         return ahn;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_ahn(Long rn1, Long rn2) 
    {
        String cq = "match path=(p1:Person)-[rp1:father|mother*1..15]->(p2:Person) where p1.RN=" + rn1 + " and p2.RN=" + rn2  + " with rp1, reduce(srt2 ='', q IN nodes(path)|srt2 + case when q.sex='M' then 0 else 1 end ) as SO with '1' + substring(SO,1,size(rp1)) as SO with gen.rel.ahn_path(SO) as SO with SO[size(SO)-1] as Ahnentafel return Ahnentafel";
        try{
        String ahn = gen.neo4jlib.neo4j_qry.qry_str(cq).replace("\"","").replace("[","").replace("]","").replace(":","");
        return ahn;}
        catch (Exception e) {
            return "not an ancestor";
                }
       
    }
}
