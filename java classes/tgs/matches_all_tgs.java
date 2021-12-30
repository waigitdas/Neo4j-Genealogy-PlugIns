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
public class matches_all_tgs {
   @UserFunction
    @Description("Add triangulation group list as property to DNA_Match and Person nodes. Adds Label DNA_Match, which enables distinctive visualization formating using grass file. The function then infers ancestor TG list for each person in each branch descending from the common ancestor, thereby creating an autosomal haplotree.")
        
    public String matches_tgs(
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
     
     String cq = "match p=(m:DNA_Match)-[rm:match_segment]-(s:Segment)-[rs:seg_seq]-(s2:Segment) where 100>=rm.cm>=7 and rm.snp_ct>=500 and m.ancestor_rn is not null and rm.p_anc_rn is not null and rm.m_anc_rn is not null  with rs.tgid as tg,m order by rs.tgid with m.fullname + ' ⦋' + m.RN + '⦌' as match,collect(distinct tg) as tgs return match,size(tgs) as tg_ct,tgs order by match";
     gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project + "_at_haplotree","haplotree",1,"","","",true,"UDF: return gen.tgs.matches_tgs()\n\ncommon ancestor is " + gen.gedcom.get_family_tree_data.getPersonFromRN(anc_rn, true) + "\n\nthe query\nmatch p=(m:DNA_Match)-[rm:match_segment]-(s:Segment)-[rs:seg_seq]-(s2:Segment) where 100>=rm.cm>=7 and rm.snp_ct>=500 and m.ancestor_rn is not null and rm.p_anc_rn is not null and rm.m_anc_rn is not null  with rs.tgid as tg,m order by rs.tgid with m.fullname + ' ⦋' + m.RN + '⦌' as match,collect(distinct tg) as tgs return match,size(tgs) as tg_ct,tgs order by match",true);
     return "completed";
}
 
    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
}
