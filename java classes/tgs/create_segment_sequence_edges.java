/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;

import gen.neo4jlib.neo4j_info;
import gen.neo4jlib.neo4j_qry;
import java.io.FileWriter;
import java.util.regex.Pattern;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

public class create_segment_sequence_edges {
    @UserFunction
    @Description("Will not run on its own. Access this by running gen.tgs.setup_tg_environmen which uses it. Creates edge between segments in the order their are arranged on the chromosome. The segments are those of a triangulation group including DNA testers descended from the common ancestor. This is used in visualizations of triangulation groups.")

    public  String create_seg_seq_edges(
        @Name("ancestor_rn") 
            Long ancestor_rn
//        @Name("rn2") 
//            Long rn2
  )
   
         { 
             
        String s = create_seg_seq(ancestor_rn);
         return s;
            }

    
    
    public static void main(String args[]) {
        create_seg_seq(Long.valueOf(33454));
    }
    
     public static  String create_seg_seq(Long ancestor_rn) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        //delete prior seg_seq edges
        String cq = "MATCH ()-[r:seg_seq]-() delete r";
        neo4j_qry.qry_write(cq);
        gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("seg_seq", "ancestor_rn");
        
        //iterate through autosomal chrmosomes
        for (int crsm=1;crsm<22; crsm++){
            String chr = String.valueOf(crsm);
            if (chr.length()==1){chr = "0" + chr; }
            
            //create csv file of sequences    
            String fn =  "tg_discovery.csv";
            cq="match p1=(t:tg)-[:tg_seg]-(s:Segment{chr:'" + chr + "'})-[r:match_segment]-(m1:DNA_Match) where r.cm>=7 and r.snp_ct>=500 and r.p_rn is not null and r.m_anc_rn=" + ancestor_rn + " with  s,t, count(*) as ct return t.tgid as tgid,s.Indx as Indx,ct order by t.tgid,s.chr,s.strt_pos,s.end_pos";
            neo4j_qry.qry_to_pipe_delimited(cq,fn);

            //read csv, parse and create new csv suitable for Neo4j importing
            String c = gen.neo4jlib.file_lib.readFileByLine(neo4j_info.Import_Dir +  fn);
            String[] cc = c.split("\n");
            FileWriter fwp;
            String fns =  "seg_seqcsv";  //_" + chr + ".
          try{
               String tmp[]=cc[1].split(Pattern.quote("|"));
                String curr_tg = tmp[0];
                String tg =curr_tg;
                Boolean b=false;
                int rw =1; //first row is header'

                fwp = new FileWriter(neo4j_info.Import_Dir + fns);
                fwp.write("tgid|from|to\n");
                    for (int i=1; i<cc.length-1;i++){    //next row
                        String ccc[] = cc[i].split(Pattern.quote("|"));  //0 is tgid; 1 segment
                        String ccc1[] = cc[i+1].split(Pattern.quote("|"));  //0 is tgid; 1 segment
                       //separate seg_seq for each tg
                        if (curr_tg.equals(ccc[0])){ }
                        else{  
                            fwp.flush();
                            fwp.close();

                            //load seg_seq edges into Neo4j
                            cq = "LOAD CSV WITH HEADERS FROM 'file:///" + fns + "' AS line FIELDTERMINATOR '|' match (s1:Segment{Indx:toString(line.from)}) match (s2:Segment{Indx:toString(line.to)}) merge (s1)-[r:seg_seq{tgid:toInteger(line.tgid),ancestor_rn:" + ancestor_rn + "}]-(s2) ";
                            neo4j_qry.qry_write(cq);


                            fwp = new FileWriter(neo4j_info.Import_Dir + fns);
                            fwp.write("tgid|from|to\n");

                            //reset curr_tg
                            curr_tg = ccc[0];    
                            
                        }
                        
                            fwp.write(ccc[0] + "|" + ccc[1] + "|" + ccc1[1] + "\n");
                
                }  //row
                    // last tgid on chr
                            fwp.flush();
                            fwp.close();

                            //load seg_seq edges into Neo4j
                            cq = "LOAD CSV WITH HEADERS FROM 'file:///" + fns + "' AS line FIELDTERMINATOR '|' match (s1:Segment{Indx:toString(line.from)}) match (s2:Segment{Indx:toString(line.to)}) merge (s1)-[r:seg_seq{tgid:toInteger(line.tgid),ancestor_rn:" + ancestor_rn + "}]-(s2) ";
                            neo4j_qry.qry_write(cq);

            }

           catch (Exception e) {
               try {
                    FileWriter fwe = new FileWriter(neo4j_info.Import_Dir + "error_" + chr + ".csv");
 
                    fwe.write("Error\n" + e.getMessage());
                    fwe.flush();
                            fwe.close();
                       }
               catch (Exception f) {}
               finally{}
      
    
        //return "completed";
    }
        }
    
        return "COMPLETED";
    }
}

