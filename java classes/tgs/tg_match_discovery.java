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
  )
   
         { 
             
        get_matches();
         return "";
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_matches()
    {
        String cq="MATCH p=(m:DNA_Match)-[r:match_tg]->(t:tg) where m.RN is null with m,t order by t.tgid with m,collect(distinct t.tgid) as tgs with m,tgs where size(tgs)>3 optional match (m)-[:match_by_segment]->(km:DNA_Match) where km.ancestor_rn>0 with m,tgs,km order by km.fullname with m,tgs,collect(distinct km.fullname + ' ⦋' + km.RN + '⦌') as known_matches,collect(distinct km.RN) as rns with m,tgs,known_matches,rns where size(known_matches)>2 with m.fullname as discovered_match,size(tgs) as ct, tgs,size(known_matches) as match_ct,known_matches,gen.rel.mrca_from_cypher_list(rns,10) as mrca return discovered_match,ct as tg_ct,tgs,match_ct,known_matches,mrca order by ct desc";
        gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project + "_tg_discovery", "discoveries", 1, "", "", "", true, "UDF: return gen.tgs.match_discovery()\n\nquery used\n\n" + cq.replace("[","⦋").replace("]","⦌"),true);
        
        return "completed";
    }
}
