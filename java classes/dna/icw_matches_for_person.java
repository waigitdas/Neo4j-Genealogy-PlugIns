/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class icw_matches_for_person {
    @UserFunction
    @Description("finds icw matches for a proband and one match.")

    public String person_icw_matches(
        @Name("proband") 
            String proband,
        @Name("match") 
            String match
  )
   
         { 
             
        String r = get_icw(proband,match);
         return r;
            }

    
    
    public static void main(String args[]) {
        String r = get_icw("David A Stumpf","26429");
    }
    
     public static String get_icw(String proband, String match) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        String p = "p:'" + proband + "'";
        String m = "fullname:'" + match + "'";
        
        if (isAllNumbers(proband)==true)
        {
            p = "p_rn:" + proband;
        }
         if (isAllNumbers(match)==true)
        {
            m = "RN:" + match;
        }
 
         String cq =  "MATCH p1=(d:DNA_Match)-[[r1:match_segment{" + p + "}]]->(s1:Segment) with collect(distinct d) as ds unwind ds as d1 call { with d1 MATCH p1=(d1:DNA_Match)-[[r1:match_segment{" + p + "}]]->(s1:Segment) MATCH p2=(d2:DNA_Match{" +  m + "})-[[r2:match_segment{p_rn:1}]]->(s2:Segment) with r1.m as m1,r2.m as m2 ,s1,s2,r1,r2 with m1,m2,s1,s2,r1,r2 where s1.chr=s2.chr and ((s1.strt_pos<s2.end_pos and s1.end_pos>s2.strt_pos) or (s2.strt_pos<s1.end_pos and s2.end_pos>s1.strt_pos)) return m1,m2,sum(gen.dna.segment_overlap(s1.Indx,s2.Indx)) as shared_cm } with m1,m2,shared_cm where shared_cm>7 return m2 as proband,m1 as match,shared_cm";
         String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "icw.csv", "matches", 1, "", "2:####.#;3:####", "", true, "UDF:\nreturn gen.dna.person_icw_matches('" + proband + "', '" + match + "')\n\n cypher query:\n" + cq + "\n\n", false);
         
         return "Excal should open";
        
        
    }
     
     public static Boolean isAllNumbers(String x)
     {
         //uses RegEx to verify if the string is all numbers
        String re = "^\\d+$";
        Pattern pnum = Pattern.compile(re);
        Matcher mnum = pnum.matcher(x.replace("\"", ""));
        return  mnum.find();
      }
}
