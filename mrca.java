/**
 * Copyright 2020 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen;
    import org.neo4j.driver.*;
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;

    import static org.neo4j.driver.Values.parameters;


    public class mrca implements AutoCloseable
{  
    private final Driver driver;

    public mrca( )
    {
        driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "cns105" ) );
            try ( Session session = driver.session() ) {
                
         }
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

    public void JavaQuery( final String qrystr,  String db )
    {
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
        }
    }

           
    public static void main( String... args ) throws Exception
    {
        try ( mrca mrcalist = new mrca() )
        {
            mrcalist.JavaQuery( "match (p1:Person{RN:1})-[r1:father|mother*0..15]->(mrca:Person)<-[r2:father|mother*0..15]-(p2:Person{RN:600}) return mrca.fullname + ' [' + mrca.RN + '] (' + left(mrca.BD,4) +'-' + left(mrca.DD,4) +')'","stumpf" );
        }
    }
}