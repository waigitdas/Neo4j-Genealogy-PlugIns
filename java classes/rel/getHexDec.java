/**
 * Copyright 2020 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

public class getHexDec {
    @UserFunction
    @Description("gen.baseConvert('1101') - Convert the provided base 2 string to a base 10 integer.")
    public String getBase16(
            @Name("s") 
                Long i
            ) 
    {
           return Long.toString(i, 16);
    }
}
