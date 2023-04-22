/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class mt_block_list {
    @UserFunction
    @Description("Template used in creating new functions.")

    public Long block_list_counts(
        @Name("block_list") 
            List<String> block_list
  )
   
         { 
             
        Long i = get_list_ct(block_list);
         return i;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public static Long get_list_ct(List<String> ls)
    {
        Long ct = 0L;
        for(int i=0;i< ls.size(); i++)
        {
            String v = ls.get(i);
            if(v!=v.replace("*","") || v!=v.replace("!","") || v!=v.replace("-x",""))
            {
                ct = ct + 1;
            }
        }
        return ct;
    }
}
