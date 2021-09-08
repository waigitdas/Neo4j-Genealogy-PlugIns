/**
 * Copyright 2020 
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
import java.util.Scanner;
import java.util.stream.Stream;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.*;

/**
 *
 * @author david
 */
public class file_lib {

    /**
     * @param args the command line arguments
     */
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
     catch (Exception e) {System.out.println("Error!");};
    }
   
 
//public static void get_file_transform_put_in_import_dir(String filePathRead, String filePathSave){
//    String c = file_lib.readFileByLine(filePathRead);
//    //System.out.println(c);
//    
//    c = c.replace("|","").replace(",","|").replace("\"", " ");
//    String[] cc = c.split("\n");
//    String ccc = cc[0].replace(" ","_");
//    c = c.replace(cc[0], ccc);
//    //System.out.println(c);
//    file_lib.writeFile(c,neo4j_info.Import_Dir +  filePathSave);
//     
// }
 

//    public static String readFileWithEscChar(String filePath){
//        File f = new File("C:\\Users\\SV7104\\Desktop\\sampletest.txt");
//Scanner sc = new Scanner(f).useDelimiter(Pattern.compile("\\s*\\u0002\\n\\s*"));
//            while (sc.hasNext()) {
//                System.out.print(1);
//                System.out.println(sc.next().toString().replaceAll("\\u0001\\n", " "));
//
//            }
//    }
}
