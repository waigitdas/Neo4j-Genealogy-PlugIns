/**
 * Copyright 2022-2023 
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


public class YHG_deepest_level {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String deepest_level(
        @Name("YHG_List") 
            String YHG_List
  )
   
         { 
             
        return get_yhg(YHG_List);

            }

    
    
    public static void main(String args[]) {
//        String yl = gen.neo4jlib.neo4j_qry.qry_str("match (p:Person) where p.RN in [1,5242] return collect(p.iYHG )as yl");
        get_yhg("R-BY75093, R-M269");
        get_yhg("R-BY582,R-M269,R-Z18138,R-ZZ9_1");
    }
    
     public static String get_yhg(String yl) 
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
        String cq = "with [" + ss + "] as bls MATCH p=(b1:block)-[r:blockchild*0..99]->(b2:block) where b1.name in bls and b2.name in bls with [x in nodes(p)|x.name] as n with n order by size(n) desc return last(n) limit 1";
        String c[] = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n");
        System.out.println(c[0]);
        return c[0];
        }
    }
}
