/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import gen.rel.mrca_path_lengths;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

public class coefficient_of_relationship {
    @UserFunction
    @Description("computes the COR from two RNs")

    public double compute_cor(
        @Name("rn1")
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
    {
        double cr = cor_calc(rn1,rn2);
        return cr;
        
    }
         
             
        public static double cor_calc(Long rn1, Long rn2){
        double cor=0.0;
        
        //get all MRCAs
        mrca_path_lengths mm = new mrca_path_lengths();
        String[] mrca =  mm.get_mrca_path_len(rn1,rn2).split("\n");
        for (int i=0; i<mrca.length; i++){
            String[] mmm = mrca[i].split(",");
            int anc_rn = Integer.parseInt(mmm[0]);
            int path1 = Integer.parseInt(mmm[1]);
            int path2 = Integer.parseInt(mmm[2]);
            cor = cor + Math.pow(0.5,path1 + path2);
                         
        }
         return cor;
        }
}
        
