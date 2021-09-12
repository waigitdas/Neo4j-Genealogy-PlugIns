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
  
            
      
    public class mrca1 {          
        @UserFunction
        @Description("Input 2 RNs and get list of MRCAs")
        
    public String mrca_str(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2,
        @Name("db") 
            String db
  )
    {
         { 
        String cq = "match (p1:Person{RN:" + rn1 + "})-[r1:father|mother*0..15]->(mrca:Person)<-[r2:father|mother*0..15]-(p2:Person{RN:" + rn2 + "}) with mrca.fullname + ' [' + mrca.RN + '] (' + left(mrca.BD,4) +'-' + left(mrca.DD,4) +')' as mrca_indv return collect(mrca_indv) as mrca" ;    
        String r =mrca_qry(cq,db);
        return r;
            }
     }
   
    public String mrca_qry(String cq,String db) 
    {
        return neo4j_qry.qry_str(cq, db);
    }

}