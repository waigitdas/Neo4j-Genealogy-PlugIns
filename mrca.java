/**
 * Copyright 2020 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen;
    import org.neo4j.driver.AuthTokens;
    import org.neo4j.driver.Driver;
    import org.neo4j.driver.GraphDatabase;
    import org.neo4j.driver.Result;
    import org.neo4j.driver.Session;    
    import org.neo4j.driver.SessionConfig;
    import org.neo4j.driver.Transaction;
    import org.neo4j.driver.TransactionWork;
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;
    import org.neo4j.procedure.Description;
    import org.neo4j.procedure.Procedure;
    import org.neo4j.procedure.Mode;
 
    import static org.neo4j.driver.Values.parameters;
  
    
    public class mrca {          
                       
     
     
    @UserFunction
    @Description("Input 2 RNs and get list of MRCAs")
    @Procedure(mode=Mode.READ)
    
    public String mrca(
        @Name("db") 
            String db,
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
      
)
    {
        
        { 
        String r =JavaQuery("stumpf");
       return r;
            }
     }
   
    public String JavaQuery(String db){
    
    String qrystr = "match (p1:Person{RN:1})-[r1:father|mother*0..15]->(mrca:Person)<-[r2:father|mother*0..15]-(p2:Person{RN:238}) return mrca.fullname + ' [' + mrca.RN + '] (' + left(mrca.BD,4) +'-' + left(mrca.DD,4) +')'" ;
      Driver driver;
        driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "cns105" ) );
        Session session = driver.session() ;
        try ( Session java_session = driver.session(SessionConfig.forDatabase(db)) )
        {
            String javasession = java_session.writeTransaction(new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    Result result = tx.run( qrystr,
                            parameters( "message", qrystr ) );

                    String output = "";
                        while (result.hasNext())
                        { output = output + result.next().values() + "\n";
                        
                        }
	                return output;
                            
                    
                }
            } );
            System.out.println( javasession );
            return javasession ;
        }
    }

    
        public static void main( String... args ) throws Exception
    {
         mrca mrcalist = new mrca() ;
        {
         String z =  mrcalist.JavaQuery("stumpf");
        
        } 
    }     
}