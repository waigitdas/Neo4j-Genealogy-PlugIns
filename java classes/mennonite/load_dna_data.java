/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.mennonite;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class load_dna_data {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String udf_name_seen_in_listing(
//        @Name("rn1") 
//            Long rn1,
//        @Name("rn2") 
//            Long rn2
  )
   
         { 
             
        load_dna();
         return "";
            }

    
    
    public static void main(String args[]) {
        load_dna();
    }
    
     public static String load_dna() 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        String fn = "Elvira_Dyck_Koehn_3922_FTDNA_v4_Match_List.csv";
        
        String cq = "";
        gen.neo4jlib.neo4j_qry.qry_write("MATCH p=()-[r:match_segment]->() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("MATCH p=()-[r:shared_match]->() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("MATCH p=()-[r:person_match]->() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (n:Segment) delete n");
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (n:DNA_Match) delete n");
        
        int grandma_id=137304;
                
        gen.neo4jlib.neo4j_qry.qry_write("create (d:DNA_Match{grandma_id:" + grandma_id + "})");
        
        cq = "LOAD CSV WITH HEADERS FROM 'file:///" + fn + "' as line FIELDTERMINATOR ',' with line where toInteger(line.Grandma_ID) is not null merge (d:DNA_Match{fullname:toString(line.Matched_Person), grandma_id:toInteger(line.Grandma_ID)})";
        gen.neo4jlib.neo4j_qry.qry_write(cq);
        
        cq = "LOAD CSV WITH HEADERS FROM 'file:///" + fn + "' as line FIELDTERMINATOR ',' with line where toInteger(line.Grandma_ID) is not null match (p:Person{RN:toInteger(line.Grandma_ID)}) match(d:DNA_Match{grandma_id:toInteger(line.Grandma_ID)}) where d.grandma_id = p.RN with  line,p, d,count(*) as seg_ct merge (p)-[r:person_match{seg_ct:seg_ct,cm:toFloat(line.Total_cM),email:toString(case when line.Email is null then '' else line.Email end)}]-(d)";
        gen.neo4jlib.neo4j_qry.qry_write(cq);
        
        cq = "LOAD CSV WITH HEADERS FROM 'file:///" + fn+ "' as line FIELDTERMINATOR ',' with line,case when size(line.Chromosome)=1 then '0' + line.Chromosome else line.Chromosome end as chr where toInteger(line.Grandma_ID) is not null match(d:DNA_Match{grandma_id:toInteger(line.Grandma_ID)}) with line, d, ltrim(toString(case when chr is null then '' else chr end)) + ':' + toInteger(case when replace(line.Start_Pos,',','') is null then 0 else replace(line.Start_Pos,',','') end) + ':' + toInteger(case when replace(line.End_Pos,',','') is null then 0 else replace(line.End_Pos,',','') end) as Indx, chr,toInteger(replace(line.Start_Pos,',','')) as strt_pos, toInteger(replace(line.End_Pos,',','')) as end_pos merge (s:Segment{Indx:Indx, chr:chr,strt_pos:strt_pos,end_pos:end_pos})";
        gen.neo4jlib.neo4j_qry.qry_write(cq);
        
        cq = "LOAD CSV WITH HEADERS FROM 'file:///" + fn + "' as line FIELDTERMINATOR ',' with line,case when size(line.Chromosome)=1 then '0' + line.Chromosome else line.Chromosome end as chr where toInteger(line.Grandma_ID) is not null match(d:DNA_Match{grandma_id:toInteger(line.Grandma_ID)}) with line, d, ltrim(toString(case when chr is null then '' else chr end)) + ':' + toInteger(case when replace(line.Start_Pos,',','') is null then 0 else replace(line.Start_Pos,',','') end) + ':' + toInteger(case when replace(line.End_Pos,',','') is null then 0 else replace(line.End_Pos,',','') end) as Indx, chr,toInteger(replace(line.Start_Pos,',','')) as strt_pos, toInteger(replace(line.End_Pos,',','')) as end_pos match (s:Segment{Indx:Indx, chr:chr,strt_pos:strt_pos,end_pos:end_pos}) match (d:DNA_Match{grandma_id:toInteger(line.Grandma_ID)}) merge (d)-[r:match_segment{strt_cm:toFloat(line.Start_cM),end_cm:toFloat(line.End_cM),cm:toFloat(line.cM),identical_snps:toInteger(case when replace(line.Identical_SNPs,',','') is null then 0 else replace(line.Identical_SNPs,',','') end),half_identical_snps:toInteger(case when replace(line.Half_Identical_SNPs,',','') is null then 0 else replace(line.Half_Identical_SNPs,',','') end) }]-(s)";
        gen.neo4jlib.neo4j_qry.qry_write(cq);
        
        cq = "LOAD CSV WITH HEADERS FROM 'file:///" + fn + "' as line FIELDTERMINATOR ',' with line where toInteger(line.Grandma_ID) is not null with line.Matched_Person as match,toInteger(line.Grandma_ID) as match_id , count(*) as ct with match,match_id, ct,gen.rel.relationship_from_RNs(match_id," + grandma_id + ") as rel,gen.rel.compute_cor(match_id," + grandma_id + ") as cor,gen.rel.mrca_str(match_id," + grandma_id + ") as mrcas match (d1:DNA_Match{grandma_id:" + grandma_id + " }) with match,match_id,ct,rel,cor,mrcas match (d2:DNA_Match{grandma_id:match_id}) with match,match_id,ct,rel,cor,mrcas where cor>0 merge (d1)-[rsm:shared_match{seg_ct:ct,rel:rel,cor:cor,mrcas:mrcas}]->(d2)";
        gen.neo4jlib.neo4j_qry.qry_write(cq);
        
        return "";
    }
}
