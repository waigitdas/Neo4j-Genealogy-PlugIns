/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import gen.rel.mrca_path_lengths;
import static java.lang.Integer.max;
import static java.lang.Integer.min;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

public class DNA_shared{
    @UserFunction
    @Description("computes the shared DNA expected and actual with family tree paths ")

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
        String rw ="";
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
            
            String propositi = gen.gedcom.get_family_tree_data.getPersonFromRN(rn1) + " ; " + gen.gedcom.get_family_tree_data.getPersonFromRN(rn2);
            String anc = gen.gedcom.get_family_tree_data.getPersonFromRN(anc_rn);
          
            gen.rel.relationship rr = new gen.rel.relationship();
            String relationship = rr.relationship_from_path(Long.valueOf(mrca.length),path1,path2);
            
            gen.dna.shared_dna dna = new gen.dna.shared_dna();
           double cm = dna.sharedCM(rn1,rn2);
           cm = Math.round(cm);
       
           gen.dna.shared_cm_dna scm = new gen.dna.shared_cm_dna();
          String sharedCM =  scm.expected_cm(Long.valueOf(mrca.length),path1,path2);
           
            rw = rw + propositi + ", " + relationship + ", " + anc + "," + mmm[1] + ", " + mmm[2] + ", " +  madd +", " + String.valueOf(cor) + ", " + String.valueOf(cm) + ":" + sharedCM +  "\n";
        }

         return rw;
        
        }
}
        
