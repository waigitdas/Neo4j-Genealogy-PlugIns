/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.neo4jlib;
    import java.util.ArrayList;
    import org.neo4j.driver.Result;
    import org.neo4j.driver.Session;    
    import java.util.List;       
    import java.util.*;  
    import java.util.function.Function;
import java.util.regex.Pattern;
    import org.neo4j.driver.Record;
    import org.neo4j.driver.Value;

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
                String cq ="CREATE INDEX " + nodeNm + "_" + s.replace(",","_").replace("n.","").replace(" ","")  + " FOR (n:" + nodeNm + ") ON (" + s + ")";
                
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
            
                session.executeWrite( tx -> {
                   Result result = tx.run( cq );
                   //session.close();
               return 1;
               
                } );
            } 
           }
   
//    public static void qry_write_qry (String cq) {
//        gen.neo4jlib.neo4j_info.wrt = gen.neo4jlib.neo4j_info.wrt + cq + "\n" ;  //with xxx\n";
//    }
//   

   
   //****************************************************
   
    public static String qry_to_csv(String cq) {
        gen.conn.connTest.cstatus();
        Session java_session =  gen.conn.connTest.session;

        return java_session.executeRead(tx -> {
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
            //java_session.close();
            return c;
        }) ;
    }
     
    public static String qry_to_pipe_delimited_str(String cq) {
        gen.conn.connTest.cstatus();
        Session java_session =  gen.conn.connTest.session;

        return java_session.executeRead( tx -> {
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
            //java_session.close();
            return c;
        }) ;
        
        
        
    }
     
   public static List<String> qry_str_list(String cq) {
        gen.conn.connTest.cstatus();
        Session java_session =  gen.conn.connTest.session;
        
        return java_session.executeRead(tx -> {
            List<String> names = new ArrayList<>();
            Result result = tx.run(cq );
            while ( result.hasNext() )
            {
                names.add( result.next().get( 0 ).asString() );
            }
            //java_session.close();
           return names;
        } );
   }
   
public static <T> List<T> readCyphers(String cq, Function<Record, T> mapper) {
        gen.conn.connTest.cstatus();
        Session java_session =  gen.conn.connTest.session;
        try (java_session) {
            Result result = java_session.run(cq);
            //java_session.close();            
            return result.list(mapper);
        }
    }
   
public static Result qry_obj_all(String cq) {
        gen.conn.connTest.cstatus();
        Session java_session =  gen.conn.connTest.session;
        
        return java_session.executeRead( tx -> 
        {
            Result result = tx.run(cq );
            List lr = result.list();
            //java_session.close();             
            return result; 
        } 
        );
   }
      

//****************************************************
     public static List<Long> qry_long_list(String cq) {
         gen.conn.connTest.cstatus();
        Session java_session =  gen.conn.connTest.session;
        return java_session.executeRead( tx -> {
            List<Long> names = new ArrayList<>();
            Result result = tx.run(cq );
            while ( result.hasNext() )
            {
                names.add(result.next().get( 0 ).asLong() );
            }
            //java_session.close();
            return names;
        } );
    }
   
    public static List<Object> qry_obj_list(String cq) {
 
        gen.conn.connTest.cstatus();
        Session java_session =  gen.conn.connTest.session;


        return java_session.executeRead( tx -> {
            List<Object> names = new ArrayList<>();
            Result result = tx.run(cq );
            while ( result.hasNext() )
            {
                names.add(result.next().get( 0 ).asObject() );
            }
            //java_session.close();
            return names;
        } );
   }
   
      public static String qry_str(String cq) {
 
        gen.conn.connTest.cstatus();
        Session java_session =  gen.conn.connTest.session;


        return java_session.executeRead( tx -> {
            String names = "";
            Result result = tx.run(cq );
            while ( result.hasNext() )
            {
                names = names + result.next().values().toString() + "; ";
            }
            names = names.substring(0, names.length()-2);
            //java_session.close();
            return names;
        } );
   }
      
      public static String qry_to_pipe_delimited(String cq,String csv_File) {
        String Q = "\"";
        String q = "call apoc.export.csv.query(" + Q + cq + Q + ",'" + csv_File + "' , {delim:'|',  quotes: false, format: 'plain'})"; 
        gen.conn.connTest.cstatus();
         Session java_session =  gen.conn.connTest.session;

        return java_session.executeRead( tx -> {
         
            tx.run(q);
  
                        
            //java_session.close();
             return csv_File;
        } 
            );
   }
   
 public static String qry_to_csv(String cq,String csv_File) {
        gen.conn.connTest.cstatus();
        Session java_session =  gen.conn.connTest.session;

            return java_session.executeRead(tx -> {
            String Q = "\"";
            String q = "call apoc.export.csv.query(" + Q  + cq + Q + ",'" + csv_File + "' , {delim:',', quotes: true, format: 'plain'})"; 
                        
            tx.run(q);
      
            //java_session.close();
            return csv_File;
        } 
            );
     
   }

public static String APOCPeriodicIterateCSV(String LoadCSV, String ReplaceCypher, int batchsize) {
    //use parallel = false to avoid deadlocks!
    //optimize with specific query design:
    //https://neo4j.com/developer/kb/a-significant-change-in-apoc-periodic-iterate-in-apoc-4-0
    String Q = "\"";
    String csv = "CALL apoc.periodic.iterate(" + Q + LoadCSV + Q + ", " + Q + ReplaceCypher + Q + ",{batchSize: " + batchsize + ", parallel:false, iterateList:true, retries:25})";
    
    qry_write(csv);
            return csv; 
}



}
        