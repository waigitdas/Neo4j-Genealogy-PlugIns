/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import java.io.File;
import java.io.FileWriter;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class de_novo_shared_matches {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String create_shared_matches(
//        @Name("rn1") 
//            Long rn1,
//        @Name("rn2") 
//            Long rn2
  )
   
         { 
             
        sm();
         return "";
            }

    
    
    public static void main(String args[]) {
        sm();
    }
    
     public static String sm()
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        //get match_segment list for processing
        String cq = "match p=(m1:DNA_Match)-[r1:match_segment]->(s1:Segment) where r1.p='David A Stumpf' and r1.m=m1.fullname and m1.fullname > '  '  return m1.fullname as match,s1.chr as chr,s1.strt_pos as s, s1.end_pos as e,s1.Indx as indx order by m1.surname";
//        String c[] = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n");
//        
//        //set up variables needed
//        String m1 = "";
//        String m2 = "";
//        String seg = "";
//        Double cm = 0.0;
//
//        //instantiate hapmap function for calls
//        gen.dna.get_hapmap_cm hp = new gen.dna.get_hapmap_cm();
//
//        //set up file to hold in common with data and writer 
//        String fn = "de_novo_match_seg.csv";
//        File f = new File(gen.neo4jlib.neo4j_info.Import_Dir + fn);
//        FileWriter fw = null;
//        try
//        {
//             fw = new FileWriter(f);
//             fw.write("match1|match2|seg|cm\n");
//        }
//        catch(Exception e){}
//        
//        //iterate list
//        //identify overlapping segments between two rows in the list with different matches to proband
//        for (int i=0; i<c.length; i++)
//        {
//            String ci[] = c[i].split(",");
//            for (int j=0;j<c.length; j++)
//            {
//                String cj[] = c[j].split(",");
//                if (ci[0].compareTo(cj[0])<0 && ci[1].compareTo(cj[1])==0 && ci[2].compareTo(cj[2])<1 && ci[3].compareTo(cj[3])>-1)
//                {
//                    //get max start and min end positions for overlap
//                    //create new segment
//                    //calculate its cM
//                    //save row to csv file
//                    Long s = Math.max(Long.parseLong(ci[2]), Long.parseLong(cj[2]));
//                    Long e = Math.min(Long.parseLong(ci[3]), Long.parseLong(cj[3]));
//                    cm = hp.hapmap_cm(ci[1].replace("\"",""),s,e);
//                    seg = ci[1].replace("\"","") + ":" + gen.genlib.handy_Functions.lpad(Long.toString(s), 9, "0") + ":" + gen.genlib.handy_Functions.lpad(Long.toString(e), 9, "0");
//                    if (cm.compareTo(7.0)>0)
//                    {
//                    try
//                    {
//                        fw.write(ci[0].replace("\"","") + "|" + cj[0].replace("\"","") + "|" + seg + "|" + cm + "\n");
//                        fw.flush();
//                    }
//                    catch(Exception ex){}
//                    }  //end if for printing
//                    
//                }
//            }
//        }
//            try
//            {
//                fw.flush();
//                fw.close();
//            }
//            catch(Exception e2){}
//            
         
        cq = "";
        gen.neo4jlib.neo4j_qry.qry_write("match (s:Segment) set s.ftdna=1");
         
        //merge to create new segments
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///de_novo_match_seg.csv' as line FIELDTERMINATOR '|' merge (s:Segmen{Indx:toString(line.seg)})");
      
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///de_novo_match_seg.csv' as line FIELDTERMINATOR '|' match (s:Segment{Indx:toString(line.seg)}) set s.icw=1");
                
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///de_novo_match_seg.csv' as line FIELDTERMINATOR '|' match (m1:DNA_Match{fullname:toString(line.match1)}) match (m2:DNA_Match{fullname:toString(line.match2)}) with m1,m2,sum(toFloat(line.cm)) as shared_cm,count(*) as seg_ct merge(m1)-[r:icw{shared_cm:shared_cm,seg_ct:seg_ct}]->(m2)");
        
        gen.neo4jlib.neo4j_qry.qry_write("CALL gds.graph.drop('icw')");
        
        gen.neo4jlib.neo4j_qry.qry_write("CALL gds.graph.project.cypher('icw','MATCH (m:DNA_Match) RETURN id(m) AS id', 'MATCH (m)-[r:icw]->(m2:DNA_Match) where 3500>r.shared_cm>35 RETURN id(m) AS source, id(m2) AS target, r.cm as weight',{readConcurrency: 4,validateRelationships:FALSE} ) YIELD graphName AS graph, nodeQuery, nodeCount AS nodes, relationshipQuery, relationshipCount AS rels return nodes, rels");
        
           gen.neo4jlib.neo4j_qry.qry_write("CALL gds.beta.modularityOptimization.stream('icw', {relationshipWeightProperty:'weight',tolerance:0.0000001}) YIELD nodeId, communityId with nodeId, gds.util.asNode(nodeId).fullname AS name, communityId as cid with nodeId,cid,name order by name with cid,collect(name) as matches,cid as community with community,matches,size(matches) as ct where ct>2 with community,ct, matches order by ct desc return community,ct,matches");
           
                
        return "";
    }
}
