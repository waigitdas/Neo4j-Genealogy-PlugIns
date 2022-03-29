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


public class degree {
    @UserFunction
    @Description("Degree centrality for each DNA_Match node in virtual graph created using the submitted parameters.")

    public String degree_centrality(
        @Name("min_cm") 
            Long min_cm,
        @Name("max_cm") 
            Long max_cm,
        @Name("known_matches_only") 
            Boolean known_matches_only
  )
   
         { 
             
        String s = get_degree("matches_degree_centrality", min_cm, max_cm, known_matches_only);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_degree(String algorithm, Long min_cm, Long max_cm,Boolean known_matches_only) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String cq ="";
         String cqv ="";
  
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
            
             cqv = "CALL gds.graph.create.cypher('icw','MATCH (m:DNA_Match) RETURN id(m) AS id', 'MATCH (m)-[r:match_by_segment]->(m2:DNA_Match) where " + max_cm + ">r.cm>" + min_cm + " RETURN id(m) AS source, id(m2) AS target, r.cm as weight',{readConcurrency: 4,validateRelationships:FALSE} ) YIELD graphName AS graph, nodeQuery, nodeCount AS nodes, relationshipQuery, relationshipCount AS rels return nodes, rels";
            }
             
        String[] c = gen.neo4jlib.neo4j_qry.qry_to_csv(cqv).split("\n");
        String[] sc = c[0].split(",");
        String node_ct = sc[0];
        String rel_ct = sc[1];
        //String fn = gen.excelLib.queries_to_excel.qry_to_excel(cq, "algo", algo, 0, "", "", "", true, "Algorithm: " + algo + "\n\nquery:\n" + cq, true);
        // end create virtual graph
        
        cq ="CALL gds.degree.stream('icw') YIELD nodeId,score with case when gds.util.asNode(nodeId).RN is not null then '*' + trim(gds.util.asNode(nodeId).fullname) else trim(gds.util.asNode(nodeId).fullname) end AS name, score return name, score order by score desc";
      gen.excelLib.queries_to_excel.qry_to_excel(cq, "algo_","degree", 1, "", "2:#.######;", "", true, "UDF: return gen.algo.degree_centrality(" + min_cm + "," + max_cm + "," + known_matches_only + ")\n\nAlgorithm: " + algorithm + " with shared centimorgan range of " + min_cm + " to " +  max_cm + "\nThese analytics use the Neo4j Graph Data Science PlugIn\nThe virtual graph created had " + node_ct + " nodes and  " + rel_ct +" relationships\n\nvirtual graph query:\n" + cqv + "\n\nalgorithm query:\n" + cq + "\n\nThere ia still a virtual graph 'icw' in the database; to remove it, restart the database or use this command CALL gds.graph.drop('icw') ", true);  
        return "completed";
    }
}
