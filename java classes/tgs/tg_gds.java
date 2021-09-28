/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.tgs;

/**
 *
 * @author david
 */
public class tg_gds {

    
//    from stackoverflow ... still needs filter ... returning >29 DNA_Match nodes
// CALL gds.graph.create.cypher(
//  "match_seg",
//  "MATCH (d:DNA_Match) where d.ancestor_rn=33454
//   RETURN id(d) AS id
//   UNION
//   MATCH (m:DNA_Match{ancestor_rn:33454})-[r:match_segment]-> 
//   (s:Segment{chr:'01'}) where r.cm>=7 and r.snp_ct>=500
//   RETURN id(s) as id",
//   "MATCH (m:DNA_Match{ancestor_rn:33454})-[r:match_segment]->(s:Segment{chr:'01'}) where r.cm>=7 and r.snp_ct>=500
//   RETURN
//     id(m) AS source,
//     id(s) AS target,
//     r.cm AS weight",
//  {
//    readConcurrency: 4,
//    validateRelationships:FALSE
//  }
//)
    
   

    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // TODO code application logic here
    }

}