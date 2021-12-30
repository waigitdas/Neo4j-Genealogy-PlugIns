/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
import gen.neo4jlib.neo4j_info;
import gen.neo4jlib.neo4j_qry;

public class mrca_set_link_property {
   @UserFunction
   @Description("Will not run on its own. Access this by running gen.tgs.setup_tg_environmen which uses it. This function sets the ancestor_rn property in DNA_Match, Kit and Person nodes if they are in the direct line to the designated common ancestor. Erases prior data, so only the most recent run of this UDF is applied. This enhancement enables analytics filtered for and limited to these descendants of the common ancestor.")

  public String mrca_link_property(
        @Name("ancestor_rn") 
            Long ancestor_rn
  )
    {
        { 
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String s = set_property(ancestor_rn);
        return s;
            }
    }
    
    public String set_property(Long ancestor_rn) {
        //create indices
        gen.neo4jlib.neo4j_qry.CreateIndex("DNA_Match", "ancestor_rn");
        gen.neo4jlib.neo4j_qry.CreateIndex("Person", "ancestor_rn");
        gen.neo4jlib.neo4j_qry.CreateIndex("Kit", "ancestor_rn");

        gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("match_segment", "p_anc_rn");
        gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("match_segment", "m_anc_rn");
        gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("match_segment", "p_rn");
        gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("match_segment", "m_rn");
        
//re-set existing property
        gen.neo4jlib.neo4j_qry.qry_write("match (m:DNA_Match) remove m.ancestor_rn ");
       gen.neo4jlib.neo4j_qry.qry_write("match (m:Person) remove m.ancestor_rn ");
        gen.neo4jlib.neo4j_qry.qry_write("match (m:Kit) remove m.ancestor_rn ");
        
        gen.neo4jlib.neo4j_qry.qry_write("match (m:DNA_Match)-[r:match_segment]-() remove r.p_anc_rn");
        gen.neo4jlib.neo4j_qry.qry_write("match (m:DNA_Match)-[r:match_segment]-() remove r.m_anc_rn");
        gen.neo4jlib.neo4j_qry.qry_write("match (m:DNA_Match)-[r:match_segment]-() remove r.p_rn");
        gen.neo4jlib.neo4j_qry.qry_write("match (m:DNA_Match)-[r:match_segment]-() remove r.m_rn");

        //set node property with new common ancestor phasing
         gen.neo4jlib.neo4j_qry.qry_write("match (p1:Person)-[r:father|mother*0..15]->(p2:Person{RN:" + ancestor_rn + "}) set  p1.ancestor_rn=case when p2 is not null then " + ancestor_rn + " else 0 end");
        gen.neo4jlib.neo4j_qry.qry_write("match (p:Person{ancestor_rn:" + ancestor_rn + "})-[r:Gedcom_DNA]-(m:DNA_Match) set m.ancestor_rn=" + ancestor_rn );
        gen.neo4jlib.neo4j_qry.qry_write("match (p:Person{ancestor_rn:" + ancestor_rn + "})-[r:Gedcom_Kit]-(k:Kit) set k.ancestor_rn=" + ancestor_rn );
        
        //set relationship property
        
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (m1:DNA_Match)-[r:match_segment]->() where r.cm>=7 and r.snp_ct>=500 and m1.fullname=r.p and m1.RN is not null set r.p_rn=m1.RN");
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (m1:DNA_Match)-[r:match_segment]->() where r.cm>=7 and r.snp_ct>=500 and m1.fullname=r.p and m1.ancestor_rn is not null set r.p_anc_rn = m1.ancestor_rn");
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (m1:DNA_Match)-[r:match_segment]->() where r.cm>=7 and r.snp_ct>=500 and r.m is not null  match (m2:DNA_Match) where m2.fullname=r.m and m2.RN is not null set r.m_rn=m2.RN");
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (m1:DNA_Match)-[r:match_segment]->() where r.cm>=7 and r.snp_ct>=500 and r.m is not null  match (m2:DNA_Match) where m2.fullname=r.m and m2.ancestor_rn is not null set r.m_anc_rn=m2.ancestor_rn");
        
//        gen.neo4jlib.neo4j_qry.qry_write("MATCH (m1:DNA_Match)-[r:match_segment]->() with m1,r match (m2:DNA_Match) where r.p=m1.fullname and m1.ancestor_rn is not null and r.m=m2.fullname and m2.ancestor_rn is not null set r.m_anc_rn=m2.ancestor_rn, r.p_anc_rn=m1.ancestor_rn");
//        //gen.neo4jlib.neo4j_qry.qry_write("MATCH (m1:DNA_Match)-[r:match_segment]->() with r match (m2:DNA_Match) where m2.fullname = r.m and m2.ancestor_rn is not null set r.m_anc_rn = m2.ancestor_rn");
//        gen.neo4jlib.neo4j_qry.qry_write("MATCH (m1:DNA_Match)-[r:match_segment]->() with m1,r match (m2:DNA_Match) where r.p=m1.fullname and  m1.RN is not null and r.m=m2.fullname and m2.RN is not null set r.m_rn=m2.RN, r.p_rn=m1.RN");
//        //gen.neo4jlib.neo4j_qry.qry_write("MATCH (m1:DNA_Match)-[r:match_segment]->() with r match (m2:DNA_Match) where m2.fullname = r.p and m2.RN is not null set r.p_rn = m2.RN");
        

        //add genealogical relationship to edges to speed queries
        add_rel_property rr = new gen.rel.add_rel_property();
        rr.add_relationship_property(); 
        
    
        return "Completed";
    } 
}
