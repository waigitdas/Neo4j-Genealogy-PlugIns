/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;
import gen.neo4jlib.neo4j_qry;
import java.util.List;
//    import org.neo4j.driver.AuthTokens;
//    import org.neo4j.driver.net.ServerAddress;
    import gen.conn.connTest;
   
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;
    import org.neo4j.procedure.Description;
  
            
      
    public class mrca2_rns {          
        @UserFunction
        @Description("Input 2 RNs and get list of mrca RNs")
        
    public String mrca_rn(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
    {
         { 
        //gen.neo4jlib.neo4j_info.neo4j_var();
       gen.conn.connTest.cstatus();
             
        String cq = "match (p1:Person{RN:" + rn1 + "})-[r1:father|mother*0..15]->(mrca:Person)<-[r2:father|mother*0..15]-(p2:Person{RN:" + rn2 + "})  with mrca order by mrca.sex desc return mrca.RN as rn" ;    
        String r =mrca_qry_rn(cq).replace("]; [", ", ");
        r = r.replace("[","").replace("]","");
        return r;
            }
     }
   
    public String mrca_qry_rn(String cq) 
    {
        return neo4j_qry.qry_str(cq);
    }

}