/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class ancestor_seg_property {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String phase_segments(

  )
   
         { 
             
        String s = phase();
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String phase() 
    {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        gen.rel.anc_rn arn = new gen.rel.anc_rn();
        Long anc_rn = arn.get_ancestor_rn();

        ////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////
        //remove prior data
        String cq ="match (s:Segment) remove s.phased_anc";
        gen.neo4jlib.neo4j_qry.qry_write(cq);

        cq ="match ()-[r:match_segment]-() remove r.phased_anc";
        gen.neo4jlib.neo4j_qry.qry_write(cq);

        //set up indicies
        try{
            gen.neo4jlib.neo4j_qry.CreateIndex("Segment", "phased_anc");
            gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("match_segment","phased_anc");
        }
        catch(Exception e){}
        
        ////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////
        
        //query identifies subset of segments associated with descendants of the common ancestor
        cq = "MATCH (t:tg)-[[r:tg_seg]]->(s:Segment)  with t,collect(distinct s) as sc optional match (s1:Segment)-[[r2:seg_seq]]-(s2:Segment) where r2.tgid=t.tgid with t,sc,apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect(s1.Indx) + collect(s2.Indx)))) as seq unwind seq as useq with t.tgid as tgid,t.name as name,t.cm as cm, t.chr as chr,t.strt_pos as strt_pos,t.end_pos as end_pos,size(sc) as seg_ct, useq  match (sp:Segment) where sp.Indx = useq with tgid,name,cm,chr,strt_pos,end_pos,seg_ct,id(sp) as id_seg,sp.chr as phased_chr,sp.strt_pos as phased_strt,sp.end_pos as phase_end,sp.Indx as phased_Indx,sp match (m:DNA_Match)-[[rm:match_segment]]->(sp) return tgid,name,cm,chr,strt_pos,end_pos,seg_ct,id_seg, phased_chr,phased_strt, phase_end, phased_Indx,id(rm) as id_phased_rel,rm.cm as phased_cm,rm.snp_ct as phased_snp,m.fullname as phased_fullname,case when m.RN is null then 0 else m.RN end as RN,case when m.kit is null then '-' else m.kit end as kit,case when m.ancestor_rn is null then 0 else m.ancestor_rn end as ancestor_rn,rm.p as r_p,rm.p_rn as r_p_rn, rm.m as r_m,case when rm.m_rn is null then 0 else rm.m_rn end as r_m_rn order by chr,strt_pos,end_pos";
        
        //create Excel report
        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "phased_segs", "anc_tg_detail", 1, "", "0:###;2:###.#;3:##;4:###,###,###;5:###,###,###;6:###;7:#####;8:##;9:###,###,###;10:###,###,###;12:#######;13:###.#;14:#####;16:######;18:######;20:######;22:######;24:######", "", false,"UDF:\nreturn gen.dna.phase_segments()\n\nCypher query:\n" + cq + "\n\nThis reports matches (phased_surname column) who sere descended from the common ancestor.\nThe report shows that there are many ancestor_descendant segments within a triangulation group.\nThis is because there are differing crossover points in different branches of the family tree\nThe report can be used to identify these crossover reprts, who number will be the number of chromosome minue the row count in the report.\n\nThis report is exported as a csv which is used to populate the phase_anc property of the Segment nodes and the match_segment relationship using the id_seg and id_phase_rel identifiers of these elements.", true);
        

        //Add property to Segments
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///phased_segs.csv' as line FIELDTERMINATOR '|' match (s:Segment) where id(s)=toInteger(line.id_seg) set s.phased_anc=" + anc_rn);

        //Add property to match_segment
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///phased_segs.csv' as line FIELDTERMINATOR '|' match (m:DNA_Match)-[r:match_segment]->(s:Segment) where id(r)=toInteger(line.id_phased_rel) set r.phased_anc=" + anc_rn);

        //add additional reports to Excel using the properties just added
        cq="MATCH p=(m:DNA_Match)-[[r:match_segment]]->(s:Segment) where r.phased_anc>0 with m,r, s with s, case when m.ancestor_rn is not null then '*' + m.fullname else m.fullname end + case when m.RN is not null then ' ⦋' + m.RN + '⦌' else '' end as fn,m with s,fn,m order by fn with s,collect(distinct fn) as matches ,collect(distinct m.RN) as rns return s.Indx as seg,size(matches) as ct,matches,gen.rel.mrca_from_cypher_list(rns,15) as mrca order by s.chr,s.strt_pos,s.end_pos";

         gen.excelLib.queries_to_excel.qry_to_excel(cq, "phased_segs2", "all_anc_segs", 2, "", "1:####;2:####", excelFile, false,"UDF:\nreturn gen.dna.phase_segments()\n\nCypher query:\n" + cq + "\n\nThis report lists each ancestor descendant segment with the matches and then the rcas of those matches.\n\nThis report has a constraint: the match_segment phased_anc is not null. This means all the matches are descended from the common ancestor.\n\nThe asterix prefix indicates the match is a descendant of the common ancestor in the analysis.\nThe presence of an RN in brackets indicates the match is in the GEDCOM/family tree.", true);

                 cq = "MATCH p=(m:DNA_Match{ancestor_rn:" + anc_rn + "})-[[r:match_segment]]->(s:Segment)  with m,r, s with s, case when m.ancestor_rn is not null then '*' + m.fullname else m.fullname end + case when m.RN is not null then ' ⦋' + m.RN + '⦌' else '' end as fn,m with s,fn,m order by fn with s,collect(distinct fn) as matches ,collect(distinct m.RN) as rns return s.Indx as seg,size(matches) as ct,matches,gen.rel.mrca_from_cypher_list(rns,15) as mrca order by s.chr,s.strt_pos,s.end_pos";
        
         gen.excelLib.queries_to_excel.qry_to_excel(cq, "phased_segs3", "all_anc_segs2", 3, "", "1:####;2:####", excelFile, false,"UDF:\nreturn gen.dna.phase_segments()\n\nCypher query:\n" + cq + "\n\nThis report differs from the prior worksheet ony in that it does not have the constraint on the match_segment phased_anc property\nThus, additional matches are reported. \nThey are more likely in the line of descent from the common ancestor because the segment is putatively specific for that line.\nThese additional matches are good subjects for further research or, if known, refining the curation of the family tree and GFG curated files.\n\nThe asterix prefix indicates the match is a descendant of the common ancestor in the analysis.\nThe presence of an RN in brackets indicates the match is in the GEDCOM/family tree.", true);
 
         cq ="MATCH p=(t:tg)-[r:tg_seg]->(s:Segment) with t,collect(distinct s) as sc optional match (s1:Segment)-[r2:seg_seq]-(s2:Segment) where r2.tgid=t.tgid RETURN t.tgid as tgid,t.name as name,t.cm as cm, t.chr as chr,t.strt_pos as strt_pos,t.end_pos as end_pos,size(sc) as seg_ct, count(s2) as seg_in_seq order by chr,strt_pos,end_pos";
         gen.excelLib.queries_to_excel.qry_to_excel(cq, "phased_segs4", "phased_ct", 4, "", "0:####;3:####.#;4:##;5:###,###,###;6:###,###,###;7:####;8:####", excelFile, true,"UDF:\nreturn gen.dna.phase_segments()\n\nCypher query:\n" + cq + "\n\nThis report shows the number of segments in the triangulation groups and the very low percent of them associated with descendants of the common ancestor.\nIt is this fact that makes these segments valuable and distinguishable from the 'noise' created by the other segments.", true);
 
         
        return "completed report and anc_rn property updates";
    }
}