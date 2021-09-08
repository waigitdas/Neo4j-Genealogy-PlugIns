<h1>Neo4j Genealogy PlugIn</h1>

This introduction has two maor audiences: alpha testers and developers. The background is avaialble at <a href="http://gfg.md/blogpost/7" target="new">http://gfg.md/blogpost/7</a>. 

<h3>Setting Up the Graph Environment</h3>

Several steps are required to implement the genealogy user defined function (Gen-UDF).<br><br>

<ol>
  <li><b>Step 1</b>. Install Neo4j Enterprise Edition, version 4.x. 
    <ol>
      <li>The process starts <a href="https://neo4j.com/download-neo4j-now/" target="new">here</a>. 
      <li>When you first open the database you start with the user name neo4j and passwords neo4j. You will then create your own password. Leave the username as neo4j. 
       <li>Create a project with a name of your chosing.
       <li>Create your first database by opening the Neo4j browser, selecting the system database and typing this command: create database <your database name>. The name must be only lower letters. You can create other databases by repeating this command. To delete a database type drop database <name>.
    </ol>

<li><b>Step 2</b>. Install the Genealogy User Defined Function.
  <ol>
         <li>Download the Gen-UDF jar file from this repository. You will find the lastest version <a href="https://github.com/waigitdas/Neo4j-Genealogy-PlugIns/tree/main/jar" target="new">here</a>. 
  <li>The Neo4j application opens to a "home page" from which you can open the PlugIn folder. The link is in the "...." icon to the right of your project "Open" button. The navigation is "...." > Open Folder > Plugins. Copy the Gen_UDF jar file into that folder and then restart the database.
  <li>Verify the installation by opening the Neo4j browser and entering this command which will show you a list of functions available: <blockquote>
Show Functions yield name, signature, description,returnDescription,aggregating
where name STARTS WITH 'gen'
    return name, signature, description,returnDescription,aggregating</blockquote>
  </ol>

<li><b>Step 3</b>. Upload your GEDCOM file.
  <ol>
         <li>Create a GEDCOM v 5.5 file from your genealogy software and identify the folder where it is found. 
  <li>Run the function gedcom_to_neo4j. This function uses three facts you must enter: the name of the database, the full path to the GEDCOM file and the letter (usually F, but not always) indentifying the family number (@<b>F</b>####@) in the GEDCOM. The GEDCOM file path currently needs to us "/" separators and not "\"; you may need to edit a copied path. The entry in Neo4j browser should look like this:<blockquote>
    return gen.gedcom.gedcom_to_neo4j("mydatabase","E:/folder1/Genealogy/Gedcom/test files/my ged file.ged","F")
    </blockquote>
  <li>Run the command to upload the GEDCOM to Neo4j. Generally this takes less than a minute, but this will vary depending on your computer setup
  <li>Verify the upload by counting the number of nodes and edges using the commands: <blockquote>match (n) return labels(n), count(*) order by labels(n)</blockquote><br>
    <blockquote>match (n)-[r]-() return type(r), count(*) order by type(r)
</blockquote>
  </ol>

         
 </ol>
             
Neo4j supports plugins for user defined functions and procedures. This project is developing a UDF supporting genealogy graphs. The development is done in Java 11 using Maven. The pom.xml contained the required dependencies and build parameters. The class (*.java) files will be added as they are developed. In setting up Maven, use the pom.xml and add the desired *.java files; then compile to produce your *.jar file. The default name of the jar is gen-##, where ## is the version number. 

<br><br>
