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
        add_rel();
    }
    
     public static String add_rel()
    {
   
        String Q = "\"";
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String cq = "MATCH (m1:DNA_Match)-[r:match_segment]->(s:Segment) where r.p_rn>0 and r.m_rn>0 with distinct r.p_rn as rn1,r.m_rn as rn2 with distinct rn1,rn2,gen.rel.relationship_from_RNs(rn1,rn2) as rel,gen.rel.gen_distance_from_RNs(rn1,rn2) as gd,gen.rel.compute_cor(rn1,rn2) as cor,replace(replace(gen.rel.mrca_str(rn1,rn2),'[',''),']','') as pm with rn1,rn2,cor where cor>0 with rn1,rn2,cor,gen.rel.relationship_from_RNs(rn1,rn2) as rel,gen.rel.mrca_rn(rn1,rn2) as mrca_rn,gen.rel.gen_distance_from_RNs(rn1,rn2) as gd,replace(replace(gen.rel.mrca_str(rn1,rn2),'[',''),']','') as pm,split(gen.rel.parental_path_to_mrca(rn1,rn2),',') as side return rn1,rn2,trim(side[0]) as side1,trim(side[1]) as side2,rel,gd as gen_dist,cor,pm as pair_mrca,mrca_rn" ;

gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited(cq,"rel_property.csv" );

         
       try{
            gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("match_segment","rel" );
            gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("match_by_segment","rel" );
            gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("KitMatch","rel" );
 }
       catch (Exception e) {}
       
       
       cq="MATCH p=()-[r:match_by_segment]->() where r.rel>' '  remove r.rel";
        gen.neo4jlib.neo4j_qry.qry_write(cq);
  cq="MATCH p=()-[r:match_segment]->() where r.rel>' '  remove r.p_side";
        gen.neo4jlib.neo4j_qry.qry_write(cq);
  cq="MATCH p=()-[r:match_segment]->() where r.rel>' '  remove r.m_side";
        gen.neo4jlib.neo4j_qry.qry_write(cq);

        cq="MATCH p=()-[r:KitMatch]->() where r.rel>' '  remove r.rel";
        gen.neo4jlib.neo4j_qry.qry_write(cq);

        cq="MATCH p=()-[r:match_segment]->() where r.rel>' '  remove r.rel";
        gen.neo4jlib.neo4j_qry.qry_write(cq);

        String lc = "LOAD CSV WITH HEADERS FROM 'file:///rel_property.csv' as line FIELDTERMINATOR '|' return line ";
        cq = " match (k:Kit{RN:toInteger(line.rn1)})-[r:KitMatch]-(m:DNA_Match{RN:toInteger(line.rn2)}) set r.rel=toString(line.rel),r.gen_dist=toInteger(line.gen_dist),r.cor=toFloat(line.cor), r.pair_mrca=toString(line.pair_mrca),r.mrca_rn=toString(line.mrca_rn)";
        neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);
       cq = " match (k:Kit{RN:toInteger(line.rn2)})-[r:KitMatch]-(m:DNA_Match{RN:toInteger(line.rn1)}) set r.rel=toString(line.rel),r.gen_dist=toInteger(line.gen_dist),r.cor=toFloat(line.cor), r.pair_mrca=toString(line.pair_mrca),r.mrca_rn=toString(line.mrca_rn)";
        neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);

        
        cq = " match (m1:DNA_Match{RN:toInteger(line.rn1)})-[r:match_by_segment]-(m2:DNA_Match{RN:toInteger(line.rn2)}) set r.rel=toString(line.rel),r.gen_dist=toInteger(line.gen_dist),r.cor=toFloat(line.cor), r.pair_mrca=toString(line.pair_mrca),r.mrca_rn=toString(line.mrca_rn)";
        neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);

        cq = " match (m1:DNA_Match{RN:toInteger(line.rn2)})-[r:match_by_segment]-(m2:DNA_Match{RN:toInteger(line.rn1)}) set r.rel=toString(line.rel),r.gen_dist=toInteger(line.gen_dist),r.cor=toFloat(line.cor), r.pair_mrca=toString(line.pair_mrca),r.mrca_rn=toString(line.mrca_rn),r.mrca_rn=toString(line.mrca_rn)";
        neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);

        cq = " match (m1:DNA_Match{RN:toInteger(line.rn1)})-[r:shared_match]-(m2:DNA_Match{RN:toInteger(line.rn2)}) set r.rel=toString(line.rel),r.gen_dist=toInteger(line.gen_dist),r.cor=toFloat(line.cor), r.pair_mrca=toString(line.pair_mrca),r.mrca_rn=toString(line.mrca_rn)";
        neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);

        cq = " match (m1:DNA_Match{RN:toInteger(line.rn2)})-[r:shared_match]-(m2:DNA_Match{RN:toInteger(line.rn1)}) set r.rel=toString(line.rel),r.gen_dist=toInteger(line.gen_dist),r.cor=toFloat(line.cor), r.pair_mrca=toString(line.pair_mrca),r.mrca_rn=toString(line.mrca_rn)";
        neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);

        //set match_seg properties differently for order of p and m
        cq = " match (m:DNA_Match)-[r:match_segment{p_rn:toInteger(line.rn1),m_rn:toInteger(line.rn2)}]-(s:Segment) set r.rel=toString(line.rel),r.gen_dist=toInteger(line.gen_dist),r.cor=toFloat(line.cor), r.pair_mrca=toString(line.pair_mrca),r.mrca_rn=toString(line.mrca_rn), r.p_side=toString(line.side1), r.m_side=toString(line.side2)";
        neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);

        cq = " match (m:DNA_Match)-[r:match_segment{p_rn:toInteger(line.rn2),m_rn:toInteger(line.rn1)}]-(s:Segment) set r.rel=toString(line.rel),r.gen_dist=toInteger(line.gen_dist),r.cor=toFloat(line.cor), r.pair_mrca=toString(line.pair_mrca),r.mrca_rn=toString(line.mrca_rn), r.p_side=toString(line.side2), r.m_side=toString(line.side1)";
        neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);

        cq = "MATCH p=()-[r:match_segment]->() where r.cor>=0.5  set r.p_side='U', r.m_side='U'";
        gen.neo4jlib.neo4j_qry.qry_write(cq);

        //add union id to relationships with mrcas
        cq = "MATCH p=()-[r:match_segment]->() where r.pair_mrca is not null with r,r.mrca_rn as mrca with r,[i IN SPLIT(mrca, ',') | TOINTEGER(i)] as mrca match (u:Union) where u.U1 in mrca and u.U2 in mrca with r, collect(u.uid) as uid set r.mrca_uid = uid";
        gen.neo4jlib.neo4j_qry.qry_write(cq);
        
        //fix aunt/uncle/nibling
       gen.neo4jlib.neo4j_qry.qry_write("MATCH p=()-[r:match_segment]->() where r.rel<>replace(r.rel,'Aunt','') set r.rel = case when gen.rel.nibling(r.p_rn,r.m_rn)='true' then 'Nibling' else 'Aunt-Uncle' end");
       gen.neo4jlib.neo4j_qry.qry_write("MATCH p=()-[r:match_by_segment]->() where r.rel<>replace(r.rel,'Aunt','') set r.rel = case when gen.rel.nibling(r.p_rn,r.m_rn)='true' then 'Nibling' else 'Aunt-Uncle' end");
        
        try 
        {
            gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("match_segment", "p_side");
            gen.neo4jlib.neo4j_qry.CreateRelationshipIndex("match_segment", "m_side");
            }
        catch(Exception e){}
        return "completed";
    }
}
