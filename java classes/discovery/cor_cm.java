/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.discovery;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class cor_cm {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String match_cor_cm_summaries(
  )
   
         { 
             
        String s = create_reports();
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String create_reports() 
    {
        int ct = 1;
        String cq = "MATCH (s:Segment)<-[[rm:match_segment]]-(md:DNA_Match) where rm.cm>=7 and rm.snp_ct>=500 and rm.cm<gen.dna.total_chr_cm(s.chr) *0.5 and rm.cor<0.1 with s,s.Indx as seg,min(rm.cm) as min_cm,max(rm.cm) as max_cm,count(md) as ct,sum(case when md.ancestor_rn is not null then 1 else 0 end) as ct_anc, apoc.coll.sort(collect(distinct case when md.RN is not null then '*' + md.fullname else md.fullname end)) as fn,collect(distinct md.RN) as rns, collect (distinct rm.p) as pfn,collect(distinct rm.m) as mfn, collect(distinct rm.gen_dist) as gd,rm.cor as cor,rm.rel as rel with s,seg,ct,min_cm,max_cm,ct_anc,size(fn) as match_ct, fn,gd,rns,toFloat(toFloat(max_cm)/toFloat(gen.dna.total_chr_cm(s.chr))) as pc,pfn,mfn,cor,rel with seg,ct,min_cm,max_cm,ct_anc,fn,gd,match_ct,rns,pc,pfn,mfn,cor,rel where match_ct>1 with seg,ct,min_cm,max_cm,pc as percent_chr_cm,ct_anc,size(fn) as match_ct,rel,gd as gen_dist,cor, fn,gen.rel.mrca_from_cypher_list(rns,15) as mrca,pfn,mfn optional match (mnew:DNA_Match)-[[rs2:match_segment]]-(s:Segment) where s.Indx=seg and not mnew.fullname in pfn and not mnew.fullname in mfn return seg,ct,min_cm as cm, percent_chr_cm,ct_anc,match_ct, case when rel='' then '~' else rel end as rel,case when gen_dist is null then '~' else gen_dist end as min_gen_dist,case when cor is null then 0 else cor end as cor, fn as match_pair, mrca,collect(distinct mnew.fullname) as other_matches_at_segment order by seg";
        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project + "_match_cor_cm", "detail", ct, "", "1:###;2:####.#;3:###%;4:###;5:###;7:##;8:0.##########", "", false, "cypher query:\n" + cq + "\n\nMatches at segments with their MRCAs. Relationships are shown in three column: \nrel is a text abbreviation (2C1r-second cousin once removed)\nminimum genetic distance is the path length between the match pair to their nearest common ancestor.\ncor: the correlation of relatedness\n\nEach project involves slight different segment ranges. GFG computes the total range on each chromosome and uses the HapMap to calculate the cm in the project on each chromosome. \nThis number is used to compute the percent of the chromosone for a given segment.\nA filter can then be applies to exclude segments lager than half those on the chromosome, which may happen with close relatives. ", true);
        ct=ct+1;
        
       cq = "MATCH (n:chr_cm) with sum(n.cm)*2 as chr_cm MATCH p=(m1)-[[r:match_by_segment]]->(m2) where r.cor is not null with chr_cm,m1,m2,r optional match(f:fam_rel) where r.rel=f.relationship and f.MeanSharedCM>0 RETURN distinct m1.fullname as match1,m2.fullname as match2,round(r.cm,2) as observed_cm,round(chr_cm*r.cor,2) as expected_cm, case when r.rel='' then '~' else r.rel end as rel,case when f.MeanSharedCM is null then '~' else f.MeanSharedCM  end as Shared_CM_Mean, case when f.MeanSharedCM is null then '~' else f.LowSharedCM + '-' + f.HighSharedCM end as Shared_CM_Range,r.cor as cor order by cor"; 
       gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project + "_match_cor_cm", "observed_expected", ct, "", "2:####.##;3:####.##;4:####;7:0.##########", excelFile, false,"cypher query:\n" +  cq + "\n\nFor each match pair the actual or observed shatrf cm is compared to the excepect cm using two methods:\nthe values in the SharedCM Project\nusing the total centimorgan on all chromosome in the project multiplied by the correlation of relatedness.\n\nThe SharedCM project assumed and assigned zero as the minumum for relationships more distant that H2C. ", true);
       ct = ct + 1;
       
       cq = "MATCH p=(m)-[[r:match_segment]]->(s) where r.cm>=7 and r.snp_ct>=500 with r.gen_dist as gen_dist,count(*) as match_seg_edge_ct,apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect(distinct r.p) + collect(distinct r.m)))) as fn,collect(distinct s.Indx) as segs,collect(distinct r.rel) as rels ,apoc.coll.sort(collect(distinct r.cor)) as observed_cor with gen_dist,match_seg_edge_ct,size(fn) as distinct_match_ct,size(segs) as distinct_seg_ct, rels,observed_cor return gen_dist as min_gen_dist,match_seg_edge_ct,distinct_match_ct as distinct_kit_match_ct,distinct_seg_ct,rels,observed_cor order by gen_dist"; 
       gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project + "_gen_dist", "observed_expected", ct, "", "0:###;1:###;2:###;3:###;5:0.###########", excelFile, true,"cypher query:\n" +  cq + "\n\n", true);
       ct = ct + 1;
       
        return "completed";
    }
}
