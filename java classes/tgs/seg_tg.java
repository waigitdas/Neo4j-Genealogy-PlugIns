/**
 * Copyright 2021-2023 
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
        
    public String segment_tgs(
        @Name("segment_indx") 
            String segment_indx,
        @Name("project") 
            String project
    )

        {
        String qry = "match (s:Segment{Indx:'" + segment_indx + "'})-[:tg_seg]-(t:tg{project:'" + project + "'})  with t order by t.tgid  with collect(distinct t.tgid) as td return td";
        String r =tg_qry(qry);
        return r;
            }
    
   
    public String tg_qry(String qry) 
    {
        //only 1 row expected
        String[] s = neo4j_qry.qry_to_csv(qry).replace(",", ";").split("\n");
        return s[0];

}
              
}