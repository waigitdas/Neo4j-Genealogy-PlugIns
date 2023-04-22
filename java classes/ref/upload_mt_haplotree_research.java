/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.ref;

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

/**
 *
 * @author david
 */
public class upload_mt_haplotree_research {
       @UserFunction
       @Description("Loads the entire mt-haplotree directly from the current FTDNA mt-DNA json refernce file into Neo4j. This json is updated frequently as new snps and haplotree branches are discovered. Source: https://www.familytreedna.com/public/mt-dna-haplotree/get")

     public String upload_FTDNA_mt_haplotree_research() {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String s = load_mt_haplotree();
        return s;
        }
        
    
     
        public static void main(String args[]) {
        load_mt_haplotree();
                
    }
     
     public static String load_mt_haplotree()
     {
        //delete prior haplotree data
//        gen.neo4jlib.neo4j_qry.qry_write("match ()-[r:mt_block_child]-() delete r");
//        gen.neo4jlib.neo4j_qry.qry_write("match ()-[r:mt_block_snp]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (b:mt_block)-[r]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (b:mt_block) delete b");
        gen.neo4jlib.neo4j_qry.qry_write("match (v:mt_variant)-[r]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (v:mt_variant) delete v");
        
        String FileNm = gen.neo4jlib.neo4j_info.Import_Dir + "mt_haplotree.json";
        
        //retrieve online FTDNA Y-haplotree json and place in import directory.
        web_file_to_import_folder.url_file_to_import_dir("https://www.familytreedna.com/public/mt-dna-haplotree/get","mt_haplotree.json");
          
        neo4j_qry.CreateIndex("mt_block", "haplogroupId");
        neo4j_qry.CreateIndex("mt_block", "name");
        neo4j_qry.CreateIndex("mt_variant", "name");
        try{
        neo4j_qry.CreateIndex("DNA_mtMatch", "mtHG");
        neo4j_qry.CreateIndex("DNA_mtMatch", "fullname");
        neo4j_qry.CreateIndex("DNA_Match", "mtHG");
        
        }
        catch (Exception e){}

        try{
            neo4j_qry.CreateCompositeIndex("mt_block","haplogroupId,name,parentId,IsRoot");
            neo4j_qry.CreateCompositeIndex("mt_variant","name,pos,anc,der,region");
        
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
        String fny =neo4j_info.Import_Dir + "mt_HT.csv";
        String fnv =neo4j_info.Import_Dir + "mt_HT_variants.csv";
        File fy = new File(fny);
        File fv = new File(fnv);

       try{

        FileWriter fwy = new FileWriter(fy);
        fwy.write("haplogroupId|name|parentId|IsRoot|kit_ct\n");
        FileWriter fwv = new FileWriter(fv);
        fwv.write("variant_name|haplogroupId|pos|anc|der|region\n");
 
        String var = "";
        String rg = "";
        String hid = "";
        String pos = "";
        String anc = "";
        String der = "";
        String kit_ct = "";
        
        JSONArray keys = jo.names();
        
        for (int i=0; i< keys.length(); i++) {
            String key = keys.getString (i); 
            JSONObject jo2 = jo.getJSONObject(key);
            try{
                fwy.write(jo2.get("haplogroupId") + "|" + jo2.get("name") + "|" + jo2.get("parentId") + "|" + jo2.get("isRoot") + "|" + jo2.get("kitsCount") + "\n");
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
//                 try { kit_ct = ja4.get("kitsCount") + "|";}
//                catch (Exception e) {kit_ct = "0|";}
                try {
                    fwv.write(var + hid + pos + anc + der + rg  + "\n");
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
       String lc = "LOAD CSV WITH HEADERS FROM 'file:///mt_HT.csv' as line FIELDTERMINATOR '|' return line ";
 
       String cq = "merge (b:mt_block{haplogroupId:toInteger(line.haplogroupId),name:toString(line.name),parentId:toInteger(line.parentId),IsRoot:toBoolean(line.IsRoot),kit_ct:toInteger(line.kit_ct),ftdna:1})";
       neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 200000);
       
       lc = "LOAD CSV WITH HEADERS FROM 'file:///mt_HT_variants.csv' as line FIELDTERMINATOR '|' return line ";
       cq = "merge (v:mt_variant{name:toString(case when line.variant_name is null then '' else line.variant_name end),pos:toInteger(line.pos),anc:toString(case when line.anc is null then '' else line.anc end),der:toString(case when line.der is null then '' else line.der end),region:toString(case when line.region is null then '' else line.region end),ftdna:1})";
       neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 200000);
       
       
      cq = "match (b:mt_block{haplogroupId:toInteger(line.haplogroupId)}) match (v:mt_variant{name:toString(line.variant_name)}) merge (b)-[r:mt_block_snp]->(v)";
      neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 200000);

       gen.neo4jlib.neo4j_qry.qry_write("MATCH (b1:mt_block) with b1 match (b2:mt_block) where b2.haplogroupId=b1.parentId merge (b2)-[r:mt_block_child]-(b1)");
       
       //mt_match-block edge
       gen.neo4jlib.neo4j_qry.qry_write("match (y:DNA_mtMatch) where trim(y.mtHG)>' ' with y match (b:mt_block) where b.name=y.mtHG merge (y)-[r:mt_match_block]-(b)");
       
       //match_block
       gen.neo4jlib.neo4j_qry.qry_write("MATCH (m:DNA_Match) where m.mtHG is not null with m match (b:mt_block) where b.name=m.mtHG merge (m)-[mb:mt_match_block]->(b)");
       
       //mt_block snp aliases
       gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(b:mt_block)-[r:mt_block_snp]->(v:mt_variant) with b, v.name as vname order by v.pos with b,collect(distinct vname) as vname set b.snp_ct=size(vname),b.snps=vname");
       
       //all_snps
       gen.neo4jlib.neo4j_qry.qry_write("MATCH path=(b1:mt_block{name:'RSRS'})-[r:mt_block_child*0..999]->(b2:mt_block) with b2, [x in nodes(path)|x.name] as blocks, [y in nodes(path)|id(y)] as op, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten([z in nodes(path) where z.snps is not null|z.snps]))) as snps with b2,blocks,snps,size(op) as lvl, gen.graph.get_ordpath(op) as op set b2.all_snps = snps,b2.all_snp_ct=size(snps)");
       
       //mt chilId
       gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(b1)-[r:mt_block_child]->(b2) set b1.childId=1");
       
       //mt_block_shared_snps
       gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(b1:mt_block)-[r1:mt_block_snp]->(v:mt_variant)<-[r3:mt_block_snp]-(b2:mt_block) where b1.name < b2.name with b1,b2,apoc.coll.sort(collect(distinct v.name)) as snps merge (b1)-[r:mt_block_shared_snps{snps:snps,snp_ct:size(snps)}]->(b2)");
       
       //need kit_ct to avoid deleting SNPs needed for kit haplotree assihments
       gen.neo4jlib.neo4j_qry.qry_write("MATCH (k:mt_kit_snp) with k match (v:mt_variant{name:k.name}) set v.kit_ct=k.kit_ct");

       gen.neo4jlib.neo4j_qry.qry_write("match (b:mt_block) set b.ftdna=1");
       gen.neo4jlib.neo4j_qry.qry_write("match(v:mt_variant) set v.ftdna=1");
       gen.neo4jlib.neo4j_qry.qry_write("match (b:mt_block)-[r]->(v:mt_variant) set r.ftdna=1");
              
       gen.dna.load_mt_phylotree.load_phylotree();
       
       gen.ref.upload_mt_yfull mtyf = new gen.ref.upload_mt_yfull();
       mtyf.upload_yfull_mt_haplotree();
       
       
        //prepare reports
        int ct = 1;
        cq = "MATCH path=(b1:mt_block{name:'RSRS'})-[r:mt_block_child*0..999]->(b2:mt_block) with b2.name as hg,path,[m in nodes(path)|id(m)] as R optional match (m:DNA_Match) where m.mtHG=hg with hg,apoc.coll.sort(collect(case when m.ancestor_rn>0 then '*^' + m.fullname else m.fullname end + case when m.RN is not null then ' [' + m.RN + ']' else '' end)) as match, R, gen.graph.get_ordpath(R) as op with op, R,match,hg order by op MATCH p=(b3:mt_block{name:hg})-[r:mt_block_snp]->(v) with v, R,match,hg order by op, v.sort with R,match,hg, collect(v.name) as variants return apoc.text.lpad('',(size(R)-1)*3,'.') + hg as haplogroup ,size(variants) as var_ct,variants,size(R) as haplotree_level, size(match) as match_ct,case when size(match)>100 then 'truncated ' + apoc.coll.remove(match,100,size(match)-99) else match end as match";
        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "pruning_mt_haplotree", "original_tree", ct, "", "1:####;3:####;4:####", "", false, cq, false);
        ct = ct + 1;

        cq = "MATCH path=(b1:mt_block{name:'RSRS'})-[r:mt_block_child*0..999]->(b2:mt_block) with b2.name as hg,path,[m in nodes(path)|id(m)] as R optional match (m:DNA_Match) where m.mtHG=hg with hg,apoc.coll.sort(collect(case when m.ancestor_rn>0 then '*^' + m.fullname else m.fullname end + case when m.RN is not null then ' [' + m.RN + ']' else '' end)) as match, R, gen.graph.get_ordpath(R) as op with op, R,match,hg order by op optional MATCH p=(b3:mt_block{name:hg})-[r:mt_block_snp]->(v) with v, R,match,hg order by op, v.sort with R,match,hg, collect(v.name) as variants where size(collect(v.name))=0 return apoc.text.lpad('',(size(R)-1)*3,'.') + hg as haplogroup ,size(variants) as var_ct,variants,size(R) as haplotree_level, size(match) as match_ct,case when size(match)>100 then 'truncated ' + apoc.coll.remove(match,100,size(match)-99) else match end as match";
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "original_just_zero_snp", "original_just_zero_snp", ct, "", "1:####;3:####;4:####", excelFile, false, cq, false);
        ct = ct + 1;

       
       //set deleteable property
       //method 2
       //gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(b1)-[r:mt_block_shared_snps]->(b2) with b1,r, apoc.coll.subtract(b1.snps,r.snps) as remaining_snps with b1 as b1,remaining_snps,r.snps as shared_snps, b1.snps as block_snps with b1,remaining_snps,shared_snps,block_snps where size(remaining_snps)>0 match (b1)-[r2:mt_block_snp]-(v:mt_variant) where v.name in shared_snps set r2.deletable=1,r2.del_snp=v.name");
             
       //method 1
        //gen.neo4jlib.neo4j_qry.qry_write(" MATCH p=(b1)-[r:mt_block_shared_snps]->(b2) with b1,r, apoc.coll.subtract(b1.snps,r.snps) as remaining_snps with b1 as b1,remaining_snps,r.snps as shared_snps, b1.snps as block_snps with b1,remaining_snps,shared_snps,block_snps where size(remaining_snps)>0 match (b1)-[r2:mt_block_snp]-(v:mt_variant) where v.name in shared_snps set r2.deletable=1,r2.del_snp=v.name");
       
      //method 3
      //gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(b1)-[r:mt_block_shared_snps]->(b2) where size(b1.snps)>1 and size(b2.snps)>1 match (b1)-[rv:mt_block_snp]-(v:mt_variant) where v.name in r.snps with distinct b1.name as b1,v,rv, id(rv) as id,count(*) as ct with b1,v,rv, id,ct where ct>1 delete rv");
                              
               
               
       //prune mt_block_snp nodes
       //method 2
       //gen.neo4jlib.neo4j_qry.qry_write("MATCH (b1:mt_block) optional match p=(b1)-[r:mt_block_snp]->(v) with b1,apoc.coll.sort(apoc.coll.flatten(collect([x in relationships(p) where x.deletable=1 |id(x) ]))) as del, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten (collect (distinct[y in nodes(p) where y.kit_ct>0 | y.name])))) as snps1 with b1.name as hg,b1.snps as snps, b1.snp_ct as snp_ct, del,b1.snp_ct-size(del) as snp_left,size(del) as del_ct,snps1, size(snps1) as kit_snp_ct where snp_left=0 with hg,snp_left, snp_ct,del_ct,kit_snp_ct, del,snps,snps1 as snp_in_kits,apoc.coll.subtract(snps,snps1) as toDelete with hg,snp_left, snp_ct,del_ct,kit_snp_ct,size(toDelete) as toDel_ct, del,snps, snp_in_kits,toDelete optional match (m2:mt_block{name:hg})-[rr:mt_block_snp]-(vv:mt_variant) where vv.name in toDelete with hg,snp_left, snp_ct,del_ct,kit_snp_ct,toDel_ct, del,snps, snp_in_kits,toDelete,collect(id(rr)) as idToDel where snp_ct-toDel_ct + 1 >0 and toDel_ct>0 match (m3:mt_block)-[rdel:mt_block_snp]-(vv3:mt_variant) where id(rdel) in idToDel delete rdel");
      
       //method1
      //gen.neo4jlib.neo4j_qry.qry_write("MATCH (b1:mt_block) optional match p=(b1)-[r:mt_block_snp]->(v) with b1,apoc.coll.sort(apoc.coll.flatten(collect([x in relationships(p) where x.deletable=1 |id(x) ]))) as del, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten (collect (distinct[y in nodes(p) where y.kit_ct>0 | y.name])))) as snps1 with b1.name as hg,b1.snps as snps, b1.snp_ct as snp_ct, del,b1.snp_ct-size(del) as snp_left,size(del) as del_ct,snps1, size(snps1) as kit_snp_ct where snp_left=0 with hg,snp_left, snp_ct,del_ct,kit_snp_ct, del,snps,snps1 as snp_in_kits,apoc.coll.subtract(snps,snps1) as toDelete with hg,snp_left, snp_ct,del_ct,kit_snp_ct,size(toDelete) as toDel_ct, del,snps, snp_in_kits,toDelete optional match (m2:mt_block{name:hg})-[rr:mt_block_snp]-(vv:mt_variant) where vv.name in toDelete with hg,snp_left, snp_ct,del_ct,kit_snp_ct,toDel_ct, del,snps, snp_in_kits,toDelete,collect(id(rr)) as idToDel where snp_ct-toDel_ct + 1 >0 and toDel_ct>0 with apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten (collect(idToDel)))) as ids match (m3:mt_block)-[rdel:mt_block_snp]-(vv3:mt_variant) where id(rdel) in ids delete rdel");
        
      //method 3 -- no 2nd step
       
       //shared_mtHG
       cq = "match (mt:DNA_Match) with mt where mt.mtHG is not null and 'Z'>=left(mt.mtHG,1)>='A' with mt order by mt.mtHG with collect (distinct mt.mtHG) as mito unwind mito as x call {with x MATCH (m:DNA_Match)-[rs:shared_match]-(n:DNA_Match) where m.mtHG = x and n.mtHG = x and m.fullname<n.fullname merge (m)-[r:shared_mtHG{mtHG:x}]-(n)}";
        gen.neo4jlib.neo4j_qry.qry_write(cq);
     
        
        // report continued
        cq = "MATCH path=(b1:mt_block{name:'RSRS'})-[r:mt_block_child*0..999]->(b2:mt_block) with b2.name as hg,path,[m in nodes(path)|id(m)] as R optional match (m:DNA_Match) where m.mtHG=hg with hg,apoc.coll.sort(collect(case when m.ancestor_rn>0 then '*^' + m.fullname else m.fullname end + case when m.RN is not null then ' [' + m.RN + ']' else '' end)) as match, R, gen.graph.get_ordpath(R) as op with op, R,match,hg order by op MATCH p=(b3:mt_block{name:hg})-[r:mt_block_snp]->(v) with v, R,match,hg order by op, v.sort with R,match,hg, collect(v.name) as variants return apoc.text.lpad('',(size(R)-1)*3,'.') + hg as haplogroup ,size(variants) as var_ct,variants,size(R) as haplotree_level, size(match) as match_ct,case when size(match)>100 then 'truncated ' + apoc.coll.remove(match,100,size(match)-99) else match end as match";
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "pruned_tree", "pruned_tree", ct, "", "1:####;3:####;4:####", excelFile, false, cq, false);
        ct = ct + 1;

        cq = "MATCH path=(b1:mt_block{name:'RSRS'})-[r:mt_block_child*0..999]->(b2:mt_block) with b2.name as hg,path,[m in nodes(path)|id(m)] as R optional match (m:DNA_Match) where m.mtHG=hg with hg,apoc.coll.sort(collect(case when m.ancestor_rn>0 then '*^' + m.fullname else m.fullname end + case when m.RN is not null then ' [' + m.RN + ']' else '' end)) as match, R, gen.graph.get_ordpath(R) as op with op, R,match,hg order by op optional MATCH p=(b3:mt_block{name:hg})-[r:mt_block_snp]->(v) with v, R,match,hg order by op, v.sort with R,match,hg, collect(v.name) as variants return apoc.text.lpad('',(size(R)-1)*3,'.') + hg as haplogroup ,size(variants) as var_ct,variants,size(R) as haplotree_level, size(match) as match_ct,case when size(match)>100 then 'truncated ' + apoc.coll.remove(match,100,size(match)-99) else match end as match";
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "pruned_with_zero_snps", "pruned_with_zero_snps", ct, "", "1:####;3:####;4:####", excelFile, false, cq, false);
        ct = ct + 1;
        
        cq = "MATCH path=(b1:mt_block{name:'RSRS'})-[r:mt_block_child*0..999]->(b2:mt_block) with b2.name as hg,path,[m in nodes(path)|id(m)] as R optional match (m:DNA_Match) where m.mtHG=hg with hg,apoc.coll.sort(collect(case when m.ancestor_rn>0 then '*^' + m.fullname else m.fullname end + case when m.RN is not null then ' [' + m.RN + ']' else '' end)) as match, R, gen.graph.get_ordpath(R) as op with op, R,match,hg order by op optional MATCH p=(b3:mt_block{name:hg})-[r:mt_block_snp]->(v) with v, R,match,hg order by op, v.sort with R,match,hg, collect(v.name) as variants where size(collect(v.name))=0 return apoc.text.lpad('',(size(R)-1)*3,'.') + hg as haplogroup ,size(variants) as var_ct,variants,size(R) as haplotree_level, size(match) as match_ct,case when size(match)>100 then 'truncated ' + apoc.coll.remove(match,100,size(match)-99) else match end as match";
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "pruned_just_zero_snp", "pruned_just_zero_snp", ct, "", "1:####;3:####;4:####", excelFile, true, cq, false);
        ct = ct + 1;
        

        
//        cq = " ";
//        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "pruning_mt_haplotree", "pruned_tree", ct, "", "1:####;3:####;4:####", excelFile, false, cq, false);
//        ct = ct + 1;
//        
//
//        
//        cq = " ";
//        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "pruning_mt_haplotree", "pruned_tree", ct, "", "1:####;3:####;4:####", excelFile, false, cq, false);
//        ct = ct + 1;
//        

        //gen.tree.mt_haplotree_with_matches mtm = new gen.tree.mt_haplotree_with_matches();        
//        mtm.mt_haplotree_matches();
        
        
       return "Completed";
    }
         
}