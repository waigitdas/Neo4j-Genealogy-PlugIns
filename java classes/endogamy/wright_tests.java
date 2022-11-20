/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.endogamy;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;

/**
 *
 * @author david
 */
public class wright_tests {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
//        example1();
        example2();
    }
    
    public static void example1()
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        //initialize string
        //gen.neo4jlib.neo4j_info.wrt =""; //"xxx as 0\n";
        
        gen.neo4jlib.neo4j_qry.qry_write("match ()-[r]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (n) delete n");
        
        for(int i=1;i<37; i++)
        {
            gen.neo4jlib.neo4j_qry.qry_write("merge (p:Person{RN:" + i + ",fullname:'Person_" + i + "', BD:'',DD:''})");
            
        }
    //Wright II  1921
        create_parent(1,9,"father");
        create_parent(2,9,"mother");
        create_parent(10,3,"father");
        create_parent(10,4,"mother");
        create_parent(11,3,"father");
        create_parent(11,4,"mother");
        create_parent(12,5,"father");
        create_parent(12,6,"mother");
        create_parent(13,5,"father");
        create_parent(13,6,"mother");
        create_parent(14,7,"father");
        create_parent(14,8,"mother");
        create_parent(15,9,"father");
        create_parent(15,10,"mother");
        create_parent(16,9,"father");
        create_parent(16,10,"mother");
        create_parent(17,11,"father");
        create_parent(17,12,"mother");
        create_parent(18,11,"father");
        create_parent(18,12,"mother");
       create_parent(19,13,"father");
        create_parent(19,14,"mother");
       create_parent(20,13,"father");
        create_parent(20,14,"mother");
       create_parent(21,15,"father");
        create_parent(21,16,"mother");
       create_parent(22,15,"father");
        create_parent(22,15,"mother");
        create_parent(23,17,"father");
        create_parent(23,18,"mother");
        create_parent(24,17,"father");
        create_parent(24,18,"mother");
        create_parent(25,17,"father");
        create_parent(25,18,"mother");
        create_parent(26,17,"father");
        create_parent(26,18,"mother");
       create_parent(27,19,"father");
        create_parent(27,20,"mother");
       create_parent(28,19,"father");
        create_parent(28,20,"mother");
       create_parent(29,21,"father");
        create_parent(29,22,"mother");
       create_parent(30,21,"father");
        create_parent(30,22,"mother");
       create_parent(31,23,"father");
        create_parent(31,24,"mother");
       create_parent(32,23,"father");
        create_parent(32,24,"mother");
       create_parent(33,26,"father");
        create_parent(33,37,"mother");
       create_parent(34,26,"father");
        create_parent(34,37,"mother");
       create_parent(35,27,"father");
        create_parent(35,28,"mother");
       create_parent(36,27,"father");
        create_parent(36,28,"mother");
     
        int uid=1;
        create_union(uid,1,2);
        uid= uid + 1;
        create_union(uid,3,4);
        uid= uid + 1;
        create_union(uid,5,6);
        uid= uid + 1;
        create_union(uid,7,8);
        uid= uid + 1;
        create_union(uid,9,10);
        uid= uid + 1;
        create_union(uid,11,12);
        uid= uid + 1;
        create_union(uid,13,14);
        uid= uid + 1;
        create_union(uid,15,16);
        uid= uid + 1;
        create_union(uid,17,18);
        uid= uid + 1;
        create_union(uid,19,20);
        uid= uid + 1;
        create_union(uid,21,22);
        uid= uid + 1;
        create_union(uid,23,24);
        uid= uid + 1;
        create_union(uid,25,26);
        uid= uid + 1;
        create_union(uid,27,28);
    
        finish_setup();
    }


///////////////////////////////////////////////////////////
        public static void example2()
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        gen.neo4jlib.neo4j_qry.qry_write("match ()-[r]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (n) delete n");
        
//        for(int i=1;i<37; i++)
//        {
//            gen.neo4jlib.neo4j_qry.qry_write("merge (p:Person{RN:" + i + ",fullname:'Person_" + i + "', BD:'',DD:''})");
//            
//        }
    //Wright   1922
        gen.neo4jlib.neo4j_qry.qry_write("create (p1:Person{RN:1,fullname:'Lancaster Comet'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (p2:Person{RN:2,fullname:'Virtue'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (u1:Union{uid:1,U1:1,U2:2})");
 
        gen.neo4jlib.neo4j_qry.qry_write("create (p3:Person{RN:3,fullname:'Lord Reglan'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (p4:Person{RN:4,fullname:'Duchess of Gloster 6'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (u2:Union{uid:2,U1:3,U2:4})");
 
        gen.neo4jlib.neo4j_qry.qry_write("create (p5:Person{RN:5,fullname:'Maidstone'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (u3:Union{uid:3,U1:3,U2:5})");
 
        gen.neo4jlib.neo4j_qry.qry_write("create (p6:Person{RN:6,fullname:'Queens Roan'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (p7:Person{RN:7,fullname:'?'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (u4:Union{uid:4,U1:6,U2:7})");
 
        gen.neo4jlib.neo4j_qry.qry_write("create (p8:Person{RN:8,fullname:'Plantagenet'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (p9:Person{RN:9,fullname:'Verdant'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (u5:Union{uid:5,U1:8,U2:9})");
 
        gen.neo4jlib.neo4j_qry.qry_write("create (p10:Person{RN:10,fullname:'Corianda'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (u6:Union{uid:6,U1:3,U2:10})");
 
        gen.neo4jlib.neo4j_qry.qry_write("create (p11:Person{RN:11,fullname:'John Bull'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (p12:Person{RN:12,fullname:'Clipper'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (u7:Union{uid:7,U1:11,U2:12})");
 
        //////////////////////////////////////
     
        
        gen.neo4jlib.neo4j_qry.qry_write("create (p13:Person{RN:13,fullname:'Champion of England'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (p14:Person{RN:14,fullname:'Duchess of Gloster 9'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (u8:Union{uid:8,U1:13,U2:14})");
           create_parent(13,1,"father");
          create_parent(13,2,"mother");
          create_parent(14,3,"father");
          create_parent(14,4,"mother");     
        
        gen.neo4jlib.neo4j_qry.qry_write("create (p15:Person{RN:15,fullname:'Mistletoe'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (u9:Union{uid:9,U1:13,U2:15})");
           create_parent(15,3,"father");
          create_parent(15,5,"mother");
       
           create_parent(1,7,"mother");
          create_parent(1,6,"father");
          create_parent(2,8,"father");
          create_parent(2,9,"mother");
   
        gen.neo4jlib.neo4j_qry.qry_write("create (p16:Person{RN:16,fullname:'The Czar'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (p17:Person{RN:17,fullname:'Cressida'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (u10:Union{uid:10,U1:16,U2:17})");
          create_parent(16,3,"father");
          create_parent(16,10,"mother");
          create_parent(17,11,"father");
          create_parent(17,12,"mother");

        //////////////////////////////////////////////////////////////////
        
        gen.neo4jlib.neo4j_qry.qry_write("create (p18:Person{RN:18,fullname:'Grand Duke of Gloster 19900'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (p19:Person{RN:19,fullname:'Mimulus'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (u11:Union{uid:11,U1:18,U2:19})");
          create_parent(18,13,"father");
          create_parent(18,14,"mother");
          create_parent(19,13,"father");
          create_parent(19,15,"mother");
   
        gen.neo4jlib.neo4j_qry.qry_write("create (p20:Person{RN:20,fullname:'Carmine'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (u12:Union{uid:12,U1:13,U2:20})");
          create_parent(20,16,"father");
          create_parent(20,17,"mother");
       
        ///////////////////////////////////////////////////////
        
        gen.neo4jlib.neo4j_qry.qry_write("create (p21:Person{RN:21,fullname:'Royal Duke of Gloster'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (p22:Person{RN:22,fullname:'Princess Royal'})");
        gen.neo4jlib.neo4j_qry.qry_write("create (u13:Union{uid:13,U1:21,U2:22})");
           create_parent(21,18,"father");
          create_parent(21,19,"mother");
          create_parent(22,13,"father");
          create_parent(22,20,"mother");
      
        ///////////////////////////////////////////////////////////////////
        gen.neo4jlib.neo4j_qry.qry_write("create (p23:Person{RN:23,fullname:'Royal Gauntlt'})");
          create_parent(23,21,"father");
          create_parent(23,22,"mother");
 
        finish_setup();
    }

    
    public static void finish_setup()
    {
        //add uid to person nodes
        gen.neo4jlib.neo4j_qry.qry_write("match (p:Person) set p.uid=0");
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (u:Union) with u match (p:Person)-[r:father|mother]->(a:Person) where a.RN=u.U1 or a.RN =u.U2 set p.uid=u.uid");
        
        //add union_parent relationship
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (u:Union) with u,u.U1 as u1,u.U2 as u2 match (p:Person) where p.RN=u1 or p.RN =u2 with u,p match (u2:Union) where p.uid=u2.uid merge (u)-[r:union_parent]->(u2)");

        //add spouse relationship
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (u:Union) with u match (pm:Person) where pm.RN=u.U1 with pm,u match (pf:Person) where pf.RN=u.U2 merge (pm)-[r:spouse]-(pf)");

        //add Person properties
        gen.neo4jlib.neo4j_qry.qry_write("match (p:Person) set p.BD='',p.DD=''");
        gen.neo4jlib.neo4j_qry.qry_write("match (u:Union) with u match(pf:Person) where pf.RN=u.U1 set pf.sex='M'");
        gen.neo4jlib.neo4j_qry.qry_write("match (u:Union) with u match(pf:Person) where pf.RN=u.U2 set pf.sex='F'");
        
        File fnc = new File(gen.neo4jlib.neo4j_info.Import_Dir + "wright.cypher");
        FileWriter fw = null;
        try{
            fw = new FileWriter(fnc, Charset.forName("UTF8"));
            fw.write(gen.neo4jlib.neo4j_info.wrt);
            fw.flush();
            fw.close();
        }
        catch(Exception e){}
        
        
    }
    
    public static void create_parent(int rp,int rc,String p)
    {
        gen.neo4jlib.neo4j_qry.qry_write("match (p1:Person{RN:" + rp + "}) with p1 match (p2:Person{RN:" + rc + "}) merge (p1)-[r:" + p +"]->(p2)");
    }
    
    public static void create_union(int uid, int u1, int u2)
    {
                gen.neo4jlib.neo4j_qry.qry_write("merge (u:Union{uid:" + uid + ",U1:" + u1 + ", U2:" + u2 + "})" );
    }
}
