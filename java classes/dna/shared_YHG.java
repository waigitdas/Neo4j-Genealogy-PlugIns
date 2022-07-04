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


public class shared_YHG {
    @UserFunction
    @Description("Finds shared_macthes who also share Y-haplogroup")

    public String shared_Y_haplogroup(
  )
   
         { 
             
        String s = shared_hg();
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String shared_hg() 
    {
        String cqx = "match (Y:DNA_Match) with Y where Y.YHG is not null and 'Z'>=left(Y.YHG,1)>='A' with Y order by Y.YHG with collect (distinct Y.YHG) as mito unwind mito as x call {with x MATCH (m:DNA_Match)-[rs:shared_match]-(n:DNA_Match) where m.YHG = x and n.YHG = x and m.fullname<n.fullname merge (m)-[r:shared_YHG{YHG:x}]-(n)}";
        gen.neo4jlib.neo4j_qry.qry_write(cqx);
        
        String cq = "MATCH p=(m1)-[r:shared_YHG]-(m2) with case when m1.RN >0 then '*' + m1.fullname else m1.fullname end  as m1,case when m2.RN>0 then '*' + m2.fullname else m2.fullname end as m2,r with r,case when m1<m2 then m1  else m2  end as match1,case when m1<m2 then m2 else m1 end as match2 RETURN distinct match1,match2,r.YHG as shared_Y_haplogroup";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, gen.neo4jlib.neo4j_info.project +  "_shared_Y_haplogroups", "matches", 1, "", "", "", true, "UDF: \nreturn gen.dna.shared_Y_haplogroup()\n\nThis report lists shared autosomal DNA matches who also share a mitochondrial haplogroup.\nThis narrows down the list of Y-DNA matches but may also exclude relevant Y-DNA match wjho are too remote for at-DNA matching.\n\nThis function also creates a new shard_YHG relationship, which is done by this cypher query:\n" + cqx, true);
        return "";
    }
}
