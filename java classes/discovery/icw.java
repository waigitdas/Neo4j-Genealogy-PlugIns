/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.discovery;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class icw {
    @UserFunction
    @Description("Produces a comprehensive list of in-common-with matches for each pair of known persons. This triangulation uses the match_by_segment relationship to discover three persons matching each other. The icw list includes other known person (marked with an * prefix) and may include other matches who would be relevant for further research.")

    public String in_common_with_matches(
  )
   
         { 
             
        get_matches();
         return "";
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_matches() 
    {
        
        String cq = "match (m1:DNA_Match)-[[r1:match_by_segment]]->(icw:DNA_Match)<-[[r2:match_by_segment]]-(m2:DNA_Match) where m1.fullname<m2.fullname and m1<>icw and m2<>icw and r1.cm>=7 and r2.cm>=7 and m1.ancestor_rn is not null and m2.ancestor_rn is not null with m1,m2,case when icw.RN is null then icw.fullname else '*' + icw.fullname + ' ⦋' + icw.RN + '⦌' end + ' {' + toInteger(r1.cm) + ', ' + toInteger(r2.cm) + '}' as fn with m1,m2,fn order by fn with m1,m2,collect(fn) as cicw with m1,m2,cicw where size(cicw)<=50 return m1.fullname + ' ⦋' + m1.RN + '⦌' as match1,m2.fullname + ' ⦋' + m2.RN + '⦌' as match2,size(cicw) as ct,cicw as in_common_with_matches";
        gen.excelLib.queries_to_excel.qry_to_excel(cq,"icw_matches", "matches", 1, "", "2:###", "", true, "UDF: \nreturn gen.discovery.icw_matches()\n\nThe icw matches with an * prefix are those sharing the common ancestor.\nThe numbers in brackets[] are the shared cm of the icw and match1 and match2 respectively\n\nquery:\n" + cq,true);
        return "completed";
    }
}
