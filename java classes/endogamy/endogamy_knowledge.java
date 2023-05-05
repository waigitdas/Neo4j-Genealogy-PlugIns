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


public class endogamy_knowledge {
    @UserFunction
    @Description("Create knowledge library enhancements for endogamy.")

    public String endogamy_knowledge_graph(
  )
   
         { 
             
        String s = create_knowledge();
         return s;
            }

    
    
    public static void main(String args[]) {
        create_knowledge();
    }
    
     public static String create_knowledge() 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
       
        //remove any prior properties
        try{
            gen.neo4jlib.neo4j_qry.qry_write("match (u:Union) remove u.cor");
            gen.neo4jlib.neo4j_qry.qry_write("match (u:Union) remove u.rel");
            gen.neo4jlib.neo4j_qry.qry_write("match (p:Person) remove p.coi");


            gen.neo4jlib.neo4j_qry.CreateIndex("Person","coi");
            gen.neo4jlib.neo4j_qry.CreateIndex("Union","cor");
            gen.neo4jlib.neo4j_qry.CreateIndex("Union","rel");
        }
        catch(Exception e){}
        
        
        
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
        //System.out.println(j + "\t" + curr_ct + "\t" + endct);
        
        } //next j
        
        
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
 


         ;

//    try
//    {
//        //add union rel property
//        gen.neo4jlib.neo4j_qry.qry_write("match (u:Union) where u.cor is not null with u,gen.rel.relationship_from_RNs(u.U1,u.U2) as rel set u.rel=rel");
////        }
//        //add coi property to Person nodes
//        gen.neo4jlib.neo4j_qry.qry_write("match (p:Person) with distinct p where gen.endogamy.coefficient_of_inbreeding(p.RN) >0 set p.coi = gen.endogamy.coefficient_of_inbreeding(p.RN)");
//
////        //add missing union cor to unions of those who are endogamous
////        gen.neo4jlib.neo4j_qry.qry_write("MATCH (p:Person) where p.coi>0 with p match (u:Union) where u.uid=p.uid and u.cor is null set u.cor=gen.rel.compute_cor(u.U1,u.U2) ");
//        
//        //add most recent endogamous union and generation to Person nodes
//        gen.neo4jlib.neo4j_qry.qry_write("Match (p:Person) where p.coi>0 with collect(p) as rns unwind rns as z call { with z MATCH path=(u1:Union{uid:z.uid})-[r:union_parent*0..25]->(ua:Union) with z,ua, reduce(s='',x in relationships(path)|s + x.side) as uid_path where ua.cor >0 with z, ua as ua,ua.cor as cor ,size(uid_path) + 1 as gen, uid_path order by gen with z, ua,cor,gen,uid_path, left(uid_path,1) as side with z.coi as zr,ua, cor,gen,uid_path,side limit 1 return ua.uid as uid, gen, zr,cor,cor * exp(log(0.5)*gen) as proband_coi } set z.coi_gen=gen,z.mreu_uid=uid");
//        
//        //add end of line ancestor cout if coi>0
//        gen.neo4jlib.neo4j_qry.qry_write("match path=(p:Person)-[r:father|mother*0..25]->(a:Person) where p.coi is not null and a.uid=0 with p,collect(distinct a.RN) as arn set p.eol_anc_ct= size(arn)");
//    }
//    catch (Exception e)
//    {
//        
//    }
       
        return "completed";
    }
}
