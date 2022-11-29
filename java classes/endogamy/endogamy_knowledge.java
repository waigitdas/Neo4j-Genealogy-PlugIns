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
        
        //create union cor property
        gen.neo4jlib.neo4j_qry.qry_write("match (u:Union) with distinct u where gen.rel.compute_cor(u.U1,u.U2) >0 set u.cor=gen.rel.compute_cor(u.U1,u.U2)");
        
        //add union rel property
        gen.neo4jlib.neo4j_qry.qry_write("match (u:Union) where u.cor is not null with u,gen.rel.relationship_from_RNs(u.U1,u.U2) as rel set u.rel=rel");
        
        //add coi property to Person nodes
        gen.neo4jlib.neo4j_qry.qry_write("match (p:Person) with distinct p where gen.endogamy.coefficient_of_inbreeding(p.RN) >0 set p.coi = gen.endogamy.coefficient_of_inbreeding(p.RN)");

        return "completed";
    }
}
