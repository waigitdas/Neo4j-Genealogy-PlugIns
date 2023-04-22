/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.load;

import gen.neo4jlib.neo4j_qry;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class mennonite_project_load {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String load_and_enhance(
  )
   
         { 
             
        load_menn();
         return "";
            }

    
    
    public static void main(String args[]) {
        load_menn();
    }
    
     public static String load_menn() 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        gen.gedcom.upload_gedcom g = new gen.gedcom.upload_gedcom();
        g.load_gedcom();


        //create union cor property
        String uids[] = gen.neo4jlib.neo4j_qry.qry_to_csv("MATCH (u:Union) where u.U1>0 and u.U2>0  RETURN u.uid as uid").split("\n");
        int increment = 1000;
        int curr_ct = 0;
        int endct = increment;
        int stop = (uids.length/increment) + 1;
        
       
        for (int j=0; j<stop; j++)
        {
        for (int i = curr_ct; i < endct; i++)
            {
            try
                {
                gen.neo4jlib.neo4j_qry.qry_write("match (u:Union{uid:" + uids[i] + "}) with distinct u where u.U1>0 and u.U2>0 with u, gen.rel.compute_cor(u.U1,u.U2) as cor with u,cor where cor>0 set u.cor=cor");
            }
            catch (Exception e){}
        } //next i
         
        curr_ct = curr_ct + increment;
        endct = endct + increment;
        System.out.println(j + "\t" + curr_ct + "\t" + endct);
        
        } //next j
        
    
        

       //create instances class to load reference data
        gen.ref.fam_rel fr = new gen.ref.fam_rel();
        fr.load_family_relationships();
 
        String uids2[] = gen.neo4jlib.neo4j_qry.qry_to_csv("MATCH (u:Union) where u.cor is not null RETURN u.uid as uid, u.U1 as u1, u.U2 as u2").split("\n");
        //add union rel property and coi property to Person nodes
        for (int i=0; i<uids2.length; i++)
        {
        String uu[] = uids2[i].split(",");
        gen.neo4jlib.neo4j_qry.qry_write("match (u:Union{uid:" + uu[0] + "}) with u,gen.rel.relationship_from_RNs(u.U1,u.U2) as rel set u.rel=rel");
        gen.neo4jlib.neo4j_qry.qry_write("match (p:Person{RN:" + uu[1] + "}) with  p, gen.endogamy.coefficient_of_inbreeding(p.RN) as coi set p.coi = coi");
        gen.neo4jlib.neo4j_qry.qry_write("match (p:Person{RN:" + uu[2] + "}) with p, gen.endogamy.coefficient_of_inbreeding(p.RN) as coi set p.coi = coi");
        }        
       
        //union_parennt relationship
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (u:Union) with u,u.U1 as u1,u.U2 as u2 match (p:Person) where p.RN=u1 or p.RN =u2 with u,p match (u2:Union) where p.uid=u2.uid merge (u)-[r:union_parent{side:case when u.U1=p.RN then 'P' else 'M' end}]->(u2)");

        //add most recent endogamous union and generation to Person nodes
        gen.neo4jlib.neo4j_qry.qry_write("Match (p:Person) where p.coi>0 with collect(p) as rns unwind rns as z call { with z MATCH path=(u1:Union{uid:z.uid})-[r:union_parent*0..25]->(ua:Union) with z,ua, reduce(s='',x in relationships(path)|s + x.side) as uid_path where ua.cor >0 with z, ua as ua,ua.cor as cor ,size(uid_path) + 1 as gen, uid_path order by gen with z, ua,cor,gen,uid_path, left(uid_path,1) as side with z.coi as zr,ua, cor,gen,uid_path,side limit 1 return ua.uid as uid, gen, zr,cor,cor * exp(log(0.5)*gen) as proband_coi } set z.coi_gen=gen,z.mreu_uid=uid");
        
        //add end of line ancestors cout if coi>0
        gen.neo4jlib.neo4j_qry.qry_write("match path=(p:Person)-[r:father|mother*0..25]->(a:Person) where p.coi is not null and a.uid=0 with p,collect(distinct a.RN) as arn set p.eol_anc_ct= size(arn)");
        
        gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited("match path=(p:Person)-[r:father|mother*0..25]->(a:Person) where p.coi>0 and p.RN<>a.RN with p,[x in nodes(path)|x.RN] as path_nodes with p,path_nodes as path_nodes,size(path_nodes) as gen with p, path_nodes,gen return p.RN as RN,path_nodes,gen","fam_paths.csv");
        
        
////        gen.neo4jlib.neo4j_qry.qry_write("MATCH p=()-[r:path_person]->() delete r");
////        gen.neo4jlib.neo4j_qry.qry_write("MATCH (n:fam_path) delete n");
        
 
       gen.neo4jlib.neo4j_qry.CreateIndex("fam_path","persons");
////        gen.neo4jlib.neo4j_qry.CreateCompositeIndex("fam_path", "persons, gen");
 

////        gen.neo4jlib.neo4j_qry.qry_write("drop index fam_path_persons_gen");
        
        String lc = "LOAD CSV WITH HEADERS FROM 'file:///fam_paths.csv' as line FIELDTERMINATOR '|' return line ";
        String  cq = "create (f:fam_path{persons:gen.genlib.covertStrToIntList(line.path_nodes),gen:line.gen})";
        gen.neo4jlib.neo4j_qry.APOCPeriodicIterateCSV(lc,cq, 10000);
       
        
        cq = "match(p:Person{RN:toInteger(line.RN)}) match (f:fam_path{persons:gen.genlib.covertStrToIntList(line.path_nodes),gen:line.gen}) using index f:fam_path(persons) merge (f)-[r:path_person]->(p)";
        gen.neo4jlib.neo4j_qry.APOCPeriodicIterateCSV(lc,cq, 10000);

        

//        String path_int[] = gen.neo4jlib.neo4j_qry.qry_to_csv("MATCH (p:fam_path) return p.persons as person").split("\n");
        

//        create intersect nodes
//        uses gen.endogamy.sort_subpath(x,intersect) to kep sort order in intersect the same as the original path
//        gen.neo4jlib.neo4j_qry.qry_write("MATCH (p:fam_path) with collect (p.persons) as persons unwind persons as x unwind persons as y call { with x,y with x,y,apoc.coll.intersection(x,y) as intersect where x<y with intersect,x,y match (px:fam_path) where px.persons=x with gen.endogamy.sort_subpath(x,y,intersect) as intersect,px,y match (py:fam_path) where py.persons=y return intersect,id(px) as idx,id(py) as idy } with intersect where intersect <>[] with intersect,count(*) as ct,size(intersect) as len create(i:intersect{persons:intersect,ct:ct,len:len})");
//        
//        create patn_intersect relationship
//        gen.neo4jlib.neo4j_qry.qry_write("match (f:fam_path) with f match (i:intersect) where size(apoc.coll.intersection(f.persons,i.persons))=size(i.persons) merge (i)-[r:path_intersect]->(f)");
//        

        return "completed";

      }
}
