/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.mt_research;

import gen.load.web_file_to_import_folder;
import gen.neo4jlib.neo4j_qry;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.*;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class entrez_mt_hg_from_genbank1 {
    @UserFunction
    @Description("https://www.wai.md/post/ordpath-computing-genealogy-descendancy-trees")

    public String mt_haplogroup_from_sequence(
        @Name("seq_filepath") 
            String seq_filepath
  )
   
         { 
             
        //get_fastas(seq_filepath);
         return "";
            }

    
    
    public static void main(String args[]) {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
//        FileWriter fwerr = null;
//        FileWriter fwtracker = null;
//        Boolean b = false;
//        String h2 ="";
//        long tmprev = System.currentTimeMillis();
//        
//       int strt = 58400;
//       int incr = 100;
//       int end = strt + incr;
//       int kit_ct = 0;
//       int rmax = 63000;
//       String fnerr = "E:/DAS_Coded_BU_2017/Genealogy/DNA/mt_haplotree_project/mt_DNA_sequences/entrez/errors.csv";

       String hg="";
        String dir = "E:/DAS_Coded_BU_2017/Genealogy/DNA/mt_haplotree_project/mt_DNA_sequences/entrez/";
        //get file list and export csv files for Neo4j import'
       List<File> fasta = new ArrayList<File>();
//        String fnout = gen.neo4jlib.neo4j_info.Import_Dir + "genbank_mt_seq.csv";
//        FileReader fr = null;
//        FileWriter fw =null;
//        int pos_ct=0;
//        String strseq="";
//        String so="";
       
        int i = 0;
       for (File fasta_file : (List<File>) FileUtils.listFiles(new File(dir), new String[]{"fasta", "FASTA"}, true)) 
          {
            fasta.add(fasta_file);
        }
       
       for (i=43290; i<fasta.size(); i++)
       {
        String fnr = fasta.get(i).getPath().replace("\\","/");
        String kit = gen.neo4jlib.file_lib.getFileNameFromPath(fnr).replace(".fasta", "").split(Pattern.quote("."))[0];
       
       
       String id_query = "https://www.ncbi.nlm.nih.gov/nuccore/?term=" + kit + "&retmode=text&rettype=title";
       String fnids = "genbankhg.txt";
       try
       {
        web_file_to_import_folder.url_file_to_import_dir(id_query,fnids);
       
        String fids = gen.neo4jlib.neo4j_info.Import_Dir + fnids;         
        hg = "";
        hg = gen.neo4jlib.file_lib.readFileByLine(fids).split(Pattern.quote("haplotype"))[1].strip().split(" ")[0];
        if (hg.compareTo(" ")<0){
        hg = gen.neo4jlib.file_lib.readFileByLine(fids).split(Pattern.quote("haplogroup"))[1].strip().split(" ")[0];            
        }

        if (hg.compareTo(" ")>0){
//        System.out.println(i + "\t" + kit + "\t" + hg);
        gen.neo4jlib.neo4j_qry.qry_write("create(k:seq_kit{name:'" + kit + "'}) set k.gb_hg='" + hg + "'");
       String fnw="";
        }
       }
       catch(Exception e){}
       System.out.println(i + "\t" + kit + "\t" + hg);       
       
    }
       

      
    }  //end functio
    
}
