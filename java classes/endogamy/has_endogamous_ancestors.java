/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.endogamy;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class has_endogamous_ancestors {
    @UserFunction
    @Description("Does person have endogamy.")

    public String get_mrea(
        @Name("rn") 
            Long rn
  )
   
         { 
             
        String s = get_mrea_data(rn);
         return s;
            }

    
    
    public static void main(String args[]) {
        System.out.println(get_mrea_data(234160L));
    }
    
     public static String get_mrea_data(Long rn) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        String cq = "match p=(n:Person{RN:" + rn + "})-[r:father|mother*0..99]->(x) with x, x.RN as Name, length(p) as gen, [z in nodes(p)|z.RN] as op, '1' + reduce(srt ='', q IN nodes(p)|srt + case when q.sex='M' then '0' else '1' end ) AS Anh with x,Name,gen,'1' + right(Anh,size(Anh)-2) as Ahnen with Name as Person,gen,gen.rel.ahnentafel(Ahnen) as Ahnentafel order by Ahnentafel with Person,collect(Ahnentafel) as ahn with collect(Person) as rns,ahn where size(ahn)>1 with rns, ahn limit 2 return collect(rns[0])"; //,collect(ahn[0])";
        String s = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).replace("[","").replace("]","").replace("\n","");
         //System.out.println(s);
        
        return  s;  //.split(",") ;
    }
}
