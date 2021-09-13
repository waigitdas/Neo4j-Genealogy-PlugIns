/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;
    import org.neo4j.driver.AuthToken;
    import gen.auth.AuthInfo;
import gen.neo4jlib.neo4j_qry;
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
            String project
    )

        {
        String qry = "match (s:Segment{Indx:'" + segment_indx + "'})-[:tg_seg]-(t:tg{project:'" + project + "'}) with distinct t as td order by td.tgid  return td.tgid";
        List<Long> r =tg_qry(qry);
        return r;
            }
    
   
    public List<Long> tg_qry(String qry) 
    {
        return neo4j_qry.qry_long_list(qry);
//  
}
              
}