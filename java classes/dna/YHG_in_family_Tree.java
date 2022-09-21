/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class YHG_in_family_Tree {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String udf_name_seen_in_listing(
        @Name("rn_propositus") 
            Long rn_propositus
  )
   
         { 
             
        get_yhgs(rn_propositus);
         return "";
            }

    
    
    public static void main(String args[]) {
        get_yhgs(1L);
    }
    
     public static String get_yhgs(Long rn) 
    {
        String cq ="match path1=(p:Person{RN:" + rn + "})-[r:father|mother*0..10]->(a:Person) with a where a.sex='M' with collect(a.RN) as rns match path2 =(p2:Person)-[r:father|mother*0..10]->(a2:Person) where a2.RN in rns with p2,collect(p2.RN) as rns2 match (d:DNA_Match) where d.RN in rns2 and d.YHG is not null with p2.surname as surname , d.YHG as Y_haplogroup,apoc.coll.sort(collect(d.fullname)) as Y_tester return surname,Y_haplogroup,size(Y_tester) as tester_ct,Y_tester order by surname,Y_tester";
        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "family_tree_Y_haplogroups", "YHGs", 1, "","","", true, "cypher query:\n" + cq, true);
        
        return "completed";
    }
}
