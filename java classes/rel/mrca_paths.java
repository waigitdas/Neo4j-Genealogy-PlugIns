/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.rel;


import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
import gen.neo4jlib.neo4j_qry;

public class mrca_paths {
       @UserFunction
        @Description("Load a GEDCOM into Neo4j creating Person, Union and Place nodes and the edges connecting them.")
                    
    
    public List<Object> mrca_with_path_facts (
        @Name("rn1") 
             Long rn1,
        @Name("rn2") 
            Long rn2,
        @Name("gen") 
            Long gen
       )
      {
       List lst = getMrcaPathFacts(rn1,rn2, gen);
       
       return lst;
      }
  
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
         getMrcaPathFacts(Long.valueOf(1),Long.valueOf(33914), Long.valueOf(15));
    }
    
    public static List<Object> getMrcaPathFacts(Long rn1,Long rn2, Long gen) {
        String cq = "match path=(p1:Person)-[rp1:father|mother*1.." + gen + "]->(mrca:Person)<-[rp2:father|mother*1.." + gen +"]-(p2:Person) where p1.RN=" + rn1 +" and p2.RN=" + rn2 + " with path, gen.rel.ahn_path(reduce(srt2 ='', q IN nodes(path)|srt2 + case when q.sex='M' then 0 else 1 end )) AS ahn,gen.rel.relationship_from_path(size(rp1),size(rp2),2) as czn,reduce(srt2 ='', q IN nodes(path)|srt2 + case when q.RN=mrca.RN then '**' + q.fullname + ' [' + q.RN + '] (' + left(q.BD,4) + '-' + left(q.DD,4) + ')  ** > ' else q.fullname + ' [' + q.RN + '] (' + left(q.BD,4) + '-' + left(q.DD,4) + ') >' end ) as anc, reduce(srt2 ='', q IN nodes(path)|srt2 + case when q.RN=mrca.RN then '** \\u2699'  + q.BP +  ' \\u2670' + q.DP + ')  **  ' else '\\u2699' + q.BP +  ' \\u2670' + q.DP + ' >' end + '||') as places,mrca.fullname as mrca with collect(mrca + ahn + czn + anc + places) as ListOfLists RETURN  ListOfLists";
       //System.out.println(cq); 
       return neo4j_qry.qry_obj_list(cq);
       
    }
    
}
