/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.stats;

import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
import gen.neo4jlib.neo4j_qry;
import gen.neo4jlib.neo4j_info;
import gen.excelLib.write_open_csv;
/**
 *
 * @author david
 */
public class project_persons {
       @UserFunction
        @Description("Persons in the genealogy at Neo4j. Use in other queries to speed processing")
        
    public String project_persons(
//        @Name("rn1") 
//            Long rn1,
//        @Name("rn2") 
//            Long rn2
  )
    {
         { 
        gen.neo4jlib.neo4j_info.neo4j_var();
        //String cq = "match (p1:Person{RN:" + rn1 + "})-[r1:father|mother*0..15]->(mrca:Person)<-[r2:father|mother*0..15]-(p2:Person{RN:" + rn2 + "})  with mrca order by mrca.sex desc return mrca.RN" ;    
        //String r ="test";
        String r = people(); 
        return r;
            }
     }
    
    
        public static String people() {
        gen.neo4jlib.neo4j_info.neo4j_var();
        String cq = "match (p:Person) where p.surname>'A' return p.fullname as Name, p.RN as RN, p.BDGed as BD,p.DDGed as DD, p.kit as Kit order by p.surname, p.first_name, p.middle_name";
       
        gen.excelLib.queries_to_excel.qry_to_excel(cq,"people","People at Neo4j", 1, "", "", "", true);
        return "completed";
    }
  
    public static void main(String args[]) {
        people();
    }
}
