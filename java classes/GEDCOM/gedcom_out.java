/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.gedcom;
    import gen.neo4jlib.neo4j_qry;
    import gen.genlib.*;
    import java.util.List;       
    import org.neo4j.procedure.Description;
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;

public class gedcom_out {
    @UserFunction
    @Description("Creates GEDCOM.")

    /**
     * @param args the command line arguments
     */
    public String create_gedcom (
        @Name("rn") 
            Long rn,
         @Name("ged_file") 
            String ged_file,
        @Name("db") 
            String db
        )
  
        
    {
                    float tm = System.currentTimeMillis();
                    get_gedcom(rn,ged_file,db);
                    float tmelapsed = System.currentTimeMillis() - tm;
                    return "Completed in " + tmelapsed + " msec" + "\nFile at " + ged_file;
 }
    
    
    public static String get_gedcom(Long RN, String ged_file, String db) {
      String header = "0 HEAD\n1 SOUR Graphs for Genealogists\n2 CORP Who Am I\n3 ADDR 1101 Alpine Lane\n4 CONT Woodstock, Illinois 60098-9726, USA\n3 PHON 1-847-494-7589\"3 EMAIL genealogy@stumpf.org\n1 DATE 4 Spt 2021 & \n1 CHAR ASCII\n1 GEDC\n2 VERS 5.5\n2 Form Lineage - Linked\n1 LANG English\n";  
        
     String cq = "match (n:Person{RN:" + RN + "})-[r:father|mother*0..99]->(m:Person) where m.RN>0 with collect(distinct m.RN) as RNDirAnc match p=((s:Person)-[:father|mother*0..99]->(m:Person)) where s.RN>0 and m.RN in RNDirAnc with RNDirAnc,collect(distinct s.union_id) as UIDFAMC,collect(distinct s.RN) as RNDesc match (s)-[:husband|wife*0..1]->(t) where t.RN>0 and s.RN in RNDesc with RNDirAnc,UIDFAMC,RNDesc,collect(distinct t.RN) as RNSp,collect(t.union_id) as UIDSp match (s)-[:ufather|umother*0..1]->(u:Union)-[:child*0..1]-(c) where u.UID>1 and u.U1>0 and u.U2>0 and s.RN in RNDesc with RNDirAnc,RNDesc,UIDFAMC,RNSp,UIDSp,collect(distinct u.UID) as UIDFAMS,collect(distinct case when s.sex='M' then u.U2 else u.U1 end) as RNSpFAMS,collect(distinct c.RN) as RNFAMChild with RNDirAnc,RNDesc,UIDFAMC,RNSp,UIDSp,UIDFAMS,RNSpFAMS,RNFAMChild,collect(distinct RNDirAnc + RNDesc + RNSp + RNSpFAMS + RNFAMChild) as RNAll,collect(distinct UIDFAMC + UIDSp + UIDFAMS) as UAll match (pp:Person) where pp.RN in RNAll[0] optional match (pp)-[:ufather|umother]-(uu) with pp,collect(uu) as UN,collect(case when uu.U1=pp.RN then uu.U2 else uu.U1 end) as USpp with '0 @I' + pp.RN + '@ INDI ' + '~~1 NAME ' + trim(pp.name) + '/' + trim(pp.surname) + '/' + '~~1 SEX ' + pp.sex + '~~1 BIRT ' + '~~2 DATE ' + pp.BDGed + '~~2 PLAC ' + pp.BP + '~~1 DEAT ' + '~~2 DATE ' + pp.DDGed + '~~2 PLAC ' + pp.DP + case when pp.union_id>1 then '~~1 FAMC @F' + pp.union_id + '@' else '' end + reduce(z='',v in UN|z + '~~1 FAMS @F' + v.UID + '@~~') as GEDCOM_Indv return GEDCOM_Indv";
        
      List<String> sl = neo4j_qry.qry_str_list(cq, db);
      String indv = gen.genlib.listStrToStr.list_to_string(sl);
      indv = indv.replace("~~", "\n");
      
      cq = "match (n:Person{RN:" + RN + "})-[r:father|mother*0..99]->(m:Person) where m.RN>0 with collect(distinct m.RN) as RNDirAnc match (s:Person)-[:father|mother*0..99]->(m:Person) where s.RN>0 and m.RN in RNDirAnc with RNDirAnc,collect(distinct s.RN) as RNDesc with [RNDirAnc,RNDesc] as n1 unwind n1 as uw1 unwind uw1 as uw2 with distinct uw2 with collect(distinct uw2) as RNDirRel match (t)-[:ufather|umother*0..1]->(ut) where ((t.U1 in RNDirRel and ut.U2>0) or (ut.U2 in RNDirRel and ut.U1>0)) with RNDirRel,collect(distinct case when ut.U1 in RNDirRel then ut.U2 else ut.U1 end) as RNSp match (u:Union) where (u.U1 in RNDirRel or u.U2 in RNDirRel) optional match (u:Union)-[:child*0..1]->(c:Person) with u, collect(c.RN) as Child,RNDirRel return case when size(Child)=0 then '0 @F' + u.UID + '@ FAM' + '~~1 HUSB @I' + u.U1 + '@~~1 WIFE @I' + u.U2 + '@~~1 MARR~~2 DATE ' + u.UDGed + '~~2 PLAC ' + u.UP else '0 @F' + u.UID + '@ FAM' + '~~1 HUSB @I' + u.U1 + '@~~1 WIFE @I' + u.U2 + '@~~1 MARR~~2 DATE ' + u.UDGed + '~~2 PLAC ' + u.UP + reduce(w='',x in Child|w + '~~1 CHIL @I' + x + '@' ) end as Unions order by u.UID";
      List<String> slf = neo4j_qry.qry_str_list(cq, db);
      String fam = gen.genlib.listStrToStr.list_to_string(slf);
      fam = fam.replace("~~", "\n");
      String ged = header + indv + fam  + "0 TRLR";
      ged = ged.replace("@0","@\n0");
      
      gen.neo4jlib.file_lib.writeFile(ged, ged_file);
      return ged;  
    }
}
