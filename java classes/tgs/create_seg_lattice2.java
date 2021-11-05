/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.regex.Pattern;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

public class create_seg_lattice2 {
   @UserFunction
    @Description("Creates a de Bruign tree for segments with seg_seq edge. Used in identifying triangulation groups.")

    public String seq_lattice2 (
           
  )  
            
    {
    String s =make_lattice();  
    return s;
    }
    
    
    public static String make_lattice() {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        String schr ="tgid|chr|grp|strt|end|mrca_seg_ct|seg_cm|matches|MRCAs\n";
        int tg = 0;
        
        for (int crsm=1;crsm<22; crsm++){
            String chr = String.valueOf(crsm);
            if (chr.length()==1){chr = "0" + chr; }

            
            gen.neo4jlib.neo4j_qry.qry_to_csv("match p= (m:DNA_Match{ancestor_rn:41}) -[r:match_segment{m_anc_rn:41, p_anc_rn:41,p:m.fullname}]-(s:Segment) where s.chr='" + chr + "' and 100>=r.cm>=7 and r.snp_ct>=500 return s.Indx as Indx,s.chr as chr,  s.strt_pos as strt_pos,s.end_pos as end_pos,r.cm as cm,collect(distinct m.fullname) as matches order by s.strt_pos,s.end_pos","seg_seq.csv").split("\n");
           String[] c = gen.neo4jlib.file_lib.readFileByLine(gen.neo4jlib.neo4j_info.Import_Dir + "seg_seq.csv").replace("\"","").split("\n");
        
        
        String s = "lvl|db|chr|s|e|smin|emax\n";
        int lvl=1;
        int grp =0;
        int grpstrt = 0;
        int grpend = 0;
        int grpct=0;
        gen.dna.get_hapmap_cm hg = new gen.dna.get_hapmap_cm();
        gen.dna.segment_desc_matches dm = new gen.dna.segment_desc_matches();
        Boolean boolOverlap =false;
        int icurr = 0;
        String matches;
        //String chr="";
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
                 double grp_cm = hg.hapmap_cm(chr, Long.valueOf(grpstrt),Long.valueOf(grpend));
  
                if (grp_cm > 7 && grp_cm < 100){
                    tg = tg + 1 ;
                    grp=grp+1;
                    grpct = grpct + 1;
                    //grpend = Math.max(grpend,Integer.parseInt(ss2[2]));
                    matches = dm.get_matches(chr, Long.valueOf(grpstrt),Long.valueOf(grpend) );
                    s = s + String.valueOf(grp) + "|" +  String.valueOf(i) + "|" + chr + "|" + ss1[0] + "|" + ss2[0] + "|" + boolOverlap + "\n";
                    gen.neo4jlib.file_lib.writeFile(s,gen.neo4jlib.neo4j_info.Import_Dir + "seg_lattice_" + chr + ".csv" );
                    schr = schr + String.valueOf(tg) + "|" + chr + "|" + String.valueOf(grp) + "|" +  String.valueOf(grpstrt) + "|" + String.valueOf(grpend) + "|" + String.valueOf(grpct) + "|" + String.valueOf(grp_cm) + "|" + matches + "\n" ;
                   grpstrt = Integer.parseInt(ss2[2]);  //next segment
                   grpct=0;
                }
         }
        }  //next grp

        double grp_cm = hg.hapmap_cm(chr, Long.valueOf(grpstrt),Long.valueOf(grpend));
        if (grp_cm > 7 && grp_cm < 100){
        tg = tg + 1;  
        grp=grp+1;
        matches = dm.get_matches(chr, Long.valueOf(grpstrt),Long.valueOf(grpend) );
         //grpend = Math.max(grpend,Integer.parseInt(ss2[2]));
        schr = schr + String.valueOf(tg) + "|" + chr + "|" + String.valueOf(grp) + "|" +  String.valueOf(grpstrt) + "|" + String.valueOf(grpend) + "|" + String.valueOf(grpct) + "|" + String.valueOf(grp_cm) + "|" + matches + "\n" ;
        }
        
        }  //next chr
        gen.neo4jlib.file_lib.writeFile(schr.replace("|",", "),gen.neo4jlib.neo4j_info.Import_Dir + "seg_lattice_summary.csv" );
        return "completed: identified  " + String.valueOf(tg) + " putative triangulation groups."; 
        
        }  
       

}
