/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class shared_dna {
    @UserFunction
    @Description("Used match_by_segment edge property to retrieve shared cm between two persons")

    public String shared_cm(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
   
         { 
             
        String cm = sharedCM(rn1,rn2);
         return cm;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String sharedCM(Long rn1, Long rn2) 
    {
    
        String cmstr = gen.neo4jlib.neo4j_qry.qry_to_csv("MATCH p=(m1:DNA_Match{RN:" + rn1 + "})-[r:match_by_segment]->(m2:DNA_Match{RN:" + rn2 + "}) with toString(r.cm) as cm return cm");
        //double cmnbr = Double.parseDouble(cmstr[0].replace("\\",""));
        return cmstr.replace("\n","");
    }
}
