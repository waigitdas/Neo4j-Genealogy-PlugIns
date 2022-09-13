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


public class Y_dna_asc_hapltree
{
    @UserFunction
    @Description("Enter haplogroup and returns tree")

    public String Y_haplotree_ascending(
        @Name("haplogroup") 
            String haplogroup
  )
   
         { 
             
        String s = get_Y_htree(haplogroup);
         return s;
            }
 
    public static void main(String args[]) {
        //get_tree("R-FTA80691");
    }
     public String get_Y_htree(String hg) 
    {
        String s = gen.neo4jlib.neo4j_qry.qry_str("MATCH p=(b1:block)-[r:blockchild*0..99]->(b2:block{name:'" + hg + "'}) with apoc.coll.reverse(collect((b1.name) + ' > ')) as hg   return hg");
        s = s.replace("\"","").replace("[[","").replace("]]","").replace(", ","");
        
        return s;
    }
}
