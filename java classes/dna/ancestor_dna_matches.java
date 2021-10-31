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
     
        String cq = "match (n:Person)-[z:Gedcom_DNA]->(m) with collect(m.RN) + collect(n.RN) as DM optional match path=(p:Person{RN:" + rn + "})<-[:father|mother*0..99]-(q:Person) where q.RN in DM with p,path,collect(last(nodes(path))) as cEnds optional match (q:Person)-[r:Gedcom_DNA]->(s:Person) where q in cEnds with r,p,[m in cEnds|m.fullname + gen.neo4jlib.RNwithBrackets(m.RN)] as E,[n in nodes(path)|n.fullname +  gen.neo4jlib.RNwithBrackets(n.RN) + ' (' + left(n.BD,4) + '-' +  left(n.DD,4) + ')  ' ] as N return distinct p.fullname + gen.neo4jlib.RNwithBrackets(p.RN) as MRCA, E as Descendant_DNA_Tester,size(N) as generations, N as Path_to_Descendant_Tester";
       
        String q = gen.excelLib.queries_to_excel.qry_to_excel(cq,"descendant_matches","Descendants", 1, "2:10;3:75", "2:####;2:####", "", true);
        return q;
    }
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
}
