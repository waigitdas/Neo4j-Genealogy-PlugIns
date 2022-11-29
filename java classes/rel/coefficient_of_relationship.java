/**
 * Copyright 2021-2023 
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
    @Description("computes the coefficient of relationship (COR) from two RNs. COR is used to estimate the expected shared cm and as a measure of pedigree collapse. Returns zero if there is an error or no relationship." )

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
        
      public static void main(String args[]) {
          cor_calc(1L,600L);
          cor_calc(1L,4L);
          cor_calc(1L,341L);
      }
    
             
        public static double cor_calc(Long rn1, Long rn2){
        double cor=0.0;
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        //get all MRCAs
        try{
        mrca_path_lengths mm = new mrca_path_lengths();
        String[] mrca =  mm.get_mrca_path_len(rn1,rn2).split("\n");
        for (int i=0; i<mrca.length; i++){
            String[] mmm = mrca[i].split(",");
            int anc_rn = Integer.parseInt(mmm[0]);
            int path1 = Integer.parseInt(mmm[1]);
            int path2 = Integer.parseInt(mmm[2]);
            cor = cor + Math.pow(0.5,path1 + path2);
                         
        }
        }
        catch (Exception e){}
        System.out.println(cor);
         return cor;
        }
}
        
