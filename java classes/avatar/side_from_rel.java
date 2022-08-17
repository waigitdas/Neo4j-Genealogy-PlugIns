/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.avatar;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class side_from_rel {
    @UserFunction
    @Description("Obsolete? Enhanced graph more efficient")

    public String parental_side_from_rel(
        @Name("rel_id") 
            Long rel_id,
        @Name("rn") 
            Long rn
  )
   
         { 
             
        String s = get_side(rel_id,rn);
         return s;
            }

    
    
    public static void main(String args[]) {
        String s = get_side(802798L, 210L);
        System.out.println(s);
    }
    
     public static String get_side(Long id,Long rn) 
    {
        String cq = "match()-[r]->() where id(r)=" + id + " return id(r) as id,r.p_rn as prn,r.p_side as ps,r.m_rn as mrn,r.m_side as ms";
        String c[]= gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n")[0].split(",");
        if(Long.parseLong(c[1])==rn) 
        {
            return translate(c[2].replace("\"","").strip());
        }
        else if (Long.parseLong(c[3])==rn)
        {
            return translate(c[4].replace("\"","").strip());
        }
        else {return "";}
     
    }
     
     public static String translate(String s)
     {
         if (s.equals("F")) {return "paternal";}
         else if (s.endsWith(("M"))) {return "maternal";}
         else if (s.replace("; ","").replace("\"", "").equals("MF") || s.replace(", ","").replace("\"", "").equals("FM") ) {return "both";}
         else {return "unknown";}
     }
}
