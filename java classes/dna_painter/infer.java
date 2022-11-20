/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna_painter;

import gen.neo4jlib.neo4j_qry;
import java.io.File;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class infer {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String inferred_seg_from_two_lists(
        @Name("base_rn") 
            Long base_rn,
        @Name("close_rn") 
            Long close_rn,
        @Name("compare_rn") 
            Long compare_rn
  )
   
         { 
             
        String s = get_infer_seg_sets(base_rn,close_rn,compare_rn);
         return s;
            }

    
    
    public static void main(String args[]) {
        //get_infer_seg_sets(1L,210L,600L);
        //get_infer_seg_sets(1L,210L,582L);  DNA painter oly matches X
        //get_infer_seg_sets(1L,216L,2044L);
        //get_infer_seg_sets(1L,210L,582L);
    }
    
     public static String get_infer_seg_sets(Long base_rn,Long close_rn, Long compare_rn) 
    {
        String s = gen.neo4jlib.neo4j_qry.qry_to_csv("MATCH p=(m:DNA_Match)-[r:match_segment{p_rn:" + base_rn + ",m_rn:" + close_rn + "}]->(s:Segment)  RETURN toInteger(case when s.chr='0X' then 23 else s.chr end) as chromosome,toInteger(s.strt_pos) as start_location, toInteger(s.end_pos) as end_location,toFloat(r.cm) as centimorgans,toInteger(r.snp_ct) as snps");
        gen.neo4jlib.file_lib.writeFile(s,gen.neo4jlib.neo4j_info.Import_Dir + "base_match_segs.csv");
        
        String s2 = gen.neo4jlib.neo4j_qry.qry_to_csv("MATCH p=(m:DNA_Match)-[r:match_segment{p_rn:" + close_rn + ",m_rn:" + compare_rn + "}]->(s:Segment) RETURN toInteger(case when s.chr='0X' then 23 else s.chr end) as chromosome,toInteger(s.strt_pos) as start_location, toInteger(s.end_pos) as end_location,toFloat(r.cm) as centimorgans,toInteger(r.snp_ct) as snps");
        gen.neo4jlib.file_lib.writeFile(s2,gen.neo4jlib.neo4j_info.Import_Dir + "close_compare_segs.csv");
        
        s2 =gen.neo4jlib.neo4j_qry.qry_to_csv("MATCH p=(a:Avatar)-[r:avatar_segment{p_rn:1,m_rn:210,compare_rn:600}]->(s:Segment)  with distinct s.chr as c,s.strt_pos as s,s.end_pos as e,r.cm as cm return c,s,e,cm order by c,s,e");
        gen.neo4jlib.file_lib.writeFile(s2,gen.neo4jlib.neo4j_info.Import_Dir + "avatar_segs.csv");
        
        //Desktop.getDesktop().open(new File(gen.neo4jlib.neo4j_info.Import_Dir + "base_match_segs.csv"));  
        //Desktop.getDesktop().open(new File(gen.neo4jlib.neo4j_info.Import_Dir + "close_compare_segs.csv"));  
        
        return "Files are in the import directory";
    }
}
