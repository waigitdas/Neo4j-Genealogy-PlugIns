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
import java.nio.charset.Charset;
import java.util.regex.Pattern;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
//import java.awt.Desktop;
//import java.awt.Desktop;  


public class create_avatars {
    @UserFunction
    @Description("Create DNA avatars")

    public String create_avatar_relatives(
        @Name("ancestor_rn") 
            Long ancestor_rn
  )
   
         { 
             
        get_avatars(ancestor_rn);
         return "";
            }
    
    public static void main(String args[]) {
        get_avatars(4441L);
    }
    
     public static String get_avatars(Long anc_rn) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String cq="";
        String excelFile = "";
        int excelSheetCt=0;
        try{  //avoid error if indices already created
        gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("avatar_segment", "source");
        gen.neo4jlib.neo4j_qry.CreateIndex("Avatar", "fullname");
        gen.neo4jlib.neo4j_qry.CreateIndex("Avatar", "RN");
        gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("avatar_segment", "p");
        gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("avatar_segment", "m");
        gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("avatar_segment", "p_rn");
        gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("avatar_segment", "m_rn");
       gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("avatar_segment", "avatar_rn");
        //gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("avatar_segment", "m_rn");
           } 
        catch(Exception e){}
        
        gen.neo4jlib.neo4j_qry.qry_write("match ()-[r:avatar_segment]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match ()-[r:avatar_avsegment]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match ()-[r:avseg_seg]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match ()-[r:person_avatar]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match ()-[r:avfather]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match ()-[r:avmother]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (d:Avatar) delete d");
   
        //get all descendants who are DNA testers
        cq = "match path=(p:Person{RN:" + anc_rn + "})<-[:father|mother*0..15]-(q:Person) where q.at_DNA_tester in ['Y'] with path,q  with apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect (distinct[x in nodes(path)|x.RN])))) as rns return rns";        
        //string collection of rns in paths from ancestor to descendants
        String paths = gen.neo4jlib.neo4j_qry.qry_to_csv(cq);
        //array of these rns for iteration
        String ca[] = paths.split("\n")[0].replace("[","").replace("]","").split(",");
   
       //Double Tbl[][] = null;
       int nbr_kids = 0;
        int DescList[][] = null ;
        Double coverage[][] = null;  
        int tester_ct=0;
        
        //get descendant rns only for indiv processing
        cq = "match path=(p:Person{RN:" +  anc_rn + "})<-[:father|mother*0..15]-(q:Person{at_DNA_tester:'Y'})  with q, [x in nodes(path)|x.RN] as rns return rns";
        String c[] = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n");
        
        //get max length
        int maxLen=0;
        for (int i=0; i<c.length; i++){
            String cb[] = c[i].split(",");
            if (cb.length>maxLen) { maxLen = cb.length;}
            }

        //other family members (kids, parents) associated with testers. Get's ORDPATHs
        cq= "match path=(p:Person{RN:" + anc_rn + "})<-[:father|mother*0..15]-(q:Person) where q.at_DNA_tester in ['Y'] with q, [x in nodes(path)|x.RN] as rns unwind rns as x call { with x MATCH (p:Person)-[r:child]->(u:Union) where (u.U1=x or u.U2=x) and p.RN in  " + paths + "  RETURN count(*) as ct } with distinct x,ct match path2=(p:Person{RN:" + anc_rn + "})<-[:father|mother*0..15]-(q:Person{RN:x}) with x,ct as children_descendants_who_tested, nodes(path2) as p2, length(path2) as gen,gen.graph.get_ordpath([y in nodes(path2) | y.RN]) as op, q.at_DNA_tester as test_type return x,children_descendants_who_tested,p2[gen-1].RN as parent, gen, op order by op";
                      
        String[] kids = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n");
 
        //instantiate variable to hold descendant data and calculations
        int persons[][] = new int[kids.length][7];
        //persons array will hold data from analytics
        //one row per descendant in the paths
        //2nd dimension with descendant's data
        //  0 = RN of descendant
        //  1 = number of descendant's children who did a DNA test
        //  2 = descendant's parent's RN
        //  3 = generations from the ancestor
        //  4 = index of row
        //  5 = tested
        //  6 = number of paths; for pedigree collapse and endogamy
   
        coverage = new Double[kids.length][2];
        //coverage array dimensions
        //  0 = personal coverage which is 1 for testers and the fraction contributed by children
        //  1 = the fraction the person contributes to the parent. 0.5 when there is a single child and a fraction when siblings also contribute.

        String[] ordpath = new String[kids.length];
 
             //add computed results to arrays
        for (int i=0;i<kids.length; i++)
        {   try{
            String[] sKids = kids[i].split(",");
            persons[i][0] = Integer.valueOf(sKids[0].strip());  //record number
            persons[i][2] =  Integer.valueOf(sKids[2].strip());  ;   //parent
            //persons[i][2] = 0;   //iterating below will add person themself
            persons[i][4] = i;  //index to facilitate lookups with ordering is filtered
        }
        catch(Exception e){
            //error; do nothing in current version
        }
        
        }
        
        //build person 2-D array
        for (int i=0;i<c.length; i++ ){
            String cs[]=c[i].replace("[","").replace("]","").split(",");
            for (int j=0;j<cs.length; j++)
            {
                for (int k=0;k< persons.length; k++) 
                {
                    if (Integer.valueOf(cs[j].strip()).equals(persons[k][0]) && Integer.valueOf(persons[k][3])!=null)
                    {
                        //add child to kids if not there already and increment child count if added
                        try{
                            persons[k][3] = cs.length;  //gen
                            if (persons[k][0] == Integer.valueOf(cs[cs.length-1].strip()))
                            {
                                persons[k][5] = 1;
                            }
                           }
                        catch(Exception e){}
                    }
                }
            }
          }
       
                for (int i=0;i<persons.length;i++)
        {
                for (int j=0; j<kids.length; j++)
            {
                //add kid count
                String[] css = kids[j].split(",");
                if (Integer.parseInt(css[0])==persons[i][0])
                {
                    persons[i][1] = Integer.parseInt(css[1]);
                    persons[i][3] = Integer.parseInt(css[3]);
                   ordpath[i] = css[4].replace("\"","");
                }
            }
        }

        //////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////
        //set up file writers
        //avatar people piple delimited files
        String fn = gen.neo4jlib.neo4j_info.Import_Dir + gen.neo4jlib.neo4j_info.project +  "_avatars_" + anc_rn + "_" + gen.genlib.current_date_time.getDateTime() + ".txt";
        File fnc = new File(fn);
        FileWriter fw = null;
        try{
            fw = new FileWriter(fnc, Charset.forName("UTF8"));
            fw.write("source,imported,match_seg_ct,rows_to_import\n");
        }
        catch(Exception e){}
        
        

        //////////////////////////////////////////////////////////////
        
        //compute cumulative coverage; 
      String delimiter = "\t";
        String s = "RN" + delimiter + "parent" + delimiter +  "gen" + delimiter + "coverage" + delimiter + "kids" + delimiter + "ordpath\n";
        for (int gen=maxLen; gen>-1; gen--)  //iterate generations up the family tree
        {
            for (int xrow=0; xrow<persons.length; xrow++ ){  //iterate rows
                if(persons[xrow][3]==gen){  // row has xrow in processing generaion i
                
               /////////////////////////////////////////////////////////////////
               /////////  TESTER ... 
               if (persons[xrow][5]==1){  // tester
                   
                    //coverage[persons[xrow][4]][0]  = 1.0;
                    tester_ct = tester_ct + 1;
                }
               
               /////////////////////////////////////////////////////////////////
               /////////  NON TESTER ... 
               else // non-tester; get coverage from children previously computed
               {   
 
                   int indv = persons[xrow][0];
                   int indv_row = persons[xrow][4];

                   int parent = persons[xrow][2];  //parent to find
                   nbr_kids = persons[xrow][1];  //kids who tested
                   DescList = new int[nbr_kids][2];   // 0=RN; 1=index in x

                   int kid_ct = 0;
                   for (int t=0; t<persons.length; t++)  //iterare entire list to find kids
                   {
                        if (persons[t][2]==indv) // parent value; k = kid's row
                           {
                            DescList[kid_ct][0] = persons[t][0];
                           
                             DescList[kid_ct][1] = t; 
                                                 
                          kid_ct = kid_ct + 1;
                       }//end kid   
                        
                        if (kid_ct>nbr_kids-1){break;}
                   } //end search for kids
                   
               
            Double[] kid_cov = new Double[nbr_kids];
            Double maxCov = 0.0;
            Double tmp = 0.0;
            }  
                }
            }  //filer for generation
        } //next generation
        
         for (int i=persons.length-1; i>-1; i-- )
         {
             if (persons[i][5]==1) //tested
             {
           process_tester(Long.valueOf(persons[i][0]),fw);

 
             }
         }
       
        
       try{
        fw.flush();
        fw.close();
       }
       catch(Exception e){}
       
       
       //ENHANCEMENTS
       //flag curated avatars
       gen.neo4jlib.neo4j_qry.qry_write("MATCH (n:DNA_Match) where n.curated=1 with collect(n.RN) as rns match (v:Avatar) where v.RN in rns set v.curate=1");
       
       //set avatar side using direct method
       gen.neo4jlib.neo4j_qry.qry_write("MATCH (d:Avatar)-[r:avatar_segment]-(s:Segment) where d.RN=r.p_rn with d,r, case when r.p_side='maternal' then 'maternal' else case when r.p_side='paternal' then 'paternal' else case when replace(r.p_side,'maternal','')<> r.p_side and replace(r.p_side,'paternal','')<> r.p_side then 'both' else case  when r.p_side is null then 'unknown' else '' end end end end as vnode_side set r.avatar_side=vnode_side,r.side_method='direct'");

       gen.neo4jlib.neo4j_qry.qry_write("MATCH (d:Avatar)-[r:avatar_segment]-(s:Segment) where d.RN=r.m_rn with d,r, case when r.m_side='maternal' then 'maternal' else case when r.m_side='paternal' then 'paternal' else case when replace(r.m_side,'maternal','')<> r.m_side and replace(r.m_side,'paternal','')<> r.m_side then 'both' else case when r.m_side is null then 'unknown' else '' end end end end as vnode_side set r.avatar_side=vnode_side,r.side_method='direct'");

       //set avatar side using collateral match pairs
       String fncoll = "avatar_collateral_attribution.txt";
       cq ="MATCH p=(d:Avatar)-[r:avatar_segment]->(s:Segment) where r.avatar_side is null and r.cor<0.25 with distinct d.fullname as fn,d.RN as rn, r.p_rn as prn, r.m_rn as mrn,r.p as p,r.m as m return rn,prn,mrn,gen.rel.mrca_side_attribution(rn,prn,mrn,5) as side,fn,p,m";
       gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited(cq, fncoll);
       
        String  lc = "LOAD CSV WITH HEADERS FROM 'file:///" + fncoll + "' as line FIELDTERMINATOR '|' return line ";
        cq = "match (a:Avatar{RN:toInteger(line.rn)})-[r:avatar_segment{p_rn:toInteger(line.prn),m_rn:toInteger(line.mrn)}]-(s:Segment) where line.side<>'-' set r.avatar_side=line.side, r.side_method='collateral'";
        neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 1000);

        //infere method
        gen.avatar.inferred_segments_overlap ios = new gen.avatar.inferred_segments_overlap();
        ios.infer_segments();
        
        //add c_gen_dist property; delete relationship where 0
       gen.neo4jlib.neo4j_qry.qry_write("MATCH p=()-[r:avatar_segment]->(s:Segment{chr:'0X'}) with r, gen.dna.x_chr_min_genetic_distance(r.avatar_rn,r.source) as xgd set r.x_gen_dist=xgd");
       gen.neo4jlib.neo4j_qry.qry_write("MATCH p=()-[r:avatar_segment{x_gen_dist:0}]->(s:Segment{chr:'0X'})   delete r");
       
       gen.avatar.avatar_parental_cm  gap = new gen.avatar.avatar_parental_cm() ;
       gap.parental_cm();

       //create person_avatar relationship
       gen.neo4jlib.neo4j_qry.qry_write("match (a:Avatar) with a match(p:Person) where p.RN=a.RN merge (p)-[r:person_avatar]->(a) set a.sex=p.sex");
       
       gen.neo4jlib.neo4j_qry.qry_write("match (a:Avatar) with a match(p:Person)-[r:father]->(anc:Person) where p.RN=a.RN with a,p,anc,r with a.fullname as fn,a.RN as rn,anc.RN as arn match (a1:Avatar{RN:rn}) match(a2:Avatar{RN:arn}) merge (a1)-[rp:avfather]->(a2)");
       
       gen.neo4jlib.neo4j_qry.qry_write("match (a:Avatar) with a match(p:Person)-[r:mother]->(anc:Person) where p.RN=a.RN with a,p,anc,r with a.fullname as fn,a.RN as rn,anc.RN as arn match (a1:Avatar{RN:rn}) match(a2:Avatar{RN:arn}) merge (a1)-[rp:avmother]->(a2)");
       
       gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(a1:Avatar)-[r1:avatar_avsegment]->(s:avSegment)<-[r2:avatar_avsegment]-(a2:Avatar) with a1,a2,sum(s.cm) as cm merge (a1)-[r3:av_shared_avsegments{cm:cm}]->(a2)");
       
       
       ////////////////////////////////////////////////////////////////////////
       /////////////////////  REPORTS//////////////////////////////////////////
       ////////////////////////////////////////////////////////////////////////
       ////////////////////////////////////////////////////////////////////////
       
       String anc_name = gen.gedcom.get_family_tree_data.getPersonFromRN(anc_rn, true);
       int ct = 1;
      String UDF_query = "return gen.avatar.create_avatar_relatives(" + anc_rn + ")";
       
        //Count of avatar nodes and relationships
       cq ="match (d:Avatar) return labels(d) as item,count(*) as ct union match ()-[[r:avatar_segment]]->() return type(r) as item,count(*) as ct";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_initial_report_" + anc_rn, "nodes and rel", ct, "", "1:#,###2:####", "", false,"UDF:\n" + UDF_query + "\nthe targetted ancestor is: " + anc_name + "\n\ncypher query:\n" + cq, false);
       ct = ct+1;

       //tracking file generated during iteration of descendants
       gen.excelLib.excel_from_csv.load_csv(fn, excelFile, "load_log", ct, "", "0:#####;1:######;2:###,###;3:######", excelFile, false,"Tracking generated during creation of the avatars. The source is the descendant of the targeted ancestor who is processed and the number of rows imported. \nThe imports overlap and will not be duplicated when encountered in a later kit.\nSegments will be created for each row imported.",false);
       
       //sources
       cq ="MATCH (a:Avatar)-[[r:avatar_segment]]->() with a,r match (p:Person{RN:r.source}) with p.fullname + ' [[' + p.RN + ']]'as source,collect(distinct a.fullname + ' [' + a.RN + ']') as avatars return source,avatars";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_sources", "source_kits", ct, "", "1:######;2:###,###;4:###,###;5:###,###;6:###,###", excelFile, false, "UDF:\n" + UDF_query + "\n\ncypher query:\n" + cq + "\n\nFTDNA kits that were the source of segments attributed to avatars.", false);
       ct = ct+1;

      //avatar list by origin
       cq ="MATCH p=(v:Avatar)-[[r:avatar_segment]]->(s:Segment) with v.fullname as fullname,v.RN as RN, count(*) as seg_ct,apoc.coll.sort(collect(distinct r.source)) as sources return fullname,RN, seg_ct, sources order by seg_ct desc";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_list", "avatar_lineages", ct, "", "1:######;2:###,###;4:###,###;5:###,###;6:###,###", excelFile, false, "UDF:\n" + UDF_query + "\n\ncypher query:\n" + cq + "\n\nList of other avatar RNs associated with the avatar. Segments will be double counted because of the aggregation; they do not reconcile to other sheets.", false);
       ct = ct+1;

       //avatar list by names 
       cq = "MATCH p=(d:DNA_Match)-[[r:match_segment]]->(s:Segment)<-[[rv:avatar_segment]]-(v:Avatar) where d.RN is not null with v.fullname as v,v.RN as RN,collect(distinct d.fullname) as dc,count(*) as source_segment_ct,sum(case when rv.avatar_side='maternal' then 1 else 0 end) as maternal ,sum(case when rv.avatar_side='paternal' then 1 else 0 end) as paternal return v as Avatar,RN as RN, size(dc) as match_ct,source_segment_ct,maternal,paternal,source_segment_ct/size(dc) as aver_ct_per_match,dc as DNA_Matches order by match_ct desc";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Arvatar_names", "avatar_by_name", ct, "", "1:######;2:###,###;3:###,###;4:###,###;5:###,###;6:###,###", excelFile, false, "UDF:\n" + UDF_query + "\n\ncypher query:\n" + cq + "\n\nSegments counts for maternal or paternaln atributions are in columns E and F\n\nList of avatars with their shared matches. Segments will be double counted because of the aggregation; they do not reconcile to other sheets.", false);
       ct = ct+1;
       
       //avatar side categorization
       cq = "MATCH p=()-[[r:avatar_segment]]->() RETURN case when r.avatar_side is null then 'null' else r.avatar_side end as avatar_side,count(*) as seg_ct";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_side", "avatar_side", ct, "", "1:######;2:###,###", excelFile, false, "UDF:\n" + UDF_query + "\n\ncypher query:\n" + cq + "\n\nThis reports the classification of the parential side of the avatar's segments.\nThis exclused the classifications of the side of the match-pair of the segment.", false);
       ct = ct+1;
       
       //avatar segments
       cq ="MATCH p=(v:Avatar)-[[r:avatar_segment]]->(s:Segment) with s.Indx as segment,apoc.coll.sort(collect(distinct v.fullname)) as avatar_relatives,replace(replace(r.pair_mrca,'â¦','⦋'),'â¦','⦌')  as mrca,apoc.coll.sort(collect(distinct r.source)) as sources RETURN segment, sources,size(avatar_relatives) as rel_ct, avatar_relatives, mrca order by segment";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_segs", "segments", ct, "", "1:######;2:###,###", excelFile, false, "UDF:\nr" + UDF_query + "\n\ncypher query:\n" + cq, false);
       ct = ct+1;

       //avatar segment details
       cq ="MATCH p=(a:Avatar)-[[r:avatar_segment]]->(s:Segment) with s, case when r.avatar_side='maternal; paternal' or r.avatar_side='paternal; maternal' then 'both' else r.avatar_side end as side with s,apoc.coll.sort(collect(distinct side)) as side,sum(case when side='both' then 1 else 0 end) as both,sum(case when side='maternal' then 1 else 0 end) as maternal,sum(case when side='paternal' then 1 else 0 end) as paternal,sum(case when side='unknown' then 1 else 0 end) as unknown,sum(case when side is null then 1 else 0 end) as no_category,count(*) as ct return s.Indx, side as source_side,both, maternal,paternal,unknown,no_category, ct as total_source_ct order by s.Indx";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_seg_detail", "segment_details", ct, "", "1:######;2:###;3:###;4:###;5:###;6:###;7:###,###", excelFile, false, "UDF:\nr" + UDF_query + "\n\ncypher query:\n" + cq, false);
       ct = ct+1;

       //avatar shared matches
       cq = "MATCH (k:Avatar)-[[r:avatar_segment]]-(s:Segment) where s.chr<>'0X' with case when r.p<r.m then r.p else r.m end as Match1, case when r.p>r.m then r.p else r.m end as Match2,collect(distinct (s.end_pos-s.strt_pos)/1000000.0) as m,collect(distinct r.cm) as c,count(*) as segment_ct,r.rel as rel with Match1,Match2,m,c,segment_ct,apoc.coll.min(m) as shortest_mbp,apoc.coll.max(m) as longest_mbp,apoc.coll.min(c) as shortest_cm,apoc.coll.max(c) as longest_cm,rel with Match1,Match2,apoc.coll.sum(m) as mbp,apoc.coll.sum(c) as cm,segment_ct,shortest_mbp,longest_mbp,shortest_cm,longest_cm,rel RETURN Match1 as Avatar_Match1,Match2 as Avatar_Match2,rel,segment_ct,mbp,cm,shortest_mbp,longest_mbp,shortest_cm,longest_cm order by cm desc";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_shared_matches", "avatar_shared_matches", ct, "", "2:###,###;3:###,###.##;4:###,###.##;5:#,###.##;6:###,###.##;7:###,###.##;8:###.##;9:##.##", excelFile, false, "UDF:\n" + UDF_query + "\n\ncypher query:\n" + cq, false);
       ct = ct+1;

//       //discovery of avatar matches
//       cq = "MATCH p=(d:DNA_Match)-[[r:match_segment]]->(s:Segment)<-[[rv:avatar_segment]]-(v:Avatar) where d.RN is null with s.Indx as seg,d.fullname as Discovered_Tester,case when d.RN is null then 0 else d.RN end as RN, collect (distinct v.fullname) as matched_avatars,collect(distinct v.RN) as rns return seg,Discovered_Tester,size(matched_avatars) as ct,matched_avatars, gen.rel.mrca_from_cypher_list(rns,10) as mrca order by seg";
//       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_discovery_segs", "Discovered_match_segs", ct, "", "1:######;2:###,###", excelFile, false, "UDF:\nreturn gen.virtual.avatar_relatives(" + anc_rn + ")\n\ncypher query:\n" + cq + "\n\nAvatar segments shared with actual DNA testers who do not have a record number in the curated file and/or family tree.", false);
//       ct = ct+1;

       //gen.genlib.java_wait.wait(1000);
       
       cq = "MATCH p=(d:DNA_Match)-[r:match_segment]->(s:Segment)<-[rv:avatar_segment]-(v:Avatar) where d.RN is null with d.fullname as Discovered_DNA_Tester,case when d.RN is null then 0 else d.RN end as RN,collect(distinct v.fullname + ' [' + v.RN + ']') as matching_avatars,collect(distinct s.Indx) as segs match (k:Kit)-[km:KitMatch]-(d2:DNA_Match) where d2.fullname=Discovered_DNA_Tester return Discovered_DNA_Tester, apoc.coll.sort(collect(distinct k.fullname + case when k.RN is not null then ' [' + k.RN + ']' else '' end)) as source_kits ,matching_avatars, segs";
               //"MATCH p=(d:DNA_Match)-[[r:match_segment]]->(s:Segment)<-[[rv:avatar_segment]]-(v:Avatar) where d.RN is null RETURN d.fullname as Discovered_DNA_Tester,case when d.RN is null then 0 else d.RN end as RN,collect(distinct v.fullname) as matching_avatars,collect(distinct s.Indx) as segs";
       excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "Avatar_discovery", "discovered_matches", ct, "", "1:######;2:###,###", excelFile, true, "UDF:\n" + UDF_query + "\n\ncypher query:\n" + cq + "\n\nAvatar segments shared with actual DNA testers who do not have a record number in the curated file and/or family tree.", false);
       ct = ct+1;
       
       
       return "completed";
    }
     
     /////////////////////////////////////////////////////////////////////
     /////////////////////////////////////////////////////////////////////
     ////////////////////////////////////////////////////////////////////
     public static String process_tester(Long rn, FileWriter fw)
     {
     String fn = "avatar_" + rn + ".csv";
     
    //initial query
     String cq = "with " + rn + " as source MATCH ()-[r:match_segment]->(s) where (r.p_rn=source or r.m_rn=source) and r.pair_mrca is not null with r,source,r.p_rn as prn,r.p_side as p_side,r.m_rn as mrn,r.m_side as m_side, s.Indx as seg,case when r.p_side='paternal' then 'father' else 'mother' end as p_rt,case when r.m_side='paternal' then 'father' else 'mother' end as m_rt where r.p_side in ['paternal','maternal'] match(p:Person{RN:prn})-[r2]->(a:Person) where type(r2)=p_rt with distinct m_rt,source,prn,p_side,mrn,m_side, seg,a.RN as p_parent_rn,r match(p2:Person{RN:mrn})-[r3]->(a2:Person) where type(r3)=m_rt with source,prn,p_side,mrn,m_side,seg,p_parent_rn,a2.RN as m_parent_rn,r with r,source,prn,p_side,p_parent_rn,mrn,m_side,m_parent_rn,gen.rel.mrca_path_attribution(prn,mrn) as path_without_anc,seg,r.cm as cm,r.snp_ct as snp_ct,r.cor as cor,case when r.rel is null then '~' else r.rel end as rel, r.mrca_rn as mrca_rn,replace(replace(r.pair_mrca,'â¦','⦋'),'â¦','⦌') as pair_mrca return distinct source,r.p as p,prn,p_side,p_parent_rn,r.m as m,mrn,m_side,m_parent_rn, size(path_without_anc)-size(replace (path_without_anc,',','')) + 1 as anc_ct, path_without_anc, seg,r.cm as cm,r.snp_ct as snp_ct,r.cor as cor,r.rel as rel, r.mrca_rn as mrca_rn,replace(replace(r.pair_mrca,'â¦','⦋'),'â¦','⦌') as pair_mrca";    
    try{
     gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited(cq, fn);
    //gen.neo4jlib.neo4j_qry.qry_to_csv(cq,fn);
     
    process_avatar(rn,fn, fw);
    }
    catch(Exception e){}
     
     return "" ;
     }
    
     public static void process_avatar(Long rn, String fn, FileWriter fw)
     {

        //read initial csv file
        String c[]= gen.neo4jlib.file_lib.ReadFileByLineWithEncoding(gen.neo4jlib.neo4j_info.Import_Dir + fn).split("\n");
        
        //create new file writer
        String fn2 = fn.replace(".txt","") + "_detail.txt";
        FileWriter fw2 = null;
        
        //write header
        try{
            fw2 = new FileWriter(gen.neo4jlib.neo4j_info.Import_Dir + fn2, Charset.forName("UTF8"));
            fw2.write("avatar_rn|" + c[0] + "\n");
        }
        catch(Exception e){System.out.println("Error 1");}
  
        //interate rows and re-write with row for each RN in path to ancestor
        int newrowct = 0;
         for (int i=1; i<c.length; i++)
         {
             String cs[] =c[i].split(Pattern.quote("|"));
             int ct = 0;
             String anc[] = cs[10].replace(";",",").replace(" ","").split(",");
             for (int a=0; a<anc.length; a ++)
             {
             try{fw2.write(anc[a] + "|");} catch(Exception e){System.out.println("Error 2");}
             for (int j= 0; j<cs.length; j++)
             {
                try{ fw2.write(cs[j]);} catch(Exception e){System.out.println("Error 3  " + e.getMessage());}
                 if (j<cs.length)
                 {
                     try {fw2.write("|");} catch(Exception e){System.out.println("Error 4");} 
                 }
             } //detail
                    try{fw2.write("\n");} catch(Exception e){System.out.println("Error 5");}
                    newrowct = newrowct +1 ;
             }  //ancestor path         
         } // row
        
         try{
  
             fw2.flush();
             fw2.close();
         }
         catch(Exception e){}
         
    
         //get all record numbers from csv file and create Avatar nodes using merge to avoid duplicates
         //reads expanded file with a row for each person in the path between the ancestor and tester
         String cq = "LOAD CSV WITH HEADERS FROM 'file:///" + fn2 + "' as line FIELDTERMINATOR '|' with [toInteger(case when line.avatar_rn is null then 0 else line.avatar_rn end),toInteger(line.source), toInteger(line.prn),toInteger(line.mrn), toInteger(line.p_parent_rn), toInteger(line.m_parent_rn)] as rns,  [y in split(replace(line.mrca_rn,' ',''),',')|toInteger(y)] as mrca with collect(rns) + collect(mrca) as mt return apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(apoc.coll.flatten(collect(mt))))) as rns";
    String rns[] =null;
        
         rns = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n")[0].replace("[","").replace("]","").replace(" ","").split(",");
   
         for (int i=0; i<rns.length; i++)
         {
             //create Avatar nodes
             String p = gen.neo4jlib.neo4j_qry.qry_str("match (p:Person{RN:" + rns[i] + "}) return p.fullname");
             gen.neo4jlib.neo4j_qry.qry_write("merge (d:Avatar{RN:" + rns[i] + ",fullname:'" + p.replace("[","").replace("]","").replace("\"","") + "'})");
         }
          //tracker iteration initial count
         Long strt = gen.neo4jlib.neo4j_qry.qry_long_list("match ()-[r:avatar_segment]->() return count(*) as ct").get(0);
         
        //create avatar_segment relationships 
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///" + fn2 + "' as line FIELDTERMINATOR '|'  match (d:Avatar{RN:toInteger(line.avatar_rn)}) match(s:Segment{Indx:toString(line.seg)})  merge (d)-[r:avatar_segment{avatar_rn:toInteger(line.avatar_rn),source:toInteger(line.source), p_side:toString(line.p_side),m_side:toString(line.m_side),p:toString(line.p),p_rn:toInteger(line.prn),m:toString(line.m),m_rn:toInteger(line.mrn),cm:toFloat(line.cm),snp_ct:toInteger(line.snp_ct),rel:toString(case when line.rel is null then '~' else line.rel end),cor:toFloat(line.cor), path_ct:toInteger(line.anc_ct),path_without_anc:toString(line.path_without_anc),pair_mrca:toString(replace(replace(line.pair_mrca,'â¦','⦋'),'â¦','⦌')),mrca_rn:toString(line.mrca_rn),p_parent_rn:toInteger(line.p_parent_rn),m_parent_rn:toInteger(line.m_parent_rn),method:'asc'}]->(s)");
 
        //tracker iteration ending count
        Long newct = gen.neo4jlib.neo4j_qry.qry_long_list("match ()-[r:avatar_segment]->() return count(*) as ct").get(0) - strt;
        System.out.println(rn + " : " + newct + " : " + c.length + " : " + newrowct);
        try{
            //write to tracker file
            fw.write(rn + "," + newct + "," + c.length + "," + newrowct + "\n");
        }
        catch(Exception e){}
        
//   PLAN B --- individual iteration -- 2x slower in testing, but may have a role in some scenarios?        
//         //cs 
//         //0 source
//         //1 p
//         //2 prn 
//         //3 p_side
//         //4 p_parent_rn
//         //5 m
//         //6 mrn
//         //7 m_mide
//         //8 m_parent_rn
//         //9 ct
//         //10 path_without_ancestors 
//         //11 seg 
//         //12 cm
//         //13 snp_ct
//         //14 cor
//         //15 rel 
//         //16 mrca_rn 
//         //17 oair_mrca 
//         
 
     }
     
}
