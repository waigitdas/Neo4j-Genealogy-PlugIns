/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class limbs {
    @UserFunction
    @Description("Create a limb in a family line.")

    public String create_named_limb(
        @Name("anc_rn") 
            Long anc_rn,
        @Name("limb_name")
            String limb_name
  )
   
         { 
             
        create_limb(anc_rn,limb_name);
         return "";
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String create_limb(Long anc_rn,String name) 
    {
        String cq ="match path=(p:Person{RN:" + anc_rn + "})<-[:father|mother*0..15]-(q:Person)  set q.limb='" + name + "'";
        gen.neo4jlib.neo4j_qry.qry_write(cq);
        cq = "MATCH path=(p:Person)-[r:Gedcom_DNA]->(m:DNA_Match) where p.limb is not null set m.limb=p.limb";
        gen.neo4jlib.neo4j_qry.qry_write(cq);
        cq="match path=(p1:Person{RN:33454})MATCH p=(m:DNA_Match)-[r:match_segment]->(s:Segment) where m.limb is not null and r.cm>=7 and r.snp_ct>=500 with s as seg,apoc.coll.sort(collect(distinct m.limb)) as limb with seg,reduce(s='',x in limb|s + x ) as limb set seg.limb=limb";
        gen.neo4jlib.neo4j_qry.qry_write(cq);
        cq="MATCH path=(p:Person)-[r:person_mss]->(m:MSS) where p.limb is not null set m.limb=p.limb";
        gen.neo4jlib.neo4j_qry.qry_write(cq);
        
        return "created limb";
    }
}
