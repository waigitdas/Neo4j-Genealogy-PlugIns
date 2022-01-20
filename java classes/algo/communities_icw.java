/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.algo;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class communities_icw {
    @UserFunction
    @Description("Detects comminities using graph algrorithms: 1=louvain; 2=modularity; 3 = label propagation. Enter range of cm. Specify whether to limit the results to known persons; if this is false and the cm range too large, you may get very long lists that overwhelm the Excel capabilities.")

    public String community_detection_icw(
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
        run_algo(2L,25L,100L,true);
    }
    
     public static String run_algo(Long algorithm, Long min_cm, Long max_cm,Boolean known_matches_only)  //, Boolean intermediateCommunities) 
    {
        String cq ="";
         String cqv ="";
        String algo = "";
        Long algo_nbr = 0L ;
        
        try {
            gen.neo4jlib.neo4j_qry.qry_write("CALL gds.graph.drop('icw')");
        }
        catch (Exception e){}
        
        //create virtual graph
        //different query for each algorithm
             if (known_matches_only ) {
            cqv = "CALL gds.graph.create.cypher('icw','MATCH (m:DNA_Match) where m.RN is not null RETURN id(m) AS id', 'MATCH (m)-[[r:match_by_segment]]->(m2:DNA_Match) where " + max_cm + ">r.cm>" + min_cm + " RETURN id(m) AS source, id(m2) AS target, r.cm as weight',{readConcurrency: 4,validateRelationships:FALSE} ) YIELD graphName AS graph, nodeQuery, nodeCount AS nodes, relationshipQuery, relationshipCount AS rels return nodes, rels";
            }
            else {
            
             cqv = "CALL gds.graph.create.cypher('icw','MATCH (m:DNA_Match) RETURN id(m) AS id', 'MATCH (m)-[[r:match_by_segment]]->(m2:DNA_Match) where " + max_cm + ">r.cm>" + min_cm + " RETURN id(m) AS source, id(m2) AS target, r.cm as weight',{readConcurrency: 4,validateRelationships:FALSE} ) YIELD graphName AS graph, nodeQuery, nodeCount AS nodes, relationshipQuery, relationshipCount AS rels return nodes, rels";
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
            cq = "CALL gds.louvain.stream('icw', {relationshipWeightProperty:'weight' }) YIELD nodeId, communityId, intermediateCommunityIds with case when gds.util.asNode(nodeId).RN is not null then '*' +  gds.util.asNode(nodeId).fullname else gds.util.asNode(nodeId).fullname end AS name, communityId as cid, intermediateCommunityIds as ici with cid,name order by name with cid,collect(name) as names with cid as community,size(names) as ct, names as matches order by ct desc with community,ct,matches where ct>2 return community,ct, matches";
        }

        else if (algorithm.equals(2L)) { //modularity optimization
            algo = "modularity";
            algo_nbr = 2L;
            cq = "CALL gds.beta.modularityOptimization.stream('icw', {relationshipWeightProperty:'weight'}) YIELD nodeId, communityId with case when gds.util.asNode(nodeId).RN is not null then '*' + trim(gds.util.asNode(nodeId).fullname) else  trim(gds.util.asNode(nodeId).fullname) end AS name, communityId with name,communityId order by name with collect(name) as names,communityId with communityId,size(names) as ct,names where ct>1 return communityId,ct,names ORDER BY ct desc,names";
        }
        
         else if (algorithm.equals(3L)) { //modularity optimization
            algo = "Label propagation";
            algo_nbr = 3L;
            cq = "CALL gds.labelPropagation.stream('icw', {relationshipWeightProperty:'weight'}) YIELD nodeId, communityId with case when gds.util.asNode(nodeId).RN is not null then '*' + trim(gds.util.asNode(nodeId).fullname) else  trim(gds.util.asNode(nodeId).fullname) end AS name, communityId with name,communityId order by name with collect(name) as names,communityId with communityId,size(names) as ct,names where ct>1 return communityId,ct,names ORDER BY ct desc,names";
        }
        
        //gen.neo4jlib.neo4j_qry.qry_to_csv(cq, "test.csv");
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "algo_" + algo, "communities", 1, "", "0:#######;1:#####", "", true, "UDF: return gen.algo.community_detection_icw(" + algorithm + "," + min_cm + "," + max_cm + "," + known_matches_only + ")\n\nAlgorithm: " + algo + " with shared centimorgan range of " + min_cm + " to " +  max_cm + "\nThese analytics use the Neo4j Graph Data Science PlugIn\nThe virtual graph created had " + node_ct + " nodes and  " + rel_ct +" relationships\n\nvirtual graph query:\n" + cqv + "\n\nalgorithm query:\n" + cq + "\n\nGraph algorithms use iterations and probabilities. Results may vary between runs. They will also vary with the submitted parameters. Try different settings for the centimorgan ranges to extract different sets of matches. \nThe communityId has no intrinsic meaning; it is simply an identifier assigned by the algorithm.\n\nThere ia still a virtual graph 'icw' in the database; to remove it, restart the database or use this command CALL gds.graph.drop('icw') ", true);
        //System.out.println(algo);
        return algo + " completed";  //\n\n" + cq;
    }
}
