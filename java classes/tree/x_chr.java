/**
 * Copyright 2022 
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
    @Description("X-inheritance tree reports.")

    public String x_chr_lineage(
        @Name("rn") 
            Long rn
  )
   
         { 
             
        String s = show_lineage(rn);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String show_lineage(Long rn)
    {
        int ct = 1;
        String cq = "MATCH (n:Person{RN:" + rn + "}) match p=(n)-[[:father|mother*0..99]]->(x) with x.fullname + ' ⦋' + x.RN + '⦌ (' + left(x.BD,4) + '-' + left(x.DD,4) + ')' as Name, length(p) as gen,[[rn in nodes(p)|rn.RN]] AS op, reduce(srt2 ='', q IN nodes(p)| srt2 + replace(replace(q.sex,'M','A'),'F','B')) AS sortOrder, '1' + reduce(srt ='', q IN nodes(p)|srt + case when q.sex='M' then '0' else '1' end ) AS Anh with Name,gen,sortOrder,'1' + right(Anh,size(Anh)-2) as Ahnen,op where sortOrder=replace(sortOrder,'MM','') return Name,gen,gen.rel.ahnentafel(Ahnen) as Ahnentafel order by gen.graph.get_ordpath(op) desc";
        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "x_chr_Trees", "ascending", ct, "", "1:###;2:#########", "", false,"UDF:\nreturn gen.tree.x_chr_lineage(" + rn + ")\n\ncypher query:\n" + cq + "\n\nX-chromosome path to most distance ancestors.", false);
    ct=ct +1;      
    
    cq = "match p=(n:Person{RN:" + rn + "})-[[:father|mother*0..99]]->(m) with m, reduce(status ='', q IN nodes(p)| status + q.sex) AS c, reduce(srt2 ='|', q IN nodes(p)| srt2 + q.RN + '|') AS PathOrder,[[rn in nodes(p)|rn.RN]] as op where c=replace(c,'MM','') return  m.fullname + ' ⦋' + m.RN + '⦌ (' + left(m.BD,4) + '-' + left(m.DD,4) + ')' as Name, c as gender_path,size(c)-1 as gen order by gen desc,gen.graph.get_ordpath(op) desc";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "x_chr_all", "all x_chr relatives", ct, "", "1:###;1:###", excelFile, true, "UDF:\nreturn gen.tree.x+chr_lineage(" + rn + ")\n\ncypher query:\n" + cq + "\n\nX-chtomosome descendants of the most distant ancestor. You can use this to identify candidates for verifying whether to accept X-matches.", false);
    ct=ct +1;      
    
    
        return "patrilineal tree report completed";
    }
}
