/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class match_relationships {
    @UserFunction
    @Description("Reports relationships of all matches by segments when the match is in the family tree (e.g., RN >0).")

    public String known_match_relationships(
        
  )
   
         { 
             
        String r = get_rel();
         return r;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_rel() 
    {
        String cq = "MATCH p=(m1:DNA_Match)-[r:match_by_segment]->(m2:DNA_Match) where m1.RN>0 and m2.RN>0 and m1.fullname<m2.fullname\n" +
"with m1.fullname as match1, m2.fullname as match2,gen.rel.relationship_from_RNs(m1.RN,m2.RN) as rel,r.cm as cm,r.mbp as mbp,r.seg_ct as segs \n" +
"return match1,match2,rel,cm as shared_cm,mbp,segs order by cm desc,rel,match1";
        gen.excelLib.queries_to_excel.qry_to_excel(cq,"match_relationships","relationships",1,"4:10;5:10;6:10", "3:####.0;4:####;5:###","",true,"",true);
        return "completed";
    }
}
