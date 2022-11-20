/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.avatar;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class chr_parental_origin {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String parental_origin_of_chromosomes(
        @Name("rn") 
            Long rn
  )
   
         { 
             
        String s = get_origins(rn);
         return s;
            }

    
    
    public static void main(String args[]) {
        get_origins(1L);
    }
    
     public static String get_origins(Long rn) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
  
        String cqdnapainter = "with " + rn + " as rn CALL { with rn MATCH p=()-[[r:match_segment]]->(s:Segment) where (r.m_rn=rn and size(r.m_side) is not null) RETURN s as segment, case when r.m_side='U' then 'unknown' when r.m_side='F' then 'paternal' else 'maternal' end as side, r.m as target,r.p as match ,r.cor as cor,r.cm as cm,r.snp_ct as snps,r.pair_mrca as group union with rn MATCH p=()-[[r:match_segment]]->(s:Segment) where (r.p_rn=rn and size(r.p_side) is not null) RETURN s as segment,case when r.p_side='U' then 'unknown' when r.p_side='F' then 'paternal' else 'maternal' end as side, r.p as target,r.m as match , r.cor as cor, r.cm as cm,r.snp_ct as snps,r.pair_mrca as group } return case when segment.chr='0X' then 23 else toInteger(segment.chr) end as chr,segment.strt_pos as start,segment.end_pos as end, cm,snps,target +':' + match as match,'good' as confidence,group, case when side='unknown' then 'both' else side end as side,'' as notes,'' as color";
        
        String cq ="with " + rn + " as rn CALL { with rn MATCH p=()-[[r:match_segment]]->(s:Segment) where (r.m_rn=rn and size(r.m_side) is not null) RETURN s,r, r.m as target,r.p as match,r.m_side as side union with rn MATCH p=()-[[r:match_segment]]->(s:Segment) where (r.p_rn=rn and size(r.p_side) is not null) RETURN s,r, r.p as target,r.m as match, r.p_side as side } with r,s,target,match,s.Indx as segment,case when side='U' then 'unknown' when side='F' then 'paternal' else 'maternal' end as side,  r.cor as cor, r.cm as cm,r.snp_ct as snps,r.pair_mrca as pair_mrca return distinct segment, side,target, match,cor, cm,snps,pair_mrca order by segment,side"; 

        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, gen.neo4jlib.neo4j_info.project + "_" + rn + "_chr_parental_origins", "parental_origins", 1, "", "2:###;3:###;4:0.######;5:####.#;6:#####", "", true, "UDF:\nreturn gen.profile.parental_origin_of_chromosomes(" + rn + ")\n\ncypher query:\n" + cq + "\n\nDNA Painter query:\n" + cqdnapainter + "\n\nThe segments reported are half identical identity by descent regions.\n\nReferences:\nhttps://isogg.org/wiki/Identical_by_descent\nhttps://isogg.org/wiki/Half-identical_region\n", true);
        
        
        cq = "with " + rn + " as rn CALL { with rn MATCH p=()-[r:match_segment]->(s:Segment) where (r.m_rn=rn and size(r.m_side) is not null) RETURN s as segment, case when r.m_side='U' then 'unknown' when r.m_side='F' then 'paternal' else 'maternal' end as side, r.m as target,r.p as match ,r.cor as cor,r.cm as cm,r.snp_ct as snps,r.pair_mrca as group union with rn MATCH p=()-[r:match_segment]->(s:Segment) where (r.p_rn=rn and size(r.p_side) is not null) RETURN s as segment,case when r.p_side='U' then 'unknown' when r.p_side='F' then 'paternal' else 'maternal' end as side, r.p as target,r.m as match , r.cor as cor, r.cm as cm,r.snp_ct as snps,r.pair_mrca as group } return case when segment.chr='0X' then 23 else toInteger(segment.chr) end as chr,segment.strt_pos as start,segment.end_pos as end, cm,snps,target +':' + match as match,'good' as confidence,group, case when side='unknown' then 'both' else side end as side,'' as notes,'' as color";
        gen.neo4jlib.neo4j_qry.qry_to_csv(cq,gen.neo4jlib.neo4j_info.project + "_chr_origins_dna_painter_" + gen.genlib.current_date_time.getDateTime() +  ".csv");
         return "complted";
    }
}
