/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import java.util.regex.Pattern;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class parental_paths {
    @UserFunction
    @Description("concatenated string of sexes in paths to ancestor")

    public String parental_paths_from_rns(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
   
         { 
        String d = get_sides(rn1,rn2);
        return d;
            }

    public static void main(String args[]) {
       String s = "";
       s = get_sides(1L,341L);
        System.out.println(s);
       s = get_sides(1L,26429L);
        System.out.println(s);
       s= get_sides(216L,209L);
        System.out.println(s);
        s= get_sides(209L,216L);
        System.out.println(s);
        s = get_sides(1L,600L);
        System.out.println(s);
       s = get_sides(1L,26446L);
        System.out.println(s);
     }
    
     public static String get_sides(Long rn1,Long rn2) 
    {   
        //sort and r1<r2 in query speeds up query time
        Long rnmin;
        Long rnmax;
        String rtn[] = new String[2];
        Long sortorder = 0L;  //used to restore output to order of submitted rns
        if (rn1<rn2){
            rnmin=rn1;
            rnmax=rn2;
            sortorder=1L;
                    
        }
        else {
            rnmin=rn2;
            rnmax=rn1;
            sortorder = 2L;
        }
        
        String cq = "match path = (p:Person{RN:" + rnmin + "})-[r1:father|mother*0..15]->(mrca:Person)<-[r2:father|mother*0..15]-(b:Person{RN:" + rnmax + "})  where p.RN<b.RN with reduce(rl='', x in r1|rl + case when left(toUpper(type(x)),1)='F' then 'P' else 'M' end) as pa1,reduce(rm='', x in r2|rm + case when left(toUpper(type(x)),1)='F' then 'P' else 'M' end) as pa2 with pa1,reverse(pa2) as pa2 return collect(pa1) as pa1,collect(pa2) as pa2"; 


       try{
           //create two set of paths for the two persons to each common ancestor
           //the number of sets is always 2, one for each person (rn1, rn2)
           //there is one elementin each set for each common ancestor
        String[] sides = gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited_str(cq).replace("\"","").replace("[","").replace("]","").replace("\n","").strip().split("\n")[0].split(Pattern.quote("|"));
        
 
    //restore ordering to order submitted
    if (sortorder == 1L)
        {
            if (sides.length>2) //for aunt/uncle rtn has extra elements
            {
                rtn[0] = sides[0].strip();
                rtn[1] = sides[1].strip();
            }
            else
            {
                rtn[0] = sides[0].strip();
                rtn[1] = sides[1].strip();
            }
        }
    else {
        if (sides.length>2) 
        {
           rtn[0] = sides[1].strip();
           rtn[1] = sides[0].strip();
        }
        else
        {
            rtn[0] = sides[1].strip();
            rtn[1] = sides[0].strip();
        }
    }

        //System.out.println(sides[0] + "|" + sides[1]);
       }
       catch(Exception e)
       {
           System.out.println(e.getMessage());
           return "";
       }
       return rtn[0] + "|" + rtn[1];
    }
     
     
           
}
