/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.ref;

import gen.neo4jlib.neo4j_qry;
import java.util.regex.Pattern;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class mt_hg_from_snps {
    @UserFunction
    @Description("Template used in creating new functions.")
    
    public String udf_name_seen_in_listing(
        @Name("snps") 
            String snps
  )
   
         { 
                 
        get_snp_haplogroup(snps);
         return "";
            }

    
    
    public static void main(String args[]) {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        

        
        String filePath = gen.neo4jlib.neo4j_info.Import_Dir + "Stinnett_mt_haplotree.csv";
        String s[] = gen.neo4jlib.file_lib.ReadFileByLineWithEncoding(filePath).split("\n");
        
       
//        String s =gen.neo4jlib.neo4j_qry.qry_str("LOAD CSV WITH HEADERS FROM 'file:///Stinnett_mt_haplotree.csv' as line FIELDTERMINATOR '|' with line.Kit_Number +'|' +line.Haplogroup +'|' + line.HVR1_Mutations + ', ' + line.HVR2_Mutations + ', ' + line.Coding_Region_Mutations as snps with snps where snps is not null return snps + '\n'");
        
       // 
        
        for (int i=1;i<s.length; i++)
        {
        String snps[] = s[i].split(Pattern.quote("|"));
        String explain = "";
        String delimiter = ","; // "\t";
        //System.out.println(snps[0] + "\t" + snps[1]);
       try{
        String hg = get_snp_haplogroup(snps[5] + ", " + snps[6] + ", " + snps[7]);
        if (hg.compareTo(snps[4])!=0)
        {
            explain = explain_discrepancy(snps[5] + ", " + snps[6] + ", " + snps[7], snps[4],hg);
        }
        System.out.println(i + delimiter + snps[0] + delimiter + snps[4] + delimiter + hg + delimiter + explain );  //+ delimiter + snps[1] );
       }
       catch(Exception e){
           explain = "no CR";
        System.out.println(i + delimiter + snps[0] + delimiter + snps[4]  + delimiter +   delimiter +  explain );  //+ delimiter + snps[1] );
       }
        }  
        
    }
    
       public static String get_snp_haplogroup(String snps)
     {
            String new_snp = "[";
             String ss[] = snps.split(",");
             for (int i=0; i<ss.length;i++)
             {
                 new_snp = new_snp + "'" + ss[i].strip() + "'";
                 if (i < ss.length-1) {new_snp=new_snp + ",";}
                 else {new_snp=new_snp + "]";}
             }
             try{
             gen.neo4jlib.neo4j_info.cq = "with " + new_snp + " as proband_snps MATCH path=(b1:mt_block{name:'RSRS'})-[r:mt_block_child*0..999]->(b2:mt_block) with proband_snps,b2, [x in nodes(path)|case when size(apoc.coll.intersection(x.snps, proband_snps))>0 then '*' else '' end + x.name] as blocks,[y in nodes(path)|id(y)] as op, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten([z in nodes(path) where z.snps is not null|z.snps]))) as snps, [w in nodes(path)|case when size(apoc.coll.intersection(w.snps, proband_snps))>0 then '1' else '0' end] as lvl_ct with proband_snps,b2,blocks,snps,size(op) as lvl, lvl_ct,gen.graph.get_ordpath(op) as op with b2.name as block,lvl-1 as lvl,size(b2.snps) as ct,lvl_ct,b2.snps as block_snps,blocks,size(snps) as ct2,snps as cum_snps,op,apoc.coll.intersection(snps, proband_snps) as intersect,apoc.coll.subtract(proband_snps,snps ) as novel,apoc.coll.subtract(snps, proband_snps) as missing with block,lvl,apoc.coll.occurrences(lvl_ct,'1') as lvl_ok, blocks as path,ct2 as ref_snp_ct, size(intersect) as matching_snp_ct,size(missing) as missing_ct,size(novel) as novel_snp_ct, intersect,missing, novel, ct as block_snp_ct,block_snps,cum_snps with block, lvl, lvl_ok, path, ref_snp_ct, matching_snp_ct, missing_ct, novel_snp_ct, intersect, missing, novel, block_snp_ct, block_snps, cum_snps where lvl=lvl_ok return block, lvl, lvl_ok, path, ref_snp_ct, matching_snp_ct, missing_ct, novel_snp_ct, intersect, missing, novel, block_snp_ct, block_snps, cum_snps order by size(intersect) desc,lvl desc";
             String c[] = gen.neo4jlib.neo4j_qry.qry_to_csv(gen.neo4jlib.neo4j_info.cq).split("\n");
             String h[]=c[0].split(",");
             
             return h[0].replace("\"", "");
             } catch(Exception e) {return "not computer";}
     }
     
     public static String explain_discrepancy(String snps, String ftdna, String gfg)
     {
         if(ftdna.endsWith("!"))         
         {
             String fs[] = ftdna.split("-");
             if(fs[0].compareTo(gfg)==0)
             {
             return("OK; back mutation");
             }
             else
             {
                 return "back mutation";
             }
         }
 
         if(snps.compareTo(snps.replace("Private", ""))!=0)
         {
             return "Private CR";
         }
         
         return "unknown";
         //return("\n\n"+ snps + "\n--------------------------\n");
         
//         if (ftdna.endsWith("D") || ftdna.endsWith("d")  || ftdna.endsWith("-") )
//                 {
//             return "deletion code";
//         }
// 
//         
//         
//         if (ftdna.endsWith("A") || ftdna.endsWith("C") || ftdna.endsWith("G") || ftdna.endsWith("T") ) {}
//         else
//         {
//             return "heteroplasmy code";
//         }
//         
//        if (ftdna.endsWith("a") || ftdna.endsWith("c") || ftdna.endsWith("g") || ftdna.endsWith("r") ) {}
//         else
//         {
//             return "transversion";
//         }
//         
         
//         String new_snp = "[";
//             String ss[] = snps.split(",");
//             for (int i=0; i<ss.length;i++)
//             {
//                 new_snp = new_snp + "'" + ss[i].strip() + "'";
//                 if (i < ss.length-1) {new_snp=new_snp + ",";}
//                 else {new_snp=new_snp + "]";}
//             }
//             
//         String s ="explaination";
//         String cq = "with " + new_snp + " as proband_snps MATCH path=(b1:mt_block{name:'RSRS'})-[r:mt_block_child*0..999]->(b2:mt_block) with proband_snps,b2, [x in nodes(path)|case when size(apoc.coll.intersection(x.snps, proband_snps))>0 then '*' else '' end + x.name] as blocks,[y in nodes(path)|id(y)] as op, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten([z in nodes(path) where z.snps is not null|z.snps]))) as snps, [w in nodes(path)|case when size(apoc.coll.intersection(w.snps, proband_snps))>0 then '1' else '0' end] as lvl_ct with proband_snps,b2,blocks,snps,size(op) as lvl, lvl_ct,gen.graph.get_ordpath(op) as op with b2.name as block,lvl-1 as lvl,size(b2.snps) as ct,lvl_ct,b2.snps as block_snps,blocks,size(snps) as ct2,snps as cum_snps,op,apoc.coll.intersection(snps, proband_snps) as intersect,apoc.coll.subtract(proband_snps,snps ) as novel,apoc.coll.subtract(snps, proband_snps) as missing with block,lvl,apoc.coll.occurrences(lvl_ct,'1') as lvl_ok, blocks as path,ct2 as ref_snp_ct, size(intersect) as matching_snp_ct,size(missing) as missing_ct,size(novel) as novel_snp_ct, intersect,missing, novel, ct as block_snp_ct,block_snps,cum_snps with block, lvl, lvl_ok, path, ref_snp_ct, matching_snp_ct, missing_ct, novel_snp_ct, intersect, missing, novel, block_snp_ct, block_snps, cum_snps  return block, lvl, lvl_ok, path, ref_snp_ct, matching_snp_ct, missing_ct, novel_snp_ct, intersect, missing, novel, block_snp_ct, block_snps, cum_snps order by size(intersect) desc,lvl desc";
//
//        String c[] = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split(Pattern.quote("["));  //splits a snp list
//        String rowHg = c[0].split(",")[0];
//        
//     int iio=0;
//        //find row with FTDNA haplogroup
//        for (int i=0; i<c.length; i++)
//        {
//            String h[]=c[i].split(",");
//        if (rowHg.compareTo(ftdna)==0)
//        {
//            System.out.println("found");
//        }            
//            int iop=0;
            
//        }
                 
         //run unfiltered query and then find row with ftdna result, report bavk path
         
         //identify whether there  are indels
         
         //heteroplasmy codes
         
         //return "unknown";
     }

}
