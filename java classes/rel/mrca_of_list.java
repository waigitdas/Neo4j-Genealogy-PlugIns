/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;
    import gen.neo4jlib.neo4j_qry;

    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;
    import org.neo4j.procedure.Description;
     import gen.conn.connTest;
 
    import java.util.List;        
      

public class mrca_of_list {          
    @UserFunction
    @Description("Returns most recent common ancestor shared by multiple persons. Each pair of persons in the list may have a more recent common ancestor who is descended from the reported ancestor shared by all in the list. Returns nothing if there is none.")
        
    public String mrca_from_list(
        @Name("rn_list") 
            String rn_list
            
    )

        {
        String qry = "match (c:Person) where c.RN in [" + rn_list + "] With c order by c.RN With collect(distinct c.RN) As cc match (c2:Person)-[:father|mother*0..20]->(MRCA:Person)<-[:father|mother*0..20]-(c3:Person) where c2.RN in cc And c3.RN in cc and c2.RN<>c3.RN with MRCA,cc,c2 order by c2.RN with MRCA,cc,collect(distinct c2.RN) as cc2 with distinct cc,cc2,MRCA.fullname + ' [' + MRCA.RN + ']' as CommonAncestor where cc2=cc return CommonAncestor";
        List r =mrca_from_list_qry(qry,rn_list);
        try{
        String s = gen.genlib.listStrToStr.list_to_string(r);
        return s;
        }
        catch (Exception e){return "No common ancestor.";}       
        
        }
    
   
    public List<String> mrca_from_list_qry(String qry,String rn_list) 
    {
     return neo4j_qry.qry_str_list(qry);
}
              
}