/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.endogamy;

import gen.neo4jlib.neo4j_qry;
import java.util.ArrayList;
import java.util.List;
import org.javatuples.Pair;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class dna_inherited {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String inherited_dna(
        @Name("rn") 
            Long rn
  )
   
         { 
             
        compute_dna(rn);
        return "";
            }

    
    
    public static void main(String args[]) {
        compute_dna(12L);
    }
    
     public static String compute_dna(Long rn) 
    {
        int mx = Integer.parseInt(gen.neo4jlib.neo4j_qry.qry_str("match p= (p1:Person{RN:" + rn + "})-[r1:father|mother*0..15]->(a:Person) with [x in nodes(p)|x.RN] as rns return max(size(rns)) as s").replace("[","").replace("]",""));
        String cq = "match p= (p1:Person{RN:" + rn + "})-[r1:father|mother*0..15]->(a:Person) with [x in nodes(p)|x.RN] as rns with apoc.coll.reverse(rns) as rns, gen.graph.get_ordpath(rns) as op return rns order by op desc";
        String paths[] = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n");
        String rt[] = gen.neo4jlib.neo4j_qry.qry_to_csv("match p= (p1:Person{RN:12})-[r1:father|mother*0..15]->(a:Person) with [x in nodes(p)|x.RN] as rns with apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect(rns)))) as rns return rns").replace("[","").replace("]","").split(",");
        String arns[] = gen.neo4jlib.neo4j_qry.qry_to_csv("match p= (p1:Person{RN:12})-[r1:father|mother*0..15]->(a:Person) with a where a.uid=0 with collect(distinct(a.RN)) as arns return arns").replace("[","").replace("]","").split(",");
        
        Long rns[][] = new Long[rt.length][4];
        //0 = rn
        //1 = ancestor (1=true)
        //2 = appearance
        //3 = unique path count
        
        Double dna[] = new Double[rt.length];
        String rn_paths[] = new String[rns.length];
       
        //add initial values of rn and dna_contribution
        for (int i=0;i<rt.length;i++)
        {
            rns[i][0] = Long.parseLong(rt[i].strip());
            rns[i][1] = 0L;
            rns[i][2] = 0L;
            rns[i][3] = 0L;
            dna[i] = 0.0;
            rn_paths[i] = "";
            }
        
        //initialize end of line ancestors to 1.0
        for (int i=0;i<arns.length;i++)
        {
            for (int j=0;j<rns.length;j++)
            {
                 if (Long.valueOf(arns[i].strip()).equals(rns[j][0]))
                 {
               dna[j] =1.0;
               rns[j][1]=1L;
               break;
                 }
                 }
        }
        
        ///////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////
        //iterate paths and fill other arrays
        //for each path ...
        //  increment counter in rns, item 2
        //  add any unique path id to the rn_paths
        String rn_path_temp = "";
        for (int i=0;i<paths.length;i++)
            //iterate paths
        {
            Boolean path_counted = false;
            rn_path_temp="";            
            String path_item[] = paths[i].replace("[","").replace("]","").split(",");
            Double carry = 0.0;
            for (int j=0;j<path_item.length;j++)
                //iterate RN in path
            {
                for (int k=0; k<rns.length;k++)
                {
                if (Long.valueOf(path_item[j].strip()).equals(rns[k][0]) )
                    //iterate and find RNs in path 
                        {
                            rns[k][2] = rns[k][2]+1; //add appearance in path
                            if (path_counted.equals(false))
                            {
                                rns[k][3] = rns[k][3] + 1;
                                rn_path_temp = "|" + i + "|";  //temp path item gfor comparison
                                 rn_paths[k] = rn_paths[k] + rn_path_temp;
                                path_counted=true;
                            }
                            
//                            rn_path_temp = "|" + path_item[j].strip() + "|";  //temp path item gfor comparison
//                            //for (int m=0;m<rns.length;m++)
//                               // {
//                                if(rn_paths[m].equals("") ||  rn_paths[m].replace(rn_path_temp,"")!=(rn_paths[k])  )
//                                { //look at rn_path to see if it is blank or does not have the path item
//
//
//                                    rn_paths[k] = rn_paths[k] + rn_path_temp;
//                                    int iou=0;
//                               //     break;
//                               // }
//                            }

                            if(carry.equals(0.0)) {
                                carry = dna[k];
                            }
                            else {
                                carry = carry * 0.5;
                                dna[k] = dna[k] + carry;
                                carry = carry * 0.5;
                            }
                        }
                }
            }
        }
        
        //report out
        for (int i=0;i<rns.length;i++)
        {
            System.out.println(i + "\t" + rns[i][0] + "\t" + rns[i][1] + "\t" + rns[i][2] + "\t" + rns[i][3] );
        }
        
            return "";
    }
}
