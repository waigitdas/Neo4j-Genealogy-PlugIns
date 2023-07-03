/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.discovery;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class surname_knowledge {
    @UserFunction
    @Description("Finds high value surnames and adds surname nodes and match_surname relationships.")

    public String surname_enhancements(
//        @Name("rn1") 
//            Long rn1,
//        @Name("rn2") 
//            Long rn2
  )
   
         { 
             
        enhances_surnames();
         return "";
            }

    
    
    public static void main(String args[]) {
        enhances_surnames();
    }
    
     public static String enhances_surnames()
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        String cq = "MATCH (n:DNA_Match) where n.RN >0 and trim(n.surname)<>'?' with collect(distinct n.RN) as rns MATCH (p:Person)-[r:mother*4..10]-(a:Person) where p.RN in rns with apoc.coll.sort(collect (distinct a.surname)) as asn RETURN  asn";
        String matrilineal_surname_list = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n")[0].replace("[", "").replace("]","").replace("\"","");
       
        String[] ss = matrilineal_surname_list.split(",");
        String label = ss[0];
        String s = "";
        String cq1 = "";
        String cq2 ="";
        for (int i=0; i<ss.length; i++)
        {
            s = s + "'" + ss[i].toUpperCase().strip() + "'";
            if (i<ss.length-1){s = s + ",";}
        }
    
 
        
//        cq ="CALL db.index.fulltext.queryNodes('ancestor_surnames_names','" + matrilineal_surname_list + "') YIELD node, score WITH [" + s + "] as submitted,score,node.p as match, node.m as match_with_surnames, case when size(node.name)>20000 then left(node.name,200) + ' (truncated)' else node.name end as anc_names , apoc.coll.flatten(collect(split(toUpper(replace(node.name,' ','')),'/'))) as anc_list MATCH (m:DNA_Match{fullname:match})-[rs:match_by_segment]-(m2:DNA_Match{fullname:match_with_surnames}) with distinct m.fullname as source,case when apoc.coll.contains(submitted,toUpper(trim(m2.surname))) then 'x' else '~' end as own_surname, case when m.RN is null then '~' else m.RN end as source_rn, apoc.coll.intersection(anc_list,submitted) as found, match_with_surnames,rs.cm as cm,rs.seg_ct as segs, case when rs.rel is null then '~' else rs.rel end as rel,round(score,2) as score,anc_names as ancestor_list,submitted,anc_list where found<>[[]] match (n:DNA_Match{fullname:match_with_surnames}) with source,source_rn,found,score,match_with_surnames,case when n.YHG is null then '~' else n.YHG end as YHG, case when n.mtHG is null then '~' else n.mtHG end as mtHG,own_surname,cm,segs,rel,ancestor_list unwind found as fnd return fnd as found,source,match_with_surnames";
//        gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited(cq, "matrilineal_surnames.csv");
//        
        
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///matrilineal_surnames.csv' as line FIELDTERMINATOR '|' merge (sn:surname{surname:toString(line.found)}) set sn.type='matrilineal'");
        
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///matrilineal_surnames.csv' as line FIELDTERMINATOR '|' match (sn:surname{surname:toString(line.found)}) match(d:DNA_Match{fullname:toString(line.match_with_surnames)}) merge (d)-[r:match_surname{source:toString(line.source)}]->(sn)");
        
        
        return "";
    }
}
