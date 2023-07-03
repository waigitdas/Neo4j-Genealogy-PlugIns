/**
 * Copyright 2021-2023
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.gedcom;
    import gen.neo4jlib.neo4j_qry;
    import gen.neo4jlib.file_lib;
    import gen.neo4jlib.neo4j_info;
    import java.io.BufferedWriter;

    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.IOException;    
    import java.io.OutputStreamWriter;
    import java.io.Writer;
    import java.nio.charset.StandardCharsets;
    import org.neo4j.procedure.Description;
    import org.neo4j.procedure.UserFunction;
   
public class upload_gedcom {
        @UserFunction
        @Description("Load a GEDCOM into Neo4j creating Person, Union and Place nodes and the edges connecting them.")
    
    public String gedcom_to_neo4j(

       )
      {
        String sss = load_gedcom();
        return sss;
      }
    
  public void main(String args[]){
    load_gedcom();
}

 
  
    public static String load_gedcom() 
   
    {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();  //initialize variables
        //gen.conn.connTest.cstatus();
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
        
        //String filePath ="E:/DAS_Coded_BU_2017/Genealogy/Client Work/David Vance/Vance-Gates Family Tree (1).ged";
        String c = file_lib.ReadFileByLineWithEncoding(filePath );
        String[] s = c.replace("|","^").split("0 @");
                
        //String[] s = gen.neo4jlib.file_lib.ReadGEDCOM(filePath); 
        
        String Indiv_Str_Id = getIndvStr(s);
        String FAM_Str_Id = "F";     // getFamStr(s);
       
        
        neo4j_info.neo4j_var();
        String fnmp =neo4j_info.Import_Dir + "person.csv";
        File fnp = new File(fnmp);
        String fnmu =neo4j_info.Import_Dir + "union.csv";
        File fnu = new File(fnmu);
        try{

            Writer fwp = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fnp), StandardCharsets.ISO_8859_1));
            Writer fwu = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fnu), StandardCharsets.ISO_8859_1));

            fwp.write("rn|fullname|first_name|surname|sex|bd|bp|dd|dp|uid|nmar|\n"); 
            fwu.write("uid|u1|u2|ud|up|\n");
            //fwu.flush();
            
            String p="";
            String u="";
            
            for (String i : s){
                //fwu.write(i);
                if (i.substring(0, 1).equals(Indiv_Str_Id)) {
                  //create person node
                    try {
                        //fwp.write(person_node_from_gedmatch_I(i, FAM_Str_Id) + "\n");
                        p = person_node_from_gedmatch_I(i,Indiv_Str_Id, FAM_Str_Id) + "\n";
                        fwp.write(p);
                    }
                    catch (IOException e) {
                        //fwp.write("Error");
                        fwp.write("Error" + e.getMessage());
                        fwp.flush();
                        //fwp.close();
                                   }
                    }
        
           else {
                    if (i.substring(0, 1).equals(FAM_Str_Id)) {
                
                   //create union node
                   try {
                       //fwu.write(i);
                      //fwu.write(union_node_from_gedmatch_I(i,FAM_Str_Id) + "\n");
                      u = union_node_from_gedmatch_I(i,FAM_Str_Id) + "\n";
                      fwu.write(u);

                    } 
                    catch (IOException e) {
                        fwu.write("Error");
                        fwu.flush();
                        //fwu.close();
                     } 
                    }
               } 

           }  
            //fwp.write("Finished");
            fwp.flush();
            fwp.close();
            fwu.flush();
            fwu.close();
  
            String lc = "";
            String cq = "";
            String csv = "";

          //Load Persons
          lc = "LOAD CSV WITH HEADERS FROM 'file:///person.csv' AS line FIELDTERMINATOR '|'  return line ";
          cq = "create (p:Person{RN:toInteger(line.rn),fullname:toString(line.fullname),first_name:toString(case when line.first_name is null then '' else line.first_name end),surname:toString(case when line.surname is null then '' else line.surname end), BDGed:toString(case when line.bd is null then '' else line.bd end), BP:toString(case when line.bp is null then '' else line.bp end), DDGed:toString(case when line.dd is null then '' else line.dd end), DP:toString(case when line.dp is null then '' else line.dp end), nm:toInteger(case when line.nm is null then -1 else line.nm end), uid:toInteger(case when line.uid is null then 0 else line.uid end), sex:toString(case when line.sex is null then '' else line.sex end)})";
         neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 100000);
          
                   
        //Load unions/marriages
          lc = "LOAD CSV WITH HEADERS FROM 'file:///union.csv' AS line FIELDTERMINATOR '|'  return line ";
          cq = "create (u:Union{uid:toInteger(line.uid),U1:toInteger(line.u1),U2:toInteger(line.u2),UDGed:toString(line.ud),Union_Place:toString(line.up)})";
          neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 100000);

           //Create place nodes using extant data
            String cqpl =  "match (p1:Person) with 'b' as type, p1.BP as Place return type,Place union match (p2:Person) with 'd' as type,p2.DP as Place return type, Place union match (u:Union) with 'u' as type,u.UDP as Place where Place is not null return type,Place" ;
           neo4j_qry.qry_to_pipe_delimited(cqpl, "places.csv");

            lc = "LOAD CSV WITH HEADERS FROM 'file:///places.csv' AS line FIELDTERMINATOR '|'  return line ";
            cq = " merge (p:Place{desc:toString(line.Place)})";
            neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 100000);
          

           neo4j_qry.qry_write("match (p:Person) with p match (u:Union) where u.uid=p.uid with p,u match (f:Person) where f.RN=u.U1 with p,f,u merge (p)-[r:father]->(f)");
           
           neo4j_qry.qry_write("match (p:Person) with p match (u:Union) where u.uid=p.uid with p,u match (m:Person) where m.RN=u.U2 with p,m,u merge (p)-[r:mother]->(m)");
           
           neo4j_qry.qry_write("match (p:Person) with p match (u:Union) where u.uid=p.uid with p,u merge (p)-[r:child]->(u)");
           
           neo4j_qry.qry_write("match (u:Union) where u.uid>1 match (h:Person) where h.RN = u.U1 and u.U1>1 with h,u match (w:Person) where w.RN=u.U2 and u.U2>1 with h,w merge (h)-[r:spouse]-(w)");
           
           
           
         //create edges of Person & Union events to places
           gen.neo4jlib.neo4j_qry.qry_write("match (p:Person) with p Match (l:Place) where l.desc=p.BP create (p)-[r:person_place{type:'bp'}]->(l) ");
           gen.neo4jlib.neo4j_qry.qry_write("match (p:Person) with p Match (l:Place) where l.desc=p.DP create (p)-[r:person_place{type:'dp'}]->(l) ");
           gen.neo4jlib.neo4j_qry.qry_write("match (p:Union) with p Match (l:Place) where l.desc=p.Union_Place create (p)-[r:person_place{type:'up'}]->(l) ");
           
        }
        catch (IOException e){
             //System.out.println( e);
             }
        
        //create genealogy dates from gedcom dates using User Defined Function
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (p:Person) with p, gen.genlib.ged_to_gen_date(p.BDGed) as gen   set p.BD = gen");
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (p:Person) with p, gen.genlib.ged_to_gen_date(p.DDGed) as gen   set p.DD = gen");
        gen.neo4jlib.neo4j_qry.qry_write("MATCH (u:Union) with u,u.UDGed as ged, gen.genlib.ged_to_gen_date(u.UDGed) as gen   set u.UD = gen");
        
        return "Completed";
         }

    
    private static String person_node_from_gedmatch_I(String ged,String Indv_Str_Id,String FAM_Str_Id) {
        //process @INDV@ tag data in GEDCOM
        String[] s = ged.split("\n");
        String rn = s[0].split("(?=\\D)")[0].replace(Indv_Str_Id,""); 
        String sout = rn + "|" ;
        
        String name = "";
        String surname = "";
        String first =  "";   
        String numNames[] = ged.split("NAME");
        String nn[];
        if (numNames.length == 2){
            name = ged.split("NAME")[1].strip();;
            nn = name.split("\n")[0].split("/");
            if (nn.length == 1){  //INCOMPLETE NAME      
                name = ged.split("NAME")[1].strip();;
                surname ="_";
                first = name.split("\n")[0];
            }
            else{
                name = ged.split("NAME")[1].strip();;
                surname = name.split("/")[1].replace("/","").strip();
                first =   name.split("/")[0].replace("/","").strip();      
            }
        }
        else {   //more than one NAME tag for the person
            try{
                name = ged.split("NAME")[1].split("\n")[0];
            
            surname = name.split("/")[1].replace("/","").strip();
            first =   name.split("/")[0].replace("/","").strip();  
            int x = 0;
            }
            catch (Exception e){}
        }
     
//        String nn[] = name.split("\n")[0].split("/");
        
        String sex = "";
        try{
        sex = ged.split("SEX")[1].strip().substring(0,1);
        }
        catch (Exception e){sex="U";}
        
        
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

      public static  String getIndvStr(String[] s) {
        String indv="";
        for (String i : s){
                if (i.substring(0, 1).equals("I") || i.substring(0, 1).equals("P")) {
                    indv = i.substring(0, 1);
                    break;
                }
                }
        return indv;
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