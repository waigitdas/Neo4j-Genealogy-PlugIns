/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.mt_research;

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
public class upload_mt_yfull {
       @UserFunction
       @Description("Loads the entire mt-haplotree directly from the current FTDNA mt-DNA json refernce file into Neo4j. This json is updated frequently as new snps and haplotree branches are discovered. Source: https://raw.githubusercontent.com/YFullTeam/MTree/master/mtree/current_mtree.json")

     public String upload_yfull_mt_haplotree() {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String s = load_mt_yfull();
        return s;
        }
        
    
     
        public static void main(String args[]) {
        load_mt_yfull();
                
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
        
     public static String load_mt_yfull()
     {
        //delete prior haplotree data
//        gen.neo4jlib.neo4j_qry.qry_write("match ()-[r:mt_block_child]-() delete r");
//        gen.neo4jlib.neo4j_qry.qry_write("match ()-[r:mt_block_snp]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (b:mt_yfull_block)-[r]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (b:mt_yfull_block) delete b");
        gen.neo4jlib.neo4j_qry.qry_write("match (v:mt_yfull_variant)-[r]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (v:mt_yfull_variant) delete v");
        
        String FileNm = gen.neo4jlib.neo4j_info.Import_Dir + "yfull_mt_haplotree.json";
        ArrayList<haplogroup> MasterList = new ArrayList<haplogroup>();
        
        //retrieve online FTDNA Y-haplotree json and place in import directory.
        web_file_to_import_folder.url_file_to_import_dir("https://raw.githubusercontent.com/YFullTeam/MTree/master/mtree/current_mtree.json","yfull_mt_haplotree.json");
          
        neo4j_qry.CreateIndex("mt_block", "haplogroupId");
        neo4j_qry.CreateIndex("mt_yfull_block", "name");
        neo4j_qry.CreateIndex("mt_yfull_variant", "name");
        try{
//        neo4j_qry.CreateIndex("DNA_mtMatch", "mtHG");
        neo4j_qry.CreateIndex("mt_yfull_block", "name");
        neo4j_qry.CreateIndex("DNA_Match", "mtHG");
        
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
        MasterList.add(hg);
        
        for (int h=0; h<50000; h++)
        {
            try{
                //get hchild haplogroups
                temp = parseBlock(MasterList.get(h));
            }
            catch(Exception e){
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
        } // next iteration until break
}  //end try
        catch(Exception e){System.out.println(e.getMessage());}  //not caught in development
        
        
        //use MasterList to export csv for import into Neo4j
        String s = "hg|snp\n";
        String c = "hg|parent\n";
        for (int i=0;i<MasterList.size(); i++)
        {
            haplogroup hp = MasterList.get(i);
          
            if (hp.snps!=null)
            {
                

            String ss[] = hp.snps.split(",");
            try{
            c = c + hp.id + "|" + hp.parent_id + "\n";
            }
            catch(Exception e){}

            for (int j=0; j<ss.length; j++)
            {
                s = s + hp.id + "|" + ss[j].strip() + "\n";

            }
            }
                
        }
        
        gen.neo4jlib.file_lib.writeFile(s, gen.neo4jlib.neo4j_info.Import_Dir + "YFull_mt_hg_snps.csv");
        gen.neo4jlib.file_lib.writeFile(c, gen.neo4jlib.neo4j_info.Import_Dir + "YFull_mt_hg_parent.csv");
        System.out.println("Haplogroups processed: " + MasterList.size());
        
        //mt_yfull_block
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///YFull_mt_hg_parent.csv' as line FIELDTERMINATOR '|' merge (m:mt_block{name:line.hg})");
        
        //mt_yfull_block
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///YFull_mt_hg_parent.csv' as line FIELDTERMINATOR '|' match (m:mt_block{name:line.hg}) set m.yfull=1");
        
        //mt_block_child
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///YFull_mt_hg_parent.csv' as line FIELDTERMINATOR '|' match (m1:mt_block{name:line.hg}) match (m2:mt_block{name:line.parent}) merge (m2)-[r:mt_block_child]-(m1)");
        
        //add variant from YFull
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///YFull_mt_hg_snps.csv' as line FIELDTERMINATOR '|' with line.snp as snp with snp where snp is not null merge(v:mt_variant{name:snp})");
        
        //tag variate from YFill
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///YFull_mt_hg_snps.csv' as line FIELDTERMINATOR '|' match(v:mt_variant{name:line.snp}) set v.yfull=1");
                
        //mt_block_snp
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///YFull_mt_hg_snps.csv' as line FIELDTERMINATOR '|' match (m:mt_block{name:line.hg,yfull:1}) match(v:mt_variant{name:line.snp,yfull:1}) merge (m)-[r:mt_block_snp]->(v)");
               
        //add pos to variant nodes
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (v:mt_variant{yfull:1}) with v,apoc.text.replace(v.name, '[^0-9]','') as pos set v.pos = toInteger(pos)");
                
        //add snp & pos to blocks
        gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(b:mt_block{yfull:1})-[r:mt_block_snp]->(v:mt_variant{yfull:1}) with b, v.name as snp,v.pos as pos with b,snp,pos order by pos with b,collect(distinct snp) as snps,collect(distinct toInteger(pos)) as pos set b.yfull_snp_ct=size(snps),b.yfull_snps=snps,b.yfull_pos=pos");
        
        //all cumulative _snps to blocks
        gen.neo4jlib.neo4j_qry.qry_write("MATCH path=(b1:mt_block{name:'L'})-[r:mt_block_child*0..999]->(b2:mt_block{yfull:1}) with b2, [x in nodes(path)|x.name] as blocks, [y in nodes(path)|id(y)] as op, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten([z in nodes(path) where z.yfull_snps is not null|z.yfull_snps]))) as snps with b2,blocks,snps,size(op) as lvl, gen.graph.get_ordpath(op) as op set b2.yfull_all_snps = snps,b2.yfull_all_snp_ct=size(snps)");
        
        
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