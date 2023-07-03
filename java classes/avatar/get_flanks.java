/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.avatar;

import gen.neo4jlib.neo4j_qry;
import java.util.regex.Pattern;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class get_flanks {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String get_segment_flanks(
        @Name("seg1") 
            String seg1,
        @Name("seg2") 
            String seg2
  )
   
         { 
             
        String r = find_flanks(seg1,seg2);
         return r;
            }

    
    
    public static void main(String args[]) {
        String f = find_flanks("06:000169141:022491413", "06:014131804:021142478");
        System.out.println(f);
    }
    
     public static String find_flanks(String seg1, String seg2) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        gen.dna.get_hapmap_cm hp = new gen.dna.get_hapmap_cm();
        gen.genlib.handy_Functions hf = new gen.genlib.handy_Functions();
      
//GET CHR, START AND END POST BY PARSING SEGMENT iNDX
        String c1[] = seg1.replace("\"","").split(Pattern.quote(":"));
        String c2[] = seg2.replace("\"","").split(Pattern.quote(":"));
        
        //convert string vvalues to numbers
        //get maximum start position and minimum end position in the two segments
        Long max_s = Math.max(Long.parseLong(c1[1]), Long.parseLong(c2[1]));
        Long min_e = Math.min(Long.parseLong(c1[2]), Long.parseLong(c2[2]));
        //get shared_cM of overlapping segment
        Double cm_shared = hp.hapmap_cm(c1[0],max_s,min_e);
        
        //get segment shared by base and close relative
        //will use this to determine flanking regions
        String seg_shared = c1[0] + ":" + hf.lpad(String.valueOf(max_s).strip(),9,"0") + ":" + hf.lpad(String.valueOf(min_e).strip(),9,"0") ;
        String seg_left = "";
        String seg_right = "";
        Double left_cm = 0.0;
        Double right_cm = 0.0;
        
        //pad estart and end positions for constructing segments that sort properly
        String max_ss =  String.valueOf(max_s).strip();
        String min_ee = String.valueOf(min_e).strip();
        max_ss = hf.lpad(max_ss, 9, "0");
        min_ee = hf.lpad(min_ee, 9, "0");
        String method = "";
        String delimiter ="|";
        
        //left flanks
        //first segment has larger start position
        //thus left flank is from second segment
        if (max_s.compareTo(Long.parseLong(c1[1]))<1)  
        {
              seg_left = c1[0] + ":" + c2[1]+ ":" + c1[1] ;
              left_cm = hp.hapmap_cm(c1[0], Long.parseLong(c2[1]), Long.parseLong(c1[1]));
              method= method + "1 ";
            }
        //second segment has larger start position
        //thuss, left flank is from first segment
        if (max_s.compareTo(Long.parseLong(c2[1]))<1)
                {
                seg_left = c2[0]+ ":" + c1[1] + ":" + c2[1] ;
                left_cm = hp.hapmap_cm(c2[0], Long.parseLong(c1[1]), Long.parseLong(c2[1]));
               method = method + "2 ";     
                }
        
        //right flanks
        //first segment end position has smaller end pos
        //thus, right flank is in second segment
        if (min_e.compareTo(Long.parseLong(c1[2]))<1)  //right flank on c1
        {
            seg_right = c1[0] + ":" + c1[2] + ":" + c2[2];
            right_cm = hp.hapmap_cm(c1[0], Long.parseLong(c1[2]), Long.parseLong(c2[2]));
            method = method + "3 ";
        }

        //second segment end position has smaller end pos
        //thus, right flank is in first segment
        if (min_e.compareTo(Long.parseLong(c2[2]))<1)  //right flank on c2
        {
            seg_right = c2[0] + ":" + c2[2] + ":"  + c1[2];
            right_cm = hp.hapmap_cm(c2[0], Long.parseLong(c2[2]), Long.parseLong(c1[2]));
            method = method = method + "4 ";
        } 
            
        String q = "\"";
        return  cm_shared + delimiter +  seg_shared +  delimiter +left_cm + delimiter + seg_left +  delimiter + right_cm + delimiter + seg_right +  delimiter + method.strip();
        
    }
}
