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


public class cor_using_parental_sides {
    @UserFunction
    @Description("computer parental side for both rn in path to their mrca.")

    public double cor_with_parental_paths(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
   
         { 
        double d = get_sides(rn1,rn2);
        return d;
            }

    public static void main(String args[]) {
       double s = 0.0;
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
    
     public static double get_sides(Long rn1,Long rn2) 
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
        
        String cq = "match path = (p:Person{RN:" + rnmin + "})-[r1:father|mother*0..15]->(mrca:Person)<-[r2:father|mother*0..15]-(b:Person{RN:" + rnmax + "})  where p.RN<b.RN with reduce(rl='', x in r1|rl + case when left(toUpper(type(x)),1)='F' then 'P' else 'M' end) as pa1,reduce(rm='', x in r2|rm + case when left(toUpper(type(x)),1)='F' then 'P' else 'M' end) as pa2 return collect(pa1) as pa1,collect(pa2) as pa2"; 

       //String rtn="";
        double[] cor;
        double cor_total = 0.0;

       try{
           //create two set of paths for the two persons to each common ancestor
           //the number of sets is always 2, one for each person (rn1, rn2)
           //there is one elementin each set for each common ancestor
        String[] ss = gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited_str(cq).replace("\"","").replace("[","").replace("]","").replace("\n","").strip().split("\n")[0].split(Pattern.quote("|"));
        
        String s1[] = ss[0].split(",");
        String s2[] = ss[1].split(",");
 
        cor = new double[s1.length];

        //iterate through set elements, one for each common ancestor
        for (int i=0; i<s1.length; i++)
        {
            cor[i] = get_cor(s1[i].strip() + s2[i].strip());
            cor_total = cor_total + cor[i];
        }  //next i
        
       }
       catch(Exception e)
       {
           return cor_total;
       }
       return cor_total;
    }
     
     
     public static double get_cor(String s)
     {
         double cor = 1.0;
         
         for (int i=0; i<s.length(); i++)
         {
             String sx = s.substring(i,i+1);
             if (sx.compareTo("M")==0)
             {
                 cor = cor * gen.neo4jlib.neo4j_info.male_crossocer_rate;
             }
             else
             {
                 cor = cor * gen.neo4jlib.neo4j_info.female_crossocer_rate;
             }
         }
         return cor;
     }
     public static String fix_s(String str)
     {
         String r = str.replace("\"","").replace("[","").replace("]","").replace("\n","").strip();
         return r;
         }
           
}
