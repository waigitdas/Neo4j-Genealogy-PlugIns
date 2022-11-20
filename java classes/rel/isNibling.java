/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class isNibling {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String nibling(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
   
         { 
             
        String s = isnibling(rn1,rn2);
         return s;
            }

    
    
    public static void main(String args[]) {
//        isnibling(1L,216L);
//        isnibling(216L,1L);
//        isnibling(1L,2161L);
    }
    
     public String isnibling(Long rn1, Long rn2) 
    {
        String cq = "match path=(p1:Person)-[rp1:father|mother*0..2]->(mrca:Person)<-[rp2:father|mother*0..2]-(p2:Person) where p1.RN=" + rn1 + " and p2.RN=" + rn2 + " with distinct size(rp1) as pl1 return case when pl1=1 then 'true' else 'false' end  as isNibling"; 
        String b = "false";
        try{
        b= gen.neo4jlib.neo4j_qry.qry_str(cq).replace("\"", "").replace("[","").replace("]","");
        }
        catch(Exception e){}
        System.out.println(b);
        return b;
    }
}
