/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna_painter;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class mss_dna_painter {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String dna_painter_csv(
        @Name("mrca_rn_list") 
            String mrca_rn_list
  )
   
         { 
             
        String s = create_csv(mrca_rn_list);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String create_csv(String rns) 
    {
        String[] colors = "blue,green,orange,purple,teal,red,cyan,olive,beige,brown,pink".split(",");
        String[] rn = rns.split(",");
        String cq ="";
        String cq_all = "";
        gen.gedcom.get_person gp = new gen.gedcom.get_person();
        
        for (int i=0; i<rn.length; i++)
        {
        cq = "MATCH p=(m:MSS{mrca:" + rn[i] + "})-[[r:ms_seg]]->(s:Segment)<-[[r2:match_segment]]-() where r2.cm>=7 and r2.snp_ct>=500 RETURN distinct case when s.chr='0X' then 23 else toInteger(s.chr)  end as Chr,s.strt_pos as Start_Location,s.end_pos as End_Location,r2.cm as Centimorgan,r2.snp_ct as SNPs,case when r2.p<r2.m then r2.p  + ' - ' + r2.m else r2.m  + ' - ' + r2.p  end as Match,'good' as Confidence,'" + gp.person_from_rn(Long.parseLong(rn[i]),true).replace("[","").replace("]","") + "' as Group,'maternal' as Side,'~' as Notes,'" + colors[i] + "' as Color";    
                //"MATCH p=(m:MSS{mrca:" + rn[i] + "})-[[r:ms_seg]]->(s:Segment)<-[[r2:match_segment]]-() where r2.cm>=7 and r2.snp_ct>=500 RETURN distinct case when s.chr='0X' then 23 else toInteger(s.chr)  end as Chr,s.strt_pos as Start_Location,s.end_pos as End_Location,r2.cm as Centimorgan,r2.snp_ct as SNPs,r2.p  + ' - ' + r2.m as Match,'good' as Confidence,'" + gp.person_from_rn(Long.parseLong(rn[i]),true).replace("[","").replace("]","") + "' as Group,'maternal' as Side,'~' as Notes,'" + colors[i] + "' as Color";    
        if (i < rn.length-1) {cq = cq + " union ";}
        
        cq_all=cq_all + cq;
        }
        //create csv for DNA Painter
        gen.neo4jlib.neo4j_qry.qry_to_csv(cq_all.replace("[[","[").replace("]]","]").replace("⦋","[").replace("⦌","]"),gen.neo4jlib.neo4j_info.project + "_mss_dna_painter_" + gen.genlib.current_date_time.getDateTime() +  ".csv");
        
        //create Excel output for reference
        gen.excelLib.queries_to_excel.qry_to_excel(cq_all,gen.neo4jlib.neo4j_info.project +  "_MSS_DNA_Painter_Excel", "DNA_Painter-excel",  1, "", "0:##;1:###,###,###;2:###,###,###;3:####.#;4:#####", "", true,"UDF:\nreturn gen.mss.dna_painter_csv('" + rns +  "')\n\n cypher query:\n" +  cq_all + "\n\nLook in the import directory for the csv file DNA_Painter.csv which can be bulk loaded toDNA Painter.\n\nThe monophylytic ancestors are, by definition, on one side of the family tree. Since GFG does not know this a priori, the default in this outpiut is maternal.\nThis allows DNA Paint to leave all the segments on one set of chrromosomes (maternal).", false);
        return "completed";
    }
}
