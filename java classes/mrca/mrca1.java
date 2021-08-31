/**
 * Copyright 2020 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.mrca;
    import gen.auth.AuthInfo;
//    import org.neo4j.driver.AuthTokens;
    import org.neo4j.driver.Driver;
    import org.neo4j.driver.GraphDatabase;
    import org.neo4j.driver.Result;
    import org.neo4j.driver.Session;    
    import org.neo4j.driver.SessionConfig;
    import org.neo4j.driver.Transaction;
    import org.neo4j.driver.TransactionWork;
    import org.neo4j.driver.AccessMode;
    import org.neo4j.driver.AuthToken;
//    import org.neo4j.driver.net.ServerAddress;
   
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;
    import org.neo4j.procedure.Description;
  
            
    import static org.neo4j.driver.Values.parameters;
      
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
        String q = "match (p1:Person{RN:" + rn1 + "})-[r1:father|mother*0..15]->(mrca:Person)<-[r2:father|mother*0..15]-(p2:Person{RN:" + rn2 + "}) with mrca.fullname + ' [' + mrca.RN + '] (' + left(mrca.BD,4) +'-' + left(mrca.DD,4) +')' as mrca_indv return collect(mrca_indv) as mrca" ;    
        String r =mrca_qry(q,rn1,rn2,db);
        return r;
            }
     }
   
    public String mrca_qry(String qry, long rn1, long rn2,String db) 
    {
        AuthToken myToken = AuthInfo.getToken();
        Driver driver;
        driver = GraphDatabase.driver( "bolt://localhost:7687", myToken );
        driver.session(SessionConfig.builder().withDefaultAccessMode(AccessMode.READ).build());
//        Session session = driver.session() ;
        try ( Session java_session = driver.session(SessionConfig.forDatabase(db)) )
        { 
            String javasession = java_session.writeTransaction(new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    Result rslt = tx.run( qry,
                            parameters( "message", qry ) );

                    
                    String output = "";
                        while (rslt.hasNext())
                        { 
                            output = output + rslt.next().values().toString() + "; ";
                        
                        }
	                return output;
                            
                    
                }
            } );
      return javasession;
        }
    }

}