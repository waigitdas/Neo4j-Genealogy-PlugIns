/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.tgs;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.driver.types.Path;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 *
 * @author david
 */
public class tg_chr_seq_kit_visualization {
//    @UserFunction
//    @Description("description here")
//
//    public Path tg_chr_tester_graph(
//        @Name("tg_id") 
//            Long tg_id,
//        @Name("ancestor_rn") 
//            Long ancestor_rn
//  )
   
//         { 
             
//         Path rslt = get_visualization(tg_id,ancestor_rn);
//         return rslt;
 //           }

    
    
//    public static void main(String args[]) {
//        // TODO code application logic here
//    }
    
//     public Path get_visualization(Long tgid,Long ancestor_rn) 
//    {
//        String cq = "match (t:tg{tgid:" + tgid + "}) match p= shortestpath((s1:Segment)-[rs:seg_seq*0..99]->(s2:Segment)) where s1.strt_pos=t.strt_pos and s2.end_pos=t.end_pos with p match (t)-[rt:match_tg]-(m1:DNA_Match{ancestor_rn:" + ancestor_rn + "}) with p, collect (m1.fullname) as mc match (m:DNA_Match{ancestor_rn:" + ancestor_rn + "})-[rm:match_segment]-(s3:Segment) where s3 in nodes(p) and (rm.m in mc or rm.p in mc) and rm.p=m.fullname with p,m ,rm return p,m,rm";
//        //Path p = gen.neo4jlib.neo4j_qry.qry_obj_list(cq);
//        
//        return p;
//    }
}
