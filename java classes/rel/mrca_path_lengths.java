/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 *
 * @author david
 */
public class mrca_path_lengths {
    @UserFunction
    @Description("Returns string with number of mrcas and paths to lengths")

    public String mrca_path_len(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
   
         { 
             
        String s = get_mrca_path_len(rn1,rn2);
         return s;
            }

        public String get_mrca_path_len(Long rn1, Long rn2){
            String s =gen.neo4jlib.neo4j_qry.qry_to_csv("match path = (p:Person{RN:" + rn1 + "})-[r1:father|mother*0..15]->(mrca:Person)<-[r2:father|mother*0..15]-(b:Person{RN:" + rn2 + "})  return mrca.RN as mrca, size(r1) as path1,size(r2) as path2");
            
            //"match (p:Person{RN:11})<-[:father]-(c:Person) with p as p,collect(c.fullname) as c return p.RN,c");
                    
                    //"match (p:Person)where p.RN<10 return p.RN,p.fullname,p.BD");
                    
                    //"match path = (p:Person{RN:" + rn2 + "})-[r1:father|mother*0..15]->(mrca:Person)<-[r2:father|mother*0..15]-(b:Person{RN:" + rn2 + "})  return count(*) as ct,size(r1) as path1,size(r2) as path2");
           return s; 
        }
}
