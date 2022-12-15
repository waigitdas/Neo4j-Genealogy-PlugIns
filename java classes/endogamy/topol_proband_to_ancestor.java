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


public class topol_proband_to_ancestor {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String topology_proband_to_ancestor(
        @Name("proband_rn") 
            Long proband_rn,
        @Name("anc_rn") 
            Long anc_rn
  )
   
         { 
             
        String s = get_topol(proband_rn,anc_rn);
         return s;
            }

    
    
    public static void main(String args[]) {
        get_topol(4L,2096L);
    }
    
     public static String get_topol(Long rn1,Long rn2) 
    {
        String proband = gen.gedcom.get_person.getPersonFromRN(rn1, true);
        int ct = 1;
        String cq = "match path=(p:Person{RN:" + rn1 + "})-[[r:father|mother*0..25]]->(a:Person{RN:" + rn2 + "}) with [[x in nodes(path)|x.RN]] as rns, [[y in nodes(path)|gen.gedcom.person_from_rn(y.RN, true)]] as names return rns as path_nodes, names";
        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "topology_proband_" + rn1 + "_anc_" + rn2 + "_", "paths", ct, "", "", "", false, "cypher query:\n" + cq + "\n\nquery to visualize in Neo4j browser:\nmatch path=(p:Person{RN:" + rn1 + "})-[[r:father|mother*0..25]]->(a:Person{RN:" + rn2 + "}) return path", false);
        ct = ct + 1 ;

        cq = "match path=(p:Person{RN:"+ rn1 + "})-[[r:father|mother*0..25]]->(a:Person{RN:" + rn2 + "}) with apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect([[x in nodes(path)|x.RN]])))) as rns with size(rns) as ct,rns unwind rns as z call { with z,rns match (pp:Person{RN:z})-[[rp:father|mother]]->(ppa:Person) with z,rns,apoc.coll.intersection(rns,collect(ppa.RN)) as split with z,rns,case when size(split)=2 then true else false end as split with z,rns,split match (ppp:Person{RN:z})<-[[rp2:father|mother]]-(pppa:Person) with rns,split,apoc.coll.intersection(rns,collect(pppa.RN)) as merged,pppa.coi as coi,pppa.coi_gen as coi_gen with split,case when size(merged)>1 then true else false end as merged,coi,coi_gen return split,merged,coi,coi_gen } with z, split,merged,coi,coi_gen where split=true or merged=true return z as RN, gen.gedcom.person_from_rn(z,true)as path_person,split,merged,coi, coi_gen";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "topology1", "merge_split", 1, "", "0:#####;1:######", excelFile, true, "vypher query:\n" + cq + "\n\nProband: " + proband + "\nEach row represents a split and/or merge in the paths between the proband and the ancestor.", false);
        ct = ct + 1 ;

        return "completed";
    }
}
