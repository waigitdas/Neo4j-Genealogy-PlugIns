/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import java.util.Arrays;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class mt_snp_list {
    @UserFunction
    @Description("in development. process SNP with anomalist values.")

    public List<String> mt_snp_in_list(
        @Name("proband_list") 
            List<String> proband_list,
        @Name("ref_list") 
            List<String> ref_list
  )
   
         { 
             
        List<String> r = compare_snp_lists(proband_list,ref_list);
         return r;
            }

    
    
    public static void main(String args[]) {
        List<String> s1 = Arrays.asList("A16129G", "T16187C", "C16189T", "T16223C", "G16230A", "T16278C", "C16311T", "T16356C	A247G", "G499A", "522.1A", "522.2C", "309.1C", "315.1C", "522.3A", "522.4C", "A769G", "A825t", "A1018G", "A1811G", "A2758G", "C2885T", "T3594C", "G4104A", "T4312C", "T4646C", "T5999C", "A6047G", "G7146A", "T7256C", "A7521G", "T7705C", "T8468C", "T8655C", "G8701A", "C9540T", "G10398A", "T10664C", "A10688G", "C10810T", "C10873T", "C10915T", "C11332T", "T11339C", "A11467G", "A11914G", "A12308G", "G12372A", "T12705C", "G13105A", "G13276A", "T13506C", "A13528G", "C13565T", "T13650C", "C14620T", "T15693C");
        List<String> s2 = Arrays.asList("A10688G", "A11914G", "A16129G", "A247G", "A2758G", "A825t", "A9221G", "C10810T", "C10915T", "C146T", "C150T", "C152T", "C16189T", "C16311T", "C182T", "C195T", "C2885T", "G13105A", "G13276A", "G13590A", "G15301A", "G16230A", "G16390A", "G7146A", "G8206A", "T10115C", "T10664C", "T13506C", "T146C!", "T152C!", "T16187C", "T2416C", "T4312C", "T8468C", "T8655C");
   
    compare_snp_lists(s1,s2);
    
    }
    
     public static List<String> compare_snp_lists(List<String> pl, List<String> rl)
    {
        for (int i=0; i<pl.size(); i++)
        {
            for (int j=0; j<rl.size(); j++)
            {
                
            }
        }
        List<String> r = Arrays.asList("01:0923052");
        return r;
    }
}
