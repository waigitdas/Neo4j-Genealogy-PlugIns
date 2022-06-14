///**
// * Copyright 2022 
// * David A Stumpf, MD, PhD
// * Who Am I -- Graphs for Genealogists
// * Woodstock, IL 60098 USA
// */
//package gen.load;
//
//import gen.neo4jlib.neo4j_qry;
//import org.neo4j.procedure.Description;
//import org.neo4j.procedure.Name;
//import org.neo4j.procedure.UserFunction;
//
//
//public class rel_load {
//    @UserFunction
//    @Description("Template used in creating new functions.")
//
//    public String load_rel_property(
//  )
//   
//         { 
//             
//        rel_setup();
//         return "";
//            }
//
//    
//    
//    public static void main(String args[]) {
//        // TODO code application logic here
//    }
//    
//public String rel_setup(){
//        gen.neo4jlib.neo4j_info.neo4j_var();
//        gen.neo4jlib.neo4j_info.neo4j_var_reload();
//                
//        //gen.rel.add_rel_property arp = new gen.rel.add_rel_property();
//        //arp.add_relationship_property();
//
//
//    //chr_cm node
//    try{
//String cq = "MATCH (s:Segment) with s.chr as c,min(s.strt_pos) as s,max(s.end_pos) as e with c,s,e, gen.dna.hapmap_cm(case when c='0X' then 'X' else c end,s,e) as cm return c,s,e,apoc.math.round(cm,1) as cm";
//String Q = "\"";
////            String q = "call apoc.export.csv.query(" + Q +  cq + Q + ", 'chr_cm.csv' , {delim:'|', stream:true, quotes: false, format: 'plain'})"; 
// //           neo4j_qry(q);
//            
//String q = neo4j_qry.qry_to_pipe_delimited(cq,"chr_cm.csv");
//
//String lc = "LOAD CSV WITH HEADERS FROM 'file:///chr_cm.csv' as line FIELDTERMINATOR '|' return line ";
//cq = "create (cn:chr_cm{chr:toString(line.c),str_pos:toInteger(line.s),end_pos:toInteger(line.e),cm:toFloat(line.cm)})";
//neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);
//    }catch(Exception e){}
//        
// 
//return "completed";
//}
//}
