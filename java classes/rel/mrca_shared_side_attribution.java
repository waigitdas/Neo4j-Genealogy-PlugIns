/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class mrca_shared_side_attribution {
    @UserFunction
    @Description("family side of attributee with two matches.")

    public String mrca_side_attribution(
        @Name("rn_attributee") 
            Long rn_attributee,
        @Name("rn2") 
            Long rn2,
        @Name("rn3") 
            Long rn3,
        @Name("gen") 
            Long gen
  )
   
         { 
             
        return get_attribution(rn_attributee, rn2, rn3, gen);
            }

    
    
    public static void main(String args[]) {
        String s = get_attribution(8L,1L,2938L, 5L);
        System.out.println(s);
        
    }
    
     public static String get_attribution(Long rn1, Long rn2, Long rn3, Long g) 
    {
            String cq = "match (c2:Person{RN:" + rn1 + "})-[r:father|mother*0.." + g + "]->(MRCA:Person)<-[:father|mother*0.." + g + "]-(c3:Person) where c3.RN in [" + rn2 + "," + rn3 + "]  with r[0] as rf where rf is not null with case when Type(rf) ='mother' then 'maternal' else 'paternal' end as rr return distinct rr"; 
                   // "match (c2:Person)-[r:father|mother*0.." + g + "]->(MRCA:Person)<-[:father|mother*0.."+ g + "]-(c3:Person) where c2.RN = " + rn1 + " And c3.RN in [" + rn2 + "," + rn3 + "] with r[0] as rf with case when Type(rf) ='mother' then 'maternal' else 'paternal' end as rr where rf is not null return distinct rr";
        try{
            return gen.neo4jlib.neo4j_qry.qry_str(cq).replace("[","").replace("]","").replace("\"","");
        }
        catch(Exception e){return "-";}
            }
  
}
