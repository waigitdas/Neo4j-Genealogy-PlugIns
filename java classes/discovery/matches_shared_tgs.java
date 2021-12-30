/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.discovery;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class matches_shared_tgs {
    @UserFunction
    @Description("Matches are identified by triangulation group. Matches with ancestor_rn have an '*' prefix and sort at the beginning of the list of matches at the triangulation group. Only known matches with are returned.")

    public String shared_tgs(

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
        String cq = "MATCH p=(m:DNA_Match)-[[r:match_tg]]->(t:tg) where m.RN is not null with t,m, sum(case when m.ancestor_rn is not null then 1 else 0 end) as mm_ct , case when m.ancestor_rn is not null then '*' + m.fullname else m.fullname end + ' ⦋' + m.RN + '⦌' as mm with t,m,mm,mm_ct order by mm with t,sum(mm_ct) as mrca_ct,collect(distinct mm) as matches,collect(distinct m.RN) as rns with t,matches,mrca_ct RETURN t.tgid as tg,t.chr as chr, t.strt_pos as strt_pos,t.end_pos as end_pos,t.cm as cm,size(matches) as ct,mrca_ct,matches order by chr,strt_pos,end_pos";
             gen.excelLib.queries_to_excel.qry_to_excel(cq,"matches sharing tgs", "matches", 1, "", "0:###;1:###;2:###,###,###;3:###,###,###;4:###.#;5:###;6:###", "", true, "UDF: return shared_tgs()\n\n",true);
        return "completed";
    }
}
