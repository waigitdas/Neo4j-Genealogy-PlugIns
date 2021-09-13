<h1>Neo4j Genealogy PlugIn</h1>

<h3>Evangelizing Graphs for Genealogists</h3>

Most genealogists are using data tools such as Excel, relational databases, various note taking software and commercial consumer genealogy software. Graph mehods have been difficult for genealogist to use because of the steep learning curve. This effort attempts to address this hurdle by providing a plugin, specifically designed for genealogists. It enhances the caabilities of an industry leading native graph database, Neo4j. This step-by-step guide should allow you to get your genealogy and DNA data into Neo4j within a short time frame and see some immediate value. You don't need to know the mechanics of graph methods to get started. If we can get a sufficient number of geeks engaged, the kit of easy to use tools should expand over time. Your <a href="mailto:dave@wai.md?subject=Neo4j Genealogy User Defined Function">feedback</a> is encouraged.<br>

This introduction has two major audiences: alpha testers and developers. The background is avaialble at <a href="http://gfg.md/blogpost/7" target="new">http://gfg.md/blogpost/7</a>. There are additional links at that site if you'd like more background.

<h3>Setting Up the Graph Environment</h3>

Below is a detailed set of steps for setting up a graph environment. But before getting to them, consider the specific items (prerequisites) required to take full advantage of the tools:
<ol>
  <li>A simple Excel workbook (template provided) which contains information about your local enviroment needed to run the tools.
  <li>Your GEDCOM file providing your family's historical and conventional genealogy.
  <li>Y- and at-DNA results from Family Tree DNA, preferably from multiple kits.
  <li>A curated Excel file (template provided) linking GEDCOM person identifiers (from the @I###@ tags) with DNA results (names and kit numbers). This takes some effort but the benefits are notable and actionable.
  <li>A famly relationship lookup file (provided) used to report relationships (1C, 3C1R, etc.).It needs to be in a specified directory on the PC running the tools  
</ol>

Several steps are required to implement the genealogy user defined function (Gen-UDF).<br><br>

<ol>
  <li><b>Step 1</b>. Install Neo4j Enterprise Edition, version 4.x. 
    <ol>
      <li>The process starts <a href="https://neo4j.com/download-neo4j-now/" target="new"><span style="color:red">here</span></a>. 
      <li>When you first open the software you see the "home page." You do not yet have your database. 
       <li>From the home page you create a project with a name of your chosing. A project can hold numerous databases which might support projects on several family lines.
       <li>Create your first database by opening the Neo4j browser, selecting the system database and typing this command: create database <your database name>. The name must be only lower letters. You can create other databases by repeating this command. To delete a database type drop database <name>.
       <li>Verify that you can start and stop the project database from the home page. This is important because the next steps may damage the functionality and you'd like to learn where this happened. Thus, be careful with this step.
       <li>Modify the neo4j configuration file to accomodate the project requirements. 
         <ol>
           <li>Open the configuration folder. The Neo4j application opens to a "home page" from which you can open the Configuration folder. The link is in the "...." icon to the right of your project "Open" button. The navigation is "...." > Open Folder > Configuration.
           <li>Open the file neo4j.conf in a text editor (Notepad or Notepad++; not Word or other tools that add extraneous text). Save this original file in case something goes awry and you need to revert to it.
           <li>At the bottom of the config file paste these lines:<blockquote>
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
         <li>Finally, you must set up a Excel file with configuration information about your specific project so that the Gen_UDF knows where to find key facts unique to your environment. 
           <ol>
             <li>Download this <a href="https://blobswai.blob.core.windows.net/gen-udf/neo4j-template.wai" target="new">file</a> and store it in this specific required directory: "c://Genealogy/Neo4j/"  It is very important that you use the capitalization as specified because java is case sensitive.
             <li>Open the file in a text editor and edit the information to the right of the colons with your specific information:
               <ol>
                 <li>neo4j_username: leave this a neo4j unless you changed it, which is not advised.
                 <li>neo4j_password: the password you entered at your initial login (above).
                 <li>Import_Dir: the neo4j import directory. The format is important! You must use / rather than \ which may come from a copying of the directory path. You must also have the last character as / so the Gen-UDF knows this is a directory. Here is how to get the import directory path. The Neo4j application opens to a "home page" from which you can open the Configuration folder. The link is in the "...." icon to the right of your project "Open" button. The navigation is "...." > Open Folder > Configuration.  
                 <li>Save the file and rename: c://Genealogy/Neo4j/neo4j.wai. Note, "template" should be removed from the name. 
                 <li>This file isolates your personal information from those developing the Gen-UDF, preserving your privacy. It is read locally on your computer and not viewed or stored offsite.  
               </ol>
           </ol>
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
<li><b>Step 4</b>. Upload your Family Tree DNA (FTDNA) downloadable csv files with results.
  <ol>
     <li>The naming of the directories and files is very important. Please assiduously follow the convention outlined here.
    <li>Create a directory dedicated to your project using a name you select. This is the root directory used by the Gen-UDF.
    <li>Set up a sub-directory within the root directory for each FTDNA kit whose data you will downloaded. The name of this directory will be used in the Neo4j data, so make it discerable and discrete.
    <li>Log in to a FTDNA project or to a specific kit and download the following files to the sub-directory you created for the kit. Take care not to modify the name of the downloaded file because its content is parsed to ket the kit number and the type of data in the file.
      <ol>
        <li>Family Finder matches. Example name: B51965_Family_Finder_Matches_2021-07-20.csv
        <li>Chromosome browser. Example name: B51965_Chromosome_Browser_Results_20210719.csv
        <li>Y-DNA matches; not Big Y. Set the view to Y-37 and then download the csv. Example name: 792577_YDNA_Matches_20210125.csv
        <li>Repeat this process for each kit, using a new sub-directory of the root directory for each.   
      </ol>
    <li>Prepare a curated file to link your GEDCOM to the DNA data. You will have two different graphs, the genealogy family history and the DNA data. One of the powerful aspects of graph methods is their analytics using multiple graphs. But they need help from expert curation until such a time that we can automate the links.
      <ol>
        <li>Download this <a href="https://blobswai.blob.core.windows.net/gen-udf/Family ftdna curation file.xlsx" target'="new">template file</a>.
        <li>Enter the match name, kit number and curated_RN (record number). The curated RN is the GEDCOM number for each person. The GEDCOM number is found at 0 @I###@ INDI. The ### is what you want. The match name is tricky. The latest formating of the FTDNA downloads does not mae this easy. You must find the match name in the file of a match. Thus, if John Doe is the kit, you'd have to find him in the kit of Jane Doe was a match and copy that name into the curation template. If you do not have the person in your GEDCOM, leave that field blank.
         <li>Save the template. The path and its name will be used as a variable in running the Gen-UDF.
      </ol>
    <li>Upload your FTDNA data to Neo4j.
      <ol>
        <li>In the Neo4j browser, run this command:
          <blockquote>
              return gen.dna.ftdna.load_ftdna_csv_files("path to root directory using / including tailing /",'your curated.csv',"database name")
          </blockquote>
      </ol>
      <li>Check to see that you have new data
        <blockquote>
          match (n) return labels(n), count(*) order by labels(n)
        </blockquote><br>
        <blockquote>
          match (n)-[r]-() return type(r), count(*) order by type(r)
        </blockquote>
    </ol>
        <li><b>Step 5.</b>Other functions are available to illustrate what is possible. This list will expand over time and suggestions are aooreciated.
      <ol>
        <li>Start with the Gen_UDF functions see with this query<br><blockquote>Show Functions yield name, signature, description,returnDescription,aggregating
where name STARTS WITH 'gen'
          return name, signature, description,returnDescription,aggregating</blockquote>
          <li>Find other query ideas at these <a href="http://stumpf.org/genealogy-blog/tag/neo4j">blog posts</a>.
      </ol>  
  

         
 </ol>
  
         
<h3>Developer Notes</h3>
         
Neo4j supports plugins for user defined functions and procedures. This project is developing a UDF supporting genealogy graphs. The development is done in Java 11 (Neo4j requirement). I am using Maven. The pom.xml contained the required dependencies and build parameters. The class (*.java) files will be added as they are developed. In setting up Maven, use the pom.xml and add the desired *.java files; then compile to produce your *.jar file. The default name of the jar is wai.neo4j.gen-##, where ## is the version number. The end user jar will need to be moderated, a role I will manage initially. That is, do NOT add jar files to the jar directory. Rather, add java classes and notify me when they are mature. I can then add then to the project and regenerate the end user jar.<br><br>
         
The project priorities will depend, in part, on developer interests and requests from end users. These are summarized at <a href="http://gfg.md/blogpost/7" target="new">http://gfg.md/blogpost/7</a>, which will be updated periodically. But for project management by developers, we should use GitHub functionalities.
         
         

<br><br>
         -------------------------------------------<br>
         David A Stumpf, MD., PhD<br>
         Professional Genealogists<br>
         Professor Emeritus, Northwestern University<br>
         email: <a href="mailto:dave@wai.md?subject=Neo4j Genealogy User Defined Function">dave@wai.md</a><br>
         &copy;2021
