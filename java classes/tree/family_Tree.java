/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tree;

import gen.neo4jlib.neo4j_qry;
import java.util.Map;
import static org.bouncycastle.util.Strings.toUpperCase;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class family_Tree {
    @UserFunction
    @Description("Descendant tree is ORDPATH order")

    public String family_tree(
        @Name("rn") 
            Long rn,
        @Name("order")
            String order 
  )
   
         { 
             
        String m = get_tree(rn,order);
         return m;
            }

    
    
    public static void main(String args[]) {
        get_tree(1L,"p");
    }
    
     public static String get_tree(Long rn, String order) 
    {
        String cq = "";
        if (order.toUpperCase().equals("P")){
        cq = "MATCH (n:Person{RN:" + rn + "}) match p=(n)-[r:father|mother*0..99]->(x) with x,x.fullname + ' [' + x.RN + '] (' + left(x.BD,4) + '-' + left(x.DD,4) + ')' as Name, length(p) as gen, [z in nodes(p)|z.RN] as op, '1' + reduce(srt ='', q IN nodes(p)|srt + case when q.sex='M' then '0' else '1' end ) AS Anh with x,Name,gen,'1' + right(Anh,size(Anh)-2) as Ahnen, gen.graph.get_ordpath(op) as op optional match (d:Person{RN:x.RN}) return apoc.text.lpad('',(gen)*5,'.') + Name,gen,gen.rel.ahnentafel(Ahnen) as Ahnentafel,case when d.iYHG is null then '~' else d.iYHG end as inferred_YHG,case when d.imtHG is null then '~' else d.imtHG end as inferred_mtHG order by op";
        }
        else {
        cq = "MATCH (n:Person{RN:" + rn + "}) match p=(n)-[r:father|mother*0..99]->(x) with x,x.fullname + ' [' + x.RN + '] (' + left(x.BD,4) + '-' + left(x.DD,4) + ')' as Name, length(p) as gen, [z in nodes(p)|z.RN] as op, '1' + reduce(srt ='', q IN nodes(p)|srt + case when q.sex='M' then '0' else '1' end ) AS Anh with x,Name,gen,'1' + right(Anh,size(Anh)-2) as Ahnen, gen.graph.get_ordpath(op) as op optional match (d:Person{RN:x.RN}) return apoc.text.lpad('',(gen)*5,'.') + Name,gen,gen.rel.ahnentafel(Ahnen) as Ahnentafel,case when d.iYHG is null then '~' else d.iYHG end as inferred_YHG,case when d.imtHG is null then '~' else d.imtHG end as inferred_mtHG order by Ahnentafel";
        }
            
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "family_tree", "tree", 1, "", "1:###;2:##########", "", true,"UDF:\nreturn gen.tree.family_tree(" + rn + ")\n\nCypher query:\n" + cq , true);
        return "completed";    
    }
}
