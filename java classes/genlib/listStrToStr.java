/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.genlib;

    import java.util.List;


public class listStrToStr {

    
    
    
      public static String list_to_string(List<String> ListStr) {
        String s ="";
        for(String x: ListStr){
             s += x;
        }
        return s;
    }
}
