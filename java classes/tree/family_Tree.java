/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tree;

import gen.neo4jlib.neo4j_qry;
import java.util.Map;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class family_Tree {
    @UserFunction
    @Description("Descendant tree is ORDPATH order")

    public String family_tree(
        @Name("rn") 
            Long rn
  )
   
         { 
             
        String m = get_tree(rn);
         return m;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_tree(Long rn) 
    {
        String cq = "MATCH (n:Person{RN:" + rn + "}) match p=(n)-[:father|mother*0..99]->(x) with x.fullname + ' ⦋' + x.RN + '⦌ (' + left(x.BD,4) + '-' + left(x.DD,4) + ')' as Name, length(p) as gen, reduce(srt2 ='', q IN nodes(p)| srt2 + replace(replace(q.sex,'M','A'),'F','B')) AS sortOrder, '1' + reduce(srt ='', q IN nodes(p)|srt + case when q.sex='M' then '0' else '1' end ) AS Anh with Name,gen,sortOrder,'1' + right(Anh,size(Anh)-2) as Ahnen return Name,gen,gen.rel.ahnentafel(Ahnen) as Ahnentafel order by Ahnentafel";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "family_tree", "tree", 1, "", "1:###;2:##########", "", true,"UDF:\nreturn gen.tree.family_tree(" + rn + ")\n\nCypher query:\n" + cq , true);
        return "completed";    
    }
}
