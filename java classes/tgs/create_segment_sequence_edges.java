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
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 *
 * @author david
 */
public class create_segment_sequence_edges {
    @UserFunction
    @Description("Creates edge between segments in the order their are arranged on the chromosome. The segments are those of a triangulation group including DNA testers descended from the common ancestor. This is used in visualizations of triangulation groups.")

    public static String create_seg_seq_edges(
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
        // TODO code application logic here
    }
    
     public static String create_seg_seq(Long ancestor_rn) 
    {
        //delete prior seg_seq edges
        String cq = "MATCH ()-[r:seg_seq]-() delete r";
        neo4j_qry.qry_write(cq);

        //iterate through autosomal chrmosomes
        for (int crsm=0;crsm<22; crsm++){
            String chr = String.valueOf(crsm);
            if (chr.length()==1){chr = "0" + chr; }
            
        //create csv file of sequences    
        String fn =  "tg_discovery.csv";
        cq = "match (m:DNA_Match{ancestor_rn:" + ancestor_rn + "})-[r:match_segment]-(s:Segment) where 50>=r.cm>=7 and r.snp_ct>=500 and s.chr='" + chr + "' with m,s order by s.chr,s.strt_pos,s.end_pos with s.Indx as i,s.chr as c,s.strt_pos as s,s.end_pos as e, count(*) as ct,sum(case when m.ancestor_rn=33454 then 1.0 else 0.0 end) as branch_ct with i,c,s,e,ct,branch_ct, e-s as diff return i as Indx order by c,s,e";
        neo4j_qry.qry_to_pipe_delimited(cq,fn);
        
        //read csv, parse and create new csv suitable for Neo4j importing
        String c = gen.neo4jlib.file_lib.readFileByLine(neo4j_info.Import_Dir +  fn);
        String[] cc = c.split("\n");
       try{
            FileWriter fwp = new FileWriter(neo4j_info.Import_Dir + "seg_seq.csv");
            fwp.write("from|to\n");
            int sct = 0;
            for (int i=1; i<cc.length-1;i++){
                sct = sct + 1;
                fwp.write(cc[i] + "|" + cc[i+1] + "\n");

            }
            fwp.flush();
            fwp.close();
           
            //load seg_seq edges into Neo4j
            cq = "LOAD CSV WITH HEADERS FROM 'file:///seg_seq.csv' AS line FIELDTERMINATOR '|' match (s1:Segment{Indx:toString(line.from)}) match (s2:Segment{Indx:toString(line.to)}) merge (s1)-[r:seg_seq]-(s2) ";
      neo4j_qry.qry_write(cq);
            
        }
    
       catch (Exception e) {}
       
        //System.out.println(c);
  
    }   
    
        return "completed";
    }
}
