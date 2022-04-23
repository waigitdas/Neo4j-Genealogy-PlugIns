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

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_tree(Long rn) 
    {
        String cq = "match path=(p:Person{RN:" + rn + "})<-[[:father|mother*0..15]]-(q:Person) with [[m in nodes(path)|m.fullname + gen.neo4jlib.RNwithBrackets(m.RN)]] as E,[[m in nodes(path)|m.RN]] as R,collect(last(nodes(path))) as cEnds with [[m in cEnds|m.fullname + gen.neo4jlib.RNwithBrackets(m.RN)]] as descendant,R,E,gen.graph.get_ordpath(R) as op, size(R) as gen return apoc.text.lpad('',(gen-1)*3,'.') + descendant as descendant,gen,R as rn_list,E as paths order by op";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "descendancy_tree", "tree", 1, "", "", "", true,"UDF:\nreturn gen.tree.descendancy_tree(" + rn + ")\n\nCypher query:\n" + cq + "\n\nIf you want to see the ORDPATH field and how it works, use this query:\nmatch path=(p:Person{RN:" + rn + "})<-[[:father|mother*0..15]]-(q:Person) with [[m in nodes(path)|m.fullname + gen.neo4jlib.RNwithBrackets(m.RN)]] as E,[[m in nodes(path)|m.RN]] as R,collect(last(nodes(path))) as cEnds with [[m in cEnds|m.fullname + gen.neo4jlib.RNwithBrackets(m.RN)]] as descendant,R,E,gen.graph.get_ordpath(R) as op, size(R) as gen return descendant,op as ORDPATH,gen,R as rn_list,E as paths order by op", true);
        return "completed";    
    }
}
