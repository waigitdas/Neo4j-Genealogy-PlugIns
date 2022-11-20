/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.quality;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class are_there_duplicate_nodes {
    @UserFunction
    @Description("Template used in creating new functions.")
    
    public String duplicate_nodes(

  )
   
         { 
             
        String s = find_nodes();
         return s;
            }

    
     public String find_nodes() {
        String s = "Report on duplicate nodes --- there should be none:\n\n";
  
        
        String cq = "MATCH (s:Segment) with s, count(*) as ct  with s,ct where ct>1 return s.Indx as Indx,ct order by ct desc, Indx";
        try{
            gen.excelLib.queries_to_excel.qry_to_excel(cq, "seg_node_ct_error", "duplicate_segments", 1, "", "", "", true, cq, true);
        }
        catch (Exception e) {
            s = s + "Segment nodes: no duplicates";
        }
  
            s = s + check_node_dup("Person","RN");
            s = s + check_node_dup("Union","uid");
            s = s + check_node_dup("Segment","Indx");
            s = s + check_node_dup("SeqBoundary","Indx");
            s = s + check_node_dup("tg","Indx");
            s = s + check_node_dup("DNA_Match","fullname");
            s = s + check_node_dup("Kit","kit");
            s = s + check_node_dup("Place","desc");
            s = s + check_node_dup("Continent","Name");
            s = s + check_node_dup("pop_group","Name");
            s = s + check_node_dup("DNA_YMatch","fullname");
            s = s + check_node_dup("Lookup","fullname");
            s = s + check_node_dup("block","name");
            s = s + check_node_dup("variant","name");
        

   return s;
     }

     public String check_node_dup(String NodeName,String NodeIndx){

        String s = "";
        // cq = "MATCH (s:" + NodeName + ") with s, count(*) as ct  with s,ct where ct>1 return s." + NodeIndx + " as Name,ct order by ct desc, Name";
        String cq = "MATCH (s:" + NodeName + ") with s." + NodeIndx + " as fn, count(*) as ct  with fn,ct where ct>1 return fn as Name,ct order by ct desc, Name";
         try{
            //test to see if there will be any return. If not, triggers error. If so, it repeat query into excel
            //String ss = gen.neo4jlib.neo4j_qry.qry_to_csv(cq);
            //if (ss != "") {
            gen.excelLib.queries_to_excel.qry_to_excel(cq, NodeName + "_node_ct_error", "duplicate_" + NodeIndx, 1, "", "", "", true, cq, true);
            return  NodeName + ": duplicate " + NodeIndx + " founds; see Excel\n";
            //}
            //else {
            //    return NodeName + " nodes: \t\tno duplicates for " + NodeIndx +"\n";    
           //         }
         }
        catch (Exception e) {
            return NodeName + " nodes: no duplicates for " + NodeIndx +"\n" + cq + "\n\n";
        } 
      }
}
