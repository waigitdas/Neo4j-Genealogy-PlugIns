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


public class ancestor_reconstruction_methods_2 {
    @UserFunction
    @Description("ancestral reconstruction using monophyletic segments")

    public String triangulated_segment_matches(
        @Name("min_cm") 
            Long min_cm,
        @Name("max_cm") 
            Long max_cm,
        @Name("known_matches_only") 
            Boolean known_matches_only

  )
   
         { 
             
        get_matches_segments(1L, min_cm, max_cm, known_matches_only);
         return "";
            }

    
    
    public static void main(String args[]) {
        get_matches_segments(1L,7L,3800L,true);
    }
    
     public static String get_matches_segments(Long match_type, Long min_cm, Long max_cm,Boolean known_matches_only)
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        try {
            if (match_type.equals(1L)) { 
                gen.neo4jlib.neo4j_qry.qry_write("CALL gds.graph.drop('shared_matches')");
            }
        }
        catch (Exception e){}
  
               
        String cq="";
        String cqv="";
       String cqdnapainter="";
       String excelFile="";
       Double vcor =0.125;
       
        //GRAPH PROJECTION
        //Some algorithms require that the graph was loaded with UNDIRECTED orientation. 
        //These algorithms can not be used with a graph projected by a Cypher projection.
        //https://neo4j.com/docs/graph-data-science/current/management-ops/projections/graph-project/
        
               cqv = "CALL gds.graph.project( 'shared_matches', 'DNA_Match', {shared_match: {orientation: \"Undirected\"}} ) YIELD graphName AS graph, nodeProjection, nodeCount AS nodes, relationshipProjection, relationshipCount AS rels";
    
        String[] c = gen.neo4jlib.neo4j_qry.qry_to_csv(cqv).split("\n");
        String[] sc = c[0].split(",");
        String node_ct = sc[0];
        String rel_ct = sc[1];

        
            if (known_matches_only) {  //known matches only
       cq = "CALL gds.alpha.triangles('shared_matches') YIELD nodeA, nodeB, nodeC with nodeA, nodeB, nodeC where gds.util.asNode(nodeA).RN is not null and gds.util.asNode(nodeB).RN is not null and gds.util.asNode(nodeC).RN is not null with gds.util.asNode(nodeA).fullname AS nodeA, gds.util.asNode(nodeB).fullname AS nodeB, gds.util.asNode(nodeC).fullname AS nodeC , [[gds.util.asNode(nodeA).fullname, gds.util.asNode(nodeB).fullname, gds.util.asNode(nodeC).fullname]] as fn, [[gds.util.asNode(nodeA).RN, gds.util.asNode(nodeB).RN, gds.util.asNode(nodeC).RN]] as rns with nodeA,nodeB,nodeC, gen.rel.mrca_from_cypher_list(rns,15) as mrca, fn with distinct apoc.coll.sort(fn) as x,mrca match (m:DNA_Match)-[[r:match_segment]]->(s:Segment) where r.cor<=" + vcor + " and r.p in x and r.m in x and " + max_cm + ">=r.cm>=" + min_cm + " and r.snp_ct>=500 with x,mrca,case when r.p< r.m then r.p + ':' + r.m else r.m + ':' + r.p end as match_pair,collect(distinct s.Indx) as sc with x as fns,gen.dna.shared_overlapping_segments_for_list(x,100) as segs, mrca where size(segs)>0 return distinct fns as triangulated_matches,mrca,size(segs) as seg_ct,segs order by fns";
            cqdnapainter = "CALL gds.alpha.triangles('shared_matches') YIELD nodeA, nodeB, nodeC with nodeA, nodeB, nodeC where gds.util.asNode(nodeA).RN is not null and gds.util.asNode(nodeB).RN is not null and gds.util.asNode(nodeC).RN is not null  with gds.util.asNode(nodeA).fullname AS nodeA, gds.util.asNode(nodeB).fullname AS nodeB, gds.util.asNode(nodeC).fullname AS nodeC , [[gds.util.asNode(nodeA).fullname, gds.util.asNode(nodeB).fullname, gds.util.asNode(nodeC).fullname]] as fn, [[gds.util.asNode(nodeA).RN, gds.util.asNode(nodeB).RN, gds.util.asNode(nodeC).RN]] as rns with nodeA,nodeB,nodeC, gen.rel.mrca_from_cypher_list(rns,15) as mrca, fn with distinct apoc.coll.sort(fn) as x,mrca match (m:DNA_Match)-[[r:match_segment]]->(s:Segment) where r.cor<=" + vcor + " and r.p in x and r.m in x and " + max_cm + ">=r.cm>=" + min_cm + " and r.snp_ct>=500 with x,mrca,case when r.p< r.m then r.p + ':' + r.m else r.m + ':' + r.p end as match_pair,collect(distinct s.Indx + ':' + r.cm + ':' + r.snp_ct) as sc with x as fns,gen.dna.shared_overlapping_segments_for_list(x,100) as segs, mrca with distinct fns as triangulated_matches,mrca,size(segs) as seg_ct,segs where size(segs)>0 unwind segs as x call {with x with split(x,':') as xs return xs } return toInteger(case when xs[[0]]='0X' then 23 else xs[[0]] end) as chr,toInteger(xs[[1]]) as start,toInteger(xs[[2]]) as end, toFloat(xs[[3]]) as cm, toInteger(xs[[4]]) as snpd,triangulated_matches as match,'good' as confidence, mrca as group, 'maternal' as side, '' as notes, '' as color order by triangulated_matches";
            }
            else{
         cq = "CALL gds.alpha.triangles('shared_matches') YIELD nodeA, nodeB, nodeC with gds.util.asNode(nodeA).fullname AS nodeA, gds.util.asNode(nodeB).fullname AS nodeB, gds.util.asNode(nodeC).fullname AS nodeC , [[gds.util.asNode(nodeA).fullname, gds.util.asNode(nodeB).fullname, gds.util.asNode(nodeC).fullname]] as fn, [[gds.util.asNode(nodeA).RN, gds.util.asNode(nodeB).RN, gds.util.asNode(nodeC).RN]] as rns with nodeA,nodeB,nodeC, gen.rel.mrca_from_cypher_list(rns,15) as mrca, fn with distinct apoc.coll.sort(fn) as x,mrca match (m:DNA_Match)-[[r:match_segment]]->(s:Segment) where r.cor<=" + vcor + " and r.p in x and r.m in x and " + max_cm + ">=r.cm>=" + min_cm + " and r.snp_ct>=500 with x,mrca,case when r.p< r.m then r.p + ':' + r.m else r.m + ':' + r.p end as match_pair,collect(distinct s.Indx) as sc with x as fns,gen.dna.shared_overlapping_segments_for_list(x,100) as segs, mrca where size(segs)>0 return distinct fns as triangulated_matches,mrca,size(segs) as seg_ct,segs order by fns";
            cqdnapainter = "CALL gds.alpha.triangles('shared_matches') YIELD nodeA, nodeB, nodeC with gds.util.asNode(nodeA).fullname AS nodeA, gds.util.asNode(nodeB).fullname AS nodeB, gds.util.asNode(nodeC).fullname AS nodeC , [[gds.util.asNode(nodeA).fullname, gds.util.asNode(nodeB).fullname, gds.util.asNode(nodeC).fullname]] as fn, [[gds.util.asNode(nodeA).RN, gds.util.asNode(nodeB).RN, gds.util.asNode(nodeC).RN]] as rns with nodeA,nodeB,nodeC, gen.rel.mrca_from_cypher_list(rns,15) as mrca, fn with distinct apoc.coll.sort(fn) as x,mrca match (m:DNA_Match)-[[r:match_segment]]->(s:Segment) where r.cor<=" + vcor + " and r.p in x and r.m in x and " + max_cm + ">=r.cm>=" + min_cm + " and r.snp_ct>=500 with x,mrca,case when r.p< r.m then r.p + ':' + r.m else r.m + ':' + r.p end as match_pair,collect(distinct s.Indx + ':' + r.cm + ':' + r.snp_ct) as sc with x as fns,gen.dna.shared_overlapping_segments_for_list(x,100) as segs, mrca with distinct fns as triangulated_matches,mrca,size(segs) as seg_ct,segs where size(segs)>0 unwind segs as x call {with x with split(x,':') as xs return xs } return toInteger(case when xs[[0]]='0X' then 23 else xs[[0]] end) as chr,toInteger(xs[[1]]) as start,toInteger(xs[[2]]) as end, toFloat(xs[[3]]) as cm, toInteger(xs[[4]]) as snpd,triangulated_matches as match,'good' as confidence, mrca as group, 'maternal' as side, '' as notes, '' as color order by triangulated_matches";
                   }
            
                  
            //export match groups with shared/triangulated segments
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Triangulated_segments", "shared_matches", 1, "", "2:####;2:####", "", false, "UDF: \nreturn gen.discovery.triangulated_segment_matches(" + match_type + "," + min_cm + "," + max_cm + "," + known_matches_only + ")\n\nMatch_ type: " + match_type + " with shared centimorgan range of " + min_cm + " to " +  max_cm + "\nThese analytics use the Neo4j Graph Data Science PlugIn\nThe virtual graph created had " + node_ct + " nodes and  " + rel_ct +" relationships\n\nThe criteria for robust triangulation, used in this report, are\n1. A triad of matches: A, B, C\n2. Shared matches for each possibility: A-B, A-C and B-C (requires 3 DNA testers)\n3. The match pairs share overlapping segments\n4. The coefficient of relationship is less than or equal to " + vcor + " for each match pair.\n5. The match pairs share a common ancestor. Optional while discovering potential new descendants. \n\nvirtual graph query:\n" + cqv + "\n\nalgorithm query:\n" + cq + "\n\nTo see the virtual graph information use this query:\ncall gds.graph.streamRelationshipProperty('shared_matches','weight') yield sourceNodeId,targetNodeId, propertyValue as weight with gds.util.asNode(sourceNodeId) as s, gds.util.asNode(targetNodeId) as t, weight as w return s.fullname as source,t.fullname as target,w as weight order by weight desc\n\nGraph algorithms use iterations and probabilities. Results may vary between runs. They will also vary with the submitted parameters. Try different settings for the centimorgan ranges to extract different sets of matches. \nThere is still a virtual graph 'shared_matches' in the database; to remove it, use this command CALL gds.graph.drop('shared_matches')\n\nVisualization query; individual segments may not map to three DNA_Match nodes because the triangulation is based on overlapping segments:\nCALL gds.alpha.triangles('shared_matches') YIELD nodeA, nodeB, nodeC with gds.util.asNode(nodeA).fullname AS nodeA, gds.util.asNode(nodeB).fullname AS nodeB, gds.util.asNode(nodeC).fullname AS nodeC ,gds.util.asNode(nodeA) AS A, gds.util.asNode(nodeB) AS B, gds.util.asNode(nodeC) AS C , [[gds.util.asNode(nodeA).fullname, gds.util.asNode(nodeB).fullname, gds.util.asNode(nodeC).fullname]] as fn,[[gds.util.asNode(nodeA).RN, gds.util.asNode(nodeB).RN, gds.util.asNode(nodeC).RN]] as rns with A,B,C,nodeA,nodeB,nodeC, gen.rel.mrca_from_cypher_list(rns,15) as mrca, fn with distinct A,B,C,apoc.coll.sort(fn) as x,mrca match (m:DNA_Match)-[[r:match_segment]]->(s:Segment) where r.cor<=" + vcor + " and r.p in x and r.m in x and " + max_cm + ">=r.cm>=" + min_cm + " and r.snp_ct>=500 with A,B,C,x,mrca,case when r.p< r.m then r.p + ':' + r.m else r.m + ':' + r.p end as match_pair,collect(distinct s.Indx) as sc,collect(distinct s) as scn with A,B,C,scn,x as fns,gen.dna.shared_overlapping_segments_for_list(x,100) as segs unwind segs as y with A,B,C,y, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(apoc.coll.flatten(collect(distinct fns))))) as tm with A,B,C,y ,size(tm) as ct,tm as tm unwind tm as z with A,B,C,y,z match (s:Segment)-[[rrr:match_segment]]-(mmm:DNA_Match) where s.Indx = y and ((rrr.p=A.fullname or rrr.p=B.fullname or rrr.p=C.fullname) and (rrr.m=A.fullname or rrr.m=B.fullname or rrr.m=C.fullname)) return s,A,B,C,z,rrr\n\nDNA Painter query\n" + cqdnapainter + "\n\nReferences:\nNeo4j Graph Data Science: https://neo4j.com/docs/graph-data-science/current/algorithms/triangle-count/\n Bettinger, B. A Triangulation Intervention. The Genetic Genealogist https://thegeneticgenealogist.com/2016/06/19/a-triangulation-intervention/ (2016)." , true);
            
       //export segments with triangulated matches
       cq ="CALL gds.alpha.triangles('shared_matches') YIELD nodeA, nodeB, nodeC with gds.util.asNode(nodeA).fullname AS nodeA, gds.util.asNode(nodeB).fullname AS nodeB, gds.util.asNode(nodeC).fullname AS nodeC , [[gds.util.asNode(nodeA).fullname, gds.util.asNode(nodeB).fullname, gds.util.asNode(nodeC).fullname]] as fn, [[gds.util.asNode(nodeA).RN, gds.util.asNode(nodeB).RN, gds.util.asNode(nodeC).RN]] as rns with nodeA,nodeB,nodeC, gen.rel.mrca_from_cypher_list(rns,15) as mrca, fn with distinct apoc.coll.sort(fn) as x,mrca match (m:DNA_Match)-[[r:match_segment]]->(s:Segment) where r.cor<=" + vcor + " and r.p in x and r.m in x and " + max_cm + ">=r.cm>=" + min_cm + " and r.snp_ct>=500 with x,mrca,case when r.p< r.m then r.p + ':' + r.m else r.m + ':' + r.p end as match_pair,collect(distinct s.Indx) as sc with x as fns,gen.dna.shared_overlapping_segments_for_list(x,100) as segs unwind segs as y with y, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(apoc.coll.flatten(collect(distinct fns))))) as tm return y as segment,size(tm) as ct,tm as triangulated_match_group,gen.tgs.get_segment_indx_tg(y) as tgs, gen.mss.get_mss_for_segment(y) as mss_mrca order by segment";
       gen.excelLib.queries_to_excel.qry_to_excel(cq, "Triangulated_segments", "seg_matches", 2, "", "1:####;3:####", excelFile, true, "UDF: \nreturn gen.discovery.triangulated_segment_matches(" + match_type + "," + min_cm + "," + max_cm + "," + known_matches_only + ")\n\nMatch_ type: " + match_type + " with shared centimorgan range of " + min_cm + " to " +  max_cm + "\nThese analytics use the Neo4j Graph Data Science PlugIn\nThe virtual graph created had " + node_ct + " nodes and  " + rel_ct +" relationships\n\nvirtual graph query:\n" + cqv + "\n\nalgorithm query:\n" + cq  , true);
            
//            }
            
            //export danpainter file
          gen.neo4jlib.neo4j_qry.qry_to_csv(cqdnapainter.replace("[[","[").replace("]]","]"), gen.neo4jlib.neo4j_info.project + "_DNA_Painter_Triangulated_segments_" + gen.genlib.current_date_time.getDateTime() + ".csv");
        return "completed";
        }
}
