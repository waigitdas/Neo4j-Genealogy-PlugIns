/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;
import gen.neo4jlib.neo4j_qry;
//    import org.neo4j.driver.AuthTokens;
//    import org.neo4j.driver.net.ServerAddress;
   
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;
    import org.neo4j.procedure.Description;
  
            
      
    public class relationship {          
        @UserFunction
        @Description("Input length of path of two descendants to the common ancestor and the number of common ancestors. Returns the relationship is MRCA count <3. Used in other queries to simplify query and speed processing")
        
    public String relationship_from_path(
        @Name("path1") 
            Long path1,
        @Name("path2") 
            Long path2,
        @Name("mrca_ct") 
            Long mrca_ct
  )

         { 
        try{
        if (mrca_ct >2) {return "n/a"; }
        String cq="";
        if (path1<path2){
        cq = "match (f:fam_rel) where f.nmrca=" + mrca_ct + " and f.path1=" + path1 + " and f.path2=" +path2 + " return f.relationship" ;    
        }
        else{
         cq = "match (f:fam_rel) where f.nmrca=" + mrca_ct + " and f.path1=" + path2 + " and f.path2=" +path1 + " return f.relationship" ;    
           
        }
        String r =mrca_qry(cq);
        if (r =="") {r = "n/a";}
        return r;
        }
        catch (Exception e) {return "n/a";}
     }
   
    public String mrca_qry(String cq) 
    {
        return neo4j_qry.qry_str(cq);
    }

    
}