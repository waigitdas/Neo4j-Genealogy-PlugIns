/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.excelLib;

    import static gen.neo4jlib.neo4j_qry.qry_to_csv;
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
        String e =qry_to_excel(cq,"tg_report","Item 1",1, "3:66;4:15", "2:###.#;3:###,###,###", "", false);
        qry_to_excel(cq,"tg_report","Item 25",2,"","0:##,###",e, true);
    }
    
//public static newWorkbook()    
    
public static String qry_to_excel(String cq,String FileNm,String SheetName, int SheetNumber, String ColWidths, String colNumberFormat, String ExcelFile, Boolean OpenFile ) {

    gen.neo4jlib.neo4j_info.neo4j_var();  // initialize user variable
    String csvFile = gen.neo4jlib.neo4j_info.Import_Dir + FileNm + ".csv";  // intermediate file to be saved
    String excelFile = "";
    String excelFileNm = "" ;
    File file;
    WritableWorkbook w;
    
    //create csv from results
     gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited(cq,FileNm + ".csv");  //uses apoc and save defaults to import dir
     //get and parse lines of csv
     String c = gen.neo4jlib.file_lib.readFileByLine(csvFile);

    //set up excel. Create new or open prior if there are to be multiple worksheets
     try{
    if (ExcelFile=="") {
    excelFile = gen.neo4jlib.neo4j_info.Import_Dir + FileNm + ".xls";;
    excelFileNm=excelFile;
    file = new File(excelFileNm);
    WorkbookSettings wbSettings = new WorkbookSettings();
    wbSettings.setLocale(new Locale("en", "EN"));
    w = Workbook.createWorkbook(file, wbSettings);
      
    }
    else {
        excelFile=ExcelFile;
        excelFileNm=excelFile;
        file = new File(excelFile);
       Workbook existingWorkbook = Workbook.getWorkbook(new File(file.getAbsolutePath()));
        w = Workbook.createWorkbook(new File(excelFile), existingWorkbook);
 
     }
   
     WritableSheet excelSheet = w.createSheet(SheetName, SheetNumber);
    createLabel(excelSheet);
    excelSheet.getSettings().setVerticalFreeze(1);

     //**************************************** 

 
//iterate through csv lines to create excel worksheets within the active workbook
    String[] rws = c.split("\n");
    int rows = rws.length;
    int colct = rws[0].split(Pattern.quote("|")).length;

    //************************************************************************
    //instantiate the formating from the function call variables
    //initialize column numeric formats
       String[][] colformat = new String[colct][2];  //col - numeric format
    String[][] colwidth = new String[colct][2];  //col - width
    
    //column number formats
       for (int i=0;i<colformat.length ;i++){
           colformat[i][0]=String.valueOf(i);
           colformat[i][1]="";
       }
  
       if (colNumberFormat.strip() != "") {
       
        String[] cwf = colNumberFormat.split(Pattern.quote(";"));
        for (int i=0; i<cwf.length; i++) {
            String[] cc = cwf[i].split(Pattern.quote(":"));
            colformat[Integer.parseInt(cc[0])][0] = cc[0];
            colformat[Integer.parseInt(cc[0])][1] = cc[1];
            
       }
    }

        //initialize column numeric formats
       for (int i=0;i<colwidth.length ;i++){
           colwidth[i][0]=String.valueOf(i);
           colwidth[i][1]="";
       }
 
       if (ColWidths.strip() != "") {
       
        String[] cww = ColWidths.split(Pattern.quote(";"));
        for (int i=0; i<cww.length; i++) {
            String[] cc = cww[i].split(Pattern.quote(":"));
            colwidth[Integer.parseInt(cc[0])][0] = cc[0];
            colwidth[Integer.parseInt(cc[0])][1] = cc[1];
       }
    }
    //************************************************************************

    //iterate rows and each column in the row and create either text or number entry. Number datatype derived from function call variable 
    for (int rw=0 ; rw < rws.length; rw++ ) {
        String cols[] = rws[rw].split(Pattern.quote("|"));
        for (int col=0; col < cols.length; col++) {
            if (colformat[col][1] != "") {
                if (rw==0) {addLabel(excelSheet, col, rw , fixCellStr(cols[col]));}
                else {addNumber(excelSheet, col, rw , Double.parseDouble(fixCellStr(cols[col])),colformat[col][1]);}
            }
            else {
                addLabel(excelSheet, col, rw , fixCellStr(cols[col]));}

        }  // next col
    }  // next rw

    
      //column widths as specified, otherwise auto
            for (int col=0; col < colct; col++) {
                if (colwidth[col][1] != "" ) {
                    WritableCellFormat cellFormat = new WritableCellFormat();        
                    CellView cellView = new CellView();
                    cellView.setSize(256 * Integer.parseInt(colwidth[col][1]));
                    excelSheet.setColumnView(Integer.parseInt(colwidth[col][0]), cellView);
                }
                else {
                 CellView cell=excelSheet.getColumnView(col);
                cell.setAutosize(true);
                excelSheet.setColumnView(col, cell);}
            }
            
    //label excel tab, include number of rows
    int rr = rows-1;
    excelSheet.setName(excelSheet.getName() + "-" + rr);
 

    //wrap up and open file
    w.write();
    w.close();
    Desktop.getDesktop().open(new File(excelFileNm));
    }
    catch (Exception e) {}
     
    return excelFile;
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
        double nbr,String ColFormat) throws WriteException, RowsExceededException {
        try{
        NumberFormat cf = new NumberFormat(ColFormat); 
        WritableCellFormat numberFormat = new WritableCellFormat(cf);
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
        label = new Label(column, row, s, times);
        sheet.addCell(label);
    }

    public static String fixCellStr(String s) {
        return s.replace("\"","").replace("[","").replace("]","");
    }
}
