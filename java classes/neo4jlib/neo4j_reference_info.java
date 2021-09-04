/**
 * Copyright 2020 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.neo4jlib;

/**
 *
 * @author david
 */
public class neo4j_reference_info {
    public static String Import_Dir;
    public static String neo4j_username;
    public static String neo4j_password;
    
public static void neo4j_var() {
            
    String c = file_lib.readFileByLine("C:\\Genealogy\\Neo4j\\neo4j.wai");
    String[] s = c.split("\n");

    neo4j_username = getItem(s[1]);
    neo4j_password = getItem(s[2]);
    Import_Dir=s[3].replace("neo4j import_directory: ", "");
  } 

  


private static String getItem(String s) {
    String[] c = s.split(":");
    return c[1].strip();

}


}