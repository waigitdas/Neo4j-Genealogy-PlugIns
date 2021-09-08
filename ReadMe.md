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
       <li>Verify that you can start and stop the project database. This is important because the next step may damage the functionality. Thus, be careful with this step
       <li>Modify the neo4j configuration file to accomodate the project requirements. 
         <ol>
           <li>Open the configuration folder. The Neo4j application opens to a "home page" from which you can open the Configuration folder. The link is in the "...." icon to the right of your project "Open" button. The navigation is "...." > Open Folder > Configuration.
           <li>Open the file neo4j.conf in a text editor (Notepad or Notepad++; not Word or other tools that add extraneous text). Save this original file in cse something goes awry and you need to revert to it.
           <li>At the bottom of the file paste these lines:<blockquote>
             apoc.export.file.enabled=true<br>
dbms.security.procedures.unrestricted=jwt.security.*,apoc.*,gds.*,gen.*<br>
dbms.security.procedures.allowlist=jwt.security.*,gds.*,apoc.*, gen.*<br>

dbms.checkpoint.interval.time=30s<br>
dbms.checkpoint.interval.tx=1<br>
dbms.tx_log.rotation.retention_policy=false<br>
dbms.tx_log.rotation.size=1M<br>
dbms.transaction.timeout=30m
             </blockquote>  
         <li>Search for "dbms.security.procedures.unrestricted" to locate entries above those just entered and comment out the lines by adding a # at the start of the line. 
         <li>Search for "dbms.memory.heap." and adjust the memory upward to speed the processing. The number you select will depend on the RAM you have installed. I am a power user with lots of RAM and set as follows  <blockquote>
           dbms.memory.heap.initial_size=1G<br>
           dbms.memory.heap.max_size=4G
           </blockquote>
         </ol>
         <li>Finally, you must set up a configuration file about your specific project so that the Gen_UUDF knows where to find key facts unique to your environment. 
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
  
         
<h3>Developer Notes</h3>
         
Neo4j supports plugins for user defined functions and procedures. This project is developing a UDF supporting genealogy graphs. The development is done in Java 11 (Neo4j requirement). I am using Maven. The pom.xml contained the required dependencies and build parameters. The class (*.java) files will be added as they are developed. In setting up Maven, use the pom.xml and add the desired *.java files; then compile to produce your *.jar file. The default name of the jar is wai.neo4j.gen-##, where ## is the version number. The end user jar will need to be moderated, a role I will manage initially. That is, do NOT add jar files to the jar directory. Rather, add java classes and notify me when they are mature. I can then add then to the project and regenerate the end user jar.<br><br>
         
The project priorities will depend, in part, on developer interests and requests from end users. These are summarized at <a href="http://gfg.md/blogpost/7" target="new">http://gfg.md/blogpost/7</a>, which will be updated periodically. But for project management by developers, we should use GitHub functionalities.
         
         

<br><br>
