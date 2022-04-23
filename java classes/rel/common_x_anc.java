/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import java.lang.reflect.Array;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class common_x_anc {
    @UserFunction
    @Description("Finds possible x-chromosome common ancestor(s).")

    public String common_x_ancestor(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
   
         { 
             
        String s = get_list(rn1,rn2);
         return s;
            }

      
    public static void main(String args[]) {
        //get_list(1L,26429L);
    }
    
     public String get_list(Long rn1,Long rn2)
    {
        //Find all X-linked ancestors using a cypher query
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String cq ="with [[" + rn1 + ","  + rn2 + "]] as x unwind x as rn call { with rn MATCH (n:Person{RN:rn}) match p=(n)-[[:father|mother*0..99]]->(x) with x.RN as Name, length(p) as gen,[[rn in nodes(p)|rn.RN]] AS op, reduce(srt2 ='', q IN nodes(p)| srt2 + q.sex) AS sortOrder, '1' + reduce(srt ='', q IN nodes(p)|srt + case when q.sex='M' then '0' else '1' end ) AS Anh with Name,gen,sortOrder,'1' + right(Anh,size(Anh)-2) as Ahnen,op where sortOrder=replace(sortOrder,'MM','') return collect(Name) as cn} with apoc.coll.sort(apoc.coll.flatten(collect(cn))) as c1, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect(cn)))) as c2 with apoc.coll.intersection(c1,c2) as c3 unwind c3 as person with person match ppath1=(pp1:Person{RN:" + rn1 + "})-[[rr2:father|mother*0..20]]->(pp2:Person) where pp2.RN=person with pp2,person,reduce(srt2 ='', q IN nodes(ppath1)| srt2 + q.sex) as sx1 match ppath2=(pp3:Person{RN:" + rn2 + "})-[[rr2:father|mother*0..20]]->(pp4:Person) where pp4.RN=person with pp2,pp3,sx1,reduce(srt3 ='', u IN nodes(ppath2)| srt3 + u.sex) as sx2 with  pp2.fullname as possible_common_x_ancestor,size(sx1) + size(sx2)-2 as x_gen_dist, size(sx1) as gen1,size(sx2) as gen2,sx1 as path1,sx2 as path2 where sx1=replace(sx1,'MM','')  and sx2=replace(sx2,'MM','')  return  possible_common_x_ancestor,x_gen_dist, gen1,gen2,path1,path2 order by x_gen_dist";
       
        //String cqq = cq.replace("[[","^").replace("]]","@");
        
       //export resuls to csv file and then read back in for processing.
       //the sex-paths excluding MM to each ancestor could not be filtered in a cypher query
       //therefore, iterate thru the list, filtering out those whose pattern is present in a shorter concatenated string
       //this leaves only the most recent common ancestor(s) from whom the x-chromosome was inherited by two descendants.
       gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited(cq.replace("[[","[").replace("]]","]"),"poss_x_matches.csv");
       String[] c = gen.neo4jlib.file_lib.readFileByLine(gen.neo4jlib.neo4j_info.Import_Dir + "poss_x_matches.csv").replace("|",",").split("\n");
        
        String track[] = new String[c.length]; 
        for (int i = 1; i<c.length;i++){
            String[] cc1 = c[i].split(",");
        for (int j = i+1; j< c.length  ;j++){
            String[] cc2 = c[j].split(",");
            if (cc1[4].length() < cc2[4].length() || cc1[5].length() < cc2[5].length()) {
            if (i!=j && cc2[4].substring(0,cc1[4].length()).equals(cc1[4]) && cc2[5].substring(0,cc1[5].length()).equals(cc1[5])) 
            {
                track[j]="remove";
            } 
            }
        } 
        }
    
        
        String sr ="";
        for (int k=0; k<c.length; k++){
            if (track[k]!="remove"){
                sr = sr + c[k] + "\n";
            }
        }
        
        //export the results to a csv and then read it into Excel with explanatory notes.
        String fn = gen.neo4jlib.neo4j_info.Import_Dir + "x_mrca.csv";
        gen.neo4jlib.file_lib.writeFile(sr, fn);
        
        gen.excelLib.excel_from_csv.load_csv(fn, "poss_x_mrca", "poss_mrca", 1, "", "2:####;3:####;4:####", "", true, "UDF:\nreturn gen.rel.common_x_ancestor(" + rn1 + ","  + rn2 + ")\n\nCypher query not shown because post processing is required to produce a filtered view of just the most recent common ancestor(s).\nBoth sex-paths to the common ancestor, which are shown, must not have MM.\n\nThis is a list of POSSIBLE sources of the X-chromosome. Not all of these people will share the same X-chromosome.\nThe X-chromosome is not inherited male to male. The sex path cannot include MM and is shown to demonstrate this.", false);
        return sr;
        //Cypher query:\n"  + cqq.replace("^","[").replace("@","]") + "\n\n
    }
}
