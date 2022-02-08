/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.genlib;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class Data_Summary {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String understand_your_data(
//        @Name("rn1") 
//            Long rn1,
//        @Name("rn2") 
//            Long rn2
  )
   
         { 
             
        String s = get_data();
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_data() 
    {
        int ct = 0;
        
        String cq = "match (n) return labels(n) as Node_label, count(*) as ct order by labels(n)";
        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Know_your_data", "Nodes", ct, "",  "1:######", "",  false, cq, false);
        ct=ct+1;
        
//        cq = "MATCH (n) with DISTINCT labels(n) as nd,apoc.coll.sort(keys(n)) as props with nd,apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect(props)))) as pr with nd,pr unwind pr as property return nd as nodes,property order by nodes,property";
//        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "node_prop", "Node_properties", ct, "",  "1:###,###,###", excelFile,  false, cq + "\n\nfor more detail, including data types: CALL apoc.meta.nodeTypeProperties()", false);
//        ct=ct+1;
        
        cq = "match (n)-[r]->() return type(r) as Relationship_type, count(*) as ct order by type(r)";             
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "rels", "Relationships", ct, "", "1:######",  excelFile, false, cq, false);
        ct=ct+1;

        cq = "MATCH (p:Person)-[[r:Gedcom_Kit]]->(k:Kit) where k.RN=p.RN RETURN k.kit_desc,k.RN, k.fullname,p.fullname,p.RN";
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "person-kit", "Person_Kit quality", ct, "", "1:######;4:######",  excelFile, false, cq, false);
        ct=ct+1;
        
        cq = "MATCH p=(k:Kit)-[[r:KitMatch]]->(m:DNA_Match) where k.RN=m.RN RETURN k.kit_desc, k.RN, m.fullname, m.RN";
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "kit_match", "Kit_match quality", ct, "", "1:######;4:######",  excelFile, false, cq, false);
        ct=ct+1;
        
        
//        cq = "MATCH ()-[r]-() with DISTINCT type(r) as rel,apoc.coll.sort(keys(r)) as props with rel,apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect(props)))) as pr with rel,pr unwind pr as property return rel as relationships,property order by relationships,property";             
//        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "rels_prop", "edge_properties", ct, "", "1:###,###,###",  excelFile, false, cq + "\n\nfor more detail, including data types: CALL apoc.meta.relTypeProperties()", false);
//        ct=ct+1;

        cq = "MATCH p=(k:Kit)-[r:KitMatch]->(m:DNA_Match) RETURN k.kit as kit,k.kit_desc as name,count(*) as matches order by k.kit_desc";             
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "kits", "Kits", ct, "", "2:######",  excelFile, false, cq, false);
        ct=ct+1;

//                cq = "MATCH p=(m1:DNA_Match)-[r:match_by_segment]->(m2:DNA_Match) with r,case when m1.fullname<m2.fullname then m1 else m2 end as match1,case when m2.fullname<m1.fullname then m1 else m2 end as match2 with distinct case when match1.RN>0 then match1.fullname + ' ⦋' + match1.RN + '⦌' else match1.fullname end as match1,case when match2.RN>0 then match2.fullname + ' ⦋' + match2.RN + '⦌' else match2.fullname end as match2,toInteger(r.cm) as shared_cm,toInteger(r.longest_cm) as longest_cm, r.seg_ct as segments return match1,match2,shared_cm, longest_cm,segments order by shared_cm desc";             
//        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "matches", "Shared Matches", ct, "", "1:###",  excelFile, false, cq, false);
//        ct=ct+1;
//      
//        cq = "MATCH (m:DNA_Match)-[r:match_pop]->(p:pop_group) RETURN p.name as population,count(*) as DNA_Matches order by DNA_Matches desc,population";                       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "pops", "pop_groups", ct, "", "1:###",  excelFile, false, cq, false);
//        ct=ct+1;

       
        cq = "match (t:tg) return t.project as project,t.name as name,t.tgid as tgid,t.chr as chr,t.strt_pos as start, t.end_pos as end,t.cm as cm, case when t.mrca_rn is not null then t.mrca_rn else '' end as mrca_rn order by chr,start,end";             
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "tgs", "Tiangulation groups", ct, "", "2:###;3:##;4:###,###,###;5:###,###,###;6:####.#;7:#####",  excelFile, false, cq, false);
        ct=ct+1;

        cq = "MATCH p=()-[r:match_segment]->() with sum (case when r.p_rn>0 then 1 else 0 end) as p_rn, sum (case when (r.p_rn=0 or r.p_rn is null) and r.m_rn>0 then 1 else 0 end) as m_rn_without_p_rn, sum (case when (r.m_rn=0 or r.m_rn is null) and r.p_rn>0 then 1 else 0 end) as p_rn_without_m_rn, sum (case when r.m_rn >0 and r.p_rn >0 then 1 else 0 end) as p_rn_with_m_rn, sum (case when r.m_rn >0 or r.p_rn >0 then 1 else 0 end) as p_rn_or_m_rn, sum (case when r.m_rn >0 then 1 else 0 end) as m_rn, sum (case when r.p_anc_rn >0 then 1 else 0 end) as p_anc_rn, sum (case when r.m_anc_rn >0 then 1 else 0 end) as m_anc_rn, count(*) as match_segment_ct return match_segment_ct, p_rn, m_rn, p_rn_or_m_rn, p_rn_with_m_rn, p_rn_without_m_rn, m_rn_without_p_rn,p_anc_rn,m_anc_rn";             
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Match_seg", "match_seg properties", ct, "", "0:###,###;1:###,###;2:###,###;3:###,###;4:###,###;",  excelFile, false, cq, false);
        ct=ct+1;


//        cq = "match (n)-[r]->() return type(r) as Relationship_type, count(*) as ct order by type(r)";             
//        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "rels", "Relationships", 1, "", "1:###,###,###",  excelFile, false, "", false);

       //cq = "match (t:tg) return t.project as project,t.name as name,t.tgid as tgid,t.chr as chr,t.strt_pos as start, t.end_pos as end,t.cm as cm, case when t.mrca_rn is not null then t.mrca_rn else '' end as mrca_rn order by chr,start,end";             
      //  excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "tgs", "Tiangulation groups", 3, "", "4:###,###,###;5:###,###,###,6:####.#",  excelFile, false, cq, false);

       cq = "Show Functions yield name, signature, description,returnDescription,aggregating where name STARTS WITH 'gen' return name, signature, description,returnDescription,aggregating";             
       gen.excelLib.queries_to_excel.qry_to_excel(cq, "functions", "Functions", ct, "", "1:###,###,###",  excelFile, true, cq, false);

//        cq ="MATCH (p:Person)-[r:Gedcom_DNA]->(m:DNA_Match) where p.RN=m.RN RETURN p.fullname, p.RN,m.fullname, m.RN order by p.fullname";
//        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "person_match", "Person_Match quality", ct, "", "1:######;3:#####",  excelFile, false, cq, false);
//        ct=ct+1;
        
//        cq = "MATCH (p:Place) where p.desc>'  ' RETURN p.desc as place, count(*) as ct order by ct desc,place";                      
//        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "places", "places", ct, "", "1:###",  excelFile, false, cq, false);
//        ct=ct+1;

 

       
        return excelFile;
    }
}
