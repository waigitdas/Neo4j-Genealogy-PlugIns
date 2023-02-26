/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.mt_research;

import gen.neo4jlib.neo4j_qry;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Array;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class mt_seq_to_haplogroup {
    public static String r[][];
    public static String rt;
    public static int type_pos;
    public static int phase_shift;
    public static int look_ahead=2;
    public static int look_ahead_threshold = 2;
    public static String kit_name;
    public static int ext = 8;
    public static int nref=2;
     @UserFunction
    @Description("Template used in creating new functions.")
    
    public String find_mt_seq_variants(
        @Name("kit") 
            String kit,
        @Name("ref_type")
            String ref_type,
        @Name("vendor")
            String vendor
  )
   
         { 
             
        find_variants(kit,ref_type,vendor);
         return "";
            }

    
    
    public static void main(String args[]) {
//        find_variants("425497","RSRS","ftdna");                               
//        find_variants("425497","RSRS","yfull");                               
//        find_variants("425497","RSRS","phylotree");                               
//        find_variants("B51965","RSRS","ftdna");
//        find_variants("B51965","RSRS","yfull");
//        find_variants("B51965","RSRS","phylotree");
        find_variants("EU684000","RSRS","ftdna");
//        find_variants("330527","RSRS","yfull");
//        find_variants("330527","RSRS","phylotree");
//        find_variants("446574","RSRS","ftdna");
//        find_variants("446574","RSRS","yfull");
//        find_variants("446574","RSRS","phylotree");
//        find_variants("425497","rCRS","ftdna");                               
//        find_variants("425497","rCRS","yfull");                               
//        find_variants("425497","rCRS","phylotree");                               
//        find_variants("B51965","rCRS","ftdna");
//        find_variants("B51965","rCRS","phylotree");
//        find_variants("B51965","rCRS","yfull");
//        find_variants("330527","rCRS","ftdna");
//        find_variants("330527","rCRS","yfull");
//        find_variants("330527","rCRS","phylotree");
//        find_variants("446574","rCRS","ftdna");
//        find_variants("446574","rCRS","yfull");
//        find_variants("446574","rCRS","phylotree");
//        find_variants("792577","RSRS","ftdna");
         }
    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
     public static String find_variants(String kit, String ref_type, String vendor) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String variants = "[";
       kit_name=kit;
        phase_shift = 0 ;
        if (ref_type.compareTo("rCRS")==0)
        {
            rt= "rCRC";
            type_pos = 3;
        }
            
        if (ref_type.compareTo("RSRS")==0)
        {
            rt= "RSRS";
            type_pos = 4;
        }
            
            
        //initial query sort by sequence position and comparing probaband to rCRS and RSRS values at each location
        String cq = "MATCH path=(k:seq_kit{name:'" + kit + "'})-[r:seq_kit_pos]->(p1:seq_pos) with k.name as kit, p1.loc as loc,p1.rCRS as rCRS,p1.RSRS as RSRS,r.der as der,case when p1.rCRS=r.der then 0 else 1 end as rCRS_diff,case when p1.RSRS=r.der then 0 else 1 end as RSRS_diff,case when p1.RSRS=p1.rCRS then 0 else 1 end as rCRS_RSRS_diff return kit,loc,der,rCRS,RSRS,rCRS_diff,RSRS_diff, rCRS_RSRS_diff order by kit, loc";
        
        //export to csv
        String fn =  "mt_inserts.csv";
        gen.neo4jlib.neo4j_qry.qry_to_csv(cq, fn);
        
        //read in csv for processing
        String c[] = gen.neo4jlib.file_lib.readFileByLine(gen.neo4jlib.neo4j_info.Import_Dir + fn).split("\n");
        
        //move data to array
        r = new String[c.length][11];
        // 0 = kit
        // 1 = position
        // 2 = der
        // 3 = rCRS anc
        // 4 = RSRS anc
        // 5 = rCRS different
        // 6 = RSRS different
        // 7 rCRS and RSRS are different
        // 8 phase shift
        // 9 added text to variant name
        
        for (int i= 0;i<c.length;i++)
        {
            String cc[] = c[i].split(",");
            for (int j=0; j<cc.length; j++)
            {
                r[i][j] = cc[j].replace("\"","");
            }
        }
        
        // populate phase shift field in the array
        for (int i=1;i<r.length; i++)
        {
            try
            {
            if (r[i][type_pos + nref].compareTo("1") == 0 )
            {
                //check to see if insertion, deletion or simple mutation
                //if statement adds phased_shift to access evaluated position, 
                //but this needs to be corrected for nect step; thus i - phase_shift.
                pos_eval(i);
                
                //if [ext] is null, then simple mutation
                if (r[i-1][ext]==null)
                {
//                System.out.println(r[i][type_pos] +  r[i][1] + r[i + phase_shift][2]  + "\t\t" + phase_shift);
                variants = variants + "'" + r[i][type_pos] +  r[i][1] + r[i + phase_shift][2]  + "',";
                }
                else //insertion or deletion
                {
//                System.out.println(r[i-1][1] + r[i-1][ext] + "\t\t" + phase_shift);
                variants = variants + "'" + r[i  - 1][1] + r[i  - 1][ext] +  "',";
                }
            }
            }
            catch(Exception e){}
        }  //next i
        
        variants = variants.strip().substring(0,variants.length()-1) + "]";
        
//        System.out.println(variants);
        
        File fsum = new File(gen.neo4jlib.neo4j_info.Import_Dir + "seq_sum.csv");
        try{
        FileWriter fs = new FileWriter(fsum);
        
        for (int i=0; i<r.length; i++)
        {
            for (int j=0;j<10;j++)
            {
                if (r[i][j]==null)
                {
                    r[i][j]="~";
                }
                fs.write(r[i][j]);
            
            if (j<9)
            {
            fs.write(",");
            }
            else 
            {
                fs.write("\n") ;
                        }
        }
        }
        fs.flush();
        fs.close();
        }
        catch(Exception e){
//        System.out.println(e.getMessage());
        }
        
        if (vendor.compareTo("ftdna")==0){
        cq = "with " + variants + " as proband_variants MATCH path=(b1:mt_block{name:'RSRS'})-[r:mt_block_child*0..999]->(b2:mt_block{ftdna:1}) with proband_variants,b2, [x in nodes(path)|case when size(apoc.coll.intersection(x.variants, proband_variants))>0 then '*' else '' end + x.name] as blocks,[y in nodes(path)|id(y)] as op, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten([z in nodes(path) where z.variants is not null|z.variants]))) as variants, [w in nodes(path)|case when size(apoc.coll.intersection(w.variants, proband_variants))>0 then '1' else '0' end] as lvl_ct with proband_variants,b2,blocks,variants,size(op) as lvl, lvl_ct,gen.graph.get_ordpath(op) as op with b2.name as block,lvl-1 as lvl, size(b2.variants) as ct, lvl_ct, b2.variants as block_variants, blocks,size(variants) as ct2, variants as cum_variants, op, apoc.coll.intersection(variants, proband_variants) as intersect,apoc.coll.subtract(proband_variants,variants ) as unused,apoc.coll.subtract(variants, proband_variants) as missing with block,lvl,apoc.coll.occurrences(lvl_ct,'1') as lvl_ok, blocks as path,ct2 as ref_variant_ct, size(intersect) as matching_variant_ct,size(missing) as missing_ct,size(unused) as unused_variant_ct, intersect,missing, unused, ct as block_variant_ct,block_variants,cum_variants with block, lvl, lvl_ok, path, ref_variant_ct, matching_variant_ct, missing_ct, unused_variant_ct, intersect, missing, unused, block_variant_ct, block_variants, cum_variants where lvl=lvl_ok return block, lvl, lvl_ok, path, ref_variant_ct, matching_variant_ct, missing_ct, unused_variant_ct, intersect, missing, unused, block_variant_ct, block_variants, cum_variants order by size(intersect) desc,lvl desc limit 1";
       System.out.println(cq);
        }
  
        if (vendor.compareTo("phylotree")==0){
        cq = "with " + variants + " as proband_variants MATCH path=(b1:mt_block{name:'RSRS'})-[r:mt_block_child*0..999]->(b2:mt_block{phylotree:1}) with proband_variants,b2, [x in nodes(path)|case when size(apoc.coll.intersection(x.variants, proband_variants))>0 then '*' else '' end + x.name] as blocks,[y in nodes(path)|id(y)] as op, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten([z in nodes(path) where z.variants is not null|z.variants]))) as variants, [w in nodes(path)|case when size(apoc.coll.intersection(w.variants, proband_variants))>0 then '1' else '0' end] as lvl_ct with proband_variants,b2,blocks,variants,size(op) as lvl, lvl_ct,gen.graph.get_ordpath(op) as op with b2.name as block,lvl-1 as lvl, size(b2.variants) as ct, lvl_ct, b2.variants as block_variants, blocks,size(variants) as ct2, variants as cum_variants, op, apoc.coll.intersection(variants, proband_variants) as intersect,apoc.coll.subtract(proband_variants,variants ) as unused,apoc.coll.subtract(variants, proband_variants) as missing with block,lvl,apoc.coll.occurrences(lvl_ct,'1') as lvl_ok, blocks as path,ct2 as ref_variant_ct, size(intersect) as matching_variant_ct,size(missing) as missing_ct,size(unused) as unused_variant_ct, intersect,missing, unused, ct as block_variant_ct,block_variants,cum_variants with block, lvl, lvl_ok, path, ref_variant_ct, matching_variant_ct, missing_ct, unused_variant_ct, intersect, missing, unused, block_variant_ct, block_variants, cum_variants where lvl=lvl_ok return block, lvl, lvl_ok, path, ref_variant_ct, matching_variant_ct, missing_ct, unused_variant_ct, intersect, missing, unused, block_variant_ct, block_variants, cum_variants order by size(intersect) desc,lvl desc limit 1";
        }
  
        if (vendor.compareTo("yfull")==0){
                    cq = "with " + variants + " as proband_variants MATCH path=(b1:mt_block{name:'L'})-[r:mt_block_child*0..999]->(b2:mt_block{yfull:1}) with proband_variants,b2, [x in nodes(path)|case when size(apoc.coll.intersection(x.yfull_variants, proband_variants))>0 then '*' else '' end + x.name] as blocks,[y in nodes(path)|id(y)] as op, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten([z in nodes(path) where z.yfull_variants is not null|z.yfull_variants]))) as variants, [w in nodes(path)|case when size(apoc.coll.intersection(w.yfull_variants, proband_variants))>0 then '1' else '0' end] as lvl_ct with proband_variants,b2,blocks,variants,size(op) as lvl, lvl_ct,gen.graph.get_ordpath(op) as op with b2.name as block,lvl-1 as lvl, size(b2.yfull_variants) as ct, lvl_ct, b2.yfull_variants as block_variants, blocks,size(variants) as ct2, variants as cum_variants, op, apoc.coll.intersection(variants, proband_variants) as intersect,apoc.coll.subtract(proband_variants,variants ) as unused,apoc.coll.subtract(variants, proband_variants) as missing with block,lvl,apoc.coll.occurrences(lvl_ct,'1') as lvl_ok, blocks as path,ct2 as ref_variant_ct, size(intersect) as matching_variant_ct,size(missing) as missing_ct,size(unused) as unused_variant_ct, intersect,missing, unused, ct as block_variant_ct,block_variants,cum_variants with block, lvl, lvl_ok, path, ref_variant_ct, matching_variant_ct, missing_ct, unused_variant_ct, intersect, missing, unused, block_variant_ct, block_variants, cum_variants where lvl=lvl_ok return block, lvl, lvl_ok, path, ref_variant_ct, matching_variant_ct, missing_ct, unused_variant_ct, intersect, missing, unused, block_variant_ct, block_variants, cum_variants order by size(intersect) desc,lvl desc limit 1";
            //cq="with " + variants + " as proband_variants MATCH path=(b1:mt_yfull_block{name:'L'})-[r:mt_yfull_block_child*0..999]->(b2:mt_yfull_block) with proband_variants,b2, [x in nodes(path)|case when size(apoc.coll.intersection(x.variants, proband_variants))>0 then '*' else '' end + x.name] as blocks,[y in nodes(path)|id(y)] as op, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten([z in nodes(path) where z.variants is not null|z.variants]))) as variants, [w in nodes(path)|case when size(apoc.coll.intersection(w.variants, proband_variants))>0 then '1' else '0' end] as lvl_ct with proband_variants,b2,blocks,variants,size(op) as lvl, lvl_ct,gen.graph.get_ordpath(op) as op with b2.name as block,lvl-1 as lvl, size(b2.variants) as ct, lvl_ct, b2.variants as block_variants, blocks,size(variants) as ct2, variants as cum_variants, op, apoc.coll.intersection(variants, proband_variants) as intersect,apoc.coll.subtract(proband_variants,variants ) as unused,apoc.coll.subtract(variants, proband_variants) as missing with block,lvl,apoc.coll.occurrences(lvl_ct,'1') as lvl_ok, blocks as path,ct2 as ref_variant_ct, size(intersect) as matching_variant_ct,size(missing) as missing_ct,size(unused) as unused_variant_ct, intersect,missing, unused, ct as block_variant_ct,block_variants,cum_variants with block, lvl, lvl_ok, path, ref_variant_ct, matching_variant_ct, missing_ct, unused_variant_ct, intersect, missing, unused, block_variant_ct, block_variants, cum_variants where lvl=lvl_ok return block, lvl, lvl_ok, path, ref_variant_ct, matching_variant_ct, missing_ct, unused_variant_ct, intersect, missing, unused, block_variant_ct, block_variants, cum_variants order by size(intersect) desc,lvl desc limit 1";
        }
//        String fn2 = "haplogroup_report";
//        File fnhg = new File(gen.neo4jlib.neo4j_info.Database_Dir + fns);
//        try{
//            FileWriter fw2 = new FileWriter(fnhg);
//            fw2.write("kit,hg,ref-seq,vendor\n");
//        
//            
        String hg = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n")[0].split(",")[0];
        
        System.out.println(kit_name + ": " + hg + "\t\t" + ref_type + "\t\t" + vendor);
//    }
//    catch(exception e){}
    
        return "";
    }
     
    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
     public static void pos_eval(int i)
     {
         //i is the position where the anc and der nucleotide differ.

         //an insertion will have occurred at 
         //   pos i if it is a simple mutation
         //   pos i + 1 if this is a cytosine-stretch region
         
         //check for insertion
         int ct = 0;   //may be several rows with offsets
         int ins_ct = 0; //how many rows with insersion
         Boolean isSequential = true;  //insersion mutations must be sequential
         
//         try
//         {
//           //is it backe mutation?
//           if(r[i-1][type_pos].compareTo(r[i-1 + phase_shift][2])==0 && r[i-1][type_pos].compareTo(r[i  + phase_shift][2])==0 && r[i][type_pos].compareTo(r[i  + phase_shift + 1][2])==0 && r[i+phase_shift -1][type_pos + nref].compareTo("1")==0 ) 
//           {
//              r[i][2] = r[i][2].toLowerCase();
//           }
//           
//           else{
           if((r[i-1][type_pos].compareTo(r[i-1 + phase_shift][2])==0 && (r[i-1][type_pos].compareTo(r[i  + phase_shift][2])==0 && r[i][type_pos].compareTo(r[i  + phase_shift + 1][2])==0) || r[i][type_pos].compareTo("N")==0))
           {
//               System.out.println("\t" + r[i-1][type_pos+ nref] + r[i][type_pos+ nref] +  r[i + phase_shift -1][type_pos+ nref]+ r[i + phase_shift][type_pos+ nref] );
               //is mutation a back mutation?
               if(r[i-1][type_pos+ nref].compareTo("0")==0 && r[i + phase_shift][type_pos+ nref].compareTo("0")==0) 
                    //  && r[i + phase_shift][2].compareTo(r[i + phase_shift + 1][2])==0) 
               {
                  r[i][2] = r[i][2].toLowerCase(); 
                   }
               
               else
               {
               if  (r[i][type_pos].compareTo("N")!=0)
               {
                   phase_shift = phase_shift + 1;
               }
               r[i-1][ext] = ".1" + r[i-1][2];
               r[i-1][9] = String.valueOf(phase_shift);

           
            //update comparisons for downstream rows
            //phase_shift will realign positions in reference and proband sequences

            for(int k=i+1; k < r.length-phase_shift; k++)
            {
                if (r[k][type_pos].compareTo(r[k + phase_shift][2])==0)
                {
                    r[k][type_pos+ nref] = "0";
                }
                else 
                {
                    r[k][type_pos+ nref] = "1";
//                    System.out.println("\t\t" + k);
                }
            }
           }
     }
     
     }
           
           

         
     
}
