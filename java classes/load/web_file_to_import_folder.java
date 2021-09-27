/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.load;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import gen.neo4jlib.neo4j_info;
        
public class web_file_to_import_folder {

    public static void main(String args[]) {
        gen.neo4jlib.neo4j_info.neo4j_var();
        url_file_to_import_dir("https://blobswai.blob.core.windows.net/gen-udf/Family_relationship_table.csv","Family_relationship_table.csv");
    }
 
    
    public static void url_file_to_import_dir(String url,String FileNm) {
  
        InputStreamReader isr  = null;
        BufferedReader buffRead = null;
        try{
           FileWriter fw= new FileWriter(gen.neo4jlib.neo4j_info.Import_Dir + FileNm);

           URL url_str  = new URL(url);
           URLConnection conn  = url_str.openConnection();
           isr   = new InputStreamReader(conn.getInputStream());
           buffRead  = new BufferedReader(isr);
           String str = "";
           while ((str = buffRead.readLine()) != null) {
            fw.write(str + "\n");    
           }     
            fw.flush();    
            fw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        } finally{
            try{
             if(buffRead != null) buffRead.close();
             if(isr != null) isr.close();
            }catch (IOException e) {
             e.printStackTrace();
            }
           }
    }
}


    
