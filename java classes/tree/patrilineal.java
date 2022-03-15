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


public class patrilineal {
    @UserFunction
    @Description("Patrilineal tree reports.")

    public String patrilineal_lineage(
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
        String cq = "MATCH (n:Person{RN:" + rn + "}) match p=(n)-[[:father*0..99]]->(x) with x.fullname + ' ⦋' + x.RN + '⦌ (' + left(x.BD,4) + '-' + left(x.DD,4) + ')' as Name, length(p) as gen,[[rn in nodes(p)|rn.RN]] AS op, reduce(srt2 ='', q IN nodes(p)| srt2 + replace(replace(q.sex,'M','A'),'F','B')) AS sortOrder, '1' + reduce(srt ='', q IN nodes(p)|srt + case when q.sex='M' then '0' else '1' end ) AS Anh with Name,gen,sortOrder,'1' + right(Anh,size(Anh)-2) as Ahnen,op return Name,gen,gen.rel.ahnentafel(Ahnen) as Ahnentafel order by gen.graph.get_ordpath(op) desc";
        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Patrilineal_Trees", "ascending", ct, "", "1:###;2:#########", "", false,"UDF:\nreturn gen.rel.patrilineal_lineage(" + rn + ")\n\ncypher query:\n" + cq + "\n\nPatrilineal path to most distance male-line ancestor.", false);
    ct=ct +1;      
    
    cq = "MATCH a=(n:Person{RN:" + rn + "})-[[:father*0..99]]->(x:Person) where x.sex='M' and (x.uid is null or x.uid=0) with collect(x.RN) as rns,length(a) as gen_max unwind rns as ua call { with ua match p=(n:Person{RN:ua})<-[[r:father*0..50]]-(m) where m.sex='M' with distinct m.RN as RN, m.fullname + ' [[' + m.RN + ']](' + case when m.BD is null then '' else left(m.BD,4) end + '-' + case when m.DD is null then '' else left(m.DD,4) end + ')' as FullName, length(p) as generation, case when left(m.BD,4)>'1930' and rtrim(m.DD)='' then 'Y' else 'N' end as Y_DNA_Candidate, reduce(srt2 ='|', q IN nodes(p)| srt2 + q.RN + '|') AS PathOrder, [[rn in nodes(p)|rn.RN]] AS op with RN,FullName,generation as gen,PathOrder,gen.graph.get_ordpath(op) as srt,op \n" +
"optional MATCH p=(m:DNA_Match{RN:RN})-[[r:match_block]]->(b:block)  return RN,FullName, gen,PathOrder,srt ,op,case when b.name is null then '' else b.name end as YHG } return apoc.text.lpad('',(gen-1)*3,'.') + FullName as Fullname,gen,YHG order by srt";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "Patrilineal_all", "all patrilineal relatives", ct, "", "1:###;1:###", excelFile, true, "UDF:\nreturn gen.rel.patrilineal_lineage(" + rn + ")\n\ncypher query:\n" + cq + "\n\nPatrilineal descendants of the most distant patrilineal ancestor. You can use this to identify candidates for Y-DNA testing.", false);
    ct=ct +1;      
    
    
        return "patrilineal tree report completed";
    }
}
