/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import java.util.regex.Pattern;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class two_seg_overlap {
    @UserFunction
    @Description("cM of overlapof two segments")

    public Double segment_overlap(
        @Name("seg1") 
            String seg1,
        @Name("seg2") 
            String seg2
  )
   
         { 
             
        Double cm = get_overlap(seg1,seg2);
         return cm;
            }

    
    public static void main(String args[]) {
        //get_overlap("01:018563889:036636835","01:018839394:022952823");
        get_overlap("01:241458102:245417562","01:244652199:249218992");
    }
    
     public static Double get_overlap(String seg1,String seg2) 
    {
        gen.dna.get_hapmap_cm hp = new gen.dna.get_hapmap_cm();
        
        String c1[] = seg1.split(Pattern.quote(":"));
        String c2[] = seg2.split(Pattern.quote(":"));
        Long s = Math.max(Long.parseLong(c1[1]), Long.parseLong(c2[1]));
        Long e = Math.min(Long.parseLong(c1[2]), Long.parseLong(c2[2]));
        Double cm = hp.hapmap_cm(c1[0],s,e);
                
        return cm;
    }
}
