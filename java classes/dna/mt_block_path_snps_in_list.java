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
import org.apache.commons.lang3.StringUtils;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class mt_block_path_snps_in_list {
//    @UserFunction
//    @Description("in development. process SNP with anomalist values.")
//
//    public List<String> mt_snp_in_list(
//        @Name("block_list") 
//            List<String> block_list,
//        @Name("snp_list")
//            List<String> snp_list
//  )
//   
//         { 
//             
//        //List<String> r = compare_snp_lists(block_list,snp_list);
//         //return r;
//            }
//
//    
//    
//    public static void main(String args[]) {
//        //List<String> s1 = Arrays.asList("RSRS","L1'2'3'4'5'6'7","L2'3'4'5'6'7","L2'3'4'6","L3'4'6","L3'4,L3","M","M1'20'51","M1","M1a","M1a1","M1a1-T16093C","M1a1d","C150T");
//        //List<String> s2 = Arrays.asList("C1250T");
//   
//    //compare_snp_lists(s1,"C105T");
//    
//    }
//    
//     public static List<String> compare_snp_lists(String pl, String rl)
//    {
//        String pls = StringUtils.join(pl, ", ");
//        System.out.println(pls);
//        String ca = "with [" + p1s + "] as blocks, ['C150T'] as compare unwind blocks as x call { with x,compare optional MATCH p=(b:mt_block{name:x})-[r:mt_block_snp]->(v) where v.name in compare RETURN case when v.name is not null then '*' else '' end as b } return collect(b + x) as tagged_block";
////        for (int i=0; i<pl.size(); i++)
////        {
////            for (int j=0; j<rl.size(); j++)
////            {
////                
////            }
////        }
//       List<String> r = Arrays.asList("01:0923052");
//        return r;
//    }
}
