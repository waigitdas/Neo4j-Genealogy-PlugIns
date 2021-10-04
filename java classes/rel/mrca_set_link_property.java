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
   @Description("sets property in matches, kits and person with who are in the direct line to a common ancestor. Erases prior data, so only the most recent run of this UDF is applied")

  public static String mrca_link_property(
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
    
    public static String set_property(Long ancestor_rn) {
        //create indices
        gen.neo4jlib.neo4j_qry.CreateIndex("DNA_Match", "ancestor_rn");
        gen.neo4jlib.neo4j_qry.CreateIndex("Person", "ancestor_rn");
        gen.neo4jlib.neo4j_qry.CreateIndex("Kit", "ancestor_rn");

        //re-set existing property
        gen.neo4jlib.neo4j_qry.qry_write("match (m:DNA_Match) remove m.ancestor_rn ");
        gen.neo4jlib.neo4j_qry.qry_write("match (m:Person) remove m.ancestor_rn ");
        gen.neo4jlib.neo4j_qry.qry_write("match (m:Kit) remove m.ancestor_rn ");
        
        //set property with new common ancestor phasing
         gen.neo4jlib.neo4j_qry.qry_write("match (p1:Person)-[r:father|mother*0..15]->(p2:Person{RN:" + ancestor_rn + "}) set  p1.ancestor_rn=case when p2 is not null then " + ancestor_rn + " else 0 end");
        gen.neo4jlib.neo4j_qry.qry_write("match (p:Person{ancestor_rn:" + ancestor_rn + "})-[r:Gedcom_DNA]-(m:DNA_Match) set m.ancestor_rn=" + ancestor_rn );
        gen.neo4jlib.neo4j_qry.qry_write("match (p:Person{ancestor_rn:" + ancestor_rn + "})-[r:Gedcom_Kit]-(k:Kit) set k.ancestor_rn=" + ancestor_rn );
         
        return "Completed";
    } 
}
