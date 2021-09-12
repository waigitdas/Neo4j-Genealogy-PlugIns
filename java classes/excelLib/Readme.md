<h1>Excel Library for Genealogy User Defined Functions</h1>

This library is used to read and write Excel files. It uses java the jxl plugin which is included in the Maven dependencies. The file queries_to_excel.java accepts a Neo4j Cypher query and processes the results, outputing them into a Excel file whose name is specified in the UDF. The output file is located in the Neo4j Import Directory and is opened when it is ready. While this could be run directly from the Neo4j browser, it is perhaps best incorporated into other UDFs whose output is in Excel.
