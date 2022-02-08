/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.load;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

public class load_ftdna_enhancements {
    @UserFunction
    @Description("Adds match_segment properties")

    public String enhance_match_segment_edge(
  
    )
   
         { 
             
        String s = add_match_segment_properties();
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String add_match_segment_properties()
    {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
  

        neo4j_qry.qry_write("match ()-[r:match_by_segment]-() remove r.x_gen_dist");
        
        //set x_gen_dist property
        neo4j_qry.qry_to_pipe_delimited("MATCH p=(m1:DNA_Match)-[r:match_by_segment]->(m2:DNA_Match) where r.x_cm>0 and m1.RN>0 and m2.RN >0 with r,gen.dna.x_chr_min_genetic_distance(m1.RN,m2.RN) as x_gen_dist return id(r) as r,x_gen_dist","x_gen_dist.csv");
        neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///x_gen_dist.csv' as line FIELDTERMINATOR '|' MATCH p=(m1:DNA_Match)-[r:match_by_segment]->(m2:DNA_Match) where id(r)=toInteger(line.r) set r.x_gen_dist=toInteger(line.x_gen_dist)");  
       
//        neo4j_qry.qry_write("match ()-[r:match_by_segment]-() remove r.rel");

//        neo4j_qry.qry_write("match ()-[r:match_segment]-() remove r.p_rn");
//        neo4j_qry.qry_write("MATCH (m1:DNA_Match)-[r:match_segment]->() where r.cm>=7 and r.snp_ct>=500 and m1.fullname=r.p and m1.RN is not null set r.p_rn=m1.RN");
// 
//        neo4j_qry.qry_write("match ()-[r:match_segment]-() remove r.m_rn");
//        neo4j_qry.qry_write("MATCH (m1:DNA_Match)-[r:match_segment]->() where r.cm>=7 and r.snp_ct>=500 and r.m is not null  match (m2:DNA_Match) where m2.fullname=r.m and m2.RN is not null set r.m_rn=m2.RN");
//       
// 
 
        return "completed";
    }
}
