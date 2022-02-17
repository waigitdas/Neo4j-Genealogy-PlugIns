/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class matches_at_seg {
    @UserFunction
    @Description("Returns list of matches with the previously specified common ancestor at the chromosome region whose boundaries are submitted")

    public String matches_at_chr_region(
        @Name("chr") 
            String chr,
        @Name("strt") 
            Long strt,
        @Name("end") 
            Long end
  )
   
         { 
             
        String m = get_matches(chr,strt,end,gen.neo4jlib.file_lib.currExcelFile);
         return m;
            }

    
    
    public static void main(String args[]) {
       //System.out.println(get_matches("21",Long.valueOf(9990360),Long.valueOf(31384543)));
    }
    
     public String get_matches(String chr,Long strt, Long end, String excelFile)
    {
        
        String cq=""; 
        String r = "";
        //String ef = "";
        try{
            cq= "match (m:DNA_Match)-[[r:match_segment]]-(s:Segment) where  s.chr='" + chr + "' and s.strt_pos>=" + strt + " and s.end_pos<=" + end + " with m,r.cm as cm, case when m.fullname=r.p then  case when r.m_rn is not null then '*' +  r.m + ' ⦋' + r.m_rn + '⦌'  else r.m end  else  case when r.p_rn is not null then '*' + r.p + ' ⦋' + r.m_rn + '⦌' else r.p  end end + ' {' + toInteger(r.cm) + '}'  as seg_match order by m.fullname with  max(cm) as max_cm,min(cm) as min_cm,case when m.RN is not null then '*' + m.fullname + ' ⦋' + m.RN + '⦌' else m.fullname end as match,collect(distinct seg_match) as seg_matches with min_cm,max_cm,match,size(seg_matches) as ct,apoc.coll.sort(seg_matches) as matches_at_region order by ct desc,matches_at_region  return match,min_cm,max_cm,ct,matches_at_region";
           //match (m:DNA_Match)-[r:match_segment]-(s:Segment) where m.ancestor_rn is not null and s.chr='" + chr + "' and s.strt_pos>=" + strt + " and s.end_pos<=" + end + " with m order by m.fullname with distinct m.fullname + ' ⦋' + m.RN + '⦌' as match return match";
       gen.neo4jlib.file_lib.currExcelFile= gen.excelLib.queries_to_excel.qry_to_excel(cq, "Matches at chr_region", "Region matches", 1, "", "1:###.#;2:####.#;3:####;4:###", excelFile,gen.neo4jlib.file_lib.openExcelFile, "UDF: gen.dna.matches_at_chr_region(" + chr + ", " + strt + ", " + end + ")\n\nmatches at chr " + chr + " between positions " + strt + " and " + end + " where the * prefix indicates those who are identified.\nThe number in brackets {} are the centimorgans shared with the match (column A)\n\nthe query is\n" + cq,true);
        return gen.neo4jlib.file_lib.currExcelFile;
    }
    catch (Exception e) {return "None: " + cq.replace("⦋","[").replace("⦌","]");} 

    
    //return gen.neo4jlib.file_lib.currExcelFile;
    }
}
