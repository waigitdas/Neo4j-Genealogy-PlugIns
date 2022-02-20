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
        String[] s = search_term.split(",");
        
        String n = "";
        String f = " WITH node, m, score, anc_names,[[w in split(node.name,' ') where ";
        
       
        for (int i = 0; i< s.length; i++){
            n = n + s[i].strip();
            f = f + "toLower(w) =~ toLower('" + s[i].strip().replace("*","[a-zA-Z]") + "')";  //use regex
            if (i < s.length - 1){
                n = n + " AND ";
                f = f + " or ";
            }
        }
        
        f = f.replace(" "," ") + "|w]] as words ";
        
        
       String  cq = "CALL db.index.fulltext.queryNodes('ancestor_surnames_names','" + n + "') YIELD node, score with node, score, node.p as match, case when node.name is null then '-' else node.name end as anc_names MATCH (m:DNA_Match{fullname:match}) " + f + " return distinct m.fullname as DNA_Match_name,case when m.RN is null then '-' else toString(m.RN) end as RN,apoc.coll.dropDuplicateNeighbors(apoc.coll.flatten(words)) as found,round(score,2) as score, replace(anc_names,'/','-') as ancestor_list order by score desc,m.fullname";
       cq = "CALL db.index.fulltext.queryNodes('ancestor_surnames_names','" + n + "') YIELD node, score with node, score, node.p as match, case when node.name is null then '-' else node.name end as anc_names MATCH (m:DNA_Match{fullname:match}) " + f + "  match (m:DNA_Match)-[[r:match_by_segment]]-(m2:DNA_Match) where mr.RN is not null with distinct m.fullname as DNA_Match_name,case when m.RN is null then '-' else toString(m.RN) end as RN, apoc.coll.dropDuplicateNeighbors(apoc.coll.flatten(words)) as found,round(score,2) as score,case when m2.RN is not null then '*' + m2.fullname else m2.fullname end as mfn, replace(anc_names,'/','-') as ancestor_list order by mfn with DNA_Match_name,RN,found,score,collect(mfn) as matches,ancestor_list return DNA_Match_name,RN,found,score,matches,ancestor_list order by score desc,DNA_Match_name";
 try{   
     gen.excelLib.queries_to_excel.qry_to_excel(cq, "ancestor_surnames", "matches_with_surnames", 1, "","1:###;2:##.##", "", true,"UDF:\nreturn gen.discovery.ancestral_surnames('" + search_term + "')\n\nCypher query:\n\ncypher query:\n" + cq + "\n\nYou can use wildcards to find variations in the surname, but the trade off is you loose the scoring.\nfor more information on wildcard searches, see: \nhttps://graphaware.com/neo4j/2019/01/11/neo4j-full-text-search-deep-dive.html ", false);
        return "completed\n\n" + cq;
 }
 catch (Exception e) {return "Error. Try modifying your search term\n\nSee for a deeper dive into full text searching.\n https://graphaware.com/neo4j/2019/01/11/neo4j-full-text-search-deep-dive.html\n\n" + n + "\n" + f + "\n\n" + cq + "\n\n" + e.getMessage(); }
    }
}
