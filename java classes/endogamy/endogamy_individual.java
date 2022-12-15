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
        @Name("proband_rn") 
            Long proband_rn
  )
         { 
             
        String s = create_panel(proband_rn);
         return s;
            }
    
    public static void main(String args[]) {
        create_panel(4L);
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
         String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_package_" + rn, "family_tree", ct, "", "1:#######;2:###,###,###,###", "", false,"UDF: \nreturn gen.endogamy.endogamy_panel(" + rn + ")\n\nUDF:\nreturn gen.endogamy.coefficient_of_inbreeding(" + rn + ")\n\nCypher query:\n" + cq, false);
        ct = ct + 1;
        int fam_member_ct =gen.excelLib.queries_to_excel.rwCt;
        
        //pedigree completeness
        cq = "match p=(n:Person{RN:" + rn + "})-[[:father|mother*..99]]->(x) with reduce(n=0,q in nodes(p)|n+1) as gen RETURN gen, count(*) as Observed,2^(gen-1) as Expected,count(*)/(2^(gen-1)) as Ratio order by gen";
         excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_package2", "pedigree_completeness", ct, "", "0:#####;1:###,###,###;2:######;3:##.############",excelFile, false, "cypher query:\n" + cq, false);
        ct = ct + 1;
 
         //duplicate unions
         cq = "with " + rn + " as urn MATCH p=(u1:Union)-[[r:union_parent*0..99]]->(u2:Union) where u1.U1=urn or u1.U2= urn with u1,u2,u2.uid as uid, count(*) as ct,u2.cor as cor,u2.rel as rel with distinct u1,u2,uid,ct,cor,rel where ct>1  with distinct u1,u2,uid,ct,cor,rel,gen.gedcom.person_from_rn(case when u2.U1 is null then 0 else u2.U1 end,true) as u1p, gen.gedcom.person_from_rn(case when u2.U2 is null then 0 else u2.U2 end,true) as u2p RETURN u1.uid as proband_uid, uid as parents_uid, u1p as father, u2p as mother, ct,cor,rel order by ct desc,proband_uid,parents_uid";
         excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "duplicate_unions", "duplicate_unions", ct, "", "0:#####;1:###,###,###;2:######;3:##.############;4:#####",excelFile, false, "cypher query:\n" + cq + "\n\nUnions (marriages) that occur more thsn once in the family tree. \nComparing this with the next worksheet will distinguish duplicate ancestors with more than one union.\n\nThis report gives granular information so you can see it.\nThe may be redundancies in there are multiple marriages.", false);
        ct = ct + 1;
         
        
        //all union tree
        cq = "with 5816 as urn MATCH p=(u1:Union)-[r:union_parent*0..99]->(u2:Union) where u1.U1=urn or u1.U2= urn with u1,u2,u2.uid as uid, count(*) as ct,u2.cor as cor,u2.rel as rel with distinct u1,u2,uid,ct,cor,rel with distinct u1,u2,uid,ct,cor,rel,gen.gedcom.person_from_rn(case when u2.U1 is null then 0 else u2.U1 end,true) as u1p, gen.gedcom.person_from_rn(case when u2.U2 is null then 0 else u2.U2 end,true) as u2p RETURN gen.gedcom.person_from_rn(5816,true) as proband, u1.uid as proband_uid, uid as parents_uid, u1p as father, u2p as mother, ct,cor,rel order by ct desc,proband_uid,parents_uid";
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "all_unions", "union_tree", ct, "", "0:#####;1:###,###,###;2:######;3:##.############;4:#####",excelFile, false, "cypher query:\n" + cq + "\n\n", false);
        ct = ct + 1;
           
        //duplicate ancestors
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
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_package", "duplicate_ancestors", ct, "", "1:#########;4:0.########;5:####", excelFile,false,"cypher query:\n" +  cq + "\n\nfam_member_positions in the family tree  \t" + fam_member_ct + "\nUnique family members in the family tree:\t" + unique_fam_mbrs.intValue() + "\nDuplicate ancestors:\t" + rws + "    percent of unique ancestors: " + uniq_mbr_ct_pc + "%\nTotal appearances in family tree:\t" + dup_ct + "     percent of positions in the family tree:\t" + dup_ct_pc + "%\n\n\nTo see the details of paths for a specific ancestor, use this query placing the record number of the ancestor in place of the **\nmatch path=(p1:Person)-[[rp1:father|mother*1..25]]->(p2:Person) where p1.RN=" + rn + "  and p2.RN=** and p1.RN<p2.RN with rp1,reduce(srt='', z in nodes(path)|srt+z.fullname + ', ') as fn, reduce(srt2 ='', q IN nodes(path)|srt2 + case when q.sex='M' then 0 else 1 end ) as SO with fn, '1' + substring(SO,1,size(rp1)) as SO with fn,gen.rel.ahn_path(SO) as SO with fn,SO[[size(SO)-1]] as Ahnentafel return fn,Ahnentafel order by Ahnentafel\n\nTo visualize th path between the proband and any ancestor, using this query in the Neo4j browser, adding the ancestor RN in place of **\nmatch path=(n:Person{RN:" + rn + "})-[r:father|mother*0..99]->(x:Person{RN:**} return path", false);
        ct = ct + 1;
        
        //path to individuals all ancestors
        cq ="MATCH (p:fam_path) where apoc.coll.contains(p.persons," + rn + ")=true with p with apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect(p.persons)))) as rns with size(rns) as ct,rns unwind rns as z call { with z,rns match (pp:Person{RN:z})-[[rp:father|mother]]->(ppa:Person) with z,rns,apoc.coll.intersection(rns,collect(ppa.RN)) as split with z,rns,case when size(split)=2 then true else false end as split with z,rns,split match (ppp:Person{RN:z})<-[[rp2:father|mother]]-(pppa:Person) with rns,split,apoc.coll.intersection(rns,collect(pppa.RN)) as merged with split,case when size(merged)>1 then true else false end as merged return split,merged } with z, split,merged  match (pz:Person{RN:z}) return gen.gedcom.person_from_rn(z,true) as Path_persons,case when split then 'Y' else '~' end as split,case when merged then 'Y' else '~' end as merge,pz.coi as COI, pz.coi_gen as coi_gen";
           excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_package", "paths_to_ancestors", ct, "", "1:#########;3:0.########;4:####", excelFile,false,"cypher query:\n" +  cq + "\n\nShown are persons who are in all the paths to the proband's ancestors.\n\nIf the person has endogamy, the COI is shown\nThe coi_gen is the genetic distance of the person to their most recent endogamous ancestor (MREA).\n\nThe paths to common ancestors may merge or split at the person's position in the paths.\nGFG determined this by seeing if the person has more than one parent (split) or child (merge) in the paths.", false);
        ct = ct + 1;
         
        //paths
        cq ="MATCH path=(i:intersect)-[[ri:path_intersect]]-(f:fam_path)-[[r:path_person]]->(p:Person{RN:" + rn + "}) with f.persons as fam_path,collect(distinct i.persons) as i order by gen.graph.get_ordpath(f.persons) return gen.gedcom.person_from_rn(last(fam_path),true) as last_in_path, fam_path,size(fam_path) as len, size(i) as intersections";
            excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_package", "paths", ct, "", "2:#########;3:0.########;4:####", excelFile,false,"cypher query:\n" +  cq + "\n\nAll of the unique paths of the proband and the number of intersections with other paths.", false);
        ct = ct + 1;
         
        
        
        //intersections
        cq = "MATCH path=(i:intersect)-[[ri:path_intersect]]-(f:fam_path)-[[r:path_person]]->(p:Person{RN:" + rn + "}) with i.persons as intersection,collect(distinct f.persons) as f order by gen.graph.get_ordpath(i.persons) return gen.gedcom.person_from_rn(last(intersection),true) as last_in_path, intersection,size(intersection) as len, size(f) as fam_paths";
             excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_package", "intersections", ct, "", "2:#########;3:0.########;4:####", excelFile,false,"cypher query:\n" +  cq + "\n\nSubpaths in the proband path that intersect with other paths.", false);
        ct = ct + 1;
         
       
        //coi
        Double coi_proband = gen.endogamy.coi.get_coi(rn);
        
        cq = "Match (p:Person{RN:" + rn + "}) with p MATCH path=(u1:Union{uid:p.uid})-[[r:union_parent*0..25]]->(ua:Union) with ua, reduce(s='',x in relationships(path)|s + x.side) as uid_path where ua.cor is not null with ua as ua,ua.cor as cor ,size(uid_path) + 1 as gen, uid_path order by gen with ua,cor,gen,uid_path, left(uid_path,1) as side with ua, cor,gen,uid_path,side  return ua.uid as uid, gen.gedcom.person_from_rn(ua.U1,true) + ' : ' + gen.gedcom.person_from_rn(ua.U2,true) as union_couple, gen, cor,uid_path, side"; 
//        cqq = cq.replace("[[","[").replace("]]","]");
//        gen.neo4jlib.neo4j_qry.qry_to_csv(cqq,"coi2.csv");      
//         String cor = gen.neo4jlib.neo4j_qry.qry_str("LOAD CSV WITH HEADERS FROM 'file:///coi2.csv' as line FIELDTERMINATOR ','  with sum(toFloat(line.cor)) as total_cor return toFloat(total_cor) as cor").replace("[","").replace("]","");
//         String coi = gen.neo4jlib.neo4j_qry.qry_str("LOAD CSV WITH HEADERS FROM 'file:///coi2.csv' as line FIELDTERMINATOR ','  with line.cor as cor where toInteger(line.ct)>1 with sum(toFloat(cor)) as total_cor return toFloat(total_cor) as cor").replace("[","").replace("]","");
//         
        //DecimalFormat dfRound = new DecimalFormat("0.00");
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_package_" + rn, "coi", ct, "", "0:#######;2:######;3:0.############", excelFile, true,"UDF: \nreturn gen.rel.endogamy_panel(" + rn + ")\n\nCypher query:\n" + cq + "\n\nThis analysis uses the union_parent relationship and the uid_path length (=generation) to create an ordered list of unions with the coefficient of relationship between the couple in the union. \nThe top row represents the union producing the most recent endogamous ancestor (MREA) and its generation in the family tree of the proband. \nThis information is used to compute the proband's coefficient of inbreeding (COI) by dividing the COR of the MREA's parents by 1/2 for each generation between the proband and the MREA.\n\nCOI: " + coi_proband + "\n\nReferences:\nhttps://www.journals.uchicago.edu/doi/10.1086/279872\n", false);
      
        
        return "completed";
    }
}
