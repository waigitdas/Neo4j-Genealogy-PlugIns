/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.mt_research;

import java.io.File;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author david
 */
public class mt_mutation_probes {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        
        try{
            gen.neo4jlib.neo4j_qry.CreateIndex("probe", "partition_id");
            gen.neo4jlib.neo4j_qry.CreateIndex("probe", "pos");
            gen.neo4jlib.neo4j_qry.CreateIndex("probe","mutation");
            gen.neo4jlib.neo4j_qry.CreateIndex("probe", "probe");
        }
        catch(Exception e){}
        
        String fn = "probes.csv";
        File f = new File(gen.neo4jlib.neo4j_info.Import_Dir + fn);
        FileWriter fw=null;
        try
        {
            fw = new FileWriter(f);
            fw.write("partition_id|mutation|mut2|pos|change|probe\n");
            
        }
        catch(Exception e){}
        
        
        String refSeq = gen.neo4jlib.neo4j_qry.qry_to_csv("MATCH p=(k:seq_kit{name:'NC_012920'})-[r:kit_tile]->(t:tile) with t order by t.partition_id with reduce(s='', x in collect(t)|s + x.seq) as rCRS return rCRS").split("\n")[0];
                //gen.neo4jlib.file_lib.ReadFileByLineWithEncoding("E:/DAS_Coded_BU_2017/Genealogy/DNA/mt_haplotree_project/mt_DNA_sequences/human_mt_DNA_reference_sequence/NC_012920.1");
                
        
        String c[] = gen.neo4jlib.neo4j_qry.qry_to_csv("MATCH (v:mt_variant) where v.ftdna=1 or v.phylotree=1 RETURN v.name as mutation order by mutation").split("\n");
        for (int i=0; i<c.length; i++)
        {
        int pos[] = getPos(c[i]);
        String mm="";
          try
        {
            mm = c[i].split(Pattern.quote("."))[0];
        }
        catch(Exception e){}
         String delimiter = ",";
         int flank=10;
         String flankLeft = "";
         String flankRight = "" ;
         int rs = 0; 
         int partition_id = 0 ;
        
         //simple mutations
         if (c[i].contains(".")==false && c[i].contains(".")==false && c[i].contains("d")==false && c[i].contains(")")==false && c[i].compareTo(c[i].toUpperCase())==0)
//             if (c[i].compareTo("A73C")==0)
//             {
//                 int df=0;
//             }
         {
         String probe ="";
         String mut = "";
         rs = c[i].length()-2;
         try{
            flankLeft =  refSeq.substring(pos[0] - flank,pos[0]);
            flankRight = refSeq.substring(pos[0] + 1,pos[0] + flank); //pos[0] is mutation point
            probe = flankLeft + c[i].substring(rs).replace("\"","") + flankRight;
                     //flankLeft + "*" + c[i].substring(rs).replace("\"","") + "*" + flankRight;
            partition_id = (pos[0]/100) + 1 ;  
        mut = refSeq.substring(pos[0],pos[0]+1) + "->"  + c[i].substring(rs).replace("\"","");
        fw.write(partition_id + delimiter + c[i].replace("\"", "") + delimiter + mm.replace("\"", "") + delimiter + pos[0] + delimiter + mut + delimiter + probe.replace("\"", "") + "\n");
         }
         
         catch(Exception e){}
         } // end simple mutation
        } // next mutation
        
        try{
            fw.flush();
            fw.close();
                    }
        catch(Exception e){}
        
        
//        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///probes.csv' AS line FIELDTERMINATOR '|'  merge(p:probe{partition_id:toInteger(line.partition_id),pos:toInteger(line.pos),variant:toString(line.mutation),probe:toString(line.probe)})");
        
    }
    
    public static int[] getPos(String mutation)
    {
        Pattern p = Pattern.compile("\\d+");
        
        String mm = mutation;
        try
        {
            mm = mutation.split(Pattern.quote("."))[0];
        }
        catch(Exception e){}
                
        
        Matcher m = p.matcher(mm);
        int n[] = new int[5];
        int ct =0;
        while(m.find()) {
            n[ct] = Integer.valueOf(m.group());
            
        }
        return n;
    }
    
}
