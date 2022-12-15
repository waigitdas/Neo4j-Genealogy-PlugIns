/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.endogamy;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class endogamy_knowledge_report {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String endogamy_knowledge(
  )
   
         { 
             
        String s = create_report();
         return s;
            }

    
    
    public static void main(String args[]) {
        create_report();
    }
    
     public static String create_report() 
    {
        int ct = 1 ;
        
        //persons with coi
        String cq = "MATCH (p:Person) where p.coi is not null RETURN gen.gedcom.person_from_rn(p.RN,true) as name ,p.coi as coi, p.coi_gen as coi_gen order by coi desc, name";
        String      excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_knowledge_graph", "person_COIs", ct, "", "0:#####;1:0.##########;2:#####", "", false,"UDF:\nreturn gen.endogamy.endogamy_knowledge()\n\ncypher query:\n" +  cq + "\n\nShown are persons with endogamy and their coefficients of inbreeding (COI).\nSorted by COI in descending order.\n\nThe COI is the COR of the parents of the most recent endoganous ancestor (MREA) divided in half for each generation (coi_gen) to the MREA.\n", false);
        ct = ct + 1;
        
        //unions where members are related
        cq = "match (u:Union) where u.cor is not null with u match(p:Person) where p.RN=u.U1 or p.RN=u.U2 return collect(gen.gedcom.person_from_rn(p.RN,true)) as union_pair,u.uid as uid, u.cor as cor,u.rel as rel order by cor desc";
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_knowledge_graph", "unions_of_relatives", ct, "", "1:#####;2:0.##########", excelFile, false,"UDF:\nreturn gen.endogamy.endogamy_knowledge_graph()\n\ncypher query:\n" +  cq + "\n\nShown are unions in which the people are related.\nSorted by COR in descendding order.", false);
        ct = ct + 1;
      
        
                //union relationships
         cq="MATCH (u:Union) where u.rel is not null RETURN u.rel as partner_relationship,u.cor as cor, count(*) as ct order by cor desc";
          excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_package", "union_relationships", ct, "", "1:0.#########;2:#########;3:0.########;4:####", excelFile,false,"cypher query:\n" +  cq + "\n\nUnions where the partners are related with their coefficient of relationship and number of occurences.", false);
        ct = ct + 1;
 
        //paths
        cq = "match (m:MSS) with collect(distinct m.RN) as rns match path=(p:Person)-[[r:father|mother*0..25]]->(a:Person) where p.RN in rns and a.RN in rns and p.RN<>a.RN with [[x in nodes(path)|x.RN]] as path_nodes match (m2:MSS)-[[e2:ms_seg]]->(s:Segment) where m2.RN in path_nodes with path_nodes as path_nodes,size(path_nodes) as gen, gen.graph.get_ordpath(path_nodes) as op, apoc.coll.sort(collect(distinct s.Indx)) as segs with op,path_nodes,gen with path_nodes,gen,op order by op optional match (f:fam_path{persons:path_nodes})-[[rp:path_intersect]]-(i:intersect) with distinct path_nodes,gen as path_length,i return path_nodes, path_length,count(i) as unique_intersect_ct";
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_knowledge_graph", "paths", ct, "", "1:#####;2:0.##########", excelFile, false,"UDF:\nreturn gen.endogamy.endogamy_knowledge_graph()\n\ncypher query:\n" +  cq + "\n\nPaths and their lengths in the entire family tree graph shown in ordpath order.\n\nA similar query is used to create the fam_path nodes.\n\nTo see the intersect (subgraph) detail for a path, use this query replacing xxx with the path:\nmatch (f:fam_path{persons:[[xxx]]})-[[rp:path_intersect]]-(i:intersect) with f,i, gen.graph.get_ordpath(i.persons) as op with f,i,op order by op return f.persons as fam_path,i.persons as intersect", false);
        ct = ct + 1;
        
        //intersects
        cq = "MATCH p=(i:intersect)-[r:path_intersect]->(f:fam_path) with  i.persons as ip,count(*) as ct return ip as intercept,size(ip) as len,ct  as paths_with_intersect order by gen.graph.get_ordpath(ip)";
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "endogamy_knowledge_graph", "intersections", ct, "", "1:#####;2:0.##########", excelFile, true,"UDF:\nreturn gen.endogamy.endogamy_knowledge_graph()\n\ncypher query:\n" +  cq + "\n\nPaths of intersections of two paths and the lengths and the number of paths they intersect.\n\nA similar query is used to produce the intercept nodes.\n\nTo see the path detail for an intersect, use this query replacing xxx with the intersext:\nmatch (f:fam_path)-[[rp:path_intersect]]-(i:intersect{persons:[[xxx]]}) with f,i, gen.graph.get_ordpath(f.persons) as op with f,i,op order by op return distinct i.persons as intersect,f.persons as fam_path", false);
        ct = ct + 1;
        
        
        
        return "completed";
    }
}
