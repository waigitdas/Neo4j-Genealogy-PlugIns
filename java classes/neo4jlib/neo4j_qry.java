/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.neo4jlib;
    //import org.neo4j.driver.AuthToken;
    //import org.neo4j.driver.AuthTokens;
    import gen.auth.AuthInfo;
    import org.neo4j.driver.Driver;
    import org.neo4j.driver.GraphDatabase;
    import org.neo4j.driver.Result;
    import org.neo4j.driver.Session;    
    import org.neo4j.driver.SessionConfig;


public class neo4j_qry {

   public static void qry_no_return (String cq,String db) {
        var myToken = AuthInfo.getToken();
        Driver driver;
        driver = GraphDatabase.driver( "bolt://localhost:7687", myToken );
       // Session session = driver.session(SessionConfig.forDatabase(db));
    //driver.session(SessionConfig.builder().withDefaultAccessMode(AccessMode.WRITE).build());
 
            Session session = driver.session(SessionConfig.forDatabase(db));
            {
                session.writeTransaction( tx -> {
                   Result result = tx.run( cq );
                    return 1;
                } );
            }
   }
   
}
        
