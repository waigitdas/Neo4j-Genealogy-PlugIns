/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.excellib;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class cypher_to_excel {
    @UserFunction
    @Description("In development.")

    public String cypher_qry_to_excel(
        @Name("qry") 
            String qry
  )
   
         { 
             
        get_excel(qry);
         return "";
            }

    
    
    public static void main(String args[]) {
        get_excel("MATCH (p:Person) where p.coi >0 RETURN p.fullname as name,p.RN as RN,p.BD as BD, p.DD as DD, p.coi as coi, p.coi_gen as coi_gen order by coi desc, name limit 25");
    }
    
     public static String get_excel(String qry) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String c[] = gen.neo4jlib.neo4j_qry.qry_to_csv(qry).split("\n");
        
        System.out.println(qry);
        return "";
    }
}
