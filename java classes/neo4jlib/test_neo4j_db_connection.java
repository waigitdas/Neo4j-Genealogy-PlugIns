/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.neo4jlib;

/**
 *
 * @author david
 */
public class test_neo4j_db_connection {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();                
        
        System.out.println(gen.neo4jlib.neo4j_info.user_database + "\n" + gen.neo4jlib.neo4j_info.neo4j_password);
        
        
try
{
        String c = gen.neo4jlib.neo4j_qry.qry_to_csv("call dbms.listConfig('server.directories') yield name,value  with value where name='server.directories.neo4j_home' return value");
        System.out.println("Server directory: " + c);
        c = gen.neo4jlib.neo4j_qry.qry_to_csv("SHOW FUNCTIONS");
        System.out.println(c);
        gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited("SHOW FUNCTIONS", "xxx");

}
catch(Exception e)
{
    System.out.println("*error #1101: " + e.getMessage());
}
    }
}
