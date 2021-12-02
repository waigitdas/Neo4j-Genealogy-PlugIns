/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class tg_match_summary {
    @UserFunction
    @Description("Lists triangulation groups with boundaries and matches. ")

    public String tg_matches(
  )
   
         { 
             
        String r = get_matches();
         return r;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
     public String get_matches() 
    {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        gen.rel.anc_rn anc = new gen.rel.anc_rn();
        Long anc_rn = anc.get_ancestor_rn();
        String anc_name = gen.gedcom.get_family_tree_data.getPersonFromRN(anc_rn,true);   
        String cq = "match p=(m:DNA_Match{ancestor_rn:" + anc_rn + "})-[rm:match_segment{p_anc_rn:" + anc_rn + ",m_anc_rn:" + anc_rn + "}]-(s:Segment)-[rs:seg_seq]-(s2:Segment) where 100>=rm.cm>=7 and rm.snp_ct>=500 with rs,m,s order by m.fullname with s,rs,m,m.fullname + ' ⦋' + m.RN + '⦌' as match with rs.tgid as tg,collect(distinct match) as matches, collect (distinct m.RN) as rns, max(s.chr) as chr, min(s.strt_pos) as start, max(s.end_pos) as end with tg,matches,chr,start,end,gen.dna.hapmap_cm(chr,start,end) as overlap_cm,(end-start)/1000000 as overlap_mbp,rns match (t:tg) where t.tgid=tg return tg,t.chr as chr,t.strt_pos as tg_start,t.end_pos as tg_end,t.cm as cm, start as overlap_start,end as overlap_end,overlap_cm,overlap_mbp, size(matches) as match_ct,matches,rns order by chr,start,end";
         //with mrca = times out 
         //String cq = "match p=(m:DNA_Match{ancestor_rn:" + anc_rn + "})-[rm:match_segment{p_anc_rn:" + anc_rn + ",m_anc_rn:" + anc_rn + "}]-(s:Segment)-[rs:seg_seq]-(s2:Segment) where 100>=rm.cm>=7 and rm.snp_ct>=500 with rs,m,s order by m.fullname with s,rs,m,m.fullname + ' [' + m.RN + ']' as match with rs.tgid as tg,collect(distinct match) as matches, collect (distinct m.RN) as rns, max(s.chr) as chr, min(s.strt_pos) as start, max(s.end_pos) as end with tg,matches,chr,start,end,gen.dna.hapmap_cm(chr,start,end) as overlap_cm,(end-start)/1000000 as overlap_mbp,gen.rel.mrca_from_cypher_list(rns,10) as mrca match (t:tg) where t.tgid=tg return tg,t.chr as chr,t.strt_pos as tg_start,t.end_pos as tg_end,t.cm as cm, start as overlap_start,end as overlap_end,overlap_cm,overlap_mbp, size(matches) as match_ct,mrca as mrca_of_matches,matches order by chr,start,end";
        gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project +  "_TG_matches", "matches", 1, "", "0:###;1:##;2:###,###,###;3:###,###,###;4:###.#;5:###,###,###;6:###,###,###;7:###.#;8:###.#;9:###", "", true,"The common ancestor is " + anc_name + "\nYou can use the RN list in return gen.rel.mrca_from_list('{rn list}',10) to compute the MRCAs\nDoing this with long list increases the processing time.");
        return "completed";
    }
}
