/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.ref;

import gen.load.web_file_to_import_folder;
import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class upload_condensed_hapmap {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String load_condensed_hapmap(
  )
   
         { 
             
        get_chm();
         return "completed";
            }

    
    
    public static void main(String args[]) {
        get_chm();
    }
    
     public static String get_chm() 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        
        //delete if already present
        gen.neo4jlib.neo4j_qry.qry_write("match (c:cHapMap) delete c");

        try{
            gen.neo4jlib.neo4j_qry.CreateIndex("cHapMap", "chr");
            gen.neo4jlib.neo4j_qry.CreateIndex("cHapMap", "pos");
           gen.neo4jlib.neo4j_qry.qry_write("CREATE INDEX IF NOT EXISTS FOR (h:cHapMap) ON (h.chr,h.pos)");        
        }
        catch (Exception e) {}
        
        String fn="";
        try {   
        fn ="HapMap_condensed.csv";
        String src ="https://raw.githubusercontent.com/waigitdas/Neo4j-Genealogy-PlugIns/main/HapMap/cHapMap.csv";
        web_file_to_import_folder.url_file_to_import_dir(src,fn);
         }
         catch (Exception e) {
             return "Failed: " + e.getMessage();
         }
//        
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///" + fn + "' as line FIELDTERMINATOR ',' create (c:cHapMap{chr:toInteger(line.chr),pos:toInteger(line.pos),cm:toFloat(line.cm),icm:toFloat(line.increment_cm)})");
        return "";
    }
}
