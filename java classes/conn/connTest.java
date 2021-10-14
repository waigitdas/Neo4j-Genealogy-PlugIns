/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.conn;


    import org.neo4j.driver.Driver;
    import org.neo4j.driver.GraphDatabase;
    import org.neo4j.driver.Session;
    import org.neo4j.driver.SessionConfig;
    import org.neo4j.driver.AuthTokens; 
    import org.neo4j.driver.AuthToken;
    import org.neo4j.procedure.Description;
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;


public class connTest {
        public static Boolean isSessionOpen;    
        public static Session session;
        public static Driver driver;
        @UserFunction
        @Description("Checks connection and restrats if broken; otherwise re-uses existing connection")
   
        
    public  String connStatus()      
    {
        String s = cstatus();
        return s;
      }
  
       public static String cstatus() {
        String s ;
        
        Boolean b = gen.neo4jlib.neo4j_info.neo4j_var();
        if (driver == null) {
            gen.neo4jlib.neo4j_info.neo4j_var();
            AuthToken myToken = AuthInfo.getToken();
            driver = GraphDatabase.driver( "bolt://localhost:7687", myToken );
            session = driver.session(SessionConfig.forDatabase(gen.neo4jlib.neo4j_info.user_database));
            isSessionOpen = true;
        }
        else { 
            try{session.close();}
            catch (Exception e) {}
            session = driver.session(SessionConfig.forDatabase(gen.neo4jlib.neo4j_info.user_database));

        }
        
           Boolean isOpen = session.isOpen();
           s = "Session closed";
           if (isOpen==true) {s = "Session Open";}
           
           //s = s + "\nConnections: " + gen.neo4jlib.neo4j_qry.qry_str("call dbms.listConnections() YIELD connectionId with collect(connectionId) as cid return cid");
           
           //driver.verifyConnectivity();
        return s;
    }
    
    

    public static void main(String args[]) {
        cstatus();
    }
    
}
