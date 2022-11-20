/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.discovery;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class matches_from_clustered_matches_without_mrca {
    @UserFunction
    @Description("Uses clusters of in-common-with kits (known persons) to find other matches that are not yet in cuarated files. Use this to sreen the parameter settings and then run matches_from_clustered_matches to get the mrcas for the cluster matches.")

    public String match_clusters_without_mrca(
        @Name("min_cluster_size") 
            Long min_cluster_size,
         @Name("min_cm") 
            Long min_cm,
        @Name("max_cm") 
            Long max_cm
  )
   
         { 
        
        get_matches(min_cluster_size,min_cm, max_cm);
         return "";
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_matches(Long min_cluster_size,Long min_cm, Long max_cm) 
    {
        String cq = "match (k:Kit) where k.ancestor_rn is not null with collect(k.RN) as krns MATCH (k1:Kit)-[[r1:KitMatch]]->(f:DNA_Match)<-[[r2:KitMatch]]-(k2:Kit) where k1.RN in krns and k2.RN in krns and " + max_cm + ">=r1.sharedCM>=" + min_cm + " and " + max_cm + ">=r2.sharedCM>=" + min_cm + " and k1<>k2 with f,apoc.coll.dropDuplicateNeighbors (apoc.coll.sort(collect(k1.fullname) + collect(k2.fullname))) as ck,apoc.coll.dropDuplicateNeighbors (apoc.coll.sort(collect(k1.RN) + collect(k2.RN))) as crn with f.fullname as fullname,size(crn) as ct,ck,crn with fullname,ct,ck,crn where ct>" + min_cluster_size + " -1 with fullname,ct,ck,crn order by fullname with collect(fullname) as fn,ct,ck,crn  with ct,ck,crn,fn with ct as Kit_ct,size(fn) as Match_ct,ck as Kits,crn as Kit_RNs,fn as Matches with Kit_ct,Kit_ct+Match_ct as Total,Kits,Kit_RNs,Matches where Total<50 with Kit_ct, Total,Kits,Kit_RNs,Matches return Kit_ct, Total,Kits,Kit_RNs,Matches  order by Total desc";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "cluster_matches", "matches", 1, "", "0:###;1:###", "", true, "UDF: return gen.dicivery.match_clusters(" + min_cluster_size + ", " + min_cm + ", " + max_cm + ")\n which is return gen.discovery.match_clusters(min_cluster_size,min_cm, max_cm)\n\nThis function finds clusters of kits (known peope) which are in-common-with matches and then \nfinds those matches who are also in-common-with all in the cluster\nThese discovered matches are good candidates for further research\n\nThe full query which will run in Neo4j Browser is \n" + cq ,true);
        return "completed";
    }
}
