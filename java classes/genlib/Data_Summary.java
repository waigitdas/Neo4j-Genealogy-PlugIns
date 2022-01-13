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
        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Know_your_data", "Nodes", ct, "",  "1:###,###,###", "",  false, cq, false);
        ct=ct+1;
        
//        cq = "MATCH (n) with DISTINCT labels(n) as nd,apoc.coll.sort(keys(n)) as props with nd,apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect(props)))) as pr with nd,pr unwind pr as property return nd as nodes,property order by nodes,property";
//        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "node_prop", "Node_properties", ct, "",  "1:###,###,###", excelFile,  false, cq + "\n\nfor more detail, including data types: CALL apoc.meta.nodeTypeProperties()", false);
//        ct=ct+1;
        
        cq = "match (n)-[r]->() return type(r) as Relationship_type, count(*) as ct order by type(r)";             
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "rels", "Relationships", ct, "", "1:###,###,###",  excelFile, false, cq, false);
        ct=ct+1;

//        cq = "MATCH ()-[r]-() with DISTINCT type(r) as rel,apoc.coll.sort(keys(r)) as props with rel,apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect(props)))) as pr with rel,pr unwind pr as property return rel as relationships,property order by relationships,property";             
//        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "rels_prop", "edge_properties", ct, "", "1:###,###,###",  excelFile, false, cq + "\n\nfor more detail, including data types: CALL apoc.meta.relTypeProperties()", false);
//        ct=ct+1;


        cq = "match (p:Person) where p.surname>'A' return p.fullname as Name, p.RN as RN, p.BDGed as BD,p.DDGed as DD, case when p.kit is null then ' ' else p.Kit end as Kit order by p.surname, p.first_name, p.middle_name";             
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "persons", "persons", ct, "", "1:###,###,###",  excelFile, false, cq, false);
        ct=ct+1;

        cq = "MATCH p=(k:Kit)-[r:KitMatch]->(m:DNA_Match) RETURN k.kit as kit,count(*) as ct order by kit";             
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "kits", "Kits", ct, "", "1:###,###",  excelFile, false, cq, false);
        ct=ct+1;

                cq = "MATCH p=(m1:DNA_Match)-[r:match_by_segment]->(m2:DNA_Match) with r,case when m1.fullname<m2.fullname then m1 else m2 end as match1,case when m2.fullname<m1.fullname then m1 else m2 end as match2 with distinct case when match1.RN>0 then match1.fullname + ' ⦋' + match1.RN + '⦌' else match1.fullname end as match1,case when match2.RN>0 then match2.fullname + ' ⦋' + match2.RN + '⦌' else match2.fullname end as match2,toInteger(r.cm) as shared_cm,toInteger(r.longest_cm) as longest_cm, r.seg_ct as segments return match1,match2,shared_cm, longest_cm,segments order by shared_cm desc";             
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "matches", "Shared Matches", ct, "", "1:###",  excelFile, false, cq, false);
        ct=ct+1;
      
        cq = "MATCH (m:DNA_Match)-[r:match_pop]->(p:pop_group) RETURN p.name as population,count(*) as DNA_Matches order by DNA_Matches desc,population";                       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "pops", "pop_groups", ct, "", "1:###",  excelFile, false, cq, false);
        ct=ct+1;

        cq = "MATCH (p:Place) where p.desc>'  ' RETURN p.desc as place, count(*) as ct order by ct desc,place";                       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "places", "places", ct, "", "1:###",  excelFile, false, cq, false);
        ct=ct+1;

       cq = "MATCH (p:Place) where p.desc>'  ' RETURN p.desc as place, count(*) as ct order by ct desc,place";                       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "places", "places", ct, "", "1:###",  excelFile, false, cq, false);
        ct=ct+1;

        
        cq = "match (t:tg) return t.project as project,t.name as name,t.tgid as tgid,t.chr as chr,t.strt_pos as start, t.end_pos as end,t.cm as cm, case when t.mrca_rn is not null then t.mrca_rn else '' end as mrca_rn order by chr,start,end";             
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "tgs", "Tiangulation groups", ct, "", "2:###;3:##;4:###,###,###;5:###,###,###;6:####.#;7:#####",  excelFile, false, cq, false);
        ct=ct+1;


       //cq = "match (t:tg) return t.project as project,t.name as name,t.tgid as tgid,t.chr as chr,t.strt_pos as start, t.end_pos as end,t.cm as cm, case when t.mrca_rn is not null then t.mrca_rn else '' end as mrca_rn order by chr,start,end";             
      //  excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "tgs", "Tiangulation groups", 3, "", "4:###,###,###;5:###,###,###,6:####.#",  excelFile, false, cq, false);

                cq = "Show Functions yield name, signature, description,returnDescription,aggregating where name STARTS WITH 'gen' return name, signature, description,returnDescription,aggregating";             
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "functions", "Functions", ct, "", "1:###,###,###",  excelFile, true, cq, false);

//        cq = "match (n)-[r]->() return type(r) as Relationship_type, count(*) as ct order by type(r)";             
//        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "rels", "Relationships", 1, "", "1:###,###,###",  excelFile, false, "", false);

              


        return excelFile;
    }
}