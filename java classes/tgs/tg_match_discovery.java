/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class tg_match_discovery {
    @UserFunction
    @Description("Triangulation group patterns are used to discovery matches of interest not currently in the project. These may be matches on collaral branches or in the branch to the common ancestor.")

    public String match_discovery(
            @Name("tg_min_ct")
                Long tg_min_ct,
            @Name("match_min_ct")
                Long match_min_ct,
            @Name("include_mrca")
                Boolean include_mrca
    )
   
         { 
             
        String s = get_matches(tg_min_ct, match_min_ct, include_mrca);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_matches(Long tg_min_ct, Long match_min_ct, Boolean include_mrca)
    {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        gen.neo4jlib.neo4j_info.neo4j_var();
        
        String cq="";
        if (include_mrca==true){
        cq="MATCH p=(m:DNA_Match)-[[r:match_tg]]->(t:tg{project:'" + gen.neo4jlib.neo4j_info.project + "'}) where m.RN is null with m,t order by t.tgid with m,collect(distinct t.tgid) as tgs with m,tgs where size(tgs)>=" + tg_min_ct + " optional match (m)-[[:match_by_segment]]->(km:DNA_Match) where km.ancestor_rn>0 with m,tgs,km order by km.fullname with m,tgs,collect(distinct km.fullname + ' ⦋' + km.RN + '⦌') as known_matches,collect(distinct km.RN) as rns with m,tgs,known_matches,rns where size(known_matches)>=" + match_min_ct + " with case when m.RN is not null then m.fullname + ' ⦋' + m.RN + '⦌' else m.fullname end as discovered_match,size(tgs) as ct, tgs,size(known_matches) as match_ct,known_matches,gen.rel.mrca_from_cypher_list(rns,10) as mrca,rns return discovered_match,ct as tg_ct,tgs,case when size(mrca)=0 then '     none: ' + rns else mrca end as suggested_mrcas, match_ct,known_matches order by ct desc";
        gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project + "_tg_discovery", "discoveries", 1, "", "1:###;4:###", "", true, "UDF: \nreturn gen.tgs.match_discovery(" + tg_min_ct + ", " + match_min_ct + ", " + include_mrca + ")\n\nquery used\n\n" + cq.replace("[","⦋").replace("]","⦌") + "\n\nThis report finds patterns of triangulation groups for known DNA tester and then new matches who fit that pattern. \nBecause there are multiple TGs, the likelihood that the new matches are in the same family branch is increased.\nThese new matches are candidates for further study.\n\nThe default look back for MRCAs is 10 generations. Longer look backs make the query time out.\nSince the query includes the default common ancestor, that ancestor may be missed if too distantly related.\nYou can use the RN list to check for MRCAs using this UDF:\nreturn gen.rel.mrca_from_list(('{rns}',{gen})",true);
        
        }
        else {
                   cq="MATCH p=(m:DNA_Match)-[[r:match_tg]]->(t:tg{project:'" + gen.neo4jlib.neo4j_info.project + "'}) where m.RN is null with m,t order by t.tgid with m,collect(distinct t.tgid) as tgs with m,tgs where size(tgs)>=" + tg_min_ct + " optional match (m)-[[:match_by_segment]]->(km:DNA_Match) where km.ancestor_rn>0 with m,tgs,km order by km.fullname with m,tgs,collect(distinct km.fullname + ' ⦋' + km.RN + '⦌') as known_matches,collect(distinct km.RN) as rns with m,tgs,known_matches,rns where size(known_matches)>=" + match_min_ct + " with case when m.RN is not null then m.fullname + ' ⦋' + m.RN + '⦌' else m.fullname end as discovered_match,size(tgs) as ct, tgs,size(known_matches) as match_ct,known_matches,rns return discovered_match,ct as tg_ct,tgs,match_ct,known_matches,rns as RNs_for_finding_MRCA order by ct desc";
        gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project + "_tg_discovery", "discoveries", 1, "", "1:###;3:###", "", true, "UDF: \nreturn gen.tgs.match_discovery(" + tg_min_ct + ", " + match_min_ct + ", " + include_mrca + ")\n\nquery used\n\n" + cq.replace("[","⦋").replace("]","⦌") + "\n\nThis report finds patterns of triangulation groups for known DNA tester and then new matches who fit that pattern. \nBecause there are multiple TGs, the likelihood that the new matches are in the same family branch is increased.\nThese new matches are candidates for further study.\n\nYou can use the RN list to check for MRCAs using this UDF:\nreturn gen.rel.mrca_from_list(('{rns}',{gen})",true);
        
   
        }
        return "completed";
    }
}
