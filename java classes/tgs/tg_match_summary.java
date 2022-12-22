/**
 * Copyright 2021-2023 
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
    @Description("Lists triangulation groups with boundaries and matches. Enter minimum ans maximum cm and whether you want MRCAs")

    public String tg_matches(
        @Name("min_cm") 
            Long min_cm,
        @Name("max_cm") 
            Long max_cm
        )
   
         { 
             
        String r = get_matches(min_cm,max_cm,true);
         return r;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
     public String get_matches(Long min_cm, Long max_cm, Boolean mrcas) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        gen.rel.anc_rn anc = new gen.rel.anc_rn();
        Long anc_rn = anc.get_ancestor_rn();
        String anc_name = gen.gedcom.get_person.getPersonFromRN(anc_rn,true);  
        String cq="";
        if (mrcas == true) {
        cq = "match p=(m:DNA_Match{ancestor_rn:" + anc_rn + "})-[[rm:match_segment{p_anc_rn:" + anc_rn + ",m_anc_rn:" + anc_rn + "}]]-(s:Segment)-[[rs:seg_seq]]-(s2:Segment) where " + max_cm + ">=rm.cm>=" + min_cm + " and rm.snp_ct>=500 with rs,m,s order by m.fullname with s,rs,m,m.fullname + ' ⦋' + m.RN + '⦌' as match with rs.tgid as tg,collect(distinct match) as matches, collect (distinct m.RN) as rns, max(s.chr) as chr, min(s.strt_pos) as start, max(s.end_pos) as end with tg,matches,chr,start,end,gen.dna.hapmap_cm(chr,start,end) as overlap_cm,(end-start)/1000000 as overlap_mbp,rns match (t:tg) where t.tgid=tg return tg,t.chr as chr,t.strt_pos as tg_start,t.end_pos as tg_end,t.cm as cm, start as overlap_start,end as overlap_end,overlap_cm,overlap_mbp, gen.rel.mrca_from_cypher_list(rns,15) as mrcas_of_matches, size(matches) as match_ct,matches,rns order by chr,start,end";

        }
        else{
        cq = "match p=(m:DNA_Match{ancestor_rn:" + anc_rn + "})-[[rm:match_segment{p_anc_rn:" + anc_rn + ",m_anc_rn:" + anc_rn + "}]]-(s:Segment)-[[rs:seg_seq]]-(s2:Segment) where " + max_cm + ">=rm.cm>=" + min_cm + " and rm.snp_ct>=500 with rs,m,s order by m.fullname with s,rs,m,m.fullname + ' ⦋' + m.RN + '⦌' as match with rs.tgid as tg,collect(distinct match) as matches, collect (distinct m.RN) as rns, max(s.chr) as chr, min(s.strt_pos) as start, max(s.end_pos) as end with tg,matches,chr,start,end,gen.dna.hapmap_cm(chr,start,end) as overlap_cm,(end-start)/1000000 as overlap_mbp,rns match (t:tg) where t.tgid=tg return tg,t.chr as chr,t.strt_pos as tg_start,t.end_pos as tg_end,t.cm as cm, start as overlap_start,end as overlap_end,overlap_cm,overlap_mbp, size(matches) as match_ct,matches,rns order by chr,start,end";
        }
 
        gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project +  "_TG_matches", "matches", 1, "", "0:###;1:##;2:###,###,###;3:###,###,###;4:###.#;5:###,###,###;6:###,###,###;7:###.#;8:###.#;9:###", "", true,"UDF:\n return gen.tgs.tg_matches(" + min_cm + ", " + max_cm + ") \n\ncypher query:\n" + cq,true);
        return "completed";
    }
}
