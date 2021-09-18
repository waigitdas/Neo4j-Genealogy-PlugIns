/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.neo4jlib;
    import org.neo4j.procedure.Description;
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;

/**
 *
 * @author david
 */
public class bracketedRN {
   @UserFunction
    @Description("Unicode brackets that appear the same, but are different from [] used by Neo4j and replaced to obscure them.")

    public String RNwithBrackets(
        @Name("rn") 
            Long rn
    )   
        { 
        String s = getBracketedRN(rn);
            return s;
        }
        
        
        
    public static String getBracketedRN(Long rn) {
        String x = " \u298B" + String.valueOf(rn) + "\u298C ";  //space refix and suffix; unicode for []
        return x;
    }
    public static void main(String args[]) {
        System.out.println(getBracketedRN(Long.valueOf(23456)));
    }
}

   