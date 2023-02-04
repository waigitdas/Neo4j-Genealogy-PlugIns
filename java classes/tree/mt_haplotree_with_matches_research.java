/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tree;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class mt_haplotree_with_matches_research {
    @UserFunction
    @Description("mt-DNA haplotree with matches.")

    public String mt_haplotree_matches_research(
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
        cq ="MATCH path=(b1:mt_block{name:'RSRS'})-[r:mt_block_child*0..999]->(b2:mt_block) with b2,path,[m in nodes(path)|id(m)] as R optional match (m:DNA_Match) where m.mtHG=b2.name with b2,apoc.coll.sort(collect(case when m.ancestor_rn>0 then '*^' + m.fullname else m.fullname end + case when m.RN is not null then ' [' + m.RN + ']' else '' end)) as match, R, gen.graph.get_ordpath(R) as op with op, R,match,b2 order by op MATCH p=(b3:mt_block{name:b2.name})-[r:mt_block_snp]->(v:mt_variant) with v,r, R,match,b2 order by op, v.sort with R,match,b2, collect(case when r.ftdna=1 or r.phylotree=1 then case when r.phylotree=1 then '^' + v.name else v.name end else '' end) as variants,sum(r.phylotree) as phylotree_snps return apoc.text.lpad('',(size(R)-1)*3,'.') + case when b2.phylotree is null then b2.name else '^' + b2.name end as haplogroup ,size(variants) as var_ct,phylotree_snps,variants,size(R) as haplotree_level, size(match) as match_ct,case when size(match)>100 then 'truncated ' + apoc.coll.remove(match,100,size(match)-99) else match end as match";
        int ct=1;
        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project +  "_mt_hapolotree_with_matches", "ftdna mtree_with_matches", ct, "","1:####;2:####;4:####;5:#####", "", false,"udf\nreturn gen.tree.mt_haplotree_matches()\n\ncypher query\n" + cq + "\n\nTotal number of mt-DNA matchis: " + matchCt + "\n\nThis is the FTDNA mitochondrial haplotree with phylotree blocks and SNPs tagged with a caret (^) abd matches shown at the branch.\n\nmt_DNA matches are reported only if the match pair has an at-DNA match.\nThe reported genetic distance the number of non-congruent mutations.\n\nSearch for ^ to locate matches who are descended from the common ancestor.\n\nThe most recently revised json file with the mt-haplotree is at\nhttps://www.familytreedna.com/public/mt-dna-haplotree/get\n\nShown is the Reconstructed Sapiens Reference Sequence (RSRS)\n\nHeteroplasmy codes are at\nhttps://learn.familytreedna.com/mtdna-testing/heteroplasmy-nomenclature/\n\nother nominclatures\ninsertions\nhttps://learn.familytreedna.com/mtdna-testing/insertion-nomenclature-display/.\ndeletions\nhttps://learn.familytreedna.com/mtdna-testing/nomenclature-display-deletion/\ntransversions\nhttps://learn.familytreedna.com/mtdna-testing/transversion-nomenclature-display/\ntransitions\nhttps://learn.familytreedna.com/mtdna-testing/transition-nomenclature-display/\n\nThe phylotree.org haplotree is returned with this cypher query:\nMATCH path=(b1:mt_block{name:'RSRS'})-[[r:mt_block_child*0..999]]->(b2:mt_block) where b2.phylotree=1  with b2,path,[[m in nodes(path)|id(m)]] as R optional match (m:DNA_Match) where m.mtHG=b2.name with b2,apoc.coll.sort(collect(case when m.ancestor_rn>0 then '*^' + m.fullname else m.fullname end + case when m.RN is not null then ' [[' + m.RN + ']]' else '' end)) as match, R, gen.graph.get_ordpath(R) as op with op, R,match,b2 order by op MATCH p=(b3:mt_block{name:b2.name})-[[r:mt_block_snp]]->(v{src:'phylotree'}) with v, R,match,b2 order by op, v.sort with R,match,b2, collect(case when v.src='phylotree' then '^' + v.name else v.name end) as variants return apoc.text.lpad('',(size(R)-1)*3,'.') + case when b2.phylotree is null then b2.name else '^' + b2.name end as haplogroup ,size(variants) as var_ct,variants,size(R) as haplotree_level, size(match) as match_ct,case when size(match)>100 then 'truncated ' + apoc.coll.remove(match,100,size(match)-99) else match end as match", false);
           
        return "completed";
    }
}
