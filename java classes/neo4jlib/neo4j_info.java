/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.neo4jlib;

import gen.auth.AuthInfo;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

//reads text file with user name, password, folders relevant to the functions. 
//this strategy mitigates the need for users to input this info into the function calls

public class neo4j_info {
    public static String Import_Dir;
    public static String neo4j_username;
    public static String neo4j_password;
    public static String tg_logic ;
    public static String user_database ;
    //public static Driver driver_instance;
    
public static void neo4j_var() {
            
    String c = file_lib.readFileByLine("C:\\Genealogy\\Neo4j\\neo4j.wai");
    String[] s = c.split("\n");
    int ct = 0;
    for (int i=1; i < s.length; i++) {  //first row is comment
        String[] ss = s[i].split(":");
        switch (ss[0]){
            case "neo4j_import_directory": Import_Dir = s[ct+1].replace("neo4j_import_directory: ", "");
           break; 
            case "neo4j_user name": neo4j_username = ss[1].strip();
            break;
            case "neo4j_password": neo4j_password = ss[1].strip();
            break;
            case "user_database": user_database = ss[1].strip();        }
        ct = ct + 1;
       

    }
    
    tg_logic = "s.chr=t.chr and t.strt_pos<=s.strt_pos and t.end_pos>=s.end_pos and s.cm>=7 and s.snp>=500";

// testing results    
// System.out.println(neo4j_username);
// System.out.println(neo4j_password);
// System.out.println(Import_Dir);
// System.out.println(user_database);
  } 

  
public static void main(String args[]){
    neo4j_var();
}

//private static String getItem(String s) {
//    String[] c = s.split(":");
//    return c[1].strip();
//
//}


//public static void startDriver(){
//        var myToken = AuthInfo.getToken();
//        //Driver driver;
//        driver_instance = GraphDatabase.driver( "bolt://localhost:7687", myToken );
//        //return driver;
//}
        
}