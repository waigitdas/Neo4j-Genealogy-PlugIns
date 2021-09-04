/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.neo4jlib;
    import gen.auth.AuthInfo;
    import java.util.ArrayList;
    import org.neo4j.driver.Driver;
    import org.neo4j.driver.GraphDatabase;
    import org.neo4j.driver.Result;
    import org.neo4j.driver.Session;    
    import org.neo4j.driver.SessionConfig;
    import java.util.List;       
    import org.neo4j.driver.AccessMode;
    import org.neo4j.driver.Transaction;
    import org.neo4j.driver.TransactionWork;
    import static org.neo4j.driver.Values.parameters;
    import java.util.*;  


public class neo4j_qry {
    
    
    public static void CreateIndex(String nodeNm,String propertyNm,String db){
            try{
                String cq ="CREATE INDEX " + nodeNm + "_" + propertyNm + " FOR (n:" + nodeNm + ") ON (n." + propertyNm + ")";
                qry_str(cq,db);
            }
            catch (Exception e) {}
    }   

    
    public static void qry_write (String cq,String db) {
        var myToken = AuthInfo.getToken();
        Driver driver;
        driver = GraphDatabase.driver( "bolt://localhost:7687", myToken );
 
            Session session = driver.session(SessionConfig.forDatabase(db));
           
            {
                session.writeTransaction( tx -> {
                   Result result = tx.run( cq );
                    return 1;
                } );
            }
            
            
            
   }
   
   //****************************************************
   
   public static List<String> qry_str_list(String cq,String db) {
        var myToken = AuthInfo.getToken();
        Driver driver;
        driver = GraphDatabase.driver( "bolt://localhost:7687", myToken );
        driver.session(SessionConfig.builder().withDefaultAccessMode(AccessMode.READ).build());
 
        try ( Session java_session = driver.session(SessionConfig.forDatabase(db)) )
                {
        return java_session.readTransaction( tx -> {
            List<String> names = new ArrayList<>();
            Result result = tx.run(cq );
            while ( result.hasNext() )
            {
                names.add( result.next().get( 0 ).asString() );
            }
            return names;
        } );
    }
   }
   
   //****************************************************
     public static List<Long> qry_long_list(String cq,String db) {
        var myToken = AuthInfo.getToken();
           
        Driver driver;
        driver = GraphDatabase.driver( "bolt://localhost:7687", myToken );
        driver.session(SessionConfig.builder().withDefaultAccessMode(AccessMode.READ).build());
 
        try ( Session java_session = driver.session(SessionConfig.forDatabase(db)) )
                {
        return java_session.readTransaction( tx -> {
            List<Long> names = new ArrayList<>();
            Result result = tx.run(cq );
            while ( result.hasNext() )
            {
                names.add(result.next().get( 0 ).asLong() );
            }
            return names;
        } );
    }
   }
   
  
    //****************************************************
   
   public static String qry_str(String cq,String db) {
        var myToken = AuthInfo.getToken();
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
                    Result rslt = tx.run( cq,
                            parameters( "message", cq ) );

                    
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
   
    //****************************************************
 
 public static String qry_to_csv(String cq,String db,String csv_File) {
        //NEED KEYS AND VALUES
        var myToken = AuthInfo.getToken();
        Driver driver;
        driver = GraphDatabase.driver( "bolt://localhost:7687", myToken );
        driver.session(SessionConfig.builder().withDefaultAccessMode(AccessMode.READ).build());

         try ( Session java_session = driver.session(SessionConfig.forDatabase(db)) )
        { 
            return java_session.readTransaction( tx -> {
            String names = "";
            
            String q = "call apoc.export.csv.query(\"" + cq+ "\",'" + csv_File + "' , {delim:'|', quotes: false, format: 'plain'})"; 
                        
            tx.run(q);
  
            //file_lib.writeFile(names, csv_File);
                            
            return names;
        } 
            );
           
         }
      
   }
   
}
        