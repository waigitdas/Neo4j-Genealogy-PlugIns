/**
 * Copyright 2021
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.gedcom;
    import gen.conn.connTest;
    import gen.neo4jlib.neo4j_qry;
    import gen.neo4jlib.file_lib;
    import gen.neo4jlib.neo4j_info;
import java.io.BufferedWriter;

    import java.io.File;
import java.io.FileOutputStream;
    import java.io.FileWriter;
    import java.io.IOException;    
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
    import org.neo4j.procedure.Description;
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;
   
public  class upload_gedcom {
        @UserFunction
        @Description("Load a GEDCOM into Neo4j creating Person, Union and Place nodes and the edges connecting them.")
                    
    
    public String gedcom_to_neo4j(
        @Name("FAM_Str_Id") 
            String FAM_Str_Id
       )
      {
        //double tm = System.currentTimeMillis();
        load_gedcom(FAM_Str_Id);
        //double tmelapsed= System.currentTimeMillis() - tm;
        return "Completed";
      }
  public  void main(String args[]){
    load_gedcom("F");
}

    
    public  void load_gedcom (String FAM_Str_Id ) 
   
    {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();  //initialize variables
        gen.conn.connTest.cstatus();
      String filePath =  gen.neo4jlib.neo4j_info.gedcom_file;
        //create indices to speed upload using merge
        neo4j_qry.CreateIndex("Person", "RN");
        neo4j_qry.CreateIndex("Person", "fullname");
        neo4j_qry.CreateIndex("Person", "uid");
        neo4j_qry.CreateIndex("Person", "BP");
        neo4j_qry.CreateIndex("Person", "DP");
        neo4j_qry.CreateIndex("Union", "uid");
        neo4j_qry.CreateIndex("Union", "U1");
        neo4j_qry.CreateIndex("Union", "U2");
        neo4j_qry.CreateIndex("Union", "UP");
        neo4j_qry.CreateIndex("Place", "desc");
        neo4j_qry.CreateRelationshipIndex("person_place","type");
        //neo4j_qry.qry_write("create text index for (p:Person) on (p.fullname)");
        
        String c =  file_lib.ReadFileByLineWithEncoding(filePath );
        
        String[] s = c.replace("|","^").split("0 @");
        
        neo4j_info.neo4j_var();
        String fnmp =neo4j_info.Import_Dir + "person.csv";
        File fnp = new File(fnmp);
        String fnmu =neo4j_info.Import_Dir + "union.csv";
        File fnu = new File(fnmu);
        try{
//            Writer fwp = null;
//            Writer fwu = null;
//           BufferedWriter fpout = null;
//           BufferedWriter fuout = null;

            Writer fwp = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fnp), StandardCharsets.ISO_8859_1));
            Writer fwu = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fnu), StandardCharsets.ISO_8859_1));

            // FileWriter fwp = new FileWriter(fnp);
            //FileWriter fwu = new FileWriter(fnu);
            //fwp.write("rn|fullname|first_name|surname|sex|bd|bp|dd|dp|uid|nmar|\n");
            //fwu.write("uid|u1|u2|ud|up|\n");
            fwp.write("rn|fullname|first_name|surname|sex|bd|bp|dd|dp|uid|nmar|\n"); 
            fwu.write("uid|u1|u2|ud|up|\n");
            String p="";
            String u="";
            
            for (String i : s){
                if (i.substring(0, 1).equals("I")) {
                  //create person node
                    try {
                        //System.out.println(person_node_from_gedmatch_I(i));
                        //fwp.write(person_node_from_gedmatch_I(i, FAM_Str_Id) + "\n");
                        p = person_node_from_gedmatch_I(i, FAM_Str_Id) + "\n";
                        fwp.write(p);
                    }
                    catch (IOException e) {
                        //fwp.write("Error");
                        fwp.write("Error" + e.getMessage());
                        fwp.flush();
                        fwp.close();
                                   }
                    }
        
           else {
                    if (i.substring(0, 1).equals(FAM_Str_Id)) {
                
                   //create union node
                   try {
                      //fwu.write(union_node_from_gedmatch_I(i,FAM_Str_Id) + "\n");
                      u = union_node_from_gedmatch_I(i,FAM_Str_Id) + "\n";
                      fwu.write(u);

                    } 
                    catch (IOException e) {
                        fwu.write("Error");
                        fwu.flush();
                        fwu.close();
                     } 
                    }
               } 

           }  
            //fwp.write("Finished");
            fwp.flush();
            fwp.close();
            fwu.flush();
            fwu.close();
      
            //Load Persons
            String cqp = "LOAD CSV WITH HEADERS FROM 'file:///person.csv' AS line FIELDTERMINATOR '|' merge (p:Person{RN:toInteger(line.rn),fullname:toString(line.fullname),first_name:toString(case when line.first_name is null then '' else line.first_name end),surname:toString(case when line.surname is null then '' else line.surname end),BDGed:toString(line.bd),BP:toString(line.bp),DDGed:toString(line.dd),DP:toString(line.dp),nm:toInteger(case when line.nm is null then -1 else line.nm end),uid:toInteger(case when line.uid is null then 0 else line.uid end),sex:toString(line.sex)})";
           neo4j_qry.qry_write(cqp);

           //Load unions/marriages
           String cqu = "LOAD CSV WITH HEADERS FROM 'file:///union.csv' AS line FIELDTERMINATOR '|' merge (u:Union{uid:toInteger(line.uid),U1:toInteger(line.u1),U2:toInteger(line.u2),UDGed:toString(line.ud),Union_Place:toString(line.up)})";
           neo4j_qry.qry_write(cqu);

           //Create place nodes using extant data
           String cqpl =  "match (p1:Person) with 'b' as type, p1.BP as Place return type,Place union match (p2:Person) with 'd' as type,p2.DP as Place return type, Place union match (u:Union) with 'u' as type,u.UDP as Place where Place is not null return type,Place" ;
           neo4j_qry.qry_to_pipe_delimited(cqpl, "places.csv");

           String cqplup = "LOAD CSV WITH HEADERS FROM 'file:///places.csv' AS line FIELDTERMINATOR '|' merge (p:Place{desc:toString(line.Place)})";
           neo4j_qry.qry_write(cqplup);
           
           //create child edges using extant data
           String pu = "match (p:Person) where p.uid>1 return p.RN as rn,p.uid as uid";
           neo4j_qry.qry_to_pipe_delimited(pu, "child.csv");
           String puup = "LOAD CSV WITH HEADERS FROM 'file:///child.csv' AS line FIELDTERMINATOR '|' match (p:Person{RN:toInteger(line.rn)}) match (u:Union{uid:toInteger(line.uid)}) merge (p)-[r:child]-(u)";
           neo4j_qry.qry_write(puup);

           //create father-union edges using extant data
           String fu = "match (p:Person)-[r:child]->(u:Union) where u.U1 >0 return p.RN as rn,u.U1 as father";
           neo4j_qry.qry_to_pipe_delimited(fu, "father.csv");
           String fuup = "LOAD CSV WITH HEADERS FROM 'file:///father.csv' AS line FIELDTERMINATOR '|' match (p:Person{RN:toInteger(line.rn)}) match (u:Union{U1:toInteger(line.father)}) merge (p)-[r:ufather]-(u)";
           neo4j_qry.qry_write(fuup);

           //create mother-union edges using extant data
           String mu = "match (p:Person)-[r:child]->(u:Union) where u.U2 >0 return p.RN as rn,u.U2 as mother";
           neo4j_qry.qry_to_pipe_delimited(mu, "mother.csv");
           String muup = "LOAD CSV WITH HEADERS FROM 'file:///mother.csv' AS line FIELDTERMINATOR '|' match (p:Person{RN:toInteger(line.rn)}) match (u:Union{U2:toInteger(line.mother)}) merge (p)-[r:umother]-(u)";
           neo4j_qry.qry_write(muup);

           //create person-father edges using extant data
           String fup = "LOAD CSV WITH HEADERS FROM 'file:///father.csv' AS line FIELDTERMINATOR '|' match (p1:Person{RN:toInteger(line.rn)}) match (p2:Person{RN:toInteger(line.father)}) merge (p1)-[r:father]-(p2)";
           neo4j_qry.qry_write(fup);

           //create person-mother edges using extant data
           String mup = "LOAD CSV WITH HEADERS FROM 'file:///mother.csv' AS line FIELDTERMINATOR '|' match (p1:Person{RN:toInteger(line.rn)}) match (p2:Person{RN:toInteger(line.mother)}) merge (p1)-[r:mother]-(p2)";
           neo4j_qry.qry_write(mup);

           //create spouse edges using extant data
           String hwup = "LOAD CSV WITH HEADERS FROM 'file:///union.csv' AS line FIELDTERMINATOR '|' match (p1:Person{RN:toInteger(line.u1)}) match (p2:Person{RN:toInteger(line.u2)}) merge (p1)-[r:spouse]-(p2)";
           neo4j_qry.qry_write(hwup);

           //create edges of Person & Union events to places
           neo4j_qry.qry_write("match (p:Person) with p Match (l:Place) where l.desc=p.BP create (p)-[r:person_place{type:'bp'}]->(l) ");
           neo4j_qry.qry_write("match (p:Person) with p Match (l:Place) where l.desc=p.DP create (p)-[r:person_place{type:'dp'}]->(l) ");
           neo4j_qry.qry_write("match (p:Union) with p Match (l:Place) where l.desc=p.Union_Place create (p)-[r:person_place{type:'up'}]->(l) ");
           
        }
        catch (IOException e){
             //System.out.println( e);
             }
        //create genealogy dates from gedcom dates using User Defined Function
        neo4j_qry.qry_write("MATCH (p:Person) with p,p.BDGed as ged,gen.genlib.ged_to_gen_date(p.BDGed) as gen   set p.BD = gen");
        neo4j_qry.qry_write("MATCH (p:Person) with p,p.DDGed as ged,gen.genlib.ged_to_gen_date(p.DDGed) as gen   set p.DD = gen");
        neo4j_qry.qry_write("MATCH (u:Union) with u,u.UDGed as ged,gen.genlib.ged_to_gen_date(u.UDGed) as gen   set u.UD = gen");
        
         }

    
    private static String person_node_from_gedmatch_I(String ged,String FAM_Str_Id) {
        //process @INDV@ tag data in GEDCOM
        String[] s = ged.split("\n");
        String rn = s[0].split("(?=\\D)")[0].replace("I",""); 
        String sout = rn + "|" ;
        
        String name = ged.split("NAME")[1].strip();
        String surname = name.split("/")[1].replace("/","").strip();
        String first =   name.split("/")[0].replace("/","").strip();      
    
        
        String sex = ged.split("SEX")[1].strip().substring(0,1);
        
        String bd = EventDate(ged,"BIRT");
        String bp = EventPlace(ged,"BIRT");
        String dd = EventDate(ged,"DEAT");
        String dp = EventPlace(ged,"DEAT");
        String uid =getUID(ged, FAM_Str_Id);
        String nmar = getNumberMarriages(ged);
        
        sout=sout + first + " " + surname + "|" + first + "|" + surname + "|" + sex + "|" + bd + "|" + bp  + "|" + dd + "|" + dp + "|" + uid + "|" + nmar + "|";
        return sout;
        }
    
   private static String union_node_from_gedmatch_I(String ged,String FAM_Str_Id) {
        //parses @FAM@ tag in GEDCOM
        String[] s = ged.split("\n");
        String uid = s[0].split("(?=\\D)")[0].replace(FAM_Str_Id,""); 
        String sout = "";
        String u1; 
        if(ged.contains("HUSB")) {
            String[] u1h = ged.split("HUSB")[1].split("\n");
            u1= u1h[0].strip().replace("@","").substring(1);
        }
        else {u1="0";}

        String u2;
        
        if(ged.contains("WIFE")) {
        String[] u1m = ged.split("WIFE")[1].split("\n");
        u2 = u1m[0].strip().replace("@","").substring(1);
        }
        else {u2="0";}
      //String mnm = name.split("/")[1].replace("/","").strip();
        //String fnm =   name.split("/")[0].replace("/","").strip();      
    
        
        //String sex = ged.split("SEX")[1].strip().substring(0,1);
        
        String ud = EventDate(ged,"MARR");
        String up = EventPlace(ged,"MARR");

        
        sout=sout + uid + "|" +u1 + "|" + u2 + "|" + ud + "|" + up + "|";  // + "MNN" + "|" + "FNM" ;
        return sout;
        }

   private static String EventDate(String ged,String event_type) {
        //parses date of event from @INDV@ or @FAM@ tags in GEDCOM    
        String s = " ";
            if(ged.contains("DATE")){
               try{
                   String[] d = ged.split(event_type);
                if (d.length ==2) {
                    String dd[] = d[1].split("DATE");
                    if (dd.length > 1) {
                    String[] ddd = dd[1].split("\n");
                        if (ddd.length >1) {
                          if (ddd[0].strip()!=""){
                            {return ddd[0].strip();}
                          }
                          else {return s;}    
                        }
                        else {return s;}                    
                    }
                    else { return s;}
                    }
                
                
                else {return s; }
           
            }
            catch(Exception e){return s;}
            
            
            }
                else {return s;}
   }
       
  
        private static String EventPlace(String ged,String event_type) {
        //parses place of event from @INDV@ or @FAM@ tags in GEDCOM    
            String s = " ";
                if (ged.contains("PLAC")){
                    String[] d = ged.split(event_type);
                    if (d.length ==2) {
                        String dd[] = d[1].split("PLAC");
                        if (dd.length > 1) {
                        String[] ddd = dd[1].split("\n");
                            if (ddd.length >1) 
                            {return ddd[0].strip();
                            }
                            else {return s;}                    
                        }
                        else { return s;}
                        }

                
                else {
                    return s;
                            }
                       
        
                }
                else{return s;}
        }
  
        private static String getUID(String ged,String FAM_Str_Id){
            //gets union id of person from GEDCOM
            String s = "0";
            if (ged.contains("FAMC")){
            String[] ss = ged.split("FAMC")[1].split("\n");
            if (ss.length>0) {
                return ss[0].replace("@","").replace(FAM_Str_Id, "").strip();
                
            }            else {return s;}
            }
            else {return s;}
        }
        
        private static String getNumberMarriages(String ged) {
            //not yet fully coded; 
            //# of marriages may be more that are in the GEDCOM file
            //you cannot assume the number is defined by extant data
            String s = "0";
            String[] ss = ged.split("FAMS");
            if (ss.length >1) {
                return Integer.toString(ss.length - 1);
            }
            else {return s;}
        }
}