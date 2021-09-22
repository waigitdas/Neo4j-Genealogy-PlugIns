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

public class mrca_set_link_property_ {
   @UserFunction
    @Description("sets property in matches, kits and person with who are in the direct line to a common ancestor. Erases prior data, so only the most recent run of this UDF is applied")

  public String mrca_link_property(
        @Name("ancestor_rn") 
            Long ancestor_rn
  )
    {
        { 
        gen.neo4jlib.neo4j_info.neo4j_var();
        String s = set_property(ancestor_rn);
        return s;
            }
    }
    
    public String set_property(Long ancestor_rn) {
        //create indices
//        gen.neo4jlib.neo4j_qry.CreateIndex("DNA_Match", "match_ancestor");
//        gen.neo4jlib.neo4j_qry.CreateIndex("Person", "person_ancestor");
//        gen.neo4jlib.neo4j_qry.CreateIndex("Kit", "kit_ancestor");
        //re-set existing property
        
//        gen.neo4jlib.neo4j_qry.qry_write("match (m:DNA_Match) remove m.match_ancestor ");
//        gen.neo4jlib.neo4j_qry.qry_write("match (m:Person) remove m.person_ancestor ");
//        gen.neo4jlib.neo4j_qry.qry_write("match (m:Kit) remove m.kit_ancestor ");
//        
        //set property with new common ancestor phasing
        gen.neo4jlib.neo4j_qry.qry_write("match (m:DNA_Match) where m.RN is not null with m, gen.rel.mrca_str(m.RN," + ancestor_rn + ") as mrca set m.match_ancestor=" + ancestor_rn );
        gen.neo4jlib.neo4j_qry.qry_write("match (m:Person) where m.kit is not null with m, gen.rel.mrca_str(m.RN," + ancestor_rn + ") as mrca set m.person_ancestor" + ancestor_rn );
        gen.neo4jlib.neo4j_qry.qry_write("match (m:Kit) where m.RN is not null with m, gen.rel.mrca_str(m.RN," + ancestor_rn + ") as mrca set m.kit_ancestor=" + ancestor_rn);           gen.neo4jlib.neo4j_qry.qry_write("match (m:DNA_Match) where m.RN >0 set m:DNA_Match_RN");
          gen.neo4jlib.neo4j_qry.qry_write("match (m:DNA_Match) where m.RN is not null with m , gen.rel.mrca_str(m.RN," + ancestor_rn + ") as mrca set m:Match_mrca");
          return "Completed";
    } 
}
