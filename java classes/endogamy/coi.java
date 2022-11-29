/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.endogamy;

import gen.neo4jlib.neo4j_qry;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class coi {
    @UserFunction
    @Description("Coeffient of Imbreeding.")

    public Double coefficient_of_inbreeding(
        @Name("rn") 
            Long rn
  )
   
         { 
             
        Double coi = get_coi(rn);
         return coi;
            }

    
    
    public static void main(String args[]) {
        get_coi(1L);
        get_coi(4L);
        get_coi(5L);
        get_coi(12L);
        get_coi(13L);
        get_coi(18L);
        get_coi(19L);
        get_coi(42L);
        get_coi(1050L);
        get_coi(1058L);
        get_coi(1047L);
        get_coi(1051L);
    }
    
     public static Double get_coi(Long rn) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        try{
        String cq = "Match (p:Person{RN:" + rn +"}) with p MATCH path=(u1:Union{uid:p.uid})-[r:union_parent*0..25]->(ua:Union) with ua, reduce(s='',x in relationships(path)|s + x.side) as uid_path where ua.cor is not null with ua as ua,ua.cor as cor ,size(uid_path) + 1 as gen, uid_path order by gen with ua,cor,gen,uid_path, left(uid_path,1) as side with ua, cor,gen,uid_path,side limit 1 return cor * exp(log(0.5)*gen) as proband_coi";
        //String s = gen.neo4jlib.neo4j_qry.qry_str(cq);
        Double coi =  Double.parseDouble(gen.neo4jlib.neo4j_qry.qry_str(cq).replace("[","").replace("]",""));
        //System.out.println(" " + rn + "\t" + coi);
        return coi;
        }
        catch (Exception e){
            //System.out.println(" " + rn + "\t" + 0.0);
            return 0.0;
        }
        
        
       // try{
//            //calculated the coefficients of relationships for parents and then divide by 2.
//            Long rnprev= rn;
//            String cq="";
//            Long[] parent_rns;
//            String[] side = new String[50];
//            int[] gen = new int[50];
//            Double[][] endogamous_parent = new Double[4][4]; 
//            int epct=0;
//            gen.rel.get_parents_from_rn prn = new gen.rel.get_parents_from_rn();
//            gen.endogamy.has_endogamous_ancestors ea = new gen.endogamy.has_endogamous_ancestors();
//            
////            for (int i=0; i<99; i++)
//            {
//                String sa[] = ea.get_mrea(rnprev);
//                //System.out.println(sa[0] + "\t" + sa.length );
//               if (sa.length==2) 
//                { // has endogamous ancestor couple
//                    //check parent1
//                    parent_rns = prn.get_parents(rnprev);
//                    
//                    String sa1[] = ea.get_mrea(parent_rns[0]);
//                    String sa2[] = ea.get_mrea(parent_rns[1]);
//                    System.out.println(parent_rns.length + "*\t" + sa1[0] + "*\t" + sa2[0] );
//                    
//                }
//               
//            }

            
//            parent_rns = prn.get_parents(rnprev);
//                Double coi = gen.rel.coefficient_of_relationship.cor_calc(parent_rns[0], parent_rns[1]);
//                endogamous_parent[epct][0] = parent_rns[0].doubleValue();
//                endogamous_parent[epct][1] = parent_rns[1].doubleValue();
//                endogamous_parent[epct][2] = coi;
//
//                    if (coi.equals(0.0) )
//                        {  //parents not endogamous; check each parent
//                            endogamous_parent[epct][3] =0.0;
//                        }
//                else{ //parentes endogamous, add coi and exclude further ancestors
//                            endogamous_parent[epct][3] =1.0;
//                }

//                for (int j=0; j<parent_rns.length; j++)
//                {
//                }     
//            }
            
//            String cq="match p=(n:Person{RN:" + rn + "})-[r:father|mother*0..99]->(x) with apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect([z in nodes(p)|z.RN])))) as rns return rns";
//            
//            String s[] = gen.neo4jlib.neo4j_qry.qry_str(cq).replace("[","").replace("]","").split(",");
//            
//            
//             cq = "Match (p:Person{RN:" + rn + "}) match (u:Union) where u.uid=p.uid  with gen.rel.compute_cor(u.U1,u.U2)/2 as coi return coi";
                    //"match p=(n:Person{RN:" + rn + "})-[r:father|mother*0..99]->(x) with x,x.fullname + ' [' + x.RN + '] (' + left(x.BD,4) + '-' + left(x.DD,4) + ')' as Name, length(p) as gen, [z in nodes(p)|z.RN] as op, '1' + reduce(srt ='', q IN nodes(p)|srt + case when q.sex='M' then '0' else '1' end ) AS Anh with x,Name,gen,'1' + right(Anh,size(Anh)-2) as Ahnen, gen.graph.get_ordpath(op) as op optional match (d:Person{RN:x.RN}) with Name as Person,collect(distinct gen) as gen,collect(,gen.rel.ahnentafel(Ahnen)) as Ahnentafel,x with Person,x,apoc.coll.sort(Ahnentafel) as Ahnentafel,apoc.coll.sort(gen) as gen where size(Ahnentafel)>1 with Person,Ahnentafel,gen,gen.rel.compute_cor(" + rn + ",x.RN) as cor return  sum(cor) as coi ";
        
//           Double coi1 = Double.parseDouble(gen.neo4jlib.neo4j_qry.qry_str(cq).replace("[","").replace("]",""));
//            System.out.println(gen.gedcom.get_family_tree_data.getPersonFromRN(rn,false) + "\t\t\t" + coi1);
        //    return 0.0  ; //
//        catch(Exception e){
//            return 0.0;
//        }

    }
}
