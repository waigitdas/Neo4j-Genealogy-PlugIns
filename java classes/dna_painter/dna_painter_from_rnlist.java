/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna_painter;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class dna_painter_from_rnlist {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String dna_painter_csv_from_rn_list(
        @Name("rn_list") 
            String rn_list
  )
   
         { 
             
        String s = get_csv(rn_list);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_csv(String rns) 
    {
        String cq = "CALL gds.louvain.stream('icw', {relationshipWeightProperty:'weight',includeIntermediateCommunities:true, tolerance:0.0000001,maxIterations:10,maxLevels:10 }) YIELD nodeId, communityId, intermediateCommunityIds with nodeId,case when gds.util.asNode(nodeId).RN is not null then '*' + gds.util.asNode(nodeId).fullname else gds.util.asNode(nodeId).fullname end AS name, communityId as cid, intermediateCommunityIds as ici with nodeId,cid,ici,name order by name with cid,ici,collect(name) as names,collect(gds.util.asNode(nodeId).RN) as rns with rns,cid as community,ici,size(names) as ct, names as matches order by ct desc with rns,community,ici,ct,matches where ct>1 with community,ici as intermediary_communities,ct, matches,gen.rel.mrca_from_cypher_list(rns,15) as mrcas, rns with community,rns,'' as cColor,case when mrcas = '-' then community else mrcas end as grp MATCH p=(m:DNA_Match)-[[r:match_segment]]->(s:Segment) where m.RN in rns and r.p_rn in rns and r.m_rn in rns return distinct case when s.chr='0X' then 23 else toInteger(s.chr) end as Chr,s.strt_pos as Start_Location,s.end_pos as End_Location,r.cm as Centimorgan,r.snp_ct as SNPs,case when r.p<r.m then r.p + ' - ' + r.m else r.m + ' - ' + r.p end as Match,'good' as Confidence,grp as Group,'maternal' as Side,'~' as Notes,cColor as Color";
        return "";
    }
}
