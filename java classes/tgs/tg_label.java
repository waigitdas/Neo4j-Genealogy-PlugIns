/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.tgs;
    import gen.neo4jlib.neo4j_info;
    import gen.neo4jlib.neo4j_qry;
            
/**
 *
 * @author david
 */
public class tg_label {

    public static String getTgLabel(Long tgid) {
        gen.neo4jlib.neo4j_info.neo4j_var();
        String s = gen.neo4jlib.neo4j_qry.qry_str("match (t:tg{tgid:" + tgid + "}) return t.chr + '-' + t.strt_pos + '-' + t.end_pos");
        s = s.replace("\"","").replace("[","").replace("]","");
        return s;
    }
    
    
}
