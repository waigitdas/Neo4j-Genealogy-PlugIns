/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.algo;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class leiden_algo {
    @UserFunction
    @Description("Detects comminities using the Leiden graph algrorithms. Enter range of cm. Specify whether to limit the results to known persons; if this is false and the cm range too large, you may get very long lists that overwhelm the Excel capabilities.")

    public String Leiden_community_detection_shared_matches(
        @Name("min_cm") 
            Long min_cm,
        @Name("max_cm") 
            Long max_cm,
        @Name("intermiate_communitiy_number") 
            Long intermiate_communities,
        @Name("known_matches_only") 
            Boolean known_matches_only
  )
         { 
             
        String s = run_algo(min_cm, max_cm,intermiate_communities, known_matches_only);  //, intermediateCommunities);
         return s;
            }

    
    
    public static void main(String args[]) {
        //run_algo(25L,100L,10L,true);
    }
    
     public String run_algo(Long min_cm, Long max_cm,Long intermiate_communities, Boolean known_matches_only)  //, Boolean intermediateCommunities) 
    {
        String cq ="";
         String cqv ="";
        String algo = "";
        Long algo_nbr = 0L ;
        String extra="";
        try {
            gen.neo4jlib.neo4j_qry.qry_write("CALL gds.graph.drop('shared_matches')");
        }
        catch (Exception e){}
        
        //create virtual graph
        //different query for each algorithm
             if (known_matches_only ) {
            cqv = "CALL gds.graph.project.cypher('shared_matches','MATCH (m:DNA_Match) where m.RN is not null RETURN id(m) AS id', 'MATCH (m)-[r:shared_match]->(m2:DNA_Match) where " + max_cm + ">r.cm>" + min_cm + " RETURN id(m) AS source, id(m2) AS target, r.cm as weight',{readConcurrency: 4,validateRelationships:FALSE} ) YIELD graphName AS graph, nodeQuery, nodeCount AS nodes, relationshipQuery, relationshipCount AS rels return nodes, rels";
             }
            else {
            
             cqv = "CALL gds.graph.project.cypher('shared_matches','MATCH (m:DNA_Match) RETURN id(m) AS id', 'MATCH (m)-[[r:shared_match]]->(m2:DNA_Match) where " + max_cm + ">r.cm>" + min_cm + " RETURN id(m) AS source, id(m2) AS target, r.cm as weight',{readConcurrency: 4,validateRelationships:FALSE} ) YIELD graphName AS graph, nodeQuery, nodeCount AS nodes, relationshipQuery, relationshipCount AS rels return nodes, rels";
            }
             
        String[] c = gen.neo4jlib.neo4j_qry.qry_to_csv(cqv).split("\n");
        String[] sc = c[0].split(",");
        String node_ct = sc[0];
        String rel_ct = sc[1];
        //String fn = gen.excelLib.queries_to_excel.qry_to_excel(cq, "algo", algo, 0, "", "", "", true, "Algorithm: " + algo + "\n\nquery:\n" + cq, true);
        // end create virtual graph
        
     
            algo = "Leiden";
            algo_nbr = 1L;
            extra ="";
            
            cq = "CALL gds.alpha.leiden.stream('shared_matches', { relationshipWeightProperty: 'weight', randomSeed: 19,includeIntermediateCommunities:true, maxLevels:6}) YIELD nodeId, communityId, intermediateCommunityIds with nodeId,case when gds.util.asNode(nodeId).RN is not null then '*' + gds.util.asNode(nodeId).fullname else gds.util.asNode(nodeId).fullname end AS name, communityId as cid, intermediateCommunityIds as ici with nodeId,cid,ici,name order by name with cid,ici,collect(name) as names,collect(gds.util.asNode(nodeId).RN) as rns with rns,cid as community,ici,size(names) as ct, names as matches order by ct desc with rns,community,ici,ct,matches where ct>1 with community,ici as intermediary_communities,ct, matches,gen.rel.mrca_from_cypher_list(rns,15) as mrcas, split(gen.mss.mss_data(rns),'|') as rns return community,intermediary_communities,ct, matches,mrcas,rns[[1]] as community_seg_ct, rns[[0]] as mss_anc_desc,rns[[2]] as mss_in_comm, rns[[8]] as mss_seg_in_comm, rns[[3]] as included_mss_total_seg_ct, rns[[9]] as mss_seg_in_community_segs";
      

        
        try{
          gen.excelLib.queries_to_excel.qry_to_excel(cq, "shared_matches_algo_" + algo, "communities", 1, "", "0:#######;1:#####;2:#####;5:####;6:####;7:#####;8:####;9:####", "", true, "UDF: \nreturn gen.algo.community_detection_shared_matches(" + min_cm + "," + max_cm + "," + intermiate_communities + "," + known_matches_only + ")\n\nAlgorithm: " + algo + " with shared centimorgan range of " + min_cm + " to " +  max_cm + "\nThese analytics use the Neo4j Graph Data Science PlugIn\nThe virtual graph created had " + node_ct + " nodes and  " + rel_ct +" relationships\n\nvirtual graph query:\n" + cqv + "\n\nalgorithm query:\n" + cq + "\n\nTo see the virtual graph information use this query:\ncall gds.graph.streamRelationshipProperty('shared_matches','weight') yield sourceNodeId,targetNodeId, propertyValue as weight with gds.util.asNode(sourceNodeId) as s, gds.util.asNode(targetNodeId) as t, weight as w return s.fullname as source,t.fullname as target,w as weight order by weight desc\n\nGraph algorithms use iterations and probabilities. Results may vary between runs. They will also vary with the submitted parameters. Try different settings for the centimorgan ranges to extract different sets of matches. \nThe communityId has no intrinsic meaning; it is simply an identifier assigned by the algorithm.\n\nThere is still a virtual graph 'shared_matches' in the database; to remove it, use this command CALL gds.graph.drop('shared_matches') " + extra, true);
        
        return algo + " completed";  
        }
        catch (Exception e) {return algo + " error: " + cq;}
     }
}
