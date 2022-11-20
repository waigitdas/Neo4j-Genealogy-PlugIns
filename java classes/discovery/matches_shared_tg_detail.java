/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.discovery;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class matches_shared_tg_detail {
    @UserFunction
    @Description("Report of all matches who share a triangulation group with a set of known matches. The min_cluster_size is the number of matches required in the cluster. The list of matches is sorted, including an * which brings those descended from the common ancestor to the beginning of the list. This produces a very long reference listing that is too big for directing research but which may be handy to look at granular details when questions arise.")

    public String matches_with_shared_tgs_detailed(
        @Name("min_cluster_size") 
            Long min_cluster_size
  )
   
         { 
             
        get_matches(min_cluster_size);
         return "";
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String  get_matches(Long min_cluster_size) 
    {
        if(gen.neo4jlib.neo4j_info.tg_file.equals("")){return "";}
        
        String cq = "MATCH p=(m1:DNA_Match)-[r1:match_tg]->(t:tg)<-[[r2:match_tg]]-(m2:DNA_Match) where m1.fullname<m2.fullname with m1,m2,t where m1.RN is not null"
                + " with case when m1.ancestor_rn is not null then '*' + m1.fullname else m1.fullname end as fn,m2,t order by t.tgid with fn,m2,collect(distinct t) as tgs order by fn with m2,collect(distinct fn + ' (' + reduce(s = '', x IN tgs | s + x.tgid + ',') + ')') as Matches with m2,Matches where size(Matches)>" + min_cluster_size + "-1 RETURN m2.fullname as Match,size(Matches) as ct,Matches as Known_Persons_shared_tgs,m2.RN as match_rn order by match_rn,ct desc,Match";
        
        gen.excelLib.queries_to_excel.qry_to_excel(cq,"shared_tgs_detailed", "matches", 1, "", "", "", true, "UDF: return matches_with_shared_tgs_detailed(" + min_cluster_size + ")\n\n",true);
        return "completed";
    }
}
