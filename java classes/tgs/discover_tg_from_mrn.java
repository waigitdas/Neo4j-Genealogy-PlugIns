/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

public class discover_tg_from_mrn {
    @UserFunction
    @Description("Uses scenario where match_segment m_rn is not null to discover possible triagnulation groups")
    

    public String discover_TGs_from_match_segment_match_rn (
        )
   
         { 
             
        String s = get_tgs();
         return s;
            }

    
    
    
     public String get_tgs() 
    {
        String cq ="MATCH p=(m:DNA_Match)-[r:match_segment]->(s:Segment) where r.m_rn >0 with s,s.chr as chr,s.strt_pos as strt_pos,s.end_pos as end_pos,r.p as propositus,r.m as match,r.p_anc_rn as p_anc,r.m_anc_rn as m_anc,r.cm as cm,count(*) as ct with chr,strt_pos,end_pos,propositus,match, ct,p_anc,m_anc, gen.tgs.segment_tgs(s.Indx,'Teves') as t with t,chr,strt_pos,end_pos,case when t =0 then '‑' else toString(t) end as tg,propositus,case when  p_anc is null then '-' else toString(p_anc) end as  p_anc,match,case when m_anc is null then '-' else toString(m_anc) end as m_anc, ct return t,chr,strt_pos,end_pos,tg,propositus,max(p_anc) as p_anc,match,max(m_anc) as m_anc, sum(ct) as ct order by t, chr,strt_pos,end_pos";
                //"MATCH p=(m:DNA_Match)-[[r:match_segment]]->(s:Segment) where r.m_rn >0 with s,s.chr as chr,s.strt_pos as strt_pos,s.end_pos as end_pos,r.p as propositus,r.m as match,r.p_anc_rn as p_anc,r.m_anc_rn as m_anc,r.cm as cm,count(*) as ct with chr,strt_pos,end_pos,propositus,match, ct,p_anc,m_anc, gen.tgs.get_segment_tg(s.chr,s.strt_pos,s.end_pos) as t return chr,strt_pos,end_pos,case when t =0 then '-' else toString(t) end as tg,propositus,case when  p_anc is null then '-' else toString(p_anc) end as  p_anc,match,case when m_anc is null then '-' else toString(m_anc) end as m_anc, ct order by t, chr,strt_pos,end_pos";
        
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "discover_tgs", "results", 1, "", "0:##;1:###,###,###;2:###,###,###;3:####;5:####;7:###;8:###", "", true,"UDF:\nreturn gen.tgs.discover_triangulation_groups_from_match_segment_mrn()\n\ncypher query:\n" + cq + "\n\nThis report finds the scenario where a lit is both a propositus and a match, which indicates there is recipricol matching compatable with a triangulation at a segment.\n\nIt can also discover matches who might join the project and thereby ccontribute their DNA results.", true);
        return "completed";
    }
}
