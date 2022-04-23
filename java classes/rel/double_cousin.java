/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class double_cousin {
    @UserFunction
    @Description("Finds double cousin using the family tree. Added rel property value of 'DC' to match_by_segment relationship.")

    public String double_cousin_reports(
  )
   
         { 
             
        String s = get_double_cousins();
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_double_cousins()
    {
        //old query ...String cq = "Match (p:Person), (q:Person) match path=(p:Person)-[[:father|mother*..2]]->(CA)<-[[:father|mother*..2]]-(q:Person) with p, q,CA where p.fullname=replace(p.fullname,'MRCA','') and q.fullname=replace(q.fullname,'MRCA','') with p.fullname + ' [' + p.RN + ']' as Name1, q.fullname + ' [' + q.RN + ']' as Name2, CA.fullname + ' [' + CA.RN + ']' as MRCAs with Name1,Name2,MRCAs order by MRCAs with Name1,Name2,collect(MRCAs) as mrcas, count(*) as MRCA_Ct where MRCA_Ct>3 return distinct Name1,Name2,MRCA_Ct,mrcas order by Name1";
        
        String cq="Match (p:Person), (q:Person) match path=(p:Person)-[[:father|mother*..2]]->(CA)<-[[:father|mother*..2]]-(q:Person) with p, q,CA where p.fullname=replace(p.fullname,'MRCA','') and q.fullname=replace(q.fullname,'MRCA','') with p.fullname + ' ⦋' + p.RN + '⦌ (' + left(p.BD,4) + '-' + left(p.DD,4) + ')' as Name1, q.fullname + ' ⦋' + q.RN + '⦌ (' + left(q.BD,4) + '-' + left(q.DD,4) + ')' as Name2, CA.fullname + ' ⦋' + CA.RN + '⦌ (' + left(CA.BD,4) + '-' + left(CA.DD,4) + ')' as MRCAs with Name1,Name2,MRCAs order by MRCAs with Name1,Name2,collect(MRCAs) as mrcas, count(*) as MRCA_Ct where MRCA_Ct>3 with apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect(Name1) + collect(Name2)))) as double_cousins, mrcas return distinct mrcas as grandparents,size(double_cousins) as ct,double_cousins order by grandparents";
        String ef = gen.excelLib.queries_to_excel.qry_to_excel(cq, "double cousins", "double_cousins", 1, "", "1:####", "", true, "UDF\nreturn gen.rel.double_cousin_reports()\n\ncypher query\n"+ cq + "\n\nDouble cousins share two sets of grandparents.", false);
        
        cq = "Match (p:Person), (q:Person) match path=(p:Person)-[:father|mother*..2]->(CA)<-[:father|mother*..2]-(q:Person) with p.RN as RN1,p.fullname as Name1,q.RN as RN2,q.fullname as Name2, count(*) as MRCA_Ct where MRCA_Ct>3 and p.fullname=replace(p.fullname,'MRCA','') and q.fullname=replace(q.fullname,'MRCA','') return distinct RN1,RN2";
        String fn ="double_cousins.csv";
        gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited(cq, fn);
        
      cq = "LOAD CSV WITH HEADERS FROM 'file:///" + fn + "' as line FIELDTERMINATOR '|'  match (m1:DNA_Match{RN:toInteger(line.RN1)})-[r:match_by_segment]-(m2:DNA_Match{RN:toInteger(line.RN2)}) set r.rel='DC'" ;
        neo4j_qry.qry_write((cq));
  
        return "completed";
    }
}
