/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.tgs;

import gen.neo4jlib.neo4j_qry;
import java.util.List;
import org.neo4j.driver.types.Path;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 *
 * @author david
 */
public class Create_at_haplotree {
   @UserFunction
    @Description("Add triangulation group list as property to DNA tester Person nodes and adds Label DNA_Match. Then infers ancestor TG list from each branch of their descendants")
        
    public String at_haplotree(
//        @Name("project") 
//            String project,
//        @Name("ancestor_rn") 
//            Long ancestor_rn
    )
    

        {
        String cq = "";
        String r =at_ht();
        return r;
            }
    
   
    public String at_ht() 
    {
     gen.neo4jlib.neo4j_info.neo4j_var_reload();
     gen.rel.anc_rn anc = new gen.rel.anc_rn();
     Long anc_rn = anc.get_ancestor_rn();
     
     String cq = "match p=(m:DNA_Match{ancestor_rn:" + anc_rn + "})-[rm:match_segment{p_anc_rn:" + anc_rn + ",m_anc_rn:" + anc_rn + "}]-(s:Segment)-[rs:seg_seq]-(s2:Segment) where 100>=rm.cm>=7 and rm.snp_ct>=500 with rs.tgid as tg,m order by rs.tgid with m.fullname + ' ⦋' + m.RN + '⦌' as match,collect(distinct tg) as tgs return match,size(tgs) as tg_ct,tgs order by match";
     gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project + "_at_haplotree","haplotree",1,"","","",true,"");
     return "completed";
}
 
    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
}
