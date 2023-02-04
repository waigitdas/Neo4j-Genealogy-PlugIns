/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.file_lib;
import gen.neo4jlib.neo4j_qry;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class project_mt_DNA {
    @UserFunction
    @Description("Load mt_DNA SNP data from FTDNA csv file(s).")

    public String load_project_mt_DNA(
        @Name("dir") 
            String dir
  )
   
         { 
             
        String s = load_mt_dna(dir);
         return s;
            }

    
    
    public static void main(String args[]) {
        load_mt_dna("E:/DAS_Coded_BU_2017/Genealogy/DNA/mt_haplotree_project/");
                
    }
    
     public static String load_mt_dna(String dir) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        try
        {
            gen.neo4jlib.neo4j_qry.qry_write("match (m:mt_kit)-[r]-() delete r");
            gen.neo4jlib.neo4j_qry.qry_write("match(m:mt_kit) delete m");
            gen.neo4jlib.neo4j_qry.CreateIndex("mt_kit", "kit");
            gen.neo4jlib.neo4j_qry.CreateIndex("mt_kit", "mt_hg");
            gen.neo4jlib.neo4j_qry.CreateIndex("mt_kit", "GFG");
            gen.neo4jlib.neo4j_qry.CreateIndex("mt_kit_snp", "name");
        }
        catch(Exception e){}
            String[] paths;
            File f = new File(dir);
            
            paths = f.list();
            Arrays.sort(paths); //so that chromosome browser data goes before population data; making cb_version predictable
            
            String project;
            
           //haplotree file / writer
            FileWriter fw = null;
            String fn =  "computed_mt_haplogroups.csv";
            File rept = new File (gen.neo4jlib.neo4j_info.Import_Dir + fn);
            try {
                fw = new FileWriter(rept);
                fw.write("project|kit|fullname|FTDNA|GFG|discrepancy\n");
            } catch (Exception ex) {
            }

           //match - snps
            FileWriter fws = null;
           String fns = "mt_match_snps.csv";
            File reptsnp = new File (gen.neo4jlib.neo4j_info.Import_Dir + fns);
            try {
                fws = new FileWriter(reptsnp);
                fws.write("kit|snp\n");
            } catch (Exception ex) {
            }

     
  
            for (String pathitem : paths) {
                    String p[] = pathitem.split("_");
                     project = p[0].strip();
                     System.out.println(project + "\n");
                     
                     //write pipe-delimited file to import directory
                     file_lib.get_file_transform_put_in_import_dir(dir + pathitem,  project + "_mt_haplotree.csv");
                    String fl[] = gen.neo4jlib.file_lib.ReadFileByLineWithEncoding(gen.neo4jlib.neo4j_info.Import_Dir + project + "_mt_haplotree.csv").split("\n");
                    String explanation="";
                    
                     for (int i=1; i<fl.length; i++)  //skip header
                     {
                         explanation="";
                         String fls[] = fl[i].split(Pattern.quote("|"));
                         String all_snps = "";
                        //compute GFG mt-haplogroup
                         String hg = "";
                         try{
                             all_snps = fls[5] + "," + fls[6] + "," + fls[7];
                             hg = get_snp_haplogroup(all_snps);
                             if (hg.compareTo(fls[4])!=0) 
                             {
                                 //discrepancy
                                explanation = explain_discrepancy(all_snps, fls[4], hg);
                             }
                         }
                         catch(Exception e){
                             //hg="no CR";
                             try
                             {
                                all_snps = fls[5] + "," + fls[6];
                                hg = get_snp_haplogroup(all_snps);
                                explanation = "no CR";
                            }
                             catch(Exception e1){
                                hg = get_snp_haplogroup(fls[5]);
                                explanation = "missing HVR1 or HVR2 and CR";
                             }
                             }
                         
                         try
                         {
                             if (hg==""){
                                 explanation = "no GFG computed";
                             }
                             fw.write(project + "|" + fls[0] + "|"  + fls[1] + "|"  + fls[4] + "|" + hg + "|" + explanation + "\n" );
                         }
                         catch(Exception e){}
                         
                    //set up mt_match snps
                    if (all_snps!="" )
                    {
                    String sss[] = all_snps.split(",");
                    for (int k=0; k<sss.length; k++)
                    {
                        try
                        {
                            if (sss[k]!=null)
                            {
                            fws.write(fls[0] + "|" + sss[k].strip() + "\n");
                            }
                        }
                        catch(Exception e3){
                               System.out.println(e3.getMessage());
}
                    }
                    }    
                     //System.out.println(project + "\t" + fls[0] + "\t"  + fls[1] + "\t"  + fls[4] + "\t" + hg );
                         
                     }
              try{     
            fw.flush();;

              }
              catch(Exception e){}
              
               }
            
                try{     
            fw.flush();;
            fw.close();         
              }
              catch(Exception e){}
              
                //memorialize analytics in knowledge graph
                //mt_matches
             String lc = "LOAD CSV WITH HEADERS FROM 'file:///" + fn + "' as line FIELDTERMINATOR '|' return line ";
             String cq = "merge (m:mt_kit{kit:toString(line.kit), fullname:toString(case when line.fullname is null then '' else line.fullname end), mt_hg:toString(case when line.FTDNA is null then '' else line.FTDNA end), GFG:toString(case when line.GFG is null then '' else line.GFG end), discrepancy:toString(case when line.discrepancy is null then '' else line.discrepancy end),src:'FTDNA_kit'})";
                    neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);

                    //add snps that are not in original mt-haplotree, creating scr property to distinguish them
                    gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///mt_match_snps.csv' as line FIELDTERMINATOR '|' with toString(case when line.snp is null then '' else line.snp end) as snp optional match(v:mt_variant{name:snp}) with snp,v.name as fnd_variant,count(*) as ct with snp,fnd_variant,ct where fnd_variant is null merge(v2:mt_variant{name:snp,ct:ct,src:'FTDNA_kits'})");
                    
                    //create mt_match_snp relationships
                    gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///mt_match_snps.csv' as line FIELDTERMINATOR '|' match (m:mt_kit{kit:toString(line.kit)}) match (v:mt_variant{name:toString(line.snp)}) merge (m)-[r:mt_match_snp]->(v)");
                    
                    //set kit_ct in mt_variant nodes
                    gen.neo4jlib.neo4j_qry.qry_write("MATCH p=()-[r:mt_match_snp]->(v) with v,count(*) as kit_ct set v.kit_ct=kit_ct");
                    
                    
                 //create mt_shared_snp
                    gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(k1:mt_kit)-[r1:mt_match_snp]->(v:mt_variant)<-[mv:mt_match_snp]-(k2:mt_kit) where k1.kit<k2.kit with distinct k1,k2,collect(distinct v.name) as snps with k1,k2,size(snps) as kit_ct merge (k1)-[r:mt_match_shared_snps{kit_ct:kit_ct}]->(k2)");
                    
                  //create mt_kit_snp nodes 
                  //this redundancy is needed because kits and mt_haplotree are loaded separately
                  gen.neo4jlib.neo4j_qry.qry_write("MATCH p=()-[r:mt_match_snp]->(v:mt_variant) with v ,count(*) as ct merge (m:mt_kit_snp{name:v.name,snp_ct:ct})")
                          ;
                    
//             lc = "LOAD CSV WITH HEADERS FROM 'file:///" + fn + "' as line FIELDTERMINATOR '|' return line ";
//             cq = "merge(m:mt_match{{kit:toString(line.kit),fullname:toString(line.fullname),FTDNA:toString(line.FTDNA),GFG:toString(line.GFG)}) ";
//                    neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);

     cq ="MATCH (mk:mt_kit) RETURN mk.kit as kit,mk.fullname as name,mk.mt_hg as mt_hg, mk.GFG as GFG_mt_hg,mk.discrepancy as discretancy";
     gen.excelLib.queries_to_excel.qry_to_excel(cq, "mt_projuect_hgs", "haplogroups", 1, "", "", "", true, "cypher:\n" + cq, false);
               
        return "completed";
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
             String cq = "with " + new_snp + " as proband_snps MATCH path=(b1:mt_block{name:'RSRS'})-[r:mt_block_child*0..999]->(b2:mt_block) with proband_snps,b2, [x in nodes(path)|case when size(apoc.coll.intersection(x.snps, proband_snps))>0 then '*' else '' end + x.name] as blocks,[y in nodes(path)|id(y)] as op, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten([z in nodes(path) where z.snps is not null|z.snps]))) as snps, [w in nodes(path)|case when size(apoc.coll.intersection(w.snps, proband_snps))>0 then '1' else '0' end] as lvl_ct with proband_snps,b2,blocks,snps,size(op) as lvl, lvl_ct,gen.graph.get_ordpath(op) as op with b2.name as block,lvl-1 as lvl,size(b2.snps) as ct,lvl_ct,b2.snps as block_snps,blocks,size(snps) as ct2,snps as cum_snps,op,apoc.coll.intersection(snps, proband_snps) as intersect,apoc.coll.subtract(proband_snps,snps ) as novel,apoc.coll.subtract(snps, proband_snps) as missing with block,lvl,apoc.coll.occurrences(lvl_ct,'1') as lvl_ok, blocks as path,ct2 as ref_snp_ct, size(intersect) as matching_snp_ct,size(missing) as missing_ct,size(novel) as novel_snp_ct, intersect,missing, novel, ct as block_snp_ct,block_snps,cum_snps with block, lvl, lvl_ok, path, ref_snp_ct, matching_snp_ct, missing_ct, novel_snp_ct, intersect, missing, novel, block_snp_ct, block_snps, cum_snps where lvl=lvl_ok return block, lvl, lvl_ok, path, ref_snp_ct, matching_snp_ct, missing_ct, novel_snp_ct, intersect, missing, novel, block_snp_ct, block_snps, cum_snps order by size(intersect) desc,lvl desc";
             String c[] = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n");
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
         
         //look at FTDNA hg and see if there is a SNP available for each branch
         //return error if any block is missing a proband snp
         try
         {
             String new_snp = "[";
             String ss[] = snps.split(",");
             for (int i=0; i<ss.length;i++)
             {
                 new_snp = new_snp + "'" + ss[i].strip() + "'";
                 if (i < ss.length-1) {new_snp=new_snp + ",";}
                 else {new_snp=new_snp + "]";}
             }
             String cq ="with " + new_snp + " as proband_snps match path=(b1:mt_block{name:'RSRS'})-[rc:mt_block_child*1..99]-(b2:mt_block{name:'" + ftdna + "'}) with proband_snps,[x in nodes(path) where x.name<>'RSRS'|x.name] as blocks unwind blocks as z call { with z MATCH (b:mt_block{name:z}) with b.snps as bsnps,b return b.name as block,bsnps } with block,apoc.coll.intersection(bsnps,proband_snps) as proband_block_snp with block, proband_block_snp where size(proband_block_snp)=0 return collect(block) as blocks_wihout_proband_snp";
             String blocks_missing_proband_snp = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n")[0];
             if (blocks_missing_proband_snp.equals("[]"))
             {
                 if(ftdna.length()<gfg.length())
                 {
                     return "FTDNA lower level in the hierarchy";
                 }
             }
             return "blocks missing snps: " + blocks_missing_proband_snp ;
         }
         catch (Exception e){}
         
//	if (ftdna.endsWith("D") || ftdna.endsWith("d")  || ftdna.endsWith("-") )
//                 {
//             return "deletion code";
//         }
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
         
         
         //////////////////////////////////////////
         // run through entire set of blocks to find rows with ftdna and gfg haplogroup
         String new_snp = "[";
             String ss[] = snps.split(",");
             for (int i=0; i<ss.length;i++)
             {
                 new_snp = new_snp + "'" + ss[i].strip() + "'";
                 if (i < ss.length-1) {new_snp=new_snp + ",";}
                 else {new_snp=new_snp + "]";}
             }
             
         String s ="explaination";
         String cq = "with " + new_snp + " as proband_snps MATCH path=(b1:mt_block{name:'RSRS'})-[r:mt_block_child*0..999]->(b2:mt_block) with proband_snps,b2, [x in nodes(path)|case when size(apoc.coll.intersection(x.snps, proband_snps))>0 then '*' else '' end + x.name] as blocks,[y in nodes(path)|id(y)] as op, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten([z in nodes(path) where z.snps is not null|z.snps]))) as snps, [w in nodes(path)|case when size(apoc.coll.intersection(w.snps, proband_snps))>0 then '1' else '0' end] as lvl_ct with proband_snps,b2,blocks,snps,size(op) as lvl, lvl_ct,gen.graph.get_ordpath(op) as op with b2.name as block,lvl-1 as lvl,size(b2.snps) as ct,lvl_ct,b2.snps as block_snps,blocks,size(snps) as ct2,snps as cum_snps,op,apoc.coll.intersection(snps, proband_snps) as intersect,apoc.coll.subtract(proband_snps,snps ) as novel,apoc.coll.subtract(snps, proband_snps) as missing with block,lvl,apoc.coll.occurrences(lvl_ct,'1') as lvl_ok, blocks as path,ct2 as ref_snp_ct, size(intersect) as matching_snp_ct,size(missing) as missing_ct,size(novel) as novel_snp_ct, intersect,missing, novel, ct as block_snp_ct,block_snps,cum_snps with block, lvl, lvl_ok, path, ref_snp_ct, matching_snp_ct, missing_ct, novel_snp_ct, intersect, missing, novel, block_snp_ct, block_snps, cum_snps  return block, lvl, lvl_ok, path, ref_snp_ct, matching_snp_ct, missing_ct, novel_snp_ct, intersect, missing, novel, block_snp_ct, block_snps, cum_snps order by size(intersect) desc,lvl desc";

        String c[] = gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited_str(cq).split(Pattern.quote("\n"));  //splits a snp list
        String rowHg = c[0].split(Pattern.quote("|"))[0];
        
     String rs ="snp detail: ";
        //find row with FTDNA haplogroup
        for (int i=0; i<c.length; i++)
        {
            String h[]=c[i].split(Pattern.quote("|"));
            String hh = h[0].replace("\"", "");
        if (hh.compareTo(ftdna)==0 || hh.compareTo(gfg)==0 )
        {
            rs = rs + hh + " block snp: " + h[12] + " path: " + h[3] + "--\t--";
        }            
            int iop=0;
            
        }
                 
//        if(rs.compareTo("snp detail: ")>0)
//        {
            return rs + " | " + new_snp;
//        }
         
         //return "unknown";

         
     }
}
