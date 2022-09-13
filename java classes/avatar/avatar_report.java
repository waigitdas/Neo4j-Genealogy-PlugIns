/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.avatar;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class avatar_report {
    @UserFunction
    @Description("Report for specified avatar")

    public String avatar_reports(
        @Name("rn") 
            Long rn
  )
   
         { 
             
        get_report(rn);
         return "";
            }

    
    
    public static void main(String args[]) {
        get_report(3L);
    }
    
     public static String get_report(Long rn) 
    {
        //excel output
        String excelFile = "";
        int ct = 1;
        String anc_name = gen.gedcom.get_family_tree_data.getPersonFromRN(rn, true);
        String cq  = "with " + rn + " as rn MATCH p=(d:Avatar{RN:rn})-[[r:avatar_segment]]->(s:Segment) RETURN   d.fullname as Avatar,d.RN as RN,case when r.avatar_side is null then '~' else r.avatar_side end  as side, case when r.p<r.m then r.p + ' : ' + r.m else r.m + ' : ' + r.p end as match_pair,r.rel as rel,r.cor  as cor, case when r.p<r.m then r.p_side + ' : ' + r.m_side else r.m_side + ' : ' + r.p_side end as source_sides, s.Indx as seg,min(r.cm) as cm,replace(replace(r.pair_mrca,'â¦','⦋'),'â¦','⦌')as mrca  order by seg";
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_Reoprt_" + rn + "_" + gen.genlib.current_date_time.getDateTime() , "MSeg_detail", ct, "", "1:#####;5:0.#######;8:####.#", "", false,"UDF:\nreturn gen.avatar.avatar_reports(" + rn + ")\n\ncypher query:\n" +  cq, false);
        
        ct = ct +1;
        cq = "with " + rn + " as rn MATCH p=(d:Avatar{RN:rn})-[[r:avatar_avsegment]]->(s:avSegment) with s,d.fullname as Avatar,d.RN as RN,case when r.side is null then '~' else r.side end as side, s.Indx as seg,min(s.cm) as cm optional match (s)-[[r2:avseg_seg]]-(s2:Segment) with  Avatar,RN,side,seg as avatar_CSegs,cm,apoc.coll.sort(collect(distinct s2.Indx)) as DNA_match_segs return Avatar,RN,side,avatar_CSegs,cm,size(DNA_match_segs) as ct,DNA_match_segs order by avatar_CSegs";
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_Reoprt_" + rn , "CSegs", ct, "","1:#####;4:####.#;5:###", excelFile, false,"cypher query:\n" +  cq + "\n\nThis report shows how the CSegs are merged from MSegs.", false);
        
        ct = ct +1;
        cq = "with " + rn + " as rn MATCH p=(a:Avatar{RN:rn})-[[r:avatar_segment]]->(s:Segment)<-[[rm:match_segment]]-(d:DNA_Match) where (rm.p_rn=d.RN or rm.m_rn=d.RN) with a.fullname as Avatar,a.RN as RN,d.fullname as match,d.RN as dRN,sum(r.cm) as a_cm,sum(rm.cm) as match_cm,apoc.coll.sort(collect(distinct s.Indx)) as segs return Avatar,RN as aRN,match as DNA_Match,dRN,a_cm,match_cm,size(segs), segs";
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_Reoprt_" + rn , "MSegs_matches", ct, "", "1:#####;3:#####;4:####.#;5:####.#;6:#####", excelFile, false,"cypher query:\n" +  cq, false);

        ct = ct +1;
        cq = "with " + rn + " as rn MATCH p=(a:Avatar{RN:rn})-[r:avatar_segment]->(s:Segment)<-[rm:match_segment]-(d:DNA_Match) with a.fullname as Avatar,a.RN as RN,d.fullname as match,d.RN as dRN,sum(r.cm) as a_cm,sum(rm.cm) as match_cm,apoc.coll.sort(collect(distinct s.Indx)) as segs where dRN is null return Avatar,RN as aRN,match as DNA_Match,a_cm,match_cm,size(segs), segs";
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_Reoprt_" + rn , "MSegs_discovery", ct, "", "1:#####;3:#####;4:####.#;5:####.#;6:#####", excelFile, false,"cypher query:\n" +  cq + "\n\nMatch who are not in the family tree but may be in the family line of the prpositus in this report.", false);

        ct = ct +1;
        cq = "with "+ rn + " as rn MATCH p=(a:Avatar{RN:rn})-[[r:avatar_segment]]->(s:Segment) match (s2:Segment)<-[[rm:match_segment]]-(d:DNA_Match) where s.chr=s2.chr and s.strt_pos=s2.strt_pos with a.fullname as Avatar,a.RN as RN,d.fullname as match,d.RN as dRN,sum(r.cm) as a_cm,sum(rm.cm) as match_cm,apoc.coll.sort(collect(distinct s.Indx)) as segs,rm.p + ':' + rm.m as match_pair,r.avatar_side as side where dRN is null return Avatar,RN as aRN,match as DNA_Match,a_cm,match_cm,size(segs), segs, match_pair,side as avatar_side order by segs";
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_Reoprt_" + rn , "MSegs_start_discovery", ct, "", "1:#####;3:#####;4:####.#;5:####.#;6:#####", excelFile, false,"cypher query:\n" +  cq + "\n\nThe new matches match only as the MSeg start position, which is a  crossover point.\nThey are not as likely to be in the targeted  family tree branch.", false);

        ct = ct +1;
        cq = "with "+ rn + " as rn MATCH p=(a:Avatar{RN:rn})-[[r:avatar_segment]]->(s:Segment) match (s2:Segment)<-[[rm:match_segment]]-(d:DNA_Match) where s.chr=s2.chr and s.end_pos=s2.end_pos with a.fullname as Avatar,a.RN as RN,d.fullname as match,d.RN as dRN,sum(r.cm) as a_cm,sum(rm.cm) as match_cm,apoc.coll.sort(collect(distinct s.Indx)) as segs,rm.p + ':' + rm.m as match_pair,r.avatar_side as side where dRN is null return Avatar,RN as aRN,match as DNA_Match,a_cm,match_cm,size(segs), segs, match_pair,side as avatar_side order by segs";
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_Reoprt_" + rn , "MSegs_end_discovery", ct, "", "1:#####;3:#####;4:####.#;5:####.#;6:#####", excelFile, true,"cypher query:\n" +  cq + "\n\nThe new matches match only as the MSeg end position, which is a  crossover point.\nThey are not as likely to be in the targeted  family tree branch.", false);

        //DNA Painter file
        String pf ="avatar_" + rn + "_dna_painter" + "_" + gen.genlib.current_date_time.getDateTime()  + ".csv";
        cq = "with " + rn + " as rn MATCH p=(d:Avatar{RN:rn})-[r:avatar_segment]->(s:Segment) RETURN distinct toInteger(case when s.chr='0X' then 23 else s.chr end) as chr,s.strt_pos as start,s.end_pos as end,r.cm as cM,r.snp_ct as snps, case when r.p<r.m then r.p + ':' + r.m else r.m + ':' + r.p end + case when r.compare_match is not null then ':' + r.compare_match else '' end  as match,'good' as confidence,r.pair_mrca as group, r.avatar_side as side,'' as notes,'' as color";
        gen.neo4jlib.neo4j_qry.qry_to_csv(cq,pf);       
        
        gen.svg.chromosome_painter cpa = new gen.svg.chromosome_painter();
        cpa.chr_paint(rn, pf, anc_name);
        return "";
        
    }
}
