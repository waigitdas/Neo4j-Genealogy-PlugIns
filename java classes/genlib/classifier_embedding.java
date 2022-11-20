/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.genlib;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class classifier_embedding {
    @UserFunction
    @Description("for future use in machine learning. Classifier for embeddings")
    //https://stackoverflow.com/questions/71860028/frequency-embedding-in-neo4j/71883712#71883712 
    
    public String embed_categories(
        @Name("classifier_node") 
            String classifier_node,
        @Name("node_to_classify") 
            String node_to_classify,
        @Name("edge_to_category") 
            String edge_to_category,
        @Name("node_with_term") 
            String node_with_term
  )
   
         { 
             
        create_embedding(classifier_node, node_to_classify, edge_to_category, node_with_term);
         return "";
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String create_embedding(String classifier_node, String node_to_classify, String edge_to_category, String node_with_term) 
    {
        String cq = "match (c:" + classifier_node + ") with c order by c.order with collect(c.name) as cn MATCH(n:" + node_to_classify + ")-[r:" + edge_to_category + "]->(b:" + node_with_term + " WITH apoc.coll.indexOf(cn, t.state) as inx, n, [0,0,0] as k WITH apoc.coll.set(k, inx, 1) AS k, n WITH collect(k) as kk, n WITH REDUCE(s = [], sublist IN kk | CASE WHEN SIZE(s) = 0 THEN sublist ELSE [i IN RANGE(0, SIZE(s)-1) | s[i] + sublist[i]] END) AS result, n SET n.embedding = result RETURN n.key, n.embedding";
        return "";
    }
}
