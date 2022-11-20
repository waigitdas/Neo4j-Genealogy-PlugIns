/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna_painter;

import gen.neo4jlib.neo4j_info;
import gen.neo4jlib.neo4j_qry;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class dna_painter_merge_queries {
    @UserFunction
    @Description("reads file with a dna painter query column and runs each row's query")

    public String merge_dna_painter_queries(
        @Name("file_name") 
            String file_name
  )
   
         { 
             
        String s = create_query(file_name);
         return s;
            }

    
    
    public static void main(String args[]) {
//        String s =create_query("C:/Users/david/AppData/Local/Neo4j/Relate/Data/dbmss/dbms-84ac309c-05d0-4b7b-9902-726a3821cf7d/import/cluster_matches.csv");
//        System.out.println(s);
    }
    
     public String create_query(String fn) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
 
        String[] c = gen.neo4jlib.file_lib.ReadFileByLineWithEncoding(gen.neo4jlib.neo4j_info.Import_Dir + fn).split("\n");
        String[] cs;
        String s ="";
        String so = "";
        for (int i=1;i<c.length; i++){ //skip header row
            cs = c[i].split(Pattern.quote("|"));
            try{
            File fo = new File(neo4j_info.Import_Dir + gen.neo4jlib.neo4j_info.project + "dna_painter_custer_matches_" + i + ".csv");
            FileWriter fw = null;
            try {
              fw= new FileWriter(fo, true);
            } catch (IOException ex) {
              //Logger.getLogger(dna_painter_merge_queries.class.getName()).log(Level.SEVERE, null, ex);
            }
             s = cs[6].replace("'green'","''").replace("[[","[").replace("]]","]").replace("'","\'").replace("⦋","[").replace("⦌","]");
                fw.write(gen.neo4jlib.neo4j_qry.qry_to_csv(s)); 
                fw.flush();
                fw.close();
            }
            catch (Exception e) {}
        }
        
     try{
     }
        catch (Exception e){};
//        gen.neo4jlib.neo4j_qry.qry_to_csv(s, gen.neo4jlib.neo4j_info.Import_Dir + gen.neo4jlib.neo4j_info.project +  "_cluster_match.csv");
          //gen.neo4jlib.file_lib.writeFile(s, fn);
        return "completed";
    }
}
