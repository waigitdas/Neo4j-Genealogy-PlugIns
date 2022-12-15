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
        gen.neo4jlib.neo4j_qry.qry_write("match (u:Union) remove u.cor");
        gen.neo4jlib.neo4j_qry.qry_write("match (u:Union) remove u.rel");
        gen.neo4jlib.neo4j_qry.qry_write("match (p:Person) remove p.coi");
        
        try{
            gen.neo4jlib.neo4j_qry.CreateIndex("Person","coi");
            gen.neo4jlib.neo4j_qry.CreateIndex("Uion","cor");
            gen.neo4jlib.neo4j_qry.CreateIndex("Union","rel");
        }
        catch(Exception e){}
        
        //create union cor property
        gen.neo4jlib.neo4j_qry.qry_write("match (u:Union) with distinct u where gen.rel.compute_cor(u.U1,u.U2) >0 set u.cor=gen.rel.compute_cor(u.U1,u.U2)");
        
        //add union rel property
        gen.neo4jlib.neo4j_qry.qry_write("match (u:Union) where u.cor is not null with u,gen.rel.relationship_from_RNs(u.U1,u.U2) as rel set u.rel=rel");
        
        //add coi property to Person nodes
        gen.neo4jlib.neo4j_qry.qry_write("match (p:Person) with distinct p where gen.endogamy.coefficient_of_inbreeding(p.RN) >0 set p.coi = gen.endogamy.coefficient_of_inbreeding(p.RN)");

//        //add missing union cor to unions of those who are endogamous
//        gen.neo4jlib.neo4j_qry.qry_write("MATCH (p:Person) where p.coi>0 with p match (u:Union) where u.uid=p.uid and u.cor is null set u.cor=gen.rel.compute_cor(u.U1,u.U2) ");
        
        //add most recent endogamous union and generation to Person nodes
        gen.neo4jlib.neo4j_qry.qry_write("Match (p:Person) where p.coi>0 with collect(p) as rns unwind rns as z call { with z MATCH path=(u1:Union{uid:z.uid})-[r:union_parent*0..25]->(ua:Union) with z,ua, reduce(s='',x in relationships(path)|s + x.side) as uid_path where ua.cor >0 with z, ua as ua,ua.cor as cor ,size(uid_path) + 1 as gen, uid_path order by gen with z, ua,cor,gen,uid_path, left(uid_path,1) as side with z.coi as zr,ua, cor,gen,uid_path,side limit 1 return ua.uid as uid, gen, zr,cor,cor * exp(log(0.5)*gen) as proband_coi } set z.coi_gen=gen,z.mreu_uid=uid");
        
        
        
        return "completed";
    }
}
