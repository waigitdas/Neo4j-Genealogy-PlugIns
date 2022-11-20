/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.endogamy;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class endogamy_compare {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String endogamy_mrcas_from_rns(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
            
  )
   
         { 
             
        String s = endogamy_mrcas(rn1,rn2);
         return s;
            }

    
    
    public static void main(String args[]) {
        //endogamy_mrcas(4L,1796L);
        endogamy_mrcas(31L,32L);
    }
    
     public static String endogamy_mrcas(Long rn1,Long rn2) 
    {
        int ct = 1;

        String rel1 = gen.gedcom.get_family_tree_data.getPersonFromRN(rn1, true);
        String rel2 = gen.gedcom.get_family_tree_data.getPersonFromRN(rn2, true);
        
        //common ancestors
        String cq ="match path1= (p1:Person{RN:" + rn1 + "})-[[r1:father|mother*0..15]]->(mrca:Person) match path2=(mrca)<-[[r2:father|mother*0..15]]-(p2:Person{RN:" + rn2 + "}) with mrca, [[x in relationships(path1)|id(x)]] as r1s, gen.rel.ahnentafel_for_ancestor(" + rn1 + ",mrca.RN) as ahn1, [[y in relationships(path2)|id(y)]] as r2s, gen.rel.ahnentafel_for_ancestor(" + rn2 + ",mrca.RN) as ahn2 with distinct mrca.fullname as mrca,mrca.RN as RN,ahn1 as ahnentafel1,ahn2 as ahnentafel2,size(r1s) as path1_length,size(r2s) as path2_length where ahnentafel1<>'not an ancestor or too remote' and ahnentafel2<>'not an ancestor or too remote' return mrca as ancestor,RN, ahnentafel1,ahnentafel2,path1_length,path2_length order by ancestor";
        String anc_ct = gen.neo4jlib.neo4j_qry.qry_str("LOAD CSV WITH HEADERS FROM 'file:///endogamy_package.csv' as line FIELDTERMINATOR '|'  with distinct line.ancestor as ancestor return count(ancestor) as ct");
        
     String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_package", "common_ancestors", ct, "", "1:#######;2:########;3:########;4:########;5:#######", "", false,"UDF:\nreturn gen.rel.endogamy_mrcas_from_rns(" + rn1 + "." + rn2 + ")\n\ncypher query:\n" +  cq + "\n\nperson 1: " + rel1 + "\nperson 2: " + rel2 + "\n\nAhnentafel are returned for both person's pedigrees", false);
        ct = ct + 1;
        
        //paths between two persons and their many common ancestors
        cq =cq = "match p= (p1:Person{RN:" + rn1 + "})-[[r1:father|mother*0..15]]->(mrca:Person)<-[[r2:father|mother*0..15]]-(p2:Person{RN:" + rn2 + "}) with p,r1,r2,collect(mrca.fullname) as mrcas, [[x in nodes(p)|x.RN]] as path, [[y in nodes(p)|y.fullname]] as names, gen.rel.compute_cor(" + rn1 + ",mrca.RN) as cor1,gen.rel.compute_cor(" + rn2 + ",mrca.RN) as cor2,gen.rel.ahnentafel_for_ancestor(" + rn1 + ",mrca.RN) as ahn1,gen.rel.ahnentafel_for_ancestor(" + rn2 + ",mrca.RN) as ahn2,mrca.RN as mrn with cor1,cor2,ahn1,ahn2,mrn,size(path) as path_length,size(r1) as path1,size(r2) as path2, names,mrcas,path, gen.graph.get_ordpath(path) as op with cor1,cor2,ahn1,ahn2,mrn,path_length,path1,path2,gen.rel.relationship_from_path(1,path1,path2) as rel,names,mrcas,path,op as ordpath return mrcas as common_ancestor,mrn as RN,ahn1,ahn2,case when rel=replace(rel,'error','') then rel else '~' end as rel,cor1,cor2, names as path_persons,path as path_rns,path_length,path1,path2, ordpath as ordpath order by ordpath";
      excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_package2", "paths", ct, "", "0:####;1:#######;5:0.########;6:0.########", excelFile, false,"cypher query:\n" +  cq+ "\n\nnumber of unique common ancestors: " + anc_ct + "\n\npath lenghts in column J is all the hops in the path.\npath1 and path2 lengths are those up to but excluding the common ancestor.\nThus path1 + path2 lengths are one less than the total length.\n\nThe relationships are between " + rel1 + " and " +rel2 + " and are computed as half because the path ends with a single ancestor.\n\nahn1 and ahn2 are the ahnentafel positions of the ancestor in the pedigree chart of persons 1 and2.\n\ncor1 and cor2 are the correlation of relationship for the two individuals to the common ancestor.match path=(p1:Person)-[rp1:father|mother*1..25]->(p2:Person) where p1.RN=4  and p2.RN=** and p1.RN<p2.RN with rp1,reduce(srt='', z in nodes(path)|srt+z.fullname + ', ') as fn, reduce(srt2 ='', q IN nodes(path)|srt2 + case when q.sex='M' then 0 else 1 end ) as SO with fn, '1' + substring(SO,1,size(rp1)) as SO with fn,gen.rel.ahn_path(SO) as SO with fn,SO[size(SO)-1] as Ahnentafel return fn,Ahnentafel order by Ahnentafel\n\n", false);
        ct = ct + 1;
      
        //relationships between common ancestors who appear more than twice in a family tree
        cq =cq = "match p= (p1:Person{RN:" + rn1 + "})-[[r1:father|mother*0..15]]->(mrca:Person)<-[[r2:father|mother*0..15]]-(p2:Person{RN:" + rn2 + "}) with collect(mrca) as mrcas unwind mrcas as x call { with x, mrcas unwind mrcas as y call{ with x, y with x,y where x<y with x,y,  gen.rel.relationship_from_RNs(x.RN,y.RN) as rel optional match (p1:Person{RN:x.RN})-[[rs:spouse*0..1]]-(p2:Person{RN:y.RN}) with x,y,rel, case when p2 is null then 'N' else 'Y' end as spouse with x,y,rel,spouse where rel>' ' or spouse='Y' return x.fullname as a,x.RN as arn, y.fullname as b,y.RN as brn, case when rel < ' ' then '~' else rel end as rel, spouse } return a,arn,b,brn,case when rel is null then '~' else rel end as rel,spouse } return distinct a as ancestor1,arn as rn1,b as ancestor2,brn as rn2, rel,spouse order by a,b";
      excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_package3", "ancestor_relationships", ct, "", "1:#######;3:########", excelFile, false,"cypher query:\n" +  cq + "\n\ncommon ancestors with relationships to each other are shown. ", false);
        ct = ct + 1;
        
          String rel_list = gen.neo4jlib.neo4j_qry.qry_str("LOAD CSV WITH HEADERS FROM 'file:///endogamy_package2.csv' as line FIELDTERMINATOR '|'  with apoc.coll.sort(apoc.coll.flatten(apoc.coll.flatten(collect(distinct line.rel))) ) as rels return  rels");
        
        //duplicate ancestors
        cq = "match path=(p1:Person{RN:" + rn1 + "})-[r1:father|mother*0..25]->(mrca:Person) with path,p1,r1,mrca match(mrca)<-[r2:father|mother*0..25]-(p2:Person{RN:" + rn2 + "}) where p2<>p1 with distinct r1,mrca with mrca, count(*) as ct with mrca,ct where ct>1 and mrca.uid=0 with mrca,gen.rel.ahnentafel_for_ancestor(" + rn1 + ",mrca.RN) as ahn, gen.rel.compute_cor(" + rn1 + ",mrca.RN) as cor return mrca.fullname + ' [' + mrca.RN + ']' as ancestor,size(ahn)-size(replace(ahn,';',''))+1 as ct,ahn as ahnentafel, cor";
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_package4", "duplicate_ancestors", ct, "", "1:#########;2:0.#######", excelFile, true,"cypher query:\n" +  cq + "\n\nRelationships identified in the paths worksheet: " + rel_list.replace("[","").replace("]","").replace("\\",""), false);
        ct = ct + 1;
     
        return "completed";
    }
}
