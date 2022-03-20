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
        String[] ss = search_term.split(",");
        String label = ss[0];
        String s = "";
        for (int i=0; i<ss.length; i++)
        {
            s = s + "'" + ss[i].toUpperCase().strip() + "'";
            if (i<ss.length-1){s = s + ",";}
        }
            
            String cq ="CALL db.index.fulltext.queryNodes('ancestor_surnames_names', '" + search_term + "') YIELD node, score WITH [[" + s + "]] as submitted,score,node.p as match, node.m as match_with_surnames, case when size(node.name)>20000 then left(node.name,200) + ' (truncated)' else node.name end as anc_names , apoc.coll.flatten(collect(split(toUpper(replace(node.name,' ','')),'/'))) as anc_list MATCH (m:DNA_Match{fullname:match})-[rs:match_by_segment]-(m2:DNA_Match{fullname:match_with_surnames}) with distinct m.fullname as source,case when apoc.coll.contains(submitted,toUpper(trim(m2.surname))) then 'x' else '~' end as own_surname, case when m.RN is null then '~' else m.RN end as source_rn, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.intersection(anc_list,submitted))) as found, match_with_surnames,rs.cm as cm,rs.seg_ct as segs, case when rs.rel is null then '~' else rs.rel end as rel,round(score,2) as score,anc_names as ancestor_list,submitted,anc_list where found<>[] return source,source_rn,found,score,match_with_surnames,own_surname,cm,segs,rel,ancestor_list order by rel desc,match_with_surnames,score desc,source"; 
                    //"CALL db.index.fulltext.queryNodes('ancestor_surnames_names', '" + search_term + "') YIELD node, score WITH [[" + s + "]] as submitted,score,node.p as match, node.m as match_with_surnames, case when size(node.name)>20000 then left(node.name,200) + ' (truncated)' else node.name end as anc_names , apoc.coll.flatten(collect(split(toUpper(replace(node.name,' ','')),'/'))) as anc_list MATCH (m:DNA_Match{fullname:match})-[[rs:match_by_segment]]-(m2:DNA_Match{fullname:match_with_surnames}) with distinct m.fullname as source,case when m.RN is null then '~' else m.RN end as source_rn,apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.intersection(anc_list,submitted))) as found, match_with_surnames,rs.cm as cm,rs.seg_ct as segs,case when rs.rel is null then '~' else rs.rel end as rel,round(score,2) as score,anc_names as ancestor_list,submitted,anc_list  where found<>[[]] return source,source_rn,found,score,match_with_surnames,cm,segs,rel,ancestor_list order by rel desc,score desc,source";
        
 try{      
    gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project + "_" + label + "_ancestor_surnames", "matches_with_surname", 1, "","1:###;3:##.##;6:###.#;7:####", "", true,"UDF:\nreturn gen.discovery.ancestral_surnames('" + search_term + "')\n\nCypher query:\n\n" + cq + "\n\nYou can use wildcards to find variations in the surname.\nSee: https://graphaware.com/neo4j/2019/01/11/neo4j-full-text-search-deep-dive.html ", true);
        return "completed";
 }
 catch (Exception e) {return "Error. Try modifying your search term\n\nSee for a deeper dive into full text searching.\n https://graphaware.com/neo4j/2019/01/11/neo4j-full-text-search-deep-dive.html"; }
    }
}
