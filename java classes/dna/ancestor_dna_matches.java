/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
import gen.neo4jlib.neo4j_info;

public class ancestor_dna_matches {
       @UserFunction
        @Description("LIsts ancestor's descendants who have DNA test results.")
        
    public String ancestor_descendants_with_dna_test(
        @Name("rn") 
            Long rn
//        @Name("rn2") 
//            Long rn2
  )
    {
         { 
        
        String r = ancestor_descendant_matches(rn); 
        return r;
            }
     }
    
    
    public static String ancestor_descendant_matches(Long rn) {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
     int ct=1;
        String cq = "match (n:Person)-[[z:Gedcom_DNA]]->(m) with collect(m.RN) + collect(n.RN) as DM optional match path=(p:Person{RN:" + rn + "})<-[[:father|mother*0..15]]-(q:Person) where q.RN in DM with p,path,collect(last(nodes(path))) as cEnds optional match (q:Person)-[[r:Gedcom_DNA]]->(s:Person) where q in cEnds with r,p,[[m in cEnds|m.fullname + gen.neo4jlib.RNwithBrackets(m.RN)] as E,[[n in nodes(path)|n.fullname +  gen.neo4jlib.RNwithBrackets(n.RN) + ' (' + left(n.BD,4) + '-' +  left(n.DD,4) + ')  ' ]] as N return distinct p.fullname + gen.neo4jlib.RNwithBrackets(p.RN) as MRCA, E as Descendant_DNA_Tester,size(N) as generations, N as Path_to_Descendant_Tester";
       try{
        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq,"descendant_matches","Descendants", ct, "2:10;3:75", "2:####;2:####", "", false,"UFD: gen.dna.ancestor_descendants_with_dna_test(" + rn + ")\n\ncypher query\n" + cq,true);
        ct = ct +1;

try{        cq = "match (n:Person)-[[z:Gedcom_DNA]]->(m) with collect(m.RN) + collect(n.RN) as DM optional match path=(p:Person{RN:" + rn + "})<-[[:father|mother*0..99]]-(q:Person) where q.RN in DM with p,path,collect(last(nodes(path))) as cEnds optional match (q:Person)-[[r:Gedcom_DNA]]->(s:Person) where q in cEnds with r,p,[[m in cEnds|m.fullname]] as E with apoc.coll.sort(apoc.coll.flatten(collect (distinct E))) as desc_tester match (m1:DNA_Match)-[[rs:match_segment]]->(s:Segment) where rs.p in desc_tester and rs.m_anc_rn=" + rn + " with s,collect(distinct m1.fullname) as propositi,collect(distinct rs.m) as matches return s.chr as chr,s.strt_pos as strt_pos,s.end_pos as end_pos,gen.tgs.segment_tgs(s.Indx,'" + gen.neo4jlib.neo4j_info.project + "') as tg,size(propositi) as ct,propositi,size(matches) as ct2,matches order by s.chr,s.strt_pos,s.end_pos";
        gen.excelLib.queries_to_excel.qry_to_excel(cq,"descendant_segs","segments", ct, "2:10;3:75", "0:##;1:###,###,###;2:###,###,###", excelFile, false,"UFD: gen.dna.ancestor_descendants_with_dna_test(" + rn + ")\n\ncypher query\n" + cq,false);
ct = ct+1;
}
catch (Exception e) {return cq + "\n\n" +  e.getMessage();}

        
  try{      cq = "match (n:Person)-[[z:Gedcom_DNA]]->(m) with collect(m.RN) + collect(n.RN) as DM optional match path=(p:Person{RN:" + rn + "})<-[[:father|mother*0..15]]-(q:Person) where q.RN in DM with p,path,collect(last(nodes(path))) as cEnds optional match (q:Person)-[[r:Gedcom_DNA]]->(s:Person) where q in cEnds with r,p,[[m in cEnds|m.fullname]] as E with apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect (distinct E)))) as desc_tester match (m1:DNA_Match)-[[rs:match_tg]]->(t:tg) where rs.p in desc_tester and rs.m_anc_rn=" + rn + " with t,apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect(distinct m1.fullname) + collect(distinct rs.m)))) as matches,apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect(distinct m1.RN) + collect(distinct rs.m_rn)))) as rns return t.tgid as tgid,t.chr as chr,t.strt_pos as strt_pos,t.end_pos as end_pos,count(matches) as ct,matches,rns order by t.chr,t.strt_pos,t.end_pos";
        gen.excelLib.queries_to_excel.qry_to_excel(cq,"descendant_tgs","tgs", ct, "2:10;3:75", "0:###;1:####;2:###,###,###;3:###,###,###", excelFile, true,"UFD: gen.dna.ancestor_descendants_with_dna_test(" + rn + ")\n\ncypher query\n" + cq + "\n\nUse this long runtime query to get the MRCAs:\nmatch (n:Person)-[[z:Gedcom_DNA]]->(m) with collect(m.RN) + collect(n.RN) as DM optional match path=(p:Person{RN:" + rn + "})<-[[:father|mother*0..15]]-(q:Person) where q.RN in DM with p,path,collect(last(nodes(path))) as cEnds optional match (q:Person)-[[r:Gedcom_DNA]]->(s:Person) where q in cEnds with r,p,[[m in cEnds|m.fullname]] as E with apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect (distinct E)))) as desc_tester match (m1:DNA_Match)-[[rs:match_tg]]->(t:tg) where rs.p in desc_tester and rs.m_anc_rn=" + rn + " with t,apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect(distinct m1.fullname) + collect(distinct rs.m)))) as matches,apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect(distinct m1.RN) + collect(distinct rs.m_rn)))) as rns return t.tgid as tgid,t.chr as chr,t.strt_pos as strt_pos,t.end_pos as end_pos,matches,collect(gen.rel.mrca_from_cypher_list(rns,15)) as mrcas order by t.chr,t.strt_pos,t.end_pos" , true);
  }catch (Exception ex) {return  cq + "\n\n" +   ex.getMessage();}
  
        
        return excelFile;
       }
       catch (Exception e)
       {return "Error. Does the person whose RN you entered have descendants who have DNA test results loaded?";}

    }
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
}
