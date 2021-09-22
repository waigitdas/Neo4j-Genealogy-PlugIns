/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.tgs;

import gen.neo4jlib.neo4j_qry;
import java.util.List;
import org.neo4j.driver.types.Path;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 *
 * @author david
 */
public class Create_at_haplotree {
   @UserFunction
    @Description("Add triangulation group list as property to DNA tester Person nodes and adds Label DNA_Match. Then infers ancestor TG list from each branch of their descendants")
        
    public List<Path> at_haplotree(
        @Name("project") 
            String project,
        @Name("ancestor_rn") 
            Long ancestor_rn
    )
    {return null; }

//        {
//        String cq = "";
//        List r =at_ht(project,ancestor_rn);
//        //List<Path> p = gen.neo4jlib.neo4j_qry.qry_obj_list(cq);
//        return p;
//            }
    
   
//    public List<Path> at_ht(String project, Long ancestor_rn) 
//    {
//     return neo4j_qry.qry_str_list(qry);
//}
 
    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
}
