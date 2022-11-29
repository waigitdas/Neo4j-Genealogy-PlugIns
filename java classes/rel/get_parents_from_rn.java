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


public class get_parents_from_rn {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String get_parents(
        @Name("rn") 
            Long rn
  )
   
         { 
             
        String rns =get_par(rn);
         return rns;
            }

    
    
    public static void main(String args[]) {
        get_par(1L);
    }
    
     public static String get_par(Long rn) 
    {
        String s[] = gen.neo4jlib.neo4j_qry.qry_to_csv("match p=(n:Person{RN:" + rn + "})-[r:father|mother]->(x) return collect(x.RN) as rns").replace("[","").replace("]","").split("\n")[0].split(",");
        
        
        //Long[] rns = new Long[s.length];
        //String rns = new String[s.length];
        String rns="";
        for (int i=0; i<s.length; i++)
        {
            rns = rns + s[i].strip();
            if (i<s.length-1) 
            {
            rns = rns + "," ;
        }
        }
        return rns;
    }
}
