/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.tgs;

import gen.neo4jlib.neo4j_qry;
import java.util.List;
import org.neo4j.driver.types.Path;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 *
 * @author david
 */
public class matches_all_tgs {
   @UserFunction
    @Description("Add triangulation group list as property to DNA_Match and Person nodes. Adds Label DNA_Match, which enables distinctive visualization formating using grass file. The function then infers ancestor TG list for each person in each branch descending from the common ancestor, thereby creating an autosomal haplotree.")
        
    public String matches_tgs(
//        @Name("project") 
//            String project,
//        @Name("ancestor_rn") 
//            Long ancestor_rn
    )
    

        {
        String cq = "";
        String r =at_ht();
        return r;
            }
    
   
    public String at_ht() 
    {
     gen.neo4jlib.neo4j_info.neo4j_var_reload();
     gen.rel.anc_rn anc = new gen.rel.anc_rn();
     Long anc_rn = anc.get_ancestor_rn();
     
//     String cq = "match p=(m:DNA_Match)-[[rm:match_segment]]-(s:Segment)-[[rs:seg_seq]]-(s2:Segment) where 100>=rm.cm>=7 and rm.snp_ct>=500 and m.ancestor_rn is not null and rm.p_anc_rn is not null and rm.m_anc_rn is not null  with rs.tgid as tg,m order by rs.tgid with m.fullname + ' ⦋' + m.RN + '⦌' as match,collect(distinct tg) as tgs return match,size(tgs) as tg_ct,tgs order by match";
     String cq = "match p=(m:DNA_Match)-[[rm:match_segment]]-(s:Segment)-[[rs:seg_seq]]-(s2:Segment) where 100>=rm.cm>=7 and rm.snp_ct>=500 and m.ancestor_rn is not null and rm.p_anc_rn is not null and rm.m_anc_rn is not null  with m,apoc.coll.sort(collect(distinct rs.tgid)) as tgs,apoc.coll.sort(collect(distinct rs.tg_name)) as tg_names, m.fullname + ' ⦋' + m.RN + '⦌' as match return match,size(tgs) as tg_ct,tg_names,tgs order by match";
    String excelFile =  gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project + "_at_haplotree","haplotree",1,"","1:####;1:####","",false,"UDF: \nreturn gen.tgs.matches_tgs()\n\ncypher qyery:\n" + cq,true);
    
    cq = "MATCH p=()-[r:tg_seg]->(s)   RETURN r.tg_name as tg_name,r.tgid as tgid,count(*) as ct, sum(case when s.anc_desc is not null then 1 else 0 end) as anc_desc_ct order by tg_name";
    gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project + "_tg_desc","anc_descendants",2,"","1:####;2:####;3:####",excelFile,true,"UDF: \nreturn gen.tgs.matches_tgs()\n\ncypher query\n" + cq,true);
    
    gen.neo4jlib.neo4j_qry.qry_to_csv("match p=(m:DNA_Match)-[rm:match_segment]-(s:Segment)-[rs:seg_seq]-(s2:Segment) where 100>=rm.cm>=7 and rm.snp_ct>=500 and m.ancestor_rn is not null and rm.p_anc_rn is not null and rm.m_anc_rn is not null with m,rm,rs,toInteger(case when s.chr='0X' then 23 else s.chr end) as chromosome,s.strt_pos as start_location,s.end_pos as end_location, rm.cm as centimorgans,rm.snp_ct as snps return distinct chromosome as chr,start_location as start,end_location as end, centimorgans as cm,snps,case when rm.p<rm.m then rm.p + '-' + rm.m else rm.m + '-' + rm.p end as match,'good' as confidence,rm.pair_mrca as group, 'maternal' as side, '' as notes,'' as color order by group union match (t:tg) return toInteger(case when t.chr='0X' then 23 else t.chr end) as chr,t.strt_pos as start,t.end_pos as end,t.cm as cm,500 as snps,t.name as match,'good' as confidence,'tg_region' as group,'paternal' as side,'tg' as notes,'red' as color", gen.neo4jlib.neo4j_info.project + "_tgs_dna_painter.csv");
    
    
     return "completed";
}
 
    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
}
