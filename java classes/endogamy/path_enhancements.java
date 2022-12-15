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


public class path_enhancements {
    @UserFunction
    @Description("Add path enhancements to knowledge graph.")

    public String create_path_enhancements(
  )
   
         { 
             
        String s = add_path_enhancements();
         return s;
            }

    
    
    public static void main(String args[]) {
        add_path_enhancements();
    }
    
     public static String add_path_enhancements()
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload(); 
        
        gen.neo4jlib.neo4j_qry.qry_write("match (p:fam_path)-[r]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match(p:fam_path) delete p");
        gen.neo4jlib.neo4j_qry.qry_write("match (p:path_segs)-[r]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (p:path_segs) delete p");
        gen.neo4jlib.neo4j_qry.qry_write("match (i:intersect)-[r]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (i:intersect) delete i");
         
        try{
        gen.neo4jlib.neo4j_qry.CreateIndex("fam_path","persons");
        gen.neo4jlib.neo4j_qry.CreateIndex("fam_path", "gen");
        //gen.neo4jlib.neo4j_qry.CreateIndex("path_segs", "segs");  too long for index
        gen.neo4jlib.neo4j_qry.CreateIndex("intersect", "persons");
        }
        catch(Exception e){}
        
        //add path nodes
        gen.neo4jlib.neo4j_qry.qry_write("match (m:MSS) with collect(distinct m.RN) as rns1 match (d:DNA_Match)-[r:match_segment]->() where r.p_rn>0 with rns1,collect(distinct r.p_rn) as rns2 with apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(rns1+rns2))) as rns match path=(p:Person)-[r:father|mother*0..25]->(a:Person) where p.RN in rns and a.RN in rns and p.RN<>a.RN with [x in nodes(path)|x.RN] as path_nodes with path_nodes as path_nodes,size(path_nodes) as gen with path_nodes,gen create (ss:fam_path{persons:path_nodes,gen:gen})");
        
//        gen.neo4jlib.neo4j_qry.qry_write("match (m:MSS) with collect(distinct m.RN) as rns1 match (d:DNA_Match)-[r:match_segment]->() where r.p_rn>0 with rns1,collect(distinct r.p_rn) as rns2 with apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(rns1+rns2))) as rns match path=(p:Person)-[r:father|mother*0..25]->(a:Person) where p.RN in rns and a.RN in rns and p.RN<>a.RN with [x in nodes(path)|x.RN] as path_nodes match (m2:MSS)-[e2:ms_seg]->(s:Segment) where m2.RN in path_nodes with path_nodes as path_nodes,size(path_nodes) as gen, gen.graph.get_ordpath(path_nodes) as op, apoc.coll.sort(collect(distinct s.Indx)) as segs with path_nodes,gen create (ss:fam_path{persons:path_nodes,gen:gen})");

//gen.neo4jlib.neo4j_qry.qry_write("match (m:Avatar) with collect(distinct m.RN) as rns match path=(p:Person)-[r:father|mother*0..25]->(a:Person) where p.RN in rns and a.RN in rns and p.RN<>a.RN with [x in nodes(path)|x.RN] as path_nodes match (m2:Avatar)-[e2:ms_seg]->(s:Segment) where m2.RN in path_nodes with path_nodes as path_nodes,size(path_nodes) as gen, gen.graph.get_ordpath(path_nodes) as op, apoc.coll.sort(collect(distinct s.Indx)) as segs with path_nodes,gen create (ss:fam_path{persons:path_nodes,gen:gen})");
  
        //add path_seg nodes
        gen.neo4jlib.neo4j_qry.qry_write("match (m:MSS) with collect(distinct m.RN) as rns match path=(p:Person)-[r:father|mother*0..25]->(a:Person) where p.RN in rns and a.RN in rns and p.RN<>a.RN with [x in nodes(path)|x.RN] as path_nodes match (m2:MSS)-[e2:ms_seg]->(s:Segment) where m2.RN in path_nodes with path_nodes as path_nodes,size(path_nodes) as gen, gen.graph.get_ordpath(path_nodes) as op, apoc.coll.sort(collect(distinct s.Indx)) as segs with segs,size(segs) as seg_size,count(*) as ct create (ss:path_segs{segs:segs,seg_ct:seg_size,appearances:ct})");
        
        //add path_seg relationship
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (ps:path_segs) with ps, id(ps) as segs unwind ps.segs as x call { with x return x as i } with segs,i,ps match (s:Segment{Indx:i}) merge (ps)-[r:path_seg]-(s)");
        
        //add path_person relationship
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (ps:fam_path) with ps, id(ps) as id unwind ps.persons as x call { with x return x as i } with id,i,ps match (p:Person{RN:i}) merge (ps)-[r:path_person]-(p)");
        
        //create intersect nodes
        //uses gen.endogamy.sort_subpath(x,intersect) to kep sort order in intersect the same as the original path
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (p:fam_path) with collect (p.persons) as persons unwind persons as x unwind persons as y call { with x,y with x,y,apoc.coll.intersection(x,y) as intersect where x<y with intersect,x,y match (px:fam_path) where px.persons=x with gen.endogamy.sort_subpath(x,y,intersect) as intersect,px,y match (py:fam_path) where py.persons=y return intersect,id(px) as idx,id(py) as idy } with intersect where intersect <>[] with intersect,count(*) as ct,size(intersect) as len create(i:intersect{persons:intersect,ct:ct,len:len})");
        
        //create patn_intersect relationship
        gen.neo4jlib.neo4j_qry.qry_write("match (f:fam_path) with f match (i:intersect) where size(apoc.coll.intersection(f.persons,i.persons))=size(i.persons) merge (i)-[r:path_intersect]->(f)");
        

        return "completed";
    }
}
