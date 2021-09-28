/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;
    import java.util.List;
    import gen.neo4jlib.neo4j_qry;
    import org.neo4j.procedure.Description;
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;
    //import gen.excelLib.queries_to_excel;

public class tg_matches {
    @UserFunction
    @Description("List of matches at each triangulation group")

    public String tg_match_summary(
//        @Name("FileNm") 
//        String FileNm
////        @Name("delimiter") 
//        String delimiter
    )   
        { 
        String s = tg_match_sum();
                return s;
        }
        
        
        
    public static String tg_match_sum() {
        String FileNm = "tg_summary";
        String cq = "MATCH p=(m:DNA_Match)-[r:match_tg]->(t:tg) where m.RN is not null with t,m,  trim(m.fullname)  as mm with t,m,mm order by mm with t,collect(mm + '; ') as matches,collect(m.RN) as rns with t,matches RETURN t.tgid as tg,t.chr as chr, t.strt_pos as strt_pos,t.end_pos as end_pos,t.cm as cm,size(matches) as ct,matches order by chr,strt_pos,end_pos";
        //String cq,String FileNm,String SheetName, int SheetNumber, String ColWidths, String colNumberFormat, String ExcelFile, Boolean OpenFile
       String s = gen.excelLib.queries_to_excel.qry_to_excel(cq,FileNm,"TG matches",1,"0:5;2:15;3:15;5:8","0:####;1:##;2:###,###,###;3:###,###,###;4:###.0;5:###","",true);  
       return "Completed";
    }
}
        
