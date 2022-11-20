/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.algo;

import gen.neo4jlib.neo4j_qry;
import java.awt.Desktop;
import java.io.File;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class communities_icw {
    @UserFunction
    @Description("Detects comminities using graph algrorithms: 1=louvain; 2=modularity; 3 = label propagation. Enter range of cm. Specify whether to limit the results to known persons; if this is false and the cm range too large, you may get very long lists that overwhelm the Excel capabilities.")

    public String community_detection_icw(
        @Name("algoritm") 
            Long algorithm,
        @Name("min_cm") 
            Long min_cm,
        @Name("max_cm") 
            Long max_cm,
        @Name("known_matches_only") 
            Boolean known_matches_only
  )
         { 
             
        String s = run_algo(algorithm, min_cm, max_cm, known_matches_only);  //, intermediateCommunities);
         return s;
            }

     
     public static String run_algo(Long algorithm, Long min_cm, Long max_cm,Boolean known_matches_only)  //, Boolean intermediateCommunities) 
    {
        String cq ="";
         String cqv ="";
        String algo = "";
        Long algo_nbr = 0L ;
        String extra="" ;
        
        try {
            gen.neo4jlib.neo4j_qry.qry_write("CALL gds.graph.drop('icw')");
        }
        catch (Exception e){}
        
        //create virtual graph
        //different query for each algorithm
             if (known_matches_only ) {
            cqv = "CALL gds.graph.project.cypher('icw','MATCH (m:DNA_Match) where m.RN is not null RETURN id(m) AS id', 'MATCH (m)-[[r:match_by_segment]]->(m2:DNA_Match) where " + max_cm + ">r.cm>" + min_cm + " RETURN id(m) AS source, id(m2) AS target, r.cm as weight',{readConcurrency: 4,validateRelationships:FALSE} ) YIELD graphName AS graph, nodeQuery, nodeCount AS nodes, relationshipQuery, relationshipCount AS rels return nodes, rels";
            }
            else {
            
             cqv = "CALL gds.graph.project.cypher('icw','MATCH (m:DNA_Match) RETURN id(m) AS id', 'MATCH (m)-[[r:match_by_segment]]->(m2:DNA_Match) where " + max_cm + ">r.cm>" + min_cm + " RETURN id(m) AS source, id(m2) AS target, r.cm as weight',{readConcurrency: 4,validateRelationships:FALSE} ) YIELD graphName AS graph, nodeQuery, nodeCount AS nodes, relationshipQuery, relationshipCount AS rels return nodes, rels";
            }
             
        String[] c = gen.neo4jlib.neo4j_qry.qry_to_csv(cqv).split("\n");
        String[] sc = c[0].split(",");
        String node_ct = sc[0];
        String rel_ct = sc[1];
        // end create virtual graph
        
        if (algorithm.equals(1L)) {  //louvain
            algo = "louvain";
            algo_nbr = 1L;
            cq ="CALL gds.louvain.stream('icw', {relationshipWeightProperty:'weight',includeIntermediateCommunities:true, tolerance:0.0000001,maxIterations:10,maxLevels:10 }) YIELD nodeId, communityId, intermediateCommunityIds with nodeId,case when gds.util.asNode(nodeId).RN is not null then '*' + gds.util.asNode(nodeId).fullname else gds.util.asNode(nodeId).fullname end AS name, communityId as cid, intermediateCommunityIds as ici with nodeId,cid,ici,name order by name with cid,ici,collect(name) as names,collect(gds.util.asNode(nodeId).RN) as rns with rns,cid as community,ici,size(names) as ct, names as matches order by ct desc with rns,community,ici,ct,matches where ct>1 with community,ici as intermediary_communities,ct, matches,gen.rel.mrca_from_cypher_list(rns,15) as mrcas, split(gen.mss.mss_data(rns),'|') as rns return community, intermediary_communities,ct, matches, mrcas,rns[[1]] as community_seg_ct, rns[[0]] as mss_anc_desc,rns[[2]] as mss_in_comm, rns[[8]] as mss_seg_in_comm, rns[[3]] as included_mss_total_seg_ct, rns[[9]] as mss_seg_in_community_segs";
            // //,rns[[5]] as mss_rns , rns[[6]] as mss_fullnames, rns[[7]] as community_segs, rns[[8]] as mss_seg_in_comm2, rns[[4]] as community_mss_seg_ct

            extra ="\n\nYou may want to try running Louvain without intermediarey communities. If so, use this query:\nCALL gds.louvain.stream('shared_matches', {relationshipWeightProperty:'weight', tolerance:0.0000000001,maxIterations:10,maxLevels:10 }) YIELD nodeId, communityId with case when gds.util.asNode(nodeId).RN is not null then '*' + gds.util.asNode(nodeId).fullname else gds.util.asNode(nodeId).fullname end AS name, communityId as cid with cid,name order by name with cid,collect(name) as names with cid as community,size(names) as ct, names as matches order by ct desc with community,ct,matches where ct>2 return community,ct, matches\n\nvisualization query:\nCALL gds.louvain.stream('icw', {relationshipWeightProperty:'weight',includeIntermediateCommunities:true, tolerance:0.0000001,maxIterations:10,maxLevels:10 }) YIELD nodeId with collect( distinct gds.util.asNode(nodeId).fullname) as list_names match path=(m1:DNA_Match)-[[r:match_by_segment]]-(m2:DNA_Match) where m1.fullname in list_names and m2.fullname in list_names return path";
        }

        else if (algorithm.equals(2L)) { //modularity optimization
            algo = "modularity";
            algo_nbr = 2L;
            cq = "CALL gds.beta.modularityOptimization.stream('icw', {relationshipWeightProperty:'weight'}) YIELD nodeId, communityId with nodeId, case when gds.util.asNode(nodeId).RN is not null then '*' + trim(gds.util.asNode(nodeId).fullname) else trim(gds.util.asNode(nodeId).fullname) end AS name, communityId with name,communityId,nodeId order by name with collect(name) as names,collect(gds.util.asNode(nodeId).RN) as rns,communityId with communityId,size(names) as ct,names,gen.rel.mrca_from_cypher_list(rns,15) as mrcas, split(gen.mss.mss_data(rns),'|') as rns where ct>1 return communityId,ct,names,mrcas,rns[[1]] as community_seg_ct, rns[[0]] as mss_anc_desc,rns[[2]] as mss_in_comm, rns[[8]] as mss_seg_in_comm, rns[[3]] as included_mss_total_seg_ct, rns[[9]] as mss_seg_in_community_segs ";
            ////,rns[[5]] as mss_rns , rns[[6]] as mss_fullnames, rns[[7]] as community_segs, rns[[8]] as mss_seg_in_comm2, rns[[4]] as community_mss_seg_ct ORDER BY ct desc,names
            
            extra="\n\nvisualization query:\nCALL gds.beta.modularityOptimization.stream('icw', {relationshipWeightProperty:'weight'}) YIELD nodeId with collect( distinct gds.util.asNode(nodeId).fullname) as list_names match path=(m1:DNA_Match)-[[r:match_by_segment]]-(m2:DNA_Match) where m1.fullname in list_names and m2.fullname in list_names return path";
        }
        
         else if (algorithm.equals(3L)) { 
            algo = "Label propagation";
            algo_nbr = 3L;
            cq ="CALL gds.labelPropagation.stream('icw', {relationshipWeightProperty:'weight'}) YIELD nodeId, communityId with nodeId,case when gds.util.asNode(nodeId).RN is not null then '*' + trim(gds.util.asNode(nodeId).fullname) else trim(gds.util.asNode(nodeId).fullname) end AS name, communityId with nodeId,name,communityId order by name with collect(name) as names,collect(gds.util.asNode(nodeId).RN ) as rns,communityId with rns,communityId,size(names) as ct,names where ct>1 with communityId,ct,names,gen.rel.mrca_from_cypher_list(rns,15) as mrcas , split(gen.mss.mss_data(rns),'|') as rns return communityId,ct,names, mrcas,rns[[1]] as community_seg_ct, rns[[0]] as mss_anc_desc,rns[[2]] as mss_in_comm, rns[[8]] as mss_seg_in_comm, rns[[3]] as included_mss_total_seg_ct, rns[[9]] as mss_seg_in_community_segs ORDER BY ct desc,names"; 
        
            extra = "CALL gds.labelPropagation.stream('icw', {relationshipWeightProperty:'weight'}) YIELD nodeId with collect( distinct gds.util.asNode(nodeId).fullname) as list_names match path=(m1:DNA_Match)-[[r:match_by_segment]]-(m2:DNA_Match) where m1.fullname in list_names and m2.fullname in list_names return path";
         
         }
        try{
            
            //extra message for query not limited to known mathes
            if (known_matches_only){}
            else {extra = extra + "\n\nCAUTION: This report is not limited to known persons. Thus the matches may be in different family lines.";}
            
          //expoert report to Excel  
          gen.excelLib.queries_to_excel.qry_to_excel(cq, "icw_algo_" + algo, "communities", 1, "", "0:#######;1:#####;2:#####;5:####;6:####;7:####;8:####9:####", "", true, "UDF: \nreturn gen.algo.community_detection_icw(" + algorithm + "," + min_cm + "," + max_cm + "," + known_matches_only + ")\n\nAlgorithm: " + algo + " with shared centimorgan range of " + min_cm + " to " +  max_cm + "\nThese analytics use the Neo4j Graph Data Science PlugIn\nThe virtual graph created had " + node_ct + " nodes and  " + rel_ct +" relationships\n\nvirtual graph query:\n" + cqv + "\n\nalgorithm query:\n" + cq + "\n\nTo see the virtual graph information use this query:\ncall gds.graph.streamRelationshipProperty('icw','weight') yield sourceNodeId,targetNodeId, propertyValue as weight with gds.util.asNode(sourceNodeId) as s, gds.util.asNode(targetNodeId) as t, weight as w return s.fullname as source,t.fullname as target,w as weight order by weight desc\n\nGraph algorithms use iterations and probabilities. Results may vary between runs. They will also vary with the submitted parameters. Try different settings for the centimorgan ranges to extract different sets of matches. \nThe communityId has no intrinsic meaning; it is simply an identifier assigned by the algorithm.\n\nThere is still a virtual graph 'icw' in the database; to remove it, use this command \nCALL gds.graph.drop('icw')" + extra, true);
          
        //prepare DNA Painter file
       if (algorithm.equals(1L)) {  //louvain
           cq= "CALL gds.louvain.stream('icw', {relationshipWeightProperty:'weight',includeIntermediateCommunities:true, tolerance:0.0000001,maxIterations:10,maxLevels:10 }) YIELD nodeId, communityId, intermediateCommunityIds with nodeId,case when gds.util.asNode(nodeId).RN is not null then '*' + gds.util.asNode(nodeId).fullname else gds.util.asNode(nodeId).fullname end AS name, communityId as cid, intermediateCommunityIds as ici with nodeId,cid,ici,name order by name with cid,ici,collect(name) as names,collect(gds.util.asNode(nodeId).RN) as rns with rns,cid as community,ici,size(names) as ct, names as matches order by ct desc with rns,community,ici,ct,matches where ct>1 with community,ici as intermediary_communities,ct, matches,gen.rel.mrca_from_cypher_list(rns,15) as mrcas, rns with community,rns,'' as cColor,case when mrcas = '-' then community else community + ' - ' + mrcas end as grp MATCH p=(m:DNA_Match)-[[r:match_segment]]->(s:Segment) where m.RN in rns and r.p_rn in rns and r.m_rn in rns return distinct case when s.chr='0X' then 23 else toInteger(s.chr) end as Chr,s.strt_pos as Start_Location,s.end_pos as End_Location,r.cm as Centimorgan,r.snp_ct as SNPs,case when r.p<r.m then r.p + ' - ' + r.m else r.m + ' - ' + r.p end as Match,'good' as Confidence,grp as Group,'maternal' as Side,'~' as Notes,cColor as Color";
       }
         else if (algorithm.equals(2L)) { 
             cq="CALL gds.beta.modularityOptimization.stream('icw', {relationshipWeightProperty:'weight'}) YIELD nodeId, communityId with nodeId,case when gds.util.asNode(nodeId).RN is not null then '*' + gds.util.asNode(nodeId).fullname else gds.util.asNode(nodeId).fullname end AS name, communityId as cid with nodeId,cid,name order by name with cid,collect(name) as names,collect(gds.util.asNode(nodeId).RN) as rns with rns,cid as community,size(names) as ct, names as matches order by ct desc with rns,community,ct,matches where ct>1 with community,ct, matches,gen.rel.mrca_from_cypher_list(rns,15) as mrcas, rns with community,rns,'' as cColor,case when mrcas = '-' then community else community + ' - ' + mrcas end as grp MATCH p=(m:DNA_Match)-[[r:match_segment]]->(s:Segment) where m.RN in rns and r.p_rn in rns and r.m_rn in rns return distinct case when s.chr='0X' then 23 else toInteger(s.chr) end as Chr,s.strt_pos as Start_Location,s.end_pos as End_Location,r.cm as Centimorgan,r.snp_ct as SNPs,case when r.p<r.m then r.p + ' - ' + r.m else r.m + ' - ' + r.p end as Match,'good' as Confidence,grp as Group,'maternal' as Side,'~' as Notes,cColor as Color";
         }
         else if (algorithm.equals(3L)) { 
             cq="CALL gds.labelPropagation.stream('icw', {relationshipWeightProperty:'weight'}) YIELD nodeId, communityId with nodeId,case when gds.util.asNode(nodeId).RN is not null then '*' + gds.util.asNode(nodeId).fullname else gds.util.asNode(nodeId).fullname end AS name, communityId as cid with nodeId,cid,name order by name with cid,collect(name) as names,collect(gds.util.asNode(nodeId).RN) as rns with rns,cid as community,size(names) as ct, names as matches order by ct desc with rns,community,ct,matches where ct>1 with community,ct, matches,gen.rel.mrca_from_cypher_list(rns,15) as mrcas, rns with community,rns,'' as cColor,case when mrcas = '-' then community else community + ' - ' + mrcas end as grp MATCH p=(m:DNA_Match)-[[r:match_segment]]->(s:Segment) where m.RN in rns and r.p_rn in rns and r.m_rn in rns return distinct case when s.chr='0X' then 23 else toInteger(s.chr) end as Chr,s.strt_pos as Start_Location,s.end_pos as End_Location,r.cm as Centimorgan,r.snp_ct as SNPs,case when r.p<r.m then r.p + ' - ' + r.m else r.m + ' - ' + r.p end as Match,'good' as Confidence,grp as Group,'maternal' as Side,'~' as Notes,cColor as Color";
         }
           String fn = gen.neo4jlib.neo4j_info.project + "_DNA__Painter_" + algo + "_" + min_cm + "_" + max_cm + "_" + gen.genlib.current_date_time.getDateTime() + ".csv" ;
          gen.neo4jlib.neo4j_qry.qry_to_csv(cq.replace("[[","[").replace("]]","]"),fn );
          Desktop.getDesktop().open(new File(gen.neo4jlib.neo4j_info.Database_Dir + fn));
          
        return algo + " completed\n\nUse csv file to pait at DNA Painter.";  
        }
        catch (Exception e) {return algo + " error: " + cq;}
     }
}