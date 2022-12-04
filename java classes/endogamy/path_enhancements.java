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

    public String add_path_enhancements(
  )
   
         { 
             
        String s = add_enhancements();
         return s;
            }

    
    
    public static void main(String args[]) {
        add_enhancements();
    }
    
     public static String add_enhancements()
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
        //gen.neo4jlib.neo4j_qry.CreateIndex("path_segs", "segs");
        gen.neo4jlib.neo4j_qry.CreateIndex("intersect", "persons");
        }
        catch(Exception e){}
        
        //add path nodes
        gen.neo4jlib.neo4j_qry.qry_write("match (m:MSS) with collect(distinct m.RN) as rns match path=(p:Person)-[r:father|mother*0..25]->(a:Person) where p.RN in rns and a.RN in rns and p.RN<>a.RN with [x in nodes(path)|x.RN] as path_nodes match (m2:MSS)-[e2:ms_seg]->(s:Segment) where m2.RN in path_nodes with path_nodes as path_nodes,size(path_nodes) as gen, gen.graph.get_ordpath(path_nodes) as op, apoc.coll.sort(collect(distinct s.Indx)) as segs with path_nodes,gen create (ss:fam_path{persons:path_nodes,gen:gen})");
        
        //add path_seg nodes
        gen.neo4jlib.neo4j_qry.qry_write("match (m:MSS) with collect(distinct m.RN) as rns match path=(p:Person)-[r:father|mother*0..25]->(a:Person) where p.RN in rns and a.RN in rns and p.RN<>a.RN with [x in nodes(path)|x.RN] as path_nodes match (m2:MSS)-[e2:ms_seg]->(s:Segment) where m2.RN in path_nodes with path_nodes as path_nodes,size(path_nodes) as gen, gen.graph.get_ordpath(path_nodes) as op, apoc.coll.sort(collect(distinct s.Indx)) as segs with segs,size(segs) as seg_size,count(*) as ct create (ss:path_segs{segs:segs,seg_ct:seg_size,appearances:ct})");
        
        //add path_seg relationship
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (ps:path_segs) with ps, id(ps) as segs unwind ps.segs as x call { with x return x as i } with segs,i,ps match (s:Segment{Indx:i}) merge (ps)-[r:path_seg]-(s)");
        
        //create path, intersect pipe-delimited file
        gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited("MATCH (p:fam_path) with collect (p.persons) as persons unwind persons as x unwind persons as y call { with x,y with x,y,apoc.coll.intersection(x,y) as intersect where x<y with intersect,x,y match (px:fam_path) where px.persons=x with intersect,px,y match (py:fam_path) where py.persons=y return intersect,id(px) as idx,id(py) as idy } with x,y,idx,idy,intersect where intersect <>[] RETURN idx,idy,x as path1,y as path2, intersect","paths.csv");
        
        //create intersect nodes
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///paths.csv' as line FIELDTERMINATOR '|' with line.intersect as intersect,count(*) as ct,size(line.intersect)-size(replace(line.intersect,',','')) +1 as len create(i:intersect{persons:intersect,ct:ct,len:len})");
        
        //create path_intersect relationship
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///paths.csv' as line FIELDTERMINATOR '|' match (i:intersect{persons:line.intersect})  match (p:fam_path) where id(p)=toInteger(line.idx) merge (i)-[r:path_intersect]->(p)");
        
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///paths.csv' as line FIELDTERMINATOR '|' match (i:intersect{persons:line.intersect})  match (p:fam_path) where id(p)=toInteger(line.idy) merge (i)-[r:path_intersect]->(p)");
        
        return "completed";
    }
}
