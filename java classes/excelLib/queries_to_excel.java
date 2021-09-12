/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import jxl.*;
    import jxl.format.*;
    import jxl.Range;
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
import net.bytebuddy.implementation.bind.annotation.Default;

public class queries_to_excel {
    public static WritableCellFormat timesBoldUnderline;
    public static WritableCellFormat times;
    public static String excelFile;

     /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        String cq = "MATCH p=(m:DNA_Match)-[r:match_tg]->(t:tg) where m.RN is not null with t,m,  trim(m.fullname)  as mm with t,m,mm order by mm with t,collect(mm + ';') as matches,collect(m.RN) as rns \n with t,matches   RETURN t.tgid as tg,t.chr as chr, t.strt_pos as strt_pos,t.end_pos as end_pos,t.cm as cm,size(matches) as ct,matches order by chr,strt_pos,end_pos";
        String e =qry_to_excel(cq,"test","tg_report","Item 1",1, "3:66", "2:###.#;3:###,###,###", "", false);
        qry_to_excel(cq,"test","tg_report","Item 25",2,"","0:##,###",e, true);
    }
    
//public static newWorkbook()    
    
public static String qry_to_excel(String cq,String db,String FileNm,String SheetName, int SheetNumber, String ColWidths, String colNumberFormat, String ExcelFile, Boolean OpenFile ) {

      //System.out.println(SheetNumber);
    gen.neo4jlib.neo4j_info.neo4j_var();
    String csvFile = gen.neo4jlib.neo4j_info.Import_Dir + FileNm + ".csv";
    String excelFile = "";
    String excelFileNm = "" ;
    File file;
    WritableWorkbook w;
    
    //create csv from results
     //gen.neo4jlib.neo4j_qry.qry_to_pipe_delimited(cq,db,FileNm + ".csv");  //uses apoc and save defaults to import dir
     //get and parse lines of csv
     String c = gen.neo4jlib.file_lib.readFileByLine(csvFile);
System.out.println(c);
    //set up excel
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
    //WritableSheet excelSheet = w.getSheet(0);
    createLabel(excelSheet);
    excelSheet.getSettings().setVerticalFreeze(1);

         //**************************************** 

        if (colNumberFormat !="") {
        String[] cnf = colNumberFormat.split(Pattern.quote(";"));
        for (int i=0; i<cnf.length; i++){
            //System.out.println(colNumberFormat);
            String[] cnfi = cnf[i].split(Pattern.quote(":"));
            NumberFormat nf = new NumberFormat(cnfi[1]); 
            WritableCellFormat cellFormat = new WritableCellFormat(nf);   
            CellView cellView = new CellView();
            
            //numberformat cannot be converted to cellformat
            excelSheet.setColumnView(Integer.valueOf(cnfi[0]), cellView);
 
//            Cell[] nc = excelSheet.getColumn(Integer.parseInt(cnfi[0]));
//            nc.setCellFormat(cellFormat);
//            for (int k=0; k< nc.length; k++) {
//                Number numberCell = new Number(0, 0, 25, cellFormat);
//                  nc[k].setCellFormat(cellFormat);
//            }
//           //WritableCellFormat cellFormat = new WritableCellFormat();        
//            CellView cellView = new CellView();
//            cellView.setFormat(cellFormat);
//            excelSheet.setColumnView(Integer.valueOf(cnfi[0]), cellView);
              //System.out.println(cnfi[1]);


        }
}

    //**************************************** 


//iterate through lines to create excel worksheets within a workbook
    String[] rws = c.split("\n");
    for (int rw=0 ; rw < rws.length; rw++ ) {
        String cols[] = rws[rw].split(Pattern.quote("|"));
        //System.out.println(cols[1] + "\t" + rw);
        for (int col=0; col < cols.length; col++) {
            addLabel(excelSheet, col, rw , fixCellStr(cols[col]));
        }  // next j
    }  // next i

    int rows = rws.length;
    int colct = excelSheet.getColumns();
    //System.out.println(rows);   
    //System.out.println(colct);   
    int rr = rows-1;
    excelSheet.setName(excelSheet.getName() + "-" + rr);
 
        //**************************************** 
    CellView cv = new CellView();
    cv.setAutosize(true);
    for (int i=0; i < colct; i++){
        excelSheet.setColumnView(i, cv);
        
    }
    //**************************************** 
    if (ColWidths !="") {
         String[] cwf = ColWidths.split(Pattern.quote(";"));
        for (int i=0; i<cwf.length; i++){
            String[] cwfi = cwf[i].split(Pattern.quote(":"));
            WritableCellFormat cellFormat = new WritableCellFormat();        
            CellView cellView = new CellView();
            cellView.setSize(256 * Integer.valueOf(cwfi[1]));
            excelSheet.setColumnView(Integer.valueOf(cwfi[0]), cellView);
        }
    }    
    //**************************************** 

    
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
        //times.setWrap(true);

        // create create a bold font with unterlines
        WritableFont times10ptBoldUnderline = new WritableFont(
                WritableFont.TIMES, 10, WritableFont.BOLD, false,
                UnderlineStyle.SINGLE);
//        timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
//        // Lets automatically wrap the cells
//        timesBoldUnderline.setWrap(true);

//        CellView cv = new CellView();
//        cv.setFormat(times);
//        cv.setFormat(timesBoldUnderline);
//        cv.setAutosize(true);
//
//        // Write a few headers
//        addCaption(sheet, 0, 0, "Header 1");
//        addCaption(sheet, 1, 0, "This is another header");


    }

   public static void addCaption(WritableSheet sheet, int column, int row, String s)
            throws RowsExceededException, WriteException {
        Label label;
        label = new Label(column, row, s, timesBoldUnderline);
        sheet.addCell(label);
    }

    public static void addNumber(WritableSheet sheet, int column, int row,
        Integer integer) throws WriteException, RowsExceededException {
        Number number;
        number = new Number(column, row, integer, times);
        sheet.addCell(number);
    }

    public static void addLabel(WritableSheet sheet, int column, int row, String s)
            throws WriteException, RowsExceededException {
        Label label;
        label = new Label(column, row, s, times);
        sheet.addCell(label);
    }

    public static String fixCellStr(String s) {
        return s.replace("\"","");
    }
}
