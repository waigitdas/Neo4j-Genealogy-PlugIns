/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class match_by_seg_anc_desc {
    @UserFunction
    @Description("match by segment when both the kit owner and match share a common ancestor specified by enhancement to the graph by the function return gen.tgs.setup_tg_environment(ancestor_rn). The enhancement function must be run before running this function.")

    public String matches_by_segments_anc_desc(
//        @Name("rn1") 
//            Long rn1,
//        @Name("rn2") 
//            Long rn2
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
        gen.rel.anc_rn anc = new gen.rel.anc_rn();
        String anc_name = gen.gedcom.get_family_tree_data.getPersonFromRN(anc.get_ancestor_rn(),true);
        String cq = "match (s1:Segment) with s1 order by s1.chr,s1.strt_pos,s1.end_pos with collect(distinct s1) as segs1 match (m1:DNA_Match)-[r1:match_segment]-(s2:Segment) where s2 in segs1 and r1.cm>=7 and r1.snp_ct>=500 and r1.p_anc_rn is not null and r1.m_anc_rn is not null with r1.p_anc_rn as anc_rn,case when m1.RN>0 then '*' + m1.fullname + ' ⦋' + m1.RN + '⦌' else m1.fullname end as m2, case when m1.RN>0 then m1.RN else null end as rns1, case when r1.m_rn>0 then r1.m_rn else null end as rns2, case when r1.m_rn>0 then '*' + r1.m + ' ⦋' + r1.m_rn + '⦌' else r1.m end as m3,max(r1.cm) as max_cm,min(r1.cm) as min_cm, s2,count(r1) as edgect, sum(case when r1.p=m1.fullname then 1 else 0 end) as unidir_ct_p, sum(case when r1.m=m1.fullname then 1 else 0 end) as unidir_ct_m with anc_rn,s2,min_cm,max_cm,apoc.coll.union(collect (distinct m2), collect(distinct m3)) as matches, apoc.coll.union(collect(distinct rns1),collect(distinct(rns2))) as rns, sum(edgect) as edgect,sum(unidir_ct_m) as unidir_ct_m,sum(unidir_ct_p) as unidir_ct_p order by s2.chr,s2.strt_pos,s2.end_pos with anc_rn,s2,min_cm,max_cm,apoc.coll.sort(apoc.coll.flatten(matches)) as matches, apoc.coll.sort(apoc.coll.flatten(rns)) as rns,edgect,unidir_ct_m,unidir_ct_p  return s2.chr as chr,s2.strt_pos as start_pos,s2.end_pos as end_pos,apoc.math.round(min_cm,1) as cm,edgect,unidir_ct_p,unidir_ct_m,size(rns) as kits,size(matches) as match_ct,anc_rn,rns,matches";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "MRCA desc match segments", "matches", 1, "", "0:##;1:###,###,###;2:###,###,###;3:##.#;4:###;5:###;6:###;7:###;8:###;9:#####", "", true,"Common ancestor set to " + anc_name);
        return "completed";
    }
}
