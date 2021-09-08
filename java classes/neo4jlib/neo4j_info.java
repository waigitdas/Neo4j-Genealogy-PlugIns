/**
 * Copyright 2020 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.neo4jlib;

import gen.auth.AuthInfo;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

/**
 *
 * @author david
 */
public class neo4j_info {
    public static String Import_Dir;
    public static String neo4j_username;
    public static String neo4j_password;
    //public static Driver driver_instance;
    
public static void neo4j_var() {
            
    String c = file_lib.readFileByLine("C:\\Genealogy\\Neo4j\\neo4j.wai");
    String[] s = c.split("\n");

    neo4j_username = getItem(s[1]);
    neo4j_password = getItem(s[2]);
    Import_Dir=s[3].replace("neo4j import_directory: ", "");
    //startDriver();
  } 

  


private static String getItem(String s) {
    String[] c = s.split(":");
    return c[1].strip();

}


//public static void startDriver(){
//        var myToken = AuthInfo.getToken();
//        //Driver driver;
//        driver_instance = GraphDatabase.driver( "bolt://localhost:7687", myToken );
//        //return driver;
//}
        
}