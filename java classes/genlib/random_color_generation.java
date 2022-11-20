/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.genlib;

import gen.neo4jlib.neo4j_qry;
import java.util.Random;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class random_color_generation{
    @UserFunction
    @Description("Generates color.")

    public String random_color(
  )
   
         { 
             
        String c = get_color();
         return c;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_color() 
    {
        Random rand = new Random();
int r = rand.nextInt(255);
int b = rand.nextInt(255);
int g = rand.nextInt(255);
return '#' + Integer.toHexString(r) +  Integer.toHexString(g) +  Integer.toHexString(b);

//        String cq = "return '#' + right(apoc.text.hexValue(abs(toInteger(substring(toString(rand()),3,2))-toInteger(substring(toString(rand()),4,2)))),2) + right(apoc.text.hexValue(abs(toInteger(substring(toString(rand()),3,2))-toInteger(substring(toString(rand()),4,2)))),2) + right(apoc.text.hexValue(abs(toInteger(substring(toString(rand()),3,2))-toInteger(substring(toString(rand()),4,2)))),2) as color";
//        return gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n")[0].replace("\"", "");
    }
}
