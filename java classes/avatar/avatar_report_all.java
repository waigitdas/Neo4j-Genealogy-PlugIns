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


public class avatar_report_all {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String report_all(
  )
   
         { 
             
        String s = create_report();
         return s;
            }

    
    
    public static void main(String args[]) {
        create_report();
    }
    
     public static String create_report() 
    {
               ////////////////////////////////////////////////////////////////////////
       /////////////////////  REPORTS//////////////////////////////////////////
       ////////////////////////////////////////////////////////////////////////
       ////////////////////////////////////////////////////////////////////////
       
       int ct = 1;
      String fn = "avatar_report_all.csv";
      String UDF_query = "return gen.avatar.report_all()";
      String cq ="";
      String excelFile = "";
        //Count of avatar nodes and relationships
       cq ="match (d:Avatar) return labels(d) as item,count(*) as ct union match ()-[[r:avatar_segment]]->() return type(r) as item,count(*) as ct";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_report", "nodes and rel", ct, "", "1:#,###2:####", "", false,"UDF:\n" + UDF_query + "\n\ncypher query:\n" + cq, false);
       ct = ct+1;

       //tracking file generated during iteration of descendants
       gen.excelLib.excel_from_csv.load_csv(fn, excelFile, "load_log", ct, "", "0:#####;1:######;2:###,###;3:######", excelFile, false,"Tracking generated during creation of the avatars. The source is the descendant of the targeted ancestor who is processed and the number of rows imported. \nThe imports overlap and will not be duplicated when encountered in a later kit.\nSegments will be created for each row imported.",false);
       
       //sources
       cq ="MATCH (a:Avatar)-[[r:avatar_segment]]->() with a,r match (p:Person{RN:r.source}) with p.fullname + ' [[' + p.RN + ']]'as source,collect(distinct a.fullname + ' [' + a.RN + ']') as avatars return source,avatars";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_sources", "source_kits", ct, "", "1:######;2:###,###;4:###,###;5:###,###;6:###,###", excelFile, false, "UDF:\n" + UDF_query + "\n\ncypher query:\n" + cq + "\n\nFTDNA kits that were the source of segments attributed to avatars.", false);
       ct = ct+1;

//       //discovery of avatar matches
       cq = "MATCH p=(d:DNA_Match)-[[r:match_segment]]->(s:Segment)<-[[rv:avatar_segment]]-(v:Avatar) where d.RN is null RETURN d.fullname as Discovered_DNA_Tester,case when d.RN is null then 0 else d.RN end as RN,collect(distinct v.fullname) as matching_avatars,collect(distinct s.Indx) as segs";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_discovery", "discovered_matches", ct, "", "1:######;2:###,###", excelFile, false, "UDF:\n" + UDF_query + "\n\ncypher query:\n" + cq + "\n\nAvatar segments shared with actual DNA testers who do not have a record number in the curated file and/or family tree.", false);
       ct = ct+1;

       //avatar shared matches
       cq = "MATCH (k:Avatar)-[[r:avatar_segment]]-(s:Segment) where s.chr<>'0X' with case when r.p<r.m then r.p else r.m end as Match1, case when r.p>r.m then r.p else r.m end as Match2,collect(distinct (s.end_pos-s.strt_pos)/1000000.0) as m,collect(distinct r.cm) as c,count(*) as segment_ct,r.rel as rel with Match1,Match2,m,c,segment_ct,apoc.coll.min(m) as shortest_mbp,apoc.coll.max(m) as longest_mbp,apoc.coll.min(c) as shortest_cm,apoc.coll.max(c) as longest_cm,rel with Match1,Match2,apoc.coll.sum(m) as mbp,apoc.coll.sum(c) as cm,segment_ct,shortest_mbp,longest_mbp,shortest_cm,longest_cm,rel RETURN Match1 as Avatar_Match1,Match2 as Avatar_Match2,rel,segment_ct,mbp,cm,shortest_mbp,longest_mbp,shortest_cm,longest_cm order by cm desc";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_shared_matches", "avatar_shared_matches", ct, "", "2:###,###;3:###,###.##;4:###,###.##;5:#,###.##;6:###,###.##;7:###,###.##;8:###.##;9:##.##", excelFile, false, "UDF:\n" + UDF_query + "\n\ncypher query:\n" + cq, false);
       ct = ct+1;

       //avatar segment coverage
       cq ="MATCH (a:Avatar)  RETURN a.fullname as avatar,a.RN as RN,case  when a.segment_coverage is null then 0 else a.segment_coverage end as segment_coverage, case when a.paternal_cm is null then 0 else a.paternal_cm end as paternal_cm, case when a.maternal_cm is null then 0 else a.maternal_cm end as maternal_cm , a.total_cm as total_cm order by segment_coverage desc";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_list", "segment_coverage", ct, "", "1:######;2:0.########;3:#####;4:####;5:#####", excelFile, false, "UDF:\n" + UDF_query + "\n\ncypher query:\n" + cq + "\n\nSegment coverage and parental sources of coverage by avatar. \n\nGFG uses the term segment coverage to distinguish its meaning from DNA coverage.\nDNA coverage, in GFG, is a theoretical amount of DNA cM expected from a set of testers.\nsegment coverage is the actual cM attributable to avatars.", false);
       ct = ct+1;

      //avatar segment coverage vs expected
       cq ="MATCH (a:Avatar) where a.stat_coverage is not null and  a.segment_coverage is not null RETURN a.fullname as avatar,a.stat_coverage as stat_coverage, a.segment_coverage as segment_coverage,a.segment_coverage/a.stat_coverage as ratio order  by ratio desc";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Coverage_obs_expected", "segment_coverage", ct, "", "1:0.########;2:0.########;3:0.########", excelFile, false, "UDF:\n" + UDF_query + "\n\ncypher query:\n" + cq + "\n\nSegment coverage aobserved compared to the expected computed from the testers available\n\nGFG uses the term segment coverage to distinguish its meaning from DNA coverage.\nDNA coverage, in GFG, is a theoretical amount of DNA cM expected from a set of testers.\nsegment coverage is the actual cM attributable to avatars.", false);
       ct = ct+1;

      //avatar list by origin
       cq ="MATCH p=(v:Avatar)-[[r:avatar_segment]]->(s:Segment) with v.fullname as fullname,v.RN as RN, count(*) as seg_ct,apoc.coll.sort(collect(distinct r.source)) as sources return fullname,RN, seg_ct, sources order by seg_ct desc";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_list", "avatar_lineages", ct, "", "1:######;2:###,###;4:###,###;5:###,###;6:###,###", excelFile, false, "UDF:\n" + UDF_query + "\n\ncypher query:\n" + cq + "\n\nList of other avatar RNs associated with the avatar. Segments will be double counted because of the aggregation; they do not reconcile to other sheets.", false);
       ct = ct+1;

       //avatar list by names 
       cq = "MATCH p=(d:DNA_Match)-[[r:match_segment]]->(s:Segment)<-[[rv:avatar_segment]]-(v:Avatar) where d.RN is not null with v.fullname as v,v.RN as RN,collect(distinct d.fullname) as dc,count(*) as source_segment_ct,sum(case when rv.avatar_side='maternal' then 1 else 0 end) as maternal ,sum(case when rv.avatar_side='paternal' then 1 else 0 end) as paternal return v as Avatar,RN as RN, size(dc) as match_ct,source_segment_ct,maternal,paternal,source_segment_ct/size(dc) as aver_ct_per_match,dc as DNA_Matches order by match_ct desc";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Arvatar_names", "direct_method_segs", ct, "", "1:######;2:###,###;3:###,###;4:###,###;5:###,###;6:###,###", excelFile, false, "UDF:\n" + UDF_query + "\n\ncypher query:\n" + cq + "\n\nSegments counts for maternal or paternaln atributions are in columns E and F\n\nList of avatars with their shared matches. Segments will be double counted because of the aggregation; they do not reconcile to other sheets.", false);
       ct = ct+1;
       
       //total CSegs
       cq = "MATCH (a:Avatar) where a.total_cm is not null with a, a.fullname as avatar, a.RN as RN, a.paternal_cm as CSeg_paternal_cm, a.maternal_cm as CSeg_maternal_cm, a.total_cm as CSeg_total_cm, apoc.math.round(a.segment_coverage,3) as ancestor_coverage RETURN avatar,RN, ancestor_coverage, CSeg_paternal_cm,CSeg_maternal_cm,CSeg_total_cm order by CSeg_total_cm desc";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Arvatar_names", "CSeg_cm", ct, "", "1:######;2:0.####;3:###,###;4:###,###;5:###,###;6:###,###", excelFile, false, "UDF:\n" + UDF_query + "\n\ncypher query:\n" + cq + "\n\nAvatar CSeg cM, which avoids double counting of segments attributed to the avatar by parental side. \nThe coverage reported here will agree with that when uploading an individual avataar report DNA Painter file to DNA Painter", false);
       ct = ct+1;
       
       
       //attribution methods
       cq = "MATCH p=(a:Avatar)-[r:avatar_segment]->(s:Segment) with a.fullname as avatar,count(*) as seg_ct,toInteger(sum(case when r.side_method='direct' then r.cm else 0 end)) as direct_cm,toInteger(sum(case when r.side_method='collateral' then r.cm else 0 end)) as collateral_cm,toInteger(sum(case when r.side_method='infer' then r.cm else 0 end)) as infer_cm with avatar, seg_ct, direct_cm, collateral_cm,infer_cm,direct_cm+collateral_cm+infer_cm as total_cm with avatar, seg_ct, direct_cm, collateral_cm,infer_cm, total_cm where total_cm>0 return avatar, seg_ct, direct_cm, collateral_cm,infer_cm, total_cm";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Arvatar_names", "attribution_method", ct, "", "1:######;2:0.####;3:###,###;4:###,###;5:###,###;6:###,###", excelFile, false, "UDF:\n" + UDF_query + "\n\ncypher query:\n" + cq + "\n\nAvatar cM assigned to a parental side by each method. The cM are for MSegs and the totals are thus double counted because the overlap. \nThis report help us understand the relative contribution of the different methods to te cM.", false);
       ct = ct+1;
       
       //avatar side categorization
       cq = "MATCH p=()-[[r:avatar_segment]]->() RETURN case when r.avatar_side is null then 'null' else r.avatar_side end as avatar_side,count(*) as seg_ct";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_side", "avatar_side", ct, "", "1:######;2:###,###", excelFile, false, "UDF:\n" + UDF_query + "\n\ncypher query:\n" + cq + "\n\nThis reports the classification of the parential side of the avatar's segments.\nThis exclused the classifications of the side of the match-pair of the segment.", false);
       ct = ct+1;
       
       //avatar segments
       cq ="MATCH p=(v:Avatar)-[[r:avatar_segment]]->(s:Segment) with s.Indx as segment,apoc.coll.sort(collect(distinct v.fullname)) as avatar_relatives,replace(replace(r.pair_mrca,'â¦','⦋'),'â¦','⦌')  as mrca,apoc.coll.sort(collect(distinct r.source)) as sources RETURN segment, sources,size(avatar_relatives) as rel_ct, avatar_relatives, mrca order by segment";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_segs", "segments", ct, "", "1:######;2:###,###", excelFile, false, "UDF:\nr" + UDF_query + "\n\ncypher query:\n" + cq, false);
       ct = ct+1;

       //avatar segment details
       cq ="MATCH p=(a:Avatar)-[[r:avatar_segment]]->(s:Segment) with s, case when r.avatar_side='maternal; paternal' or r.avatar_side='paternal; maternal' then 'both' else r.avatar_side end as side with s,apoc.coll.sort(collect(distinct side)) as side,sum(case when side='both' then 1 else 0 end) as both,sum(case when side='maternal' then 1 else 0 end) as maternal,sum(case when side='paternal' then 1 else 0 end) as paternal,sum(case when side='unknown' then 1 else 0 end) as unknown,sum(case when side is null then 1 else 0 end) as no_category,count(*) as ct return s.Indx, side as source_side,both, maternal,paternal,unknown,no_category, ct as total_source_ct order by s.Indx";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_seg_detail", "segment_details", ct, "", "1:######;2:###;3:###;4:###;5:###;6:###;7:###,###", excelFile, true, "UDF:\nr" + UDF_query + "\n\ncypher query:\n" + cq, false);
       ct = ct+1;

        return "completed";
    }
}
