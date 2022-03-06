/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.ref;
    import gen.neo4jlib.neo4j_qry;
    import gen.neo4jlib.neo4j_info;
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
    import gen.neo4jlib.neo4j_info;
    import java.io.FileWriter;
    import java.util.logging.Level;
    import java.util.logging.Logger;
//import java.util.logging.Level;
//import java.util.logging.Logger;

public class upload_Y_DNA_Haplotree {
       @UserFunction
       @Description("Loads the entire Y-haplotree directly from the current FTDNA Y-DNA json refernce file into Neo4j. This json is updated frequently as new snps and haplotree branches are discovered. Source: https://www.familytreedna.com/public/y-dna-haplotree/get")

    
//    public static void main(String args[]) {
//        gen.neo4jlib.neo4j_info.neo4j_var();
//        //upload_FTDNA_Y_haplotree(gen.neo4jlib.neo4j_info.Import_Dir + "Y_haplotree.json");
//    }
    
    public String upload_FTDNA_Y_haplotree() {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        //delete prior haplotree data
        gen.neo4jlib.neo4j_qry.qry_write("match ()-[r:block_child]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match ()-[r:block_snp]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (b:block)-[r]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (b:block) delete b");
        gen.neo4jlib.neo4j_qry.qry_write("match (v:variant) delete v");
        
        String FileNm = gen.neo4jlib.neo4j_info.Import_Dir + "Y_haplotree.json";
        
        //retrieve online FTDNA Y-haplotree json and place in import directory.
        web_file_to_import_folder.url_file_to_import_dir("https://www.familytreedna.com/public/y-dna-haplotree/get","Y_haplotree.json");
          
        neo4j_qry.CreateIndex("block", "haplogroupId");
        neo4j_qry.CreateIndex("block", "name");
        neo4j_qry.CreateIndex("variant", "name");
        try{
        neo4j_qry.CreateIndex("DNA_YMatch", "YHG");
        neo4j_qry.CreateIndex("DNA_YMatch", "fullname");
        neo4j_qry.CreateIndex("DNA_Match", "YHG");
        }
        catch (Exception e){}

        //read, parse  and load json into Neo4j
        File file = new File(FileNm);
        String fileContents="";
 
        try (FileInputStream inputStream = new FileInputStream(file))
        {
            fileContents = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
            
        //read json
        JSONObject jo = new JSONObject(fileContents).getJSONObject("allNodes");
        int jl = jo.length();
        String fny =neo4j_info.Import_Dir + "Y_HT.csv";
        String fnv =neo4j_info.Import_Dir + "Y_HT_variants.csv";
        File fy = new File(fny);
        File fv = new File(fnv);

       try{

        FileWriter fwy = new FileWriter(fy);
        fwy.write("haplogroupId|name|parentId|IsRoot\n");
        FileWriter fwv = new FileWriter(fv);
        fwv.write("variant_name|haplogroupId|pos|anc|der|region\n");
 
        String var = "";
        String rg = "";
        String hid = "";
        String pos = "";
        String anc = "";
        String der = "";

        JSONArray keys = jo.names();
        
        for (int i=0; i< keys.length(); i++) {
            String key = keys.getString (i); 
            JSONObject jo2 = jo.getJSONObject(key);
            try{
                fwy.write(jo2.get("haplogroupId") + "|" + jo2.get("name") + "|" + jo2.get("parentId") + "|" + jo2.get("isRoot") + "\n");
             }
            catch (Exception e)  //no parentId
            {
          }
            
            JSONArray ja3 = jo2.getJSONArray("variants");
            
            for (int k=0; k<ja3.length() ; k++) {
                //fourth level parse
                JSONObject ja4 = ja3.getJSONObject(k);
                try { var = ja4.get("variant") + "|";}
                catch (Exception e) {var = "|";}
                try { hid = jo2.get("haplogroupId") + "|";}
                catch (Exception e) {hid = "0|";}
                try { pos = ja4.get("position") + "|";}
                catch (Exception e) {pos = "0|";}
                try { anc = ja4.get("ancestral") + "|";}
                catch (Exception e) {anc = "|";}
                try { der = ja4.get("derived") + "|";}
                catch (Exception e) {der = "|";}
                 try { rg = ja4.get("region") + "|";}
                catch (Exception e) {rg = "0|";}
                try {
                    fwv.write(var + hid + pos + anc + der + rg +"\n");
                } 
                catch (IOException ex) {
                    //Logger.getLogger(upload_Y_DNA_Haplotree.class.getName()).log(Level.SEVERE, null, ex);
                }
              }  
         
       
        }  
            fwy.flush();
            fwy.close();
            fwv.flush();
            fwv.close();
      
            }
         catch (Exception e) {System.out.println(e.getMessage());}
    
       //Load csv to Neo4j
       String lc = "LOAD CSV WITH HEADERS FROM 'file:///Y_HT.csv' as line FIELDTERMINATOR '|' return line ";
 
       String cq = "merge (b:block{haplogroupId:toInteger(line.haplogroupId),name:toString(line.name),parentId:toInteger(line.parentId),IsRoot:toBoolean(line.IsRoot)})";
       neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);
       
       lc = "LOAD CSV WITH HEADERS FROM 'file:///Y_HT_variants.csv' as line FIELDTERMINATOR '|' return line ";
       cq = "merge (v:variant{name:toString(line.variant_name),pos:toInteger(line.pos),anc:toString(case when line.anc is null then '' else line.anc end),der:toString(case when line.der is null then '' else line.der end),region:toString(case when line.region is null then '' else line.region end)})";
       neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);
       
       
      cq = "match (b:block{haplogroupId:toInteger(line.haplogroupId)}) match (v:variant{name:toString(line.variant_name)}) merge (b)-[r:block_snp]->(v)";
      neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);

       gen.neo4jlib.neo4j_qry.qry_write("MATCH (b1:block) with b1 match (b2:block) where b2.haplogroupId=b1.parentId merge (b2)-[r:block_child]-(b1)");
       
       //Ymatch-block edge
       gen.neo4jlib.neo4j_qry.qry_write("match (y:DNA_YMatch) where trim(y.YHG)>' ' with y match (b:block) where b.name=y.YHG merge (y)-[r:y_match_block]-(b)");
       
       //match_block
       gen.neo4jlib.neo4j_qry.qry_write("MATCH (m:DNA_Match) where m.YHG is not null with m match (b:block) where b.name=m.YHG merge (m)-[mb:match_block]->(b)");
       
       return "Completed";
    }
        
}