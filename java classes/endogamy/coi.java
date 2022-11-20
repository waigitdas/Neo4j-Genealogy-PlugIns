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


public class coi {
    @UserFunction
    @Description("Coeffient of Imbreeding.")

    public Double coefficient_of_inbreeding(
        @Name("rn") 
            Long rn
  )
   
         { 
             
        Double coi = get_coi(rn);
         return coi;
            }

    
    
    public static void main(String args[]) {
//        get_coi(1L);
//        get_coi(4L);
//        get_coi(5L);
//        get_coi(12L);
//        get_coi(13L);
//        get_coi(18L);
//        get_coi(19L);
//        get_coi(42L);
//        get_coi(1050L);
//        get_coi(1058L);
//        get_coi(1047L);
//        get_coi(1051L);
           get_coi(13L);
    }
    
     public static Double get_coi(Long rn) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        try{
            //calculated the coefficients of relationships for parents and then divide by 2.
            String cq = "Match (p:Person{RN:" + rn + "}) match (u:Union) where u.uid=p.uid  with gen.rel.compute_cor(u.U1,u.U2)/2 as coi return coi";
                    //"match p=(n:Person{RN:" + rn + "})-[r:father|mother*0..99]->(x) with x,x.fullname + ' [' + x.RN + '] (' + left(x.BD,4) + '-' + left(x.DD,4) + ')' as Name, length(p) as gen, [z in nodes(p)|z.RN] as op, '1' + reduce(srt ='', q IN nodes(p)|srt + case when q.sex='M' then '0' else '1' end ) AS Anh with x,Name,gen,'1' + right(Anh,size(Anh)-2) as Ahnen, gen.graph.get_ordpath(op) as op optional match (d:Person{RN:x.RN}) with Name as Person,collect(distinct gen) as gen,collect(,gen.rel.ahnentafel(Ahnen)) as Ahnentafel,x with Person,x,apoc.coll.sort(Ahnentafel) as Ahnentafel,apoc.coll.sort(gen) as gen where size(Ahnentafel)>1 with Person,Ahnentafel,gen,gen.rel.compute_cor(" + rn + ",x.RN) as cor return  sum(cor) as coi ";
        
            Double coi1 = Double.parseDouble(gen.neo4jlib.neo4j_qry.qry_str(cq).replace("[","").replace("]",""));
            System.out.println(gen.gedcom.get_family_tree_data.getPersonFromRN(rn,false) + "\t\t\t" + coi1);
            return coi1;
        }
        catch(Exception e){
            return 0.0;
        }

    }
}
