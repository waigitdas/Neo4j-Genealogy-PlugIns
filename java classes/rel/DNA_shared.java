/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_info;
import gen.neo4jlib.neo4j_qry;
import gen.rel.mrca_path_lengths;
import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import static java.lang.Integer.max;
import static java.lang.Integer.min;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

public class DNA_shared{
    @UserFunction
    @Description("computes the shared DNA expected and actual observed with family tree paths ")

    public String shared_DNA(
        @Name("rn1")
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
    {
        String cr = calc_shared_DNA(rn1,rn2);
        return cr;
        
    }
         
             
        public static String calc_shared_DNA(Long rn1, Long rn2){
       
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        //get all MRCAs
        double cor = 0.0;
        String fname = neo4j_info.Import_Dir + "shared_dna_" + gen.genlib.current_date_time.getDateTime() + ".csv";
        File fn = new File(fname);
        try{
        Writer fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fn), "UTF-8"));
        //FileWriter fw = new FileWriter(fn);
        fw.write("propositi, relationship, ancestors,path1, path2, genetic_distance, COR, observed_cm, expected_cm\n") ;
        mrca_path_lengths mm = new mrca_path_lengths();
        String[] mrca =  mm.get_mrca_path_len(rn1,rn2).split("\n");
        for (int i=0; i<mrca.length; i++){
            String[] mmm = mrca[i].split(",");
            Long anc_rn = Long.parseLong(mmm[0]);
            Long path1 = Long.parseLong(mmm[1]);
            Long path2 = Long.parseLong(mmm[2]);
            double cor_i = Math.pow(0.5,path1 + path2);
            cor = cor + cor_i;
            Long madd = path1 + path2;
            double shared_dna = 0.0;
            //String Indx = String.valueOf(mrca.length) + ":" + String.valueOf(max(path1.intValue(),path2.intValue())) + ":" + String.valueOf(min(path1.intValue(), path2.intValue())) ;
            
            String propositi = gen.gedcom.get_family_tree_data.getPersonFromRN(rn1, false) + " ; " + gen.gedcom.get_family_tree_data.getPersonFromRN(rn2, false);
            String anc = gen.gedcom.get_family_tree_data.getPersonFromRN(anc_rn, false);
          
            gen.rel.relationship rr = new gen.rel.relationship();
            String relationship = rr.relationship_from_path(Long.valueOf(1),path1,path2);
            
            //cm = Math.round(cm);

            gen.dna.shared_cm_dna scm = new gen.dna.shared_cm_dna();
            String exp_sharedCM =  scm.expected_cm(Long.valueOf(1),path1,path2);
            
            fw.write(propositi + ", " + gen.genlib.handy_Functions.fix_str(relationship) + ", " + anc + "," + mmm[1] + ", " + mmm[2] + ", " +  madd +", " + String.valueOf(cor_i) + ", , " + gen.genlib.handy_Functions.fix_str(exp_sharedCM) +  "\n");
        }
        
            //String exp_sharedCM2 =  scm.expected_cm(Long.valueOf(1),path1,path2);

                    
            gen.dna.shared_dna dna = new gen.dna.shared_dna();
            String cm = dna.sharedCM(rn1,rn2);
            if (cm.equals("")) {cm = "unknown because neither person has DNA results in the project.";}
            
            gen.rel.rel_from_rns rr = new gen.rel.rel_from_rns();
            String all_rel = rr.relationship_from_RNs(rn1,rn2);

            gen.dna.shared_cm_from_rel dr = new gen.dna.shared_cm_from_rel();
            String scm = dr.getCM(all_rel);
            
            fw.write("," + all_rel  + ",,,,," + String.valueOf(cor) + "," + gen.genlib.handy_Functions.fix_str(cm) + ",");
            fw.flush();
            fw.close();
            int total_cm = 7200;
            gen.excelLib.excel_from_csv.load_csv(fname, "shared_dna","cor",1, "","3:###;4:###;5:###;6:#.#######","",true,"The total COR is " + cor + "\nFrom the shared centimorgan project the expected value and range is " + scm + " cm.\nThe observed shared DNA is " + cm + ".\nThe predicted DNA is " + cor + " x " + total_cm +" = " + cor*total_cm + " cm\n\nUDF:\nreturn gen.rel.shared_DNA(" + rn1 + "," + rn2 +")\n\nThe coefficient of relationship (COR) is a measure of pedigree collapse resulting from ancstors appearing more that one in the family tree.\nThe paths are the generations to the common ancestor for each person in the analysis.\n\nreferences:\nhttps://www.yourdnaguide.com/ydgblog/2019/7/26/pedigree-collapse-and-genetic-relationships\nhttp://www.genetic-genealogy.co.uk/Toc115570135.html\nhttps://isogg.org/wiki/Coefficient_of_relationship",false);
            //Desktop.getDesktop().open(fn);
        }
        catch (Exception e) {}
        
      
 
        return "completed";
        }
}
        
