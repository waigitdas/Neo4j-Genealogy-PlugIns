package genealogy;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 * This is an example of a simple user-defined function for Neo4j to perform a baseConvert
 */
public class base2to10 {

    @UserFunction
    @Description("example.baseConvert('1101', 2) - Convert the provided base 2 string to a base 10 integer.")
    public Long baseConvert(
            @Name("s") 
                String s
            
            ) 
    {
       
        return Long.parseLong(s,  2);
    }
}
