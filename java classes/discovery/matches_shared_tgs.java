/**
 * Copyright 2021-2023 
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
        @Name("min_cm") 
            Long min_cm,
        @Name("max_cm") 
            Long max_cm
 
  )
   
         { 
             
        get_matches(min_cm,max_cm);
         return "";
            }

    
    
    public static void main(String args[]) {
        get_matches(7L,2599L);
    }
    
     public static String get_matches(Long min_cm,Long max_cm) 
    {
        if(gen.neo4jlib.neo4j_info.tg_file.compareTo(" ")!=0){return "";}
            
        String cq ="MATCH p=(m:DNA_Match)-[r:match_tg]->(t:tg) where m.ancestor_rn is not null and " + max_cm + ">r.min_cm>" + min_cm + " with t,m,r, sum(case when m.RN is not null then 1 else 0 end) as mm_ct , case when m.RN is not null then '*' + trim(m.fullname) + ' ⦋' + m.RN + '⦌' else m.fullname end as mm, case when r.m_rn is not null then '*' + trim(r.m) + ' ⦋' + r.m_rn + '⦌' else r.m end as mth with t,apoc.coll.sort(collect(distinct mm)) as probands, collect(distinct mth) as mth2,collect(distinct m.RN) as rns with t,probands,apoc.coll.sort(mth2) as mth3 with t,probands,apoc.coll.dropDuplicateNeighbors(mth3) as mth4 with t,probands,apoc.coll.disjunction(mth4,probands) as matches RETURN t.tgid as tg,t.name as tg_name,t.chr as chr, t.strt_pos as strt_pos,t.end_pos as end_pos,t.cm as cm,size(probands) as ct,probands,size(matches) as ct_matches, apoc.coll.sort(matches) as matches order by chr,strt_pos,end_pos"; 
                //"MATCH p=(m:DNA_Match)-[[r:match_tg]]->(t:tg) where m.RN is not null with t,m, sum(case when m.ancestor_rn is not null then 1 else 0 end) as mm_ct , case when m.ancestor_rn is not null then '*' + m.fullname else m.fullname end + ' ⦋' + m.RN + '⦌' as mm with t,m,mm,mm_ct order by mm with t,sum(mm_ct) as mrca_ct,collect(distinct mm) as matches,collect(distinct m.RN) as rns with t,matches,mrca_ct RETURN t.tgid as tg,t.chr as chr, t.strt_pos as strt_pos,t.end_pos as end_pos,t.cm as cm,size(matches) as ct,mrca_ct,matches order by chr,strt_pos,end_pos";
             gen.excelLib.queries_to_excel.qry_to_excel(cq,"matches sharing tgs", "matches", 1, "", "0:###;1:###;2:###,###,###;3:###,###,###;4:###.#;5:###;6:###", "", true, "UDF: return shared_tgs()\n\n",true);
        return "completed";
    }
}
