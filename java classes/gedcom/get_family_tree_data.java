/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.gedcom;

import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

  
/**
 *
 * @author david
 */
public class get_family_tree_data {
    @UserFunction
    @Description("Load a GEDCOM into Neo4j creating Person, Union and Place nodes and the edges connecting them.")


    public String person_from_rn(
        @Name("rn") 
            Long rn
    )

        {

        String r =getPersonFromRN(rn);
        
        return r;
            }
    


    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
    public static String getPersonFromRN(Long rn) {
        String s = gen.neo4jlib.neo4j_qry.qry_str("match (p:Person{RN:" + rn + "}) return p.fullname + ' [' + p.RN + '] (' + right(p.BDGed,4) +'-' + right(p.DDGed,4) + ')'");
        return s.replace("[", "").replace("]", "").replace("\"", "");
        //return "xxx";
}
}
