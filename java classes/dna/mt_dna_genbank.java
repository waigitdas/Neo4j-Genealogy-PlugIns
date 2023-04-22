/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.load.web_file_to_import_folder;
import gen.neo4jlib.neo4j_info;
import gen.neo4jlib.neo4j_qry;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.lucene.document.Document;
import org.neo4j.driver.internal.spi.Connection;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
import java.text.SimpleDateFormat;        
import java.text.ParseException;
import java.util.Iterator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class mt_dna_genbank {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String process_mt_snps(
  )
   
         { 
             
        String s = process_snps();
         return s;
            }

    
    
    public static void main(String args[]) {
        //process_snps();
        compute_hg();
    }
    
     public static String process_snps() 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        
      FileWriter fwv =null;       
      try{
          fwv = new FileWriter("E:/DAS_Coded_BU_2017/Genealogy/DNA/Ian_Logan_mt_DNA/logan_mt_hg_snps.csv");
          fwv.write("ct|submitter|accession_number|location|hg|date|snps\n");
                }
      catch(Exception e){}

      String rs2 = "";
      String rs3 = "";
      String fn3 = "";
      HttpGet httpget2 =null;
      HttpClient client = new DefaultHttpClient();
      String result="";
//String rs[] = null;
      
      try{  
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("http://www.ianlogan.co.uk/checker/accession.htm");
        HttpResponse httpresponse = httpclient.execute(httpget);
        Scanner sc = new Scanner(httpresponse.getEntity().getContent());
        StringBuffer sb = new StringBuffer();
        while(sc.hasNext()) 
        {
          sb.append(sc.next());
        }
        result = sb.toString();


        
      }
      catch(Exception e){}
      
      String rs[] = result.split(Pattern.quote("../lists/"));
       
        
      for (int i=1;i<rs.length;i++)
      {
          String result2="";
          String ss[] = rs[i].split(Pattern.quote("target"));
          rs2 = rs2 + ss[0] + "\n"; 
          System.out.println(i + "\t" + ss[0] );
          rs2 ="";
          try{
            fn3 ="http://www.ianlogan.co.uk/lists/" + ss[0];
            httpget2 = new HttpGet(fn3);
            
            HttpResponse httpresponse2 = client.execute(httpget2);
            Scanner sc2 = new Scanner(httpresponse2.getEntity().getContent());
            StringBuffer sb2 = new StringBuffer();
            while(sc2.hasNext()) {
            sb2.append(sc2.next() + "\n");
           result2 = sb2.toString();
   
            }
            String ss1[] =result2.split(Pattern.quote("<pre>"));
            String ss2 = ss1[1].split(Pattern.quote("</pre>"))[0];
            String indiv="";
            String r1[] = ss2.split(Pattern.quote(">"));
            for (int j=1; j<r1.length; j++) //first row is blank
            {
                String r2[] = r1[j].split("\n");

                    indiv = indiv + i + "|" + ss[0] + "|";
                    String loc[] = r2[0].split(Pattern.quote("("));
                    if (loc.length==1)
                    { // no location
                        indiv = indiv + loc[0] + "||";
                    }
                    else {indiv = indiv + loc[0] + "|" + loc[1].replace(")","") + "|";}

                String h[] = r1[j].split("Haplogroup");
                String h2[]= h[1].split("\n");
                int strtSNPs=2;
                
                if (isDate(h2[1])==true || isDate(h2[2])==true)
                {
                    strtSNPs = 3;
                    if (isDate(h2[1])==true) 
                    {
                        indiv = indiv + h2[2] + "|" + h2[1] + "|";
                    }
                    else 
                    {
                        indiv = indiv + h2[1] + "|" + h2[2] + "|";
                    }
                }
                else {indiv = indiv + h2[1] + "||";}  //haplogroup
             
                for (int m=strtSNPs;m<h2.length; m++ )
                { 
                    indiv = indiv + h2[m];
                    if (m<h2.length-1) {indiv = indiv + ", ";}
                    else {indiv = indiv + "\n";}
                }
          
            
            fwv.write(indiv);
            fwv.flush();
            } // next j  indiv within a dataset
            
          
          }
            catch(Exception e) {
                System.out.println(e.getMessage());
            }
          
      } // next i   submission
      
      try
      {
          fwv.flush();
          fwv.close();
      }
      catch (Exception e){}
       

        return "completed";
    }
     
     public static boolean isDate(final String date) {
        boolean result;
        try {
            SimpleDateFormat DATE = new SimpleDateFormat("dd-MMMM-yyyy");
            DATE.parse(date);
            result = true;
        } catch (ParseException e) {
            result = false;
        }
        return result;
    }
     
     public static void compute_hg() 
     {
        FileWriter fwv =null;       
      try{
          fwv = new FileWriter("E:/DAS_Coded_BU_2017/Genealogy/DNA/Ian_Logan_mt_DNA/GFG_mt_hg_snps.csv");
          fwv.write("ct|submitter|accession_number|location|hg|block|lvl|lvl_ok|path|proband_block_snps|ref_snp_ct|matching|snp_ct|missing|ct|unused_snp_ct|intersect|missing|unused|block_snp_ct|block|snps|cum_snps\n");
                }
      catch(Exception e){}


            try{
            String fn = "E:/DAS_Coded_BU_2017/Genealogy/DNA/Ian_Logan_mt_DNA/logan_mt_hg_snps1.csv";
            int ct = 0 ;
            File fnc = new File(fn);

            LineIterator it = FileUtils.lineIterator(fnc, "UTF-8");
            
//            int strt=98000;
//            for (int j=0;j<98000;j++)
//            {
//                ct = ct + 1;
//                it.nextLine() ;  //1st line is hader
//            }
            while (it.hasNext()) {
                String line = it.nextLine();
                
                String c[] = line.split(Pattern.quote("|")); 
                
//                gen.dna.mt_haplogroup_from_SNPs mthg = new gen.dna.mt_haplogroup_from_SNPs();
//                String gfg = mthg.mt_haplogroup_from_snps(c[6]);
                
            String new_snp = "[";
             String ss[] = c[6].split(",");
             if (ss.length>15) 
             {
             for (int i=0; i<ss.length;i++)
             {
                 new_snp = new_snp + "'" + ss[i].strip() + "'";
                 if (i < ss.length-1) {new_snp=new_snp + ",";}
                 else {new_snp=new_snp + "]";}
             }
             
                String gfg = "";
                String cq = 
                        //"with " + new_snp + " as proband_snps match path=(b1:mt_block{name:'RSRS'})-[r:mt_block_child*0..999]->(b2:mt_block) with proband_snps,b2, [x in nodes(path)|case when size(apoc.coll.intersection(x.snps, proband_snps))>0 then '*' else '' end + x.name] as blocks,[y in nodes(path)|id(y)] as op, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten([z in nodes(path) where z.snps is not null|z.snps]))) as snps, [w in nodes(path)|case when size(apoc.coll.intersection(w.snps, proband_snps))>0 then '1' else '0' end] as lvl_ct, apoc.coll.intersection(b2.snps, proband_snps) as proband_block_snps with proband_snps,b2,blocks,snps,size(op) as lvl, lvl_ct,gen.graph.get_ordpath(op) as op,proband_block_snps with b2.name as block,lvl-1 as lvl, size(b2.snps) as ct, lvl_ct, b2.snps as block_snps, blocks,size(snps) as ct2, snps as cum_snps, op, apoc.coll.intersection(snps, proband_snps) as intersect,apoc.coll.subtract(proband_snps,snps ) as unused,apoc.coll.subtract(snps, proband_snps) as missing,proband_block_snps with block,lvl,apoc.coll.occurrences(lvl_ct,'1') as lvl_ok, blocks as path,ct2 as ref_snp_ct, size(intersect) as matching_snp_ct,size(missing) as missing_ct,size(unused) as unused_snp_ct, intersect,missing, unused, ct as block_snp_ct,block_snps,cum_snps,proband_block_snps with block, lvl, lvl_ok, path, ref_snp_ct, matching_snp_ct, missing_ct, unused_snp_ct, intersect, missing, unused, block_snp_ct, block_snps, cum_snps,proband_block_snps return replace(block,'[','') + '|' +  lvl + '|' +  lvl_ok + '|' +  path + '|' +  proband_block_snps + '|' + ref_snp_ct + '|' +  matching_snp_ct + '|' +  missing_ct + '|' +  unused_snp_ct + '|' +  intersect + '|' +  missing + '|' +  unused + '|' +  block_snp_ct + '|' +  block_snps + '|' +  cum_snps order by size(intersect) desc,lvl desc limit 1";
                        "with " + new_snp + " as proband_snps match path=(b1:mt_block{name:'RSRS'})-[r:mt_block_child*0..999]->(b2:mt_block) with proband_snps,b2, [x in nodes(path)|case when size(apoc.coll.intersection(x.snps, proband_snps))>0 then '*' else '' end + x.name] as blocks,[y in nodes(path)|id(y)] as op, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten([z in nodes(path) where z.snps is not null|z.snps]))) as snps, [w in nodes(path)|case when size(apoc.coll.intersection(w.snps, proband_snps))>0 then '1' else '0' end] as lvl_ct, apoc.coll.intersection(b2.snps, proband_snps) as proband_block_snps with proband_snps,b2,blocks,snps,size(op) as lvl, lvl_ct,gen.graph.get_ordpath(op) as op,proband_block_snps with b2.name as block,lvl-1 as lvl, size(b2.snps) as ct, lvl_ct, b2.snps as block_snps, blocks,size(snps) as ct2, snps as cum_snps, op, apoc.coll.intersection(snps, proband_snps) as intersect,apoc.coll.subtract(proband_snps,snps ) as unused,apoc.coll.subtract(snps, proband_snps) as missing,proband_block_snps with block,lvl,apoc.coll.occurrences(lvl_ct,'1') as lvl_ok, blocks as path,ct2 as ref_snp_ct, size(intersect) as matching_snp_ct,size(missing) as missing_ct,size(unused) as unused_snp_ct, intersect,missing, unused, ct as block_snp_ct,block_snps,cum_snps,proband_block_snps with block, lvl, lvl_ok, path, ref_snp_ct, matching_snp_ct, missing_ct, unused_snp_ct, intersect, missing, unused, block_snp_ct, block_snps, cum_snps,proband_block_snps return block, lvl, lvl_ok, path, proband_block_snps,ref_snp_ct, matching_snp_ct, missing_ct, unused_snp_ct, intersect, missing, unused, block_snp_ct, block_snps, cum_snps order by size(intersect) desc,lvl desc limit 1";
                String hg = gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited_str(cq); //.split(Pattern.quote("|"));
                fwv.write(ct + "|" + c[1] + "|"  + c[2] + "|"  + c[3] + "|" + c[4] + "|" + hg );
                
//               
//               if (hg[0].replace("\"","").compareTo("RSRS")!=0)
//                {
//                    System.out.println(ct + "\n" + c[1] + "\n" + c[4] + "\n" + hg[0].replace("\"","") + "\n" + hg[3] + "\n" + c[6] + "\n\n");
//                }
//                
                ct++;
                //if (ct>strt+1000){break;}
                // do something with line
             }
             }  //next report
        } 
        catch(IOException e){}

  
   }
}
   
   
   
       
                     
     

