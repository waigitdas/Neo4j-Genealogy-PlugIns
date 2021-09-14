/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.tgs;

import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 *
 * @author david
 */
public class tg_report_set {
        @UserFunction
        @Description("Loads FTDNA DNA result CSV files from a named directory and specifically structured subdirectories. File names must NOT be altered after downloaded.")

public String tg_report(
        @Name("tgif") 
            Long tgid,
         @Name("propsitus") 
            Long propositus,
        @Name("common_ancestor") 
            Long common_ancestor
       
  )
    {
        
        { 
        tg_report1(tgid, propositus, common_ancestor);
        return "Completed";
        }
     }
   
   
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        tg_report1(Long.valueOf(109), Long.valueOf(1), Long.valueOf(11));
    }
    
    public static void tg_report1(Long tgid, Long propositus, Long common_ancestor) {
        gen.neo4jlib.neo4j_info.neo4j_var();  //initialize variables
        //String cq = "match (t:tg{tgid:" + tgid + "}) with t match (s:Segment) where " + gen.neo4jlib.neo4j_info.tg_logic + " with distinct 0 as tgpath,t,s,s.Indx as Indx,s.chr as c,s.strt_pos as st,s.end_pos as e match (m1:DNA_Match)-[r:match_segment]-(sm:Segment) where sm.Indx=Indx and m1.RN is not null with distinct tgpath,m1,c,min(st) as strt,max(e) as e,m1.fullname + ' [' + m1.RN +']' as match,max(s.snp) as snps, min(s.snp) as min_snp, max(s.cm) as max_cm,min(s.cm) as min_cm match path=(p1:Person)-[rp1:father|mother*1..15]->(mrca:Person)<-[rp2:father|mother*1..15]-(p2:Person) where p1.RN=" + propositus + " and p2.RN=m1.RN with c,strt,e,match,snps,min_snp,max_cm,min_cm,collect(mrca.fullname + ' [' + mrca.RN +']') as mrca,tgpath,path,rp1,rp2, reduce(srt2 ='', q IN nodes(path)|srt2 + case when q.sex='M' then 0 else 1 end ) AS SO WITH c,strt,e,match,snps,min_snp,max_cm,min_cm,mrca ,SO,tgpath,path,rp1,rp2, reduce(srt ='', a IN nodes(path)|srt + a.fullname + ' [' + a.RN + ']\u005E' ) AS ancestors with c,strt,e,match,snps,min_snp,max_cm,min_cm,mrca,tgpath,path,rp1,rp2,'1' + substring(SO,1,size(rp1)) as SO,ancestors , reduce(geo ='', b IN nodes(path)|geo + '*' + replace(b.BP,',',';') + ' : \u2670' + replace(b.DP,',',';') + ' \u005E ' ) AS places with c,strt,e,match,snps,min_snp,max_cm,min_cm,mrca,tgpath,path,rp1,rp2,'1' + substring(SO,1,size(rp1)) as SO,ancestors,places return distinct '" + gen.gedcom.get_family_tree_data.getPersonFromRN(propositus) + "' as propositus, c as chr,strt,e as end,match,snps as max_snp,min_snp,max_cm,min_cm, mrca,size(rp1) + 1 as gen,size(rp2) + 1 as gen2, SO as Ahnentafel,ancestors,places";
       //String cq = "match (t:tg{tgid:" + tgid + "}) with t match (s:Segment) where " + gen.neo4jlib.neo4j_info.tg_logic + " with distinct 0 as tgpath,t,s,s.Indx as Indx,s.chr as c,s.strt_pos as st,s.end_pos as e match (m1:DNA_Match)-[r:match_segment]-(sm:Segment) where sm.Indx=Indx and m1.RN is not null with distinct tgpath,m1,c,min(st) as strt,max(e) as e,m1.fullname + ' [' + m1.RN +']' as match,max(s.snp) as snps, min(s.snp) as min_snp, max(s.cm) as max_cm,min(s.cm) as min_cm match path=(p1:Person)-[rp1:father|mother*1..15]->(mrca:Person)<-[rp2:father|mother*1..15]-(p2:Person) where p1.RN=" + propositus + " and p2.RN=m1.RN with c,strt,e,match,snps,min_snp,max_cm,min_cm,mrca.fullname + ' [' + mrca.RN +']' as mrca,tgpath,path,rp1,rp2, reduce(srt2 ='', q IN nodes(path)|srt2 + case when q.sex='M' then 0 else 1 end ) AS SO WITH c,strt,e,match,snps,min_snp,max_cm,min_cm,mrca ,SO,tgpath,path,rp1,rp2, reduce(srt ='', a IN nodes(path)|srt + a.fullname + ' [' + a.RN + ']\u005E' ) AS ancestors with c,strt,e,match,snps,min_snp,max_cm,min_cm,mrca,tgpath,path,rp1,rp2,'1' + substring(SO,1,size(rp1)) as SO,ancestors , reduce(geo ='', b IN nodes(path)|geo + '*' + replace(b.BP,',',';') + ' : \u2670' + replace(b.DP,',',';') + ' \u005E ' ) AS places with c,strt,e,match,snps,min_snp,max_cm,min_cm,mrca,tgpath,path,rp1,rp2,'1' + substring(SO,1,size(rp1)) as SO,ancestors,places return distinct '" + gen.gedcom.get_family_tree_data.getPersonFromRN(propositus) + "' as propositus, c as chr,strt,e as end,match,snps as max_snp,min_snp,max_cm,min_cm, mrca,gen.rel.relationship_from_path(size(rp1) + 1,size(rp2) + 1) as czn, gen.rel.ahn_path(SO) as Ahnentafel,ancestors,places";
       String cq ="match (t:tg{tgid:" + tgid + "}) with t match (s:Segment) where " + gen.neo4jlib.neo4j_info.tg_logic + " with distinct 0 as tgpath,t,s,s.Indx as Indx,s.chr as c,s.strt_pos as st,s.end_pos as e optional match (m1:DNA_Match)-[r:match_segment]-(sm:Segment) where sm.Indx=Indx and m1.RN is not null with distinct tgpath,m1,c,min(st) as strt,max(e) as e,m1.fullname + ' [' + m1.RN +']' as match,max(s.snp) as snps, min(s.snp) as min_snp, max(s.cm) as max_cm,min(s.cm) as min_cm match path=(p1:Person)-[rp1:father|mother*1..15]->(mrca:Person)<-[rp2:father|mother*1..15]-(p2:Person) where p1.RN=" + propositus + " and p2.RN=m1.RN with c,strt,e,match,snps,min_snp,max_cm,min_cm,mrca,tgpath,path,rp1,rp2, reduce(srt2 ='', q IN nodes(path)|srt2 + case when q.sex='M' then 0 else 1 end ) as SO order by size(SO),match,size(SO),mrca.sex desc WITH gen.gedcom.person_from_rn(mrca.RN) as mrca,c,strt,e,match,snps,min_snp,max_cm,min_cm,SO,tgpath,path,rp1,rp2, reduce(srt ='', a IN nodes(path)|srt + a.fullname + ' [' + a.RN + ']^' ) AS ancestors with c,strt,e,match,snps,min_snp,max_cm,min_cm,mrca,tgpath,path,rp1,rp2,'1' + substring(SO,1,size(rp1)) as SO,ancestors , reduce(geo ='', b IN nodes(path)|geo + '*' + replace(b.BP,',',';') + ' : ?' + replace(b.DP,',',';') + ' ^ ' ) AS places with c,strt,e,match,snps,min_snp,max_cm,min_cm,mrca,tgpath,path,rp1,rp2,'1' + substring(SO,1,size(rp1)) as SO,ancestors,places with distinct gen.gedcom.person_from_rn(" + propositus + ") as propositus, c as chr,strt,e as end,match,snps as max_snp,min_snp,max_cm,min_cm, mrca,gen.rel.ahn_path(SO) as Ahnentafel,ancestors,places, gen.rel.relationship_from_path(size(rp1),size(rp2),2) as czn with propositus,chr,strt,end,match,min_snp, max_snp,min_cm,max_cm,Ahnentafel,czn,ancestors,places,mrca return propositus,chr,strt,end,match,mrca,min_snp, max_snp,min_cm,max_cm,czn, Ahnentafel,ancestors,places";
       
     System.out.println(cq);
       String e = gen.excelLib.queries_to_excel.qry_to_excel(cq,"tg_report","match_ahnentafel_" , 1, "", "", "", true) ; 
       
    }
}
