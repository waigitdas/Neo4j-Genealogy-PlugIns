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


public class degree {
    @UserFunction
    @Description("Degree centrality for each DNA_Match node in virtual graph created using the submitted parameters.")

    public String degree_centrality(
        @Name("rel_type") 
            Long rel_type,
        @Name("min_cm") 
            Long min_cm,
        @Name("max_cm") 
            Long max_cm
  )
   
         { 
             
        String s = get_degree(rel_type,min_cm, max_cm, false);
         return s;
            }

    
    
    public static void main(String args[]) {
        get_degree(1L,7L,3600L,false);
    }
    
     public static String get_degree(Long relationship_type,Long min_cm, Long max_cm,Boolean known_matches_only) 
    {   gen.neo4jlib.neo4j_info.neo4j_var();
        String rel_type="";
        if(relationship_type==1) {rel_type="match_by_segment";}
        else {rel_type="match_segment";}
        
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String cq ="";
         String cqv ="";
  
        try {
            gen.neo4jlib.neo4j_qry.qry_write("CALL gds.graph.drop('" + rel_type + "')");
        }
        catch (Exception e){}
   
          //create virtual graph
  
          if(relationship_type==1L){
             cqv = "CALL gds.graph.project.cypher('" + rel_type + "','MATCH (m:DNA_Match) RETURN id(m) AS id', 'MATCH (m)-[[r:" + rel_type + "]]->(m2:DNA_Match) where " + max_cm + ">r.cm>" + min_cm + " RETURN id(m) AS source, id(m2) AS target, r.cm as weight',{readConcurrency: 4,validateRelationships:FALSE} ) YIELD graphName AS graph, nodeQuery, nodeCount AS nodes, relationshipQuery, relationshipCount AS rels return nodes, rels";
            }
             
      if(relationship_type==2L){
         cqv = "CALL gds.graph.project.cypher('" + rel_type + "','MATCH (m:DNA_Match) RETURN id(m) AS id', 'MATCH (m)-[[r:" + rel_type + "]]->(s:Segment) where r.cm>" + min_cm + " RETURN id(m) AS source, id(s) AS target, r.cm as weight',{readConcurrency: 4,validateRelationships:FALSE} ) YIELD graphName AS graph, nodeQuery, nodeCount AS nodes, relationshipQuery, relationshipCount AS rels return nodes, rels";
        }
             
        String[] c = gen.neo4jlib.neo4j_qry.qry_to_csv(cqv).split("\n");
        String[] sc = c[0].split(",");
        String node_ct = sc[0];
        String rel_ct = sc[1];
        // end create virtual graph
        
      if (relationship_type==1L){
          cq ="CALL gds.degree.stream('" + rel_type + "') YIELD nodeId,score with case when gds.util.asNode(nodeId).RN is not null then '*' + trim(gds.util.asNode(nodeId).fullname) else trim(gds.util.asNode(nodeId).fullname) end AS name, score with name, score where score>1 return name,score as degree order by degree desc";
      String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project +  "_algo_degree_centality","degree", 1, "", "1:#######;2:#####", "", false, "UDF: return gen.algo.degree_centrality(" + min_cm + "," + max_cm + ")\n\nAlgorithm: degree centrality with shared centimorgan range of " + min_cm + " to " +  max_cm + "\nThese analytics use the Neo4j Graph Data Science PlugIn\nThe virtual graph created had " + node_ct + " nodes and  " + rel_ct +" relationships\n\nvirtual graph query:\n" + cqv + "\n\nalgorithm query:\n" + cq + "\n\nThere ia still a virtual graph '" + rel_type + "' in the database; to remove it, restart the database or use this command \nCALL gds.graph.drop('" + rel_type + "') ", true); 
      
 
      cq = "CALL gds.degree.stream('" + rel_type + "') YIELD nodeId,score with score where score>0  return toInteger(score) as degree,count(*) as ct   order by degree";
            gen.excelLib.queries_to_excel.qry_to_excel(cq, "degree_centality_summary","summary", 2, "", "0:#######;1:#####", excelFile, true, "UDF: return gen.algo.degree_centrality(" + relationship_type + ", " + min_cm + "," + max_cm + ")\n\nAlgorithm: degree centrality with shared centimorgan range of " + min_cm + " to " +  max_cm + "\nThese analytics use the Neo4j Graph Data Science PlugIn\nThe virtual graph created had " + node_ct + " nodes and  " + rel_ct +" relationships\n\nvirtual graph query:\n" + cqv + "\n\nalgorithm query:\n" + cq + "\n\nThere ia still a virtual graph '" + rel_type + "' in the database; to remove it, restart the database or use this command \nCALL gds.graph.drop('shaed_matches') ", true);
}
      else {cq="MATCH p=()-[[r:match_segment]]->(s:Segment) where r.cm>=" + min_cm + " and r.snp_ct>= 500  with s.Indx as indx,count(*) as ct with ct,size(collect(indx)) as ct2 return ct,ct2 as nbr_DNA_matches order by ct";
      
      gen.excelLib.queries_to_excel.qry_to_excel(cq, "degree_match_segment","summary", 2, "", "0:###,###;1:##,###,###","", true, "UDF: return gen.algo.degree_centrality(" + relationship_type + ", " + min_cm + "," + max_cm + ")\nThe max_cm is ignore for this report\n\ncypher query:\n" + cq , true);
      }
        return "completed";
    }
}
