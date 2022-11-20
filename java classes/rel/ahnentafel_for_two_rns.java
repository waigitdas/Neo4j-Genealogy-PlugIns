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


public class ahnentafel_for_two_rns {
    @UserFunction
    @Description("Calculates the ahnentafel for the ancestor of a specified person, the proband.")

    public String ahnentafel_for_ancestor(
        @Name("proband_rn") 
            Long proband_rn,
        @Name("ancestor_rn") 
            Long ancestor_rn
  )
   
         { 
             
        String ahn = get_ahn(proband_rn,ancestor_rn);
         return ahn;
            }

    
    
    public static void main(String args[]) {
        get_ahn(1796L,1099L);
    }
    
     public static String get_ahn(Long rn1, Long rn2) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        Long rnmin;
        Long rnmax;
        if (rn1<rn2){
            rnmin=rn1;
            rnmax=rn2;
        }
        else {
            rnmin=rn2;
            rnmax=rn1;
    }
                    
       

        String cq = "match path=(p1:Person)-[rp1:father|mother*1..25]->(p2:Person) where p1.RN=" + rnmin + " and p2.RN=" + rnmax  + " and p1.RN<p2.RN with rp1, reduce(srt2 ='', q IN nodes(path)|srt2 + case when q.sex='M' then 0 else 1 end ) as SO with '1' + substring(SO,1,size(rp1)) as SO with gen.rel.ahn_path(SO) as SO with SO[size(SO)-1] as Ahnentafel return Ahnentafel union match path=(p1:Person)<-[rp1:father|mother*1..25]-(p2:Person) where p1.RN=" + rnmin + " and p2.RN=" + rnmax  + " and p1.RN<p2.RN with rp1, reduce(srt2 ='', q IN nodes(path)|srt2 + case when q.sex='M' then 0 else 1 end ) as SO with '1' + substring(SO,1,size(rp1)) as SO with gen.rel.ahn_path(SO) as SO with SO[size(SO)-1] as Ahnentafel return Ahnentafel order by Ahnentafel";
        try{
        String ahn = gen.neo4jlib.neo4j_qry.qry_str(cq).replace("\"","").replace("[","").replace("]","").replace(":","");
        //System.out.println(ahn);
        return ahn;
       
        }
        catch (Exception e) {
           // System.out.println("Error");
            return "not an ancestor or too remote";
                }
       
    }
}
