/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;

import gen.genlib.current_date_time;
import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class tg_report_multiple_propositi {
    @UserFunction
    @Description("Triangulation group report using all matches sharing the common ancestor. Each propositus-match is listed with their relationship, common ancestor, and segment data. Returns an Excel output file.")

    public String tg_report(
        @Name("tgid") 
            Long tgid
 
  )
   
         { 
             
         gen.rel.anc_rn anc = new gen.rel.anc_rn();
        Long common_ancestor = anc.get_ancestor_rn();
        
        tg_report(tgid, common_ancestor);
        return "completed";
        
           }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
      public static void tg_report(Long tgid,  Long common_ancestor) {
       gen.neo4jlib.neo4j_info.neo4j_var();  //initialize variables
       gen.conn.connTest.cstatus();
       String cq = "match p=(m:DNA_Match)-[rm:match_segment]-(s:Segment)-[rs:seg_seq{tgid:" + tgid + "}]-() where m.ancestor_rn is not null and rm.p_anc_rn is not null and rm.m_anc_rn is not null and 200>=rm.cm>=7 and rm.snp_ct>=500 with s,m.fullname + ' ⦋' + m.RN + '⦌' as propositus, rm.m + ' ⦋' + rm.m_rn + '⦌' as match, rm.cm as cm, rm.snp_ct as snp ,m,rm with m,rm,s,cm, snp, case when m.RN<rm.m_rn then propositus + ', ' + match else match + ', ' + propositus end as propositus_match, case when m.RN<rm.m_rn then 'm' else 'p' end as source with s,propositus_match,source,cm,snp, gen.rel.relationship_from_RNs(m.RN,rm.m_rn) as relationship,gen.rel.mrca_phased(m.RN,rm.m_rn) as mrca order by source desc with propositus_match,relationship,mrca as phased_mrca,s.chr as chr,s.strt_pos as start,s.end_pos as end, cm, snp,collect(distinct source) as pm return propositus_match, relationship,phased_mrca,chr,start,end,cm, snp, pm as source order by propositus_match,start,end";    
       
       String SaveFileNm = "";
          SaveFileNm = "tg_report_multiple_propositi"  + current_date_time.getDateTime() + "_seg_" + gen.tgs.tg_label.getTgLabel(tgid) +  "_ca_" + common_ancestor + "_tg_" + tgid  ;
          
     
     String e = gen.excelLib.queries_to_excel.qry_to_excel(cq,SaveFileNm,"match_ahnentafel_" , 1, "2:25;3:25", "3:##;4:#,###,###;5:#,###,###;6:###;7:###;8:###", "", true,"common ancestor is " + gen.gedcom.get_family_tree_data.getPersonFromRN(common_ancestor,true) + "\n\nUse this query in the Neo4j browser to visualize the results\nmatch p=(m:DNA_Match)-⦋rm:match_segment⦌-(s:Segment)-⦋rs:seg_seq{tgid:" + tgid +"}⦌-() where m.ancestor_rn is not null and rm.p_anc_rn is not null and rm.m_anc_rn is not null and rm.cm>=7 and rm.snp_ct>=500 and s.mbp<200 return p\n\nThis query returns the above plus links to the family tree\nmatch p=(m:DNA_Match)-⦋rm:match_segment⦌-(s:Segment)-⦋rs:seg_seq{tgid:" + tgid +"}⦌-() where m.ancestor_rn is not null and rm.p_anc_rn is not null and rm.m_anc_rn is not null and rm.cm>=7 and rm.snp_ct>=500 and s.mbp<200 with p,m match q=(m)-⦋:Gedcom_DNA⦌-(i:Person)-⦋:father|mother*0..10⦌->(a:Person) where i.RN=m.RN and a.RN=m.ancestor_rn return p,q" ); 
       
    }
}