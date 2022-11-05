/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tree;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class mt_haplotree_with_matches {
    @UserFunction
    @Description("mt-DNA haplotree with matches.")

    public String mt_haplotree_matches(
  )
   
         { 
             
        String s = get_tree();
         return s;
            }

    
    
    public static void main(String args[]) {
        get_tree();
    }
    
     public static String get_tree()
    {
        String cq = "MATCH (n:DNA_Match) where n.mtHG is not null RETURN count(*) as ct";
        Long matchCt = Long.parseLong(gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n")[0]);
        cq = "MATCH path=(b1:mt_block{name:'RSRS'})-[[r:mt_block_child*0..999]]->(b2:mt_block) with b2.name as hg,path,[[m in nodes(path)|id(m)]] as R optional match (m:DNA_Match) where m.mtHG=hg with hg,apoc.coll.sort(collect(case when m.ancestor_rn>0 then '*^' + m.fullname else m.fullname end + case when m.RN is not null then ' [[' + m.RN + ']]' else '' end)) as match, R, gen.graph.get_ordpath(R) as op with op, R,match,hg order by op MATCH p=(b3:mt_block{name:hg})-[[r:mt_block_snp]]->(v) with v, R,match,hg order by op, v.sort with R,match,hg, collect(v.name) as variants return apoc.text.lpad('',(size(R)-1)*3,'.') + hg as haplogroup ,size(variants) as var_ct,variants,size(R) as haplotree_level, size(match) as match_ct,case when size(match)>100 then 'truncated ' + apoc.coll.remove(match,100,size(match)-99) else match end as match";
                //"MATCH path=(b1:mt_block{name:'RSRS'})-[[r:mt_block_child*0..999]]->(b2:mt_block) with b2.name as hg,path,[[m in nodes(path)|id(m)]] as R optional match (m:DNA_Match) where m.mtHG=hg with hg,apoc.coll.sort(collect(case when m.ancestor_rn>0 then '*^' + m.fullname else m.fullname end + case when m.RN is not null then ' ⦋[' + m.RN + '⦌]' else '' end)) as match, R, gen.graph.get_ordpath(R) as op with R,match,hg order by op MATCH p=(b3:mt_block{name:hg})-[[r:mt_block_snp]]->(v) with R,match,hg,apoc.coll.sort(collect(v.name)) as variants return apoc.text.lpad('',(size(R)-1)*3,'.') + hg as haplogroup ,size(variants) as var_ct,variants,size(R) as haplotree_level, size(match) as match_ct,case when size(match)>100 then 'truncated ' + apoc.coll.remove(match,100,size(match)-99) else match end as match";
        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project +  "_mt_hapolotree_with_matches", "mtree_with_matches", 1, "","1:####;3:####;4:#####", "", true,"udf\nreturn gen.tree.mt_haplotree_matches()\n\ncyspher query\n" + cq + "\n\nTotal number of mt-DNA matchis: " + matchCt + "\n\nThis is the FTDNA mitochondrial haplotree with matches shown at the branch.\n\nmt_DNA matches are reported only if the match pair has an at-DNA match.\nThe reported genetic distance the number of non-congruent mutations.\n\nSearch for ^ to locate matches who are descended from the common ancestor.\n\nThe most recently revised json file with the mt-haplotree is at\nhttps://www.familytreedna.com/public/mt-dna-haplotree/get\n\nShown is the Reconstructed Sapiens Reference Sequence (RSRS)\n\nHeteroplasmy codes are at\nhttps://learn.familytreedna.com/mtdna-testing/heteroplasmy-nomenclature/\n\nother nominclatures\ninsertions\nhttps://learn.familytreedna.com/mtdna-testing/insertion-nomenclature-display/.\ndeletions\nhttps://learn.familytreedna.com/mtdna-testing/nomenclature-display-deletion/\ntransversions\nhttps://learn.familytreedna.com/mtdna-testing/transversion-nomenclature-display/\ntransitions\nhttps://learn.familytreedna.com/mtdna-testing/transition-nomenclature-display/", true);
        return "completed";
    }
}
