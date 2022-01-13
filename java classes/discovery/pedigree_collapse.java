/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.discovery;

import gen.neo4jlib.neo4j_qry;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class pedigree_collapse {
    @UserFunction
    @Description("Detects pedigree collapse and returns ancestors at the end of a duplicated branch.")

    public String discover_pedigree_collapse(
        @Name("propositus_rn") 
            Long propositus_rn
  )
   
         { 
             
        String s = find_collapse(propositus_rn);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String find_collapse(Long rn) 
    {
        String cq = "match path=(p1:Person)-[r1:father|mother*0..25]->(mrca:Person)<-[r2:father|mother*0..25]-(p2:Person) where p1.RN=" + rn + " and p2<>p1 with mrca, count(path) as ct with mrca,ct where ct>1 and mrca.uid=0 with mrca,gen.rel.relationship_from_RNs(" + rn + ",mrca.RN) as rels return mrca.fullname + ' ⦋' + mrca.RN + '⦌'  as ancestor,rels";
        try{
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "Pedigree_collapse", "duplicate_ancestors", 1, "", "", "", true, "cypher query:\n" +  cq, false);
        return "completed";
        }
        catch (Exception e){
            return "no pedigree collapse detected";
        }
    }
}
