/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;
import gen.neo4jlib.neo4j_qry;
import static java.lang.Integer.max;
import static java.lang.Integer.min;
//    import org.neo4j.driver.AuthTokens;
//    import org.neo4j.driver.net.ServerAddress;
   
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;
    import org.neo4j.procedure.Description;
  
            
      
    public class relationship {          
        @UserFunction
        @Description("Input length of path of two descendants to the common ancestor and the number of common ancestors. Returns the relationship if MRCA count <3. Used in other queries to simplify query and speed processing.")
        
    public String relationship_from_path(
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
         cq = "match (f:fam_rel) where f.Indx='" + Indx + "' return f.relationship" ;    
        
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