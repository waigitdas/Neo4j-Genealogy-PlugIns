/**
 * Copyright 2021 
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

    public double hapmap_cm(
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
        // TODO code application logic here
    }
    
     public double qry_hapmap(String chr, Long strt, Long end) 
    {
    gen.neo4jlib.neo4j_info.neo4j_var();
    gen.neo4jlib.neo4j_info.user_database="hapmap";
   try{
    String cq = "match (h:HapMap) where h.chr='" + chr + "' and h.strt_pos>=" + strt + " and h.strt_pos<=" + end + " with h order by h.cm with collect(h.cm) as cms return cms[size(cms)-1]-cms[0] as cm";
     String[] cm = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n");
     String[] cm2 = cm[0].split(",");
     gen.neo4jlib.neo4j_info.neo4j_var_reload();
     return Double.parseDouble(cm2[0]);
   }
   catch (Exception e) {return 0.0;}
    }
}
