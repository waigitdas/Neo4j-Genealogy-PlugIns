/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class x_genetic_distance {
    @UserFunction
    @Description("Determines if shared X-xhromosome segment possible and, if so, the genetic distance.")

    public Long x_chr_min_genetic_distance(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
   
         { 
             
        Long i = get_dist(rn1,rn2);
         return i;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public Long get_dist(Long rn1,Long rn2) 
    {
        Long ii = 0L;
        try{
        String cq = "match p=(p1:Person{RN:" + rn1 + "})-[:father|mother*0..99]->(x) with x,reduce(srt2 ='', q IN nodes(p)| srt2 + q.sex) AS c where c=replace(c,'MM','') with collect (x.RN) as RNs match m=(p2:Person)<-[:father|mother*0..99]-(y:Person{RN:" + rn2 + "}) where p2.RN in RNs with y, reduce(srt3 ='', s IN nodes(m)| srt3 + s.sex) AS cc with y where cc=replace(cc,'MM','') match sp=shortestpath ((y)-[:father|mother*0..99]-(z:Person{RN:" + rn1 + "}) ) with y,sp,reduce(ss='', t in nodes(sp)| t.RN + '>' + ss) as Path return distinct y.fullname as Name, y.RN as RN, length(sp) as genetic_distance,Path order by genetic_distance,Path";
        ii = Long.parseLong(gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n")[0].split(",")[2]);
        return ii;
        }
        catch (Exception e) {return ii;}
    }
}
