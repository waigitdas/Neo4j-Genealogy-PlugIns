/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class rel_from_rns {
    @UserFunction
    @Description("Returns relationship for two RNs using path hops to the common ancestor(s). If there are common ancestors on two branches there may be moe than one relationship returned. The algorithm looks back 10 generations for common ancestors.")

    public String relationship_from_RNs(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
    {
         { 
        gen.neo4jlib.neo4j_info.neo4j_var();
        String r = get_rel(rn1,rn2);
        return r;
            }
    }
    
    
    public static void main(String args[]) {
        String r = get_rel(Long.valueOf(1),Long.valueOf(600));
        
    }
    
     public static String get_rel(Long rn1, Long rn2) 
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
            
         String cq = "match path=(p1:Person)-[rp1:father|mother*0..15]->(mrca:Person)<-[rp2:father|mother*0..15]-(p2:Person) where p1.RN=" + rnmin + " and p2.RN=" + rnmax + " and p1.RN<p2.RN with count(mrca) as mc,size(rp1) as l1,size(rp2) as l2 with mc, case when l1<l2 then l1 else l2 end as path1, case when l1<l2 then l2 else l1 end as path2 with  path1 + ':' + path2 + ':' + mc as Indx  with collect(Indx) as Indx with Indx MATCH (n:fam_rel) where n.Indx in Indx return collect(n.relationship) as rel";
         String r = gen.neo4jlib.neo4j_qry.qry_str(cq).replace("[[", "").replace("]]", ",").replace("\"", "").replace(",",";"); 
         r = r.substring(0,r.length()-1 );
         return r;
    }
}
