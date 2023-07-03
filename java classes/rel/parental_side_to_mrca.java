/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class parental_side_to_mrca {
    @UserFunction
    @Description("computer parental side for both rn in path to their mrca.")

    public String parental_path_to_mrca(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
   
         { 
             
        String s = get_sides(rn1,rn2);
         return s;
            }

    
    
    public static void main(String args[]) {
       String s = null;
        s= get_sides(1L,341L);
        System.out.println(s);
        s= get_sides(216L,209L);
        System.out.println(s);
        s = get_sides(1L,600L);
        System.out.println(s);
       s = get_sides(1L,26446L);
        System.out.println(s);
       s = get_sides(1L,26429L);
        System.out.println(s);
    }
    
     public static String get_sides(Long rn1,Long rn2) 
    {   
        //sort and r1<r2 in query speeds up query time
        Long rnmin;
        Long rnmax;
        Long sortorder;  //used to restore output to order of submitted rns
        if (rn1<rn2){
            rnmin=rn1;
            rnmax=rn2;
            sortorder=1L;
                    ;
        }
        else {
            rnmin=rn2;
            rnmax=rn1;
            sortorder = 2L;
    }
        
        String cq = "match path = (p:Person{RN:" + rnmin + "})-[r1:father|mother*0..15]->(mrca:Person)<-[r2:father|mother*0..15]-(b:Person{RN:" + rnmax + "}) where p.RN<b.RN with reduce(rl='', x in r1|rl + left(toUpper(type(x)),1)) as pa1,reduce(rl='', x in r2| left(toUpper(type(x)),1) + rl) as pa2 with left(pa1,1) as pa1,left(pa2,1) as pa2 with [case when pa1='M' then 'maternal' else case when pa1='F' then 'paternal' else case when pa1> ' ' then pa1 else 'unknown' end end end] as palist1 , [case when pa2='M' then 'maternal' else case when pa2='F' then 'paternal' else case when pa2> ' ' then pa2 else 'unknown' end end end] as palist2 return distinct apoc.coll.flatten([palist1,palist2])"; 
        //String cq = "match path = (p:Person{RN:" + rnmin + "})-[r1:father|mother*0..15]->(mrca:Person)<-[r2:father|mother*0..15]-(b:Person{RN:" + rnmax + "}) where p.RN<b.RN with reduce(rl=\"\", x in r1|rl + left(toUpper(type(x)),1)) as pa1,reduce(rl=\"\", x in r2| left(toUpper(type(x)),1) + rl) as pa2 with left(pa1,1) as pa1,left(pa2,1) as pa2 with [pa1,pa2] as palist return distinct palist";
       try{
        String[] sides = gen.neo4jlib.neo4j_qry.qry_str(cq).replace("[[", "").replace("]]", "").replace("\"", "").split(",");

     String rtn="";
     //restore ordering to order submitted
    if (sortorder == 1L)
        {
            if (sides.length>2) //for aunt/uncle rtn has extra elements
            {
                rtn = sides[0].strip() + ',' + sides[2].strip();
            }
            else
            {
                rtn = sides[0].strip() + ',' + sides[1].strip();
            }
        }
    else {
        if (sides.length>2) 
        {
           rtn = sides[2].strip() + "," + sides[0].strip();
        }
        else
        {
            rtn = sides[1].strip() + "," + sides[0].strip();
        }
    }
  int hgf =0;
  //System.out.println(rtn);
  return rtn;
       }
       catch(Exception e)
       {
           return "~,~";
       }
    }
}
