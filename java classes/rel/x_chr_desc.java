/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class x_chr_desc {
    @UserFunction
    @Description("list of decendants potentially inheriting the X-chromosome from the ancestor whose RN is submitted.")

    public String X_chromosome_inheritance_from_ancestor
        (
        @Name("anc_rn") 
            Long anc_rn
  )
   
         { 
             
        String r = get_desc(anc_rn);
         return r;
            }
    
     public String get_desc(Long anc_rn) 
    {
        String cq = "match p=(n:Person{RN:" + anc_rn + "})<-[:father|mother*0..99]-(m) with m, reduce(status ='', q IN nodes(p)| q.sex + status ) AS c, reduce(srt2 ='', q IN nodes(p)| q.RN + '>' + srt2) AS PathOrder,reduce(n =-1, q IN nodes(p)| n + 1) as generations where c=replace(c,'MM','') with distinct m.fullname + ' ⦋' + m.RN + '⦌' as Fullname, c as Path, left(PathOrder,size(PathOrder)-1) as PathOrder, generations, '1' + replace(replace(c,'M','1'),'F','0') as Ahnen return Fullname as Descendant,Path,gen.rel.ahnentafel(left(Ahnen,size(Ahnen)-2) +'1') as Ancestor_Ahnentafel, PathOrder,generations order by generations,Ancestor_Ahnentafel";
        String r = gen.excelLib.queries_to_excel.qry_to_excel(cq, "X chr descendants", "descendants", 1, "", "2:###;4:#####", "", true,"UDF: return gen.rel.X_chromosome_inheritance_from_ancestor(" + anc_rn + ")\n\nCandidates for inheriting X-chr from " + gen.gedcom.get_family_tree_data.getPersonFromRN(anc_rn, true),false);
        return r;
    }
}
