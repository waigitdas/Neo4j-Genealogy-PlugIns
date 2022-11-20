/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.regex.Pattern;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 *
 * @author david
 */
public class create_seg_lattice {
   @UserFunction
    @Description("In development: Will create a de Bruign tree for segments with seg_seq edge. Used in de novo discovery of triangulation groups.")

    public String seq_lattice (
//        @Name("rn1") 
//            Long rn1,
//        @Name("rn2") 
//            Long rn2
           
  )  
            
    {
    String s =make_lattice();  
    return s;
    }
    
    public static void main(String args[]) {
       make_lattice();
       
    }
    
    public static String make_lattice() {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String[] c = gen.neo4jlib.neo4j_qry.qry_to_csv("match p= (m:DNA_Match{ancestor_rn:41}) -[r:match_segment{m_anc_rn:41, p_anc_rn:41}]-(s:Segment) where s.chr='01' and 100>=r.cm>=7 and r.snp_ct>=500 return s.Indx as Indx,s.chr as chr,  s.strt_pos as strt_pos,s.end_pos as end_pos,r.cm as cm order by s.strt_pos,s.end_pos").split("\n");
        //String[] c = gen.neo4jlib.file_lib.readFileByLine(gen.neo4jlib.neo4j_info.Import_Dir + "seg_seq.csv").split("\n");
        
        String s = "lvl|db|chr|s|e|smin|emax\n";
        int lvl=1;
        int icurr = 0;
        String chr="";
        for (int i=1 ;i<c.length-1;i++) {
            String[] ss1 = c[i].split(Pattern.quote(","));
            String[] ss2 = c[i+1].split(Pattern.quote(","));
//            String pos1[] = ss1[i].split(Pattern.quote(":"));
//            String pos2[] = ss2[i].split(Pattern.quote(":"));
            int strt = Math.min(Integer.parseInt(ss1[2]),Integer.parseInt(ss2[2]));
            int end = Math.max(Integer.parseInt(ss1[3]),Integer.parseInt(ss2[3]));
            icurr = icurr +1;
            chr = ss1[1];
            s = s + String.valueOf(lvl) + "|" +  String.valueOf(i) + "|" + chr + "|" + ss1[0] + "|" + ss2[0] + "|" + strt + "|" + end + "\n";
        }
        gen.neo4jlib.file_lib.writeFile(s,gen.neo4jlib.neo4j_info.Import_Dir + "seg_lattice.csv" );
        
        gen.neo4jlib.neo4j_qry.CreateIndex("SegLattice","id");
        gen.neo4jlib.neo4j_qry.CreateIndex("SegLattice","lvl");
        gen.neo4jlib.neo4j_qry.qry_write("match ()-[r:seg_lattice]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (g:SegLattice) delete g");
        
        String lc = "LOAD CSV WITH HEADERS FROM 'file:///seg_lattice.csv' as line FIELDTERMINATOR '|' return line ";
        String  cq = "merge (g:SegLattice{lvl:toInteger(line.lvl),id:toInteger(line.db),chr:toString(line.chr),strt_pos:toInteger(line.smin),end_pos:toInteger(line.emax)})";
        gen.neo4jlib.neo4j_qry.APOCPeriodicIterateCSV(lc,cq, 1000);
        cq = "match (s1:Segment{Indx:toString(line.s)}) match (g:SegLattice{lvl:toInteger(line.lvl),id:toInteger(line.db)}) merge (s1)-[r:seg_lattice]-(g)";
        gen.neo4jlib.neo4j_qry.APOCPeriodicIterateCSV(lc,cq, 1000);
        cq = "match (s2:Segment{Indx:toString(line.e)}) match (g:SegLattice{lvl:toInteger(line.lvl),id:toInteger(line.db)}) merge (s2)-[r:seg_lattice]-(g)";
        gen.neo4jlib.neo4j_qry.APOCPeriodicIterateCSV(lc,cq, 1000);
        Long lastLvlCt=Long.valueOf(0);
        int jct = 0;
        Boolean bStop=false;
        for (int j=0;j<500;j++){
            jct = jct +1;
            int iprev = icurr;
            int lvl_prev=lvl;
            lvl =lvl + 1;
            String cqq= "match (SegLattice{lvl:" + j + "}) return count(*) as ct";

        List<Long> llct = gen.neo4jlib.neo4j_qry.qry_long_list(cqq);
         lastLvlCt = llct.get(0);
        if (bStop != true) {
            try {
            cq = "match (g:SegLattice{lvl:" + j + " }) return g.id as id,g.strt_pos as s,g.end_pos as e order by g.id ";
            c = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n");

            String fpath = gen.neo4jlib.neo4j_info.Import_Dir + "seg_lattice_" + lvl + ".csv";
            File fn = new File(fpath);
            FileWriter fw = new FileWriter(fn);
            fw.write("lvl|idprev1|idprev2|idcurr|chr|sp|ep|sc|ec|smin|emax\n");

            for (int i=0;i< c.length-1 ;i++) {
                String[] s1 = c[i].split(Pattern.quote(","));
                String[] s2 = c[i +1].split(Pattern.quote(","));
                icurr = icurr + 1;
                int strt = Math.min(Integer.parseInt(s1[1]),Integer.parseInt(s2[1]));
                int end = Math.max(Integer.parseInt(s1[2]),Integer.parseInt(s2[2]));
                fw.write(lvl + "|" + s1[0] + "|" + s2[0] + "|" + icurr + "|" + chr + "|" + s1[1] + "|" + s1[2] + "|" + s2[1] + "|" + s2[2] + "|" + strt + "|" + end + "\n"); 
            }
            
            fw.flush();
            fw.close();
            }
            catch (Exception e) {
                 return "error \n" + e.getMessage() + "\n\n" + cq  + "\n\n" + icurr + "\n" ;
            }
            
            //gen.neo4jlib.file_lib.writeFile(s,gen.neo4jlib.neo4j_info.Import_Dir + "seg_lattice_" + lvl + ".csv" );
            lc = "LOAD CSV WITH HEADERS FROM 'file:///seg_lattice_" + lvl + ".csv' as line FIELDTERMINATOR '|' return line ";
            cq = "merge (g:SegLattice{lvl:toInteger(line.lvl),id:toInteger(line.idcurr),chr:toString(line.chr),strt_pos:toInteger(line.smin),end_pos:toInteger(line.emax)})";
            gen.neo4jlib.neo4j_qry.APOCPeriodicIterateCSV(lc,cq, 1000);
            cq = "match (gp:SegLattice{id:toInteger(line.idprev1)}) match (gc:SegLattice{id:toInteger(line.idcurr)})  merge (gp)-[r:seg_lattice]-(gc)";
            gen.neo4jlib.neo4j_qry.APOCPeriodicIterateCSV(lc,cq, 1000);
            cq = "match (gp:SegLattice{id:toInteger(line.idprev2)}) match (gc:SegLattice{id:toInteger(line.idcurr)})  merge (gp)-[r:seg_lattice]-(gc)";
            gen.neo4jlib.neo4j_qry.APOCPeriodicIterateCSV(lc,cq, 1000);
 
           
           
            
        }
         if (lastLvlCt == 1) { break;}
        }
        return "completed with " + String.valueOf(jct) + " iterations." ;  // "completed";
        //System.out.println(c);
    }

}
