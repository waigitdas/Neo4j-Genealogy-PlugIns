/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.algo;

import gen.neo4jlib.neo4j_qry;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class triangle_match_names {
    @UserFunction
    @Description("Triangle DNA_Matches in virtual graph created using the submitted parameters.")

    public String triangle_matches(
        @Name("match_method") 
            Long match_method,
       @Name("min_cm") 
            Long min_cm,
        @Name("max_cm") 
            Long max_cm,
        @Name("known_matches_only") 
            Boolean known_matches_only
  )
   
         { 
             
        String s = get_triangles(match_method, min_cm, max_cm, known_matches_only);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_triangles(Long match_method, Long min_cm, Long max_cm,Boolean known_matches_only) 
    {
         String cq ="";
         String cqv ="";
         String algorithm = "";
         String virtual_graph = "";
        
    //////////////////////////////////////////////////////////////////////////////////////////
   ////////////   shared_match////////////////////////////////////////////////////////////////
   //////////////////////////////////////////////////////////////////////////////////////////

   //create virtual graph
        //different query for each algorithm
    if (match_method.equals(1L)){
            algorithm = "shared_match";
            virtual_graph = "shared_matches";
        try {
            gen.neo4jlib.neo4j_qry.qry_write("CALL gds.graph.drop('shared_matches')");
        }
        catch (Exception e){
        }
       if (known_matches_only ) {
             cqv = "CALL gds.graph.project.cypher('shared_matches','MATCH (m:DNA_Match) where m.RN is not null RETURN id(m) AS id', 'MATCH (m)-[[r:shared_match]]->(m2:DNA_Match) where " + max_cm + ">r.cm>" + min_cm + " RETURN id(m) AS source, id(m2) AS target, r.cm as weight',{readConcurrency: 4,validateRelationships:FALSE} ) YIELD graphName AS graph, nodeQuery, nodeCount AS nodes, relationshipQuery, relationshipCount AS rels return nodes, rels";
            }
            else {            
             cqv = "CALL gds.graph.project.cypher('shared_matches','MATCH (m:DNA_Match) RETURN id(m) AS id', 'MATCH (m)-[[r:shared_match]]->(m2:DNA_Match) where " + max_cm + ">r.cm>" + min_cm + " RETURN id(m) AS source, id(m2) AS target, r.cm as weight',{readConcurrency: 4,validateRelationships:FALSE} ) YIELD graphName AS graph, nodeQuery, nodeCount AS nodes, relationshipQuery, relationshipCount AS rels return nodes, rels";
    }
            cq ="CALL gds.alpha.triangles('shared_matches') YIELD nodeA, nodeB, nodeC RETURN gds.util.asNode(nodeA).fullname AS nodeA, gds.util.asNode(nodeB).fullname AS nodeB, gds.util.asNode(nodeC).fullname AS nodeC order by nodeA,nodeB,nodeC";

}
 
      //////////////////////////////////////////////////////////////////////////////////////////
   ////////////   match_by_segment //////////////////////////////////////////////////////////
   //////////////////////////////////////////////////////////////////////////////////////////

   //create virtual graph
        //different query for each algorithm
        if (match_method.equals(2L)){
            algorithm = "match_by_segment";
            virtual_graph = "icw";
                    try {
            gen.neo4jlib.neo4j_qry.qry_write("CALL gds.graph.drop('icw')");
        }
        catch (Exception e){}

                    if (known_matches_only ) {
            cqv = "CALL gds.graph.create.cypher('icw','MATCH (m:DNA_Match) where m.RN is not null RETURN id(m) AS id', 'MATCH (m)-[r:match_by_segment]->(m2:DNA_Match) where " + max_cm + ">r.cm>" + min_cm + " RETURN id(m) AS source, id(m2) AS target, r.cm as weight',{readConcurrency: 4,validateRelationships:FALSE} ) YIELD graphName AS graph, nodeQuery, nodeCount AS nodes, relationshipQuery, relationshipCount AS rels return nodes, rels";
            }
            else {
            
             cqv = "CALL gds.graph.create.cypher('icw','MATCH (m:DNA_Match) RETURN id(m) AS id', 'MATCH (m)-[r:match_by_segment]->(m2:DNA_Match) where " + max_cm + ">r.cm>" + min_cm + " RETURN id(m) AS source, id(m2) AS target, r.cm as weight',{readConcurrency: 4,validateRelationships:FALSE} ) YIELD graphName AS graph, nodeQuery, nodeCount AS nodes, relationshipQuery, relationshipCount AS rels return nodes, rels";

             }
             cq ="CALL gds.alpha.triangles('icw') YIELD nodeA, nodeB, nodeC RETURN gds.util.asNode(nodeA).fullname AS nodeA, gds.util.asNode(nodeB).fullname AS nodeB, gds.util.asNode(nodeC).fullname AS nodeC order by nodeA,nodeB,nodeC";
        }
             
   
 
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
     String node_ct = "";
        String rel_ct = "";
        try{
        String[] c = gen.neo4jlib.neo4j_qry.qry_to_csv(cqv).split("\n");
        String[] sc = c[0].split(",");
        node_ct = sc[0];
        rel_ct = sc[1];
        }
        catch (Exception e) {return "Virtual graph not created.\n\n" + cqv + "\n\nError message:" + e.getMessage();}

        try{
       String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "algo_triangles","triangles", 1, "", "2:#######;", "", false, "UDF: \nreturn gen.algo.triangle_matches(" + match_method + ", " + min_cm + "," + max_cm + "," + known_matches_only + ")\n\nAlgorithm: " + algorithm + " with shared centimorgan range of " + min_cm + " to " +  max_cm + "\nThese analytics use the Neo4j Graph Data Science PlugIn\nThe virtual graph created had " + node_ct + " nodes and  " + rel_ct +" relationships\n\nvirtual graph query:\n" + cqv + "\n\nalgorithm query:\n" + cq + "\n\nThere ia still a virtual graph 'icw' in the database; to remove it, use this command \nCALL gds.graph.drop('icw') ", true);  
       
            cq = "CALL gds.triangleCount.stream('" + virtual_graph + "') YIELD nodeId, triangleCount AS count with nodeId,count where count>0 RETURN count(*) as ct,sum(count) as total ";
            String[] cts = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n")[0].split(",");
            Long ct = Long.parseLong(cts[0]);
            Long total = Long.parseLong(cts[1]);
            
            cq = "CALL gds.triangleCount.stream('" + virtual_graph + "') YIELD nodeId, triangleCount AS count with nodeId,count where count>0 RETURN gds.util.asNode(nodeId).fullname AS name, count order by count desc";
            gen.excelLib.queries_to_excel.qry_to_excel(cq, "kits", "Kit triangle cts", 2, "", "1:###,###;1:##,###", excelFile, true,"cypher query:\n " + cq + "\n\nnumer of kits: " + ct + "; sum of individual kit triangles (column B); " + total +"\nthe mathematically possible number of triangles in 3!(" + ct + "-1)! which is a much larger number than those actually found (worksheet 1).\n\nTo calculated the mathematicaly possible combinations use https://www.dcode.fr/combinations and set k=3 and n=" + ct, true);

            return "completed";
        }catch (Exception e){return "Query failed:\n\n" + cq + "\n\nError message\n" + e.getMessage();}
    }
}
