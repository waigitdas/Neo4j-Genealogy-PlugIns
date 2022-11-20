/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.regex.Pattern;
import org.javatuples.Pair;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
import java.util.Map;
import java.util.TreeMap;
import java.lang.Integer;
import static java.lang.Integer.parseInt;

public class process_mt_haplotree{
    @UserFunction
    @Description("process mt-DNA file from FTDNA")

    public String mt_haplotree_cumulative_snps(
        
  )
   
         { 
             
        String s = process_file();
         return s;
            }

    
    
    public static void main(String args[]) {
        process_file();
        
    }
    
     public static String process_file() 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
     
        gen.neo4jlib.neo4j_qry.qry_write("match (m:mt_block) remove m.all_snps");
        gen.neo4jlib.neo4j_qry.qry_write("match (m:mt_block) remove m.all_pos");
        
        String fn = "mt_sorted_haplotree.csv";
        gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited("MATCH path=(b1:mt_block{name:'RSRS'})-[r:mt_block_child*0..999]->(b2:mt_block) with b2.name as hg,path,[m in nodes(path)|id(m)] as R with hg, R, gen.graph.get_ordpath(R) as op with op, R,hg order by op MATCH p=(b3:mt_block{name:hg})-[r:mt_block_snp]->(v) with op,v, R,hg,v.sort as vsort order by op, v.sort with op,R,hg, collect(v.name) as variants,collect(vsort) as vsort return DISTINCT op, hg as haplogroup,size(R)-1 as haplotree_level ,size(variants) as var_ct,variants,vsort order by op", fn);
        
         
        String fs[] = gen.neo4jlib.file_lib.readFileByLine(gen.neo4jlib.neo4j_info.Import_Dir + fn).split("\n");
        //set up iteration array
        String op[] = new String[fs.length];
        String bn[] = new String[fs.length];
        String pos[] = new String[fs.length];
        String snp[] = new String[fs.length];
        String pos_cum[] = new String[fs.length];
        String snp_cum[] = new String[fs.length];
        String lvl[] = new String[fs.length] ;
        Boolean bop=false;
        int lvl_par=0;
        int lvl_child=0;
        Pair<String,String> ps = new Pair<String,String>("","");
        
        for (int i=1;i<fs.length; i++)
        {
           String ss[]=fs[i].split(Pattern.quote("|"));
           op[i] = ss[0];
           bn[i] =ss[1];
           pos[i] = ss[5].replace("[","").replace("]","").replace("\"", "").replace(",",";");
           snp[i] = ss[4].replace("[","").replace("]","").replace("\"", "").replace(",",";");
           lvl[i] =  ss[2];
        }
        
        //initialize row 1
        pos_cum[1]=pos[1];  //.replace(";",",");
        snp_cum[1]=snp[1];  //.replace(";",",");

        File fnc = new File(gen.neo4jlib.neo4j_info.Import_Dir + "mt_snp_processed.csv");
        FileWriter fw = null;
        try{
            fw = new FileWriter(fnc, Charset.forName("UTF8"));
            fw.write("block|all_pos|all_snps\n");
          fw.write(bn[1] + "|" + pos_cum[1] + "|" + snp_cum[1] + "|\n");
          fw.flush();
        }
        catch(Exception e){}
 
        //our goal is to get a full list of snp at a branch in the ht-haplotree
        //these are cumulative, with each branch requiring the snp from its parent
        //iterate thru array
        // i loop is the parent
        //j loop is descendants to whom the snp and pos lists are added
        //int lvlct=1;
        int ct = 0;
        for (int lv=1; lv<20; lv++) {
        for (int i=1; i<snp.length-1; i++){
            for (int j=i+1;j<snp.length; j++)
            {
//                int ii = Long.compare(lvl[i],lvl[j]);
                lvl_par =Integer.parseInt(lvl[i]);
                lvl_child = Integer.parseInt(lvl[j]);
                //Boolean opb =  op[j].strip() != op[j].replace(op[i],"").strip();
                //System.out.println( ct + "\t" +  i + "\t" +  j + "\t" + lvl_par + "\t"  + lvl_child + "\t" + opb + "\t" + op[i].getClass().getCanonicalName());
                //lvl[i] + "\t" + lvl[j] + "\t" + Integer.parseInt(lvl[i])==lvlct-1 +  "\t" );
                if( lvl_par==lv && lvl_par<=lvl_child)
                   
                        { 
                        bop =  op[j].strip() != op[j].replace(op[i],"").strip();
                        if(bop=true){
                        
                           
                            //op[j] is descendant of op[i] because it contain.strip()s ordpath string of op[i]
                            //add snp and pos using function which prevents duplicates
                            ps = add_snps(snp[j],snp_cum[i],pos[j],pos_cum[i]);

                            //assign tuple values to respective list
                            pos_cum[j] = ps.getValue0();
                            snp_cum[j] = ps.getValue1();

 
                            //write results to csv which will be imported to the graph
                            //System.out.println(i + "    " + bn[j] + "\t" +  ps2.getValue0() + "\t"  + ps2.getValue1()+ "\n");
                       
                       }  //ordpath loop; exits loop when ordpath root changes
                } //lvl 
            } //end of child node loop
            //lvlct = lvlct + 1;
//            int iop=0;
           
        }  //end parent node loop
            ct=0;
            System.out.println(lv);
        }   //next lvl
       
        try{
            for (int i=1; i<fs.length; i++)
            {
                //sort cumulative arrays
                Pair<String,String> ps2 = sort_array(pos_cum[i],snp_cum[i]);
                fw.write(bn[i] + "|" + pos_cum[i] + "|" + snp_cum[i] + "|\n");
            }
            fw.flush();
            fw.close();
        }
        catch(Exception e){
        int sd=0;
        }

                                        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///mt_snp_processed.csv' as line FIELDTERMINATOR '|' match (m:mt_block) where  m.name=toString(line.block) set m.all_snps=toString(line.all_snps),  m.all_pos=toString(line.all_pos)");

                                return "completed";
   
    }  //function end
     
     public static Pair<String,String> add_snps(String snp_ch, String snp_par, String pos_ch, String pos_par)
     {
         //checks to see if snp already in the snp list. If not, it adds it
         //avoids duplicate entries in the snp and pos lists
         String snp_ch_s[]=snp_ch.split(";");
         String snp_par_s[]=snp_par.split(";");
         String pos_ch_s[]=pos_ch.split(";");
         String pos_par_s[]=pos_par.split(";");
         int i = 0;
         int j = 0 ;
         for (i=0;i<snp_par_s.length;i++)
         {
             Boolean b = false;
             for (j=0;j<snp_ch_s.length; j++)
             {
                 if(snp_par_s[i].strip().equals(snp_ch_s[j].strip())){
                    b = true;
                    break;
                 }
             }
                 if (b.equals(true)){} 
                 else {
                    try{
                     pos_ch = pos_ch + ";" + pos_par_s[i];
                     snp_ch = snp_ch + ";" + snp_par_s[i];
                    }
                    catch(Exception e)
                    {
                        int fg =0;
                    }
                 }
             }

         Pair<String,String>  ps = new Pair<String,String>(pos_ch,snp_ch);                      
         return ps;
     }
     
         public static Pair<String,String> sort_array(String pos_list,String snp_list)
     {
         //uses position and snp names to create a TreeMap
         //this sorts both the pos and snp name by the position 
         //provides consistant pattern in the displys but also for sunsequent analytics
         String pos[] = pos_list.split(";");
         String snp[] = snp_list.split(";");
         
         //map with pos, snp name; TreeMap sorts by pos (1st term) as integer
         Map<Integer,String> aMap = new TreeMap<>();
         
         for (int i=0;i<pos.length;i++)
         {
             try{
             aMap.put(Integer.parseInt(pos[i]),snp[i] );  //sorted
             }
             catch(Exception e)
             {
                 //catches header rows with text or row that are blank and skips over them
                 int ffg=0;
             }
         }
         
         String pos_out = "";
         String snp_out = "";
         int ct = 0;
           for (Map.Entry<Integer, String> set : aMap.entrySet()) {
 
            // Printing all elements of a Map
            //System.out.println(set.getKey() + " = " + set.getValue());
            snp_out = snp_out + set.getValue();
            pos_out = pos_out + String.valueOf(set.getKey());
            ct = ct + 1;
            if (ct<pos.length){
                snp_out = snp_out + ";";
                pos_out = pos_out + ";";
            }
        }
         
         Pair<String,String> p = new Pair(pos_out,snp_out);
         //System.out.println("\n*" + p.getValue0() + " --- " + p.getValue1() + "*\n");
         return p;

     }
         
   
}
