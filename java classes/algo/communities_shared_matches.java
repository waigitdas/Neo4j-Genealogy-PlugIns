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


public class communities_shared_matches {
    @UserFunction
    @Description("Detects comminities using graph algrorithms: 1=louvain; 2=modularity; 3 = label propagation. Enter range of cm. Specify whether to limit the results to known persons; if this is false and the cm range too large, you may get very long lists that overwhelm the Excel capabilities.")

    public String community_detection_shared_matches(
        @Name("algoritm") 
            Long algorithm,
        @Name("min_cm") 
            Long min_cm,
        @Name("max_cm") 
            Long max_cm,
        @Name("known_matches_only") 
            Boolean known_matches_only
//        @Name("intermediateCommunities") 
//            Boolean intermediateCommunities

  )
         { 
             
        String s = run_algo(algorithm, min_cm, max_cm, known_matches_only);  //, intermediateCommunities);
         return s;
            }

    
    
    public static void main(String args[]) {
        run_algo(2L,7L,250L,true);
    }
    
     public static String run_algo(Long algorithm, Long min_cm, Long max_cm,Boolean known_matches_only)  //, Boolean intermediateCommunities) 
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
        
        if (algorithm.equals(1L)) {  //louvain
            algo = "louvain";
            algo_nbr = 1L;
            extra ="\n\nYou may want to try running Louvain without intermediarey communities. If so, use this query:\nCALL gds.louvain.stream('shared_matches', {relationshipWeightProperty:'weight', tolerance:0.0000000001,maxIterations:10,maxLevels:10 }) YIELD nodeId, communityId with case when gds.util.asNode(nodeId).RN is not null then '*' + gds.util.asNode(nodeId).fullname else gds.util.asNode(nodeId).fullname end AS name, communityId as cid with cid,name order by name with cid,collect(name) as names with cid as community,size(names) as ct, names as matches order by ct desc with community,ct,matches where ct>2 return community,ct, matches";
            
            cq = "CALL gds.louvain.stream('shared_matches', {relationshipWeightProperty:'weight',includeIntermediateCommunities:true, tolerance:0.0000001,maxIterations:10,maxLevels:10 }) YIELD nodeId, communityId, intermediateCommunityIds with nodeId,case when gds.util.asNode(nodeId).RN is not null then '*' + gds.util.asNode(nodeId).fullname else gds.util.asNode(nodeId).fullname end AS name, communityId as cid, intermediateCommunityIds as ici with nodeId,cid,ici,name order by name with cid,ici,collect(name) as names,collect(gds.util.asNode(nodeId).RN) as rns with rns,cid as community,ici,size(names) as ct, names as matches order by ct desc with rns,community,ici,ct,matches where ct>1 with community,ici as intermediary_communities,ct, matches,gen.rel.mrca_from_cypher_list(rns,15) as mrcas, split(gen.mss.mss_data(rns),'|') as rns return community,intermediary_communities,ct, matches,mrcas,rns[[1]] as community_seg_ct, rns[[0]] as mss_anc_desc,rns[[2]] as mss_in_comm, rns[[8]] as mss_seg_in_comm, rns[[3]] as included_mss_total_seg_ct, rns[[9]] as mss_seg_in_community_segs";
                    //"CALL gds.louvain.stream('shared_matches', {relationshipWeightProperty:'weight',includeIntermediateCommunities:true, tolerance:0.0000001,maxIterations:10,maxLevels:10 }) YIELD nodeId, communityId, intermediateCommunityIds with nodeId,case when gds.util.asNode(nodeId).RN is not null then '*' + gds.util.asNode(nodeId).fullname else gds.util.asNode(nodeId).fullname end AS name, communityId as cid, intermediateCommunityIds as ici with nodeId,cid,ici,name order by name with cid,ici,collect(name) as names,collect(gds.util.asNode(nodeId).RN) as rns with rns,cid as community,ici,size(names) as ct, names as matches order by ct desc with rns,community,ici,ct,matches where ct>1 return community,ici  as intermediary_communities,ct, matches,gen.rel.mrca_from_cypher_list(rns,15) as mrcas";
        }

        else if (algorithm.equals(2L)) { //modularity optimization
            algo = "modularity";
            algo_nbr = 2L;
            cq ="CALL gds.beta.modularityOptimization.stream('shared_matches', {relationshipWeightProperty:'weight',tolerance:0.0000001}) YIELD nodeId, communityId with nodeId,case when gds.util.asNode(nodeId).RN is not null then '*' + gds.util.asNode(nodeId).fullname else gds.util.asNode(nodeId).fullname end AS name, communityId as cid with nodeId,cid,name order by name with cid,collect(name) as names,collect(gds.util.asNode(nodeId).RN) as rns with rns,cid as community,size(names) as ct, names as matches order by ct desc with rns,community,ct,matches where ct>2 with community,ct, matches,gen.rel.mrca_from_cypher_list(rns,15) as mrcas, split(gen.mss.mss_data(rns),'|') as rns return community,ct,matches,size(mrcas)-size(replace(mrcas,';','')) as mrca_ct,mrcas,rns[[1]] as community_seg_ct, rns[[0]] as mss_anc_desc,rns[[2]] as mss_in_comm, rns[[8]] as mss_seg_in_comm, rns[[3]] as included_mss_total_seg_ct, rns[[9]] as mss_seg_in_community_segs";
                    //"CALL gds.beta.modularityOptimization.stream('shared_matches', {relationshipWeightProperty:'weight',tolerance:0.0000001}) YIELD nodeId, communityId with nodeId,case when gds.util.asNode(nodeId).RN is not null then '*' + gds.util.asNode(nodeId).fullname else gds.util.asNode(nodeId).fullname end AS name, communityId as cid with nodeId,cid,name order by name with cid,collect(name) as names,collect(gds.util.asNode(nodeId).RN) as rns with rns,cid as community,size(names) as ct, names as matches order by ct desc with rns,community,ct,matches where ct>2 return community,ct, matches,gen.rel.mrca_from_cypher_list(rns,15) as mrcas";
        }
        
         else if (algorithm.equals(3L)) { //modularity optimization
            algo = "Label propagation";
            algo_nbr = 3L;
            cq = "CALL gds.labelPropagation.stream('shared_matches', {relationshipWeightProperty:'weight'}) YIELD nodeId, communityId with nodeId,case when gds.util.asNode(nodeId).RN is not null then '*' + gds.util.asNode(nodeId).fullname else gds.util.asNode(nodeId).fullname end AS name, communityId as cid with nodeId,cid,name order by name with cid,collect(name) as names,collect(gds.util.asNode(nodeId).RN) as rns with rns,cid as community,size(names) as ct, names as matches order by ct desc with rns,community,ct,matches where ct>1 with community,ct, matches,gen.rel.mrca_from_cypher_list(rns,15) as mrcas, split(gen.mss.mss_data(rns),'|') as rns return community,ct, matches, mrcas,rns[[1]] as community_seg_ct, rns[[0]] as mss_anc_desc,rns[[2]] as mss_in_comm, rns[[8]] as mss_seg_in_comm, rns[[3]] as included_mss_total_seg_ct, rns[[9]] as mss_seg_in_community_segs";
                    //"CALL gds.labelPropagation.stream('shared_matches', {relationshipWeightProperty:'weight'}) YIELD nodeId, communityId with nodeId,case when gds.util.asNode(nodeId).RN is not null then '*' + gds.util.asNode(nodeId).fullname else gds.util.asNode(nodeId).fullname end AS name, communityId as cid with nodeId,cid,name order by name with cid,collect(name) as names,collect(gds.util.asNode(nodeId).RN) as rns with rns,cid as community,size(names) as ct, names as matches order by ct desc with rns,community,ct,matches where ct>1 return community,ct, matches,gen.rel.mrca_from_cypher_list(rns,15) as mrcas";
        }
        try{
          gen.excelLib.queries_to_excel.qry_to_excel(cq, "shared_matches_algo_" + algo, "communities", 1, "", "0:#######;1:#####;2:#####;5:####;6:####;7:#####;8:####;9:####", "", true, "UDF: \nreturn gen.algo.community_detection_shared_matches(" + algorithm + "," + min_cm + "," + max_cm + "," + known_matches_only + ")\n\nAlgorithm: " + algo + " with shared centimorgan range of " + min_cm + " to " +  max_cm + "\nThese analytics use the Neo4j Graph Data Science PlugIn\nThe virtual graph created had " + node_ct + " nodes and  " + rel_ct +" relationships\n\nvirtual graph query:\n" + cqv + "\n\nalgorithm query:\n" + cq + "\n\nTo see the virtual graph information use this query:\ncall gds.graph.streamRelationshipProperty('shared_matches','weight') yield sourceNodeId,targetNodeId, propertyValue as weight with gds.util.asNode(sourceNodeId) as s, gds.util.asNode(targetNodeId) as t, weight as w return s.fullname as source,t.fullname as target,w as weight order by weight desc\n\nGraph algorithms use iterations and probabilities. Results may vary between runs. They will also vary with the submitted parameters. Try different settings for the centimorgan ranges to extract different sets of matches. \nThe communityId has no intrinsic meaning; it is simply an identifier assigned by the algorithm.\n\nThere is still a virtual graph 'shared_matches' in the database; to remove it, use this command \nCALL gds.graph.drop('shared_matches') " + extra, true);
        
        return algo + " completed";  
        }
        catch (Exception e) {return algo + " error: " + cq;}
     }
}
