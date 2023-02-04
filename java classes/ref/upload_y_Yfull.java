/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.ref;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import gen.load.web_file_to_import_folder;
import gen.neo4jlib.neo4j_info;
import gen.neo4jlib.neo4j_qry;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.UserFunction;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.IOException; 
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.security.KeyStore.Entry;
import java.util.HashMap;
//import com.fasterxml.jackson.annotation.JsonRootName; 
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper; 
//import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Type;
import java.util.Map;
/**
 *
 * @author david
 */
public class upload_y_Yfull {
       @UserFunction
       @Description("Loads the entire y-haplotree directly from the current FTDNA y-DNA json refernce file into Neo4j. This json is updated frequently as new snps and haplotree branches are discovered. Source: https://www.familytreedna.com/public/y-dna-haplotree/get")

     public String upload_FTDNA_y_haplotree() {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String s = load_y_yfull();
        return s;
        }
        
    
     
        public static void main(String args[]) {
        load_y_yfull();
                
    }
     
        public class haplogroup
        {
            String id;
            String parent_id;
            String snps;
            String formed;
            String formedlowage;
            String tmrca;
            String tmrcahighage;
            String tmrcalowage;
            JsonArray children;
//           String children ;
//           {
//            String id;
//            String sns;
//            }
           
        
        }
        
     public static String load_y_yfull()
     {
        //delete prior haplotree data
//        gen.neo4jlib.neo4j_qry.qry_write("match ()-[r:y_block_child]-() delete r");
//        gen.neo4jlib.neo4j_qry.qry_write("match ()-[r:y_block_snp]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (b:y_block)-[r]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (b:y_block) delete b");
        gen.neo4jlib.neo4j_qry.qry_write("match (v:y_variant)-[r]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (v:y_variant) delete v");
        
        String FileNm = gen.neo4jlib.neo4j_info.Import_Dir + "yfull_y_haplotree.json";
        
        //retrieve online FTDNA Y-haplotree json and place in import directory.
        web_file_to_import_folder.url_file_to_import_dir("https://raw.githubusercontent.com/YFullTeam/YTree/master/current_tree.json","yfull_y_haplotree.json");
          
        neo4j_qry.CreateIndex("y_block", "haplogroupId");
        neo4j_qry.CreateIndex("y_block", "name");
        neo4j_qry.CreateIndex("y_variant", "name");
        try{
        neo4j_qry.CreateIndex("DNA_yMatch", "yHG");
        neo4j_qry.CreateIndex("DNA_yMatch", "fullname");
        neo4j_qry.CreateIndex("DNA_Match", "yHG");
        
        }
        catch (Exception e){}

        try
        {
        //read json
        String data = new String(Files.readAllBytes(Paths.get(FileNm))); 
        JsonElement parent = JsonParser.parseString(data);
 
        //initiate iteration
        haplogroup hg = new Gson().fromJson(data, haplogroup.class);
        ArrayList<haplogroup> temp = new ArrayList<haplogroup>();
        
        //MasterList will hold all the blocks in an array
        ArrayList<haplogroup> MasterList = new ArrayList<haplogroup>();
        MasterList.add(hg);
        
        for (int h=0; h<50000; h++)
        {
            try{
                //get hchild haplogroups
                temp = parseBlock(MasterList.get(h));
            }
            catch(Exception e){
                System.out.println("Haplobranches processed: " + MasterList.size());
                break;}  //break at end of list
    
            //add returned child blocks to the master list
            try
            {
            for (int j= 0; j<temp.size(); j++)
            {
                MasterList.add(temp.get(j));
            }
            }
            catch(Exception e){}  //null values not processed
        }
}
        catch(Exception e){System.out.println(e.getMessage());}
        
        
        
       return "Completed";
    }
     
     
    public static ArrayList<haplogroup> parseBlock(haplogroup parent)
    {
            
            ArrayList<haplogroup> chhg = new ArrayList<haplogroup>();  // new Gson().fromJson(ch, haplogroup.class);
            try
            {
            for (int i=0; i<parent.children.size();i++)
            {
                JsonObject ch = parent.children.get(i).getAsJsonObject();
                haplogroup chx = new Gson().fromJson(ch, haplogroup.class);
                chx.parent_id = parent.id;
                chhg.add(chx);
            }
            }
            catch(Exception e){}
          
         return chhg;
     }
         
}