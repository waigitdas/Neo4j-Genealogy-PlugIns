/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;
    import org.neo4j.driver.AuthToken;
    import gen.conn.AuthInfo;
import gen.neo4jlib.neo4j_qry;
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
    @Description("Segments within a triangultion group; returns Indx which is concatenated chr:strt_pos:end_pos. Used within other query to speed processing.")
        
    public List tg_segments(
        @Name("tg") 
            Long tg
    )

        {
        String qry = "match (t:tg{tgid:" + tg + "})-[r:tg_seg]-(s:Segment) return s.Indx as Indx order by s.Indx";
        List r =tg_qry(qry,tg);
        return r;
            }
    
   
    public List<String> tg_qry(String qry,long tg ) 
    {
        return neo4j_qry.qry_str_list(qry);
    }
}