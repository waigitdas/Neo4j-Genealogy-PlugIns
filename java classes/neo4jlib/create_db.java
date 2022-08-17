/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.neo4jlib;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class create_db {
    @UserFunction
    @Description("creates a database if it does not already exist.")

    public String create_neo4j_database(
        @Name("db_name") 
            String db_name
  )
   
         { 
             
        String s = createDB(db_name);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String createDB(String db) 
    {
        try {
        gen.neo4jlib.neo4j_qry.qry_write("create database " + db);
        return "done";
        }
        catch(Exception e){return "error";}
            
        }
}
