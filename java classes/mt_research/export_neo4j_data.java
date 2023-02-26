/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.mt_research;

public class export_neo4j_data {

    public static void main(String args[]) {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        gen.neo4jlib.neo4j_qry.qry_to_csv("MATCH p=(k:seq_kit)-[r:kit_tile]->(t:tile) where k.name>='A' and k.Ns is null with k,t order by t.n with k,collect(t.tile_id) as pattern RETURN k.name as kit,k.gb_hg as hg,size(pattern) as tile_ct,pattern order by kit","kit_seq.csv");
        
    }
}
