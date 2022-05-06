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
import java.util.function.Function;
    import java.util.regex.Matcher;
    import java.util.regex.Pattern;
    import org.neo4j.dbms.api.DatabaseManagementService;
    import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
    import org.neo4j.driver.Record;
    //import org.javatuples.Pair;
    import org.neo4j.driver.Value;
    import org.neo4j.driver.Values;
    import org.neo4j.graphdb.Path;
    //import org.neo4j.graphdb.Map;
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
                String cq ="CREATE INDEX rel_" + relationship_type + "_" + relationship_property + " FOR ()-[r:" + relationship_type + "]-() ON (r." + relationship_property + ")";
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
             //int rw = 0;
            Result result = tx.run(cq.replace("[[","[").replace("]]","]") );
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
                    
                }
            gen.neo4jlib.file_lib.writeFile("**\n" + c  + "\n**\n\n" + cq, "c://temp/csv1.csv");
            java_session.close();
            return c;
        }) ;
        
        
        
    }
     
    public static String qry_to_pipe_delimited_str(String cq) {
        gen.conn.connTest.cstatus();
        Session java_session =  gen.conn.connTest.session;

        return java_session.readTransaction( tx -> {
            String c = "";
             //int rw = 0;
            Result result = tx.run(cq.replace("[[","[").replace("]]","]") );
            while ( result.hasNext() )
            {
                Record r = result.next();
           
                List<Value> v =  r.values();
                  for (int i = 0; i < v.size(); i++) {
                  c = c + String.valueOf(r.values().get(i));
                  if (i <v.size()-1) { c = c + "|";
                  }
                  else { c = c + "\n"; }
                 }
                    //rw = rw + 1;
                }
            gen.neo4jlib.file_lib.writeFile(gen.neo4jlib.neo4j_info.user_database, "c://temp/csv1.csv");
            java_session.close();
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
   
public static <T> List<T> readCyphers(String cq, Function<Record, T> mapper) {
        gen.conn.connTest.cstatus();
        Session java_session =  gen.conn.connTest.session;
        try (java_session) {
            Result result = java_session.run(cq);
            return result.list(mapper);
//return java_session.readTransaction(tx -> tx.run(cypher).list(mapper));
        }
    }
   
public static Result qry_obj_all(String cq) {
        gen.conn.connTest.cstatus();
        Session java_session =  gen.conn.connTest.session;
        
        return java_session.readTransaction( tx -> 
        {
            Result result = tx.run(cq );
            List lr = result.list();
             
            return result; 
        } 
        );
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
//      public static String qry_str(String cq) {
//        gen.conn.connTest.cstatus();
//        Session java_session =  gen.conn.connTest.session;
//            String javasession = java_session.writeTransaction(new TransactionWork<String>()
//            {
//                @Override
//                public String execute( Transaction tx )
//                {
//                    Result rslt = tx.run( cq,
//                            parameters( "message", cq ) );
//                    
//                    String output = "";
//                        while (rslt.hasNext())
//                        { 
//                            output = output + rslt.next().values().toString() + "; ";
//                        
//                        }
//                        
//
//                       output = output.substring(0, output.length()-2);
//                       return output;
//                            
//                    
//                }
//            } );
//      return javasession;
//   }
//   
      public static String qry_str(String cq) {
 
        gen.conn.connTest.cstatus();
        Session java_session =  gen.conn.connTest.session;


        return java_session.readTransaction( tx -> {
            String names = "";
            Result result = tx.run(cq );
            while ( result.hasNext() )
            {
                names = names + result.next().values().toString() + "; ";
            }
            names = names.substring(0, names.length()-2);
            return names;
        } );
   }
  
//        public static String<List> pack_list_str(List<String> ls) {
// 
//        gen.conn.connTest.cstatus();
//        Session java_session =  gen.conn.connTest.session;
//
//
//        return java_session.readTransaction( tx -> {
//            List<String> names =  new ArrayList<String>();;
//            Result result = tx.run(cq);
//            while ( result.hasNext() )
//            {
//                names = names + result.next().values().toString() + "; ";
//            }
//            names = names.substring(0, names.length()-2);
//            return names;
//        } );
//   }
   
    //****************************************************
 
//  public static List<Object> qry_to_graph(String cq){
//      //String q = "CALL apoc.graph.fromCypher( 'MATCH (p:Person)-[r:father]->(a:Person) RETURN *', {}, 'father_son', {description: 'test graph'} ) YIELD graph AS g RETURN g";
//      List<Object> path ;
//
//      return q;
//  }

      
//    public static Path qry_path(String cq) {
//            Session java_session =  gen.conn.connTest.session;
//            return java_session.readTransaction( tx -> {
//            Result r = tx.run(cq) ;
//            Path p=r.next().get(0).asPath();
//           // org.neo4j.driver.types.Path path = r.single().get("p").asPath();        
//           Path path;
//           path= r.next().get("path").asMap("path"); //r.next().get(0).asPath();  //.get("path", Path);   //.get(Path, "path");  //r.single().get("path");
////           while (r.hasNext()) {
////                //path = path + r.next().values().;
////            } 
//            //Path p = null;        
//            return path;
//            });
//    }
      
    public static Map<String,Object> qry_map(String cq) {
        //DatabaseManagementService managementService = new DatabaseManagementServiceBuilder(gen.neo4jlib.neo4j_info.Database_Dir ).build();
        //GraphDatabaseService db = managementService.database( DEFAULT_DATABASE_NAME );
            Session java_session =  gen.conn.connTest.session;
            return java_session.readTransaction( tx -> {
            Result r = tx.run(cq) ;
            
            Map<String,Object> m = new HashMap<>();
            //m.putall(r.stream());
            {
                
                while (r.hasNext()){
                    m.put(r.next().keys().toString(),r.next().values().toString());
                    //m.put(r.next().get(0).asMap());
                    //m.entrySet(r.next().get(0).asObject());
                    //m.getOrDefault(r.next().asMap());
                    //m.merge(r.next().asMap());  //, m, remappingFunction).
                   //m = r.next().asMap();
                   
                }
            return m;
            }
            }
           );
            
                   }

//            return java_session.readTransaction( tx -> {
//            Map<String,Object> m ; 
//            Result r = tx.run(cq);
//            while ( r.hasNext() )
//    {
//              //Map<String,Object> row = r.next();
//            m = r.next();
//    }     
//            java_session.close();
//            return m;
//        } 
//            );
     
//   }
  
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
         
            String q = "call apoc.export.csv.query(\"" + cq + "\",'" + csv_File + "' , {delim:',', quotes: true, format: 'plain'})"; 
                        
            tx.run(q);
            
            
            
            
            java_session.close();
            return csv_File;
        } 
            );
     
   }

public static String APOCPeriodicIterateCSV(String LoadCSV, String ReplaceCypher, int batchsize) {
    //use parallel = false to avoid deadlocks!
    String Q = "\"";
    String csv = "CALL apoc.periodic.iterate(" + Q + LoadCSV + Q + ", " + Q + ReplaceCypher + Q + ",{batchSize: " + batchsize + ", parallel:false, iterateList:true, retries:25})";
    
    qry_write(csv);
    return csv; 
}



}
        