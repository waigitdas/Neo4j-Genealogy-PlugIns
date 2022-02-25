/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.genlib;

import gen.dna.load_ftdna_files;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import static java.nio.file.Files.list;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static scala.reflect.internal.util.NoFile.file;


/**
 *
 * @author david
 */
public class dir {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
//        String k =getDir6("E:\\DAS_Coded_BU_2017\\Genealogy\\DNA\\wai_dna\\","B51965");
//        
//        //List<String> ls = getDir3("E:\\DAS_Coded_BU_2017\\Genealogy\\DNA\\wai_dna",1,"David Stumpf"); //gets files only
//        System.out.println(k);
    }



    public static String getDirOfKit(String dir,String srchKit){
        String kit="";
        File file = new File(dir);
        String[] names = file.list();

        for(String name : names)
        {
            if (new File(dir + name).isDirectory())
            {
                File folder = new File(dir + name);
                File[] listOfFiles = folder.listFiles();
                for (int i=0; i< listOfFiles.length; i++){
                    String f = listOfFiles[i].getName();
                    if (f.contains("Chromosome_Browser") || f.contains("Family_Finder") || f.contains("Y_DNA_Matches")){
                        String[] k =f.split("_");
                        kit= k[0];
                        if (kit.equals(srchKit)){
                        return name;
                        }
                  }
                 }
            }
    }
           return "None";
    }

    
   public static String getDir6_bu(String dir,String srchKit){
     File file = new File(dir);
    String[] names = file.list();

    for(String name : names)
    {
        if (new File(dir + name).isDirectory())
        {
            System.out.println(name);
        }
}
       return "";
    }




    
    public static void getDir5(String root_dir){
        File file = new File(root_dir);
        String[] directories = file.list(new FilenameFilter() {
        @Override
        public boolean accept(File current, String name) {
            return new File(current, name).isDirectory();
          }
        });

    }



//    public static void traverseDir(Path path) {
//    try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
//        for (Path entry : stream) {
//            if (Files.isDirectory(entry)) {
//                System.out.println("Sub-Folder Name : " + entry.toString());
//                traverseDir(entry);
//            } else {
//                System.out.println("\tFile Name : " + entry.toString());
//            }
//        }
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
//}
    
        //String[] dl = d.list(filter);
                
//        for (int i=0; i<dl.length; i++){
//        System.out.println(dl[i]);  //.getClass().getName());
//    }
        
        
         //String contents[] = d.list();
         
//         return "";
 //   }
    
    
    public static Path getDir4(String dir,int depth, String s){
        //iterates each folder/subfolder in the path
        Path p = Paths.get(dir);
       Iterator<Path> elements
            = p.iterator();
  
        // Displaying the values
        System.out.println("The iterator values are: ");
        while (elements.hasNext()) {
            System.out.println(elements.next());
        }
        return p;
    }
    
    public static List<String> getDir3(String dir,int depth, String s) {
           try (Stream<Path> stream = Files.walk(Paths.get(dir), depth)) {
        return stream
                .filter(file -> !Files.isDirectory(file))
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
    }   catch (IOException ex) {
            Logger.getLogger(dir.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
           
    }    
    
    public static String getDir2(String s){
        Path directory = Paths.get("E:\\DAS_Coded_BU_2017\\Genealogy\\DNA\\wai_dna");
        
       
return "x";
    }
    
    public static void getDir(){
        Path directory = Paths.get("E:\\DAS_Coded_BU_2017\\Genealogy\\DNA\\wai_dna");
        String s = "";
       
        try {
            Files.walk(directory).filter(entry -> !entry.equals(directory))
                    .filter(Files::isDirectory).forEach(subdirectory ->
                    {
                        // process subdirectories
                       //addItem(s,subdirectory.getFileName());
                        System.out.println(subdirectory);  //.getFileName());
                        
                    });     } 
        catch (IOException ex) {
            Logger.getLogger(dir.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    
    public static String addItem(String s, String addedStr){
        s = s + addedStr;
        return s;
    }
    
    
    
    
    
    
    
    
    
    
    
    public static void getFiles(){
        
        final List<Path> files = new ArrayList<>();
        Path path = Paths.get("E:\\DAS_Coded_BU_2017\\Genealogy\\DNA\\wai_dna");      
        try{
            Files.walk(path).forEach(entry -> files.add(entry));
            
    }
        catch (IOException ex){}
        
     for ( Path s : files){
      System.out.println(s);
    }
      //System.out.println(path.toString());
        
      System.out.println("+++++++++++++++++++++++++++++{");
      
//        for (int i = 0; i < files.size(); i++) {
//      System.out.println(files.get(i));
//    }
//        
        //System.out.println(files);
        
    }
}
