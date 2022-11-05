/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.excelLib;
import static gen.excelLib.queries_to_excel.addLabel;
import static gen.excelLib.queries_to_excel.createLabel;
import static gen.excelLib.queries_to_excel.fixCellStr;
import static gen.excelLib.queries_to_excel.times;
import static gen.excelLib.queries_to_excel.timesBoldUnderline;
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


public class excel_from_csv {

   
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
    public static String load_csv(String csvFile,String FileNm,String SheetName, int SheetNumber, String ColWidths, String colNumberFormat, String ExistingExcelFile,Boolean OpenFile,String message,Boolean include_common_ancestor  ){
 //System.out.println("#!");
 String excelFile = "";
    String excelFileNm = "" ;
    File file;
    String anc_name="";
    WritableWorkbook w;
        if (include_common_ancestor==true){
        gen.rel.anc_rn anc = new gen.rel.anc_rn();
        anc_name = gen.gedcom.get_family_tree_data.getPersonFromRN(anc.get_ancestor_rn(),true);
    }
 System.out.println("#2"); 
     //set up excel. Create new or open prior if there are to be multiple worksheets
     try{
    if (ExistingExcelFile=="") {
        System.out.println("#3");
    excelFile = gen.neo4jlib.neo4j_info.Import_Dir + FileNm + "_" + gen.genlib.current_date_time.getDateTime() + ".xls";;
    excelFileNm=excelFile;
    file = new File(excelFileNm);
    WorkbookSettings wbSettings = new WorkbookSettings();
    wbSettings.setLocale(new Locale("en", "EN"));
    w = Workbook.createWorkbook(file, wbSettings);
    }
    else {
        excelFile=ExistingExcelFile;
        excelFileNm=excelFile;
        file = new File(excelFile);
       Workbook existingWorkbook = Workbook.getWorkbook(new File(file.getAbsolutePath()));
        w = Workbook.createWorkbook(new File(excelFile), existingWorkbook);
     }
   
System.out.println("#4");
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
         System.out.println(e.getMessage());
     }
     System.out.println(c);
//iterate through csv lines to create excel worksheets within the active workbook
    String[] rws = c.split("\n");
    int rows = rws.length;
    int colct = rws[0].split(Pattern.quote(",")).length;
  
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
//System.out.println("#!");
       if (ColWidths.strip() != "") {
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
        String cols[] = rws[rw].split(Pattern.quote(","));
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

    String[] msg = message.split("\n");
    for (int m=0;m < msg.length; m++){
        addLabel(excelSheet, 0, extra_rw_ct , fixCellStr(msg[m]));
        extra_rw_ct = extra_rw_ct + 1;
    }
         addLabel(excelSheet, 0, extra_rw_ct , fixCellStr("database: " + gen.neo4jlib.neo4j_info.user_database));
 
  
    //wrap up and open file
    w.write();
    w.close();
    Desktop.getDesktop().open(new File(excelFileNm));
    }
    catch (Exception e) {
        System.out.println(e.getMessage());
        return "Error in queries_to_excel\n\n" +  e.getMessage(); 
    }
     
    return "Completed";  // excelFile;
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
        sheet.addCell(label);
    }

    public static String fixCellStr(String s) {
        return s.replace("\"","").replace("[","").replace("]","").replace('⦋','[').replace('⦌',']');
    }
    
    public static void autoSizeColumns(WritableSheet sheet, int columns) {
        for (int c = 0; c < columns; c++) {
         CellView cell = sheet.getColumnView(c);
         cell.setAutosize(true);
         sheet.setColumnView(c, cell);
        }
}
 }
