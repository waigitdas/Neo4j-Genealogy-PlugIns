/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import static java.lang.Integer.max;
import static java.lang.Integer.min;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 *
 * @author david
 */
public class shared_cm_dna {
        @UserFunction
        @Description("Input length of path of two descendants to the common ancestor and the number of common ancestors. Returns the relationship if MRCA count <3. Used in other queries to simplify query and speed processing")
        
    public String expected_cm(
        @Name("mrca_ct") 
            Long mrca_ct,
        @Name("path1") 
            Long path1,
        @Name("path2") 
            Long path2
  )

         { 
       
        if (mrca_ct >2) {return "--"; }
        String cq="";

        String Indx = String.valueOf(min(path1.intValue(),path2.intValue())).strip() + ":" + String.valueOf(max(path1.intValue(), path2.intValue())).strip() + ":"+ String.valueOf(mrca_ct).strip();
         cq = "match (f:fam_rel) where f.Indx='" + Indx + "' return f.MeanSharedCM as cm" ;    
        
         try{
        String r = mrca_qry(cq);
        if (r =="") {r = "--";}
        return r;
        }
        catch (Exception e) {return "error\n\n" + Indx  +"\n\n" + cq;}
     }
   
    public String mrca_qry(String cq) 
    {
        return neo4j_qry.qry_str(cq);
    }

    
}