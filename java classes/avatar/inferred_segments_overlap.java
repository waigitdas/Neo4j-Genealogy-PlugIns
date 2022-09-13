/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.avatar;

import gen.neo4jlib.neo4j_qry;
import java.io.File;
import java.io.FileWriter;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.quote;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
import gen.genlib.handy_Functions;


public class inferred_segments_overlap {
    @UserFunction
    @Description("Inferred DNA segments")

    public String infer_segments(
//        @Name("rn1") 
//            Long rn1,
//        @Name("rn2") 
//            Long rn2
  )
   
         { 
             
        infer();
         return "";
            }

    
    
    public static void main(String args[]) {
        infer();
    }
    
     public static String infer() 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        //poitions in the query output        
        int pos_seg1 = 8;
        int pos_seg2 = 15;
        int base_pos = 1;
        int close_pos = 4;
        int compare_pos = 11;
//        int mrca_rn1 = 5;
//        int mrca_rn2= 12;
//        int cm_base = 10;
//       int cn_shared = 0;
//        int cm_compare = 0;
//        int cor1 = 6;
//        int cor2 = 13;
//        //int compare_pos = 11;
//        
        String cq="MATCH p=(d:DNA_Match)-[r:match_segment]->(s:Segment) where (r.p_rn=d.RN or r.m_rn=d.RN) and r.cor > 0.25 with distinct s,d.fullname as base,d.RN as rn, case when r.p_rn=d.RN then r.m else r.p end as match, case when r.p_rn=d.RN then r.m_rn else r.p_rn end as close_rn,r.cor as cor,case when r.p_rn=d.RN then r.p_side else r.m_side end as side,r.mrca_rn as mrca1,r.cm as cm1,r.rel as rel1 with distinct base,rn,match,close_rn,cor, s,side,mrca1,cm1,rel1 MATCH p2=(d2:DNA_Match{RN:rn})-[r2:match_segment]->(s2:Segment) where (r2.p_rn=d2.RN or r2.m_rn=d2.RN) and r2.cor <0.125 and s.chr=s2.chr and s.strt_pos<=s2.end_pos and s.end_pos>=s2.strt_pos with distinct base,rn,side,match,close_rn,cor,s.Indx as seg,case when r2.p_rn=d2.RN then r2.m else r2.p end as match2,case when r2.p_rn=d2.RN then r2.m_rn else r2.p_rn end as compare_rn,case when r2.p_rn=d2.RN then r2.p_side else r2.m_side end as side2, s2.Indx as seg2,mrca1,r2.mrca_rn as mrca2,r2.cor as cor2,rel1,cm1,r2.cm as cm2,r2.rel as rel2 with distinct base,rn,side,match as close_match,close_rn,mrca1,cor,rel1,seg,cm1,match2 as comparison_match,compare_rn,mrca2,cor2,rel2,seg2,cm2 where rel2 is not null and rel2=replace(rel2,';','') return base,rn,side, close_match,close_rn,mrca1,cor,rel1,seg,cm1, comparison_match,compare_rn,mrca2,cor2,rel2,seg2,cm2,gen.rel.compute_inference(rn,close_rn,compare_rn,mrca2) as inference_rns, gen.dna.segment_overlap(seg,seg2) as overlap_cm order by inference_rns";
                

        String c[] = gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited_str(cq).split("\n");
        
         String fn = "inferred_segs.csv";
       
        File fnc = new File(gen.neo4jlib.neo4j_info.Import_Dir + fn );
       
        FileWriter fw = null;
 
        try{
            fw = new FileWriter(fnc);
            fw.write("base|rn|side|close_match|close_rn|mrca1|cor1|rel1|seg1|cm1|comparison_match|comparison_rn|mrca2|cor2|rel2|seg2|cm2|infered_rn|shared_cm|seg_shared|left_cm|seg_left|right_cm|seg_right|method|flank|pan\n");
        }
        catch(Exception e){}
         
   
        
        for (int i=0; i<c.length; i++) 
        {
            String cs[] = c[i].split(Pattern.quote("|"));
            String rtn = seg_process(cs[pos_seg1],cs[pos_seg2]);
        try{
            //fw.write(cs[base_pos] + "," +  cs[close_pos] + "," +  cs[compare_pos] + "," +   rtn + "\n");
            fw.write(c[i] + rtn + "\n");
        }
        catch(Exception ex){}
        
   
        }

        try{
            fw.flush();
            fw.close();
        }
        catch(Exception ex2){}
        
        //remove prior data
        gen.neo4jlib.neo4j_qry.qry_write("match (a:inferSegment)-[r]-() delete r ");
        gen.neo4jlib.neo4j_qry.qry_write("match (a:inferSegment) delete a ");
        
        //read initial inference data and transform for import into Neo4j
        //left flank of overlap
        cq ="LOAD CSV WITH HEADERS FROM 'file:///inferred_segs.csv' as line FIELDTERMINATOR '|' with split(line.infered_rn,',') as ls,line.seg_left as leftseg,line.left_cm as lc,line.base as p,line.rn as p_rn,line.close_match as m,line.close_rn as m_rn,line.cor1 as cor,line.rel1 as rel,line.mrca1 as mrca_rn, line.comparison_match as comparison_match, line.comparison_rn as compare_rn,line.seg1 as seg1,line.rel2 as rel2,line.cor2 as cor2,line.seg2 as seg2,line.seg_shared as shared_seg,line.shared_cm as shared_cm,replace(line.mrca2,',',';') as compare_mrca_rn with split( ls[1],';') as lss, leftseg,lc,ls,p,p_rn,m, m_rn,cor,rel,rel2,cor2,mrca_rn,comparison_match,compare_rn,seg1, seg2,shared_seg,shared_cm, compare_mrca_rn where leftseg > '  ' unwind lss as lu call { with lu with split(lu,';' ) as lus unwind lus as lusw with split (lusw,':') as lr return toInteger(trim(lr[0])) as l1,lr[1] as l2 } with ls[0] as infer_source,l1 as avatar_rn,l2 as seg_side,leftseg,split(leftseg,':') as lss,lc as left_cm,'infer' as side_method,p,p_rn,m, m_rn,cor,rel,rel2,cor2,mrca_rn, comparison_match,compare_rn,seg1, seg2,shared_seg, shared_cm,compare_mrca_rn,replace(replace(replace(gen.rel.mrca_str(toInteger(p_rn),toInteger(m_rn)),'[',''),']',''),',',';') as pair_mrca where toInteger(left_cm)>=7 return infer_source,avatar_rn,seg_side,leftseg,lss[0] as lchr,toInteger(lss[1]) as lstrt, toInteger(lss[2]) as lend,left_cm,side_method,p, p_rn,m, m_rn,cor,rel,mrca_rn,pair_mrca, comparison_match,compare_rn,rel2,cor2,seg1, seg2,shared_seg,shared_cm,compare_mrca_rn,'left' as flank";
                //"LOAD CSV WITH HEADERS FROM 'file:///inferred_segs.csv' as line FIELDTERMINATOR '|' with split(line.infered_rn,',') as ls,line.seg_left as leftseg,line.left_cm as lc,line.seg_right as rightseg, line.right_cm as rc with split( ls[1],';') as lss, leftseg,lc,ls where leftseg > '  ' unwind lss as lu call { with lu with split(lu,';' ) as lus unwind lus as lusw with split (lusw,':') as lr return toInteger(trim(lr[0])) as l1,lr[1] as l2 } with ls[0] as infer_source,l1 as avatar_rn,l2 as seg_side,leftseg,split(leftseg,':') as lss,lc as left_cm,'infer' as side_method return infer_source,avatar_rn,seg_side,leftseg,lss[0] as lchr,toInteger(lss[1]) as lstrt, toInteger(lss[2]) as lend,left_cm,side_method";
        gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited(cq, "infer_import_left_segs.csv");
        
        // righ flank of overlap
        cq="LOAD CSV WITH HEADERS FROM 'file:///inferred_segs.csv' as line FIELDTERMINATOR '|' with split(line.infered_rn,',') as ls,line.seg_right as rightseg,line.right_cm as lc,line.base as p,line.rn as p_rn,line.close_match as m,line.close_rn as m_rn,line.cor1 as cor,line.rel1 as rel,line.mrca1 as mrca_rn, line.comparison_match as comparison_match, line.comparison_rn as compare_rn,line.seg1 as seg1,line.rel2 as rel2,line.cor2 as cor2,line.seg2 as seg2,line.seg_shared as shared_seg,line.shared_cm as shared_cm,replace(line.mrca2,',',';') as compare_mrca_rn with split( ls[1],';') as lss, rightseg,lc,ls,p,p_rn,m, m_rn,cor,rel,rel2,cor2,mrca_rn,comparison_match,compare_rn,seg1, seg2,shared_seg,shared_cm, compare_mrca_rn where rightseg > '  ' unwind lss as lu call { with lu with split(lu,';' ) as lus unwind lus as lusw with split (lusw,':') as lr return toInteger(trim(lr[0])) as l1,lr[1] as l2 } with ls[0] as infer_source,l1 as avatar_rn,l2 as seg_side,rightseg,split(rightseg,':') as lss,lc as right_cm,'infer' as side_method,p,p_rn,m, m_rn,cor,rel,rel2,cor2,mrca_rn,comparison_match,compare_rn,seg1, seg2,shared_seg, shared_cm,compare_mrca_rn, replace(replace(replace(gen.rel.mrca_str(toInteger(p_rn),toInteger(m_rn)),'[',''),']',''),',',';') as pair_mrca where toInteger(right_cm)>=7 return infer_source,avatar_rn,seg_side,rightseg,lss[0] as rchr,toInteger(lss[1]) as rstrt, toInteger(lss[2]) as rend,right_cm,side_method,p, p_rn,m, m_rn,cor,rel,mrca_rn,pair_mrca,comparison_match,compare_rn,rel2,cor2,seg1, seg2,shared_seg,shared_cm,compare_mrca_rn,'right'  as flank";
                //"LOAD CSV WITH HEADERS FROM 'file:///inferred_segs.csv' as line FIELDTERMINATOR '|' with split(line.infered_rn,',') as ls,line.seg_left as leftseg,line.left_cm as lc,line.seg_right as rightseg, line.right_cm as rc with split( ls[1],';') as lss, rightseg,rc,ls where rightseg > '  ' unwind lss as lu call { with lu with split(lu,';' ) as lus unwind lus as lusw with split (lusw,':') as lr return toInteger(trim(lr[0])) as l1,lr[1] as l2 } with ls[0] as infer_source,l1 as avatar_rn,l2 as seg_side,rightseg,split(rightseg,':') as rss,rc as right_cm,'infer' as side_method return infer_source,avatar_rn,seg_side,rightseg,rss[0] as rchr,toInteger(rss[1]) as rstrt, toInteger(rss[2]) as rend,right_cm,side_method";
        gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited(cq, "infer_import_right_segs.csv");
 

        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///infer_import_left_segs.csv' as line FIELDTERMINATOR '|' merge (s:inferSegment{Indx:toString(line.leftseg), chr:toString(line.lchr), strt_pos:toInteger(line.lstrt), end_pos:toInteger(line.lend), cm:toFloat(line.left_cm)})");
        
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///infer_import_right_segs.csv' as line FIELDTERMINATOR '|' merge (s:inferSegment{Indx:toString(line.rightseg), chr:toString(line.rchr), strt_pos:toInteger(line.rstrt), end_pos:toInteger(line.rend), cm:toFloat(line.right_cm)})");
        
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///infer_import_left_segs.csv' as line FIELDTERMINATOR '|' match (a:Avatar{RN:toInteger(line.avatar_rn)}) match (s:inferSegment{Indx:toString(line.leftseg)}) merge (a)-[r:avatar_segment{infer_source:toString(line.infer_source), avatar_rn:toInteger(line.avatar_rn), avatar_side:toString(line.seg_side), side_method:toString(line.side_method), cm:toFloat(line.left_cm), p:toString(line.p), p_rn:toInteger(line.p_rn) ,m:toString(line.m), m_rn:toInteger(line.m_rn), cor:toFloat(line.cor), rel:toString(line.rel), rel2:toString(line.rel2),cor2:toString(line.cor2),  mrca_rn:toString(line.mrca_rn), compare_match:toString(line.comparison_match), compare_rn:toInteger(line.compare_rn),seg1:ToString(line.seg1),seg2:toString(line.seg2),shared_seg:toString(line.shared_seg),shared_cm:ToFloat(line.shared_cm),compare_mrca_rn:toString(line.compare_mrca_rn),flank:toString(line.flank),pair_mrca:toString(line.pair_mrca)}]->(s)");
        
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///infer_import_right_segs.csv' as line FIELDTERMINATOR '|' match (a:Avatar{RN:toInteger(line.avatar_rn)}) match (s:inferSegment{Indx:toString(line.rightseg)}) merge (a)-[r:avatar_segment{infer_source:toString(line.infer_source),avatar_rn:toInteger(line.avatar_rn),avatar_side:toString(line.seg_side),side_method:toString(line.side_method), cm:toFloat(line.right_cm), p:toString(line.p), p_rn:toInteger(line.p_rn) ,m:toString(line.m), m_rn:toInteger(line.m_rn), cor:toFloat(line.cor), rel:toString(line.rel),rel2:toString(line.rel2), cor2:toString(line.cor2),  mrca_rn:toString(line.mrca_rn), compare_match:toString(line.comparison_match), compare_rn:toInteger(line.compare_rn),seg1:ToString(line.seg1),seg2:toString(line.seg2),shared_seg:toString(line.shared_seg),shared_cm:ToFloat(line.shared_cm),compare_mrca_rn:toString(line.compare_mrca_rn),flank:toString(line.flank),pair_mrca:toString(line.pair_mrca)}]->(s)");
        
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (n:inferSegment) set n:Segment");
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (n:inferSegment) set n.type='infer'");
        
        
        int ffg=0;
        return "";
    }
     
     public static String seg_process(String seg1, String seg2)
     {
        gen.dna.get_hapmap_cm hp = new gen.dna.get_hapmap_cm();
        gen.genlib.handy_Functions hf = new gen.genlib.handy_Functions();
        
        String c1[] = seg1.replace("\"","").split(Pattern.quote(":"));
        String c2[] = seg2.replace("\"","").split(Pattern.quote(":"));
        Long s = Math.max(Long.parseLong(c1[1]), Long.parseLong(c2[1]));
        Long e = Math.min(Long.parseLong(c1[2]), Long.parseLong(c2[2]));
        Double cm_shared = hp.hapmap_cm(c1[0],s,e);
        String seg_shared = c1[0] + ":" + hf.lpad(String.valueOf(s).strip(),9,"0") + ":" + hf.lpad(String.valueOf(e).strip(),9,"0") ;
        String seg_left = "";
        String seg_right = "";
        Double left_cm = 0.0;
        Double right_cm = 0.0;
        
        String ss =  String.valueOf(s).strip();
        String ee = String.valueOf(e).strip();
        ss = hf.lpad(ss, 9, "0");
        ee = hf.lpad(ee, 9, "0");
        String method = "";
        String delimiter ="|";
        
        if (s.compareTo(Long.parseLong(c1[1]))>0)  //rflasnk in c1
        {
              seg_left = c1[0] + ":" + c1[1]+ ":" + ss ;
              left_cm = hp.hapmap_cm(c1[0], Long.parseLong(c1[1]), s);
              method= method + "1 ";
            }

        if (s.compareTo(Long.parseLong(c2[1]))>0)
                {
                seg_left = c2[0]+ ":" + c2[1] + ":" + ss ;
                left_cm = hp.hapmap_cm(c2[0], Long.parseLong(c2[1]), s);
               method = method + "2 ";     
                }
        
        
        if (e.compareTo(Long.parseLong(c1[2]))<0)  //right flank on c1
        {
            seg_right = c1[0] + ":" + ee + ":" + c1[2];
            right_cm = hp.hapmap_cm(c1[0], e, Long.parseLong(c1[2]));
            method = method + "3 ";
        }

        if (e.compareTo(Long.parseLong(c2[2]))<0)  //right flank on c2
        {
            seg_right = c2[0] + ":" + ee + ":"  + c2[2];
            right_cm = hp.hapmap_cm(c2[0], e, Long.parseLong(c2[2]));
            method = method = method + "4 ";
        } 
            
        String q = "\"";
            //int ghh=0;
            return delimiter + seg_shared + q +  delimiter + left_cm + delimiter + q + seg_left + q + delimiter + right_cm + delimiter + q + seg_right + q + delimiter + method.strip();

     }
}
