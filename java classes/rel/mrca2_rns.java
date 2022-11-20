/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;
    import org.neo4j.procedure.Description;
  
            
      
    public class mrca2_rns {          
        @UserFunction
        @Description("Input 2 RNs and get list of mrca RNs")
        
    public String mrca_rn(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
    {
         { 
       String s = mrca_qry_rn(rn1,rn2);
       return s;
            }
     }
   
//       public static void main(String args[]) {
//        String lo = mrca_qry_rn(1L,600L);
//        int fgg = 0;
//    }
//       
       
    public String mrca_qry_rn(Long rn1, Long rn2) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
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
           String cq = "match (p1:Person{RN:" + rnmin + "})-[r1:father|mother*0..20]->(mrca:Person)<-[r2:father|mother*0..20]-(p2:Person{RN:" + rnmax + "}) where p1.RN<p2.RN with mrca order by mrca.sex desc return collect(mrca.RN) as rn" ;    
                
        String s =gen.neo4jlib.neo4j_qry.qry_str(cq).replace("[","").replace("]","").replace(";",";");
        return s;
    }

}