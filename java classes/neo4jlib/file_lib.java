/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.neo4jlib;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.Reader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.*;


public class file_lib {
    
    public static String readFileByLine(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
 
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) 
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
 
        return contentBuilder.toString();
    }
    
    
    public static void writeFile(String s, String filePath) {
        File fn = new File(filePath);
        try{
            FileWriter fw = new FileWriter(fn);
            fw.write(s);
            fw.flush();    
            fw.close();
    }
        catch (IOException e){}
        
    }
    
    
public static void get_file_transform_put_in_import_dir(String filePathRead, String filePathSave){

try{        
   Reader fileReader = new FileReader(filePathRead);
   //Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(fileReader);
   Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(fileReader);
   File fn = new File(neo4j_info.Import_Dir +  filePathSave);
   FileWriter fw = new FileWriter(fn);
            
        
   int ct = 0;
   for (CSVRecord record : records) {
       String s = "";
       for (int i=0; i < record.size(); i++) {
        if (ct==0) {s = s + record.get(i).replace(" ","_") + "|";}
        else {
            s = s + record.get(i).replace("\"","`")  + "|";
        }
    
   }
       
   fw.write(s + "\n");
   ct = ct +1;
   }

       fw.flush();    
       fw.close();
       fileReader.close();
}
     catch (Exception e) {System.out.println(e.getMessage());};
    }
   
public static String getFileNameFromPath(String FileName) {
    String[] s = FileName.split("/");
    return s[s.length-1];
}

public static void parse_chr_containing_csv_save_to_import_folder(String FileName,int ChrColNumber){
    FileWriter fw = null;
        gen.neo4jlib.neo4j_info.neo4j_var();
        try {
            String c = file_lib.readFileByLine(FileName);
            c = c.replace("|"," ").replace(",","|").replace("\"", "`");
            System.out.println("\n" + neo4j_info.Import_Dir);
            String[] cc = c.split("\n");
            String header = cc[0].replace(" ","_");
            c = c.replace(cc[0], header);
            String[] ccc = c.split("\n");
            String SaveFileName = getFileNameFromPath(FileName);
            File fn = new File(neo4j_info.Import_Dir + SaveFileName);
            fw = new FileWriter(fn);
            fw.write(header + "\n");
            for (int ii=1; ii<ccc.length; ii++){
                String[] xxx = ccc[ii].split(Pattern.quote("|"));
                
                String s = "";
                for(int j=0; j<xxx.length; j++) {
                    if (j==ChrColNumber) {  //chr
                        if (xxx[j].strip().length()==1) {
                            xxx[j] = "0" + xxx[j].strip();
                        }
                        else {xxx[j]=xxx[j].strip();}
                    }
                    s = s + xxx[j] + "|";
                }
                s = s +  "\n";
                fw.write(s);
            }
            fw.flush();
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(file_lib.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(file_lib.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
}
}
 

