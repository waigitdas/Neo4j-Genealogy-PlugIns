/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.pruning;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class degree_drop_nodes {
    @UserFunction
    @Description("Re-labels DNA_Match nodes that are uninformative (e.g., 0 or 1 relationsip to other DNA_matches")

    public String prune_uninformative_matches(
        @Name("match_method") 
            Long match_method,
       @Name("min_cm") 
            Long min_cm,
        @Name("max_cm") 
            Long max_cm
  )
   
         { 
             
        String s = drop_nodes(match_method, min_cm, max_cm);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String drop_nodes(Long match_method,Long min_cm,Long max_cm)
    { 
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String virtual_graph="";
        String cqv ="";
        String cq = "";
        
        if (match_method.equals(1L)){
            virtual_graph = "shared_matches";
        try {
            gen.neo4jlib.neo4j_qry.qry_write("CALL gds.graph.drop('shared_matches')");
        }
        catch (Exception e){
        }
             cqv = "CALL gds.graph.create.cypher('shared_matches','MATCH (m:DNA_Match) RETURN id(m) AS id', 'MATCH (m)-[r:shared_match]-(m2:DNA_Match) where " + max_cm + ">r.cm>" + min_cm + " RETURN id(m) AS source, id(m2) AS target, r.cm as weight',{readConcurrency: 4,validateRelationships:FALSE} ) YIELD graphName AS graph, nodeQuery, nodeCount AS nodes, relationshipQuery, relationshipCount AS rels return nodes, rels";
 
            cq ="CALL gds.degree.stream('shared_matches') YIELD nodeId,score with gds.util.asNode(nodeId) as n,score where score<2 with collect(n) as nc CALL apoc.refactor.rename.label('DNA_Match','DNA_Match_dud',nc) yield errorMessages as eMessages return eMessages";

}
 
      //////////////////////////////////////////////////////////////////////////////////////////
   ////////////   match_by_segment //////////////////////////////////////////////////////////
   //////////////////////////////////////////////////////////////////////////////////////////

   //create virtual graph
        //different query for each algorithm
        if (match_method.equals(2L)){
            virtual_graph = "icw";
                    try {
            gen.neo4jlib.neo4j_qry.qry_write("CALL gds.graph.drop('icw')");
        }
        catch (Exception e){}

             cqv = "CALL gds.graph.create.cypher('icw','MATCH (m:DNA_Match) RETURN id(m) AS id', 'MATCH (m)-[r:match_by_segment]-(m2:DNA_Match) where " + max_cm + ">r.cm>" + min_cm + " RETURN id(m) AS source, id(m2) AS target, r.cm as weight',{readConcurrency: 4,validateRelationships:FALSE} ) YIELD graphName AS graph, nodeQuery, nodeCount AS nodes, relationshipQuery, relationshipCount AS rels return nodes, rels";
             cq ="CALL gds.degree.stream('icw') YIELD nodeId,score with gds.util.asNode(nodeId) as n,score where score<2 with collect(n) as nc CALL apoc.refactor.rename.label('DNA_Match','DNA_Match_dud',nc) yield errorMessages as eMessages return eMessages";
        }
             
        gen.neo4jlib.neo4j_qry.qry_write(cqv);
        gen.neo4jlib.neo4j_qry.qry_write(cq);
   
 
        
        return "dropped" + virtual_graph + "matches";
    }
}
