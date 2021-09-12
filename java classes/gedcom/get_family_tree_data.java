/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.gedcom;

/**
 *
 * @author david
 */
public class get_family_tree_data {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
    public static String getPersonFromRN(Long RN, String db) {
        //String s = gen.neo4jlib.neo4j_qry.qry_str("match (p:Person{RN:1}) return p.fullname + ' [' + p.RN + '] (' + right(p.BDGed,4) +'-' + right(p.DDGed,4) + ')'", db);
        //return s;
        return "xxx";
}
}
