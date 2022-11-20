/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class get_hapmap_cm {
    @UserFunction
    @Description("Uses HapMap reference data to look up cm in a specific segment.")

    public Double hapmap_cm(
        @Name("chr")
            String chr,
        @Name("strt") 
            Long strt,
        @Name("end") 
            Long end
  )
   
         { 
             
       double cm =qry_hapmap(chr, strt, end);
         return cm;
            }

    
    
    public static void main(String args[]) {
        Double cm = qry_hapmap("01",11L,111111111L);
        System.out.println(cm);
    }
    
     public static Double qry_hapmap(String chr, Long strt, Long end) 
    {
    gen.neo4jlib.neo4j_info.neo4j_var();
    gen.neo4jlib.neo4j_info.neo4j_var_reload();
    

   try{   
    String cq = "MATCH (h:cHapMap) where h.chr=case when h.chr='0X' then 23 else toInteger('" + chr + "') end and " + end + ">=h.pos>=" + strt + " RETURN sum(h.icm) as cm";
     String[] cm = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n");
     return Double.parseDouble(cm[0]);
   }
   catch (Exception e) {return 9.0;}
    }
}
