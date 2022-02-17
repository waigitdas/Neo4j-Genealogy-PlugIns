/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.regex.Pattern;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

public class discover_tgs_denovo {
   @UserFunction
    @Description("Discovers possible triangulation groups. This will iterate over chromosomes and may discover multiple triangulation groups on each. This function will open up multiple Excel files.")

    public String discover_tgs (
//       @Name("anc_rn")
//        Long anc_rn
  )  
            
    {
    String s =get_tgs();  
    return s;
    }
    
    
    public static String get_tgs() {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String excelFile = "TG_discovery";
        int ct = 0;
        Boolean b = false;
        String matches = "";
           try{
        Long anc_rn =gen.neo4jlib.neo4j_qry.qry_long_list("match (m:DNA_Match) where m.ancestor_rn is not null return distinct m.ancestor_rn as rn").get(0);
        if (anc_rn > 0) {
        
        String schr ="tgid|chr|grp|strt|end|mrca_seg_ct|seg_cm|matches\n";
        int tg = 0;
        
        for (int crsm=1;crsm<22; crsm++){
            String chr = String.valueOf(crsm);
            if (chr.length()==1){chr = "0" + chr; }
            ct = ct + 1;
            
            gen.neo4jlib.neo4j_qry.qry_to_csv("match p= (m:DNA_Match{ancestor_rn:" + anc_rn + "}) -[r:match_segment{m_anc_rn:" + anc_rn + ", p_anc_rn:" + anc_rn + ",p:m.fullname}]-(s:Segment) where s.chr='" + chr + "' and 100>=r.cm>=7 and r.snp_ct>=500 return s.Indx as Indx,s.chr as chr,  s.strt_pos as strt_pos,s.end_pos as end_pos,r.cm as cm,collect(distinct m.fullname) as matches order by s.strt_pos,s.end_pos","seg_seq.csv").split("\n");
           String[] c = gen.neo4jlib.file_lib.readFileByLine(gen.neo4jlib.neo4j_info.Import_Dir + "seg_seq.csv").replace("[","").replace("]","").replace("\"","").split("\n");
        
        //String s = "lvl|db|chr|s|e|smin|emax\n";
        int lvl=1;
        int grp =0;
        int grpstrt = 0;
        int grpend = 0;
        int grpct=0;
        gen.dna.get_hapmap_cm hg = new gen.dna.get_hapmap_cm();
        //gen.dna.segment_desc_matches dm = new gen.dna.segment_desc_matches();
        Boolean boolOverlap =false;
        int icurr = 0;
        gen.neo4jlib.file_lib.openExcelFile=false;
        
        //instantiate function to get matches at segment
         gen.dna.matches_at_seg mats = new gen.dna.matches_at_seg();

        for (int i=1 ;i<c.length-1;i++) {
            boolOverlap=false;
  
            String[] ss1 = c[i].split(Pattern.quote(","));
            String[] ss2 = c[i+1].split(Pattern.quote(","));
            if (i == 1) {
                grpstrt = Integer.parseInt(ss1[2]);
            }
            if (Integer.parseInt(ss1[2])<Integer.parseInt(ss2[3]) && Integer.parseInt(ss1[3])>Integer.parseInt(ss2[2]) ) {
                grpend = Math.max(grpend,Integer.parseInt(ss2[2]));
                grpct = grpct +1 ;
                boolOverlap=true;
            }
            else {  // group completed
                //get cm from HapMap and boundaries
                 double grp_cm = hg.hapmap_cm(chr, Long.valueOf(grpstrt),Long.valueOf(grpend));
  
                //do not use short egment tgs 
                if (grp_cm > 7 && grp_cm < 100){
                    tg = tg + 1 ;
                    grp=grp+1;
                    grpct = grpct + 1;
                    gen.neo4jlib.file_lib.openExcelFile=false;
                    gen.neo4jlib.file_lib.currExcelFile  = mats.get_matches(chr,Long.valueOf(grpstrt),Long.valueOf(grpend),gen.neo4jlib.file_lib.currExcelFile);
                    schr = schr + String.valueOf(tg) + "|" + chr + "|" + String.valueOf(grp) + "|" +  String.valueOf(grpstrt) + "|" + String.valueOf(grpend) + "|" + String.valueOf(grpct) + "|" + String.valueOf(grp_cm) + "|" + matches +  "\n" ;
                   grpstrt = Integer.parseInt(ss2[2]);  //next segment
                   grpct=0;
                }
         }
        }  //next grp
  
        //last grp after exiting for loop
        double grp_cm = hg.hapmap_cm(chr, Long.valueOf(grpstrt),Long.valueOf(grpend));
        if (grp_cm > 7 && grp_cm < 100){
        tg = tg + 1;  
        grp=grp+1;
        //matches = dm.get_matches(chr, Long.valueOf(grpstrt),Long.valueOf(grpend) );
       gen.neo4jlib.file_lib.openExcelFile=false;
        matches = mats.get_matches(chr,Long.valueOf(grpstrt),Long.valueOf(grpend),gen.neo4jlib.file_lib.currExcelFile);
        schr = schr + String.valueOf(tg) + "|" + chr + "|" + String.valueOf(grp) + "|" +  String.valueOf(grpstrt) + "|" + String.valueOf(grpend) + "|" + String.valueOf(grpct) + "|" + String.valueOf(grp_cm) + "|" + matches  + "\n" ;
        }
        
        }  //next chr
  
        String fn = gen.neo4jlib.neo4j_info.Import_Dir + "tg_summary" + gen.genlib.current_date_time.getDateTime() + ".csv";
        gen.neo4jlib.file_lib.writeFile(schr.replace("|",", "),fn );
        try{
           gen.neo4jlib.file_lib.openExcelFile=true;
            //if (ct == 1) {
            gen.excelLib.excel_from_csv.load_csv(fn, "tgs" + ct, "tgs" + ct, ct, "", "0:###;1:###;2:##;3:###,###,###;4:###,###,###;5:###;6:####.#", gen.neo4jlib.file_lib.currExcelFile,gen.neo4jlib.file_lib.openExcelFile,"ct=1",false);
//            }
//            else {
//                if (ct==21) {b=true;}
//            gen.excelLib.excel_from_csv.load_csv(fn, "tgs" + ct, "tgs" + ct, ct, "", "0:###;1:###;2:##;3:###,###,###;4:###,###,###;5:###;6:####.#", excelFile, b,"",false);
//          
//            }
            
            //String csvFile,String FileNm,String SheetName, int SheetNumber, String ColWidths, String colNumberFormat, String ExistingExcelFile, Boolean OpenFile
        }
        catch (Exception e) {return e.getMessage(); }
        
        gen.neo4jlib.file_lib.currExcelFile="";
        return "completed: identified  " + String.valueOf(tg) + " putative triangulation groups."; 
//      
        
        }
        else {
            gen.neo4jlib.file_lib.currExcelFile="";
            gen.neo4jlib.file_lib.openExcelFile=true;
            return "Error: is ancestor_rn set?";}
        }
        
        catch(Exception e) {
            gen.neo4jlib.file_lib.currExcelFile="";
            return "ancestor_rn not yet set\n\n" + e.getMessage() + "\n\n" ;
     } 
        }  
       

}
