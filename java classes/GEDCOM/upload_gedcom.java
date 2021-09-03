package gen.GEDCOM;
    import gen.neo4jlib.neo4j_qry;
    import java.io.IOException;
    import java.nio.charset.StandardCharsets;
    import java.nio.file.Files;
    import java.nio.file.Paths;
    import java.util.stream.Stream;
    //import org.neo4j.graphdb.Node;
    import java.io.File;
    import java.io.FileWriter;
    import java.io.IOException;    
    import org.neo4j.procedure.Description;
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;
    // import org.neo4j.procedure.Procedure;
  
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
        @Name("user_name") 
            String user_name,
        @Name("password") 
            String password,
        @Name("file_path") 
             String file_path,
        @Name("FAM_Str_Id") 
            String FAM_Str_Id
       )
      {
        double tm = System.currentTimeMillis();
          load_gedcom(file_path, db, user_name, password,FAM_Str_Id);
          double tmelapsed= System.currentTimeMillis() - tm;
          return "completed in " + tmelapsed + " msec.";
      }
  
    
    public static void load_gedcom (String filePath,String db, String user_name,String password,String FAM_Str_Id ) 
   
    {
        //String FAM_Str_Id ="F";
        //String filePath = "E:\\DAS_Coded_BU_2017\\Genealogy\\Gedcom\\recd\\erwin2.ged";
        String c =  readLineByLineJava8( filePath );
        String[] s = c.split("0 @");
        
        String ImportDir ="C:\\Users\\david\\AppData\\Local\\Neo4j\\Relate\\Data\\dbmss\\dbms-02d28c77-eb91-45e9-ba16-b3d70dff733e\\import\\";
        String fnmp =ImportDir + "person.csv";
        File fnp = new File(fnmp);
        String fnmu =ImportDir + "union.csv";
        File fnu = new File(fnmu);
        try{
            FileWriter fwp = new FileWriter(fnp);
            FileWriter fwu = new FileWriter(fnu);
            fwp.write("rn|fullname|first_name|last_name|sex|bd|bp|dd|dp|uid|nmar|\n");
            fwu.write("uid|u1|u2|ud|up|\n");
            for (String i : s){
                if (i.substring(0, 1).equals("I")) {
                    //create person node
                   //System.out.println(i);
                    try {
                        fwp.write(person_node_from_gedmatch_I(i) + "\n");
                    }
                    catch (IOException e) {
                        fwp.write("Error");
                           
                                   }
                    }
        
           else {
                    if (i.substring(0, 1).equals(FAM_Str_Id)) {
                
                   //create union node
                   //System.out.println(i);
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
            
            String cqp = "LOAD CSV WITH HEADERS FROM 'file:///person.csv' AS line FIELDTERMINATOR '|' merge (p:Person{RN:toInteger(line.rn),fullname:toString(line.fullname),first_name:toString(case when line.first_name is null then '' else line.first_name end),surname:toString(case when line.surname is null then '' else line.surname end),BDGed:toString(line.bd),BP:toString(line.bp),DD:toString(line.dd),DP:toString(line.dp),nm:toInteger(case when line.nm is null then -1 else line.nm end),union_id:toInteger(case when line.uid is null then 0 else line.uid end),sex:toString(line.sex)})";
           neo4j_qry.qry_write(cqp,"test","w");


            String cqu = "LOAD CSV WITH HEADERS FROM 'file:///union.csv' AS line FIELDTERMINATOR '|' merge (u:Union{Union_id:toInteger(line.uid),U1:toInteger(line.u1),U2:toInteger(line.u2),UDGed:toString(line.ud),Union_Place:toString(line.up)})";
           neo4j_qry.qry_write(cqu,"test","w");

           String cqpl = "match (p1:Person) with 'b' as type, p1.BP as Place return Place union match (p2:Person) with 'd' as type,p2.DP as Place return Place union match (u:Union) with 'u' as type,u.UDP as Place return Place";
        
        }
        catch (IOException e){
             //System.out.println( e);
             }
         }

    
    //Read file content into the string with - Files.lines(Path path, Charset cs)
 
    private static String readLineByLineJava8(String filePath) 
    {
        StringBuilder contentBuilder = new StringBuilder();
 
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) 
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e) 
        {
            //e.printStackTrace();
        }
 
        return contentBuilder.toString();
    }
    
    private static String person_node_from_gedmatch_I(String ged) {
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
        String uid =getUID(ged);
        String nmar = getNumberMarriages(ged);
        
        sout=sout + first + " " + surname + "|" + first + "|" + surname + "|" + sex + "|" + bd + "|" + bp  + "|" + dd + "|" + dp + "|" + uid + "|" + nmar + "|";
        return sout;
        }
    
   private static String union_node_from_gedmatch_I(String ged,String FAM_Str_Id) {
        String[] s = ged.split("\n");
        String uid = s[0].split("(?=\\D)")[0].replace(FAM_Str_Id,""); 
        String sout = "";
        
        String[] u1h = ged.split("HUSB")[1].split("\n");
        String u1 = u1h[0].strip().replace("@","").substring(1);
        String[] u1m = ged.split("WIFE")[1].split("\n");
        String u2 = u1m[0].strip().replace("@","").substring(1);
       //String mnm = name.split("/")[1].replace("/","").strip();
        //String fnm =   name.split("/")[0].replace("/","").strip();      
    
        
        //String sex = ged.split("SEX")[1].strip().substring(0,1);
        
        String ud = EventDate(ged,"MARR");
        String up = EventPlace(ged,"MARR");

        
        sout=sout + uid + "|" +u1 + "|" + u2 + "|" + ud + "|" + up + "|";  // + "MNN" + "|" + "FNM" ;
        return sout;
        }

   private static String EventDate(String ged,String event_type) {
            //if (event_date == null){return "";}
            String s = " ";

                String[] d = ged.split(event_type);
                if (d.length ==2) {
                    String dd[] = d[1].split("DATE");
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
  
        private static String EventPlace(String ged,String event_type) {
            //if (event_date == null){return "";}
            String s = " ";

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
  
        private static String getUID(String ged){
            String s = "";
            String[] ss = ged.split("FAMC");
            if (ss.length==2) {
                return ss[1].replace("@","").strip().substring(1);
                
            }            else {return s;}
            
        }
        
        private static String getNumberMarriages(String ged) {
            String s = "0";
            String[] ss = ged.split("FAMS");
            if (ss.length >1) {
                return Integer.toString(ss.length - 1);
            }
            else {return s;}
        }
}