/**
 * Copyright 2022 
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
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

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
        
        String fn = "mt_sorted_haplotree.csv";
        gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited("MATCH path=(b1:mt_block{name:'RSRS'})-[r:mt_block_child*0..999]->(b2:mt_block) with b2.name as hg,path,[m in nodes(path)|id(m)] as R with hg, R, gen.graph.get_ordpath(R) as op with op, R,hg order by op MATCH p=(b3:mt_block{name:hg})-[r:mt_block_snp]->(v) with op,v, R,hg order by op, v.sort with op,R,hg, collect(v.name) as variants return op, hg as haplogroup,size(R)-1 as haplotree_level ,size(variants) as var_ct,variants", fn);
        
         
        String fs[] = gen.neo4jlib.file_lib.readFileByLine(gen.neo4jlib.neo4j_info.Import_Dir + fn).split("\n");
        //set up iteration array
        String sa[][] = new String[fs.length][4] ;
       
        for (int i=0;i<fs.length; i++)
        {
           String ss[]=fs[i].split(Pattern.quote("|"));
           sa[i][0]=ss[0];
           sa[i][1]=ss[1];
           sa[i][2]=ss[4].replace("[","").replace("]","").replace("\"", "").replace(",",";");
        }
        
        File fnc = new File(gen.neo4jlib.neo4j_info.Import_Dir + "mt_snp_processed.csv");
        FileWriter fw = null;
        try{
            fw = new FileWriter(fnc, Charset.forName("UTF8"));
            fw.write("block|all_snps\n");
        }
        catch(Exception e){}
 
        
        //iterate thru array
        // i loop is the parent
        //j loop is descendants to whom the snp are added
        for (int i=0; i<sa.length; i++){
            for (int j=i;j<sa.length; j++)
            {
                if(sa[j][0]!=sa[j][0].replace(sa[i][0], ""))
                        { //sa[j][0] is descendant of sa[i][0] because it contains ordpath string of sa[i][0]
                             sa[j][2] = add_snps(sa[j][2], sa[i][2]);
                            System.out.println(i + "    " + sa[j][1] + "\t" +  sa[j][2] + "\t"  + sa[i][2] + "\n");
                            try{
                            fw.write(sa[j][1] + "|" + sa[j][2] + "|\n");
                        }
                            catch (Exception e){}
                            
                       }
                else{break;}
            }
        }
        
        try{
            fw.flush();
            fw.close();
        }
        catch(Exception e){}
        
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///mt_snp_processed.csv' as line FIELDTERMINATOR '|' match (m:mt_block) where  m.name=toString(line.block) set m.all_snps=toString(line.all_snps)");
        
        return "completed";
    }
     
     public static String add_snps(String s1, String s2)
     {
         String s1s[]=s1.split(";");
         String s2s[]=s2.split(";");
         
         for (int i=0;i<s2s.length;i++)
         {
             Boolean b = false;
             for (int j=0;j<s1s.length; j++)
             {
                 if(s2s[i].strip().equals(s1s[j].strip())){
                    b = true;
                    break;
                 }
             }
                 if (b.equals(true)){} 
                 else {
                     s1 = s1 + ";" + s2s[i];
                 }
             }

                          
         return s1;
     }
}
