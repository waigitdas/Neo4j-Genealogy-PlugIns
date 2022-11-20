/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.graphxr;

import gen.neo4jlib.neo4j_qry;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class cluster_match {
    @UserFunction
    @Description("creates a query for GraphXR")

    public String cluster_match_query(
        @Name("name_list") 
            List name_list
  )
   
         { 
             
        String q = create_query(name_list);
         return q;
            }

    
    
    public static void main(String args[]) {
       // create_query("["Charles Edwin Stinnett\", \"David Allen Stumpf\", \"Judith Ann Davis\", \"Kaitrin Elizabeth Stumpf\", \"Molly Stinnett\", \"Peter Aubrey Davis\"]");
    }
    
     public String create_query(List<String> names) 
    {
        String s = gen.genlib.handy_Functions.cypher_list_to_quoted_list(names);
        String q = "$%^" ;// "\"";
        String cmq ="with [[" + s + "]]  as nl MATCH p=(m1:DNA_Match)-[[r:match_by_segment]]-(m2:DNA_Match) where m1.fullname in nl and m2.fullname in nl with p,m1,m2,nl match ps=(m3:DNA_Match)-[[rs1:match_segment]]->(s:Segment)<-[[rs2:match_segment]]-(m4:DNA_Match) where m3.fullname<m4.fullname and m3.fullname in nl and m4.fullname in nl and rs1.cm>=7 and rs1.snp_ct>=500 and rs2.cm>=7 and rs2.snp_ct>=500 return p,ps";
        return cmq;
    }
}
