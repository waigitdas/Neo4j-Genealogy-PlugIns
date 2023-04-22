/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.genlib;

import gen.neo4jlib.neo4j_qry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class string_to_int_list {
    @UserFunction
    @Description("Template used in creating new functions.")

    public List<Long> covertStrToIntList(
        @Name("str") 
            String str
  )
   
         { 
             
        List<Long> ll = convert(str);
        return ll;
            }

    
    
    public static void main(String args[]) {
        List<Long> l = convert("[299,156,151]");
        System.out.println(l);
    }
    
     public static List<Long> convert(String s) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String n[] = s.replace("[","").replace("]","").split(",");
        ArrayList<Long> ar = new ArrayList<Long>();
        
        for (int i=0; i<n.length; i++)
        {
            ar.add(Long.parseLong(n[i]));
        }
        List<Long> d = ar ; // new ArrayList<Long> (Arrays.asList(33454L, 5427L, 5467L, 5443L, 5436L, 5565L, 5600L, 33791L));
 
        return ar;
    }
}
