/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */

package gen.auth;
    
    import gen.neo4jlib.neo4j_info;
    import org.neo4j.driver.AuthTokens; 
    import org.neo4j.driver.AuthToken;


public class AuthInfo {

    public static AuthToken getToken( )
    {
        neo4j_info.neo4j_var();
        AuthToken myToken = AuthTokens.basic(neo4j_info.neo4j_username,neo4j_info.neo4j_password );
        return myToken;
    }
   
}
