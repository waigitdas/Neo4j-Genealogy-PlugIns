/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.genlib;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class deidentify {
    @UserFunction
    @Description("de-identified data, converting names to initials, dates to year only and removing some properties entirely")

    public String deidentify_data(

  )
   
         { 
             
        String s = deid();
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String deid() 
    {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
 
        //Kits
        gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(k:Kit)-[r:Gedcom_Kit]-(m:Person) set k.fullname=left(m.first_name,1) + ' ' + left(m.surname,1)");
        gen.neo4jlib.neo4j_qry.qry_write("match (p:Kit) remove p.kit_desc, p.surname");
  
        //DNA_Matches
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (m1:DNA_Match)-[r1:match_segment]->(s:Segment)<-[r2:match_segment]-(m2:DNA_Match)  set r2.p=left(m2.first_name,1) + ' ' + left(m2.surname,1),r1.p=left(m1.first_name,1) + ' ' + left(m1.surname,1), r2.m=left(m1.first_name,1) + ' ' + left(m1.surname,1),r1.m=left(m2.first_name,1) + ' ' + left(m2.surname,1)");
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (m:DNA_Match)  set m.fullname=left(m.first_name,1) + ' ' + left(m.surname,1)");
         gen.neo4jlib.neo4j_qry.qry_write("match (p:DNA_Match) remove p.first_name, p.middle_name, p.surname");
        //gen.neo4jlib.neo4j_qry.qry_write("MATCH p=(m:DNA_Match)-[r:match_segment]->() set r.p=m.fullname");
 
        //Person nodes
        gen.neo4jlib.neo4j_qry.qry_write("match (p:Person) set p.fullname=left(p.first_name,1) + ' ' + left(p.surname,1)");
        gen.neo4jlib.neo4j_qry.qry_write("match (p:Person) remove p.first_name, p.middle_name,p.surname,p.BDGed,p.DDGed");
        gen.neo4jlib.neo4j_qry.qry_write("match (p:Person) set p.BD=left(p.BD,4), p.DD=left(p.DD,4),p.UD=left(p.UD,4)");

        //Y_DNA+Matches
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (m:DNA_YMatch)  set m.fullname=left(m.first_name,1) + ' ' + left(m.surname,1)");
        gen.neo4jlib.neo4j_qry.qry_write("match (p:DNA_YMatch) remove p.first_name, p.middle_name,p.surname");
            
        return "deidentification completed";
    }
}
