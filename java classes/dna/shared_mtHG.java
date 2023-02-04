/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class shared_mtHG {
    @UserFunction
    @Description("Finds shared_macthes who also share mt-haplogroup")

    public String shared_mt_haplogroup(
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
        //shared_mtHG

        
        String cq = "MATCH p=(m1)-[r:shared_mtHG]-(m2) with case when m1.RN >0 then '*' + m1.fullname else m1.fullname end  as m1,case when m2.RN>0 then '*' + m2.fullname else m2.fullname end as m2,r with r,case when m1<m2 then m1  else m2  end as match1,case when m1<m2 then m2 else m1 end as match2 RETURN distinct match1,match2,r.mtHG as shared_mt_haplogroup";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, gen.neo4jlib.neo4j_info.project +  "_shared_mt_haplogroups", "matches", 1, "", "", "", true, "UDF: \nreturn gen.dna.shared_mt_haplogroup()\n\nThis report lists shared autosomal DNA matches who also share a mitochondrial haplogroup.\nThis narrows down the list of mt-DNA matches but may also exclude relevant mt-DNA match wjho are too remote for at-DNA matching.", true);
        return "";
    }
}
