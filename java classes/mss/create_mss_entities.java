/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.mss;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class create_mss_entities {
    @UserFunction
    @Description("creates monophylytic segment sets for entire database.")

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
    {   
        String cq="";
//        gen.rel.anc_rn arn = new gen.rel.anc_rn();
//        Long anc_rn = arn.get_ancestor_rn();
        
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        gen.neo4jlib.neo4j_qry.qry_write("match (s:MSS)-[r]-() delete r");
       gen.neo4jlib.neo4j_qry.qry_write("match (s:MSS) delete s");
       try{
           gen.neo4jlib.neo4j_qry.CreateIndex("MSS", "fullname");
           gen.neo4jlib.neo4j_qry.CreateIndex("MSS", "mrca");
           gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("ms_seg", "mrca");
       }
       catch (Exception e) {}
        
       //identify MSS
       gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited("match (m1:DNA_Match)-[rs:match_segment]->(s:Segment) where rs.cm>=7 and rs.snp_ct>=500 and rs.p_rn is not null and rs.m_rn is not null with m1,rs,s , gen.rel.mrca_rn_from_cypher_list([rs.p_rn,rs.m_rn],25) as mrcas unwind mrcas as mrca with distinct mrca,collect(distinct rs) as crs match (p:Person{RN:mrca}) return mrca as mrca,p.fullname as fullname,p.RN as RN,size(crs) as segs", "mss.csv");

       
       //create MSS nodes
       gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///mss.csv' as line FIELDTERMINATOR '|' create (mss:MSS{mrca:toInteger(line.mrca), fullname:toString(line.fullname),RN:toInteger(line.RN), seg_ct:toInteger(line.segs)})");

       //create mss_seg and person_mss edges
       gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited("match (m1:DNA_Match)-[rs:match_segment]->(s:Segment) where rs.cm>=7 and rs.snp_ct>=500 and rs.p_rn is not null and rs.m_rn is not null with m1,rs,s , gen.rel.mrca_rn_from_cypher_list([rs.p_rn,rs.m_rn],15) as mrcas unwind mrcas as mrca  return  distinct mrca, s.Indx as seg", "mss_segs.csv");
       gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///mss_segs.csv' as line FIELDTERMINATOR '|' match(mss:MSS{mrca:toInteger(line.mrca)}) match (s:Segment{Indx:toString(line.seg)}) merge (mss)-[r:ms_seg{mrca:toInteger(line.mrca)}]-(s)");

       gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///mss.csv' as line FIELDTERMINATOR '|' match (mss:MSS{mrca:toInteger(line.mrca)}) match (p:Person{RN:toInteger(line.mrca)}) merge (p)-[r:person_mss]-(mss)");

      gen.neo4jlib.neo4j_qry.qry_write("MATCH path=(d:DNA_Match)<-[rm:Gedcom_DNA]-(p:Person)-[r:person_mss]->(mss:MSS) merge (d)-[rnew:match_mss]-(mss)");

//add mss property to Segment nodes
      gen.neo4jlib.neo4j_qry.qry_write("MATCH p=()-[r:ms_seg]->(s:Segment) set s.mss='Y'");
      
//       //write phase_gen property to MSS node
//       gen.neo4jlib.neo4j_qry.qry_write("match path=(p1:Person{RN:" + anc_rn + "})<-[rf:father|mother*0..20]-(p2:Person{RN:m.mrca})-[rm:person_mss]-(m:MSS) with m,[rn in nodes(path)|rn.RN] AS op,length(path) as gen with m,gen,apoc.coll.remove(op,gen) as op set m.phased_gen=gen");
       
       return "monophylytic segment sets created.";
    }
}
