/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.report;
import gen.excelLib.queries_to_excel;
import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.UserFunction;

public class curated_tgs {
    @UserFunction
    @Description("description here")

    public String curated_tg_report(
        
  )
   
         { 
             
        createReport();
         return "completed";
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String createReport() 
    {
       queries_to_excel.qry_to_excel("MATCH (t:tg) RETURN t.tgid as tg_id,t.project as project,t.chr as chr,t.strt_pos as strt_pos,t.end_pos as end_pos,t.cm as cm order by t.tgid","curated_tgs","tgs", 1, "1:15;1:15", "3:###,###,###;4:###,###,###", "", true );
        return "";
    }
}
