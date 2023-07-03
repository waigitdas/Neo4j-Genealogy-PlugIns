/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.avatar;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class assign_grandparent {
    @UserFunction
    @Description("Template used in creating new functions.")

    public Long grandparent_assignment(
        @Name("close_rn") 
            Long close_rn,
        @Name("compare_rn") 
            Long compare_rn
  )
   
         { 
             
        Long rn = get_grandparent(close_rn,compare_rn);
         return rn;
            }

    
    
    public static void main(String args[]) {
        Long rn = get_grandparent(209L,5242L);
        System.out.println(rn);
    }
    
     public static Long get_grandparent(Long close_rn,Long compare_rn) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        String cq = "match path=(p1:Person{RN:" + close_rn + "})-[r1:father|mother*0..15]->(mrca:Person)<-[r2:father|mother*0..15]-(p2:Person{RN:" + compare_rn + "}) with [x in nodes(path)|x.RN] as rns with rns,rns[1] as inline_grandparent with inline_grandparent as rn, rns as pt MATCH p=(u1:Union)-[r:union_parent]->(u2:Union) where (u1.U1=rn or u1.U2=rn) with pt, u2 as assign_union where (u2.U1 in pt or u2.U2 in pt) with case when assign_union.U1 in pt then assign_union.U2 else assign_union.U1 end as assign_grandparent return assign_grandparent";
        Long rn = 0L;
        try
        {
            rn = Long.parseLong(gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n")[0]);
        }
        catch(Exception e){}
        
        return rn;
    }
}
