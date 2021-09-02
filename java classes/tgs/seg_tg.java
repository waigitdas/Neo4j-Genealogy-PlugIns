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
    import org.neo4j.driver.AccessMode;

    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;
    import org.neo4j.procedure.Description;
  
    import java.util.List;        
      
/**
 *
 * @author david
 */
public class seg_tg {          
    @UserFunction
    @Description("Segment tg if it maps to a tg; returns tg id.")
        
    public List<Long> segment_tgs(
        @Name("segment_indx") 
            String segment_indx,
        @Name("project") 
            String project,
        @Name("db")
            String db
    )

        {
        String qry = "match (s:CB_Segment{Indx:'" + segment_indx + "'})-[:tg_seg]-(t:tg{project:'" + project + "'}) with distinct t as td order by td.tgid  return td.tgid";
        List<Long> r =tg_qry(qry,segment_indx, db);
        return r;
            }
    
   
    public List<Long> tg_qry(String qry,String seg ,String db) 
    {
        AuthToken myToken = AuthInfo.getToken();
        Driver driver;
        driver = GraphDatabase.driver( "bolt://localhost:7687", myToken );
        driver.session(SessionConfig.builder().withDefaultAccessMode(AccessMode.READ).build());
 
        try ( Session java_session = driver.session(SessionConfig.forDatabase(db)) )
                {
        return java_session.readTransaction( tx -> {
            List<Long> names = new ArrayList<>();
            Result result = tx.run(qry );
            while ( result.hasNext() )
            {
                names.add(result.next().get( 0 ).asLong() );
            }
            return names;
        } );
    }
}
              
}