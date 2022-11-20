/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class get_seg_tg {
    @UserFunction
    @Description("Template used in creating new functions.")

    public Long get_segment_tg(
        @Name("chr") 
            String chr,
        @Name("strt_pos") 
            Long strt_pos,
        @Name("end_pos") 
            Long end_pos
  )
   
         { 
             
        Long x = get_tg(chr,strt_pos,end_pos);
         return x;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public Long get_tg(String chr,Long strt_pos,Long end_pos) 
    {
        Long x = 0L;
        try{
        String cq = "match (t:tg) where t.chr='" + chr + "' and t.strt_pos<=" + strt_pos + " and t.end_pos>=	" + end_pos + " return t.tgid as tgid";
        x =gen.neo4jlib.neo4j_qry.qry_long_list(cq).get(0);
        return x;
        }
        catch (Exception e) {return x;}
        
    }
}
