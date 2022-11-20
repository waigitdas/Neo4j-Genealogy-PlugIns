/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.debug;

import gen.neo4jlib.neo4j_qry;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
import java.awt.Desktop;

public class envir_info {
    @UserFunction
    @Description("Prepares report to help debug problems.")

    public String environment_check(
  )
   
         { 
             
        String s = check_env();
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String check_env() 
    {
        
    
        String cq="";
        String s = "";
//************************************************************************************        
        cq = "SHOW DATABASES YIELD name, currentStatus return name,currentStatus";
        s = s + "DATABASES\n\n";
        s = s + gen.neo4jlib.neo4j_qry.qry_to_csv(cq) + sep();
        
//************************************************************************************        
        cq = "SHOW TRANSACTIONS";
        s = s + "ACTIVE TTANSACTIONS\n\n";
        s = s + gen.neo4jlib.neo4j_qry.qry_to_csv(cq) + sep();
        
        //************************************************************************************        
        try{
            gen.quality.Data_Summary ds = new gen.quality.Data_Summary();
            ds.understand_your_data();
            s = s + "Know your data completed successfully."  + sep();
        }
        catch (Exception e) {
                    s = s + "Know your data failed to complete with the following error message\n" + e.getMessage() + sep();
        }

        //************************************************************************************        
        try{
            Runtime.Version version = Runtime.version();
            s = s + "JAVA Version: " + version +  "\n";
            s = s + "Java Specification Version: " + System.getProperty("java.specification.version") + "\n";
            s = s + "java Runtime Environment (JRE) version: " + System.getProperty("java.version") + sep();
        }
        catch (Exception e){
        
        }
    //************************************************************************************        
        try{
            s = s + "Neo4j Environment\n\n";
            s = s + " Neo4j connection status: " + gen.conn.connTest.cstatus() + "\n";
            s = s + " Neo4j user database: " + gen.neo4jlib.neo4j_info.user_database + "\n";
            s = s + " Home directory: " + gen.neo4jlib.neo4j_info.neo4j_home_dir + "\n";
            s = s + " Root directory: " + gen.neo4jlib.neo4j_info.root_directory + "\n";
            s = s + " Active project: " + gen.neo4jlib.neo4j_info.project + "\n";
            s = s + " TG file: " + gen.neo4jlib.neo4j_info.tg_file + "\n";
            s = s + " Neo4j user name: " + gen.neo4jlib.neo4j_info.neo4j_username + sep();
            
    //************************************************************************************        
            File folder = new File("c://Genealogy/Neo4j/");
            File[] listOfFiles = folder.listFiles();
            s = s + "FILES IN c://Genealogy/Neo4j\n\n";
            for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                s = s + "File " + listOfFiles[i].getName() + "\n";
            } else if (listOfFiles[i].isDirectory()) {
                s = s + "Directory " + listOfFiles[i].getName();
}
            }
            s = s + sep();
        }
        catch (Exception e){
        s = s + "" + sep();
        }
    //************************************************************************************        
        try{
            
        }
        catch (Exception e){
        s = s + "" + sep();
        }
 
        //************************************************************************************        
        try{
            String f = gen.neo4jlib.neo4j_info.Import_Dir.replace("import","plugins");
            File folder2 = new File(f);
            File[] listOfFiles2 = folder2.listFiles();
            s = s + "FILES IN PLUGIN DIRECTORY\n\n";
            for (int i = 0; i < listOfFiles2.length; i++) {
            if (listOfFiles2[i].isFile()) {
                s = s + "File " + listOfFiles2[i].getName() + "\n";
            } else if (listOfFiles2[i].isDirectory()) {
                s = s + "Directory " + listOfFiles2[i].getName();
}
}
            s = s + sep();
        }
        catch (Exception e){
        s = s + "Error in retrieving pluginsdirectory files" + sep();
        }

//************************************************************************************        
        try{
            File folder = new File(gen.neo4jlib.neo4j_info.Import_Dir);
            File[] listOfFiles = folder.listFiles();
            s = s + "FILES IN IMPORT DIRECTORY\n\n";
            for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                s = s + "File " + listOfFiles[i].getName() + "\n";
            } else if (listOfFiles[i].isDirectory()) {
                s = s + "Directory " + listOfFiles[i].getName();
}
}
            s = s + sep();
        }
        catch (Exception e){
        s = s + "Error in retrieving import directory files" + sep();
        }
    
    
//************************************************************************************        
        try{
            
        }
        catch (Exception e){
        s = s + "" + sep();
        }
    
try{
        File fn = new File(gen.neo4jlib.neo4j_info.Import_Dir + "envir_report.txt");
        FileWriter fw  = new FileWriter(fn);  
        fw.write(s);
        fw.flush();

        fw.close();
        Desktop.getDesktop().open(new File(gen.neo4jlib.neo4j_info.Import_Dir + "envir_report.txt"));  
}
catch(Exception e){}
return s;
    }
        
        public static String sep(){
            return "\n\n_________________________________________________\n\n";
}

}
