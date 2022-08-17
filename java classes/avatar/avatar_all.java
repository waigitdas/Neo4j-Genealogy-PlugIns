/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.avatar;

import gen.neo4jlib.neo4j_qry;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class avatar_all {
    @UserFunction
    @Description("Creates DNA profiles")

    public String dna_virtual_all_matches(
            @Name("rn") 
            Long rn
 
  )
   
         { 
             
        create_virtuals(rn);
         return "";
            }

    
    
    public static void main(String args[]) {
        create_virtuals(1L);
    }
    
     public static String create_virtuals(Long rn) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
       String fn = "virtual.csv";
       
       File fnc = new File(gen.neo4jlib.neo4j_info.Import_Dir + fn );
       
        FileWriter fw = null;
        try{
            fnc.delete();
            fw = new FileWriter(fnc,true);
            fw.write("source|segment|side|target|target_rn|match|match_rn|cor|cm|snp_ct|pair_mrca|mrca_rn\n");
        }
        catch(Exception e){}
        String csv = "";
        
        try{  //avoid error if indices already created
        gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("avatar_segment", "source");
        gen.neo4jlib.neo4j_qry.CreateIndex("Avatar", "fullname");
        gen.neo4jlib.neo4j_qry.CreateIndex("Avatar", "RN");
        } 
        catch(Exception e){}
        
         gen.neo4jlib.neo4j_qry.qry_write("match ()-[r:avatar_segment]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (d:Avatar) delete d");
        //MATCH p=()-[r:avatar_segment]->(s:Segment) DETACH DELETE  s       

//        String cq = "match (g:Person) where g.kit is not null with collect (g.RN) as rns  return rns";
//         String ls[] = gen.neo4jlib.neo4j_qry.qry_str(cq).replace("[[","").replace("]]","").split(",");
//         
//         for (int i=0; i<ls.length;i++)
//         {
            String cq = "with " + rn + " as rn CALL { with rn MATCH p=()-[r:match_segment]->(s:Segment) where (r.m_rn=rn and size(r.m_side) is not null) RETURN s,r, r.m as target,r.m_rn as target_rn,r.p as match,r.p_rn as match_rn,r.m_side as side union with rn MATCH p=()-[r:match_segment]->(s:Segment) where (r.p_rn=rn and size(r.p_side) is not null) RETURN s,r, r.p as target,r.p_rn as target_rn,r.m as match,r.m_rn as match_rn, r.p_side as side } with r,s,target,target_rn,match,match_rn,s.Indx as segment,case when side='U' then 'unknown' when side='F' then 'paternal' else 'maternal' end as side,  r.cor as cor, r.cm as cm,r.snp_ct as snps,r.pair_mrca as pair_mrca,r.mrca_rn as mrca_rn return distinct target_rn as source, segment, side,target,target_rn, match,match_rn,cor, cm,snps,pair_mrca,mrca_rn order by segment,side";

              csv = gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited_str(cq);
              try{
                  fw.write(csv);
                  fw.flush();
              }
              catch(Exception e){}
              int dfg=99;
//         }
        
         try{fw.close();} 
         catch(Exception e){}
        
        
        
        String lc = "LOAD CSV WITH HEADERS FROM 'file:///" + fn + "' as line FIELDTERMINATOR '|' return line ";

        gen.neo4jlib.neo4j_qry.qry_write("match (g:Person) where g.kit is not null merge (d:Avatar{RN:g.RN,fullname:g.fullname})");

        cq = "match(d:Avatar{RN:toInteger(line.target_rn)}) match (s:Segment{Indx:toString(line.segment)}) merge(d)-[r:avatar_segment{source:toInteger(line.source), side:toString(line.side),p:toString(line.target),p_rn:toInteger(line.target_rn),m:toString(line.match),m_rn:toInteger(line.match_rn),cm:toFloat(line.cm),snp_ct:toInteger(line.snp_ct),pair_mrca:toString(line.pair_mrca),mrca_rn:toString(line.mrca_rn)}]->(s)";
        gen.neo4jlib.neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);

    
    //initialize gen
    cq = "MATCH (n:Avatar) set n.gen=0";
    gen.neo4jlib.neo4j_qry.qry_write(cq);
    int gnew = 0;
    
//completed loading tester Avatar nodes and their avatar_segment relationships


    for (int g=0; g<10; g++) //iterate 1o generations
    {
        //////////////////////////////////////////////////////////////////////
        // method 1 add parental segments to mother and father virtuals
        /////////////////////////////////////////////////////////////////////
        //set father and mother in child Avatar node
        cq = "MATCH (d:Avatar{gen:" +  g + "}) with d match (p:Person{RN:d.RN})-[r:father]->(f:Person)  set d.father=f.RN"; 
        gen.neo4jlib.neo4j_qry.qry_write(cq);
        cq = "MATCH (d:Avatar{gen:" +  g + "}) with d match (p:Person{RN:d.RN})-[r:mother]->(m:Person)  set d.mother=m.RN"; 
        gen.neo4jlib.neo4j_qry.qry_write(cq);
        
        gnew = g + 1;
        
        //avatar_segment relationship for parents
        cq = "match (d:Avatar{gen:" + g + "}) with d match (p:Person{RN:d.mother}) merge (d2:Avatar{RN:d.mother,fullname:p.fullname})";
        gen.neo4jlib.neo4j_qry.qry_write(cq);
 
        cq = "MATCH p=(d:Avatar{gen:" + g + "})-[r:avatar_segment{side:'maternal'}]->(s) with d,s,r match (dn:Avatar{RN:d.mother}) with d,s,r,dn merge (dn)-[rn:avatar_segment{RN:dn.RN,child:d.RN,p:r.p,m:r.m,p_rn:r.p_rn,m_rn:r.m_rn, cm:r.cm, snp_ct:r.snp_ct, pair_mrca:replace(replace(r.pair_mrca,'â¦','⦋'),'â¦','⦌')mrca_rn:r.mrca_rn}]-(s)";
        gen.neo4jlib.neo4j_qry.qry_write(cq);

        
        cq = "match (d:Avatar{gen:" + g + "}) with d match (p:Person{RN:d.father}) merge (d2:Avatar{RN:d.father,fullname:p.fullname})";
        gen.neo4jlib.neo4j_qry.qry_write(cq);
       
        cq = "MATCH p=(d:Avatar{gen:" + g + "})-[r:avatar_segment{side:'paternal'}]->(s) with d,s,r match (dn:Avatar{RN:d.father}) with d,s,r,dn merge (dn)-[rn:avatar_segment{RN:dn.RN,child:d.RN,p:r.p,m:r.m,p_rn:r.p_rn,m_rn:r.m_rn, cm:r.cm, snp_ct:r.snp_ct, pair_mrca:replace(replace(r.pair_mrca,'â¦','⦋'),'â¦','⦌'),mrca_rn:r.mrca_rn}]-(s)";
        gen.neo4jlib.neo4j_qry.qry_write(cq);

        //////////////////////////////////////////////////////////////////////
        // method 2 add segments from non-child pairing to common ancestor
        /////////////////////////////////////////////////////////////////////

        
      gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(d:Avatar) where d.gen is null set d.gen=" + gnew);
                }
  
        int gh=879;
        return "completed";
    }
}
