/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.discovery;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class ancestral_surname_search {
    @UserFunction
    @Description("Wildcared search of ancestral surnames submitted by DNA testers. Result score can")

    public String ancestral_surnames(
        @Name("search_term") 
            String search_term

  )
   
         { 
             
        String s = find_matches(search_term);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String find_matches(String search_term) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String[] ss = search_term.split(",");
        String label = ss[0];
        String s = "";
        for (int i=0; i<ss.length; i++)
        {
            s = s + "'" + ss[i].toUpperCase().strip() + "'";
            if (i<ss.length-1){s = s + ",";}
        }
            
            String cq ="CALL db.index.fulltext.queryNodes('ancestor_surnames_names', '" + search_term + "') YIELD node, score WITH [[" + s + "]] as submitted,score,node.p as match, node.m as match_with_surnames, case when size(node.name)>20000 then left(node.name,200) + ' (truncated)' else node.name end as anc_names , apoc.coll.flatten(collect(split(toUpper(replace(node.name,' ','')),'/'))) as anc_list MATCH (m:DNA_Match{fullname:match})-[[rs:match_by_segment]]-(m2:DNA_Match{fullname:match_with_surnames}) with distinct m.fullname as source,case when apoc.coll.contains(submitted,toUpper(trim(m2.surname))) then 'x' else '~' end as own_surname, case when m.RN is null then '~' else m.RN end as source_rn, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.intersection(anc_list,submitted))) as found, match_with_surnames,rs.cm as cm,rs.seg_ct as segs, case when rs.rel is null then '~' else rs.rel end as rel,round(score,2) as score,anc_names as ancestor_list,submitted,anc_list where found<>[[]] return source,source_rn,found,score,match_with_surnames,own_surname,cm,segs,rel,ancestor_list order by rel desc,match_with_surnames,score desc,source"; 
                    //"CALL db.index.fulltext.queryNodes('ancestor_surnames_names', '" + search_term + "') YIELD node, score WITH [[" + s + "]] as submitted,score,node.p as match, node.m as match_with_surnames, case when size(node.name)>20000 then left(node.name,200) + ' (truncated)' else node.name end as anc_names , apoc.coll.flatten(collect(split(toUpper(replace(node.name,' ','')),'/'))) as anc_list MATCH (m:DNA_Match{fullname:match})-[[rs:match_by_segment]]-(m2:DNA_Match{fullname:match_with_surnames}) with distinct m.fullname as source,case when m.RN is null then '~' else m.RN end as source_rn,apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.intersection(anc_list,submitted))) as found, match_with_surnames,rs.cm as cm,rs.seg_ct as segs,case when rs.rel is null then '~' else rs.rel end as rel,round(score,2) as score,anc_names as ancestor_list,submitted,anc_list  where found<>[[]] return source,source_rn,found,score,match_with_surnames,cm,segs,rel,ancestor_list order by rel desc,score desc,source";
        
 try{      
    String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project + "_" + label + "_ancestor_surnames", "matches_with_surname", 1, "","1:###;3:##.##;6:###.#;7:####", "", false,"UDF:\nreturn gen.discovery.ancestral_surnames('" + search_term + "')\n\nCypher query:\n\n" + cq + "\n\nYou can use wildcards to find variations in the surname.\nSee: https://graphaware.com/neo4j/2019/01/11/neo4j-full-text-search-deep-dive.html ", true);
    
    
    cq="CALL db.index.fulltext.queryNodes('ancestor_surnames_names', '" + search_term + "') YIELD node, score WITH [['" + search_term + "']] as submitted,score,node.p as match, node.m as match_with_surnames, case when size(node.name)>20000 then left(node.name,200) + ' (truncated)' else node.name end as anc_names , apoc.coll.flatten(collect(split(toUpper(replace(node.name,' ','')),'/'))) as anc_list MATCH (m:DNA_Match{fullname:match})-[[rs:match_by_segment]]-(m2:DNA_Match{fullname:match_with_surnames}) with distinct m.fullname as source,case when apoc.coll.contains(submitted,toUpper(trim(m2.surname))) then 'x' else '~' end as own_surname, case when m.RN is null then '~' else m.RN end as source_rn, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.intersection(anc_list,submitted))) as found, match_with_surnames,rs.cm as cm,rs.seg_ct as segs, case when rs.rel is null then '~' else rs.rel end as rel, round(score,2) as score,anc_names as ancestor_list,submitted,anc_list where found<>[[]] with match_with_surnames match (ms:DNA_Match{fullname:match_with_surnames})-[[rm:match_by_segment]]-(ss:DNA_Match) with match_with_surnames as src, collect(distinct case when ss.ancestor_rn>0 then '*' + ss.fullname else ss.fullname end) as fn,collect( distinct ss.RN) as rns , collect(distinct ss.fullname) as fn2 with src, apoc.coll.sort(fn) as fn ,rns,fn2 with fn,fn2,src,rns where 50>size(fn)>1 with rns, src,apoc.coll.sort(apoc.coll.flatten(collect(distinct fn))) as fn,apoc.coll.sort(apoc.coll.flatten(collect(distinct fn2))) as fn2 return src, size(fn) as ct,fn,gen.rel.mrca_from_cypher_list(rns,15) as mrca,gen.dna_painter.DNA_Painter_shared_segs_matches_tomatch(apoc.coll.insert(fn2,0,src)) as dna_painter_query order by ct desc";
            //"CALL db.index.fulltext.queryNodes('ancestor_surnames_names', '" + search_term + "') YIELD node, score WITH [['CHAPMAN']] as submitted,score,node.p as match, node.m as match_with_surnames, case when size(node.name)>20000 then left(node.name,200) + ' (truncated)' else node.name end as anc_names , apoc.coll.flatten(collect(split(toUpper(replace(node.name,' ','')),'/'))) as anc_list MATCH (m:DNA_Match{fullname:match})-[[rs:match_by_segment]]-(m2:DNA_Match{fullname:match_with_surnames}) with distinct m.fullname as source,case when apoc.coll.contains(submitted,toUpper(trim(m2.surname))) then 'x' else '~' end as own_surname, case when m.RN is null then '~' else m.RN end as source_rn, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.intersection(anc_list,submitted))) as found, match_with_surnames,rs.cm as cm,rs.seg_ct as segs, case when rs.rel is null then '~' else rs.rel end as rel, round(score,2) as score,anc_names as ancestor_list,submitted,anc_list where found<>[[]] with match_with_surnames match (ms:DNA_Match{fullname:match_with_surnames})-[[rm:match_by_segment]]-(ss:DNA_Match) with match_with_surnames as src, collect(distinct case when ss.ancestor_rn>0 then '*' + ss.fullname else ss.fullname end) as fn,collect( distinct ss.RN) as rns with src, apoc.coll.sort(fn) as fn ,rns with fn,src,rns where 50>size(fn)>1 with rns, src,apoc.coll.sort(apoc.coll.flatten(collect(distinct fn))) as fn return src, size(fn) as ct,fn,gen.rel.mrca_from_cypher_list(rns,15) as mrca order by ct desc";
    gen.excelLib.queries_to_excel.qry_to_excel(cq,"matches_ancestor_surnames", "match_by_segment", 2, "","1:###;3:##.##;6:###.#;7:####", excelFile, false,"UDF:\nreturn gen.discovery.ancestral_surnames('" + search_term + "')\n\nCypher query:\n\n" + cq + "\n\nYou can use wildcards to find variations in the surname.\nSee: https://graphaware.com/neo4j/2019/01/11/neo4j-full-text-search-deep-dive.html ", true);
    
    
    cq = "CALL db.index.fulltext.queryNodes('ancestor_surnames_names', '" + search_term + "') YIELD node, score WITH [['" + search_term + "']] as submitted,score,node.p as match, node.m as match_with_surnames, case when size(node.name)>20000 then left(node.name,200) + ' (truncated)' else node.name end as anc_names , apoc.coll.flatten(collect(split(toUpper(replace(node.name,' ','')),'/'))) as anc_list MATCH (m:DNA_Match{fullname:match})-[[rs:match_by_segment]]-(m2:DNA_Match{fullname:match_with_surnames}) with distinct m.fullname as source,case when apoc.coll.contains(submitted,toUpper(trim(m2.surname))) then 'x' else '~' end as own_surname, case when m.RN is null then '~' else m.RN end as source_rn, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.intersection(anc_list,submitted))) as found, match_with_surnames,rs.cm as cm,rs.seg_ct as segs, case when rs.rel is null then '~' else rs.rel end as rel, round(score,2) as score,anc_names as ancestor_list,submitted,anc_list where found<>[[]] with match_with_surnames match (ms:DNA_Match{fullname:match_with_surnames})-[[rm:match_segment]]-(ss:Segment) with collect(distinct match_with_surnames) as src, ss.Indx as seg with seg,apoc.coll.sort(src) as src with seg,src where size(src)>1 with src,apoc.coll.sort(collect(distinct seg)) as segs return segs,size(src) as ct, src order by segs";
    gen.excelLib.queries_to_excel.qry_to_excel(cq,"segments", "segments", 3, "","1:###;3:##.##;6:###.#;7:####", excelFile, true,"UDF:\nreturn gen.discovery.ancestral_surnames('" + search_term + "')\n\nCypher query:\n\n" + cq + "\n\nYou can use wildcards to find variations in the surname.\nSee: https://graphaware.com/neo4j/2019/01/11/neo4j-full-text-search-deep-dive.html ", true);
    
    
        return "completed";
 }
 catch (Exception e) {return "Error. Try modifying your search term\n\nSee for a deeper dive into full text searching.\n https://graphaware.com/neo4j/2019/01/11/neo4j-full-text-search-deep-dive.html"; }
    }
}
