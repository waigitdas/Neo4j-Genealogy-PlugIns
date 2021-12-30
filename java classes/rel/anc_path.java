/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import java.util.List;

public class anc_path {
   Long rn1;
   Long rn2;
   Long anc_ct;                  //number of MRCA
   List<String> anc_name;       //list of mrca names
   List<Long> anc_rn;           //list of mrca RNs
   List<Long> path1;            //path from rn1 to mrca
   List<Long> path2;            //path from rn1 to mrca
   List<String> rel;            //looked up relationship(s)

//constructor
public anc_path(Long rn1, Long rn2){
    this.rn1 = rn1;
    this.rn2 = rn2;
    List<Object> c = gen.neo4jlib.neo4j_qry.qry_obj_list("match path=(p1:Person{RN:1})-[r1:father|mother*0..15]->(mrca:Person)<-[r2:father|mother*0..15]-(p2:Person{RN:600})  with mrca order by mrca.sex desc  with mrca.fullname + ' ⦋' + mrca.RN + '⦌ (' + left(mrca.BD,4) +'-' + left(mrca.DD,4) +')' as mrca_indv, mrca.RN as rn  with  collect(mrca_indv) as mrca_str, collect(rn) as mrca_rns return size(mrca_rns) as ct,mrca_str,mrca_rns");
    this.anc_ct = (Long) c.get(0);
    this.anc_name = (List<String>) c.get(1);
    
}        

}