/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.neo4jlib;

import gen.neo4jlib.neo4j_qry;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class db_backup {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String udf_name_seen_in_listing(
        @Name("db") 
            String db
//        @Name("rn2") 
//            Long rn2
  )
   
         { 
             
        backup(db);
         return "";
            }

    
    
    public static void main(String args[]) {
        backup("avitts");
    }
    
     public static String backup(String db) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        String binDir = gen.neo4jlib.neo4j_info.Database_Dir + "\\bin";
        
        
       File file = new File(binDir);
        String[] names = file.list();

        for(String name : names)
        {
//            System.out.println(name);
//            if (new File(dir + name).isDirectory())
//            {
//                File folder = new File(dir + name);
//                File[] listOfFiles = folder.listFiles();
//                for (int i=0; i< listOfFiles.length; i++){
//                    String f = listOfFiles[i].getName();
//                    if (f.contains("Chromosome_Browser") || f.contains("Family_Finder") || f.contains("Y_DNA_Matches")){
//                        String[] k =f.split("_");
//                        kit= k[0];
//                        if (kit.equals(srchKit)){
//                        return name;
//                        }
//                  }
//                 }
//            }
    }
        
        try
        {
            Runtime runtime = Runtime.getRuntime();
            String q = "\"";
            String psc =  binDir.replace("\\","/") + "/neo4j-admin.bat ";
//            psc = "cmd powershell \"\\Test\\Powershell\\powershell.ps1\" ";

            Process proc = runtime.exec(q + "cmd powershell " + psc + q );
            proc.getOutputStream().close();

            InputStream is = proc.getInputStream();

            InputStreamReader isr = new InputStreamReader(is);

            BufferedReader reader = new BufferedReader(isr);

            String line;

            while ((line = reader.readLine()) != null)
            {
                System.out.println(line);
            }

            reader.close();

            proc.getOutputStream().close();

    
//            Stream<Path> f = Files.list(Paths.get(binDir));
//            System.out.println(f);
//            Process process = Runtime.getRuntime().exec(binDir + "neo4j-admin");
//            System.out.println(binDir);
        }
        catch(Exception e)
        {
            System.out.println("***" + e.getMessage());
        }
        
        
        return "";
    }
}
