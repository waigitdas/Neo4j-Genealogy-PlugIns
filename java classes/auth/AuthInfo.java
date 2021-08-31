/**
 * Copyright 2020 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */

package gen.auth;
    
    import org.neo4j.driver.AuthTokens; 
    import org.neo4j.driver.AuthToken;

public class AuthInfo {

    public static AuthToken getToken( )
    {
        AuthToken myToken = AuthTokens.basic( "neo4j", "cns105" );
        return myToken;
    }
   
}
