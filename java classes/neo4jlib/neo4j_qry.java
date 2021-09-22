/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.neo4jlib;
    import gen.conn.connTest;
    import gen.conn.AuthInfo;
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
    import java.util.regex.Matcher;
    import java.util.regex.Pattern;
import org.neo4j.graphdb.Path;


public class neo4j_qry {
    
    
    public static void CreateIndex(String nodeNm,String propertyNm){
            try{
                String cq ="CREATE INDEX " + nodeNm + "_" + propertyNm + " FOR (n:" + nodeNm + ") ON (n." + propertyNm + ")";
                qry_write(cq);
            }
            catch (Exception e) {}
    }   

    
    public static void qry_write (String cq) {
        gen.conn.connTest.cstatus();
        Session session =  gen.conn.connTest.session;
           
            {
            
                session.writeTransaction( tx -> {
                   Result result = tx.run( cq );
               return 1;
               
                } );
            } 
   
           }
   
   //****************************************************
   
   public static List<String> qry_str_list(String cq) {
        gen.conn.connTest.cstatus();
        Session java_session =  gen.conn.connTest.session;

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
   
   //****************************************************
     public static List<Long> qry_long_list(String cq) {

        
         gen.conn.connTest.cstatus();
        Session java_session =  gen.conn.connTest.session;
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
   
    public static List<Object> qry_obj_list(String cq) {
 
        gen.conn.connTest.cstatus();
        Session java_session =  gen.conn.connTest.session;


        return java_session.readTransaction( tx -> {
            List<Object> names = new ArrayList<>();
            Result result = tx.run(cq );
            while ( result.hasNext() )
            {
                names.add(result.next().get( 0 ).asObject() );
            }
            return names;
        } );
   }
   
//    public static List<Path> qry_obj_list(String cq) {
// 
//        gen.conn.connTest.cstatus();
//        Session java_session =  gen.conn.connTest.session;
//
//
//        return java_session.readTransaction( tx -> {
//            List<Path> names = new ArrayList<>();
//            Result result = tx.run(cq );
//            while ( result.hasNext() )
//            {
//                names.add(result.next().get( 0 ).asPath());
//            }
//            return names;
//        } );
//   }
  
  
      public static String qry_str(String cq) {
        gen.conn.connTest.cstatus();
        Session java_session =  gen.conn.connTest.session;
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
                        

                       output = output.substring(0, output.length()-2);
                       return output;
                            
                    
                }
            } );
      return javasession;
   }
   
   
    //****************************************************
 
 public static String qry_to_pipe_delimited(String cq,String csv_File) {
        gen.conn.connTest.cstatus();
        Session java_session =  gen.conn.connTest.session;

            return java_session.readTransaction( tx -> {
            String names = "";
            
            String q = "call apoc.export.csv.query(\"" + cq+ "\",'" + csv_File + "' , {delim:'|', quotes: false, format: 'plain'})"; 
                        
            tx.run(q);
  
                        
            java_session.close();
             return names;
        } 
            );
           
        // }
      
   }
   
    //****************************************************
 
 public static String qry_to_csv(String cq,String csv_File) {
        gen.conn.connTest.cstatus();
        Session java_session =  gen.conn.connTest.session;

            return java_session.readTransaction( tx -> {
            String names = "";
            
            String q = "call apoc.export.csv.query(\"" + cq+ "\",'" + csv_File + "' , {delim:',', quotes: true, format: 'plain'})"; 
                        
            tx.run(q);
                    
            java_session.close();
            return csv_File;
        } 
            );
     
   }
   
 

public static void APOCPeriodicIterateCSV(String LoadCSV, String ReplaceCypher, int batchsize) {
    String Q = "\"";
    String cq = "CALL apoc.periodic.iterate(" + Q + LoadCSV + Q + ", " + Q + ReplaceCypher + Q + ",{batchSize: " + batchsize + ", parallel:true, iterateList:true, retries:25})";
    qry_write(cq);
}
}
        