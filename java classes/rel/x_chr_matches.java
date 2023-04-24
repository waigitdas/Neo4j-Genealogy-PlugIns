/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class x_chr_matches {
    @UserFunction
    @Description("Returns all known persons with shared x-chr segement(s).")

    public String x_chr_segment_matches(

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
        String cq ="match (ms:DNA_Match where ms.RN>0 )-[[r:match_segment]]->(s:Segment where s.chr='0X') where r.rel is not null with ms.RN as rn1,r.m_rn as rn2,r,r.p as p, r.m as m,r.rel as rel,s order by s.Indx with p,m,rn1,rn2, rel,collect(distinct s.Indx) as segs,sum(r.cm) as cm where rn2>0 return p,m,rn1,rn2,rel,round(cm,1) as x_cm, segs order by x_cm desc";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "x-matches_segs", "x_match segs", 1, "", "2:####,3:####;5:####.#", "", true, "query is\n" + cq, true);
        return "";
    }
}
