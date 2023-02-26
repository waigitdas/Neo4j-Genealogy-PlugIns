/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.mt_research;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class mt_haplogroup_from_SNPs {
    @UserFunction
    @Description("computed mt-haplogroup from variant mutations.")

    public String mt_haplogroup_from_variants(
        @Name("variants") 
            String variants
  )
   
         { 
             
        String hg = get_hg(variants);
         return hg;
            }

    
    
    public static void main(String args[]) {
       //das kit
        //get_hg("['A769G','A825t','A1018G','G2706A','A2758G','C2885T','T3594C','G4104A','T4312C','T7028C','G7146A','T7256C','A7521G','T8468C','T8655C','G8701A','C9540T','G10398A','T10664C','A10688G','C10810T','C10873T','C10915T','A11719G','A11914G','T12705C','G13105A','G13276A','T13506C','T13650C','T14766C','A16129G','T16187C','C16189T','T16223C','G16230A','T16278C','C16311T','G73A','A93G','C146T','C152T','C195T','A247G','315.1C','522.1A','522.2C']");
        //kit 425498
        get_hg("['A16129G', 'T16187C', 'C16189T', 'T16223C', 'G16230A', 'T16278C', 'C16311T', 'G73A', 'C146T', 'C152T', 'A247G', 'A257G', '309.1C', '315.1C', 'T477C', '522.1A', '522.2C', 'A769G', 'A825t', 'A1018G', 'G1598A', 'G2706A', 'A2758G', 'C2885T', 'G3010A', 'T3594C', 'G4104A', 'T4312C', 'T7028C', 'G7146A', 'T7256C', 'A7521G', 'T8468C', 'T8473C', 'T8655C', 'G8701A', 'C9540T', 'G10398A', 'T10664C', 'A10688G', 'C10810T', 'C10873T', 'C10915T', 'A11719G', 'A11914G', 'T12705C', 'G13105A', 'G13276A', 'T13506C', 'T13650C', 'G14198A', 'T14766C']");
   }
    
     public static String get_hg(String variants) 
    {
        String cq = "with [" + variants + "] as proband_variants MATCH path=(b1:mt_block{name:'RSRS'})-[[r:mt_block_child*0..999]]->(b2:mt_block) with proband_variants,b2, [[x in nodes(path)|case when size(apoc.coll.intersection(x.variants, proband_variants))>0 then '*' else '' end + x.name]] as blocks,[[y in nodes(path)|id(y)]] as op, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten([[z in nodes(path) where z.variants is not null|z.variants]]))) as variants, [[w in nodes(path)|case when size(apoc.coll.intersection(w.variants, proband_variants))>0 then '1' else '0' end]] as lvl_ct with proband_variants,b2,blocks,variants,size(op) as lvl, lvl_ct,gen.graph.get_ordpath(op) as op with b2.name as block,lvl-1 as lvl, size(b2.variants) as ct, lvl_ct, b2.variants as block_variants, blocks,size(variants) as ct2, variants as cum_variants, op, apoc.coll.intersection(variants, proband_variants) as intersect,apoc.coll.subtract(proband_variants,variants ) as unused,apoc.coll.subtract(variants, proband_variants) as missing with block,lvl,apoc.coll.occurrences(lvl_ct,'1') as lvl_ok, blocks as path,ct2 as ref_variant_ct, size(intersect) as matching_variant_ct,size(missing) as missing_ct,size(unused) as unused_variant_ct, intersect,missing, unused, ct as block_variant_ct,block_variants,cum_variants with block, lvl, lvl_ok, path, ref_variant_ct, matching_variant_ct, missing_ct, unused_variant_ct, intersect, missing, unused, block_variant_ct, block_variants, cum_variants where lvl=lvl_ok return block, lvl, lvl_ok, path, ref_variant_ct, matching_variant_ct, missing_ct, unused_variant_ct, intersect, missing, unused, block_variant_ct, block_variants, cum_variants order by size(intersect) desc,lvl desc";
       
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "mt_haplogroup", "hg", 1, "", "1:###;2:####;3:####;4:####;5:####;6:####;7:####;11:####", "",true, "Cypher query:\n" + cq + "\n\nIf you delete where lvl=lvl_ok from the query just before the return you will get an unfiltered report\nwhich might help you understand any ano,alies.\n\nYVisualization query:\nwith [" + variants + "] as proband_variants MATCH path=(b1:mt_block{name:'RSRS'})-[[r:mt_block_child*0..999]]->(b2:mt_block) with proband_variants,b2, [[x in nodes(path)| x.name]] as blocks,[[y in nodes(path)|id(y)]] as op, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten([[z in nodes(path) where z.variants is not null|z.variants]]))) as variants, [[w in nodes(path)|case when size(apoc.coll.intersection(w.variants, proband_variants))>0 then '1' else '0' end]] as lvl_ct with proband_variants,b2,blocks,variants,size(op) as lvl, lvl_ct,gen.graph.get_ordpath(op) as op with b2.name as block,lvl-1 as lvl, size(b2.variants) as ct,lvl_ct,b2.variants as block_variants,blocks,size(variants) as ct2,variants as cum_variants,op, apoc.coll.intersection(variants, proband_variants) as intersect,apoc.coll.subtract(proband_variants,variants ) as unused, apoc.coll.subtract(variants, proband_variants) as missing with block,lvl,apoc.coll.occurrences(lvl_ct,'1') as lvl_ok, blocks as path,ct2 as ref_variant_ct, size(intersect) as matching_variant_ct,size(missing) as missing_ct, size(unused) as unused_variant_ct, intersect,missing, unused, ct as block_variant_ct,block_variants,cum_variants with block, lvl, lvl_ok, path, ref_variant_ct, matching_variant_ct, missing_ct, unused_variant_ct, intersect, missing, unused, block_variant_ct, block_variants, cum_variants where lvl=lvl_ok MATCH path2=(b1:mt_block{name:'RSRS'})-[[r2:mt_block_child*0..999]]-(b2:mt_block)-[[rs2:mt_block_variant]]->(v2:mt_variant) where b2.name in path and v2.name in cum_variants return path2", false);
        return "completed";
    }
}
