/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.mt_research;

import gen.neo4jlib.neo4j_qry;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class load_genbank_files {
    @UserFunction
    @Description("Reads files an extracts haplogruop if reported.")

    public String load_genbank_file_kits_and_pos(
  )
   
         { 
             
        get_hgs();
         return "";
            }

    
    
    public static void main(String args[]) {
        get_hgs();
    }
    
     public static String get_hgs() 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        try
        {
            gen.neo4jlib.neo4j_qry.CreateIndex("seq_kit","name");
            gen.neo4jlib.neo4j_qry.CreateIndex("seq_pos", "loc");
        }
        catch(Exception e){}
        
        String dir = "E://DAS_Coded_BU_2017/Genealogy/DNA/mt_haplotree_project/mt_DNA_sequences/entrez/";
        //get file list and export csv files for Neo4j import'
        List<File> fasta = new ArrayList<File>();
        String fnout = gen.neo4jlib.neo4j_info.Import_Dir + "genbank_mt_seq.csv";
        FileReader fr = null;
        FileWriter fw =null;
        int pos_ct=0;
        String strseq="";
        String so="";
                
        try
        {
            File fout = new File(fnout);
             fw = new FileWriter(fnout);
        }   
        catch(Exception e){}
            
       so = "";

        int i = 0;
        for (File fasta_file : (List<File>) FileUtils.listFiles(new File(dir), new String[]{"fasta", "FASTA"}, true)) 
        {
            i = i + 1;
            pos_ct = 0;
            so = "";
            strseq = "";
            fasta.add(fasta_file);
        }
        
        for (i=0;i<fasta.size(); i++)
        {
            String fnr = fasta.get(i).getPath();
            so="kit|pos|nucleotide\n";
            strseq = "";
            pos_ct=0;
            
            try
            {
                fr = new FileReader(fnr);
                strseq = gen.neo4jlib.file_lib.readFileByLine(fnr);
                int hff =9;
                String x[] = strseq.split("\n");
                String h[] = x[0].split(" ");
                String kk[]= h[0].replace(">","").split(Pattern.quote("."));;
                String kit = kk[0];
                String hh[] = h[0].split(Pattern.quote("haplogroup"));
                String hg = " ";
                try{ hg= hh[1].strip();} catch(Exception ee){}
                Long kit_version = Long.parseLong(kk[1]);
                for (int j=1; j<x.length; j++)  //skip header row
                {
                    for (int k=0;k< x[j].length(); k++)
                    {
                    pos_ct = pos_ct + 1;
                    so = so + kit + "|" + pos_ct + "|" + x[j].substring(k,k+1) + "\n";

                    }
                }

   
               // System.out.println(so);
                gen.neo4jlib.file_lib.writeFile(so, fnout);

        //create kit_seq
                if (hg.compareTo(" ")>0 )
                   {
                    gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///genbank_mt_seq.csv' as line FIELDTERMINATOR '|'       with distinct line.kit as kit,count(*) as ct     merge (sk:seq_kit{name:kit,pos_ct:toInteger(ct),gb_hg:ToString("+ hg + ")})");
                    }
                else{
                    gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///genbank_mt_seq.csv' as line FIELDTERMINATOR '|'       with distinct line.kit as kit,count(*) as ct     merge (sk:seq_kit{name:kit,pos_ct:toInteger(ct)})");
 
                }       
        //create seq_pos
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///genbank_mt_seq.csv' as line FIELDTERMINATOR '|'  with distinct toInteger(line.pos) as pos    merge (p:seq_pos{loc:pos}) ");
        
        //create seq_kit_pos relationship
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///genbank_mt_seq.csv' as line FIELDTERMINATOR '|'  match (p:seq_pos{loc:toInteger(line.pos)})  match(k:seq_kit{name:toString(line.kit)}) merge (k)-[r:seq_kit_pos{der:line.nucleotide,pos:toInteger(line.pos)}]->(p)");
        
            }  //end try
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
            
            
        }  //next fasta
        
        //System.out.println(fasta.size());

        return "";
    }
}
