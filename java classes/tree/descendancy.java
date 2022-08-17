/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tree;

import gen.neo4jlib.neo4j_qry;
import java.util.Map;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class descendancy {
    @UserFunction
    @Description("Descendant tree is ORDPATH order")

    public String descendancy_tree(
        @Name("rn") 
            Long rn
  )
   
         { 
             
        String m = get_tree(rn);
         return m;
            }

    
     public String get_tree(Long rn) 
    {
        String cq = "match path=(p:Person{RN:" + rn + "})<-[[:father|mother*0..15]]-(q:Person) with [[m in nodes(path)|m.fullname + gen.neo4jlib.RNwithBrackets(m.RN)]] as E,[[m in nodes(path)|m.RN]] as R,collect(last(nodes(path))) as cEnds with [[m in cEnds|m.fullname + gen.neo4jlib.RNwithBrackets(m.RN)]] as descendant,R,E,gen.graph.get_ordpath(R) as op, size(R) as gen,[[m in cEnds|m.RN]] as mRN optional match (mt:DNA_Match) where mt.RN in mRN with apoc.text.lpad('',(gen-1)*3,'.') + descendant as descendant,gen,R as rn_list,E as paths,op,mt,mRN return descendant,case when mt.fullname is not null then 'Y' else '~' end as DNA_Match,gen,rn_list,paths order by op";
        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "descendancy_tree", "tree", 1, "", "", "", false,"UDF:\nreturn gen.tree.descendancy_tree(" + rn + ")\n\nCypher query:\n" + cq + "\n\nIf you want to see the ORDPATH field and how it works, use this query:\nmatch path=(p:Person{RN:" + rn + "})<-[[[[:father|mother*0..15]]]]-(q:Person) with [[[[m in nodes(path)|m.fullname + gen.neo4jlib.RNwithBrackets(m.RN)]]]] as E,[[[[m in nodes(path)|m.RN]]]] as R,collect(last(nodes(path))) as cEnds with [[[[m in cEnds|m.fullname + gen.neo4jlib.RNwithBrackets(m.RN)]]]] as descendant,R,E,gen.graph.get_ordpath(R) as op, size(R) as gen return descendant,op as ORDPATH,gen,R as rn_list,E as paths order by op", true);
        
        cq = "match path=(p:Person{RN:" + rn + "})<-[[:father|mother*0..15]]-(q:Person) with [[m in nodes(path)|m.fullname + gen.neo4jlib.RNwithBrackets(m.RN)]] as E,[[m in nodes(path)|m.RN]] as R,collect(last(nodes(path))) as cEnds with [[m in cEnds|m.fullname + gen.neo4jlib.RNwithBrackets(m.RN)]] as descendant,R,E,gen.graph.get_ordpath(R) as op, size(R) as gen,[[m in cEnds|m.RN]] as mRN optional match (mt:DNA_Match) where mt.RN in mRN with collect (mt.RN) as mRNs unwind mRNs as x call { with x,mRNs match (m1:DNA_Match{RN:x})-[[rm:shared_match]]-(m2:DNA_Match) where m2.RN is not null and m1<>m2 with m1.fullname  + ' ⦋' + m1.RN + '⦌' as match,apoc.coll.sort(collect(distinct case when m2.RN in mRNs then '*^' + + m2.fullname else m2.fullname end + ' ⦋' + m2.RN + '⦌' + ' {' + rm.rel + ': cm ' + rm.cm + ': cor' + rm.cor + '}')) as matches return match,matches } return match,size(matches) as ct, matches as matches_from_GEDCOM order by ct desc,match";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "descendancy_tree", "descentant_known_matches", 1, "", "1:###;2:###", excelFile, true,"UDF:\nreturn gen.tree.descendancy_tree(" + rn + ")\n\nCypher query:\n" + cq + "\n\nUse this report to recruit new family members to your project!\nThose with *^ are in the project; others have DNA results and are in the GEDCOM and curated file but are not in the project.\n\nEach descendant of the common ancestor is shown with his/her DNA matches to others in the GEDCOM and curated file\nAlso shown are the known matches relationship, shared cm and coeficient of relationship to the match.", true);
        return "completed";    
    }
}
