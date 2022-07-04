/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class Y_dual_matches {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String Y_DNA_dual_matches(
        @Name("Y_haplogroup") 
            String Y_haplogroup
  )
   
         { 
             
        get_matches(Y_haplogroup);
         return "";
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_matches(String Y_haplogroup) 
    {
        String cq ="with '" + Y_haplogroup + "' as hg MATCH p=(m1:DNA_Match{YHG:hg})-[r:match_by_segment]->(m2:DNA_Match{YHG:hg})  RETURN m1.fullname as match1,m2.fullname as match2,m1.YHG as Y_haplogroup";
         String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project +  "_Y_dual_matches", Y_haplogroup, 1, "","1:####;3:####;4:#####", "", false,"udf\nrreturn gen.dna.Y_dual_matches(" + Y_haplogroup + ")\n\ncyspher query\n" + cq + "\n\nDual matches to at- and Y-DNA are rare. Nothing may be returned. If there are results, these are excellent clues for further traditional genealogy research.", true);
        
         cq = "MATCH p=(m1:DNA_Match{YHG:'" + Y_haplogroup + "'})-[r:match_by_segment]->(m2:DNA_Match) where m2.RN is not null RETURN m1.fullname as match1,m2.fullname as match2, m1.YHG as YHG1,case when m2.YHG is null then '~' else m2.YHG end as YHG2,r.cm as shared_autosomal_cm, gen.dna.shared_matches_two_fullnames(m1.fullname,m2.fullname) as shared_autosomal_matches ";
         gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project +  "_Y_dual_matches", Y_haplogroup, 1, "","1:####;3:####;4:####.###", excelFile, true,"udf\nrreturn gen.dna.Y_dual_matches(" + Y_haplogroup + ")\n\ncyspher query\n" + cq + "\n\nvisualization: run this query in the Neo4j browser\nMATCH p=(m1:DNA_Match{YHG:'" + Y_haplogroup + "'})-[r:match_by_segment]->(m2:DNA_Match) where m2.RN is not null RETURN p", true);
        
        return "completed";
    }
}
