/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.mss;

import gen.neo4jlib.neo4j_qry;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class rn_list_to_mss {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String mss_data(
        @Name("rn_list") 
            List<Long> rn_list
  )
   
         { 
             
        String s = get_mss(rn_list);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_mss(List<Long> rn_list) 
    {
        String cq = "MATCH p=(m:DNA_Match)-[r:match_segment]->(s:Segment) where m.RN in  " + rn_list + "   and r.p_rn in  " + rn_list + "   and r.m_rn in " + rn_list + "   with size( \" + rn_list + \"  ) as ct,collect(distinct case when s.phased_anc>0 then '*' + s.Indx else s.Indx end) as segs,collect(distinct s.Indx) as seg_list,collect(distinct s.phased_anc) as phased_anc optional match (mss:MSS)-[rm:ms_seg]-(ms:Segment) where ms.Indx in segs with ct,segs,phased_anc,collect(distinct ms.Indx) as mss_segs,apoc.coll.sort(collect(distinct case when mss.phased_gen>0 then '*' + mss.fullname else mss.fullname end)) as mss_fullnames,apoc.coll.sort(collect(distinct mss.mrca)) as mss_rns,seg_list with phased_anc,size(segs) as seg_ct,size(mss_segs) as mss_seg,mss_fullnames,size(mss_segs) as mss_segs, apoc.coll.sort(segs) as segs,seg_list,mss_rns, apoc.coll.sort(apoc.coll.intersection(seg_list,mss_segs)) as mss_seg_in_community_segs with phased_anc,size(segs) as seg_ct,  mss_segs,mss_rns,mss_fullnames,  apoc.coll.sort(segs) as segs, size(mss_seg_in_community_segs) as mss_seg_in_comm, mss_seg_in_community_segs match (mss2:MSS)-[rm2:ms_seg]->(s3:Segment) where mss2.mrca in mss_rns return phased_anc,seg_ct as community_seg_ct,size(mss_rns) as community_mss_ct,size(collect(distinct s3.Indx)) as total_mss_seg_ct,  mss_segs as community_mss_seg_ct,mss_rns,mss_fullnames,  segs, mss_seg_in_comm, mss_seg_in_community_segs ";
        String[] c = gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited_str(cq).split("\n");
        return c[0];
        
//         String[] s = c[0].split(",");
//        String r ="";
//        for (int i=0;i<s.length;i++){
//            if (s[i].substring(0,0).equals("[")){  //collection
//            r = r + s[i].replace(",","^^");
//                 }
//            else{r = r + s[i];}
//            if (i<s.length-1){r = r + ";";}
//        }
//        return r;
        
    }
}
