/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna_painter;

import gen.neo4jlib.neo4j_qry;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class cypher_name_list_to_DNA_Painter {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String DNA_Painter_Query(
        @Name("list") 
            List list,
        @Name("group")
            String group
  )
   
         { 
             
        String s = DNA_Painter_qry(list, group);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String DNA_Painter_qry(List<String> names, String group) 
    {
        String s = gen.genlib.handy_Functions.cypher_list_to_quoted_list(names);
        //String q = "$%^" ;// "\"";
        String dnap = "MATCH p=(m:DNA_Match)-[[r:match_segment]]->(s:Segment) where r.cm>=7 and r.snp_ct>=500 and  m.fullname in [[" + s + "]] with distinct m,s,case when r.p=m.fullname then r.m  else  r.p end as match_pair, r.cm as cm, r.snp_ct as snps return case when s.chr='0X' then 23 else toInteger(s.chr) end as Chr,s.strt_pos as strt_pos,s.end_pos as end_pos,cm,snps,m.fullname + ' : ' + match_pair as match,'good' as Confidence,'" + group + "' as group,'paternal' as side,'' as notes,'green' as color order by s.Indx,m.fullname" ;
                //"return gen.dna_painter.dna_painter_query(" + q + "MATCH p=(m:DNA_Match)-[[r:match_segment]]->(s:Segment) where m.fullname in [[" + s + "]] with distinct m,s,case when r.p=m.fullname then r.m  else  r.p end as match_pair, r.cm as cm, r.snp_ct as snps return case when s.chr='0X' then 23 else toInteger(s.chr) end as Chr,s.strt_pos as strt_pos,s.end_pos as end_pos,cm,snps,m.fullname + ' : ' + match_pair as match,'good' as Confidence,'" + group + "' as group,'paternal' as side,'' as notes,'green' as color order by s.Indx,m.fullname" + q + ")";
        //gen.neo4jlib.neo4j_qry.qry_to_csv(dnap,group + "_cluster_match_dna_painter.csv");
        //gen.excelLib.queries_to_excel.qry_to_excel(dnap, group + "_cluster_match_dna_painter", "segs", 1, "", "", "", false, "", false);
        return dnap;
    }
}
