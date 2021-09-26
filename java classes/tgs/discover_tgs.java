/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;
    import org.neo4j.procedure.Description;
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;
    import gen.neo4jlib.neo4j_qry;
    import gen.neo4jlib.neo4j_info;
    import gen.neo4jlib.neo4j_info;
    import gen.conn.connTest;
import java.io.FileWriter;
import java.util.regex.Pattern;

public class discover_tgs {
    @UserFunction
    @Description("Identifies triangulation group candidates")

    public String tg_disc (
        @Name("ancestor_rn") 
            Long ancestor_rn
//         @Name("ged_file") 
//            String ged_file
        )
  
        
    {
        neo4j_info.neo4j_var();
        //connTest.cstatus();
        initialPhase(ancestor_rn);   //Long.valueOf(33454));  //,"01",neo4j_info.Import_Dir + "tg_disc.csv");
        return "Completed";
 }
    
    
    public static void initialPhase(Long mrca) {
        String cq = "MATCH ()-[r:seg_seq]-() delete r";
        neo4j_qry.qry_write(cq);
        cq = "MATCH ()-[r:seg_lattice]-() delete r";
        neo4j_qry.qry_write(cq);
       cq = "MATCH (lat:lattice) delete lat";
        neo4j_qry.qry_write(cq);
     neo4j_qry.CreateIndex("lattice", "lid");

        //get segments of mcra descendant testers
        String fn =  "tg_discovery.csv";
        cq = "match (m:DNA_Match{ancestor_rn:33454})-[r:match_segment]-(s:Segment) where 50>=r.cm>=7 and r.snp_ct>=500 and s.chr='01' with m,s order by s.chr,s.strt_pos,s.end_pos with s.Indx as i,s.chr as c,s.strt_pos as s,s.end_pos as e, count(*) as ct,sum(case when m.ancestor_rn=33454 then 1.0 else 0.0 end) as branch_ct with i,c,s,e,ct,branch_ct, e-s as diff return i as Indx order by c,s,e";
        neo4j_qry.qry_to_pipe_delimited(cq,fn);
        
        String c = gen.neo4jlib.file_lib.readFileByLine(neo4j_info.Import_Dir +  fn);
        String[] cc = c.split("\n");
       try{
            FileWriter fwp = new FileWriter(neo4j_info.Import_Dir + "seg_seq.csv");
            fwp.write("from|to\n");
            //FileWriter fws = new FileWriter(neo4j_info.Import_Dir + "seg_scafold.csv");
            //fws.write("from|to|lvl\n");
           int sct = 0;
            for (int i=1; i<cc.length-1;i++){
            sct = sct + 1;
            fwp.write(cc[i] + "|" + cc[i+1] + "\n");
            //fws.write(sct + "|" + cc[i] + "|1\n");
            //fws.write(sct + "|" + cc[i+1] + "|1\n");
            
            }
            fwp.flush();
            fwp.close();

            
            
//            cq = "LOAD CSV WITH HEADERS FROM 'file:///seg_seq.csv' AS line FIELDTERMINATOR '|' match (s1:Segment{Indx:toString(line.from)}) match (s2:Segment{Indx:toString(line.to)}) merge (s1)-[r:seg_seq]-(s2) ";
//      neo4j_qry.qry_write(cq);
//      
//            
//            int nbrIterations = iteration(sct); 
//            for (int i=1; i < nbrIterations; i++){
//                cq = "MATCH (l:lattice{lvl:" + i + "}) with l order by l.lid  RETURN l.lid ";
//                
//                cq = "LOAD CSV WITH HEADERS FROM 'file:///seg_scafold.csv' AS line FIELDTERMINATOR '|' match (s1:Segment{Indx:toString(line.to)}) merge (l:lattice{lid:toInteger(line.from),lvl:toInteger(line.lvl)}) merge (s1)-[r:seg_lattice]-(l)";
//                neo4j_qry.qry_write(cq);
//            fws.flush();
//            fws.close();
 
            }
       catch (Exception e) {}
       
        System.out.println(c);
  
    }   
    
        public static int iteration(int n) {
            int m = 0;
            while (n > Math.pow(m,2) ) {
                m = m + 1;
        }
            return m;
        }
        
        
        public static void findGaps() {
            
            
        }
    
        public static void main(String args[]) {
          //System.out.println(iteration(12)) ;  
            neo4j_info.neo4j_var();
//          initialPhase(Long.valueOf(33454));
            findGaps();    }

}
