/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.gedcom;

import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
import gen.neo4jlib.neo4j_info;
  
public class get_family_tree_data {
    @UserFunction
    @Description("Create a list of Persons their RN and kit number for curation.")


    public String person_from_rn(
        @Name("rn") 
            Long rn
    )

        {

        String r =getPersonFromRN(rn);
        
        return r;
            }
    

    public static void main(String args[]) {
        // TODO code application logic here
    }
    
    public static String getPersonFromRN(Long rn) {
        String s = gen.neo4jlib.neo4j_qry.qry_str("match (p:Person{RN:" + rn + "}) return p.fullname + ' " + gen.neo4jlib.neo4j_info.alt_left_bracket + "' + p.RN + '" + gen.neo4jlib.neo4j_info.alt_right_bracket + " (' + right(p.BDGed,4) +'-' + right(p.DDGed,4) + ')'");
        return s.replace("[", "").replace("]", "").replace("\"", "");
        //return "xxx";
}
}
