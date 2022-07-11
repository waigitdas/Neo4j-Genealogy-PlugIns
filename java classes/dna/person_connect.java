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


public class person_connect {
    @UserFunction
    @Description("Creates a summary for a specific match.")

    public String person_connect_clues(
        @Name("fullname") 
            String fullname
  )
   
         { 
             
        String s =  get_connections(fullname);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_connections(String fullname) 
    {
        String surname = gen.neo4jlib.neo4j_qry.qry_to_csv("MATCH (n:DNA_Match{fullname:'" + fullname + "'}) RETURN n.surname as surname LIMIT 1").split("\n")[0];
        surname = surname.replace("\"", "");
      
        String cq = "match (m:DNA_Match{fullname:'" + fullname + "'})-[[rs:match_segment]]-(s:Segment) with s,m.fullname as name,rs.p as propositus,rs.m as match order by s.Indx with name,propositus,match,collect(distinct s.Indx) as segs return name, propositus,match,size(segs) as shared_seg_ct,segs";
                //"match (m:DNA_Match{fullname:'" + fullname + "'})-[[rs:match_segment]]-() with m.fullname as name,rs.p as propositus,rs.m as match return name,propositus,match";
        int ct = 1;
        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, fullname + "_explorer", "matches at segments", ct, "", "", "",false, cq, false);
        ct= ct +1;
        
        cq = "match (m:DNA_Match{fullname:'" + fullname + "'})-[[rs:match_by_segment]]-(mm:DNA_Match) with m.fullname as propositus, mm.fullname as match,rs.cm as shared_cm,rs.x_cm as x_cm,rs.rel as relationship return propositus,match,shared_cm,x_cm,relationship";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "mbs", "matches by segments", ct, "", "2:####.#;3:####.#", excelFile,false, cq, false);
        ct = ct + 1;
        
        cq = "match (m:DNA_Match{fullname:'" + fullname + "'})-[[rs:shared_match]]-(mm:DNA_Match) with m.fullname as propositus, mm.fullname as match,rs.cm as shared_cm,rs.x_cm as x_cm,rs.rel as relationship return propositus,match,shared_cm,x_cm,relationship";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "shared_matches", "shared_matches", ct, "", "2:####.#;3:####.#", excelFile,false, cq, false);
        ct = ct + 1;
        
        cq = "MATCH p=(mss:MSS)-[[r:ms_seg]]->(s:Segment)-[[rs:match_segment]]-(m:DNA_Match{fullname:'" + fullname + "'}) with s,s.Indx as Indx, collect(distinct r.mrca) as mrca_rn,collect(distinct mss.fullname) as mrca_name, case when s.phased_anc is null then '~' else s.phased_anc end as phased_anc ,collect(distinct m.fullname) as fn return Indx,mrca_rn,mrca_name, phased_anc,fn order by s.chr,s.strt_pos,s.end_pos";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "sements", "segments", ct, "", "2:####.#;3:####.#", excelFile,false, cq, false);
        ct = ct + 1;
        
       cq = "match (m:DNA_Match{fullname:'" + fullname + "'})-[[rs:match_segment]]-(s:Segment)-[[rm:ms_seg]]-(mss:MSS) with m,rs,s,mss order by s.Indx with m.fullname as name,rs.p as propositus,rs.m as match,mss.fullname as monophylytic_ancestor,collect(distinct s.Indx) as segs return monophylytic_ancestor,name,propositus,match,segs";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "person_explorer", "monophylytic group", ct, "", "", excelFile,false, cq, false);
        ct = ct + 1;
        
       cq = "CALL db.index.fulltext.queryNodes('ancestor_surnames_names', '" + surname + "') YIELD node, score WITH score,node.p as match,node.m as match_with_surnames,case when size(node.name)>200 then left(node.name,200) + ' (truncated)' else node.name end as anc_names MATCH (m:DNA_Match{fullname:match})-[rs:match_by_segment]]-(m2:DNA_Match{fullname:match_with_surnames}) return distinct m.fullname as source,case when m.RN is null then '~' else toString(m.RN) end  as source_rn, match_with_surnames,rs.cm as cm,rs.seg_ct as segs,rs.rel as rel,round(score,2) as score,anc_names as ancestor_list order by rel desc,score desc,source";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "anc_surname", "ancestral_surnames", ct, "", "1:#####;3:####.#;4:####;6:###.###", excelFile,false, cq, false);
        ct = ct + 1;
 
       cq = "MATCH p=(m1:DNA_Match)-[[r:match_by_segment]]-(m2:DNA_Match) where m1.RN is not null and m2.surname='" + surname + "' and m1<>m2 RETURN m1.fullname as propositus, m2.fullname as source_match,r.cm as shared_cm,r.rel as rel  order by shared_cm desc ";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "surname_matches", "surname_matches", ct, "", "2:####.#;2:###.#", excelFile,false, cq, false);
        ct = ct + 1;
 
        cq = "match (m:DNA_Match{fullname:'" + fullname + "'})-[rs:match_tg]-(t:tg) with t,m.fullname as propositus,rs.p as match_propositus, rs.m as match,rs.seg_ct as seg_ct,count(*) as ct order by t.name with propositus,match_propositus,match,seg_ct,ct,collect(distinct t.name) as tg,collect(distinct t.Indx) as segs return propositus,match_propositus,match,size(tg) as tg_ct, tg as tgs,size(segs) as seg_ct,segs";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "tgs", "tgs", ct, "", "3:###.#;3:###.#", excelFile,true, cq, false);
        ct = ct + 1;
            
        
        return "person report completed";
    }
}
