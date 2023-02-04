/**
 * Copyright 2021-2023 
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
        url_file_to_import_dir("https://www.familytreedna.com/public/y-dna-haplotree/get","");
    }
 
    
    public static void url_file_to_import_dir(String url,String FileNm) {
        
        InputStreamReader isr  = null;
        BufferedReader buffRead = null;
        try{
           FileWriter fw= new FileWriter(gen.neo4jlib.neo4j_info.Import_Dir + FileNm);
//FileWriter fw= new FileWriter("C:/Users/david/AppData/Local/Neo4j/Relate/Data/dbmss/dbms-defa19bd-d66f-4fdd-b9ee-d2fdb9a2256b/import/" + FileNm);

           URL url_str  = new URL(url);
           URLConnection conn  = url_str.openConnection();
           isr   = new InputStreamReader(conn.getInputStream());
           buffRead  = new BufferedReader(isr);
           String str = "";
           while ((str = buffRead.readLine()) != null) {
            //System.out.println(str);
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

        public static void url_file_to_dir(String url,String FilePath) {
        
        InputStreamReader isr  = null;
        BufferedReader buffRead = null;
        try{
           FileWriter fw= new FileWriter(FilePath);
//FileWriter fw= new FileWriter("C:/Users/david/AppData/Local/Neo4j/Relate/Data/dbmss/dbms-defa19bd-d66f-4fdd-b9ee-d2fdb9a2256b/import/" + FileNm);

           URL url_str  = new URL(url);
           URLConnection conn  = url_str.openConnection();
           isr   = new InputStreamReader(conn.getInputStream());
           buffRead  = new BufferedReader(isr);
           String str = "";
           while ((str = buffRead.readLine()) != null) {
            //System.out.println(str);
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


    
