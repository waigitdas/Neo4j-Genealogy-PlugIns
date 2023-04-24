/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import java.awt.Desktop;
import java.io.File;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class matches_by_seg {
    @UserFunction
    @Description("Returns all segments with the matches at each segment. ")

    public String matches_by_segments(
//        @Name("rn1") 
//            Long rn1,
//        @Name("rn2") 
//            Long rn2
  )
   
         { 
             
        String r = get_matches();
         return r;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_matches() 
    {
        String cq = "match (s1:Segment) with s1 order by s1.chr,s1.strt_pos,s1.end_pos with collect(distinct s1) as segs1 match (m1:DNA_Match)-[r1:match_segment]-(s2:Segment) where s2 in segs1 and r1.cm>=7 and r1.snp_ct>=500 with case when m1.RN>0 then '*' + m1.fullname + ' ⦋' + m1.RN + '⦌' else m1.fullname end as m2, case when m1.RN>0 then m1.RN else null end as rns1, case when r1.m_rn>0 then r1.m_rn else null end as rns2, case when r1.m_rn>0 then '*' + r1.m + ' ⦋' + r1.m_rn + '⦌' else r1.m end as m3,max(r1.cm) as max_cm,min(r1.cm) as min_cm, s2,count(r1) as edgect, sum(case when r1.p=m1.fullname then 1 else 0 end) as unidir_ct_p, sum(case when r1.m=m1.fullname then 1 else 0 end) as unidir_ct_m with s2,min_cm,max_cm,apoc.coll.union(collect (distinct m2), collect(distinct m3)) as matches, apoc.coll.union(collect(distinct rns1),collect(distinct(rns2))) as rns, sum(edgect) as edgect,sum(unidir_ct_m) as unidir_ct_m,sum(unidir_ct_p) as unidir_ct_p order by s2.chr,s2.strt_pos,s2.end_pos with s2,min_cm,max_cm,apoc.coll.sort(apoc.coll.flatten(matches)) as matches, apoc.coll.sort(apoc.coll.flatten(rns)) as rns,edgect,unidir_ct_m,unidir_ct_p  return s2.chr as chr,s2.strt_pos as start_pos,s2.end_pos as end_pos,round(min_cm,1) as cm,edgect,unidir_ct_p,unidir_ct_m,size(rns) as kits,size(matches) as match_ct,rns,matches";
        
        Long seg_ct = gen.neo4jlib.neo4j_qry.qry_long_list("match (s1:Segment) with s1 order by s1.chr,s1.strt_pos,s1.end_pos with collect(distinct s1) as segs1 match (m1:DNA_Match)-[r1:match_segment]-(s2:Segment) where s2 in segs1 and r1.cm>=7 and r1.snp_ct>=500 with distinct s2 return count(s2) as match_ct").get(0);
        
        String match_ct = gen.neo4jlib.neo4j_qry.qry_str("match (s1:Segment) with s1 order by s1.chr,s1.strt_pos,s1.end_pos with collect(distinct s1) as segs1 match (m1:DNA_Match)-[r1:match_segment]-(s2:Segment) where s2 in segs1 and r1.cm>=7 and r1.snp_ct>=500 with distinct m1 return count(m1) as match_ct");
 
        String retn= "Excel";
        if (seg_ct < 60000) {
         
        gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project + "_match segments", "matches", 1, "", "0:##;1:###,###,###;2:###,###,###;3:##.#;4:###;5:###;6:###;7:###;8:###", "", true,"UDF: gen.dna.matches_by_segments()\n\n" + gen.neo4jlib.neo4j_info.project + " project total number of unique matches = " + match_ct + "\nThe edge count includes those originating with\nthe proband (col F) and the match (col G)\nThe kits have an * prefix in the match list." ,true);
        }
        else {
            retn = "csv";
            
            String fn ="match_segments_" + gen.genlib.current_date_time.getDateTime() + ".csv";
            gen.neo4jlib.neo4j_qry.qry_to_csv(cq.replace("⦋","[").replace("⦌","]"), fn );
            try{
                Desktop.getDesktop().open(new File(gen.neo4jlib.neo4j_info.Import_Dir + fn));  
            }
            catch (Exception e) {return "cannot open  csv";}
        }
        
        return "completed; file is " + retn + " with " + match_ct + " unique matches. ";
       }
    }
     

