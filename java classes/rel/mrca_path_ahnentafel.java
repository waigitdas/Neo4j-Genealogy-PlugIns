/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
import gen.rel.ahnentafel;
import java.util.ArrayList;

public class mrca_path_ahnentafel {
    @UserFunction
    @Description("Parses base-2 ahnentafel to ahnentafel (in base 10) at each hop in the path to the most distant ancestor.")

public List<Long> ahn_path(
    @Name("ahnentafel") 
        String ahnentafel
        
  )
    
        
        { 
        List<Long> aa = ahnentafel_path( ahnentafel);
        return aa;
        }




    
    public static void main(String args[]) {
        ahnentafel_path("1011011");
    }
    
    public static List<Long> ahnentafel_path(String ahnentafel) {
        List<Long> ahn = new ArrayList();
        for (int i=1; i < ahnentafel.length()+1; i++) {
            Long x = Long.parseLong(ahnentafel.substring(0,i),2);
            //System.out.println(x);
            ahn.add(x);
        }
        //System.out.println(ahn);
        return ahn;
    }
    
}
        