/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class gen_distance_from_rns {
    @UserFunction
    @Description("Returns relationship for two RNs using path hops to the common ancestor(s). If there are common ancestors on two branches there may be moe than one relationship returned. The algorithm looks back 10 generations for common ancestors.")

    public Long gen_distance_from_RNs(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
    {
         { 
        gen.neo4jlib.neo4j_info.neo4j_var();
        Long r = get_gd(rn1,rn2);
        return r;
            }
    }
    
    
    public static void main(String args[]) {
        //Long r = get_gd(Long.valueOf(1),Long.valueOf(600));
        
    }
    
     public static Long get_gd(Long rn1, Long rn2) 
    {
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
            
         try{
            String cq = "match path=(p1:Person)-[rp1:father|mother*0..15]->(mrca:Person)<-[rp2:father|mother*0..15]-(p2:Person) where p1.RN=" + rnmin + " and p2.RN=" + rnmax + " and p1.RN<p2.RN with distinct size(rp1) + size(rp2) as gen_dist return gen_dist order by gen_dist";
         String r = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n")[0]; 
         return Long.parseLong(r);
         }
         catch (Exception e) {return 0L;}
    }
}
