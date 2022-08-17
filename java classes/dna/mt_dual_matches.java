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


public class mt_dual_matches {
    @UserFunction
    @Description("mt_ and a-DNA dual matches")

    public String mt_DNA_dual_matches(
        @Name("mt_haplogroup") 
            String mt_haplogroup
  )
   
         { 
             
        get_matches(mt_haplogroup);
         return "";
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_matches(String mt_haplogroup) 
    {
        String cq ="with '" + mt_haplogroup + "' as hg MATCH p=(m1:DNA_Match{mtHG:hg})-[r:match_by_segment]->(m2:DNA_Match{mtHG:hg})  RETURN m1.fullname as match1,m2.fullname as match2,m1.mtHG as mt_haplogroup";
         String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project +  "_mt_dual_matches", mt_haplogroup, 1, "","1:####;3:####;4:#####", "", false,"udf\nrreturn gen.dna.mt_dual_matches(" + mt_haplogroup + ")\n\ncyspher query\n" + cq + "\n\nDual matches to at- and mt-DNA are rare. Nothing may be returned. If there are results, these are excellent clues for further traditional genealogy research.", true);
        
         cq = "MATCH p=(m1:DNA_Match{mtHG:'" + mt_haplogroup + "'})-[r:match_by_segment]->(m2:DNA_Match) where m2.RN is not null RETURN m1.fullname as match1,m2.fullname as match2, m1.mtHG as mtHG1,case when m2.mtHG is null then '~' else m2.mtHG end as mtHG2,r.cm as shared_autosomal_cm, gen.dna.shared_matches_two_fullnames(m1.fullname,m2.fullname) as shared_autosomal_matches ";
         gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project +  "_mt_dual_matches", mt_haplogroup, 2, "","1:####;3:####;4:####.###", excelFile, true,"udf\nrreturn gen.dna.mt_dual_matches(" + mt_haplogroup + ")\n\ncyspher query\n" + cq + "\n\nvisualization: run this query in the Neo4j browser\nMATCH p=(m1:DNA_Match{mtHG:'" + mt_haplogroup + "'})-[r:match_by_segment]->(m2:DNA_Match) where m2.RN is not null RETURN p", true);
        
        return "completed";
    }
}
