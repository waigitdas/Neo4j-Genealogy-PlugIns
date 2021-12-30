/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.tgs;

import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
import gen.genlib.current_date_time;
import gen.tgs.tg_label;
/**
 *
 * @author david
 */
public class tg_report_strict {
        @UserFunction
        @Description("Propositus and their match at the triangulation group, limited to those with specified common ancestor. Report includes segment data, ahnentafel path from a propositus to the common ancestor. Variables are:\r\nTriangulation group ID\r\nPropositus: the person whose ahnentafel path is reported\r\nanonymized: true or false.")

public String tg_report_exclusive(
        @Name("tgid") 
            Long tgid,
         @Name("propsitus") 
            Long propositus,
//        @Name("common_ancestor") 
//           Long common_ancestor,
        @Name("anonymized") 
            Boolean anonymized
       
  )
    {
        
        { 
            
        gen.rel.anc_rn anc = new gen.rel.anc_rn();
        Long common_ancestor = anc.get_ancestor_rn();
        
        tg_report1(tgid, propositus, common_ancestor, anonymized);
        return "Completed";
        }
     }
      
    
    public static void main(String args[]) {
        tg_report1(Long.valueOf(96), Long.valueOf(1), Long.valueOf(33454),false);
    }
    
    public static void tg_report1(Long tgid, Long propositus, Long common_ancestor,Boolean anonymized) {
       gen.neo4jlib.neo4j_info.neo4j_var();  //initialize variables
       gen.conn.connTest.cstatus();
       String cq = "";
       
       if (common_ancestor == 0 && anonymized == false) {
          cq ="match (t:tg{tgid:" + tgid + "}) with t match (s:Segment) where " + gen.neo4jlib.neo4j_info.tg_logic_overlap + " with distinct 0 as tgpath,t,s,s.Indx as Indx,s.chr as c,s.strt_pos as st,s.end_pos as e optional match (m1:DNA_Match)-[r:match_segment]-(sm:Segment) where sm.Indx=Indx and m1.RN is not null and r.cm>=7 and r.snp_ct>=500 with distinct tgpath,m1,c,min(st) as strt,max(e) as e,m1.fullname + ' \u298B' + m1.RN +'\u298C' as match,m1.RN as mrn,collect(distinct r.rel) as czn,max(r.snp_ct) as snps, min(r.snp_ct) as min_snp, max(r.cm) as max_cm,min(r.cm) as min_cm match path=(p1:Person)-[rp1:father|mother*1..15]->(mrca:Person)<-[rp2:father|mother*1..15]-(p2:Person) where p1.RN=" + propositus + " and p2.RN=m1.RN with mrn,czn,c,strt,e,match,snps,min_snp,max_cm,min_cm,mrca,tgpath,path,rp1,rp2, reduce(srt2 ='', q IN nodes(path)|srt2 + case when q.sex='M' then 0 else 1 end ) as SO order by size(SO),match,size(SO),mrca.sex desc WITH mrn,czn,gen.gedcom.person_from_rn(mrca.RN,true) as mrca,c,strt,e,match,snps,min_snp,max_cm,min_cm,SO,tgpath,path,rp1,rp2, reduce(srt ='', a IN nodes(path)|srt + a.fullname + gen.neo4jlib.RNwithBrackets(a.RN) + '^' ) AS ancestors with mrn,czn,c,strt,e,match,snps,min_snp,max_cm,min_cm,mrca,tgpath,path,rp1,rp2,'1' + substring(SO,1,size(rp1)) as SO,ancestors , reduce(geo ='', b IN nodes(path)|geo + '*' + replace(b.BP,',',';') + ' : ?' + replace(b.DP,',',';') + ' ^ ' ) AS places with mrn,czn, c,strt,e,match,snps,min_snp,max_cm,min_cm,mrca,tgpath,path,rp1,rp2,'1' + substring(SO,1,size(rp1)) as SO,ancestors,places with distinct mrn,czn, gen.gedcom.person_from_rn(" + propositus + ",true) as propositus, c as chr,strt,e as end,match,snps as max_snp,min_snp,max_cm,min_cm, mrca,gen.rel.ahn_path(SO) as Ahnentafel,ancestors,places, czn with propositus,chr,strt,end,match,min_snp, max_snp,min_cm,max_cm,Ahnentafel,czn,ancestors,places,mrca return propositus,chr,strt,end,match,mrca,min_snp, max_snp,min_cm,max_cm,czn, Ahnentafel,ancestors,places";
       }
       
       else if (common_ancestor > 0 && anonymized == true) {
       cq = "match (t:tg{tgid:" + tgid + "}) with t match (s:Segment) where  " + gen.neo4jlib.neo4j_info.tg_logic_overlap + "  with distinct 0 as tgpath,t,s,s.Indx as Indx,s.chr as c,s.strt_pos as st,s.end_pos as e optional match (m1:DNA_Match)-[r:match_segment]-(sm:Segment) where sm.Indx=Indx and m1.ancestor_rn=" +   common_ancestor + " and r.cm>=7 and r.snp_ct>=500 and r.m_rn=" + propositus + " with distinct tgpath,m1,c,min(st) as strt,max(e) as e, m1.RN as match,max(r.snp_ct) as snps, min(r.snp_ct) as min_snp, max(r.cm) as max_cm,min(r.cm) as min_cm,collect(distinct r.rel) as czn match path=(p1:Person)-[rp1:father|mother*1..15]->(mrca:Person)<-[rp2:father|mother*1..15]-(p2:Person) where p1.RN=" + propositus + " and p2.RN=m1.RN and p1.ancestor_rn=" + common_ancestor + " and p2.ancestor_rn=" + common_ancestor + " and mrca.ancestor_rn=" + common_ancestor + " with c,strt,e,match,snps,min_snp,max_cm,min_cm,czn,mrca,tgpath,path,rp1,rp2, reduce(srt2 ='', q IN nodes(path)|srt2 + case when q.sex='M' then 0 else 1 end ) as SO order by size(SO),match,size(SO),mrca.sex desc WITH gen.gedcom.person_from_rn(mrca.RN,true) as mrca,c,strt,e,match,snps,min_snp,max_cm,min_cm,czn,SO,tgpath,path,rp1,rp2, reduce(srt ='', a IN nodes(path)|srt  + gen.neo4jlib.RNwithBrackets(a.RN) + '^' ) AS ancestors with c,strt,e,match,snps,min_snp,max_cm,min_cm,czn,mrca,tgpath,path,rp1,rp2,'1' + substring(SO,1,size(rp1)) as SO,ancestors , reduce(geo ='', b IN nodes(path)|geo + '*' + replace(b.BP,',',';') + ' : ?' + replace(b.DP,',',';') + ' ^ ' ) AS places with c,strt,e,match,snps,min_snp,max_cm,min_cm,czn,mrca,tgpath,path,rp1,rp2,'1' + substring(SO,1,size(rp1)) as SO,ancestors,places with distinct gen.gedcom.person_from_rn(" + propositus + ",true ) as propositus, c as chr,strt,e as end,match,snps as max_snp,min_snp,max_cm,min_cm,czn, mrca,gen.rel.ahn_path(SO) as Ahnentafel,ancestors,places  with propositus,chr,strt,end,match,min_snp, max_snp,min_cm,max_cm,Ahnentafel,czn,ancestors,places,mrca return propositus,chr,strt,end,match,mrca as phased_mrca,min_snp, max_snp,min_cm,max_cm,czn, Ahnentafel,ancestors,places";
       }
       
         else if (common_ancestor > 0 && anonymized == false) {
       
             cq ="match (t:tg{tgid:" + tgid + "}) with t match (s:Segment) where " + gen.neo4jlib.neo4j_info.tg_logic_overlap + " with distinct 0 as tgpath,t,s,s.Indx as Indx,s.chr as c,s.strt_pos as st,s.end_pos as e optional match (m1:DNA_Match)-[r:match_segment]-(sm:Segment) where sm.Indx=Indx and m1.ancestor_rn=" + common_ancestor + " and r.cm>=7 and r.snp_ct>=500 and r.m_rn=" + propositus + " with distinct tgpath,m1,c,min(st) as strt,max(e) as e,m1.fullname + ' \u298B' + m1.RN +'\u298C' as match,m1.RN as mrn,collect(distinct r.rel) as czn,max(r.snp_ct) as snps, min(r.snp_ct) as min_snp, max(r.cm) as max_cm,min(r.cm) as min_cm match path=(p1:Person)-[rp1:father|mother*1..15]->(mrca:Person)<-[rp2:father|mother*1..15]-(p2:Person) where p1.RN=" + propositus + " and p2.RN=m1.RN and p1.ancestor_rn=" + common_ancestor + " and p2.ancestor_rn=" + common_ancestor + " and mrca.ancestor_rn=" + common_ancestor + " with mrn,czn,c,strt,e,match,snps,min_snp,max_cm,min_cm,mrca,tgpath,path,rp1,rp2, reduce(srt2 ='', q IN nodes(path)|srt2 + case when q.sex='M' then 0 else 1 end ) as SO order by size(SO),match,size(SO),mrca.sex desc WITH mrn,czn,gen.gedcom.person_from_rn(mrca.RN,true) as mrca,c,strt,e,match,snps,min_snp,max_cm,min_cm,SO,tgpath,path,rp1,rp2, reduce(srt ='', a IN nodes(path)|srt + a.fullname + gen.neo4jlib.RNwithBrackets(a.RN) + '^' ) AS ancestors with mrn,czn,c,strt,e,match,snps,min_snp,max_cm,min_cm,mrca,tgpath,path,rp1,rp2,'1' + substring(SO,1,size(rp1)) as SO,ancestors , reduce(geo ='', b IN nodes(path)|geo + '*' + replace(b.BP,',',';') + ' : ?' + replace(b.DP,',',';') + ' ^ ' ) AS places with mrn,czn,c,strt,e,match,snps,min_snp,max_cm,min_cm,mrca,tgpath,path,rp1,rp2,'1' + substring(SO,1,size(rp1)) as SO,ancestors,places with distinct gen.gedcom.person_from_rn(" + propositus + ",true) as propositus, c as chr,strt,e as end,match,snps as max_snp,min_snp,max_cm,min_cm, mrca,gen.rel.ahn_path(SO) as Ahnentafel,ancestors,places,czn with propositus,chr,strt,end,match,min_snp, max_snp,min_cm,max_cm,Ahnentafel,czn,ancestors,places,mrca return propositus,chr,strt,end,match,mrca as phased_mrca,min_snp, max_snp,min_cm,max_cm,czn, Ahnentafel,ancestors,places";
       
           
       }
       
         else if (common_ancestor == 0 && anonymized == true) {
            cq ="match (t:tg{tgid:" + tgid + "}) with t match (s:Segment) where  " + gen.neo4jlib.neo4j_info.tg_logic_overlap + "  with distinct 0 as tgpath,t,s,s.Indx as Indx,s.chr as c,s.strt_pos as st,s.end_pos as e optional match (m1:DNA_Match)-[r:match_segment]-(sm:Segment) where sm.Indx=Indx  and m1.ancestor_rn=" + common_ancestor + " and r.cm>=7 and r.snp_ct>=500 with distinct tgpath,m1,c,min(st) as strt,max(e) as e, m1.RN as match,max(r.snp_ct) as snps, min(r.snp_ct) as min_snp, max(r.cm) as max_cm,min(r.cm) as min_cm,collect(distinct r.rel) as czn match path=(p1:Person)-[rp1:father|mother*1..15]->(mrca:Person)<-[rp2:father|mother*1..15]-(p2:Person) where  where p1.RN=" + propositus + " and p2.RN=m1.RN and p1.ancestor_rn=" + common_ancestor + " and p2.ancestor_rn=" + common_ancestor + " and mrca.ancestor_rn=" + common_ancestor + " with c,strt,e,match,snps,min_snp,max_cm,min_cm,czn,mrca,tgpath,path,rp1,rp2, reduce(srt2 ='', q IN nodes(path)|srt2 + case when q.sex='M' then 0 else 1 end ) as SO order by size(SO),match,size(SO),mrca.sex desc WITH gen.gedcom.person_from_rn(mrca.RN,true) as mrca,c,strt,e,match,snps,min_snp,max_cm,min_cm,czn,SO,tgpath,path,rp1,rp2, reduce(srt ='', a IN nodes(path)|srt  + gen.neo4jlib.RNwithBrackets(a.RN) + '^' ) AS ancestors with c,strt,e,match,snps,min_snp,max_cm,min_cm,czn,mrca,tgpath,path,rp1,rp2,'1' + substring(SO,1,size(rp1)) as SO,ancestors , reduce(geo ='', b IN nodes(path)|geo + '*' + replace(b.BP,',',';') + ' : ?' + replace(b.DP,',',';') + ' ^ ' ) AS places with c,strt,e,match,snps,min_snp,max_cm,min_cm,czn,mrca,tgpath,path,rp1,rp2,'1' + substring(SO,1,size(rp1)) as SO,ancestors,places with distinct gen.gedcom.person_from_rn(" + propositus + ",true ) as propositus, c as chr,strt,e as end,match,snps as max_snp,min_snp,max_cm,min_cm,czn, mrca,gen.rel.ahn_path(SO) as Ahnentafel,ancestors,places with propositus,chr,strt,end,match,min_snp, max_snp,min_cm,max_cm,Ahnentafel,czn,ancestors,places,mrca return propositus,chr,strt,end,match,mrca,min_snp, max_snp,min_cm,max_cm,czn, Ahnentafel,ancestors,places"   ;
       };
       
       
      
     //System.out.println(cq);
     String SaveFileNm = "";
     if (anonymized==false) {
     SaveFileNm = "tg_report_"  + current_date_time.getDateTime() + "_seg_" + gen.tgs.tg_label.getTgLabel(tgid) + "_propositus_" + propositus + "_" + "_ca_" + common_ancestor + "_tg_" + tgid  ;
     }
     else {
     SaveFileNm = "tg_report_"  + current_date_time.getDateTime() + "_seg_" + gen.tgs.tg_label.getTgLabel(tgid) + "_propositus_" + propositus + "_" + "_ca_" + common_ancestor + "_tg_" + tgid + "_anonymized"  ;
     
     }

     String e = gen.excelLib.queries_to_excel.qry_to_excel(cq,SaveFileNm,"match_ahnentafel_" , 1, "2:25;3:25", "1:##;2:#,###,###;3:#,###,###;6:###;7:###;8:###.0;9:###.0", "", true,"UDF: return gen.tgs.tg_report_exclusive(" + tgid + ","+ propositus + "," + anonymized + ")\n\ncommon ancestor is " + gen.gedcom.get_family_tree_data.getPersonFromRN(common_ancestor,true),false) ; 
       
    }
}


