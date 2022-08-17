/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.excelLib;
    import gen.neo4jlib.neo4j_info;
    import java.awt.Desktop;
    import java.io.File;
    import java.util.Locale;
    import java.io.IOException;
    import java.util.regex.Pattern;

    import jxl.CellView;
    import jxl.Workbook;
    import jxl.WorkbookSettings;
    import jxl.SheetSettings;
    import jxl.format.PageOrientation;
    import jxl.format.UnderlineStyle;

    import jxl.Cell;
    import jxl.format.*;
    import jxl.Range;
    import jxl.read.biff.ColumnInfoRecord;
    import jxl.write.Formula;
    import jxl.write.Label;
    import jxl.write.Number;
    import jxl.write.NumberFormat;
    import jxl.write.WritableCellFormat;
    import jxl.write.WritableFont;
    import jxl.write.WritableSheet;
    import jxl.write.WritableWorkbook;
    import jxl.write.WriteException;
    import jxl.write.biff.RowsExceededException;

public class queries_to_excel {
    public static WritableCellFormat timesBoldUnderline;
    public static WritableCellFormat times;
    public static String excelFile;
    
    //main is for testing only. Must comment out database calls and use a properly formated file in the Import Neo4j folder
    public static void main(String args[]) {
        String cq = "MATCH p=(m:DNA_Match)-[r:match_tg]->(t:tg) where m.RN is not null with t,m,  trim(m.fullname)  as mm with t,m,mm order by mm with t,collect(mm + ';') as matches,collect(m.RN) as rns \n with t,matches   RETURN t.tgid as tg,t.chr as chr, t.strt_pos as strt_pos,t.end_pos as end_pos,t.cm as cm,size(matches) as ct,matches order by chr,strt_pos,end_pos";
        String e =qry_to_excel(cq,"tg_report","match_ahnentafel_" , 1, "2:13;3:13", "1:##;2:#,###,###;3:#,###,###;6:###;7:###;8:###.0;9:###.0", "",true,"",true);
        //qry_to_excel(cq,"tg_report","Item 25",2,"","0:##,###",e, true);
    }
    
//public static newWorkbook()    

  //  <!--https://www.baeldung.com/java-microsoft-excel-->

public static String qry_to_excel(String cq,String FileNm,String SheetName, int SheetNumber, String ColWidths, String colNumberFormat, String ExistingExcelFile, Boolean OpenFile,String message,Boolean include_common_ancestor ) {
    gen.neo4jlib.neo4j_info.neo4j_var_reload();  // initialize user variable
    gen.conn.connTest.cstatus();
    String anc_name="";
    if (include_common_ancestor==true){
        gen.rel.anc_rn anc = new gen.rel.anc_rn();
        anc_name = gen.gedcom.get_family_tree_data.getPersonFromRN(anc.get_ancestor_rn(),true);
    }
    
    String csvFile = gen.neo4jlib.neo4j_info.Import_Dir + FileNm + ".csv";  // intermediate file to be saved
    
     //create csv from results
     String cqq = cq.replace("[[","[").replace("]]","]");
     String q = gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited(cqq, FileNm + ".csv");  //uses apoc and save defaults to import dir
     
    String excelFile = "";
    String excelFileNm = "" ;
    File file;
    WritableWorkbook w;
    
     //set up excel. Create new or open prior if there are to be multiple worksheets
     try{
    if ("".equals(ExistingExcelFile)) {
    excelFile = gen.neo4jlib.neo4j_info.Import_Dir + FileNm + "_" + gen.genlib.current_date_time.getDateTime() + ".xls";;
    excelFileNm=excelFile;
    file = new File(excelFileNm);
    WorkbookSettings wbSettings = new WorkbookSettings();
    wbSettings.setLocale(new Locale("en", "EN"));
    wbSettings.setEncoding("UTC-8");
    w = Workbook.createWorkbook(file, wbSettings);
    }
    else {
        excelFile=ExistingExcelFile;
        excelFileNm=excelFile;
        file = new File(excelFile);
        Workbook existingWorkbook = Workbook.getWorkbook(new File(file.getAbsolutePath()));
        w = Workbook.createWorkbook(new File(excelFile), existingWorkbook);
     }
   

    WritableSheet excelSheet = w.createSheet(SheetName, SheetNumber);
    createLabel(excelSheet);
    excelSheet.getSettings().setVerticalFreeze(1);

     //**************************************** 
    //get and parse lines of csv
     String c="";
     try{
     c = gen.neo4jlib.file_lib.readFileByLine(csvFile);
  
     }
     catch (Exception e) {
     }
     
//iterate through csv lines to create excel worksheets within the active workbook
    String[] rws = c.split("\n");
    int rows = rws.length;
    int colct = rws[0].split(Pattern.quote("|")).length;
  
    //************************************************************************
    //instantiate the formating from the function call variables
    //initialize column numeric formats
    WritableCellFormat[] colformat = new WritableCellFormat[colct];  //col - numeric format
    CellView[] colwidth = new CellView[colct];  //col - width
    
    //column number formats
       for (int i=0;i<colformat.length ;i++){
           colformat[i] = null;
       }
  
       if (colNumberFormat.strip() != "") {
       
        String[] cwf = colNumberFormat.split(Pattern.quote(";"));
        for (int j=0; j<cwf.length; j++) {
            try{
            String[] cc = cwf[j].split(Pattern.quote(":"));
            NumberFormat cf = new NumberFormat(cc[1]); 
            colformat[Integer.parseInt(cc[0])] = new WritableCellFormat(cf);

             }
            catch(Exception e){}
       }
    }

       if (ColWidths.strip() != "" ) { // message != "" ) { //ColWidths.strip() != "" ||
//           if (message.strip() != "") {
//               ColWidths = "0:25;" + ColWidths;
//           }
        //initialize column width formats
       for (int i=0;i<colwidth.length ;i++){
           Boolean willSetWidth = false;
           String[] cww = ColWidths.split(Pattern.quote(";"));
            for (int j=0; j<cww.length; j++) {
             try{
                 String[] cc = cww[j].split(Pattern.quote(":"));
               if (Integer.parseInt(cc[0])==i) {  //width specified
                willSetWidth = true;
                try{
                colwidth[Integer.parseInt(cc[0])].setSize(256 * Integer.parseInt(cc[1]));
                
                }
                catch (Exception e) {}
               }
               else {
                colwidth[i]=excelSheet.getColumnView(i);
                colwidth[i].setAutosize(true);
           
               }
            }
             catch (Exception e) {}
            }
                excelSheet.setColumnView(i, colwidth[i]);
       }
       }
       else{
           
       }
    //************************************************************************
//iterate rows and each column in the row and create either text or number entry. Number datatype derived from function call variable 
    for (int rw=0 ; rw < rws.length; rw++ ) {
        String cols[] = rws[rw].split(Pattern.quote("|"));
        for (int col=0; col < cols.length; col++) {
        if (cols[col].strip()=="") {break;}  //last lines may be empty
           try {
               if (rw==0 || colformat[col]==null) {
               addLabel(excelSheet, col, rw , fixCellStr(cols[col]));
                }
                else {
                    addNumber(excelSheet, col, rw , Double.parseDouble(fixCellStr(cols[col])),colformat[col]);}
            }
          catch (Exception e){
               addLabel(excelSheet, col, rw , fixCellStr(cols[col]));}
          
        }  // next col
    }  // next rw

   
      //column widths as specified, otherwise auto
            for (int col=0; col < colct; col++) {
                try{
                excelSheet.setColumnView(col, colwidth[col]);
                }
                catch (Exception e) {}
           }
 
    //label excel tab, include number of rows
    int rr = rows-1;
    autoSizeColumns(excelSheet,colct);
    excelSheet.setName(excelSheet.getName() + "-" + rr);
        int extra_rw_ct = rws.length + 5;
       if (include_common_ancestor==true){
        String anc_message = "common ancestor is " + anc_name;
        addLabel(excelSheet, 0, extra_rw_ct , fixCellStr(anc_message));    
        extra_rw_ct = extra_rw_ct + 1;
        }
    //int cell_width = excelSheet.getColumn(0).length;
    String[] msg = message.split("\n");
    for (int m=0;m < msg.length; m++){
        addLabel(excelSheet, 0, extra_rw_ct , fixCellStr(msg[m]));
        extra_rw_ct = extra_rw_ct + 1;
    }
         addLabel(excelSheet, 0, extra_rw_ct , fixCellStr("\n\ndatabase: " + gen.neo4jlib.neo4j_info.user_database));
 
    //wrap up and open file
    w.write();
    w.close();
   
    if (OpenFile.equals(true)){
    
    Desktop.getDesktop().open(new File(excelFileNm));
    }
    }
    catch (Exception e) {return "Error in queries_to_excel\n\n" + gen.neo4jlib.file_lib.currExcelFile + "\n\n" +  e.getMessage(); }
       
    return excelFile;  // excelFile;
 }
     
public static void createLabel(WritableSheet sheet)
            throws WriteException {
        WritableFont times10pt = new WritableFont(WritableFont.createFont("Calibri"), 10);
         times = new WritableCellFormat(times10pt);
   }

   public static void addCaption(WritableSheet sheet, int column, int row, String s)
            throws RowsExceededException, WriteException {
        Label label;
        label = new Label(column, row, s, timesBoldUnderline);
        sheet.addCell(label);
    }

    public static void addNumber(WritableSheet sheet, int column, int row,
        double nbr,WritableCellFormat ColFormat) throws WriteException, RowsExceededException {
        try{
        //NumberFormat cf = new NumberFormat(ColFormat); 
        WritableCellFormat numberFormat =ColFormat; // new WritableCellFormat(cf);
        Number numberCell = new Number(column, row, nbr,numberFormat); 
        sheet.addCell(numberCell);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
       }

    public static void addLabel(WritableSheet sheet, int column, int row, String s)
            throws WriteException, RowsExceededException {
        Label label;
        label = new Label(column, row, s);
        //label.setCellFormat( "UTC_8");
        sheet.addCell(label);
    }

    public static String fixCellStr(String s) {
        return s.replace("\"","").replace("[[","^^").replace("]]","%%").replace("[","").replace("]","").replace('⦋','[').replace('⦌',']').replace("^^","[").replace("%%","]");
    }
    
    public static void autoSizeColumns(WritableSheet sheet, int columns) {
        for (int c = 0; c < columns; c++) {
         CellView cell = sheet.getColumnView(c);
         cell.setAutosize(true);
         sheet.setColumnView(c, cell);
        }
}
    
}
