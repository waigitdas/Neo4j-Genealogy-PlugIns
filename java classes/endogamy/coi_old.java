/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.endogamy;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class coi_old {
    @UserFunction
    @Description("Template used in creating new functions.")

    public Double coeffiecient_of_inbreeding(
        @Name("rn") 
            Long rn
  )
   
         { 
             
        Double coi = get_coi(rn);
         return coi;
            }

    
    
    public static void main(String args[]) {
        get_coi(1L);
    }
    
     public static Double get_coi(Long rn) 
    {
       //http://www.genetic-genealogy.co.uk/genetics/Toc115570144.html
        //comput coi_old for propositus using rn
        //if parents are unrelated, then coi_old will be 0.0; in this case one parent may have coi_old>0, so this must be evaluated
        String cq="match (p0:Person{RN:" + rn + "})-[r1:father|mother]->(a1:Person) with collect(a1.RN) as parentRNs match path1= (p1:Person{RN:parentRNs[0]})-[r1:father|mother*0..25]->(mrca1:Person) match path2=(mrca2)<-[r2:father|mother*0..25]-(p2:Person{RN:parentRNs[1]}) where mrca1=mrca2 with parentRNs[0] as rn1, parentRNs[1] as rn2,collect(distinct mrca1.RN) as rns unwind rns as m call { with m,rn1,rn2 match path3= (p3:Person{RN:rn1})-[r1:father|mother*0..25]->(mrca3:Person{RN:m}) match path4=(mrca4{RN:m})<-[r2:father|mother*0..25]-(p4:Person{RN:rn2}) where mrca3=mrca4 return m as rn,((0.5^length(path3)) + (0.5^length(path4))) as fn } return sum(fn) as coi";
        
        Double coi = Double.parseDouble(gen.neo4jlib.neo4j_qry.qry_str(cq).replace("[","").replace("]",""));
        if(coi.equals(0.0)){ //must compute and find parent with coi_old>0
            Long mother = Long.parseLong(gen.neo4jlib.neo4j_qry.qry_str("match(p:Person{RN:"+ rn + "})-[r:mother]->(a:Person) return a.RN as rn").replace("[","").replace("]",""));
            Long father = Long.parseLong(gen.neo4jlib.neo4j_qry.qry_str("match(p:Person{RN:"+ rn + "})-[r:father]->(a:Person) return a.RN as rn").replace("[","").replace("]",""));

            Long parent = 0L;
            
            Double coi_mother = Double.parseDouble(gen.neo4jlib.neo4j_qry.qry_str("match (p0:Person{RN:" + mother + "})-[r1:father|mother]->(a1:Person) with collect(a1.RN) as parentRNs match path1= (p1:Person{RN:parentRNs[0]})-[r1:father|mother*0..25]->(mrca1:Person) match path2=(mrca2)<-[r2:father|mother*0..25]-(p2:Person{RN:parentRNs[1]}) where mrca1=mrca2 with parentRNs[0] as rn1, parentRNs[1] as rn2,collect(distinct mrca1.RN) as rns unwind rns as m call { with m,rn1,rn2 match path3= (p3:Person{RN:rn1})-[r1:father|mother*0..25]->(mrca3:Person{RN:m}) match path4=(mrca4{RN:m})<-[r2:father|mother*0..25]-(p4:Person{RN:rn2}) where mrca3=mrca4 return m as rn,((0.5^length(path3)) + (0.5^length(path4))) as fn } return sum(fn) as coi").replace("[","").replace("]",""));;
            
            Double coi_father = Double.parseDouble(gen.neo4jlib.neo4j_qry.qry_str("match (p0:Person{RN:" + father + "})-[r1:father|mother]->(a1:Person) with collect(a1.RN) as parentRNs match path1= (p1:Person{RN:parentRNs[0]})-[r1:father|mother*0..25]->(mrca1:Person) match path2=(mrca2)<-[r2:father|mother*0..25]-(p2:Person{RN:parentRNs[1]}) where mrca1=mrca2 with parentRNs[0] as rn1, parentRNs[1] as rn2,collect(distinct mrca1.RN) as rns unwind rns as m call { with m,rn1,rn2 match path3= (p3:Person{RN:rn1})-[r1:father|mother*0..25]->(mrca3:Person{RN:m}) match path4=(mrca4{RN:m})<-[r2:father|mother*0..25]-(p4:Person{RN:rn2}) where mrca3=mrca4 return m as rn,((0.5^length(path3)) + (0.5^length(path4))) as fn } return sum(fn) as coi").replace("[","").replace("]",""));;
            
            //parent coi_old of the parent with coi_old>0
            Double coi_parent = Math.max(coi_mother,coi_father);
            
            if(coi_mother>coi_father){
                parent = mother;
            }
            else{parent=father;}
            
            if (coi_parent.equals(0.0)){return 0.0;}
            
            cq = "match (p0:Person{RN:" + parent +"})-[r1:father|mother]->(a1:Person) with collect(a1.RN) as parentRNs match path1= (p1:Person{RN:parentRNs[0]})-[r1:father|mother*0..25]->(mrca1:Person) match path2=(mrca2)<-[r2:father|mother*0..25]-(p2:Person{RN:parentRNs[1]}) where mrca1=mrca2 with parentRNs[0] as rn1, parentRNs[1] as rn2,collect(distinct mrca1.RN) as rns unwind rns as m call { with m,rn1,rn2 match path3= (p3:Person{RN:rn1})-[r1:father|mother*0..25]->(mrca3:Person{RN:m}) match path4=(mrca4{RN:m})<-[r2:father|mother*0..25]-(p4:Person{RN:rn2}) where mrca3=mrca4 return m as rn,((0.5^length(path3)*(1 + " + coi_parent + ")) + (0.5^length(path4)*(1 + " + coi_parent + "))) as fn } return sum(fn) as coi";
            Double coi2 = Double.parseDouble(gen.neo4jlib.neo4j_qry.qry_str(cq).replace("[","").replace("]",""));
            System.out.println(coi2);
            return coi2;
        }
        else {  //coi > 0; report result directly
            System.out.println(coi);
            return coi;
        }
    }
}
