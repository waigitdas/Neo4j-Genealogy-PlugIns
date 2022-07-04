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


public class overlap_shared_segments {
    @UserFunction
    @Description("Calculates overlapping segments and mrca of a list of three DNA_Matches")

    public Object shared_overlapping_segments_for_list(
        @Name("name_list") 
            List name_list,
        @Name("overlap") 
            Long overlap
  )
   
         { 
             
        Object s = get_segs(name_list,overlap);
         return s;
            }
    
    
//    public static void main(String args[]) {
//        // TODO code application logic here
//         List<String> s3 = Arrays.asList("David A Stumpf", "Diane Collins", "Paul Breevaart");
//        get_segs(s3,100L);
//    }
    
     public static Object get_segs(List names, Long overlap) 
    {
//        gen.neo4jlib.neo4j_info.neo4j_var();
//        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        String ar ="";
        List<Object> ls = null;
        String Q = "\"";
       String cq = "";
       
       try{
        for (int i = 0; i < names.size(); i++) {
        ar = ar + Q + names.get(i) + Q;
        if (i<names.size()-1) {ar = ar + ", ";}
        }
       }
       catch(Exception ex) {return ex.getMessage();}
        
        try{
           cq = "with [" + ar + "] as x match (m:DNA_Match)-[r:match_segment]->(s:Segment) where r.p in x and r.m in x and r.p<>r.m and r.cm>=7 and r.snp_ct>=500 with x,case when r.p< r.m then r.p + ':' + r.m else r.m +':' + r.p end as match_pair,collect(distinct s.Indx) as sc return sc as x";
 
        
        ls = gen.neo4jlib.neo4j_qry.qry_obj_list(cq);
        }
        catch(Exception e) {return "error here\n\n" + cq + "\n\n"  + e.getMessage();}
        
//        String s = "";
//        String[] cj =null;
//        String[] ck =null;
//        
        gen.dna.overlapping_segs_in_three_lists gdna = new gen.dna.overlapping_segs_in_three_lists();
//        List<String> AB = gdna.get_segs((List<String>) ls.get(0), (List<String>) ls.get(1),100L);
//        List<String> AC = gdna.get_segs((List<String>) ls.get(0), (List<String>) ls.get(2),100L);
//        List<String> BC = gdna.get_segs((List<String>) ls.get(1), (List<String>) ls.get(2),100L);
//        List<String> ABAC = gdna.get_segs(AB, AC,100L);
        
List<String> Segs_Final ;
try{
    Segs_Final = gdna.get_segs((List<String>) ls.get(0), (List<String>) ls.get(1), (List<String>) ls.get(2), 100L);
}
catch (Exception e) {return "****" + e.getMessage() + "\n\n" + ls;}
        
//       List<String> ABAC = gdna.get_segs(AB,AC,100L);
//       List<String> Segs_Final = gdna.get_segs(ABAC,BC,100L);
//       List<String> ACBC = gdna.get_segs(AC,BC,100L);
//              
//       List<String> ABACABBC = gdna.get_segs(ABAC,ABBC,100L);
//       List<String> ABACACBC = gdna.get_segs(ABAC,ACBC,100L);
//
//      List<String> Segs_Final = gdna.get_segs(ABACABBC,ABACACBC,100L);

        return Segs_Final;
        

    }
}
