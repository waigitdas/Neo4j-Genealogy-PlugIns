/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.excelLib;

import gen.neo4jlib.neo4j_info;
import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author david
 */
public class write_open_csv {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // TODO code application logic here
    }

    public static void write_csv(String content) {
        try{
        String f =neo4j_info.Import_Dir + gen.neo4jlib.neo4j_info.user_database +  "_project_stats.csv";
        File fn = new File(f);
        FileWriter fw = new FileWriter(fn);
        fw.write(content);
        fw.flush();
        fw.close();
        Desktop.getDesktop().open(new File(f));      
        
        }
        catch (Exception e) {};

   

    }

}
