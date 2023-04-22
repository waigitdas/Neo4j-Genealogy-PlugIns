/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tree;

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


public class mt_seqence_mapping {
    @UserFunction
    @Description("maps kit fasta file sequences to mt-chromosome positions in the reference sequence")

    public String mt_sequenc_map(
        @Name("dir") 
            String dir,
        @Name("ref_uquence_path")
            String ref_sequence_path
  )
   
         { 
             
        map_seq(dir,ref_sequence_path);
         return "";
            }

    
    
    public static void main(String args[]) {
        map_seq("E:/DAS_Coded_BU_2017/Genealogy/DNA/mt_haplotree_project/mt_DNA_sequences/","NC_012920.1");
        
    }
    
     public static String map_seq(String dir, String ref_sequence_file) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        neo4j_info.neo4j_var_reload(); //initialize user information
 
        gen.neo4jlib.neo4j_qry.qry_write("match (k:seq_kit)-[r]->() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (p:seq_pos)-[r]->() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (k:seq_kit) delete k");
        gen.neo4jlib.neo4j_qry.qry_write("match (p:seq_pos) delete p");
        

        
        try{
            gen.neo4jlib.neo4j_qry.CreateIndex("seq_kit","name");
            gen.neo4jlib.neo4j_qry.CreateIndex("seq_pos", "loc");
            gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("seq_kit_pos", "der");
            gen.neo4jlib.neo4j_qry.CreateIndex("seq_pos", "anc");
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
        List<File> FileList = new ArrayList<File>();
 
        String so = "";

        int i = 0;
        for (File fasta_file : (List<File>) FileUtils.listFiles(new File(dir), new String[]{"fasta", "FASTA"}, true)) 
        {
            i = i + 1;
            int pos_ct = 0;
            so = "";
            String strseq = "";
            FileList.add(fasta_file);
            System.out.println(i + "\t" + fasta_file);
             try
                {
                    strseq = gen.neo4jlib.file_lib.readFileByLine(fasta_file.getPath());
                    String x[] = strseq.split("\n");
                    String kit = gen.neo4jlib.file_lib.getFileNameFromPath(fasta_file.getPath().replace("\\", "/").replace(".fasta","").replace("-FASTA","").replace(" ","_"));
                    for (int j=1; j<x.length; j++)
                    {
                        for (int k=0;k< x[j].length(); k++)
                        {
                        pos_ct = pos_ct + 1;
                        so = so + kit + "|" + pos_ct + "|" + x[j].substring(k,k+1) + "\n";

                        }
                    }
                }
            catch(Exception e)
            {
                System.out.println("Error; " + e.getMessage());
            }
            //System.out.println(so);
            try{
                fw.write(so);
                fw.flush();
            }
            catch(Exception e){}
             int jg =0;
            
        }  //next in for loop

        try{
                fw.flush();
                fw.close();
        }
        catch(Exception e){}
        
        //create kit_seq
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///mt_seq.csv' as line FIELDTERMINATOR '|'       with distinct line.kit as kit,count(*) as ct     merge (sk:seq_kit{name:kit,pos_ct:ct})");
        
        //create seq_pos
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///mt_seq.csv' as line FIELDTERMINATOR '|'  with distinct toInteger(line.pos) as pos    merge (p:seq_pos{loc:pos}) ");
        
        //create seq_kit_pos relationship
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///mt_seq.csv' as line FIELDTERMINATOR '|'  match (p:seq_pos{loc:toInteger(line.pos)})  match(k:seq_kit{name:toString(line.kit)}) merge (k)-[r:seq_kit_pos{der:line.nucleotide,pos:toInteger(line.pos)}]->(p)");
        
        //create seq_order relationship
        int loc_ct = Integer.parseInt(gen.neo4jlib.neo4j_qry.qry_to_csv("MATCH (n:seq_pos) RETURN max(n.loc) as loc").split("\n")[0]);
        String s = "parent|child\n";
        for (i=1;i<loc_ct; i++){
            int ii=i+1;
            s = s + i + "|" + ii + "\n";
        }
        gen.neo4jlib.file_lib.writeFile(s, gen.neo4jlib.neo4j_info.Import_Dir + "seq_order.csv");
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///seq_order.csv' as line FIELDTERMINATOR '|' match (p1:seq_pos{loc:toInteger(line.parent)}) match (p2:seq_pos{loc:toInteger(line.child)}) merge (p1)-[r:seq_order]->(p2)");
        
        //use reference sequence to set anc value into pos nodes
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///mt_seq.csv' as line FIELDTERMINATOR '|' with line.pos as loc,line.nucleotide as nuc where line.kit='" + ref_sequence_file + "' match (p:seq_pos{loc:toInteger(loc)}) set p.rCRS=nuc");
        
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///mt_seq.csv' as line FIELDTERMINATOR '|' with line.pos as loc,line.nucleotide as nuc where line.kit='Behar_-_Sequence_S1' match (p:seq_pos{loc:toInteger(loc)}) set p.RSRS=nuc");

                
        //gen.neo4jlib.neo4j_qry.qry_write(")");
                return "";
            }
}
