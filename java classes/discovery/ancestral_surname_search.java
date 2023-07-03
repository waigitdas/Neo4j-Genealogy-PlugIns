/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.discovery;

import gen.neo4jlib.neo4j_qry;
import java.io.File;
import java.io.FileWriter;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class ancestral_surname_search {
    @UserFunction
    @Description("Wildcared search of ancestral surnames submitted by DNA testers. Result score can")

    public String ancestral_surnames(
        @Name("search_term") 
            String search_term

  )
   
         { 
             
        String s = find_matches(search_term);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
        //find_matches("avitts, mcmican, mcmakin,mcmaken,macmican,macmikan");
        find_matches("Abercrombie,Adams,Andersen,Anderson,Arbaugh,Averatts,Averyt,Avitts,Ballowe,Barnard,Bassford,Birch,Bivins,Bone,Bottini,Boyd,Bratton,Brown,Buchanan,Bullock,Burtis,Caldwell,Campbell,Canterbury,Choplin,Christiansen,Coatney,Cochem,Cochems,Cochran,Coker,Crabtree,Cyrus,Denham,Derrin,Doss,Duncan,Dunlap,Eipper,Ellis,Engelke,Erwin,Garland,Gertrude,Gibbs,Gilstrap,Graves,Green,Grissom,Grisson,Hammond,Harris,Haubold,Hiddleson,Horne,Houston,Irwin,Jacobs,Jordan,Kaster,Kerr,Kippenbrock,Layton,Loftin,Loggins,Long,Ludwig,MRCA,Mager,Maughan,McFarland,McMakin,McMican,McMichen,McMicken,Montgomery,Moss,Nelson,Nisbet,Parker,Prater,Price,Quattlebaum,Radcliffe,Ridgeway,Rinehart,Robinson,Rogers,Rosier,Rushing,Savage,Scott,Shemwell,Smith,Spracklin,Steely,Stinnett,Talbot,Taylor,Tierney,Turpell,Watkins,Wells,White,Williams,Witty,Wren,Wright,Young");
        
    }
    
     public static String find_matches(String search_term) 
    {
        Boolean err = false;
        String errMessage="Specific messages by name of failed worksheet:\n";
                
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String[] ss = search_term.split(",");
        String label = ss[0];
        String s = "";
        String cq1 = "";
        String cq2 ="";
        for (int i=0; i<ss.length; i++)
        {
            s = s + "'" + ss[i].toUpperCase().strip() + "'";
            if (i<ss.length-1){s = s + ",";}
        }
            int ct= 1;
            String cq ="CALL db.index.fulltext.queryNodes('ancestor_surnames_names', '" + search_term + "') YIELD node, score WITH [[" + s + "]] as submitted,score,node.p as match, node.m as match_with_surnames, case when size(node.name)>20000 then left(node.name,200) + ' (truncated)' else node.name end as anc_names , apoc.coll.flatten(collect(split(toUpper(replace(node.name,' ','')),'/'))) as anc_list MATCH (m:DNA_Match{fullname:match})-[[rs:match_by_segment]]-(m2:DNA_Match{fullname:match_with_surnames}) with distinct m.fullname as source,case when apoc.coll.contains(submitted,toUpper(trim(m2.surname))) then 'x' else '~' end as own_surname, case when m.RN is null then '~' else m.RN end as source_rn, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.intersection(anc_list,submitted))) as found, match_with_surnames,rs.cm as cm,rs.seg_ct as segs, case when rs.rel is null then '~' else rs.rel end as rel,round(score,2) as score,anc_names as ancestor_list,submitted,anc_list where found<>[[]] with source,source_rn,found,score,match_with_surnames,own_surname,cm,segs,rel,ancestor_list match (n:DNA_Match{fullname:match_with_surnames}) return source,source_rn,found,score,match_with_surnames,case when n.YHG is null then '~' else n.YHG end as YHG, case when n.mtHG is null then '~' else n.mtHG end as mtHG,own_surname,cm,segs,rel,ancestor_list order by rel desc,match_with_surnames,score desc,source"; 
 
      
    String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project + "_" + label + "_ancestor_surnames", "matches_with_surname", ct, "","1:###;3:##.##;6:###.#;7:####;8:####.#########;9:###", "", false,"UDF:\nreturn gen.discovery.ancestral_surnames('" + search_term + "')\n\nCypher query:\n\n" + cq + "\n\nThis search uses the ancestral surnames submitted by ~50% of matches (column E) in a project member's kit (source; Column A).\nWhen multiple surnames from a specific line of interest are searched the matche who share many surnames might be prioritize for future research.\nYou can use wildcards to find variations in the surname.\nSee: https://graphaware.com/neo4j/2019/01/11/neo4j-full-text-search-deep-dive.html \n\n", false);
    
        ct = ct + 1;
  try{
        cq = "CALL db.index.fulltext.queryNodes('ancestor_surnames_names', '" + search_term + "') YIELD node, score WITH [[" + s + "]] as submitted,score,node.p as match, node.m as match_with_surnames, case when size(node.name)>20000 then left(node.name,200) + ' (truncated)' else node.name end as anc_names , apoc.coll.flatten(collect(split(toUpper(replace(node.name,' ','')),'/'))) as anc_list MATCH (m:DNA_Match{fullname:match})-[[rs:match_by_segment]]-(m2:DNA_Match{fullname:match_with_surnames}) with distinct m.fullname as source,case when apoc.coll.contains(submitted,toUpper(trim(m2.surname))) then 'x' else '~' end as own_surname, case when m.RN is null then '~' else m.RN end as source_rn, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.intersection(anc_list,submitted))) as found, match_with_surnames,rs.cm as cm,rs.seg_ct as segs, case when rs.rel is null then '~' else rs.rel end as rel,round(score,2) as score,anc_names as ancestor_list,submitted,anc_list where found<>[[]] return match_with_surnames,apoc.coll.sort(collect (distinct source + ' [[' + source_rn + ']]')) as sources,apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect(distinct found)))) as surnames order by match_with_surnames";
    gen.excelLib.queries_to_excel.qry_to_excel(cq,"rollup", "rollup_by_match", ct, "","1:###;3:##.##;6:###.#;7:####", excelFile,false,"UDF:\nreturn gen.discovery.ancestral_surnames('" + search_term + "')\n\nCypher query:\n\n" + cq, false);
   ct = ct + 1;
   }
    catch(Exception e){
        err=true;
        errMessage = errMessage + "rollup_by_match: " + e.getMessage() + "\n";
    }

  try
  {
    cq="CALL db.index.fulltext.queryNodes('ancestor_surnames_names', '" + search_term + "') YIELD node, score WITH [[" + s + "]]  as submitted,score,node.p as match, node.m as match_with_surnames , apoc.coll.flatten(collect(split(toUpper(replace(node.name,' ','')),'/'))) as anc_list MATCH (m:DNA_Match)-[[rs:match_segment]]-(s) where (rs.p=match or rs.m=match) and (rs.p=match_with_surnames or rs.m=match_with_surnames) with match, match_with_surnames,rs,s, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.intersection(anc_list,submitted))) as found with match, match_with_surnames,rs,s,found where found<>[[]] with match,match_with_surnames,found,collect(distinct rs.rel) as rel,sum(rs.cm) as cm,sum(case when (rs.p=match and rs.p_side='paternal') or (rs.m=match and rs.m_side='paternal') then rs.cm else 0 end) as match_paternal_cm,sum(case when (rs.p=match and rs.p_side='maternal') or (rs.m=match and rs.m_side='maternal') then rs.cm else 0 end) as match_maternal_cm,sum(case when (rs.p=match_with_surnames and rs.p_side='paternal') or (rs.m=match_with_surnames and rs.m_side='paternal') then rs.cm else 0 end) as match_with_surnames_paternal_cm,sum(case when (rs.p=match_with_surnames and rs.p_side='maternal') or (rs.m=match_with_surnames and rs.m_side='maternal') then rs.cm else 0 end) as match_with_surnames_maternal_cm,apoc.coll.sort(collect(distinct s.Indx)) as segs return match, match_with_surnames,found, rel,cm, match_paternal_cm, match_maternal_cm, match_with_surnames_paternal_cm, match_with_surnames_maternal_cm,size(segs) as seg_ct,segs ";
            
    gen.excelLib.queries_to_excel.qry_to_excel(cq,"matches_ancestor_surnames", "match_by_segment", ct, "","4:###.#;5:####.#;6:###.#;7:####.#;8:###.#;9:####", excelFile, false,"UDF:\nreturn gen.discovery.ancestral_surnames('" + search_term + "')\n\nCypher query:\n\n" + cq + "\n\nColumn A are matches with DNA tests in the project\ncolumn B is matches to those in column a who have the surname(s) in their ancestral surname list\nnext columns are the surnames found, relationship if known, shared cM and their side if known and the shared segments\nfor close relationships (parent-child or sibling) the cM will be from both parents, creating a larger number than typically reported,", false);
    ct = ct + 1;
  }
   catch(Exception e){
        err=true;
        errMessage = errMessage + "match by segment: " + e.getMessage() + "\n";
    }
  try
  {
    cq = "CALL db.index.fulltext.queryNodes('ancestor_surnames_names', '" + search_term + "') YIELD node, score WITH [[" + s + "]] as submitted,score,node.p as match, node.m as match_with_surnames , apoc.coll.flatten(collect(split(toUpper(replace(node.name,' ','')),'/'))) as anc_list MATCH (m:DNA_Match)-[[rs:match_segment]]-(s) where (rs.p=match or rs.m=match) and (rs.p=match_with_surnames or rs.m=match_with_surnames) with match, match_with_surnames,rs,s, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.intersection(anc_list,submitted))) as found with match, match_with_surnames,rs,s,found where found<>[[]] with apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect(distinct match) + collect(distinct match_with_surnames)))) as persons, s.Indx as seg return seg ,count(persons) as ct, persons order by seg";
            
    gen.excelLib.queries_to_excel.qry_to_excel(cq,"segments", "segments", ct, "","1:###;3:##.##;6:###.#;7:####", excelFile, false,"UDF:\nreturn gen.discovery.ancestral_surnames('" + search_term + "')\n\nCypher query:\n\n" + cq + "\n\nYou can use wildcards to find variations in the surname.\nSee: https://graphaware.com/neo4j/2019/01/11/neo4j-full-text-search-deep-dive.html ", false);
  }
   catch(Exception e){
        err=true;
        errMessage = errMessage + "segments: " + e.getMessage() + "\n";
    }
  
  
  
   try
  {
    cq = "CALL db.index.fulltext.queryNodes('ancestor_surnames_names', '" + search_term + "') YIELD node, score WITH [[" + s + "]] as submitted,score,node.p as match, node.m as match_with_surnames , apoc.coll.flatten(collect(split(toUpper(replace(node.name,' ','')),'/'))) as anc_list MATCH (m:DNA_Match)-[rs:match_segment]-(s) where (rs.p=match or rs.m=match) and (rs.p=match_with_surnames or rs.m=match_with_surnames) with match, match_with_surnames,rs,s, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.intersection(anc_list,submitted))) as found with match, match_with_surnames,rs,s,found where found<>[] with found,match,match_with_surnames,s,rs,case when rs.p_rn is not null and rs.m_rn is not null then gen.rel.mrca_str(rs.p_rn,rs.m_rn) else '' end as mrca return distinct case when s.chr='0X' then 23 else toInteger(s.chr) end as Chr,s.strt_pos as Start_Location,s.end_pos as End_Location,rs.cm as Centimorgan,rs.snp_ct as SNPs,match + ':' +  match_with_surnames + ' ' + case when rs.rel is null then 'unk' else rs.rel end as Match,'good' as Confidence,replace(replace(mrca,'[',''),']','') as Group,'maternal' as Side,'~' as Notes,'' as Color";
     cq1 = cq.replace("[[","[").replace("]]","]")  ;
     
    gen.excelLib.queries_to_excel.qry_to_excel(cq,"segments", "dna_painter_by mrca", ct, "","0:###;1:#######;2:#######;3:##.##;4:###;7:####", excelFile, false,"The csv file is in the Import directory.\nLoad this into DNA Painter with the setting at 7 cM.\n\nDNA Painter will illustrate the segments of shared matches where the match to the kit has the matching surname in their surname list.\nThis rendering groups the segments by the most recent common ancestor of the match pair.", false);
    
    }
   catch(Exception e){
        err=true;
        errMessage = errMessage + "DNA Painter mrcas: " + e.getMessage() + "\n";
    }
  
    try
  {
    cq = "CALL db.index.fulltext.queryNodes('ancestor_surnames_names', '" + search_term + "') YIELD node, score WITH [[" + s + "]] as submitted,score,node.p as match, node.m as match_with_surnames , apoc.coll.flatten(collect(split(toUpper(replace(node.name,' ','')),'/'))) as anc_list MATCH (m:DNA_Match)-[rs:match_segment]-(s) where (rs.p=match or rs.m=match) and (rs.p=match_with_surnames or rs.m=match_with_surnames) with match, match_with_surnames,rs,s, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.intersection(anc_list,submitted))) as found with match, match_with_surnames,rs,s,found where found<>[] with found,match,match_with_surnames,s,rs,case when rs.p_rn is not null and rs.m_rn is not null then gen.rel.mrca_str(rs.p_rn,rs.m_rn) else '' end as mrca return distinct case when s.chr='0X' then 23 else toInteger(s.chr) end as Chr,s.strt_pos as Start_Location,s.end_pos as End_Location,rs.cm as Centimorgan,rs.snp_ct as SNPs,match + ':' + match_with_surnames + ' ' + case when rs.rel is null then 'unk' else rs.rel end as Match,'good' as Confidence,found as Group,'maternal' as Side,'~' as Notes,'' as Color";
     cq2 = cq.replace("[[","[").replace("]]","]"); 
     
    gen.excelLib.queries_to_excel.qry_to_excel(cq,"segments", "dna_painter_by_surnames", ct, "","0:###;1:#######;2:#######;3:##.##;4:###;7:####", excelFile, true,"The csv file is in the Import directory.\nLoad this into DNA Painter with the setting at 7 cM.\n\nDNA Painter will illustrate the segments of shared matches where the match to the kit has the matching surname in their surname list.\nThis rendering groups the segments by the surname(s) found in the match's surname list.", false);
    
 
  }
    catch(Exception e){
        err=true;
        errMessage = errMessage + "DNA Painter surnames: " + e.getMessage() + "\n";
    }
  
  


  if (err==true)
  {
      cq ="MATCH (n:ancestor_surnames) RETURN count(*) as match_kits_with_ancestral_surname_list ";
          gen.excelLib.queries_to_excel.qry_to_excel(cq,"segments", "error message", ct, "","1:###;3:##.##;6:###.#;7:####", excelFile, true,"'ERROR: one or more of the algorithms failed. Most likely this is a memory error because the surmame list submitted returns too many results\nTry changing the surname list and re-running' as error\n\n" + errMessage, false);

  }
  
  gen.neo4jlib.neo4j_qry.qry_to_csv(cq1, "dna_painter_by_mrca.csv");
  gen.neo4jlib.neo4j_qry.qry_to_csv(cq2,   "dna_painter_by_surnames.csv");
   
  
//  String fn1 = "dna_painter_by_mrca.csv";
//  File f1 = new File(gen.neo4jlib.neo4j_info.Database_Dir +  fn1);
//  FileWriter fw1 = null;
//try{          
//         fw1= new FileWriter(f1);
//         String cq1s = gen.neo4jlib.neo4j_qry.qry_to_csv(cq1);
//         fw1.write(cq1s);
//         fw1.flush();
//         fw1.close();
//         
//}       
//catch(Exception e){}

    
        return "completed";
 }
// catch (Exception e) {return "Error. Try modifying your search term\n\nSee for a deeper dive into full text searching.\n https://graphaware.com/neo4j/2019/01/11/neo4j-full-text-search-deep-dive.html"; }
//    }
     
}
