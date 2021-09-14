/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.neo4jlib;
    import gen.auth.AuthInfo;
    import java.util.ArrayList;
    import gen.neo4jlib.neo4j_info;
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
    import java.util.regex.Matcher;
    import java.util.regex.Pattern;


public class neo4j_qry {
    
    
    public static void CreateIndex(String nodeNm,String propertyNm){
            try{
                String cq ="CREATE INDEX " + nodeNm + "_" + propertyNm + " FOR (n:" + nodeNm + ") ON (n." + propertyNm + ")";
                qry_write(cq);
            }
            catch (Exception e) {}
    }   

    
    public static void qry_write (String cq) {
        var myToken = AuthInfo.getToken();
        Driver driver;
        driver = GraphDatabase.driver( "bolt://localhost:7687", myToken );
 
            Session session = driver.session(SessionConfig.forDatabase(gen.neo4jlib.neo4j_info.user_database));
           
            {
                session.writeTransaction( tx -> {
                   Result result = tx.run( cq );
//                   session.close();
//                   driver.close();
                return 1;
                } );
            }
            
            
            
   }
   
   //****************************************************
   
   public static List<String> qry_str_list(String cq) {
        var myToken = AuthInfo.getToken();
        Driver driver;
        driver = GraphDatabase.driver( "bolt://localhost:7687", myToken );
        driver.session(SessionConfig.builder().withDefaultAccessMode(AccessMode.READ).build());
 
        try ( Session java_session = driver.session(SessionConfig.forDatabase(gen.neo4jlib.neo4j_info.user_database)) )
                {
        return java_session.readTransaction( tx -> {
            List<String> names = new ArrayList<>();
            Result result = tx.run(cq );
            while ( result.hasNext() )
            {
                names.add( result.next().get( 0 ).asString() );
            }
//            java_session.close();
//            driver.close();
            return names;
        } );
    }
   }
   
   //****************************************************
     public static List<Long> qry_long_list(String cq) {
        var myToken = AuthInfo.getToken();
           
        Driver driver;
        driver = GraphDatabase.driver( "bolt://localhost:7687", myToken );
        driver.session(SessionConfig.builder().withDefaultAccessMode(AccessMode.READ).build());
 
        try ( Session java_session = driver.session(SessionConfig.forDatabase(gen.neo4jlib.neo4j_info.user_database)) )
                {
        return java_session.readTransaction( tx -> {
            List<Long> names = new ArrayList<>();
            Result result = tx.run(cq );
            while ( result.hasNext() )
            {
                names.add(result.next().get( 0 ).asLong() );
            }
//            java_session.close();
//            driver.close();
            return names;
        } );
    }
   }
   
    public static List<Object> qry_obj_list(String cq) {
        
        var myToken = AuthInfo.getToken();
           
        Driver driver;
        driver = GraphDatabase.driver( "bolt://localhost:7687", myToken );
        driver.session(SessionConfig.builder().withDefaultAccessMode(AccessMode.READ).build());
 
        try ( Session java_session = driver.session(SessionConfig.forDatabase(gen.neo4jlib.neo4j_info.user_database)) )
                {
        return java_session.readTransaction( tx -> {
            List<Object> names = new ArrayList<>();
            Result result = tx.run(cq );
            while ( result.hasNext() )
            {
                names.add(result.next().get( 0 ).asObject() );
            }
//            java_session.close();
//            driver.close();
            return names;
        } );
    }
   }
   
  
  
      public static String qry_str(String cq) {
        var myToken = AuthInfo.getToken();
        Driver driver;
        driver = GraphDatabase.driver( "bolt://localhost:7687", myToken );
        driver.session(SessionConfig.builder().withDefaultAccessMode(AccessMode.READ).build());
        try ( Session java_session = driver.session(SessionConfig.forDatabase(gen.neo4jlib.neo4j_info.user_database)) )
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
                        
//                       java_session.close();
//                       driver.close();
                       //output = output.replace("[", "").replace("]", "").replace("\"", "");
                       output = output.substring(0, output.length()-2);
                       return output;
                            
                    
                }
            } );
      return javasession;
        }
   }
   
//    //****************************************************
//      public static List<Long> qry_long(String cq) {
//        var myToken = AuthInfo.getToken();
//        Driver driver;
//        driver = GraphDatabase.driver( "bolt://localhost:7687", myToken );
//        driver.session(SessionConfig.builder().withDefaultAccessMode(AccessMode.READ).build());
//        try ( Session java_session = driver.session(SessionConfig.forDatabase(gen.neo4jlib.neo4j_info.user_database)) )
//        { 
//            String javasession = java_session.writeTransaction(new TransactionWork<String>()
//            {
//                @Override
//                public String execute( Transaction tx )
//                {
//                    Result rslt = tx.run( cq,
//                            parameters( "message", cq ) );
//                    
//                        List<Long> output;
//                        while (rslt.hasNext())
//                        { 
//                            output.add(rslt.next().get(arg0, arg1)) ;
//                        
//                        }
//                        
////                       java_session.close();
////                       driver.close();
//                       //output = output.replace("[", "").replace("]", "").replace("\"", "");
//                       output = output.substring(0, output.length()-2);
//                       return output;
//                            
//                    
//                }
//            } );
//      return javasession;
//        }
//   }
   
    //****************************************************
 
 public static String qry_to_pipe_delimited(String cq,String csv_File) {
        //NEED KEYS AND VALUES
        var myToken = AuthInfo.getToken();
        Driver driver;
        driver = GraphDatabase.driver( "bolt://localhost:7687", myToken );
        driver.session(SessionConfig.builder().withDefaultAccessMode(AccessMode.READ).build());

         try ( Session java_session = driver.session(SessionConfig.forDatabase(gen.neo4jlib.neo4j_info.user_database)) )
        { 
            return java_session.readTransaction( tx -> {
            String names = "";
            
            String q = "call apoc.export.csv.query(\"" + cq+ "\",'" + csv_File + "' , {delim:'|', quotes: false, format: 'plain'})"; 
                        
            tx.run(q);
  
            //file_lib.writeFile(names, csv_File);
                        
            java_session.close();
            driver.close();    
            return names;
        } 
            );
           
         }
      
   }
   
    //****************************************************
 
 public static String qry_to_csv(String cq,String csv_File) {
        //NEED KEYS AND VALUES
        var myToken = AuthInfo.getToken();
        Driver driver;
        driver = GraphDatabase.driver( "bolt://localhost:7687", myToken );
        driver.session(SessionConfig.builder().withDefaultAccessMode(AccessMode.READ).build());

         try ( Session java_session = driver.session(SessionConfig.forDatabase(gen.neo4jlib.neo4j_info.user_database)) )
        { 
            return java_session.readTransaction( tx -> {
            String names = "";
            
            String q = "call apoc.export.csv.query(\"" + cq+ "\",'" + csv_File + "' , {delim:',', quotes: true, format: 'plain'})"; 
                        
            tx.run(q);
  
            //file_lib.writeFile(names, csv_File);
                        
            java_session.close();
            driver.close();    
            return csv_File;
        } 
            );
           
         }
      
   }
   
 

public static void APOCPeriodicIterateCSV(String LoadCSV, String ReplaceCypher, int batchsize) {
    String Q = "\"";
    String cq = "CALL apoc.periodic.iterate(" + Q + LoadCSV + Q + ", " + Q + ReplaceCypher + Q + ",{batchSize: " + batchsize + ", parallel:true, iterateList:true, retries:25})";
    qry_write(cq);
}
}
        