/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.endogamy;

import gen.neo4jlib.neo4j_qry;
import java.text.DecimalFormat;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

public class endogamy_individual {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String endogamy_panel(
        @Name("propositus_rn") 
            Long propositus_rn
  )
         { 
             
        String s = create_panel(propositus_rn);
         return s;
            }
    
    public static void main(String args[]) {
        create_panel(32L);
    }
    
     public static String create_panel(Long rn) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        int ct = 1;

        //coefficient of inbreeding function
        gen.endogamy.coi qcoi = new gen.endogamy.coi();

        //family tree
        String cq = "match p=(n:Person{RN:" + rn + "})-[[r:father|mother*0..99]]->(x) with x,x.fullname + ' ⦋' + x.RN + '⦌ (' + left(x.BD,4) + '-' + left(x.DD,4) + ')' as Name, length(p) as gen, [[z in nodes(p)|z.RN]] as op, '1' + reduce(srt ='', q IN nodes(p)|srt + case when q.sex='M' then '0' else '1' end ) AS Anh with x,Name,gen,'1' + right(Anh,size(Anh)-2) as Ahnen, gen.graph.get_ordpath(op) as op optional match (d:Person{RN:x.RN}) return apoc.text.lpad('',(gen)*5,'.') + Name as Person,gen,gen.rel.ahnentafel(Ahnen) as Ahnentafel,case when d.iYHG is null then '~' else d.iYHG end as inferred_YHG,case when d.imtHG is null then '~' else d.imtHG end as inferred_mtHG order by Ahnentafel";
        String  cqq = cq.replace("[[","[").replace("]]","]");
         String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_package_" + rn, "family_tree", ct, "", "1:#######;2:###,###,###,###", "", false,"UDF: \nreturn gen.rel.endogamy_panel(" + rn + ")\n\nCypher query:\n" + cq, false);
        ct = ct + 1;
        int fam_member_ct =gen.excelLib.queries_to_excel.rwCt;
        
        //pedigree completeness
        cq = "match p=(n:Person{RN:" + rn + "})-[[:father|mother*..99]]->(x) with reduce(n=0,q in nodes(p)|n+1) as gen RETURN gen, count(*) as Observed,2^(gen-1) as Expected,count(*)/(2^(gen-1)) as Ratio order by gen";
         excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_package2", "pedigree_completeness", ct, "", "0:#####;1:###,###,###;2:######;3:##.############",excelFile, false, "cypher query:\n" + cq, false);
        ct = ct + 1;
 
         //duplicate unions
         cq = "with " + rn + " as urn MATCH p=(u1:Union)-[r:union_parent*0..99]->(u2:Union) where u1.U1=urn or u1.U2= urn with u1,u2,u2.uid as uid, count(*) as ct with distinct u1,u2,uid,ct where ct>1 with distinct u1,u2,uid,ct,gen.gedcom.person_from_rn(case when u2.U1 is null then 0 else u2.U1 end,true) as u1p, gen.gedcom.person_from_rn(case when u2.U2 is null then 0 else u2.U2 end,true) as u2p RETURN u1.uid as propositus_uid, uid as parents_uid, u1p as father, u2p as mother, ct order by ct desc,propositus_uid,parents_uid";
         excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "duplicate_unions", "duplicate_unions", ct, "", "0:#####;1:###,###,###;2:######;3:##.############",excelFile, false, "cypher query:\n" + cq + "\n\nUnions (marriages) that occur more thsn once in the family tree. \nComparing this with the next worksheet will distinguish duplicate ancestors with more than one union.\n\nThis report gives granular information so you can see it.\nThe may be redundancies in there are multiple marriages.", false);
        ct = ct + 1;
         
          cq = "match p=(n:Person{RN:" + rn + "})-[[r:father|mother*0..99]]->(x) with x,x.uid as uid,x.fullname + ' ⦋' + x.RN + '⦌ (' + left(x.BD,4) + '-' + left(x.DD,4) + ')' as Name, length(p) as gen, [[z in nodes(p)|z.RN]] as op, '1' + reduce(srt ='', q IN nodes(p)|srt + case when q.sex='M' then '0' else '1' end ) AS Anh with x,uid,Name,gen,'1' + right(Anh,size(Anh)-2) as Ahnen, gen.graph.get_ordpath(op) as op optional match (d:Person{RN:x.RN}) with Name as Person,uid,collect(distinct gen) as gen,collect(,gen.rel.ahnentafel(Ahnen)) as Ahnentafel,x with Person,uid,x,apoc.coll.sort(Ahnentafel) as Ahnentafel,apoc.coll.sort(gen) as gen where size(Ahnentafel)>1 with Person,uid,Ahnentafel,gen,gen.rel.compute_cor(" + rn + ",x.RN) as cor return Person,size(Ahnentafel) as ct,Ahnentafel, gen,cor,uid as union order by ct desc,Ahnentafel";
          cqq = cq.replace("[[","[").replace("]]","]");
          gen.neo4jlib.neo4j_qry.qry_to_csv(cqq,"duplicate_ancestors2.csv");
          String dup_ct_str = gen.neo4jlib.neo4j_qry.qry_str("LOAD CSV WITH HEADERS FROM 'file:///duplicate_ancestors2.csv' as line FIELDTERMINATOR ','  with sum(toInteger(line.ct)) as total_ct return toInteger(total_ct) as ct");
          String dup_ct_pc = gen.neo4jlib.neo4j_qry.qry_str("LOAD CSV WITH HEADERS FROM 'file:///duplicate_ancestors2.csv' as line FIELDTERMINATOR ','  with sum(toInteger(line.ct)) as total_ct return apoc.math.round(toFloat(total_ct)/" + fam_member_ct + " * 100,2) as pc");
 
          int dup_ct = Integer.parseInt(dup_ct_str.replace("[","").replace("]",""));
          String rws = gen.neo4jlib.neo4j_qry.qry_str("LOAD CSV WITH HEADERS FROM 'file:///duplicate_ancestors2.csv' as line FIELDTERMINATOR ',' return count(*) as ct");
          String unique_fam_mbrs_str = gen.neo4jlib.neo4j_qry.qry_str("match p=(n:Person{RN:" + rn + "})-[r:father|mother*0..99]->(x) with distinct x return count(x) as ct");
          Double unique_fam_mbrs = Double.parseDouble(unique_fam_mbrs_str.replace("[","").replace("]",""));
          String uniq_mbr_ct_pc = gen.neo4jlib.neo4j_qry.qry_str("LOAD CSV WITH HEADERS FROM 'file:///duplicate_ancestors2.csv' as line FIELDTERMINATOR ','  with sum(toInteger(line.ct)) as total_ct return apoc.math.round(toFloat(" + rws.replace("[","").replace("]","") + ")/" + unique_fam_mbrs + " * 100,2) as pc");
         // System.out.println(dup_ct);
             
        //duplicate ancestors
//      Double coi = qcoi.coefficient_of_inbredding(rn);
//        
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_package", "duplicate_ancestors", ct, "", "1:#########;4:0.########;5:####", excelFile,false,"cypher query:\n" +  cq + "\n\nfam_member_positions in the family tree  \t" + fam_member_ct + "\nUnique family members in the family tree:\t" + unique_fam_mbrs.intValue() + "\nDuplicate ancestors:\t" + rws + "    percent of unique ancestors: " + uniq_mbr_ct_pc + "%\nTotal appearances in family tree:\t" + dup_ct + "     percent of positions in the family tree:\t" + dup_ct_pc + "%\n\n\nTo see the details of paths for a specific ancestor, use this query placing the record number of the ancestor in place of the **\nmatch path=(p1:Person)-[[rp1:father|mother*1..25]]->(p2:Person) where p1.RN=" + rn + "  and p2.RN=** and p1.RN<p2.RN with rp1,reduce(srt='', z in nodes(path)|srt+z.fullname + ', ') as fn, reduce(srt2 ='', q IN nodes(path)|srt2 + case when q.sex='M' then 0 else 1 end ) as SO with fn, '1' + substring(SO,1,size(rp1)) as SO with fn,gen.rel.ahn_path(SO) as SO with fn,SO[[size(SO)-1]] as Ahnentafel return fn,Ahnentafel order by Ahnentafel\n\nTo visualize th path between the propositus and any ancestor, using this query in the Neo4j browser, adding the ancestor RN in place of **\nmatch path=(n:Person{RN:" + rn + "})-[r:father|mother*0..99]->(x:Person{RN:**} return path", false);
        
        cq = "match p= (p0:Person{RN:" + rn + "})-[[r1:father|mother*0..25]]->(a0:Person) with apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect([[x in nodes(p)|x.RN]])))) as path_nodes match p= (p1:Person{RN:" + rn + "})-[[r1:father|mother*0..25]]->(a:Person) optional match(u:Union) where u.U1=a.RN or u.U2=a.RN with a,u,path_nodes optional match (p2:Person) where p2.uid=u.uid with a.fullname as fn,a.RN as RN,u.uid as uid ,collect(p2.RN) as cRNs,path_nodes with fn,RN,cRNs, uid,apoc.coll.intersection(path_nodes,cRNs) as in_analysis,path_nodes with fn,RN as mrn,uid,cRNs,size(in_analysis) as ct, in_analysis,path_nodes optional match (p4:Person{RN:mrn})-[[:father|mother]]->(a4:Person) with fn,mrn,uid,cRNs,size(in_analysis) as ct, in_analysis,collect(a4.RN) as a2rn,path_nodes where in_analysis>[[]] with fn,mrn as RN,uid,cRNs, ct, in_analysis,apoc.coll.intersection(path_nodes,a2rn) as parents_in_analysis with fn, RN,uid,cRNs, ct, in_analysis, parents_in_analysis where parents_in_analysis=[[]] with fn,RN,uid,cRNs, ct, in_analysis, parents_in_analysis,gen.rel.compute_cor(" + rn + ",RN) as cor return fn,RN,cor,uid as union,size(cRNs) as ctc,cRNs as eol_ancestor_children, ct, in_analysis as children_in_path_to_eol_ancestor, parents_in_analysis order by RN";
        cqq = cq.replace("[[","[").replace("]]","]");
        gen.neo4jlib.neo4j_qry.qry_to_csv(cqq,"coi2.csv");      
         String cor = gen.neo4jlib.neo4j_qry.qry_str("LOAD CSV WITH HEADERS FROM 'file:///coi2.csv' as line FIELDTERMINATOR ','  with sum(toFloat(line.cor)) as total_cor return toFloat(total_cor) as cor").replace("[","").replace("]","");
         String coi = gen.neo4jlib.neo4j_qry.qry_str("LOAD CSV WITH HEADERS FROM 'file:///coi2.csv' as line FIELDTERMINATOR ','  with line.cor as cor where toInteger(line.ct)>1 with sum(toFloat(cor)) as total_cor return toFloat(total_cor) as cor").replace("[","").replace("]","");
         
        //DecimalFormat dfRound = new DecimalFormat("0.00");
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_package_" + rn, "coi", ct, "", "1:#######;2:0.############;3:#####;4:#####;6:####", excelFile, true,"UDF: \nreturn gen.rel.endogamy_panel(" + rn + ")\n\nCypher query:\n" + cq + "\n\nThis analysis uses only end of line ancestors, selected because they have no parents in the loaded GEDCOM.\nThe sum of the coefficients of relationship (cor) is theoretically 1.00, but is slightly less due to rounding of the individial cor.\n\nTotal cor: " + cor +"\ncoefficient of inbreeding (COI):" + coi + "\nPercent endogamous: " + Math.round(Double.parseDouble(coi)/Double.parseDouble(cor) *100.0) + "%\n\nCOI is the sum of the cor of the end of line ancestors with duplicate ancestors.\n\nSome reference concerning COI:\nF.M. Lancaste: http://www.genetic-genealogy.co.uk/genetics/Toc115570144.html\nAndrzej Sobczyk: https://www.sobczyk.eu/inb/\nhttp://www.insilicase.com/web/WebC_of_I.aspx\n\n", false);
      
        
        return "completed";
    }
}
