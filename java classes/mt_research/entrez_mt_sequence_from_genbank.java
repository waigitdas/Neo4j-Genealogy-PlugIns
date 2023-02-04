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
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.*;
import java.util.regex.Pattern;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class entrez_mt_sequence_from_genbank {
    @UserFunction
    @Description("https://www.wai.md/post/ordpath-computing-genealogy-descendancy-trees")

    public String mt_haplotree_from_sequence(
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
        FileWriter fwerr = null;
        FileWriter fwtracker = null;
        Boolean b = false;
        String h2 ="";
        long tmprev = System.currentTimeMillis();
        
       int strt = 58400;
       int incr = 100;
       int end = strt + incr;
       int kit_ct = 0;
       int rmax = 63000;
       String fnerr = "E:/DAS_Coded_BU_2017/Genealogy/DNA/mt_haplotree_project/mt_DNA_sequences/entrez/errors.csv";
       File ferr = new File(fnerr);
       try
       {
           fwerr = new FileWriter(ferr);
           fwerr.write("kit\n");
       }
      
     catch(Exception e){}
       
       String fntrack = "E:/DAS_Coded_BU_2017/Genealogy/DNA/mt_haplotree_project/mt_DNA_sequences/entrez/tracker.csv";
       File ftrack = new File(fntrack);
       try
       {
           fwtracker = new FileWriter(ftrack);
           fwtracker.write("start, kit_ct, msec\n");
       }
     catch(Exception e){}
               
       ////////////////////////////////////////////////////////////////////////
       ////////////////////////////////////////////////////////////////////////
       for (int g=strt;g<end;g++){
           kit_ct = 0;
          String id_query = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=nuccore&retmax=" + incr + "&retstart=" + strt + "&term=mitochondrion[filter]%20AND%20%22complete%20genome%22[title]%20AND%20txid9606[orgn:noexp]%20AND%20ddbj_embl_genbank[filter]";
       String fnids = "genbank1.txt";
       web_file_to_import_folder.url_file_to_import_dir(id_query,fnids);
        String fids = gen.neo4jlib.neo4j_info.Import_Dir + fnids;         
        String fid[] = gen.neo4jlib.file_lib.readFileByLine(fids).split(Pattern.quote("<IdList>"))[1].split(Pattern.quote("</IdList>"))[0].replace("<Id>","").replace("</Id>", "").split("\n");
       String fnw="";
       
        for (int i=0; i<incr;i++)
        {
       
          b = false;
         try
         {
         String entrez = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id=" + fid[i].strip().replace("\t","") + "&rettype=fasta&retmode=text";
       if(fid[i].compareTo(" ")>0)
       {
        fnw = "E:/DAS_Coded_BU_2017/Genealogy/DNA/mt_haplotree_project/mt_DNA_sequences/entrez/tmp.fasta";
        web_file_to_import_folder.url_file_to_dir(entrez,fnw);
 

        String fr = gen.neo4jlib.file_lib.readFileByLine(fnw);
        String h1[]= fr.split("\n")[0].split(" ");

        h2 = h1[0].strip().replace(">","");
        gen.neo4jlib.file_lib.writeFile(fr, "E:/DAS_Coded_BU_2017/Genealogy/DNA/mt_haplotree_project/mt_DNA_sequences/entrez/" + h2 + ".fasta");
         if(h2.compareTo(" ")>0) {kit_ct = kit_ct + 1;}         
       }
         }
         catch(Exception e)
         {
             b=true;
             System.out.println(i + "\t" + fid[i] + "\t" + e.getMessage());
         }

         if(b.compareTo(true)==0 || h2.compareTo(" ")<0)
         {
         try
         {
             fwerr.write(fid[i] + "\n");
             fwerr.flush();
             }
             catch(Exception e1){}
        }
        

        }  //next kit 

        try{
            Long tm = System.currentTimeMillis()-tmprev;
            fwtracker.write(strt + ", " + kit_ct + ", " + Long.toString(tm) + "\n");
            fwtracker.flush();
            tmprev=System.currentTimeMillis();
        }
        catch(Exception ex3){}
            strt = strt + incr;

       }   //get g group
try
{
    fwerr.flush();
    fwerr.close();
    fwtracker.flush();
    fwtracker.close();
}
catch(Exception e){}
       
       
    }  //end functio
    
}
