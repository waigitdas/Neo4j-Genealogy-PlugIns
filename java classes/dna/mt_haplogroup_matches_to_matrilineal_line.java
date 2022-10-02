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


public class mt_haplogroup_matches_to_matrilineal_line
 {
    @UserFunction
    @Description("Finds matches to descendants of matrilineal ancestor who share  ancestors mt-haplogroup")

    public String mt_haplogroup_matrilineal_Descendant_matches(
        @Name("matrilineal_ancestor_rn") 
            Long matrilineal_ancestor_rn,
        @Name("mt_haplogroup") 
            String mt_haplogroup
  )
   
         { 
             
        String s = get_matches(matrilineal_ancestor_rn,mt_haplogroup);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_matches(Long rn,String hg) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        String anc_name =  gen.gedcom.get_family_tree_data.getPersonFromRN(rn, true) ;
        String cq = "with '" + hg + "' as hg MATCH p=(m1:DNA_Match{mtHG:hg})-[[r:match_by_segment]]->(m2:DNA_Match) with m1,m2,r match(p1:Person{RN:m2.RN})-[[rf:father|mother*0..99]]->(p2:Person{RN:" + rn + "}) with m1.fullname as match1,collect(distinct m2.fullname) as match2,m1.mtHG as mt_haplogroup return match1 as mtHG_match,mt_haplogroup,match2 as autosomal_known_matches_descended_from_matriarch";
        
       gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project +  "_mt_matrilineal_line_matches", "matrilineal_matches", 1, "","1:####;3:####;4:#####", "", true,"udf\nreturn gen.dna.mt_haplogroup_matrilineal_Descendant_matches(" + rn + ", '" + hg + "')\n\ncyspher query\n" + cq + "\n\nThis report links all matches with the specified mt-haplogroup to descendants of a specified direct female ancestor known to have this haplogroup.\nThe known match need not be a matrilineal descendant of the matriarch.\n\nreference:\nmt_haplogroup_matrilineal_Descendant_matches.\n\nTarget matilineal ancestor: " + anc_name + ".", false);
        

        return "complete";
    }
}
