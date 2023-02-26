/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.mt_research;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class Starting_load_all_mt_dna_for_research {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String load_multiple_mt_dna_ref(
    
  )
   
         { 
             
        load_res_mt();
         return "";
            }

    
    
    public static void main(String args[]) {
        load_res_mt();
    }
    
     public static String load_res_mt() 
    {
        //load FTDNA haplotree
        gen.mt_research.upload_mito_ftdna_haplotree umt = new gen.mt_research.upload_mito_ftdna_haplotree();
        umt.upload_ftdna_mt_haplotree_research();
        
 
        gen.mt_research.tessellator tsl = new gen.mt_research.tessellator();
        tsl.tessellate_kits("all","E:/DAS_Coded_BU_2017/Genealogy/DNA/mt_haplotree_project/mt_DNA_sequences/",false);
 
//        //add rCRC reference sequence   
//        gen.mt_research.mt_add_reference_seq rcrsr = new gen.mt_research.mt_add_reference_seq();
//        rcrsr.mt_sequenc_map("rCRS", "E:/DAS_Coded_BU_2017/Genealogy/DNA/mt_haplotree_project/mt_DNA_sequences/human_mt_DNA_reference_sequence/","NC_012920.1");
//        
//        //add RSRS reference sequence
//        rcrsr.mt_sequenc_map("RSRS", "E:/DAS_Coded_BU_2017/Genealogy/DNA/mt_haplotree_project/mt_DNA_sequences/human_mt_DNA_reference_sequence/", "RSRS_reference_sequence.fasta");
//        
        
 
//        gen.mt_research.load_genbank_files lgf = new gen.mt_research.load_genbank_files();
//        lgf.load_genbank_file_kits_and_pos();
 
//        gen.mt_research.logan_database_get_kit_hg_mutations.main(args);
        
        return "";
        
        
    }
}
