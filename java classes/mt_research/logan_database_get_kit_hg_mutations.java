/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.mt_research;

import java.io.File;
import java.io.FileWriter;
import java.util.regex.Pattern;

/**
 *
 * @author david
 */
public class logan_database_get_kit_hg_mutations {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        try
                {
                    //set upindices fr node properties
                    gen.neo4jlib.neo4j_qry.CreateIndex("mt_haplogroup", "gb_hg");
                    gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("seq_kit_tile_pattern", "gb_hg");
                    gen.neo4jlib.neo4j_qry.CreateIndex("seq_kit", "gb_hg");
                    gen.neo4jlib.neo4j_qry.CreateIndex("tile_pattern", "op");
                    gen.neo4jlib.neo4j_qry.CreateIndex("tile_pattern", "tiles");
                }
        catch(Exception e){}
        
        String fn = "E:/DAS_Coded_BU_2017/Genealogy/DNA/mt_haplotree_project/Logan_mt_database_15_Feb_2023.txt";
        String c[] = gen.neo4jlib.file_lib.ReadFileByLineWithEncoding(fn).split("\n");
        
        //String k[][] = new String[c.length][3];
        String fo = "kit_hg_mutations.csv";
        String fos = "kit_mutations.csv";
        File f = new File(gen.neo4jlib.neo4j_info.Import_Dir + fo);
        File fs = new File(gen.neo4jlib.neo4j_info.Import_Dir + fos);
        FileWriter fw = null;
        FileWriter fws = null;
        
        try{
        fw= new FileWriter(f);
        fw.write("kit|hg|variants\n");
        fws= new FileWriter(fs);
        fws.write("kit|variant\n");
        }
        catch (Exception e){};
        
        int i = 0;
        for (i=0;i<c.length; i++)
        {
            if(c[i].strip().compareTo(" ")>0 && c[i].replace("//","").compareTo(c[i])==0)
            {
            String s[] = c[i].replace("[[","[").split(Pattern.quote("{"));
            try{
                fw.write(s[0].replace("case","").replace(":","").strip() + "|");
                fw.write(s[1].split(Pattern.quote("Haplogroup"))[1].strip().split(Pattern.quote("["))[1].split(Pattern.quote("]"))[0].strip() + "|");
                String variants = "\"" + s[1].split(Pattern.quote("ml ="))[1].strip().split(";")[0].strip().replace(" ","\"" + ", " + "\"") + "\"" ;
                fw.write("[" + variants + "]\n") ;
                
                String variant[] = variants.split(",");
                for (int j=0; j<variant.length; j++)
                {
                    fws.write(s[0].replace("case","").replace(":","").strip() + "|" + variant[j].strip() + "\n");
                    
                
                } // next j
            }  //end try
            catch(Exception e){}
            }
            } //next i
        
        try
        {
            fw.flush();
            fws.flush();
            fw.close();
            fws.close();
        }
        catch(Exception e){}
        
       //add seq_kit nodes if they do not already exist 
       gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///kit_hg_mutations.csv'  as line FIELDTERMINATOR '|' with line where line.kit is not null merge (k:seq_kit{name:toString(line.kit)})");
       
       //create mt_variant relationship for any new scenarios
       gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///kit_mutations.csv'  as line FIELDTERMINATOR '|' with line where line.variant is not null merge (v:mt_variant{name:toString(line.variant)}) ");
               
       //set kit properties for haplogroup and variants
       gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///kit_hg_mutations.csv'  as line FIELDTERMINATOR '|' match (k:seq_kit{name:toString(line.kit)}) set k.gb_hg=toString(line.hg) ,k.variants = toString(line.variants)");
        
       //create seq_kit_variant relationships
       gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///kit_mutations.csv'  as line FIELDTERMINATOR '|' with line where line.variant is not null match (k:seq_kit{name:toString(line.kit)})  match (v:mt_variant{name:toString(line.variant)}) merge (k)-[r:seq_kit_variant]->(v)");
       
       //create tile_pattern nodes
       gen.neo4jlib.neo4j_qry.qry_write("MATCH (k:seq_kit)-[r:kit_tile]->(t:tile) with k,t order by t.parition_id with k, collect(t.tile_id) as tids with tids,count(*) as kit_ct with tids,kit_ct,gen.graph.get_ordpath(tids) as op merge (p:tile_pattern{tiles:tids,op:op})");
       
       //create seq_kit_tile_pattern relationship
       gen.neo4jlib.neo4j_qry.qry_write("MATCH (k:seq_kit)-[r:kit_tile]->(t:tile) with k,t order by t.partition_id with k, collect(t.tile_id) as tids match (p:tile_pattern) where p.tiles = tids merge (k)-[r:seq_kit_tile_pattern]->(p)");
       
       //set seq_kit_tile_patter property hg
       gen.neo4jlib.neo4j_qry.qry_write("MATCH (k:seq_kit)-[r:seq_kit_tile_pattern]->(p:tile_pattern) where k.gb_hg is not null set r.gb_hg=k.gb_hg");
       
       //create mt_haplgroup nodes
       gen.neo4jlib.neo4j_qry.qry_write("MATCH (k:seq_kit) with k.gb_hg as hg where k.gb_hg is not null  merge(h:mt_haplogroup{gb_hg:hg})");
       
       //create kit_hg relationshop
       gen.neo4jlib.neo4j_qry.qry_write("match(k:seq_kit) match (h:mt_haplogroup) where k.gb_hg=h.gb_hg merge (k)-[r:seq_kit_hg]-(h)");
       
       
       
       String cq = "MATCH p=(k:seq_kit)-[[r:kit_tile]]->(t:tile) with k,r,t order by t.parition_id with k, collect(t.tile_id) as tids with apoc.coll.sort(collect(k.name)) as kits,apoc.coll.sort(collect (distinct k.gb_hg)) as hg,collect(k.gb_hg) as hgct, size(tids) as tid_ct,tids return size(kits) as kit_ct,size(hg) as hg_in_pattern,size(hgct) as kits_with_hg,tid_ct, hg, kits, tids order by hg";
       String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "GenBank_hg_partitions", "Summary", 1, "", "0:###;1:####;2:####;3:####;4:####", "", true, "Cypher queryn" + cq,false);
       
    }
}
