/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tree;

import gen.neo4jlib.neo4j_qry;
import java.util.Map;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class familyTree_maps {
//    @UserFunction
//    @Description("Visualization in development")
//
//    public Map<String,Object> family_tree(
//        @Name("rn") 
//            Long rn
//  )
//   
//         { 
//             
//        Map<String,Object> m = graph_family_tree(rn);
//         return m;
//            }
//
//    
//    
//    public static void main(String args[]) {
//        // TODO code application logic here
//    }
//    
//     public Map<String,Object> graph_family_tree(Long rn) 
//    {
//        Map<String,Object> m = gen.neo4jlib.neo4j_qry.qry_map("match path=(p:Person{RN:" + rn + "})-[:father|mother*0..99]->(a:Person) return path");
//        return m;
//    }
}
