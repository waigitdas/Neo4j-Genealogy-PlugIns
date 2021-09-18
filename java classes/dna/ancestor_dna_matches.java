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


public class ancestor_dna_matches {
       @UserFunction
        @Description("LIsts ancestor's descendants who have DNA test results.")
        
    public String ancestor_descendants_with_dna_test(
        @Name("rn1") 
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
        gen.neo4jlib.neo4j_info.neo4j_var();
        //String cq = "match (n:Person)-[z:Gedcom_DNA]->(m)   with collect(m.RN) + collect(n.RN) as DM optional match path=(p:Person{RN:" + rn + "})<-[:father|mother*0..99]-(q:Person) where q.RN in DM with p,path,collect(last(nodes(path))) as cEnds optional match (q:Person)-[r:Gedcom_DNA]->(s:Person) where q in cEnds with r,p,[m in cEnds|m.fullname + ' [' + m.RN + ']'] as E,[n in nodes(path)|n.fullname + ' : ' + right(n.BDGed,4) + ' : ' +  n.BP + ' : ' + n.DP + ' [' + n.RN + '] '] as N return distinct p.fullname + ' [' + p.RN + ']' as MRCA, E as Match,size(N) as ct, N as Path_to_Descendant_Match";
       
        String cq = "match (n:Person)-[z:Gedcom_DNA]->(m)   with collect(m.RN) + collect(n.RN) as DM optional match path=(p:Person{RN:" + rn + "})<-[:father|mother*0..99]-(q:Person) where q.RN in DM with p,path,collect(last(nodes(path))) as cEnds optional match (q:Person)-[r:Gedcom_DNA]->(s:Person) where q in cEnds with r,p,[m in cEnds|m.fullname + ' [' + m.RN + ']'] as E,[n in nodes(path)|n.fullname + ' \u298B' + n.RN + '\u298C (' + left(n.BD,4) + '-' +  left(n.DD,4) + ')  ' ] as N return distinct p.fullname + ' [' + p.RN + ']' as MRCA, E as Match,size(N) as ct, N as Path_to_Descendant_Match";
       
        gen.excelLib.queries_to_excel.qry_to_excel(cq,"descendant_matches","Descenants", 1, "", "2:####", "", true);
        return "completed";
    }
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
}
