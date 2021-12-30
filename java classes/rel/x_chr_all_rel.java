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


public class x_chr_all_rel {
    @UserFunction
    @Description("list of decendants potentially inheriting the X-chromosome from the ancestor whose RN is submitted.")

    public String X_chromosome_all_relatives
        (
        @Name("rn") 
            Long rn
  )
   
         { 
             
        String r = get_rel(rn);
         return r;
            }
    
     public String get_rel(Long rn) 
    {
        String cq = "match p=(p1:Person{RN:" + rn + "})-[:father|mother*0..99]->(x) with x,reduce(srt2 ='', q IN nodes(p)| srt2 + q.sex) AS c where c=replace(c,'MM','') with collect (x.RN) as RNs match m=(p2:Person)<-[:father|mother*0..99]-(y) where p2.RN in RNs with y, reduce(srt3 ='', s IN nodes(m)| srt3 + s.sex) AS cc with y where cc=replace(cc,'MM','') match sp=shortestpath ((y)-[:father|mother*0..99]-(z:Person{RN:" + rn + "}) ) with y,sp,reduce(ss='', t in nodes(sp)| t.RN + '>' + ss) as Path return distinct y.fullname as Name, y.RN as RN, length(sp) as genetic_distanced,left(Path,size(Path)-1) as Path order by genetic_distanced,Path";
        String r = gen.excelLib.queries_to_excel.qry_to_excel(cq, "X chr relatives", "X-chr relatives", 1, "", "1:#####;2:#####", "", true,"UDF: return gen.rel.X_chromosome_all_relatives(" + rn + ")\n\nAll relatives who may share X-chromosome",true);
        return r;
    }
}
