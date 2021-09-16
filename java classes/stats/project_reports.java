/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.stats;

import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
import gen.neo4jlib.neo4j_qry;
import gen.neo4jlib.neo4j_info;
import gen.excelLib.write_open_csv;
/**
 *
 * @author david
 */
public class project_reports {
       @UserFunction
        @Description("Descriptive statics about the Neo4j data")
        
    public String project_stats(
//        @Name("rn1") 
//            Long rn1,
//        @Name("rn2") 
//            Long rn2
  )
    {
         { 
        gen.neo4jlib.neo4j_info.neo4j_var();
        String r = stats(); 
        return r;
            }
     }
    
    public static String stats() {
        gen.neo4jlib.neo4j_info.neo4j_var();
        String s = "";
        s = s + "kits,"  + gen.neo4jlib.neo4j_qry.qry_str("match (k:Kit) return count(*) as ct") + "\n";
        s = s + "kits in genealogy,"  + gen.neo4jlib.neo4j_qry.qry_str("MATCH p=()-[r:Gedcom_FF_Kit]->() RETURN count(*)") + "\n";
        s = s + "at-DNA matches,"  + gen.neo4jlib.neo4j_qry.qry_str("match (k:DNA_Match) return count(*)") + "\n";
        s = s + "chromosome segments,"  + gen.neo4jlib.neo4j_qry.qry_str("match (k:Segment) return count(*)") + "\n";
        s = s + "Person in genealogy,"  + gen.neo4jlib.neo4j_qry.qry_str("match (k:Person) return count(*)") + "\n";
       System.out.println(s);
       
        gen.excelLib.write_open_csv.write_csv(s);
        return s;
    }
  
      
    public static void main(String args[]) {
        stats();
    }
}
