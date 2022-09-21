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


public class Y_haplotree_with_matches {
    @UserFunction
    @Description("Y-DNA haplotree with matches.")

    public String Y_haplotree_matches(
         @Name("haplogroup") 
            String haplogroup
    )
   
         { 
             
        String s = get_tree(haplogroup);
         return s;
            }

    
    
    public static void main(String args[]) {
        //get_tree("R-M269");
    }
    
     public String get_tree(String haplogroup)
    {
        String cq = "MATCH (n:DNA_Match) where n.YHG is not null RETURN count(*) as ct";
        Long matchCt = Long.parseLong(gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n")[0]);
        cq = "MATCH path=(b1:block{name:'" + haplogroup + "'})-[[r:blockchild*0..999]]->(b2:block) with b2.name as hg,path,[[m in nodes(path)|id(m)]] as R optional match (m:DNA_Match) where m.YHG=hg with hg,apoc.coll.sort(collect(case when m.ancestor_rn>0 then '*^' + m.fullname else m.fullname end + case when m.RN is not null then ' ⦋' + m.RN + '⦌' else '' end)) as match, R, gen.graph.get_ordpath(R) as op with R,match,hg order by op MATCH p=(b3:block{name:hg})-[[r:block_snp]]->(v) with R,match,hg,apoc.coll.sort(collect(v.name)) as variants return apoc.text.lpad('',(size(R)-1)*3,'.') + hg as haplogroup ,size(variants) as var_ct,variants,size(R) as haplotree_level, size(match) as match_ct,case when size(match)>100 then 'truncated ' + apoc.coll.remove(match,100,size(match)-99) else match end as match";
        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq,gen.neo4jlib.neo4j_info.project +  "_Y_hapolotree_with_matches", "Y_tree_with_matches", 1, "","1:####;3:####;4:#####", "", true,"udf\nreturn gen.tree.Y_haplotree_matches('" + haplogroup + "')\n\ncyspher query\n" + cq + "\n\nvisualization query; run in Neo4j browser:\nMATCH path1=(b1:block{name:'R-L555'})-[[r:blockchild*0..999]]->(b2:block) with b2,path1,[[m in nodes(path1)|id(m)]] as R  match path2=(m:DNA_Match)-[[rb:match_block]]->(b2) where m.YHG=b2.name return path1,path2 \n\nThis Excel output is the FTDNA Y haplotree with matches shown at the branch/block.\n\nY_DNA matches are reported only if there is an at-DNA match.\nThe Neo4j browser visualization includes all Y-DNA testers at the branch/block\n\nSearch for *^ to locate matches who are descended from the common ancestor.\n\nThe most recently revised json file with the Y-haplotree is at\nhttps://www.familytreedna.com/public/y-dna-haplotree/get\n\n", true);
        return "completed\n\nYou may need to manually open column A in the Excel.";
    }
}
