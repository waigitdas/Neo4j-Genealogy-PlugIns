/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.ref;
    import gen.neo4jlib.neo4j_qry;
    import java.io.File;
    import java.io.FileInputStream;
    import java.io.FileReader;
    import java.util.Iterator;
    import java.util.Map;
    import org.json.JSONArray;
    import org.json.JSONObject;   
    import java.io.IOException;

    import org.apache.commons.io.IOUtils;

    import java.io.IOException;
    import java.io.InputStream;
    import java.net.URL;
    import java.nio.charset.StandardCharsets;
    import org.neo4j.procedure.Description;
    import org.neo4j.procedure.UserFunction;
    import gen.load.web_file_to_import_folder;
//import java.util.logging.Level;
//import java.util.logging.Logger;
public class upload_Y_DNA_Haplotree {
       //@UserFunction
       //@Description("Loads FTDNA DNA result CSV files from a named directory and specifically structured subdirectories. File names must NOT be altered after downloaded.")

    
    public static void main(String args[]) {
        upload_FTDNA_Y_haplotree();
    }
    
    public static void upload_FTDNA_Y_haplotree() {
          web_file_to_import_folder.url_file_to_import_dir("https://www.familytreedna.com/public/y-dna-haplotree/get","Y_haplotree.json");
//        neo4j_qry.CreateIndex("block", "haplogroupId", db);
//        neo4j_qry.CreateIndex("block", "name", db);
//        neo4j_qry.CreateIndex("variant", "name", db);
   

//        File file = new File(FTDNA_json_file);
//        String fileContents="";
// 
//        try (FileInputStream inputStream = new FileInputStream(file))
//        {
//            fileContents = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//            
//        JSONObject jo = new JSONObject(fileContents);
//        JSONArray ja = new JSONArray();
//        ja.put(jo);
//        
//        JSONArray ja1=jo.getJSONArray("roots");
        //JSONArray ja2=jo.getJSONArray("haplogroupId");
        
        
// System.out.println(fileContents.length());
//System.out.println(ja.getJSONObject(0).length());
//System.out.println(ja.getJSONObject(0).keys().next() + "**");
////System.out.println(ja.getJSONObject(0).keys().next().chars().toString());
//System.out.println(ja.getJSONObject(0).keys().next().chars().count());
// 
//System.out.println("-----------------");
//
//System.out.println(ja1.getJSONObject(0).length());
//System.out.println(ja1.getJSONObject(10).length());
//System.out.println(ja.getJSONObject(0).keys() + "**");
////System.out.println(ja.getJSONObject(0).get("haplogroupId").toString() + "**");
//
////System.out.println(ja2.getJSONObject(0).length());
////System.out.println(ja2.getJSONObject(10).length());
////System.out.println(ja.getJSONObject(0).keys() + "**");


    }
     
}
