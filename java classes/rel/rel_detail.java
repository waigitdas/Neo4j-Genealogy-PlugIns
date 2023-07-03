/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class rel_detail {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String relationship_detail(
  )
   
         { 
             
        get_rel_detail();
         return "";
            }

    
    
    public static void main(String args[]) {
        get_rel_detail();
    }
    
     public static String get_rel_detail() 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        String cq = "MATCH p=(d1:DNA_Match)-[r:match_by_segment]->(d2:DNA_Match) where r.rel is not null RETURN  d1.RN as GEDOM_id1,d1.sex,d1.BD,d1.DD,d2.RN as GEDCOM_id2,d2.sex, d2.BD,d2.DD, gen.rel.parental_path_to_mrca(d1.RN, d2.RN) as parental_sides,r.cm as at_cM,r.longest_cm,r.shortest_cm, r.seg_ct as at_seg_ct,r.x_cm,  r.x_seg_ct,r.x_longest_cm,r.x_shortest_cm, r.x_gen_dist, r.rel as rel_from_gedcom,r.cor as coef_relationship,r.ps_cor as parental_side_cor,r.side_paths1,r.side_paths2,  d1.coi as coef_inbredding1 , d2.coi as coef_inbredding2 order by r.cor desc";
        String fn = "relationship_detail_" + gen.genlib.current_date_time.getDateTime() + ".csv";
        gen.neo4jlib.neo4j_qry.qry_to_csv(cq, fn);
//        try {
//            Desktop.getDesktop().open(new File gen.neo4jlib.neo4j_info.Import_Dir + fn));
//        } catch (IOException ex) {
//            Logger.getLogger(rel_detail.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "relationship_detail", "relationships", 1, "", "", "", true, "Cypher query:\n" + cq, true);
        return "";
    }
}
