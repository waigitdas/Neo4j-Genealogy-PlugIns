/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class add_rel_property {
    @UserFunction
    @Description("Enhancement to the graph by adding the relationship to match_by_segment edge where both the source and target are known matches. This one-time process enables displaying the relation in queries without re-computing it.")

    public String add_relationship_property(
  )
   
         { 
             
        String c =add_rel();
         return c;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String add_rel()
    {
   
        String Q = "\"";
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String cq ="MATCH (m1:DNA_Match)-[r:match_segment]->(s:Segment) where r.p_rn>0 and r.m_rn>0 with distinct r.p_rn as rn1,r.m_rn as rn2 with distinct rn1,rn2,gen.rel.relationship_from_RNs(rn1,rn2) as rel with rn1,rn2,rel where rel>' ' return rn1,rn2,rel";
                //"MATCH (m1:DNA_Match)-[r:match_segment]->(s:Segment) where r.p_rn>0 and r.m_rn>0 with distinct r.p_rn as rn1,r.m_rn as rn2 with rn1,rn2,gen.rel.relationship_from_RNs(rn1,rn2) as rel1 with rn1,rn2,rel1 where trim(rel1)>' ' with rn1,rn2,collect(rel1) as rel2 return  rn1, rn2,apoc.text.join(rel2, ' ') as rel order by rn1,rn2"; 
       String qry =  "call apoc.export.csv.query(" + Q + cq +  Q + ",'rel_property.csv', {delim:'|', quotes: false, format: 'plain'}) ";
//        neo4j_qry.qry_write(qry);
// 

        gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited(cq, "rel_property.csv");
         
       try{
            gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("match_segment","rel" );
            gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("match_by_segment","rel" );
            gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("KitMatch","rel" );
 }
       catch (Exception e) {}
       
       
       cq="MATCH p=()-[r:match_by_segment]->() where r.rel>' '  remove r.rel";
        gen.neo4jlib.neo4j_qry.qry_write(cq);

        cq="MATCH p=()-[r:KitMatch]->() where r.rel>' '  remove r.rel";
        gen.neo4jlib.neo4j_qry.qry_write(cq);

        cq="MATCH p=()-[r:match_segment]->() where r.rel>' '  remove r.rel";
        gen.neo4jlib.neo4j_qry.qry_write(cq);

        String lc = "LOAD CSV WITH HEADERS FROM 'file:///rel_property.csv' as line FIELDTERMINATOR '|' return line ";
        cq = " match (k:Kit{RN:toInteger(line.rn1)})-[r:KitMatch]-(m:DNA_Match{RN:toInteger(line.rn2)}) set r.rel=toString(line.rel)";
        neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);

        cq = " match (m1:DNA_Match{RN:toInteger(line.rn1)})-[r:match_by_segment]-(m2:DNA_Match{RN:toInteger(line.rn2)}) set r.rel=toString(line.rel)";
        neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);

        cq = " match (m:DNA_Match)-[r:match_segment{p_rn:toInteger(line.rn1),m_rn:toInteger(line.rn2)}]-(s:Segment) set r.rel=toString(line.rel)";
        neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);

        
//        neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///rel_property.csv' AS line FIELDTERMINATOR '|' match (k:Kit{RN:toInteger(line.rn1)})-[r:KitMatch]-(m:DNA_Match{RN:toInteger(line.rn2)}) set r.rel=toString(line.rel)");
//        neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///rel_property.csv' AS line FIELDTERMINATOR '|' match (m1:DNA_Match{RN:toInteger(line.rn1)})-[r:match_by_segment]-(m2:DNA_Match{RN:toInteger(line.rn2)}) set r.rel=toString(line.rel)");
//        neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///rel_property.csv' AS line FIELDTERMINATOR '|' match (m:DNA_Match)-[r:match_segment{p_rn:toInteger(line.rn1),m_rn:toInteger(line.rn2)}]-(s:Segment) set r.rel=toString(line.rel)");
// 
        return "completed";
    }
}
