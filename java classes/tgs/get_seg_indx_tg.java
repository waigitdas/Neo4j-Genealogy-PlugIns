/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;

import gen.neo4jlib.neo4j_qry;
import java.util.regex.Pattern;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class get_seg_indx_tg {
    @UserFunction
    @Description("get tgid for specified segment index")

    public Long get_segment_indx_tg(
        @Name("indx") 
            String indx
  )
   
         { 
             
        Long x = get_tg(indx);
         return x;
            }

    
    
    public static void main(String args[]) {
        //get_tg("08:154984:5031704");
    }
    
     public Long get_tg(String indx) 
    {
        Long x = 0L;
        try{
        String[] ss = indx.split(Pattern.quote(":"));
        String cq = "match (t:tg) where t.chr='" + ss[0] + "' and t.strt_pos<=" + Integer.valueOf(ss[2]) + " and t.end_pos>=" + Integer.valueOf(ss[1]) + " return t.tgid as tgid";
        x =Long.parseLong(gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n")[0]);
        return x;
        }
       catch (Exception e) {return x;}
        
    }
}
