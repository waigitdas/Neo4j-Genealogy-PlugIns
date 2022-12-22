/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.graph;

import gen.neo4jlib.neo4j_qry;
import java.io.FileWriter;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class path_topology_shared_paths {
    @UserFunction
    @Description("Generic path analytics.")

    public String triangulate_paths_candidates(
        @Name("path_qry1") 
            String path_qry1,
          @Name("path_qry2") 
            String path_qry2)
   
         { 
             
        get_aligned_paths(path_qry1, path_qry2);
         return "";
            }

        
    public static void main(String args[]) {
        get_aligned_paths("MATCH(p1:Person{RN:4})<-[pp1:path_person]-(f1:fam_path)-[p11:path_intersect] -(i:intersect) with  i.persons as pi return pi","match path=(p1:Person{RN:4})-[r1:father|mother*0..25]->(mrca:Person)<-[r2:father|mother*0..25]-(p2:Person) with distinct p2,collect(distinct mrca.RN) as anc_rns with p2,anc_rns where toInteger(left(p2.BD,4))>1940 and p2.DD='' with anc_rns, collect(distinct p2.RN) as rns unwind rns as x call { with x,anc_rns match path3=(p3:Person{RN:x})-[r3:father|mother*0..25]->(p4:Person) where p4.RN in anc_rns and p4.RN<>p3.RN with p3,p4,reduce(s='', x in nodes(path3)|s + x.RN + ';') as ipath return p3.RN as irn,p4.RN as arn, ipath } return irn,arn, ipath");
    }
    
     public static String get_aligned_paths(String path_qry1, String path_qry2) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        //file for output of raw data needed for reports
        FileWriter fw = null;
        String fn ="path_triangulation_" + gen.genlib.current_date_time.getDateTime() + ".csv";
        
        try{
            String fnc = gen.neo4jlib.neo4j_info.Import_Dir + fn;
            fw = new FileWriter(fnc);
            fw.write("match_rn|proband_anc_rn|match_path|intersect|proband_path\n");
        }
        catch(Exception e){}
 
            int i = 0;
            int j = 0;
            String c = "";
            
        List<Object> ppath = gen.neo4jlib.neo4j_qry.qry_obj_list(path_qry1);
        
        //get in memory list of all putativel living persons relate to the proband and their mrcas with the proband
        //retrieves paths of relatives too
        //tried shortening the path comparison list by selecting less parameter but had minimal benefit
        String mpaths[] = gen.neo4jlib.neo4j_qry.qry_to_csv(path_qry2).split("\n");
        

        //iterate two large arrays
        for (int a=0;a<ppath.size();a++)  // proband's paths
        {
            String obj = ppath.get(a).toString().replace("[","").replace("]","");
            
            for (int b=0; b<mpaths.length-1;b++)  // living relative's paths
            {
                
                //get comparison of two paths to see if they intersect
                //strings of paths need to be cleaned up before submitting
                compare_paths(mpaths[b],obj,fw);
            } //next b
        }  //next a
        
        try{
    fw.flush();
    fw.close();
    }
     catch(Exception e){}

     //run report
     candidate_report(fn);
        
        return "";
    }
     
     public static String compare_paths(String p1, String p2, FileWriter fw)
     {
         int minPathLen = 3;  //path lentgth must be greater than this value
         int i = 0;
         int j=0;
         //comparison function
       
         String[] js = p1.split(",");
        //relatives array 
        // 1. relative's rn
        // 2. ancestor's rn
        // 3. paths between them to be compared to all paths of proband to the same ancestor


        String v = "";
        String sj = "";
        
        String fwstr="";
        
         String s1[] = js[2].split(";"); //match path

         String s2[] = p2.split(",");  //proband path
         
        //no point in comparing if start strings fail to meet criteria
        //this if statement speads processing significantly
        if (Integer.compare(s1.length,minPathLen)==-1 || Integer.compare(s2.length , minPathLen)==-1) { return "";}
       
                         if(Integer.parseInt(js[0])!=1)
                 {
                     int r666=0;
                 }

         int prev_pos=0;

        
         Boolean jfnd = false;
         /////////////////////////////////////////////////////////////////////
         //  i loop == matching relatives
         /////////////////////////////////////////////////////////////////////
         for (i=0;i<s1.length-1;i++)  //trailing ; thus -1
         {
          jfnd = false ;
         /////////////////////////////////////////////////////////////////////
         //  j loop -- proband
         /////////////////////////////////////////////////////////////////////
             for (j=0;j<s2.length; j++)
             {
              //lengths suitable for processing
              // are i and j values the same
              if(Long.parseLong(s1[i].replace("[","").replace("]","").replace("\"","").replace("\\","").strip())==Long.parseLong(s2[j].strip()))
                {
                    jfnd=true;     
                    if(Integer.compare(prev_pos,0)==0)  { prev_pos = i; }
                    break;
               } 
                    
                }  /// end of j loop, on to next i
                
             if (jfnd==true && Integer.compare(prev_pos,0)==0) 
             {
                v = v.concat(s1[i].replace("\"","")  + ",");
                prev_pos = i;
             }
             else if(jfnd==true && Integer.compare(prev_pos,i)==0) 
             {
                    v = v.concat(s1[i].replace("\"","") + ",");
                    
                    prev_pos = prev_pos + 1;
             }
             else {
                // break;
             }
             
               } //next i

//            System.out.println("*" +  v);
 
            if(StringUtils.countMatches(v,",")>minPathLen){
//                    System.out.println("^^^" + "\t" + v);
                    v = v.substring(0, v.length() - 1);
                    sj = js[2].replace("[","").replace("]","").replace("\"","");
                    sj = sj.substring(0, sj.length() - 1);
                    js[2] = js[2].replace(";",",").strip();
                    js[2] = js[2].substring(0, js[2].length() - 2);
                        
                        try{
                          fwstr=js[0] + "|" + js[1] + "|[" + js[2] + "]|[" + v.replace("\"","")  + "]|[" +  p2.replace(" ","")  + "]\n";
                          fw.write(fwstr.replace("\"",""));

                            //fw.write(js[0] + "|" + js[1] + "|" + js[2].replace("[","").replace("]","").replace("\"","")  + "|" + s  + "|" +  p2 + "\n");
                          return v;
                      }
                      catch (Exception e){
                      System.out.println(e.getMessage());
                      }
                       return v;
                }
     
     return"";
//   
     }
     
     public static void candidate_report(String fn)
     {
         //report results by reading and processing prior output
         int ct = 1;
//         String proband = gen.gedcom.get_person.getPersonFromRN(rn, true);
         
         String cq="LOAD CSV WITH HEADERS FROM 'file:///" + fn + "' as line FIELDTERMINATOR '|' with line.proband_anc_rn as proband_anc_rn,line.match_rn as rn,line.match_path as candidate_path,line.intersect as i,collect(distinct line.proband_path) as pp with proband_anc_rn,rn,candidate_path, i,pp return rn as candidate,proband_anc_rn,'[' + candidate_path + ']' as candidate_path, '[' + i + ']' as intersection,'[' + head(pp) + '] ' as proband_path order by  candidate,proband_anc_rn";
     
         String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "path_triangulation_candidates for proband", "detailed_list", ct, "", "0:######;1:######;3:######", "", false,"Data is rendered into a csv file that resides in the import directory\nCypher queries are run against the data in this file. \n\nCypher query:\n" + cq + "\n\n", false);
         ct = ct +1;
         
         //unique candidates
         cq = "LOAD CSV WITH HEADERS FROM 'file:///" + fn + "' as line FIELDTERMINATOR '|'  with line.match_rn as c,apoc.coll.sort(collect(distinct line.proband_anc_rn)) as arn with c,arn  return gen.gedcom.person_from_rn(toInteger(c), true) as candidate,size(arn) as ancestor_ct,arn as ancestor_rns order by size(arn)";
         excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "path_triangulation_candidate", "candidates", ct, "", "1:######;1:######;2:######", excelFile, false,"cypher query:\n" + cq + "\n\n", false);
         ct = ct +1;
         
         //unique ancestors
         cq= "LOAD CSV WITH HEADERS FROM 'file:///" + fn + "' as line FIELDTERMINATOR '|'  with line.proband_anc_rn as p,apoc.coll.sort(collect(distinct line.match_rn)) as crn with p,crn  return gen.gedcom.person_from_rn(toInteger(p), true) as proband_ancestor,size(crn) as candidate_ct,crn as candidate_rns order by size(crn)";
         excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, "path_triangulation_candidate", "ancestors", ct, "", "1:####;2:#####", excelFile, true, "cypher query:\n" + cq, false);
         ct = ct +1;
         
         
         
     }
}
