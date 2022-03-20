/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.report;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class UDF_list_to_Excel {
    @UserFunction
    @Description("Exports UDF list to Excel")

    public String UDF_list(
        
  )
   
         { 
             
        String s = get_list();
         return s;
            }

       
     public String get_list() 
    { 
        String cq = "Show Functions yield name, signature, description,returnDescription where name STARTS WITH 'gen' return name, signature, case when size(description)>300 then left(replace(description,'\\n',''),290) + ' (truncated).' else replace(description,'\\n','') end as Description";
    gen.excelLib.queries_to_excel.qry_to_excel(cq, "GFG_UDF", "GFG_Function list", 1, "", "","",  true, "UDF:\nreturn gen.report.UDF_list()\n\nCypher query:\n" + cq, false);
        return "completed";
    }
}
