/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.endogamy;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class topol_mrca_2_rns {
    @UserFunction
    @Description("Tpology of graph to mrcas of two persons.")

    public String topology_2_person_mrcas(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
   
         { 
             
        String s = get_topol2(rn1,rn2);
         return s;
            }

    
    
    public static void main(String args[]) {
        get_topol2(4L,5280L);
    }
    
     public static String get_topol2(Long rn1,Long rn2) 
    {
        int ct = 1;
        String cq = "match path= (p1:Person{RN:" + rn1 + "})-[[r1:father|mother*0..15]]->(mrca:Person)<-[[r2:father|mother*0..15]]-(p2:Person{RN:" + rn2 + "}) with [[x in nodes(path)|x.RN]] as rns, [[y in nodes(path)|gen.gedcom.person_from_rn(y.RN, true)]] as names return rns as path_nodes, names, gen.graph.get_ordpath(rns) as op order by op";
        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "topology_mrca_rn1_" + rn1 + "_rn2_" + rn2 + "_", "paths", ct, "", "", "", false, "cypher query:\n" + cq + "\n\nquery to visualize in Neo4j browser:\nmatch p= (p1:Person{RN:" + rn1 + "})-[[r1:father|mother*0..15]]->(mrca:Person)<-[[r2:father|mother*0..15]]-(p2:Person{RN:" + rn2 + "}) return p", false);
        ct = ct + 1 ;

        cq = "match path= (p1:Person{RN:" + rn1 + "})-[[r1:father|mother*0..15]]->(mrca:Person)<-[[r2:father|mother*0..15]]-(p2:Person{RN:" + rn2 + "}) with apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect([x in nodes(path)|x.RN ])))) as rns with size(rns) as ct,rns unwind rns as z call { with z,rns match (pp:Person{RN:z})-[[rp:father|mother]]->(ppa:Person) with z,rns,apoc.coll.intersection(rns,collect(ppa.RN)) as split with z,rns,case when size(split)=2 then 1 else 0 end as split with z,rns,split match (ppp:Person{RN:z})<-[[rp2:father|mother]]-(pppa:Person) with rns,split,apoc.coll.intersection(rns,collect(pppa.RN)) as merged with split,case when size(merged)>1 then 1 else 0 end as merged return split,merged } with z, split,merged where split=1 or merged=1 return z,split,merged";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "topology", "merge_split", 1, "", "0:######;1:#####;2:######", excelFile, true, "cypher query:\n" + cq + "\n\nEach row represents a split and/or merge in the paths to the common ancestors of the two individuals.", false);
        ct = ct + 1 ;

        return "completed";
    }
}
