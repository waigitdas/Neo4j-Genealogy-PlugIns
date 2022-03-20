/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tree;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class x_chr {
    @UserFunction
    @Description("list of decendants potentially inheriting the X-chromosome from the ancestor whose RN is submitted.")

    public String X_chromosome_inheritance
        (
        @Name("rn") 
            Long rn
  )
   
         { 
             
        String r = get_x(rn);
         return r;
            }
    
     public String get_x(Long rn) 
    {
        int ct = 1 ;
        //ancestors
        String cq = "MATCH (n:Person{RN:" + rn + "}) match p=(n)-[[:father|mother*0..99]]->(x) with x.fullname + ' ⦋' + x.RN + '⦌ (' + left(x.BD,4) + '-' + left(x.DD,4) + ')' as Name, length(p) as gen,[[rn in nodes(p)|rn.RN]] AS op, reduce(srt2 ='', q IN nodes(p)| srt2 + q.sex) AS sortOrder, '1' + reduce(srt ='', q IN nodes(p)|srt + case when q.sex='M' then '0' else '1' end ) AS Anh with Name,gen,sortOrder,'1' + right(Anh,size(Anh)-2) as Ahnen,op where sortOrder=replace(sortOrder,'MM','') return Name,gen,sortOrder as sex_path,gen.rel.ahnentafel(Ahnen) as Ahnentafel order by Ahnentafel,gen.graph.get_ordpath(op) ";
        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "X chr inheritance", "ancestors", ct, "", "1:###;3:#####", "", false,"UDF: \return gen.tree.X_chromosome_inheritance(" + rn + ")\n\nCypher query:\n" + cq + "\n\nAncestors from whom the X-chromosome may have come.to " + gen.gedcom.get_family_tree_data.getPersonFromRN(rn, true) + "\n\nThis is a list of POSSIBLE sources of the X-chromosome. Not all of these people will share the same X-chromosome.\nThe X-chromosome is not inherited male to male. The sex path cannot include MM and is shown to demonstrate this.\n\nThis report sorts the rows using ORDPATH, creating a hierarchical ordering.",false);
        ct = ct + 1;
       
        //descendants
        cq = "match p=(n:Person{RN:" + rn + "})<-[[:father|mother*0..99]]-(m) with m, reduce(status ='', q IN nodes(p)| q.sex + status ) AS c, [[rn in nodes(p)|rn.RN]]  AS op, reduce(srt2 ='', q IN nodes(p)|srt2  + q.RN + '>') AS PathOrder,reduce(n =-1, q IN nodes(p)| n + 1) as generations where c=replace(c,'MM','') with distinct m.fullname + ' ⦋' + m.RN + '⦌' as Fullname, c as Path, op,left(PathOrder,size(PathOrder)-1) as PathOrder, generations, '1' + replace(replace(c,'M','1'),'F','0') as Ahnen return apoc.text.lpad('',4*generations,' ') + Fullname as Descendant,Path as Ascending_Sex_Path,gen.rel.ahnentafel(left(Ahnen,size(Ahnen)-2) +'1') as Ancestor_Ahnentafel, PathOrder as Descending_Hierarchical_Path_Order,generations order by gen.graph.get_ordpath(op)";
       gen.excelLib.queries_to_excel.qry_to_excel(cq, "X chr descendants", "descendants", ct, "", "2:###;4:#####", excelFile, false,"UDF: \nreturn gen.tree.X_chromosome_inheritance(" + rn + ")\n\nCypher query:\n" + cq + "\n\nCandidates for inheriting X-chr from " + gen.gedcom.get_family_tree_data.getPersonFromRN(rn, true) + "\n\nThis is a list of POSSIBLE sources of the X-chromosome. Not all of these people will share the same X-chromosome.\nThe X-chromosome is not inherited male to male. The sex path cannot include MM and is shown to demonstrate this.\n\nThis report sorts the rows using ORDPATH, creating a hierarchical ordering.",false);
        ct = ct + 1;
        
       //all
           cq =    "MATCH a=(n:Person{RN:" + rn + "})-[[:father|mother*0..15]]->(x:Person) where  (x.uid is null or x.uid=0)  with collect(x.RN) as rns,length(a) as gen_max,reduce(srt2 ='', q IN nodes(a)| srt2 + q.sex) AS c where c=replace(c,'MM','') unwind rns as ua call { with ua match p=(n:Person{RN:ua})<-[[r:father|mother*0..15]]-(m) with m,p,reduce(srt ='', q IN nodes(p)| srt + q.sex) AS cc where cc=replace(cc,'MM','') with distinct cc,m.RN as RN, m.fullname  + ' ⦋' + m.RN + '⦌  (' + case when m.BD is null then '' else left(m.BD,4) end + '-' + case when m.DD is null then '' else left(m.DD,4) end + ')' as FullName, length(p) as generation, case when left(m.BD,4)>'1930' and rtrim(m.DD)='' then 'Y' else 'N' end as Y_DNA_Candidate, reduce(srt2 ='|', q IN nodes(p)| srt2 + q.RN + '|') AS PathOrder, [[rn in nodes(p)|rn.RN]] AS op with cc,RN,FullName,generation as gen,PathOrder,gen.graph.get_ordpath(op) as srt,op optional MATCH p=(m:DNA_Match{RN:RN})-[[r:match_block]]->(b:block)  return cc as sex_path,RN,FullName, gen,PathOrder,srt ,op,case when b.name is null then '' else b.name end as YHG } return apoc.text.lpad('',(gen-1)*3,'.') + FullName as Fullname,sex_path,gen,YHG order by srt";
         gen.excelLib.queries_to_excel.qry_to_excel(cq, "X chr all", "all", ct, "", "2:###;4:#####", excelFile, false,"UDF: \nreturn gen.tree.X_chromosome_inheritance(" + rn + ")\n\nCypher query:\n" + cq + "\n\nRelatives of " + gen.gedcom.get_family_tree_data.getPersonFromRN(rn, true) + " who may share the X-chromome from a common ancestor.\n\nThis is a list of POSSIBLE sources of the X-chromosome. Not all of these people will share the same X-chromosome.\nThe X-chromosome is not inherited male to male. The sex path cannot include MM and is shown to demonstrate this.\n\nThis report sorts the rows using ORDPATH, creating a hierarchical ordering.",false);
        ct = ct + 1;
        
        
              cq =    "MATCH p=(m1:DNA_Match)-[r:match_by_segment]->(m2:DNA_Match) where r.x_gen_dist >0 RETURN m1.fullname  + ' ⦋' + m1.RN + '⦌'   as match1,m2.fullname   + ' ⦋' + m2.RN + '⦌'  as match2,r.rel as rel,r.x_gen_dist as x_gen_dist,r.x_cm as x_cm order by x_gen_dist";
         gen.excelLib.queries_to_excel.qry_to_excel(cq, "X known_relatives", "known_x_relatives", ct, "", "3:###;4:#####", excelFile, false,"UDF: \nreturn gen.tree.X_chromosome_inheritance(" + rn + ")\n\nCypher query:\n" + cq + "\n\nThis is a list of persons in the family tree who ACTUALLY may share an X-chromosome. \nThe X-chromosome is not inherited male to male.\nFTDNA does not know the family tree and will report x-mtches when it is not tenable within the known family tree; the match is on a branch not yet in the family tree.",false);
        ct = ct + 1;
        
              cq =    "MATCH p=(m1:DNA_Match)-[r:match_by_segment]->(m2:DNA_Match) where r.x_gen_dist =0 RETURN m1.fullname  + ' ⦋' + m1.RN + '⦌'   as match1,m2.fullname   + ' ⦋' + m2.RN + '⦌'  as match2,r.rel as rel,r.x_gen_dist as x_gen_dist,r.x_cm as x_cm order by x_gen_dist";
         gen.excelLib.queries_to_excel.qry_to_excel(cq, "X unknown_relatives", "unknown_x_ancestor", ct, "", "3:###;4:#####", excelFile, true,"UDF: \nreturn gen.tree.X_chromosome_inheritance(" + rn + ")\n\nCypher query:\n" + cq + "\n\nThis is a list of persons in the family tree who ACTUALLY share an X-chromosome but whose common ancestor is not in the family tree. \nThe X-chromosome is not inherited male to male.\nFTDNA does not know the family tree and will report x-mtches when it is not tenable within the known family tree; the match is on a branch not yet in the family tree.",false);
        ct = ct + 1;
        
       
        
        return "x-inheritace report completed!";
    }
}
