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
        String cq = "CALL db.index.fulltext.queryNodes('ancestor_surnames_names', '" + search_term + "') YIELD node, score WITH score,node.p as match,node.name as anc_names MATCH (m:DNA_Match{fullname:match}) return distinct m.fullname as DNA_Match_name,case when m.RN is null then '-' else toString(m.RN) end as RN,round(score,2) as score,anc_names as ancestor_list order by score desc,m.fullname";
 try{       gen.excelLib.queries_to_excel.qry_to_excel(cq, "sncestor_surnames", "matches_with_surname", 1, "","1:###,2:##.##", "", true,"UDF:\nreturn gen.discovery.ancestral_surnames('" + search_term + "')\n\nCypher query:\n\n" + cq + "\n\nYou can use wildcards to find variations in the surname.\nSee: https://graphaware.com/neo4j/2019/01/11/neo4j-full-text-search-deep-dive.html ", true);
        return "completed";
 }
 catch (Exception e) {return "Error. Try modifying your search term\n\nSee for a deeper dive into full text searching.\n https://graphaware.com/neo4j/2019/01/11/neo4j-full-text-search-deep-dive.html"; }
    }
}
