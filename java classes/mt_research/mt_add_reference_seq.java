/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.mt_research;

import gen.load.web_file_to_import_folder;
import gen.neo4jlib.neo4j_info;
import gen.neo4jlib.neo4j_qry;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class mt_add_reference_seq {
    @UserFunction
    @Description("maps kit fasta file sequences to mt-chromosome positions in the reference sequence")

    public String mt_sequenc_map(
        @Name("ref_seq_name") 
            String ref_seq_name,
        @Name("dir") 
            String dir,
        @Name("ref_uquence_path")
            String ref_sequence_path
  )
   
         { 
             
      map_seq(ref_seq_name, dir, ref_sequence_path);
         return "";
            }

    
    
    public static void main(String args[]) {
//        map_seq("E:/DAS_Coded_BU_2017/Genealogy/DNA/mt_haplotree_project/mt_DNA_sequences/","NC_012920.1");
        
    }
    
     public static void map_seq(String ref_seq_name, String dir, String ref_sequence_file) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        neo4j_info.neo4j_var_reload(); //initialize user information
 
//        gen.neo4jlib.neo4j_qry.qry_write("match (k:seq_kit)-[r]->() delete r");
//        gen.neo4jlib.neo4j_qry.qry_write("match (p:seq_pos)-[r]->() delete r");
//        gen.neo4jlib.neo4j_qry.qry_write("match (k:seq_kit) delete k");
//        gen.neo4jlib.neo4j_qry.qry_write("match (p:seq_pos) delete p");
        

try{
        if(ref_seq_name.compareTo("RSRS")==0) 
                {
                    web_file_to_import_folder.url_file_to_import_dir("htthttps://blobswai.blob.core.windows.net/gfg-blog/RSRS_reference_sequence.fasta", ref_seq_name + ".fasta");

                }        

        if(ref_seq_name.compareTo("cCRS")==0) 
                {
                    web_file_to_import_folder.url_file_to_import_dir("https://blobswai.blob.core.windows.net/gfg-blog/NC_012920.1.fasta", ref_seq_name + ".fasta");

                }        

        
        gen.neo4jlib.neo4j_qry.CreateIndex("seq_kit","name");
            gen.neo4jlib.neo4j_qry.CreateIndex("seq_pos", "loc");
            gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("seq_kit_pos", "der");
            gen.neo4jlib.neo4j_qry.CreateIndex("seq_pos", "rCRS");
            gen.neo4jlib.neo4j_qry.CreateIndex("seq_pos", "RSRS");
        }
        catch(Exception e){}
        
        String fn = "mt_seq";
        File fns = new File(fn);
        FileReader fr = null;
        FileWriter fw = null;
        
        try{
        String fnn = "mt_seq.csv";
        File fo = new File(fnn);
        fw = new FileWriter(gen.neo4jlib.neo4j_info.Import_Dir + fo);
        fw.write("kit|pos|nucleotide\n");
        }
        catch(Exception e){}
        
        //get file list and export csv files for Neo4j import'
        String strseq= "";
        String so = "";
        int pos_ct = 0 ;
        
        int i = 0;

            strseq = gen.neo4jlib.file_lib.readFileByLine(gen.neo4jlib.neo4j_info.Import_Dir + ref_seq_name + "/fasta");
            String x[] = strseq.split("\n");
            String kit = gen.neo4jlib.file_lib.getFileNameFromPath(ref_seq_name + "ref_seq");
            for (int j=1; j<x.length; j++)
            {
                for (int k=0;k< x[j].length(); k++)
                {
                pos_ct = pos_ct + 1;
                so = so + kit + "|" + pos_ct + "|" + x[j].substring(k,k+1) + "\n";

                }
            }

     
            try{
                fw.write(so);
                fw.flush();
            }
            catch(Exception e){}
       
      
      
        //create seq_pos
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///mt_seq.csv' as line FIELDTERMINATOR '|'  with distinct toInteger(line.pos) as pos  merge (p:seq_pos{loc:pos}) ");
        
        //create seq_kit_pos relationship
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///mt_seq.csv' as line FIELDTERMINATOR '|'  match (p:seq_pos{loc:toInteger(line.pos)})  match(k:seq_kit{name:toString(line.kit)}) merge (k)-[r:seq_kit_pos{der:line.nucleotide,pos:toInteger(line.pos)}]->(p)");
        
      
        //use reference sequence to set anc value into pos nodes
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///mt_seq.csv' as line FIELDTERMINATOR '|' with line.pos as loc,line.nucleotide as nuc where line.kit='" + ref_sequence_file + "' match (p:seq_pos{loc:toInteger(loc)}) set p." + ref_seq_name + "=nuc");

            
}

}