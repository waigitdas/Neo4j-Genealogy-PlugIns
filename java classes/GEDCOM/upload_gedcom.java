/**
 * Copyright 2020 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.gedcom;
    import gen.neo4jlib.neo4j_qry;
    import gen.neo4jlib.file_lib;
    import gen.neo4jlib.neo4j_info;
//   import java.io.IOException;
//    import java.nio.charset.StandardCharsets;
//    import java.nio.file.Files;
//    import java.nio.file.Paths;
//    import java.util.stream.Stream;
    //import org.neo4j.graphdb.Node;
    import java.io.File;
    import java.io.FileWriter;
    import java.io.IOException;    
    import org.neo4j.procedure.Description;
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;
   
public class upload_gedcom {
        @UserFunction
        @Description("")
                    
    /**
     *
     * @param rn_list
     * @param generations
     * @param db
     */
    public String gedcom_to_neo4j(
        @Name("db")
            String db,
        @Name("file_path") 
             String file_path,
        @Name("FAM_Str_Id") 
            String FAM_Str_Id
       )
      {
        double tm = System.currentTimeMillis();
        load_gedcom(db, file_path, FAM_Str_Id);
        double tmelapsed= System.currentTimeMillis() - tm;
        return "completed in " + tmelapsed + " msec.";
      }
  
    
    public static void load_gedcom (String db, String filePath ,String FAM_Str_Id ) 
   
    {
        gen.neo4jlib.neo4j_info.neo4j_var();  //initialize variables
        //create indices to speed upload using merge
        neo4j_qry.CreateIndex("Person", "RN", db);
        neo4j_qry.CreateIndex("Person", "fullname", db);
        neo4j_qry.CreateIndex("Union", "union_id", db);
        neo4j_qry.CreateIndex("Union", "U1", db);
        neo4j_qry.CreateIndex("Union", "U2", db);
        neo4j_qry.CreateIndex("Place", "desc", db);
 
        String c =  file_lib.readFileByLine( filePath );
        String[] s = c.replace("|","^").split("0 @");
        
        neo4j_info.neo4j_var();
        String fnmp =neo4j_info.Import_Dir + "person.csv";
        File fnp = new File(fnmp);
        String fnmu =neo4j_info.Import_Dir + "union.csv";
        File fnu = new File(fnmu);
        try{
            FileWriter fwp = new FileWriter(fnp);
            FileWriter fwu = new FileWriter(fnu);
            fwp.write("rn|fullname|first_name|last_name|sex|bd|bp|dd|dp|uid|nmar|\n");
            fwu.write("uid|u1|u2|ud|up|\n");
            for (String i : s){
                if (i.substring(0, 1).equals("I")) {
                  //create person node
                    try {
                        //System.out.println(person_node_from_gedmatch_I(i));
                        fwp.write(person_node_from_gedmatch_I(i, FAM_Str_Id) + "\n");
                    }
                    catch (IOException e) {
                        fwp.write("Error");
     
                                   }
                    }
        
           else {
                    if (i.substring(0, 1).equals(FAM_Str_Id)) {
                
                   //create union node
                   try {
                      fwu.write(union_node_from_gedmatch_I(i,FAM_Str_Id) + "\n");
                    } 
                    catch (IOException e) {
                        fwu.write("Error");
                     } 
                    }
               } 

           }  
            fwp.flush();
            fwp.close();
            fwu.flush();
            fwu.close();
      
            //Load Persons
            String cqp = "LOAD CSV WITH HEADERS FROM 'file:///person.csv' AS line FIELDTERMINATOR '|' merge (p:Person{RN:toInteger(line.rn),fullname:toString(line.fullname),first_name:toString(case when line.first_name is null then '' else line.first_name end),surname:toString(case when line.surname is null then '' else line.surname end),BDGed:toString(line.bd),BP:toString(line.bp),DD:toString(line.dd),DP:toString(line.dp),nm:toInteger(case when line.nm is null then -1 else line.nm end),uid:toInteger(case when line.uid is null then 0 else line.uid end),sex:toString(line.sex)})";
           neo4j_qry.qry_write(cqp,db);

           //Load unions/marriages
           String cqu = "LOAD CSV WITH HEADERS FROM 'file:///union.csv' AS line FIELDTERMINATOR '|' merge (u:Union{uid:toInteger(line.uid),U1:toInteger(line.u1),U2:toInteger(line.u2),UDGed:toString(line.ud),Union_Place:toString(line.up)})";
           neo4j_qry.qry_write(cqu,db);

           //Create place nodes using extant data
           String cqpl =  "match (p1:Person) with 'b' as type, p1.BP as Place return type,Place union match (p2:Person) with 'd' as type,p2.DP as Place return type, Place union match (u:Union) with 'u' as type,u.UDP as Place where Place is not null return type,Place" ;
           neo4j_qry.qry_to_csv(cqpl, db, "places.csv");

           String cqplup = "LOAD CSV WITH HEADERS FROM 'file:///places.csv' AS line FIELDTERMINATOR '|' merge (p:Place{desc:toString(line.Place)})";
           neo4j_qry.qry_write(cqplup,db);
           
           //create child edges using extant data
           String pu = "match (p:Person) where p.uid>1 return p.RN as rn,p.uid as uid";
           neo4j_qry.qry_to_csv(pu, db, "child.csv");
           String puup = "LOAD CSV WITH HEADERS FROM 'file:///child.csv' AS line FIELDTERMINATOR '|' match (p:Person{RN:toInteger(line.rn)}) match (u:Union{uid:toInteger(line.uid)}) merge (p)-[r:child]-(u)";
           neo4j_qry.qry_write(puup,db);

           //create father-union edges using extant data
           String fu = "match (p:Person)-[r:child]->(u:Union) where u.U1 >0 return p.RN as rn,u.U1 as father";
           neo4j_qry.qry_to_csv(fu, db, "father.csv");
           String fuup = "LOAD CSV WITH HEADERS FROM 'file:///father.csv' AS line FIELDTERMINATOR '|' match (p:Person{RN:toInteger(line.rn)}) match (u:Union{U1:toInteger(line.father)}) merge (p)-[r:ufather]-(u)";
           neo4j_qry.qry_write(fuup,db);

           //create mother-union edges using extant data
           String mu = "match (p:Person)-[r:child]->(u:Union) where u.U2 >0 return p.RN as rn,u.U2 as mother";
           neo4j_qry.qry_to_csv(mu, db, "mother.csv");
           String muup = "LOAD CSV WITH HEADERS FROM 'file:///mother.csv' AS line FIELDTERMINATOR '|' match (p:Person{RN:toInteger(line.rn)}) match (u:Union{U2:toInteger(line.mother)}) merge (p)-[r:umother]-(u)";
           neo4j_qry.qry_write(muup,db);

           //create person-father edges using extant data
           String fup = "LOAD CSV WITH HEADERS FROM 'file:///father.csv' AS line FIELDTERMINATOR '|' match (p1:Person{RN:toInteger(line.rn)}) match (p2:Person{RN:toInteger(line.father)}) merge (p1)-[r:father]-(p2)";
           neo4j_qry.qry_write(fup,db);

           //create person-mother edges using extant data
           String mup = "LOAD CSV WITH HEADERS FROM 'file:///mother.csv' AS line FIELDTERMINATOR '|' match (p1:Person{RN:toInteger(line.rn)}) match (p2:Person{RN:toInteger(line.mother)}) merge (p1)-[r:mother]-(p2)";
           neo4j_qry.qry_write(mup,db);

           //create spouse edges using extant data
           String hwup = "LOAD CSV WITH HEADERS FROM 'file:///union.csv' AS line FIELDTERMINATOR '|' match (p1:Person{RN:toInteger(line.u1)}) match (p2:Person{RN:toInteger(line.u2)}) merge (p1)-[r:spouse]-(p2)";
           neo4j_qry.qry_write(hwup,db);

        }
        catch (IOException e){
             //System.out.println( e);
             }
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