/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class mtHG_deepest_level1 {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String mt_deepest_level(
        @Name("YHG_List") 
            String YHG_List
  )
   
         { 
             
        return get_mthg(YHG_List);

            }

    
    
    public static void main(String args[]) {
//        String yl = gen.neo4jlib.neo4j_qry.qry_str("match (p:Person) where p.RN in [1,5242] return collect(p.iYHG )as yl");
        //get_mthg("R-BY75093, R-M269");
    }
    
     public static String get_mthg(String yl) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String s[] = yl.split(",");
        String ss="";
            
        if (s.length<2){ return yl; }
        else {
            String q = "\"";
        
            for (int i=0; i<s.length; i++)
            {
                ss = ss + q + s[i].strip() + q;
                if (i<s.length-1){ss = ss +",";}
            }
        //gets ordered list of all hgs paths, sorts by size of path, returns last element of the first path - deepest element in the haplotree
        String cq = "with [" + ss + "] as bls MATCH p=(b1:mt_block)-[r:mt_block_child*0..99]->(b2:mt_block) where b1.name in bls and b2.name in bls with [x in nodes(p)|x.name] as n with n order by size(n) desc return last(n) limit 1";
                //"with [" + ss + "] as bls MATCH p=(b1:mt_block)-[r:mt_block_child*0..99]->(b2:mt_block) where b1.name in bls and b2.name in bls with [x in nodes(p)|x.name] as n with n where size(n)>1 return last(n) as nl";
        String c[] = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n");
        return c[0];
        }
    }
}
