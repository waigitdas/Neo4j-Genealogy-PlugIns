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
import java.util.regex.Pattern;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class overlapping_segs_in_three_lists {
    @UserFunction
    @Description("Computes overlapping shared segments in two lists of segments.")

    public List<String> overlapping_segments_in_Two_list(
        @Name("seg_list1") 
            List<String> seg_list1,
        @Name("seg_list2") 
            List<String> seg_list2,
       @Name("seg_list3") 
            List<String> seg_list3,
        @Name("overlap") 
            Long overlap
  )
   
         { 
             
        List<String> s = get_segs(seg_list1,seg_list2,seg_list3,overlap);
         return s;
            }
  
    public static void main(String args[]) {
        List<String> s1 = Arrays.asList("01:092305250:168569112", "01:180495670:249218992", "01:002494330:067146639", "02:031387957:243068403", "02:000018674:007454360", "03:000061495:023820938", "03:027953634:197838262", "04:013787277:113003174", "04:113044027:180250708", "05:000038139:169524020", "06:011764205:023972386", "06:042099371:170919470", "07:033984313:076991935", "07:078315591:159122659", "08:002060411:146293414", "09:134246225:141066491", "09:013124767:119922556", "10:004044541:135447971", "11:000198510:134934063", "12:000191619:133766122", "13:019121950:115103529", "14:019327823:107287663", "15:020161372:102376655", "16:000088165:090170495", "17:000008547:081046413", "18:035078859:078007784", "20:000063799:059776834", "21:014601415:030377104", "21:042860136:048090629", "22:016869887:051213613", "0X:143626006:154916845", "0X:044510950:115364226", "0X:005874617:012319092");
      
         List<String> s2 = Arrays.asList("06:023442310:036053864", "09:079993871:088141836");
         List<String> s3 = Arrays.asList("09:079163629:088101397", "18:069541362:078010620");
         
        // List<String> l = get_segs(s1,s2,s3,100L);
         
                         //"04:013787277:113003174", "04:113044027:180250708", "05:000038139:169524020", "06:011764205:023972386", "06:042099371:170919470", "07:033984313:076991935", "07:078315591:159122659", "08:002060411:146293414", "09:134246225:141066491", "09:013124767:119922556", "10:004044541:135447971", "11:000198510:134934063", "12:000191619:133766122", "13:019121950:115103529", "14:019327823:107287663", "15:020161372:102376655", "16:000088165:090170495", "17:000008547:081046413", "18:035078859:078007784", "20:000063799:059776834", "21:014601415:030377104", "21:042860136:048090629", "22:016869887:051213613", "0X:143626006:154916845", "0X:044510950:115364226", "0X:005874617:012319092"); 
 
                                         //"04:057108271:108528923", "04:118310450:156758741", "05:173036161:177222590", "05:051809445:064290004", "10:105213765:130997640", "12:118289948:129178435", "15:093916392:098464120", "22:047102722:049290881");
        //

    }
    
    
     public List<String> get_segs(List<String> segs1, List<String> segs2, List<String> segs3, Long overlap) 
    {
        String s = "";
        String[] ca =null;
        String[] cb =null;
       String[] cc =null;
        String Q = "\"";
        List<String> lst = new ArrayList < String > ();;

        List r = null;
        int ct = 0;
        for (int i = 0; i < segs1.size(); i++)  
        {
            ca = segs1.get(i).split(Pattern.quote(":"));
            
            for (int j = 0; j < segs2.size(); j++)  
            {
                cb = segs2.get(j).split(Pattern.quote(":"));
 
                if (ca[0].equals(cb[0]) && Long.parseLong(ca[1])<=Long.parseLong(cb[2]) && Long.parseLong(ca[2])>=Long.parseLong(cb[1]) ) 
                {    
 
                       for (int k = 0; k < segs3.size(); k++) {
                            cc = segs3.get(k).split(Pattern.quote(":"));
                            //AB, AC & BC all have overlapping segments
                             if (ca[0].equals(cb[0]) && Long.parseLong(ca[1])<=Long.parseLong(cb[2]) && Long.parseLong(ca[2])>=Long.parseLong(cb[1]) && 
                                 ca[0].equals(cc[0]) && Long.parseLong(ca[1])<=Long.parseLong(cc[2]) && Long.parseLong(ca[2])>=Long.parseLong(cc[1]) && 
                                 cb[0].equals(cc[0]) && Long.parseLong(cb[1])<=Long.parseLong(cc[2]) && Long.parseLong(cb[2])>=Long.parseLong(cc[1])) 
                                {        
                                
                                if (s==null || s.equals(s.replace(segs1.get(i),""))) { //not yet added
                                         lst.add(segs1.get(i));
                                         s = s +  Q + segs1.get(i) + Q + ",";
                                         ct = ct +1;
                                    }
                                if (s==null || s.equals(s.replace(segs2.get(j),""))) { //not yet added
                                         lst.add(segs2.get(j));
                                         s = s +  Q + segs2.get(j) + Q + ",";
                                         ct = ct +1;
                                    }
                                if (s==null || s.equals(s.replace(segs3.get(k),""))) { //not yet added
                                         lst.add(segs3.get(k));
                                         s = s +  Q + segs3.get(k) + Q + ",";
                                         ct = ct +1;
                                    }
                               }
                       
                      
                       } //k
                         
                
                }
                       } //j
                }  //i
 
               return lst;
    }
    
}
