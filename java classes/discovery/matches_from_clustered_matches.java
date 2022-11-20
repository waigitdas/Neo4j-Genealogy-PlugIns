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


public class matches_from_clustered_matches {
    @UserFunction
    @Description("Uses clusters of in-common-with kits (known persons) to find other matches that are not yet in cuarated files. The discovered matches are much more likely in the line to the common ancestor when matching multiple descendants of that common ancestor, making this list particlarly relevant! The run time may be long (~10 minutes) because MRCAs are computed for each row. You can screen results without MRCAs using the function match_clusters_without_mrca; it runs faster")

    public String match_clusters(
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
        String cq = "match (k:Kit) where k.ancestor_rn is not null with collect(k.RN) as krns MATCH (k1:Kit)-[[r1:KitMatch]]->(f:DNA_Match)<-[[r2:KitMatch]]-(k2:Kit) where k1.RN in krns and k2.RN in krns and " + max_cm + ">=r1.sharedCM>=" + min_cm + " and " + max_cm + ">=r2.sharedCM>=" + min_cm + " and k1<>k2 with f,apoc.coll.dropDuplicateNeighbors (apoc.coll.sort(collect(k1.fullname) + collect(k2.fullname))) as ck,apoc.coll.dropDuplicateNeighbors (apoc.coll.sort(collect(k1.RN) + collect(k2.RN))) as crn with case when f.RN is not null then '*' + f.fullname + ' ⦋' + f.RN + '⦌' else f.fullname end as fullname,collect(distinct f.fullname) as fn2, size(crn) as ct,ck,crn with fullname,fn2,ct,ck,crn where ct>" + min_cluster_size + " -1 with fullname,fn2,ct,ck,crn order by fullname with collect(fullname) as fn,collect(fn2) as fn3,ct,ck,crn with ct,ck,crn,fn,fn3 with ct as Kit_ct,size(fn) as Match_ct,ck as Kits,crn as Kit_RNs,fn as Matches,fn3 with Kit_ct,Kit_ct+Match_ct as Total,Kits,Kit_RNs,Matches,fn3 where Total<50 with Kit_ct, Total,Kits,Kit_RNs,Matches,fn3,gen.rel.mrca_from_cypher_list(Kit_RNs,15) as mrca match (mmm:DNA_Match) where mmm.RN in Kit_RNs with Kit_ct, Total,Kits,mrca,Kit_RNs,Matches,collect(mmm.fullname) as kfn ,fn3 with Kit_ct, Total,Kits,mrca,Kit_RNs,Matches,apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(fn3 + kfn))) as nl with Kit_ct, Total,Kits,mrca,Kit_RNs,Matches,gen.dna_painter.DNA_Painter_Query(nl,mrca) as DNA_Painter_Query,gen.graphxr.cluster_match_query(nl) as graphxr_query with Kit_ct, Total,Kits,mrca,Kit_RNs,Matches,DNA_Painter_Query ,graphxr_query return Kit_ct, Total,Kits,mrca,Kit_RNs,Matches,DNA_Painter_Query,graphxr_query order by Total desc";
                //"match (k:Kit) where k.ancestor_rn is not null with collect(k.RN) as krns MATCH (k1:Kit)-[[r1:KitMatch]]->(f:DNA_Match)<-[[r2:KitMatch]-(k2:Kit) where k1.RN in krns and k2.RN in krns and " + max_cm + ">=r1.sharedCM>=" + min_cm + " and 150>=r2.sharedCM>=7 and k1<>k2 with f,apoc.coll.dropDuplicateNeighbors (apoc.coll.sort(collect(k1.fullname) + collect(k2.fullname))) as ck,apoc.coll.dropDuplicateNeighbors (apoc.coll.sort(collect(k1.RN) + collect(k2.RN))) as crn with case when f.RN is not null then '*' + f.fullname + ' ⦋' + f.RN + '⦌'  else f.fullname end as fullname,collect(distinct f.fullname) as fn2, size(crn) as ct,ck,crn with fullname,fn2,ct,ck,crn where ct>3 -1 with fullname,fn2,ct,ck,crn order by fullname with collect(fullname) as fn,collect(fn2) as fn3,ct,ck,crn  with ct,ck,crn,fn,fn3 with ct as Kit_ct,size(fn) as Match_ct,ck as Kits,crn as Kit_RNs,fn as Matches,fn3 with Kit_ct,Kit_ct+Match_ct as Total,Kits,Kit_RNs,Matches,fn3 where Total<50 with Kit_ct, Total,Kits,Kit_RNs,Matches,fn3,gen.rel.mrca_from_cypher_list(Kit_RNs,15) as mrca return Kit_ct, Total,Kits,mrca,Kit_RNs,Matches  order by Total desc";
                //"match (k:Kit) where k.ancestor_rn is not null with collect(k.RN) as krns MATCH (k1:Kit)-[[r1:KitMatch]]->(f:DNA_Match)<-[[r2:KitMatch]]-(k2:Kit) where k1.RN in krns and k2.RN in krns and " + max_cm + ">=r1.sharedCM>=" + min_cm + " and " + max_cm + ">=r2.sharedCM>=" + min_cm + " and k1<>k2 with f,apoc.coll.dropDuplicateNeighbors (apoc.coll.sort(collect(k1.fullname) + collect(k2.fullname))) as ck,apoc.coll.dropDuplicateNeighbors (apoc.coll.sort(collect(k1.RN) + collect(k2.RN))) as crn with case when f.RN is not null then '*' + f.fullname + ' ⦋' + f.RN + '⦌'  else f.fullname end as fullname,collect(distinct f.fullname) as fn2, size(crn) as ct,ck,crn with fullname,fn2,ct,ck,crn where ct>" + min_cluster_size + " -1 with fullname,fn2,ct,ck,crn order by fullname with collect(fullname) as fn,collect(fn2) as fn3,ct,ck,crn  with ct,ck,crn,fn,fn3 with ct as Kit_ct,size(fn) as Match_ct,ck as Kits,crn as Kit_RNs,fn as Matches,fn3 with Kit_ct,Kit_ct+Match_ct as Total,Kits,Kit_RNs,Matches,fn3 where Total<50 with Kit_ct, Total,Kits,Kit_RNs,Matches,fn3,gen.rel.mrca_from_cypher_list(Kit_RNs,15) as mrca return Kit_ct, Total,Kits,mrca,Kit_RNs,Matches,gen.genlib.DNA_Painter_Query( apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(fn3 + Kits))),mrca) as DNA_Painter_Query  order by Total desc";
                //"match (k:Kit) where k.ancestor_rn is not null with collect(k.RN) as krns MATCH (k1:Kit)-[[r1:KitMatch]]->(f:DNA_Match)<-[[r2:KitMatch]]-(k2:Kit) where k1.RN in krns and k2.RN in krns and " + max_cm + ">=r1.sharedCM>=" + min_cm + " and " + max_cm + ">=r2.sharedCM>=" + min_cm + " and k1<>k2 with f,apoc.coll.dropDuplicateNeighbors (apoc.coll.sort(collect(k1.fullname) + collect(k2.fullname))) as ck,apoc.coll.dropDuplicateNeighbors (apoc.coll.sort(collect(k1.RN) + collect(k2.RN))) as crn with case when f.RN is not null then '*' + f.fullname + ' ⦋' + f.RN + '⦌' else f.fullname end as fullname,size(crn) as ct,ck,crn with fullname,ct,ck,crn where ct>" + min_cluster_size + " -1 with fullname,ct,ck,crn order by fullname with collect(fullname) as fn,ct,ck,crn  with ct,ck,crn,fn with ct as Kit_ct,size(fn) as Match_ct,ck as Kits,crn as Kit_RNs,fn as Matches with Kit_ct,Kit_ct+Match_ct as Total,Kits,Kit_RNs,Matches where Total<50 with Kit_ct, Total,Kits,Kit_RNs,Matches,gen.rel.mrca_from_cypher_list(Kit_RNs," + mrca_generations + ") as mrca return Kit_ct, Total,Kits,mrca,Kit_RNs,Matches  order by Total desc";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "cluster_matches", "matches", 1, "", "0:###;1:###", "", true, "UDF: \nreturn gen.discover.match_clusters(" + min_cluster_size + ", " + min_cm + ", " + max_cm + ")\n which is return gen.discover.match_clusters(min_cluster_size,min_cm, max_cm)\n\nThis function finds clusters of kits (known peope sharing the common ancestor) which are in-common-with matches to each other and then \nfinds those matches who are also in-common-with all in the cluster\nThese discovered matches are good candidates for further research.\nNew matches (column F) are likely to be in the line of the MRCAs identified from the known members of the cluster (column C).\nThe odds of this are increased with larger cluter sizes (column A).\n\nThe full query which will run in Neo4j Browser is \n" + cq + "\n\nThere are columns in the report with queries which drill down into the cluster:\nDNA Painter query: Run the query, save the file and then load to DNA Painter\nGraphXR query: Run the query in GraphXR and visualize as a circle. ",true );
        return "completed";
    }
}
