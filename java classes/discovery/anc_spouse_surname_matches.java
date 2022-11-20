/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.discovery;

import gen.neo4jlib.neo4j_qry;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

public class anc_spouse_surname_matches {
    @UserFunction
    @Description("finds all the surnames of direct ancestor's spouses and then matches with those surnames")

    public String ancestor_spouse_surname_matches(
        @Name("propositus_rn") 
            Long propositus_rn
  )
   
         { 
             
        String s = get_matches(propositus_rn);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_matches(Long propositus_rn) 
    {
        String cq = "match (p:Person{RN:" + propositus_rn + "})-[:father|mother*0..99]->(a:Person{sex:'F'}) where not a.surname in ['MRCA','?'] match path=(p:Person{RN:" + propositus_rn + "})-[:father|mother*0..99]->(a:Person{sex:'F'}) where not a.surname in ['MRCA','?'] with a  order by a.surname with collect(distinct a.surname) as surnames unwind surnames as s  return  s ";
        
        try{
        String[] ls = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n");
        String s ="";
        for (int i=0;i<ls.length; i++){
            s = s + ls[i];
            if (i<ls.length-1){s = s + ",";}
            else {s = s + "\n";}
        }
        s = s.replace("\"", "");
        gen.discovery.surname_variants sn = new gen.discovery.surname_variants();
        
        cq = "return gen.discovery.matches_by_surname('" + s + "')";
        sn.matches_by_surname(s);
//gen.neo4jlib.neo4j_qry.qry_write(cq);
//gen.excelLib.queries_to_excel.qry_to_excel(cq, "ancestor_spouse_surname_matches", "matches", 1, "", "", "", true, "the surnames of matches are the same as the maiden names of the spouses of the direct ancestors of " + gen.gedcom.get_family_tree_data.getPersonFromRN(propositus_rn, true) + "\nThese surnames are in the UDF call shown", true);
        return cq;
        }
        catch (Exception e) {
            return cq;
        }    
            }
}
