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


public class create_mss {
    @UserFunction
    @Description("creates monophylytic segment sets.")

    public String create_monophylytic_segment_sets(

    )
   
         { 
             
        String s =create_mss();
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String create_mss()
    {   String cq="";
        
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        gen.neo4jlib.neo4j_qry.qry_write("match (s:MSS)-[r]-() delete r");
       gen.neo4jlib.neo4j_qry.qry_write("match (s:MSS) delete s");
       try{
           gen.neo4jlib.neo4j_qry.CreateIndex("MSS", "fullname");
           gen.neo4jlib.neo4j_qry.CreateIndex("MSS", "mrca");
           gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("mss_seg", "mrca");
       }
       catch (Exception e) {}
        gen.neo4jlib.neo4j_qry.qry_write("match (m1:DNA_Match)-[rs:match_segment]->(s:Segment) where rs.cm>=7 and rs.snp_ct>=500 and rs.p_rn is not null and rs.m_rn is not null with m1,rs,s , gen.rel.mrca_rn_from_cypher_list([rs.p_rn,rs.m_rn],15) as mrcas unwind mrcas as mrca with s,mrca  merge (sn:MSS{mrca:mrca}) with distinct s,mrca,sn merge (sn)-[r:ms_seg{mrca:mrca}]-(s)");
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (m:MSS)-[r:ms_seg]->() with distinct m,m.mrca as rn match (p:Person{RN:rn}) set m.fullname=p.fullname");
        return "monophylytic segment sets create.";
    }
}
