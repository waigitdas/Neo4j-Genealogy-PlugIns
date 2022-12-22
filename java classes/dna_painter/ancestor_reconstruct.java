/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna_painter;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class ancestor_reconstruct {
    @UserFunction
    @Description("Finds ancestor's descendants and their segments")

    public String ancestor_reconstruction(
        @Name("anc_rn") 
            Long anc_rn
  )
   
         { 
             
        String s = get_data(anc_rn);
         return s;
            }

      public static void main(String args[]) {
        get_data(3032L);
    }
   
    
     
     public static String get_data(Long rn) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
      String cqp = "match path=(p:Person{RN:" + rn + "})<-[[:father|mother*0..15]]-(q:Person) with collect (distinct q.RN) as rns match (m:DNA_Match)-[[rs:match_segment]]->(s:Segment) where m.RN in rns and rs.cm>=7 and rs.snp_ct>=500 and gen.dna.chr_portion_of_segment(s.chr,id(rs))< 0.5 with rns,collect(distinct s.Indx) as segs,apoc.coll.sort(collect(distinct m.RN)) as rns_desc_tester,apoc.coll.sort(collect(distinct m.fullname)) as fn_desc_tester match (m2:DNA_Match)-[[rs2:match_segment]]->(s2:Segment) where s2.Indx in segs with rns,segs,apoc.coll.sort(collect(distinct m2.RN)) as rns_all,apoc.coll.sort(collect(distinct m2.fullname)) as fn_all,rns_desc_tester,fn_desc_tester with rns,segs,rns_all,rns_desc_tester,fn_all,fn_desc_tester match (m3:DNA_Match)-[[rs3:match_segment]]->(s3:Segment) where s3.Indx in segs and m3.fullname in fn_desc_tester and rs3.p_anc_rn>0 and rs3.m_anc_rn>0 and gen.dna.chr_portion_of_segment(s3.chr,id(rs3))<0.5 with case when rs3.p<rs3.m then rs3.p + ':' + rs3.m else rs3.m + ':' + rs3.p end as fn3,s3,rs3 return distinct toInteger(case when s3.chr='0X' then 23 else s3.chr end) as chr,s3.strt_pos as start, s3.end_pos as end, rs3.cm as cm,rs3.snp_ct as snps, fn3 as match,'good' as confidence,rs3.pair_mrca as group,'maternal' as side, '' as notes,'' as color";
        
        
        String cq = "match path=(p:Person{RN:" + rn + "})<-[[:father|mother*0..15]]-(q:Person) with collect (distinct q.RN) as rns  match (m:DNA_Match)-[[rs:match_segment]]->(s:Segment) where m.RN in rns and rs.cm>=7 and rs.snp_ct>=500 and gen.dna.chr_portion_of_segment(s.chr,id(rs))<0.5 with rns,collect(distinct s.Indx) as segs,apoc.coll.sort(collect(distinct m.RN)) as rns_desc_tester,apoc.coll.sort(collect(distinct m.fullname)) as fn_desc_tester match (m2:DNA_Match)-[[rs2:match_segment]]->(s2:Segment) where s2.Indx in segs and rs2.cm<gen.dna.total_chr_cm(s2.chr) *0.5 and rs2.cm>=7 with rns,segs,apoc.coll.sort(collect(distinct m2.RN)) as rns_all,apoc.coll.sort(collect(distinct m2.fullname)) as fn_all,rns_desc_tester,fn_desc_tester with rns,segs,rns_all,rns_desc_tester,fn_all,fn_desc_tester   return size(rns) as descendant_ct,size(rns_desc_tester) as tester_ct,rns_desc_tester,fn_desc_tester,size(rns_all) as ct_all_rns,rns_all,fn_all,size(segs) as seg_ct, segs";
        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project + "_ancestor_descendant_segs", "Rollup", 1, "", "0:#,###;1:#,###;4:#,###;7:#,###", "", true, "UDF:\nreturn gen.dna_painter.ancestor_reconstruction(" + rn + ")\n\ncypher query\n" + cq + "\n\nDNA Painter query\n" + cqp + "\n\nThis is a summary of the descendants and their segments.\nColumn A is the total number of descendant; column B the descendants with at-DNA test results; columns C and D a list of them; coumns E,  F, and G re all descendant matches in the GEDCOM.\ncolumns H and I are the segments reported out in the DNA Painter file by match-pair.\n\n The accompanying DNA Painter file has the segments of the descentants of " + gen.gedcom.get_person.getPersonFromRN(rn, true) + ".\n\nReferences:\nhttps://dna-explained.com/2014/10/03/ancestor-reconstruction/?fbclid=IwAR254i11oH1ibwGYl0xeMn2S6R5Vk_xkXO14OAARta6WvGRGfmgtrN-rwv4\nhttps://dna-explained.com/category/ancestor-reconstruction/", true);
        
        gen.neo4jlib.neo4j_qry.qry_to_csv(cqp.replace("[[","[").replace("]]","]"), gen.neo4jlib.neo4j_info.project + "_DNA_Painter_ancestor_" + rn + "_desc_segments_" + gen.genlib.current_date_time.getDateTime() + ".csv");
        
        return "completed\n\nDNA Painter file in import directory.";
    }
}
