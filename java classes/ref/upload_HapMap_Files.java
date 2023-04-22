/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.ref;

import gen.load.web_file_to_import_folder;
import gen.neo4jlib.neo4j_info;
import gen.neo4jlib.neo4j_qry;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//import java.util.logging.Level;
import jxl.common.Logger;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class upload_HapMap_Files {
    @UserFunction
    @Description("Uploads HapMap to the Neo4j database HapMap.")

    public String load_HapMap(
  )
   
         { 
             
        String s = upload_HapMap();
         return s;
            }

    
    
    public void main(String args[]) {
//        try{ 
            upload_HapMap();
//        }
//        catch(Exception e){}
    }
    
     public static String upload_HapMap() //throws FileNotFoundException 
    {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String activeDb = gen.neo4jlib.neo4j_info.user_database;
    
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        FileWriter fw = null;
       FileWriter fwseq = null;
    
        gen.neo4jlib.neo4j_info.user_database="hapmap";
          
        try{
            //takes lots of memory! gen.neo4jlib.neo4j_qry.qry_write("match (h:HapMap) delete h");
            gen.neo4jlib.neo4j_qry.CreateIndex("HapMap", "Indx");
            gen.neo4jlib.neo4j_qry.CreateIndex("HapMap", "chr");
           gen.neo4jlib.neo4j_qry.CreateIndex("HapMap", "strt_pos");
           gen.neo4jlib.neo4j_qry.qry_write("CREATE INDEX IF NOT EXISTS FOR (h:HapMap) ON (h.chr,h.strt_pos)");
//             gen.neo4jlib.neo4j_qry.CreateCompositeIndex("HapMap","chr,start_pos");
    }
        catch (Exception e){}
        
      
        String fn ="";
        String src ="";
        //gen.neo4jlib.neo4j_info.Import_Dir="C:/Users/david/AppData/Local/Neo4j/Relate/Data/dbmss/dbms-defa19bd-d66f-4fdd-b9ee-d2fdb9a2256b/import/";
        for (int i=1; i<23; i++){
            String chr = String.valueOf(i).strip();
            if (i < 10){chr = "0" + chr; }
             try {   
            fn ="HapMap_chr_" + chr + ".csv";
            src ="https://raw.githubusercontent.com/waigitdas/Neo4j-Genealogy-PlugIns/main/HapMap/hg37/genetic_map_GRCh37_chr" + String.valueOf(i).strip() + ".txt";
            web_file_to_import_folder.url_file_to_import_dir(src,fn);
             }
             catch (Exception e) {
                 return "Failed: " + e.getMessage();
             }
            //fix header
            String[] s = gen.neo4jlib.file_lib.readFileByLine(gen.neo4jlib.neo4j_info.Import_Dir +  fn).split("\n");
            File fn2 = new File(neo4j_info.Import_Dir + fn);
            String sprev="0";
            String prevcm="0.0";
            try {
            fw = new FileWriter(fn2);
            fwseq = new FileWriter(fn2);
            fw.write("Indx|chr|strt_pos|rate|cm|prev|prevcm\n");
            for (int j=1; j<s.length; j++){
                String[] ss = s[j].split("\t");
                String sss = "";
                 sss = sss + chr + ":" + ss[1].strip() + "|";
                sss = sss + chr + "|";
                sss = sss + ss[1] + "|";
                sss = sss + ss[2] + "|";
                sss = sss + ss[3] + "|";
                sss = sss + sprev  + "|";;
                sss = sss + prevcm;
                sprev = ss[1];
                prevcm= ss[3];
                //if (i.equals(0)) {sprev=String.valueOf(ss[1]); }
                fw.write(sss + "\n");
            }
            } catch (IOException ex) {
                    return "Error loading chr " + String.valueOf(i);
//Logger.getLogger(upload_HapMap_Files.class.getName()).log(Level.SEVERE, null, ex);
            }
                  
       try{fw.flush();} catch(Exception e){}
       
//            gen.neo4jlib.neo4j_qry.APOCPeriodicIterateCSV("LOAD CSV WITH HEADERS FROM 'file:///" + fn + "' as line FIELDTERMINATOR '|' return line ", "create (h:HapMap{Indx:toString(line.Indx), chr:toString(line.chr),strt_pos:toInteger(line.strt_pos),rate:toFloat(line.rate),cm:toFloat(line.cm),icm:toFloat(toFloat(line.cm)-toFloat(line.prevcm))})", 10000);
//gen.neo4jlib.neo4j_qry.qry_write("Using periodic commit 5000 LOAD CSV With HEADERS FROM 'file:///" + fn + "' AS line FIELDTERMINATOR '|' create (h:HapMap{chr:toString(line.chr),strt_pos:toInteger(line.strt_pos),rate:toFloat(line.rate),cm:toFloat(line.cm),Indx:toString(line.Indx)})");
   
      
         
         gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///" + fn + "' as line FIELDTERMINATOR '|' create (h:HapMap{Indx:toString(line.Indx), chr:toString(line.chr),strt_pos:toInteger(line.strt_pos),rate:toFloat(line.rate),cm:toFloat(line.cm),icm:toFloat(toFloat(line.cm)-toFloat(line.prevcm))})");
        
    
         
        } // next chr
               
    try{
        fw.flush();
        fw.close();
    }

    catch(Exception e){}
    
    for (int i=1; i<23; i++){
            String chr = String.valueOf(i).strip();
            if (i < 10){chr = "0" + chr; }
              
            fn ="HapMap_chr_" + chr + ".csv";
            gen.neo4jlib.neo4j_qry.APOCPeriodicIterateCSV("LOAD CSV WITH HEADERS FROM 'file:///" + fn + "' AS line FIELDTERMINATOR '|' return line ", "match (h1:HapMap {chr:toString(line.chr),strt_pos:toInteger(line.prev)}) match (h2:HapMap{Indx:toString(line.Indx)}) merge (h1)-[r:seq]->(h2)", 100000);

             }
        
        gen.neo4jlib.neo4j_info.user_database=activeDb;
        
        
        return "completed";
    }
     
     public static void write_hapmap (String cq) {
        String Q = "\"";
        //String csv = "CALL apoc.periodic.iterate(" + Q + LoadCSV + Q + ", " + Q + ReplaceCypher + Q + ",{batchSize: " + batchsize + ", parallel:false, iterateList:true, retries:25})";
        gen.conn.connTest.cstatus();
        Session session =  gen.conn.connTest.session;
           
            {
            
                session.executeWrite(tx -> {
                   Result result = tx.run( cq );
               return 1;
               
                } );
            } 
   
           }
   

}
