/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.avatar;

import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
import gen.tgs.create_segment_sequence_edges;
import gen.rel.mrca_set_link_property;
import gen.tgs.create_segment_sequence_edges;

public class targeted_anc_enhance {
    @UserFunction
    @Description("Creates ancestor_rn property in Person, DNA_match and Kit nodes and then the sequence of segments mapping to descendants of the ancestor. This facilitates queries in triangulation group reporting. The seq_seq edge enables rapid traversals to the segments shared by multiple persons without directly computing overlaps.")

    public  String add_enhancements(
        @Name("ancestor_rn") 
            Long ancestor_rn
  )
         { 
             
         String s = setup(ancestor_rn);
         return s;
            }
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String setup(Long ancestor_rn) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();  //initialize variables
        
           
        try{
        //add ancestor_rn property to Person, Kit and DNA_Match nodes after erasing prior properties
        mrca_set_link_property s = new mrca_set_link_property();
        s.mrca_link_property(ancestor_rn );
        } 
        catch (Exception e1) {
            
}
        
        try{
        //create seqment sequences for all segments linked to descendants of the specified ancestor after removing previously created edges
        create_segment_sequence_edges e = new create_segment_sequence_edges();
        e.create_seg_seq_edges(ancestor_rn);
        }catch (Exception e2){
                      
        }
      
//        try{
//        //create seqment sequences for all segments linked to descendants of the specified ancestor after removing previously created edges
//        gen.dna.ancestor_seg_property asp = new gen.dna.ancestor_seg_property();
//        asp.overlap_segments();
//        }catch (Exception e2){
//           
//        }
      
      //mss creation is not targetted to a specific ancestor
       try{
           
        gen.mss.create_mss_entities mss = new gen.mss.create_mss_entities();
        mss.create_mss();
           
              
       }
       catch (Exception ex4){
            
                }

       
             //inferred HGs
       gen.neo4jlib.neo4j_qry.qry_write("MATCH (d:DNA_Match) where d.RN>0 and d.YHG is not null match path=(p:Person{RN:d.RN})-[ra:father*0..20]->(a:Person) with d,p,path unwind nodes(path) as rns with distinct p,d, rns where rns.RN<>d.RN set rns.iYHG= d.YHG ");
               //"match path1=(p:Person)-[r:father|mother*0..10]->(a:Person) with a where a.sex='M' and a.surname<>'MRCA' with collect(a.RN) as rns match path2 =(p2:Person)-[r:father*0..10]->(a2:Person) where a2.RN in rns and a2.sex='M' with rns,p2,collect(p2.RN) as rns2 ,[x in nodes(path2)|x.RN] as path_rns match (d:DNA_Match) where d.RN in rns2 and d.YHG is not null with rns,p2.surname as surname , d.YHG as Y_haplogroup,apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect( distinct path_rns)))) as path_rns with surname,Y_haplogroup,apoc.coll.intersection(path_rns,rns) as pedigree_rns unwind pedigree_rns as x call { with x return x as prn } match path4=(p4:Person{RN:prn})-[r4:father*0..15]->(a4:Person) with p4,surname, gen.dna.deepest_level(apoc.text.join(collect(distinct Y_haplogroup),',')) as yhg, prn,last(collect(a4.RN)) as anc_rn set p4.iYHG=yhg");
       
       gen.neo4jlib.neo4j_qry.qry_write("match (p:Person{sex:'M'})-[r:father*1..99]->(d:Person{sex:'M'}) where d.iYHG is not null with distinct p, d.iYHG as yhg set p.iYHG=yhg");
       
       gen.neo4jlib.neo4j_qry.qry_write("MATCH (d:DNA_Match) where d.RN>0 and d.mtHG is not null match path=(p:Person{RN:d.RN})-[ra:mother*0..20]->(a:Person) with d,p,path unwind nodes(path) as rns with distinct p,d, rns where rns.RN<>d.RN set rns.imtHG= d.mtHG");
               //"match path1=(p:Person)-[r:father|mother*0..10]->(a:Person) with a where a.sex='F' and a.surname<>'MRCA' with collect(a.RN) as rns match path2 =(p2:Person)-[r:mother*0..10]->(a2:Person) where a2.RN in rns with rns,p2,collect(p2.RN) as rns2 ,[x in nodes(path2)|x.RN] as path_rns match (d:DNA_Match) where d.RN in rns2 and d.mtHG is not null with rns, d.mtHG as mt_haplogroup, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect( distinct path_rns)))) as path_rns with mt_haplogroup,apoc.coll.intersection(path_rns,rns) as pedigree_rns unwind pedigree_rns as x call { with x return x as prn } match path4=(p4:Person{RN:prn})-[r4:mother*0..15]->(a4:Person) with distinct p4,gen.dna.mt_deepest_level(apoc.text.join(collect(distinct mt_haplogroup),',')) as mthg, prn set p4.imtHG=mthg");
       
//       gen.neo4jlib.neo4j_qry.qry_write("match (p:Person{sex:'F'})-[r:mother*1..99]->(d:Person{sex:'F'}) where d.imtHG is not null with distinct p, d.imtHG as hg set p.imtHG=hg");
       
      
       
        return "completed";
    }
}
