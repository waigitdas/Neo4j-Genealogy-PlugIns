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
    import org.neo4j.driver.Record;
    //import org.javatuples.Pair;
    import org.neo4j.driver.Value;
    import org.neo4j.driver.Values;
    import org.neo4j.graphdb.Path;
    import org.neo4j.driver.util.Pair;

public class neo4j_qry {
    
    
    public static void CreateIndex(String nodeNm,String propertyNm){
            try{
                String cq ="CREATE INDEX " + nodeNm + "_" + propertyNm + " FOR (n:" + nodeNm + ") ON (n." + propertyNm + ")";
                qry_write(cq);
            }
            catch (Exception e) {}
    }   

    public static void CreateCompositeIndex(String nodeNm,String propertyNmList){
            try{
                String[] n = propertyNmList.split(",");
                String s = "";
                for (int i=0; i < n.length; i++) {
                    s = s + "n." + n[i];
                    if (i<n.length-1) {s = s + ",";}
                }
                String cq ="CREATE INDEX " + nodeNm + "_" + s.replace(",","_") + " FOR (n:" + nodeNm + ") ON (" + s + ")";
                qry_write(cq); 
            }
            catch (Exception e) {}
    }   


    public static void CreateRelationshipIndex(String relationship_type,String relationship_property){
            try{
                String cq ="CREATE INDEX rel_" + relationship_type + "_" + relationship_property + " FOR ()-[r:" + relationship_type + "]-() ON (k." + relationship_property + ")";
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
   
    public static String qry_to_csv(String cq) {
        gen.conn.connTest.cstatus();
        Session java_session =  gen.conn.connTest.session;

        return java_session.readTransaction( tx -> {
            String c = "";
             int rw = 0;
            Result result = tx.run(cq );
            while ( result.hasNext() )
            {
                Record r = result.next();
           
                List<Value> v =  r.values();
                  for (int i = 0; i < v.size(); i++) {
                  c = c + String.valueOf(r.values().get(i));
                  if (i <v.size()-1) { c = c + ",";
                  }
                  else { c = c + "\n"; }
                 }
                    rw = rw + 1;
                }
           return c;
        }) ;
    }
     
    
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
   
//    public static Map qry_obj_list(String cq) {
// 
//        gen.conn.connTest.cstatus();
//        Session java_session =  gen.conn.connTest.session;
//
////          graph = new TinkerGraph();
////  while (result.hasNext()) {
////    Map<String, Object> map = result.next();
////  }
//
//        return java_session.readTransaction( tx -> {
//            List<Path> names = new ArrayList<>();
//            Result result = tx.run(cq );
//            while ( result.hasNext() )
//            {
//                names.add(result.next().get( 0 ).as);
//            }
//            return names;
//        } );
//   }
//  
//  
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

            return java_session.writeTransaction( tx -> {
            
            
            String q = "call apoc.export.csv.query(\"" + cq+ "\",'" + csv_File + "' , {delim:'|', stream:true, quotes: false, format: 'plain'})"; 
                        
            tx.run(q);
  
                        
            java_session.close();
             return q;
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
    //use parallel = false to avoid deadlocks!
    String Q = "\"";
    String cq = "CALL apoc.periodic.iterate(" + Q + LoadCSV + Q + ", " + Q + ReplaceCypher + Q + ",{batchSize: " + batchsize + ", parallel:false, iterateList:true, retries:25})";
    qry_write(cq);
}
}
        