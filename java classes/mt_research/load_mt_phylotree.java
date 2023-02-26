/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.mt_research;

import gen.load.web_file_to_import_folder;
import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class load_mt_phylotree {
    @UserFunction
    @Description("In development. The haplotree used here is not compplete.")

    public String mt_phylotree_load(
        
  )
   
         { 
             
        load_phylotree();
         return "";
            }

    
    
    public static void main(String args[]) {
        load_phylotree();
    }
    
     public static String load_phylotree() 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        //gen.ref.upload_mt_haplotree.load_mt_haplotree();
        
        
        //phylotree file on Azure
        String ptf = "https://blobswai.blob.core.windows.net/gfg-software/phylotree16.txt";
        web_file_to_import_folder.url_file_to_import_dir(ptf,"phylotree16.txt");        
        
        //delete prior load
        gen.neo4jlib.neo4j_qry.qry_write("match (b:mt_block)-[r:mt_block_variant{src:'phylotree'}]-(v:mt_variant{src:'phylotree'}) delete r,v") ;
        gen.neo4jlib.neo4j_qry.qry_write("match(b:mt_block{phylotree:1}) remove b.phylotree");
        gen.neo4jlib.neo4j_qry.qry_write("match (v:mt_varant) remove v.phylotree");
        gen.neo4jlib.neo4j_qry.qry_write("match ()-[r:mt_block_variant]-() remove r.phylotree");

        //gen.neo4jlib.neo4j_qry.qry_write("match (b:mt_block) set b.ftdna=1");
        
        String c[] = gen.neo4jlib.file_lib.ReadFileByLineWithEncoding(gen.neo4jlib.neo4j_info.Import_Dir + "phylotree16.txt").split("\n");
        String s="hg|variant\n";
        for (int i=0; i < c.length; i++)
        {
            String ss[] = c[i].split(" ");
            for (int j=1; j<ss.length; j++)  //first item is the hg
            { 
                if (ss[j]!="")
                {
                    s = s + ss[0] + "|" + ss[j].replace(",","") + "\n";
                }
            }
        }
 
                           System.out.println(s);
 
        gen.neo4jlib.file_lib.writeFile(s,gen.neo4jlib.neo4j_info.Import_Dir + "phylotree.csv");
        //tag mt_blocks
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///phylotree.csv' as line FIELDTERMINATOR '|' match (b:mt_block{name:line.hg}) set b.phylotree=1");
        
        
        //add new SNPs from phylotree
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///phylotree.csv' as line FIELDTERMINATOR '|' with collect(distinct line.variant) as variants match (v0:mt_variant) with variants,collect(distinct v0.name) as existing_variants with apoc.coll.subtract(variants,existing_variants) as add_variants unwind add_variants as x with x where x<>',' merge (v2:mt_variant{name:x, phylotree:1})");
        
        //add phylotree mt_block_variant relationship
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///phylotree.csv' as line FIELDTERMINATOR '|' match (b:mt_block{name:line.hg}) match (v:mt_variant{name:line.variant}) merge (b)-[t:mt_block_variant]->(v)");
        
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///phylotree.csv' as line FIELDTERMINATOR '|' MATCH p=(b:mt_block{name:line.hg})-[r:mt_block_variant]->(v:mt_variant{name:line.variant})  set b.phylotree=1, v.phylotree=1, r.phylotree=1");
        
        //set flag
        //gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(b:mt_block)-[r:mt_block_variant]->(v:mt_variant) with v where b.phylotree is null and v.phylotree is null and r.phylotree=1 set v.phylotree=1,r.phylotree=1");
        
        //MATCH (v:mt_variant{src:'phylotree'}) LOAD CSV WITH HEADERS FROM 'file:///phylotree.csv' as line FIELDTERMINATOR '|' with v, line.hg as hg where line.variant=v.name match (b:mt_block{name:hg}) merge (b)-[rv:mt_block_variant{src:'phylotree'}]-(v)");
        
        
        //add mt_variant src property when mt_block_variant relationship already present and
        //distinguish origin of the relationship
        //gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///phylotree.csv' as line FIELDTERMINATOR '|' MATCH p=(b:mt_block{name:line.hg})-[r:mt_block_variant]->(v:mt_variant{name:line.variant}) where v.src is null set r.src='phylotree'");
        
        
       // gen.neo4jlib.neo4j_qry.qry_write("MATCH (n:mt_variant) where n.name='' delete n");
        
        
        
        return "";
    }
}
