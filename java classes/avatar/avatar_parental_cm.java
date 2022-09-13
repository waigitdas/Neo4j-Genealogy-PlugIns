/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.avatar;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class avatar_parental_cm {
    @UserFunction
    @Description("Computes cm for avatars from merged overlaping segments.")

    public String parental_cm(
  )
        { 
             
        get_cm();
         return "complete";
            }

    
    
    public static void main(String args[]) {
        get_cm();
    }
    
     public static String get_cm() 
    {
       Double total_expected_cm =  7453.2;
       gen.neo4jlib.neo4j_info.neo4j_var();
       gen.neo4jlib.neo4j_info.neo4j_var_reload();
       
       gen.neo4jlib.neo4j_qry.qry_write("MATCH p=()-[r:avatar_avsegment]->() delete r");
       gen.neo4jlib.neo4j_qry.qry_write("MATCH p=()-[r:avseg_seg]->() delete r");
       gen.neo4jlib.neo4j_qry.qry_write("match (a:avSegment) delete a");
       
       //merge segments, computertheir cm and create avSegments
        gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(a:Avatar)-[r:avatar_segment]->(s:Segment) where r.avatar_side='paternal' with a,a.fullname as fn, a.RN as RN, apoc.coll.sort(collect(distinct s.Indx)) as segs with a,fn,RN, reduce(s = '', x IN segs | s + x + ',') as segs with a,fn,RN,gen.avatar.segment_cm(a.RN,'paternal',segs) as cm, segs set a.paternal_cm=cm");

        gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(a:Avatar)-[r:avatar_segment]->(s:Segment) where r.avatar_side='maternal' with a,a.fullname as fn, a.RN as RN, apoc.coll.sort(collect(distinct s.Indx)) as segs with a,fn,RN, reduce(s = '', x IN segs | s + x + ',') as segs with a,fn,RN,gen.avatar.segment_cm(a.RN,'maternal',segs) as cm, segs set a.maternal_cm=cm");

        //gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(a:Avatar)-[r:avatar_segment]->(s:Segment) where r.avatar_side is not null with a,a.fullname as fn, a.RN as RN, apoc.coll.sort(collect(distinct s.Indx)) as segs with a,fn,RN, reduce(s = '', x IN segs | s + x + ',') as segs with a,fn,RN,gen.avatar.segment_cm(a.RN,'total',segs) as cm, segs set a.total_cm=cm");

        //gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(a:Avatar)-[r:avatar_segment]->(s:Segment) with a,a.fullname as fn, a.RN as RN, apoc.coll.sort(collect(distinct s.Indx)) as segs with a,fn,RN, reduce(s = '', x IN segs | s + x + ',') as segs with a,fn,RN,gen.avatar.segment_cm(a.RN,'unknown',segs) as cm, segs set a.total_cm=cm");

        //get cm data
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (a:avSegment) with a,gen.dna.hapmap_cm(case when a.chr='0X' then '23' else a.chr end,a.strt_pos,a.end_pos) as cm set a.cm=cm ");
        
        gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(a:Avatar)-[r:avatar_avsegment]->(s) where r.side='paternal' with a,sum(s.cm) as cm set a.paternal_cm = cm");
        gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(a)-[r:avatar_avsegment]->(s) where r.side='maternal' with a,sum(s.cm) as cm set a.maternal_cm = cm");
        //gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(a)-[r:avatar_avsegment]->(s) where r.side='total' with a,sum(s.cm) as cm set a.total_cm = cm");
  
        
        String cq = "MATCH (a:Avatar) where a.dna_coverage is not null RETURN a.fullname as Avatar, a.RN as RN, toInteger(case when a.paternal_cm is null then 0 else a.paternal_cm end) as paternal_cm, toInteger(case when a.maternal_cm is null then 0 else a.maternal_cm end) as maternal_cm,a.total_cm as total_cm order by a.total_cm desc";
         gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(a)-[r:avatar_avsegment]->(s:avSegment)  with distinct a,a.fullname as fn,s with a,fn,sum(s.cm) as cm set a.dna_coverage=cm/" + total_expected_cm + ",a.total_cm=cm");

         gen.excelLib.queries_to_excel.qry_to_excel(cq, gen.neo4jlib.neo4j_info.project + "Avatar_cm", "cm", 1, "", "1:#####;2:####;3:####;4:####", "", true, cq, true);
        
         
        return "completed";
    }
}
