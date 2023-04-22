/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tree;

import gen.load.web_file_to_import_folder;
import gen.neo4jlib.neo4j_qry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.*;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class mt_de_novo_haplotree {
    @UserFunction
    @Description("https://www.wai.md/post/ordpath-computing-genealogy-descendancy-trees")

    public String mt_haplotree_from_sequence(
        @Name("seq_filepath") 
            String seq_filepath
  )
   
         { 
             
        make_tree(seq_filepath);
         return "";
            }

    
    
    public static void main(String args[]) {
        
       int ct = 76;
       String[][] op = new String[ct+1][3];
       String[][] tmp = new String[1][3];
       int strt = 588221;
       
        for (int i=0; i<ct;i++)
        {
         strt = strt + 1;
         String entrez = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id=MT" + strt + ".1&rettype=fasta&retmode=text";
          
        String fnw = "MT" + strt + ".fasta";
                //"E:/DAS_Coded_BU_2017/Genealogy/DNA/mt_haplotree_project/mt_DNA_sequences/entrez/MT" + strt + ".fasta";
        web_file_to_import_folder.url_file_to_import_dir(entrez,fnw);
        String fn = gen.neo4jlib.neo4j_info.Import_Dir + fnw;         
        
        String f[] = gen.neo4jlib.file_lib.readFileByLine(fn).split("\n");
        String h1[]= f[0].split("haplogroup");
         op[i][0] = "MT" + strt;
        try{
        String h2[] = h1[1].strip().split(" ");
       op[i][1] = h2[0].strip();
        }
        catch(Exception e){}
        
        op[i][2] = make_tree(fn);
        }

//        op[0][0] = "xxx";
//        op[1][0] = "330527";
//        op[2][0] = "425497";
//        op[3][0] = "446574";
//        op[4][0] = "792577";
//        op[5][0] = "B51965";
//
//        op[0][1] = "xxx";
//        op[1][1] = "J1c1b2a";
//        op[2][1] = "H1c3";
//        op[3][1] = "H23";
//        op[4][1] = "I1a1";
//        op[5][1] = "H-h";
//
//        op[0][2] = make_tree(dir + "Behar - Sequence S1.fasta");
//        op[1][2] = make_tree(dir + "330527-FASTA.fasta");
//        op[2][2] = make_tree(dir + "425497-FASTA.fasta");
//        op[3][2] = make_tree(dir + "446574-FASTA.fasta");
//        op[4][2] = make_tree(dir + "792577-FASTA.fasta");
//        op[5][2] = make_tree(dir + "B51965-FASTA.fasta");
        
        //java.util.Arrays.sort(op);
        
        int yu=999;
        
        for (int i=0;i<op.length; i++)
        {
            for (int j=i; j<op.length; j++)
                try{
                if(op[i][2].compareTo(op[j][2])<0)
                {
                    tmp[0][0] = op[i][0];
                    tmp[0][1] = op[i][1];
                    tmp[0][2] = op[i][2];
                    
                    op[i][0] = op[j][0];
                    op[i][1] = op[j][1];
                    op[i][2] = op[j][2];
                            
                    op[j][0] = tmp[0][0];
                    op[j][1] = tmp[0][1];
                    op[j][2] = tmp[0][2];
                }
                }
            catch (Exception e){}
        }
        
        for (int i=0;i<op.length; i++)
        {
            System.out.println(op[i][0] + "\t" + op[i][1]);
        }
               
    }  //end functio
    
     public static String make_tree(String seq_file) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
          //web_file_to_import_folder.url_file_to_import_dir("https://www.ncbi.nlm.nih.gov/nuccore/MT588296.1?report=fasta","MT588296.fasta");
 
        String f[] = gen.neo4jlib.file_lib.readFileByLine(seq_file).split("\n");
        List<Long> op = new ArrayList<Long>();
        for (int i=1;i<f.length; i++)
        {
          for(int j = 0;j<f[i].length();j++)
          {
             op.add(op_number(f[i].substring(j, j+1)));
          }
        }
        
        String ordpath = gen.graph.ordpath.get_op(op);
        
        gen.neo4jlib.file_lib.writeFile(ordpath, "E:/DAS_Coded_BU_2017/Genealogy/DNA/mt_haplotree_project/mt_DNA_sequences/" + gen.neo4jlib.file_lib.getFileNameFromPath(seq_file).replace("fasta", "ordpath"));
        
        //System.out.println(ordpath);
        return ordpath;
    }
     
     public static Long op_number(String s)
     {
         Long op=0L;
         if(s.compareTo("A")==0){op=1L;}
         if(s.compareTo("C")==0){op=2L;}
         if(s.compareTo("G")==0){op=3L;}
         if(s.compareTo("T")==0){op=4L;}
         
         return op;
     }
}
