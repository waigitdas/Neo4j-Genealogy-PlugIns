/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.graph;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class descendant_ordering {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String add_descenant_order(
        @Name("query") 
            String query,
        @Name("gen_col") 
            Long gen_col
  )
   
         { 
             
        String s = get_orders(query,gen_col);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_orders(String cq, Long gen_col) 
    {
        String csv = gen.neo4jlib.neo4j_qry.qry_to_csv(cq);
        //receives ORDPATH file sorted by generation as csv file  
        //parse csv into rows
        String c[] = csv.split("\n");
        String s = "";
        int curr_gen = 0;
        int ct_in_gen = 0;
        int gc = 0;
        
        for (int i=0; i<c.length; i++){
            //parse row into fields
            String[] rw = c[i].split(",");
            gc = gen_col.intValue();
            //if generation has changed, re-set varianles
            if (Integer.parseInt(rw[gc])!=curr_gen){
                ct_in_gen= 0;
                curr_gen=Integer.parseInt(rw[gc]);
            }
            
            //increment count in the generation
            ct_in_gen = ct_in_gen + 1;
            String rs = "";
            for (int j=0; j<rw.length; j++){
                //re-create row with the position in the generation
                //if (j==0){rw[j]=gen.genlib.handy_Functions.lpad(rw[j], Integer.parseInt(rw[gc])*2,"_");} 
                rs = rs + rw[j] ;
                if (j == gc){rs = rs + ", " + ct_in_gen;}
                if (j<rw.length-1){
                    rs = rs + ", ";
                } 
                 
            }  //next column
            s = s + rs  + "\n";
        } //next row
        gen.neo4jlib.file_lib.writeFile(s, gen.neo4jlib.neo4j_info.Import_Dir + "ordered_Descendant_tree.csv");
        return s;
    }
}
