/**
 * Copyright 2022-2023 
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
  

        //set x_gen_dist property
        gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(m1:DNA_Match)-[r:match_by_segment]->(m2:DNA_Match) where r.x_cm>0 and m1.RN>0 and m2.RN >0 \n" +
"set r.x_gen_dist= gen.dna.x_chr_min_genetic_distance(m1.RN,m2.RN)");
        
        gen.neo4jlib.neo4j_qry.qry_write("MATCH p=()-[r:match_segment]->(s) where r.p_rn is not null and r.m_rn is not null and s.chr='0X' with p,r, gen.dna.x_chr_min_genetic_distance(r.p_rn,r.m_rn) as x_gen_dist set r.x_gen_dist=x_gen_dist");
        
// try{
//        neo4j_qry.qry_to_pipe_delimited("MATCH p=(m1:DNA_Match)-[r:match_by_segment]->(m2:DNA_Match) where r.x_cm>0 and m1.RN>0 and m2.RN >0 with r,gen.dna.x_chr_min_genetic_distance(m1.RN,m2.RN) as x_gen_dist return id(r) as r,x_gen_dist","x_gen_dist.csv");
// 
//        neo4j_qry.qry_write("match ()-[r:match_by_segment]-() remove r.x_gen_dist");
//        
//        neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///x_gen_dist.csv' as line FIELDTERMINATOR '|' MATCH p=()-[r:match_by_segment]-() where id(r)=toInteger(line.r) set r.x_gen_dist=toInteger(line.x_gen_dist)");  
// }
// 
// catch(Exception e){}
// 
//    gen.neo4jlib.neo4j_qry.qry_write("MATCH p=()-[r:match_segment]->(s) where r.p_rn is not null and r.m_rn is not null and s.chr='0X' with p,r, gen.dna.x_chr_min_genetic_distance(r.p_rn,r.m_rn) as x_gen_dist set r.x_gen_dist=x_gen_dist");
 
 return "completed";
    }
}