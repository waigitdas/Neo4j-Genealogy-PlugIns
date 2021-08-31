/**
 * Copyright 2020 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;
    import org.neo4j.driver.AuthToken;
    import gen.auth.AuthInfo;
    import java.util.ArrayList;
    import org.neo4j.driver.Driver;
    import org.neo4j.driver.GraphDatabase;
    import org.neo4j.driver.Result;
    import org.neo4j.driver.Session;    
    import org.neo4j.driver.SessionConfig;
    import org.neo4j.driver.Transaction;
//    import org.neo4j.driver.TransactionWork;
    import org.neo4j.driver.AccessMode;

    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;
    import org.neo4j.procedure.Description;
  
    import java.util.List;        
    import static org.neo4j.driver.Values.parameters;
      
/**
 *
 * @author david
 */
public class tg_segs {          
    @UserFunction
    @Description("Segments within a triangultion group; returns Indx which is concatenated chr:strt_pos:end_pos.")
        
    public List tg_segments(
        @Name("tg") 
            Long tg,
        @Name("db") 
            String db
    )

        {
        String qry = "match (t:tg{tgid:" + tg + "})-[r:tg_seg]-(s:CB_Segment) return s.Indx as Indx order by s.Indx";
        List r =tg_qry(qry,tg, db);
        return r;
            }
    
   
    public List<String> tg_qry(String qry,long tg ,String db) 
    {
        AuthToken myToken = AuthInfo.getToken();
        Driver driver;
        driver = GraphDatabase.driver( "bolt://localhost:7687", myToken );
        driver.session(SessionConfig.builder().withDefaultAccessMode(AccessMode.READ).build());
 
        try ( Session java_session = driver.session(SessionConfig.forDatabase(db)) )
                {
        return java_session.readTransaction( tx -> {
            List<String> names = new ArrayList<>();
            Result result = tx.run(qry );
            while ( result.hasNext() )
            {
                names.add( result.next().get( 0 ).asString() );
            }
            return names;
        } );
    }
}
              
}