/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;
import gen.neo4jlib.neo4j_qry;
//    import org.neo4j.driver.AuthTokens;
//    import org.neo4j.driver.net.ServerAddress;
    import gen.conn.connTest;
   
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;
    import org.neo4j.procedure.Description;
  
            
      
    public class mrca1_str {          
        @UserFunction
        @Description("Input 2 RNs and get list of MRCA names")
        
    public String mrca_str(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
    {
         { 
        gen.neo4jlib.neo4j_info.neo4j_var();
        String s = mrca_qry(rn1,rn2);
        return s;
              
             }
     }
   
    public String mrca_qry(Long rn1, Long rn2) 
    {
        Long rnmin;
        Long rnmax;
        if (rn1<rn2){
            rnmin=rn1;
            rnmax=rn2;
        }
        else {
            rnmin=rn2;
            rnmax=rn1;
    }
                    
       
        String cq = "match (p1:Person{RN:" + rnmin + "})-[r1:father|mother*0..15]->(mrca:Person)<-[r2:father|mother*0..15]-(p2:Person{RN:" + rnmax + "}) where p1.RN<p2.RN with mrca order by mrca.sex desc with mrca.fullname + ' ⦋' + mrca.RN + '⦌ (' + left(mrca.BD,4) +'-' + left(mrca.DD,4) +')' as mrca_indv return collect(mrca_indv) as mrca" ;    
       // String r =mrca_qry(cq).replace("\"", "");  //.replace("[[", "").replace("]]", ",");
        //return r;

        return neo4j_qry.qry_str(cq).replace("\"", "");  
    }

}