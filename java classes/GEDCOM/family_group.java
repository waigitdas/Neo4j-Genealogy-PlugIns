/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.gedcom;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class family_group {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String fgs(
        @Name("rn") 
            Long rn
  )
   
         { 
             
        create_fgs(rn);
         return "";
            }

    
    
    public static void main(String args[]) {
        String s = create_fgs(57L);
        System.out.println(s);
    }
    
     public static String create_fgs(Long rn) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        String cq = "with 53 as rn match(u:Union) where u.U1=rn or u.U2=rn with rn,u order by u.UD with rn,collect(u) as uu, gen.gedcom.person_from_rn(rn,false) as s unwind uu as x call { with x,rn match(us:Union{uid:x.uid}) with x,rn,case when us.U1=rn then gen.gedcom.person_from_rn(us.U2,false) else gen.gedcom.person_from_rn(us.u1,false) end as sp with x,rn,sp match (p2:Person) where p2.uid=x.uid with x,rn,sp,p2 order by left(p2.BD,4) with x,rn,sp,collect(p2) as cc unwind cc as ch call { with ch return gen.gedcom.person_from_rn(ch.RN,false) as kid } with x,rn,sp,collect(kid) as kids return x.uid as uid,x.UDGed as ud, sp,kids } return s as proband,sp as spouse,ud as union_date,size(kids) as ct, kids";
        String c[] = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n");
        String s = "";
        for (int i=0; i<c.length;i++)
        {
            s = s + c[i] + "\n";
        }
        
        return s;
    }
}
