/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.find_ancestors;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class no_parents {
    @UserFunction
    @Description("Clues regarding parents when unknown and in common with data loaded.")

    public String no_parent_known(
        @Name("proband_fullname") 
            String proband_fullname,
        @Name("min_cm2") 
            Long min_cm,
        @Name("max_cm")
            Long max_cm
  )
   
         { 
             
        no_parent_analytics(proband_fullname, min_cm, max_cm);
         return "";
            }

    
    
    public static void main(String args[]) {
        no_parent_analytics("David A Stumpf",50L,3600L);
    }
    
     public static String no_parent_analytics(String fullname, Long min_cm, Long max_cm)
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
    
        try{
        gen.neo4jlib.neo4j_qry.qry_write("CALL gds.graph.drop('icw')");
        }
        catch(Exception e){}
        
        //create virtual graph using cypher projection and return node and relationship counts
        String cqvg = "CALL gds.graph.project.cypher('icw','MATCH (m:DNA_Match) RETURN id(m) AS id', 'MATCH (m)-[[r:icw]]->(m2:DNA_Match) where " + max_cm + ">r.src_cm>" + min_cm + " RETURN id(m) AS source, id(m2) AS target, r.src_cm as weight',{readConcurrency: 4,validateRelationships:FALSE} ) YIELD graphName AS graph, nodeQuery, nodeCount AS nodes, relationshipQuery, relationshipCount AS rels return nodes, rels";
        String[] c = gen.neo4jlib.neo4j_qry.qry_to_csv(cqvg.replace("[[","[").replace("]]","]")).split("\n");
        
        //get counts
        String[] sc = c[0].split(",");
        String node_ct = sc[0];
        String rel_ct = sc[1];
        
        //set up reporting variables
        String excelFile = "";
         String cq = "";
        int ct = 1;
        
       //matches ordered by shared_cM       
       cq="MATCH p=(d:DNA_Match)-[r:match_segment]->(s:Segment) where r.p='" + fullname + "' and r.m=d.fullname and 7<r.cm with r.m as match,sum(r.cm) as shared_cM, sum(r.snp_ct) as snps optional match (f:fam_rel) where f.LowSharedCM<=shared_cM<=f.HighSharedCM with match,shared_cM,snps, f.relationship as rel order by f.MeanSharedCM desc with match, shared_cM,snps,collect(rel) as possible_rel with distinct match, shared_cM,snps,possible_rel optional match p2=(d1DNA_Match)-[ri:icw]->(d2:DNA_Match) where ri.kit_match=match return distinct  match,case when ri.kit_match is not null then 'X' else '~' end as icw_added, shared_cM, snps,possible_rel  order by shared_cM desc";

        
        excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "no_parent_analytics", "ordered_match_list", ct, "", "2:####.#;3:#,###,###", "", false, "Cypher query:\n" + cq + "\nThis is a prioritized list of matches\nYou should add several of their in-common-with matches to your project; this is necessary for creating communities.", false);
        ct = ct + 1;

                cq = "CALL gds.louvain.stream('icw', {relationshipWeightProperty:'weight',includeIntermediateCommunities:true, tolerance:0.0000001,maxIterations:10,maxLevels:10 }) YIELD nodeId, communityId, intermediateCommunityIds with nodeId,case when gds.util.asNode(nodeId).RN is not null then '*' + gds.util.asNode(nodeId).fullname else gds.util.asNode(nodeId).fullname end AS name, communityId as cid, intermediateCommunityIds as ici MATCH p=(d:DNA_Match)-[rms:match_segment]->(s:Segment) where rms.p='" + fullname + "' and rms.m=name and 7<rms.cm with nodeId,cid,ici,name,sum(rms.cm) as cm with nodeId,cid,ici,name,cm order by toInteger(cm) desc,name with cid,ici,collect(distinct name + ' {' + toInteger(cm) + '}') as names,collect(gds.util.asNode(nodeId).RN) as rns with rns,cid as community,ici,size(names) as ct, names as matches order by ct desc with rns,community,ici,ct,matches where ct>1 with community,ici as intermediary_communities,ct, matches return community, reverse(intermediary_communities) as intermediary_communities,ct, matches order by ct desc,intermediary_communities";
                        //"CALL gds.louvain.stream('icw', {relationshipWeightProperty:'weight',includeIntermediateCommunities:true, tolerance:0.0000001,maxIterations:10,maxLevels:10 }) YIELD nodeId, communityId, intermediateCommunityIds with nodeId,case when gds.util.asNode(nodeId).RN is not null then '*' + gds.util.asNode(nodeId).fullname else gds.util.asNode(nodeId).fullname end AS name, communityId as cid, intermediateCommunityIds as ici with nodeId,cid,ici,name order by name with cid,ici,collect(name) as names,collect(gds.util.asNode(nodeId).RN) as rns with rns,cid as community,ici,size(names) as ct, names as matches order by ct desc with rns,community,ici,ct,matches where ct>1 with community,ici as intermediary_communities,ct, matches,gen.rel.mrca_from_cypher_list(rns,15) as mrcas, split(gen.mss.mss_data(rns),'|') as rns return community, reverse(intermediary_communities) as intermediary_communities,ct, matches, mrcas,rns[1] as community_seg_ct, rns[0] as mss_anc_desc,rns[2] as mss_in_comm, rns[8] as mss_seg_in_comm, rns[3] as included_mss_total_seg_ct, rns[9] as mss_seg_in_community_segs order by intermediary_communities";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "no_parent_analytics", "louvain communities", ct, "", "0:######;2:#####", excelFile, false, "Virtual graph created by:\n" + cqvg + "\nThe virtual graph has " + node_ct +" nodes and " + rel_ct + " relationships\n\nCypher query for communities:\n" + cq + "\n\nGenerally with high cM matches, community matches will be in a family line such as paternal or maternal and possibly a grandparent.\nIf you have matches who are more distantly related there will be more than two communities.\n\nto drop virtual graph:\nCALL gds.graph.drop('icw')\n\n" , false);
        ct = ct + 1;

    
       //query for modularity optimization communities 
        cq = "CALL gds.beta.modularityOptimization.stream('icw', {relationshipWeightProperty:'weight',tolerance:0.0000001}) YIELD nodeId, communityId with nodeId, gds.util.asNode(nodeId).fullname AS name, communityId as cid with nodeId,cid,name order by name with cid,collect(name) as matches,cid as community with community,matches,size(matches) as ct where ct>2 with community,ct, matches order by ct desc return community,ct,matches";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "modularity", "modularity communities", ct, "", "0:######;1:#####", excelFile, true, "Virtual graph created by:\n" + cqvg + "\nThe virtual graph has " + node_ct +" nodes and " + rel_ct + " relationships\n\nCypher query for communities:\n" + cq + "\n\nGenerally with high cM matches, community matches will be in a family line such as paternal or maternal and possibly a grandparent.\nIf you have matches who are more distantly related there will be more than two communities.\n\nto drop virtual graph:\nCALL gds.graph.drop('icw')\n\n" , false);
        ct = ct + 1;
       
//        try
//        {gen.genlib.java_wait jw = new gen.genlib.java_wait();
//        jw.wait(1000L);
//        }
//        catch(InterruptedException e){}
    
    
        //code to implement Leiden communities ... does not improve on the above but might in other scenarios
//                need numeric value in nodes(s)
//        match(d:DNA_Match) set d.ndid= toInteger(split(elementid(d),':')[2]) 
//
//        create initial projection
//        CALL gds.graph.project(
//          'icw',
//          {
//            DNA_Match: {properties:'ndid' }
//          },
//          {
//            icw: 
//            { 
//              properties: 'src_cm' ,
//              orientation: 'undirected'
//             }    
//          }
//        )
//        YIELD graphName, nodeCount, relationshipCount, projectMillis
//
//
//
//
//
//
//        create subgraph
//        CALL gds.beta.graph.project.subgraph(
//          'icwfiltered',
//          'icw',
//          "n.ndid>0",
//          'r.src_cm > 35.0 and r.src_cm<3500.0',
//           {}
//        )
//        YIELD graphName, fromGraphName, nodeCount, relationshipCount
//
//
//
//        RUN LEIDEN
//        CALL gds.beta.leiden.stream('icwfiltered', {relationshipWeightProperty:'src_cm', randomSeed: 19,includeIntermediateCommunities:true, maxLevels:10}) YIELD nodeId, communityId, intermediateCommunityIds with nodeId,case when gds.util.asNode(nodeId).RN is not null then '*' + gds.util.asNode(nodeId).fullname else gds.util.asNode(nodeId).fullname end AS name, communityId as cid, intermediateCommunityIds as ici with nodeId,cid,ici,name order by name 
//        with cid,ici,collect(name) as names,collect(gds.util.asNode(nodeId).RN) as rns with rns,cid as community,ici,size(names) as ct, names as matches order by ct desc with rns,community,ici,ct,matches where ct>1 
//        with community,ici as intermediary_communities,ct, matches,gen.rel.mrca_from_cypher_list(rns,15) as mrcas, split(gen.mss.mss_data(rns),'|') as rns return community,intermediary_communities,ct, matches,mrcas,rns[1] as community_seg_ct, rns[0] as mss_anc_desc,rns[2] as mss_in_comm, rns[8] as mss_seg_in_comm, rns[3] as included_mss_total_seg_ct, rns[9] as mss_seg_in_community_segs
//

        return "";

    }
}
