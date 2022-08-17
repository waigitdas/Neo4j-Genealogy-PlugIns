/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.mss;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class segment_to_mss{
    @UserFunction
    @Description("Template used in creating new functions.")

    public String get_mss_for_segment(
        @Name("segment_indx") 
            String segment_indx
  )
   
         { 
             
        String s = get_mss(segment_indx);
         return s;
            }

    
    
    public static void main(String args[]) {
        //get_mss("20:003056686:010120173");
    }
    
     public String get_mss(String indx) 
    {
        String cq = "MATCH p=(m:MSS)-[r:ms_seg]->(s:Segment{Indx:'" + indx + "'}) RETURN apoc.coll.sort(collect(distinct m.fullname)) as mrca";
        String s = gen.neo4jlib.neo4j_qry.qry_str(cq).replace("[[","").replace("]]","").replace("\"", "");
        return s;
    }
}
