/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.mss;

import gen.neo4jlib.neo4j_qry;
import java.util.regex.Pattern;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class descendancy_with_mss {
    @UserFunction
    @Description("Descendant mms report")

    public String descendant_monophyletic_segment_matches(
        @Name("anc_rn") 
            Long anc_rn
  )
   
         { 
             
        String s = get_mss_desc(anc_rn);
         return s;
            }

    
    
    public static void main(String args[]) {
        get_mss_desc(4441L);
    }
    
     public static String get_mss_desc(Long anc_rn) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String anc = gen.gedcom.get_family_tree_data.getPersonFromRN(anc_rn, true);
        String fn = gen.neo4jlib.neo4j_info.project + "_descendant_" + anc_rn + "_mss";
        
         String cq = "with " + anc_rn + " as anc match path=(p:Person{RN:anc})<-[[:father|mother*0..15]]-(q:Person)  with anc,[[m in nodes(path)|m.fullname + gen.neo4jlib.RNwithBrackets(m.RN)]] as E,[[m in nodes(path)|m.RN]] as R,collect(last(nodes(path))) as cEnds  with anc,[[m in cEnds|m.fullname + gen.neo4jlib.RNwithBrackets(m.RN)]] as descendant,R,E,gen.graph.get_ordpath(R) as op, size(R) as gen,[[m in cEnds|m.RN]] as mRN  optional match (mt:DNA_Match) where mt.RN in mRN  with anc, collect (mt.RN) as mRNs  with mRNs,anc match (ms:DNA_Match)-[[rs:match_segment]]-(s:Segment) where rs.p_rn is not null and rs.m_rn is not null with s,rs where gen.rel.is_direct_ancestor(rs.p_rn,anc) = true and gen.rel.is_direct_ancestor(rs.m_rn,anc) = true  with collect(s) as segs unwind segs as x call { with x  match (ms2:DNA_Match)-[[rs2:match_segment]]-(s2:Segment) where rs2.cm>=7 and rs2.cm<gen.dna.total_chr_cm(s2.chr) * 0.5 and s2=x with distinct  ms2,case when rs2.p<rs2.m then rs2.p + ' : ' + rs2.m else rs2.m + ' : ' + rs2.p end as newmatch,apoc.coll.sort(collect(distinct s2.Indx)) as segs2 return ms2,newmatch,segs2 } with newmatch,case when ms2.RN is not null then '󠀠'  else 'Y' end as fn, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect(distinct segs2)))) as segs2 return newmatch as match_pair,fn as new, size(segs2) as match_mss_seg_ct, segs2 as mss_segs order by mss_segs";
                 
         String cqp = "with " + anc_rn + "  as anc match path=(p:Person{RN:anc})<-[[:father|mother*0..15]]-(q:Person) with anc,[[m in nodes(path)|m.fullname + gen.neo4jlib.RNwithBrackets(m.RN)]] as E,[[m in nodes(path)|m.RN]] as R,collect(last(nodes(path))) as cEnds with anc,[[m in cEnds|m.fullname + gen.neo4jlib.RNwithBrackets(m.RN)]] as descendant,R,E,gen.graph.get_ordpath(R) as op, size(R) as gen,[[m in cEnds|m.RN]] as mRN optional match (mt:DNA_Match) where mt.RN in mRN with anc, collect (mt.RN) as mRNs with mRNs,anc match (ms:DNA_Match)-[[rs:match_segment]]-(s:Segment) where rs.p_rn is not null and rs.m_rn is not null with s,rs where gen.rel.is_direct_ancestor(rs.p_rn,anc) = true and gen.rel.is_direct_ancestor(rs.m_rn,anc) = true with collect(s) as segs unwind segs as x call { with x match (ms2:DNA_Match)-[[rs2:match_segment]]-(s2:Segment) where rs2.cm>=7 and rs2.cm<gen.dna.total_chr_cm(s2.chr) *0.5 and s2=x with distinct rs2, ms2,case when rs2.p<rs2.m then rs2.p + ' : ' + rs2.m else rs2.m + ' : ' + rs2.p end as newmatch, s2 as segs2 return rs2,ms2,newmatch,segs2 } with rs2, apoc.coll.sort(collect(distinct newmatch)) as match_pairs, segs2 as segs return distinct case when segs.chr='0X' then 23 else toInteger(segs.chr) end as chr,segs.strt_pos as start_pos,segs.end_pos as end_pos, rs2.cm as cm, rs2.snp_ct as snps, match_pairs as match,'good' as confidence,case when rs2.pair_mrca is null then 'new' else rs2.pair_mrca end as group,'maternal' as side, '' as note order by chr,start_pos,end_pos";
          
         String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, fn, "mss_matches", 1, "", "2:###;3:###", "", false,"ancestor in this analysis: :" + anc +"\n\nUDF query:\n" + "return gen.mss.descendant_monophyletic_segment_matches(" + anc_rn + ")\n\ncypher query:\n" + cq + "\n\nThis is a powerful query finding a very select set of matches. You will likely see familar people, but the purpose is to find rare new matches hiding in the big data, You may or may not see any new people.\n\nThis query uses specific crossover points (start and end positions of segments) of direct descendants of the targetted ancesor and then uses these rare segments to find matches to the descendants.\n\nDNA Painter query\n" + cqp + "\n\nEducational note: The cypher query uses a subquery which is iterated using the unwind command to produce the list of segments for each match-pair.\n\nSee next tab for the direct ancestors and their segments which were the prelude to creating this worksheet.", true);
         
        String csv = gen.neo4jlib.file_lib.ReadFileByLineWithEncoding(gen.neo4jlib.neo4j_info.Import_Dir +  fn + ".csv");
        String c[] = csv.split("\n");
        String new_segs="";
        String Q = "'";
        
        for (int cs=1; cs<c.length; cs++)
        {
            String ccs[]=c[cs].split(Pattern.quote("|"));
            try{
                if (ccs[1].equals("Y")){
                new_segs = new_segs + Q + ccs[3] + Q + ",";
            }
            }
            catch(Exception e){}
        }
        new_segs = new_segs.replace("[","").replace("]","");
        new_segs=new_segs.substring(0,new_segs.length()-1);
        
        String cqvis = "MATCH p=(m:DNA_Match)-[[r:match_segment]]->(s:Segment) where s.Indx in [[" + new_segs + "]] RETURN p";
        
        cq = "MATCH p=(d:DNA_Match)-[[r:match_segment]]->(s:Segment) where s.Indx in [[" + new_segs + "]] with r,case when r.p<r.m then r.p + ' : ' + r.m else r.m + ' : ' + r.p end as match_pair,s where r.p_rn is null or r.m_rn is null with r, match_pair, collect (distinct s.Indx) as segs match (d1:DNA_Match{fullname:r.p}) with r, match_pair, segs , case when d1.YHG is null then '~' else d1.YHG end as p_Y, case when d1.mtHG is null then '~' else d1.mtHG end as p_mt with r, match_pair, segs , p_Y, p_mt match (d2:DNA_Match{fullname:r.m}) with r, match_pair, segs , p_Y, p_mt, case when d2.YHG is null then '~' else d2.YHG end as m_Y, case when d2.mtHG is null then '~' else d2.mtHG end as m_mt,case when r.p_rn is null then r.p  else r.m end as new_match return new_match, match_pair,size(segs) as ct, segs, p_Y as match1_YHG,p_mt as match1_mtHG, m_Y as match2_YHG, m_mt as match2_mtHG order by toUpper(new_match)";
               gen.excelLib.queries_to_excel.qry_to_excel(cq, "new_match_segs", "new_match_segs", 2, "", "1:###;3:###", excelFile, false,"ancestor in this analysis: :" + anc +"\n\nUDF query:\n" + "return gen.mss.descendant_monophyletic_segment_matches(" + anc_rn + ")\n\ncypher query:\n" + cq + "\n\nvisualization query:\n" + cqvis + "\n\nThis is a list of dscovered new matches. You will find them as a match in the kit of their match_pair mate.", true); 


// redundant        
//        cq="with " + anc_rn + " as anc match path=(p:Person{RN:anc})<-[[:father|mother*0..15]]-(q:Person) with anc,[[m in nodes(path)|m.fullname + gen.neo4jlib.RNwithBrackets(m.RN)]] as E,[[m in nodes(path)|m.RN]] as R,collect(last(nodes(path))) as cEnds with anc,[[m in cEnds|m.fullname + gen.neo4jlib.RNwithBrackets(m.RN)]] as descendant,R,E,gen.graph.get_ordpath(R) as op, size(R) as gen,[[m in cEnds|m.RN]] as mRN optional match (mt:DNA_Match) where mt.RN in mRN with anc, collect (mt.RN) as mRNs with mRNs,anc match (ms:DNA_Match)-[[rs:match_segment]]-(s:Segment) where rs.cm>=7 and rs.cm<gen.dna.total_chr_cm(s.chr) * 0.5  and rs.p_rn is not null and rs.m_rn is not null with s,rs where gen.rel.is_direct_ancestor(rs.p_rn,anc) = true and gen.rel.is_direct_ancestor(rs.m_rn,anc) = true with case when rs.p<rs.m then rs.p + ' : ' + rs.m else rs.m + ' : ' + rs.p end as match_pair,apoc.coll.sort(collect(s.Indx)) as segs return match_pair,size(segs) as mss_seg_ct, segs as mss_segs";
//        gen.excelLib.queries_to_excel.qry_to_excel(cq, "direct_desc", "direct_descendants", 3, "", "1:###;3:###", excelFile, false,"ancestor in this analysis: :" + anc +"\n\nUDF query:\n" + "return gen.mss.descendant_monophyletic_segment_matches(" + anc_rn + ")\n\ncypher query:\n" + cq + "\n\nThis is a list of the direct descendant match-pair(s) of " + anc + " and their segments. \nThese are the monophyletic segments used to try to identify other matches with overlapping segment. They are reported on the prior worksheet.", true); 


    cq = "with " + anc_rn + " as anc match path=(p:Person{RN:anc})<-[[:father|mother*0..15]]-(q:Person)  with anc,[[m in nodes(path)|m.fullname + gen.neo4jlib.RNwithBrackets(m.RN)]] as E,[[m in nodes(path)|m.RN]] as R,collect(last(nodes(path))) as cEnds  with anc,[[m in cEnds|m.fullname + gen.neo4jlib.RNwithBrackets(m.RN)]] as descendant,R,E,gen.graph.get_ordpath(R) as op, size(R) as gen,[[m in cEnds|m.RN]] as mRN  optional match (mt:DNA_Match) where mt.RN in mRN  with anc, collect (mt.RN) as mRNs  with mRNs,anc match (ms:DNA_Match)-[[rs:match_segment]]-(s:Segment) where rs.p_rn is not null and rs.m_rn is not null with s,rs where gen.rel.is_direct_ancestor(rs.p_rn,anc) = true and gen.rel.is_direct_ancestor(rs.m_rn,anc) = true  with collect(s) as segs unwind segs as x call {     with x      match (ms2:DNA_Match)-[[rs2:match_segment]]-(s2:Segment) where rs2.cm>=7 and rs2.cm<gen.dna.total_chr_cm(s2.chr) *0.5  and s2=x  with distinct  ms2,case when rs2.p<rs2.m then rs2.p + ' : ' + rs2.m else rs2.m + ' : ' + rs2.p end as newmatch,apoc.coll.sort(collect(distinct s2.Indx)) as segs2 return ms2,newmatch,segs2 } with apoc.coll.sort(collect(distinct newmatch)) as match_pairs, segs2 as segs return segs as mss_segment,size(match_pairs) as ct,match_pairs order by mss_segment";
         gen.excelLib.queries_to_excel.qry_to_excel(cq, "segments", "segments", 3, "", "1:###;2:###", excelFile, true,"ancestor in this analysis: :" + anc +"\n\nUDF query:\n" + "return gen.mss.descendant_monophyletic_segment_matches(" + anc_rn + ")\n\ncypher query:\n" + cq + "\n\nThis is a list of the direct descendant match-pair(s) of " + anc + " and their segments. \nThese are the monophyletic segments used to try to identify other matches with overlapping segment. They are reported on the prior worksheet.", true); 
        
         
        
           gen.neo4jlib.neo4j_qry.qry_to_csv(cqp.replace("[[","[").replace("]]","]"), gen.neo4jlib.neo4j_info.project + "_DNA_Painter_" + anc_rn + "_descendant_segments_" + gen.genlib.current_date_time.getDateTime() + ".csv");
        
        return "completed";
    }
}
