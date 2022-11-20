/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.quality;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class compared_match_methods {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String match_method_concordance(
  )
   
         { 
             
        String s = get_concordance();
            return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_concordance() 
    {
        String cq ="MATCH p=(m1:DNA_Match)-[[r:shared_match|match_by_segment]]-(m2:DNA_Match) where r.cm>7 with m1,m2,sum(case when type(r)='shared_match' then 1 else 0 end) as sm, sum(case when type(r)='match_by_segment' then 1 else 0 end) as mbs with m1,m2,case when sm>0 and mbs>0 then 'Y' else 'N' end as both, case when sm>0 then 'Y' else 'N' end as sm ,case when mbs>0 then 'Y' else 'N' end as mbs return sm as shared_match,mbs as match_by_segment,both,count(*)/2 as ct order by ct desc";
        String ex = gen.excelLib.queries_to_excel.qry_to_excel(cq, "match_methods", "concordance", 1, "", "3:#,###,###", "", false,"UDF:\nreturn gen.quality.match_method_concordance()\n\nCypher query:\n " + cq, false);
        
        cq ="MATCH path=(m1:DNA_Match)-[r1:shared_match]-(mc:DNA_Match)-[r2:match_by_segment{p:m2.fullname,m:m1.fullname}]-(m2:DNA_Match) where m1.fullname=m2.fullname with m1.fullname as match1,mc.fullname as match2,r1.cm as sm_cm,apoc.math.round(r2.cm,2) as mbs_cm,r1.snp_ct as sm_snp,r2.snp_ct as mbs_snp with match1,match2,sm_cm,mbs_cm,sm_snp,mbs_snp, abs(sm_cm - mbs_cm) as cm_diff, abs(sm_snp - mbs_snp) as snp_diff with match1,match2,sm_cm,mbs_cm,cm_diff where cm_diff>0 or snp_diff>0 return match1,match2,sm_cm,mbs_cm,cm_diff";
        ex = gen.excelLib.queries_to_excel.qry_to_excel(cq, "match_methods_diff", "disconcordance", 2, "", "3:#,###,###", ex, true,"UDF:\nreturn gen.quality.match_method_concordance()\n\nCypher query:\n " + cq + "\n\nIf no rows are returned, it indicates complete concurraance between the two match methods when they are both applicable to the two matches.\nThis does not address the scenario where the two methods create distinct and non overlapping relationships (see the first worksheet).", false);
        
        return "completed";
    }
}
