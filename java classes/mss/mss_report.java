/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.mss;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class mss_report {
    @UserFunction
    @Description("Monophylrtic segement sets reporting.")

    public String monophyletic_segment_set_report(
  )
   
         { 
             
        String s = create_mss_report();
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String create_mss_report() 
    {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        gen.rel.anc_rn arn = new gen.rel.anc_rn();
        Long anc_rn = arn.get_ancestor_rn();
        
        int ct =1;
        String cq="match (m1:DNA_Match)-[[rs:match_segment]]->(s:Segment) where rs.cm>=7 and rs.snp_ct>=500 and rs.p_rn is not null and rs.m_rn is not null with m1,rs,s , gen.rel.mrca_from_cypher_list([[rs.p_rn,rs.m_rn]],15) as mrcas with m1 as p,s,rs, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect(distinct rs.p_rn) + collect(distinct rs.m_rn)))) as rns,mrcas where mrcas >'-' return rs.p as proband,rs.m as match,case when rs.rel is null then '-' else rs.rel end as rel,s.chr as chr,s.strt_pos as strt_pos,s.end_pos as end_pos,rs.cm as cm,rns,mrcas order by s.chr,s.strt_pos,s.end_pos,rns";
        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project +  "_monophyletic report", "known_matches", ct, "", "3:##;4:###,###,###;5:###,###,###;6:####.#", "", false, "UDF: \nreturn gen.report.monophyletic_segment_set_report() \n\ncypher query:\n" + cq + "\n\nThis report lists DNA testers and matches who share the common ancestor(s) noted and the segments shared. The segments are ordered so you can see the overlaps. The boundaries of these segments represent crossover points.\n\nThe report is limited to known probands and matches who have a known MRCA. The next report finds known matches (e.g., in GEDCOM) who do not yet have an identified common ancestor.\n\nThis query is included for you to run in the Neo4j browser. It is long running (several minutes) and may produce many rows. It finds all matches to descendants of the specified commn ancestor and their segments\nThis shows the specificity of the segments attributed to the known common ancestor. The infrequency of the rows with the common ancestor illustrates both the specificity and also the overlaps of segments with other matches from other branches of the family tree of the various testers (probands). \nmatch (n:Person)-[[z:Gedcom_DNA]]->(m) with collect(m.RN) + collect(n.RN) as DM optional match path=(p:Person)<-[[:father|mother*0..99]]-(q:Person) where p.RN in [[" + anc_rn + "]] and q.RN in DM with p,path,collect(last(nodes(path))) as cEnds optional match (q:Person)-[[r:Gedcom_DNA]]->(s:Person) where q in cEnds with r,p,[[m in cEnds|m.fullname]] as E with p,apoc.coll.sort(apoc.coll.flatten(collect (distinct E))) as desc_tester match (m1:DNA_Match)-[[rs:match_segment]]->(s:Segment) where rs.p in desc_tester and gen.rel.mrca_from_cypher_list([[rs.p_rn,rs.m_rn]],15)> '' with p,s,collect(distinct rs.p) as probands,collect(distinct rs.m) as matches, collect(rs.p_rn) + collect(rs.m_rn) as rns return p.RN,s.chr as chr,s.strt_pos as strt_pos,s.end_pos as end_pos,size(probands) as ct,probands,size(matches) as ct2,matches,gen.rel.mrca_from_cypher_list(rns,15) as mrca order by s.chr,s.strt_pos,s.end_pos,rns \n\nThis query will produce a graph with MSS attached to the descendancy tree\nmatch path=(p1:Person{RN:33454})<-[[rf:father|mother*0..20]]-(p2:Person{RN:m.mrca})-[[rm:person_mss]]-(m:MSS) RETURN path", false);
        ct=ct+1;
        
        cq="match (m1:DNA_Match)-[[rs:match_segment]]->(s:Segment) where rs.cm>=7 and rs.snp_ct>=500 and rs.p_rn is not null and rs.m_rn is not null with m1,rs,s , gen.rel.mrca_from_cypher_list([[rs.p_rn,rs.m_rn]],15) as mrcas with m1 as p,s,rs, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect(distinct rs.p_rn) + collect(distinct rs.m_rn)))) as rns,mrcas return rs.p as proband,rs.m as match,case when rs.rel is null then '-' else rs.rel end as rel,s.chr as chr,s.strt_pos as strt_pos,s.end_pos as end_pos,rs.cm,rns,mrcas order by s.chr,s.strt_pos,s.end_pos,rns";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "monophyletic report2", "all_matches", ct, "", "3:##;4:###,###,###;5:###,###,###;6:####.#", excelFile, false, "UDF: \nreturn gen.report.monophyletic_segment_set_report() \n\ncypher query:\n" + cq + "\n\nThis adds DNA testers and matches who are in the GEDCOM but do not have a known shared common ancestor and the segment shared. The segments are ordered so you can see the overlaps. This allows you to see these added matches without an mrca in context of others, perhaps providing a clue about their location on a branch of the family tree.", false);
        ct=ct+1;
        
       cq="MATCH p=(mss:MSS)-[[r:ms_seg]]-(s:Segment)-[[rs:match_segment]]-(m:DNA_Match)  with   mss,case when m.RN is not null then '*' + m.fullname else m.fullname end as fn,r with mss,fn order by fn  with  gen.gedcom.person_from_rn(mss.mrca,true) as  mrca,collect(distinct fn) as fn RETURN mrca,size(fn) as ct,fn order by ct desc";
         gen.excelLib.queries_to_excel.qry_to_excel(cq, "mss_specificity", "specificity", ct, "", "0:##;1:####", excelFile, false,"cypher query:\n" + cq + "\n\nThis report finds all matches who map to segments in the monophyletic segment set. Notably, there are few of them and mostly identiable as descended from the mrca listed. The unknowns are likely descendants of the common ancestor.", false);
        ct=ct+1;
        
       cq="MATCH (m:MSS)-[[r:ms_seg]]->(s:Segment) with m,left(s.Indx,2) as chr with chr,count(*) as ct optional match (s2:Segment) where s2.chr=chr with chr,ct,collect(distinct s2.Indx) as sc with chr,size(sc) as chr_total_segs,ct as mss_seg_ct return chr,chr_total_segs,mss_seg_ct,apoc.math.round((toFloat(mss_seg_ct)/chr_total_segs)*100,2) as percent order by chr";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "mss_chr_cts", "chr frequency", ct, "", "0:##;1:###,###;2:###,#;3:0.##", excelFile, false,"cypher query:\n" + cq + "\n\nThis report finds and counts all segments in the monophyletic segment set and compares this number to the total segments on the chromosome. The very small number found is related to the specificity of the monophyletic segment set.", false);
        ct=ct+1;

        cq="MATCH p=(mss:MSS)-[[r:ms_seg]]->(s:Segment)-[[rm:match_segment]]-(m:DNA_Match) with mss,m,s order by m.fullname with mss.fullname + ' ⦋' + mss.mrca + '⦌' as ancestor,collect(distinct m.fullname) as fn,s with ancestor,fn,s order by ancestor with collect(distinct ancestor) as ancestors,fn,s with s,ancestors,fn return s.chr as chr,s.strt_pos as strt_pos,s.end_pos as end_pos, size(ancestors) as anc_ct, ancestors as mss_ancestors,size(fn) as match_ct,fn as matches order by s.chr,s.strt_pos,s.end_pos";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "mss_segs", "segs", ct, "", "0:##;1:###,###;2:###,#;3:####", excelFile, false,"cypher query:\n" + cq + "\n\nThis report shows segments with their matches and monophyletic ancestors.", false);
        ct=ct+1;
        
       cq="MATCH p=(mss:MSS)-[[r:ms_seg]]->(s:Segment)-[[rm:match_segment]]-(m:DNA_Match) with mss.fullname + ' ⦋' + mss.mrca + '⦌' as ancestor,m with ancestor,m order by ancestor with collect(distinct ancestor) as ancestors,m return m.fullname as match,case when m.RN is null then '~'  else toString(m.RN) end as RN, size(ancestors) as ct, ancestors as mss_ancestor order by match";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "mss_chr_cts", "match_mss_ancestors", ct, "", "0:##;1:######;2:###,###;3:###,#", excelFile, false,"cypher query:\n" + cq + "\n\nThis reports each match with their monophyletic ancestors.", false);
        ct=ct+1;
        
      cq="match path=(p1:Person{RN:" + anc_rn + "})<-[[rf:father|mother*0..20]]-(p2:Person{RN:m.mrca})-[[rm:person_mss]]-(m:MSS)-[[rs:ms_seg]]-(s:Segment) with p2,m,s,[[rn in nodes(path)|rn.RN]] as rns with p2.fullname as fn,p2.RN as rn,m.mrca as mrca,s.Indx as seg,[[val in rns WHERE val is not null]] as rns return fn,rn,seg,size(rns) as gen,rns as family_tree_path_to_MDCA order by seg,gen.graph.get_ordpath(rns)";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "mss_ordpath_anc", "mss_segs_ordpath", ct, "", "0:##;1:######;2:###,###;3:###,#", excelFile, false,"cypher query:\n" + cq + "\n\nThis reports each monophyletic ancestor, their monophyletic segments and the path to the most distant common ancestor.\nThis allows you to see whether a segment is passes down through different family lines.\nORDPATH is used to sort the path in family tree hierarchical order.", false);
        ct=ct+1;
        
        cq="MATCH path=(s:Segment)-[[r:segment_pop]]->(p:pop_group) with p,collect(distinct s.Indx) as sc match (mss:MSS)-[[rm:ms_seg]]-(s2:Segment) where s2.Indx in sc with s2,s2.chr as chr,s2.Indx as seg,count(*) as ct match (s2)-[[r2:segment_pop]]->(p2:pop_group) with s2,chr,seg,ct,collect(distinct p2.name) as pop_groups return chr,seg,ct,pop_groups order by chr,s2.chr,s2.strt_pos,s2.end_pos";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "mss_pop_grps", "mss_in_pop)groups", ct, "", "0:##;2:####", excelFile, true,"cypher query:\n" + cq + "\n\nThis report finds all population group segments which map to segments in the monophyletic segment set. Comingling of these two sets of segments is expected and might increase over time because family migrations and their family specific segments are informative.", false);
        ct=ct+1;
        
        
        
        return "monophyletic reports completed";
    }
}
