/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.discovery;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class surname_variants {
    @UserFunction
    @Description("Matches to the surname list submitted.")

    public String matches_by_surname(
        @Name("variant_spellings") 
            String variant_spellings
  )
   
         { 
             
        String s = get_matches(variant_spellings);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_matches(String variant_spellings) 
    {
        String[] vs = variant_spellings.replace("'","").split(",");
        String vss ="";
        for (int i=0; i < vs.length; i++ ){
            vss = vss + "'" +  vs[i].strip() + "'";
            if (i != vs.length-1) {vss = vss + ", ";}
         }
        String cq="MATCH p=() -[[r:KitMatch]]->(f:DNA_Match) where f.surname in [[" + vss + "]] And f.kit is null with collect(distinct f) as fm match (m1:DNA_Match)-[[r1:match_segment]]->(s:Segment)<-[[r2:match_segment]]-(m2:DNA_Match) where m1 in fm and m1.fullname<>m2.fullname and r1.cm>=7 and r1.snp_ct>=500 and r2.cm>=7 and r2.snp_ct>=500 with distinct s,m1,r1,r2,s.chr as chr,s.strt_pos as strt_pos, s.end_pos as end_pos,m2.fullname as match2,s.cm as cm,m2, case when m2.kit is not null then '*' + m2.fullname else m2.fullname end as nn, case when m2.kit is not null then 1 else 0 end as nct with s,r1,r2,m1,chr,strt_pos,end_pos,cm, nn,nct,m2 order by nn with s,m1,r1,r2,chr,strt_pos,end_pos,cm,sum(nct) as KitCt,collect(distinct nn) as matches,collect(distinct m2.RN) as RNs match (c:Person) where c.RN in RNs with s,m1,r1,r2,chr,strt_pos,end_pos,cm,KitCt,matches,RNs,c order by c.RN with s,m1,r1,r2,chr,strt_pos,end_pos,cm,KitCt,matches,RNs,collect(distinct c.RN) As cc match (m1)-[[rms:match_by_segment]]-(ms:DNA_Match) where rms.longest_cm>=7 with s,m1,r1,r2,chr,strt_pos,end_pos,cm,KitCt,matches,RNs,cc,collect(distinct '*' + ms.fullname) as msn, collect(distinct ms.RN) as msrn with s,m1,r1,r2,chr,strt_pos,end_pos,cm,KitCt,RNs,msn, apoc.coll.sort(apoc.coll.union(cc,msrn)) as cc, apoc.coll.sort(apoc.coll.union(msn,matches)) as matches match (c2:Person)-[[:father|mother*0..10]]->(MRCA:Person)<-[[:father|mother*0..10]]-(c3:Person) where c2.RN in cc And c3.RN in cc with msn, s, m1,r1,r2, chr, strt_pos, end_pos,cm,KitCt,matches,RNs,MRCA,cc,c2 order by c2.RN with msn, s, m1,r1,r2, chr, strt_pos, end_pos, cm, KitCt, matches, RNs, MRCA, cc, collect(distinct c2.RN) as cc2 with msn, s, m1,r1,r2, chr, strt_pos, end_pos, cm, KitCt,matches,RNs, cc,cc2, collect(distinct MRCA.fullname + ' ⦋' + MRCA.RN + '⦌') as CommonAncestors,msn as match1_source_kits where cc2=cc with m1.fullname as match1,case when m1.notes is null then '  ' else m1.notes end as notes,case when m1.email is null then '  ' else m1.email end as email, chr,strt_pos,end_pos,cm,matches,RNs,cc,CommonAncestors,match1_source_kits,min(r1.cm) as r1_cm,max(r2.cm) as r2_cm, min(r1.snp_ct) as r1_snps,max(r2.snp_ct) as r2_snps with match1,chr,strt_pos,end_pos,r1_cm,r2_cm,r1_snps,r2_snps,size(matches) as MatchCt, size([[x IN matches WHERE left(x,1)='*']]) as KitCt, matches as matches_at_any_segment,cc as RNs_matches_at_this_segment,CommonAncestors, match1_source_kits,notes,email with distinct match1, chr, strt_pos, end_pos, r1_cm,r2_cm, r1_snps,r2_snps,MatchCt,KitCt,matches_at_any_segment,RNs_matches_at_this_segment,CommonAncestors, match1_source_kits,notes,email order by chr,strt_pos,end_pos,match1 optional match (t:tg) where t.chr=chr and strt_pos>=t.strt_pos and end_pos<=t.end_pos with match1, chr, strt_pos, end_pos,case when t.tgid is null then '-' else toString(t.tgid) end as tg,r1_cm as cm, r1_snps as snps,MatchCt,KitCt,matches_at_any_segment,RNs_matches_at_this_segment,CommonAncestors, match1_source_kits,notes,email return match1,chr,strt_pos, end_pos,tg,cm, snps,MatchCt,matches_at_any_segment,RNs_matches_at_this_segment, CommonAncestors,match1_source_kits,notes,email order by chr,strt_pos,end_pos,match1";
 try{
        gen.excelLib.queries_to_excel.qry_to_excel(cq, vs[0] + "_matches", "matches", 1, "", "1:##;2:###,###,###;3:###,###,###;4:###;5:####.#;7:###;8:####", "", true, "UDF:\n return gen.discovery.matches_by_surname('" + variant_spellings + "')\n\nquery:\n" + cq + "\n\nFinds all the match with their surname in the submitted list. Use for evaluating variations in the spelling of surnames.\n\nThe results may help identify new triangulation groups for the surname(s).", true);
        return "completed";
 }
 catch (Exception e) {return "Error occurred.\n" + e.getMessage() + "\n\nYou might try running this query with fewer surnames\n\n" + cq;}
 
 
 }
}
