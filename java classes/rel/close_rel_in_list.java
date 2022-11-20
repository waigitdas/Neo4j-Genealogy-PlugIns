/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class close_rel_in_list {
    @UserFunction
    @Description("Template used in creating new functions.")

    public List coalsece_close_rel_in_list(
        @Name("name_list") 
            List<String> name_list,
        @Name("max_cor") 
            Double max_cor
  )
   
         { 
             
        List ls = fix_list(name_list,max_cor);
         return ls;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public List fix_list(List<String> names, Double min_cor) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String cq = "";
        List<String> nl =  new ArrayList<String>();
        Double cor =1.0;
        String cqq = "";
        String Q ="";  // "\"";
        
        for (int i = 0; i < names.size()-1; i++) {
            for (int j = 0; j < names.size(); j++) {
                if (names.get(i).compareTo(names.get(j))<0) //less than
           {
                 try{
                     cq = "MATCH p=()-[r:match_segment]->() where  (r.p='" + names.get(i) + "' and r.m='" + names.get(j) + "') or (r.m='" + names.get(i) + "' and (r.p='" + names.get(j) + "')) RETURN distinct r.cor as cor";
                     cqq = gen.neo4jlib.neo4j_qry.qry_str(cq).replace(";","");
                     cor = Double.parseDouble(gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n")[0]);
                     //cor = Double.parseDouble(gen.neo4jlib.neo4j_qry.qry_str(cq).replace(";",""));
                 }
                
                 catch (Exception e)
                         { cor = 0.01;}
                 //nl.add(Q + cor + Q);
                 if (cor <= min_cor){
                  nl.add(Q + names.get(i) + Q);
                  nl.add(Q + names.get(j) + Q);
                 }
                 else {
                     nl.add(Q + names.get(i) + ":" + names.get(j) + Q);
                 }
                }
             }
        }
             
        List<String> unl = nl.stream().distinct().collect(Collectors.toList());
        
        for (int i = 0; i < unl.size(); i++) {
            String[] cs = unl.get(i).split(Pattern.quote(":"));
            if (cs.length == 2) {
                for (int j=unl.size()-1; j > -1  ; j--)
                {
                    if (cs[0].equals(unl.get(j))) { unl.remove(j);}
                    if (cs[1].equals(unl.get(j))) { unl.remove(j);}
            }
            }
            
        }
 
        
        return unl;
    }
}
