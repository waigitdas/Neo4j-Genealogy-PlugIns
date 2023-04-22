/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import gen.neo4jlib.neo4j_info;
import java.io.File;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class Y_DNA_FTDNA {
    @UserFunction
    @Description("Load Y STRs and SNPs")

    public String load_derived_file(
    
  )
   
         { 
             
        String s = load_file();
         return s;
            }

    
    
    public static void main(String args[]) {
        load_file() ;  ;
    }
    
     public static String load_file()  
    {   

         try
         {
            neo4j_qry.CreateIndex("matchYvariant","name");
          neo4j_qry.CreateIndex("STR","name");

         }
catch(Exception e){}
         
//delete prior nodes/relationships in this set
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (v:matchYvariant)-[r]-() delete r,v");
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (v:STR)-[r]-() delete r,v");
       gen.neo4jlib.neo4j_qry.qry_write("MATCH ()-[r:shared_YHG]-() delete r");

        //shared Y-HG
        gen.neo4jlib.neo4j_qry.qry_write("match (Y:DNA_Match) with Y where Y.YHG is not null and 'Z'>=left(Y.YHG,1)>='A' with Y order by Y.YHG with collect (distinct Y.YHG) as mito unwind mito as x call {with x MATCH (m:DNA_Match)-[rs:shared_match]-(n:DNA_Match) where m.YHG = x and n.YHG = x and m.fullname<n.fullname merge (m)-[r:shared_YHG{YHG:x}]-(n)");
        
        
//        //STRs
//        File dir1 = new File(gen.neo4jlib.neo4j_info.Import_Dir);
//        String[] extensions = new String[] {"csv"};
//        //System.out.println("Getting all .txt and .jsp files in " + dir.getCanonicalPath() + " including those in subdirectories");
//        List<File> files2 = (List<File>) FileUtils.listFiles(dir1, extensions, true);
//        for (File file2 : files2) {
//            if(file2.getName().contains("YDNA_DYS_Results") )
//                    { 
//                String cq = "LOAD CSV WITH HEADERS FROM 'file:///" + file2.getName() + "' as line FIELDTERMINATOR '|' merge (v:STR{name:toString(line.str)})";
//                gen.neo4jlib.neo4j_qry.qry_write(cq);
//
//                gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///" + file2.getName() + "' as line FIELDTERMINATOR '|' match (v:STR{name:toString(line.str)}) match (d:DNA_Match{kit:toString(line.kit)}) merge (d)-[r:match_y_str{value:toString(line.value)}]->(v)");
//
//                    }
//        }
//        
//        //SNPs
//        File dir = new File(gen.neo4jlib.neo4j_info.Import_Dir);
//        //String[] extensions = new String[] {"csv"};
//        //System.out.println("Getting all .txt and .jsp files in " + dir.getCanonicalPath() + " including those in subdirectories");
//        List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
//        for (File file : files) {
//            if(file.getName().contains("BigY_Data_Derived") )
//                    { 
//                String cq = "LOAD CSV WITH HEADERS FROM 'file:///" + file.getName() + "' as line FIELDTERMINATOR '|' merge (v:matchYvariant{name:toString(line.SNPName),anc:toString(line.Reference), der:toString(line.Genotype)})";
//                gen.neo4jlib.neo4j_qry.qry_write(cq);
//
//                gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///" + file.getName() + "' as line FIELDTERMINATOR '|' match (v:matchYvariant{name:toString(line.SNPName)}) match (d:DNA_Match{kit:toString(line.kit)}) merge (d)-[r:match_y_variant]->(v)");
//
//                    }
//        }
//        
//        gen.neo4jlib.neo4j_qry.qry_write("MATCH (v:matchYvariant) with v match (b:block) with v,b,split(b.name,'-') as bs where bs[1]=v.name merge (v)-[r:block_y_variant]-(b)");
//        
//        //System.out.println("###");
////      String cq = "LOAD CSV WITH HEADERS FROM 'file:///" + file + "' as line FIELDTERMINATOR ',' with line.SNPName as SNP,line.Derived as der,line.OnTree as ontree,line.Reference as ref,line.Genotype as genotype where line.Type='Known SNP' return distinct 'B51965' as kit,1 as rn,SNP,der,ontree, ref,genotype";
////        String c[] = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n"); 
////        //gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited(cq, "Y_derived.csv");
////gen.neo4jlib.file_lib.writeFile(c, "Y_derived.csv");
//        //String = new ();(cq, );
//        
//       // cq ="LOAD CSV WITH HEADERS FROM 'file:///Y_derived.csv' as line FIELDTERMINATOR '|' merge (v:kitYvariant{name:toString(line.SNP),anc:toString(line.ref), der:toString(line.genotype)})";
//       // gen.neo4jlib.neo4j_qry.qry_write(cq);
//        
//        //
//        //cq = "LOAD CSV WITH HEADERS FROM 'file:///Y_derived.csv' as line FIELDTERMINATOR '|' match (v:kitYvariant{name:toString(line.SNP)}) match (d:DNA_Match{kit:toString(line.kit)}) merge (d)-[r:match_y_variant]->(v)";
//       // gen.neo4jlib.neo4j_qry.qry_write(cq);
//
//              ;
//   
        return "completed";
    }
}
