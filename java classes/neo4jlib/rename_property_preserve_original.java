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
public class rename_property_preserve_original {

    public static void rename(String node, String property_name){
        String cq = " match (p:" + node + ") set p.orig_" + property_name + "=p.fullname" + property_name ;
        gen.neo4jlib.neo4j_qry.qry_write(cq);
        
        
    }

    public static void main(String args[]) {
        // TODO code application logic here
    }
}
