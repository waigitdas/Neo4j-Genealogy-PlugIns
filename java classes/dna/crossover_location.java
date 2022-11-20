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


public class crossover_location {
    @UserFunction
    @Description("In development: returns crossover locatio(s) of two segments")

    public String seg_overlap2(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
   
         { 
             
            List<String> lst = Arrays.asList("01:000072526:009415592","01:000072526:249222527","01:000752566:009415592","01:000752721:249218992","01:002494330:067146639","01:005902367:014900708","01:010924273:091821503","01:011660710:018210909","01:014080748:028209297","01:015651062:037028917");
        String s = compute_crossover2("01:005291357:016359380",lst);
 
         return "";
            }

    
    
    public static void main(String args[]) {
        List<String> lst = Arrays.asList("01:000072526:009415592","01:000072526:249222527","01:000752566:009415592","01:000752721:249218992","01:002494330:067146639","01:005902367:014900708","01:010924273:091821503","01:011660710:018210909","01:014080748:028209297","01:015651062:037028917");
        String s = compute_crossover2("01:005291357:016359380",lst);
        System.out.println(s);
    };
    
    
     public static String compute_crossover2(String tg_Indx,List<String> segs) 
    {
        String[] t =tg_Indx.split(":");
        String[] s = null;
        String x="";
        Long co1 = 0L;
        Long co2 = 0L;
        String b1="0";
        String b2="0";
        String b3="0";
        String b4="0";
        
        
        for (int i=0; i<segs.size(); i++){
            s = segs.get(i).split(":");
            co1 = 0L;
            co2 = 0L;
            
            if (Long.parseLong(s[1]) >= Long.parseLong(t[1])){b1="1"; }
            if (Long.parseLong(s[1]) >= Long.parseLong(t[2])){b2="1"; }
            if (Long.parseLong(s[2]) >= Long.parseLong(t[1])){b3="1"; }
            if (Long.parseLong(s[2]) >= Long.parseLong(t[2])){b4="1"; }
            String boolList= b1 + b2 + b3 + b4;
            //System.out.println(boolList);
            
            if (boolList.equals("0010"))
            {
                co1 = Long.parseLong(s[2]);
            }
            else if (boolList.equals("0011")) {
//do nothing}
            }
            else if (boolList.equals("1011")){
                co2 =Long.parseLong(s[1]);
                  }
            else if (boolList.equals("1010")){
                co1 =Long.parseLong(s[1]);
                co2 =Long.parseLong(s[2]);
                  }
            else {
                co1=-1L;
                co2=-1L;
            }
            //else {co1 =0L; }
            
//            if ( 
//                    Long.parseLong(s[1]) <= Long.parseLong(t[1]) && Long.parseLong(s[2]) <= Long.parseLong(t[1])) {co1=Long.parseLong(s[2]) ;
//            }
//           if ( 
//                    Long.parseLong(s[2]) >= Long.parseLong(t[1]) && Long.parseLong(s[1]) <= Long.parseLong(t[2])) {co2=Long.parseLong(s[2]) ;
//           }

                //x = x + t[1] + "\t" + t[2] + "\t" + segs.get(i) + "\t"  + b1  + "\t" + b2  + "\t" + b3  + "\t" + b4  + "\t" +  Long.parseLong(s[1])  + "\t" + Long.parseLong(s[2])  + "\t"  + Long.parseLong(t[1])  + "\t" + Long.parseLong(t[2])  + "\n";
                            x = x + i + "\t" +  t[1] + "\t" + t[2] + "\t" + segs.get(i) + "\t"  + boolList  + "\t" + co1 + "\t" + co2 + "\t"  + "\n";
        }//+  Long.parseLong(s[1])  + "\t" + Long.parseLong(s[2])  + "\t"  + Long.parseLong(t[1])  + "\t" + Long.parseLong(t[2]) 
     return x;
    }
}
        
