/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.graph;

import gen.neo4jlib.neo4j_qry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

public class ordpath {
    @UserFunction
    @Description("Creates ORDPATH bitstring from list of RNs.")

    public String get_ordpath(
        @Name("dewey") 
            List<Long> dewey
 
    
)
   
         { 
             
        String s =get_op(dewey);
         return s;
            }

    
    
    public static void main(String args[]) {
//        List<Long> d = new ArrayList<Long>(Arrays.asList(33454L, 5427L, 5467L, 5443L, 5436L, 5565L, 5600L, 33791L));
//        String op = get_op(d);
//        System.out.println(op);
    }
    
     public  static String get_op(List<Long> dewey) 
    {
        String op="";
        
        for (int i=0; i<dewey.size(); i++){
        op = op + getLiOi(dewey.get(i)) ;
                 
    }
        return op;
    }
     
     public static String getLiOi(Long deweyItem){
         String LiOi = "01,3,0,7\n" +
"001,3,-8,-1\n" +
"00011,4,-24,-9\n" +
"00010,6,-88,-25\n" +
"100,4,8,23\n" +
"101,6,24,87\n" +
"000010,12,-4440,-325\n" +
"0000011,16,-69976,-4441\n" +
"0000010,32,-4300000000,-69977\n" +
"0000001,48,-280000000000000,-4300000000\n" +
"1100,8,88,343\n" +
"1101,12,344,4439\n" +
"11100,16,4440,69976\n" +
"11101,32,69976,4300000000\n" +
"11110,48,4300000000,280000000000000";
         
         gen.rel.getHexDec b2 = new gen.rel.getHexDec();
         String X = "";
         String[] li= LiOi.split("\n");
         for (int i=0; i<li.length; i++){
             String[] lf = li[i].split(",");
             for (int j=0; j<lf.length; j++){
             if (deweyItem >= Long.parseLong(lf[2]) && deweyItem <= Long.parseLong(lf[3]) )
             {
                 X = b2.getBase16(deweyItem - Long.parseLong(lf[2]));
                 //X = gen.genlib.handy_Functions.lpad(X,Integer.parseInt(lf[1]),"0");
                 X = lf[0] + X;
             }
                 }
      
         }
        return X;
     }
     
}
