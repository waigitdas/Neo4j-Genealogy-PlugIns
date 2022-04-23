<h1>Neo4j Genealogy PlugIn</h1>

<h3>Introducing Graphs for Genealogists</h3>

Most genealogists are using data tools such as Excel, relational databases, various note taking software and commercial consumer genealogy software. Graph mehods have been difficult for genealogists to use because of the steep learning curve. This effort attempts to address this hurdle by providing a plugin, specifically designed for genealogists. It enhances the capabilities of an industry leading native graph database, Neo4j. A <a href="https://www.wai.md/gfg" target="new">series of videos</a> is available to guide you in the installation of Neo4, the GFG Plug-In and the GFG application software. If we can get a sufficient number of geeks engaged, the toolkit of easy to use tools should expand over time. Your <a href="mailto:dave@wai.md?subject=Neo4j Genealogy User Defined Function">feedback</a> is encouraged. Part of the inital feedback was to create a user friendly interface to allow users to get quickly to results. There is now a Graphs for Genealogist application available that runs on your PC and uses the Neo4j PlugIn and its functions. <br>

Graphs for Genealogists allows the user to load GEDCOM, DNA results, reference data and their curated data into a robust graph database schema optimized for genetic genealogy analytics. The initial capabilities focus on triangulation groups, autosomal haplotrees, graph visualiztions linking different types of graphs, and discovering clues about distant ancestors. These topics, at present, differentiate this project from several other excellent tools available to genetic genealogist. Once you experience the power and beauty of graph analytics, we hope you will see the opportunities to expand the capabilities.<br> 

<b>Get Involved</b><br>

GFG is open source. Get involved, please go to <a href="https://www.wai.md/gfg" target="new">https://www.wai.md/gfg</a>. 

<b>Developer Notes</b>
         
Neo4j supports plugins for user defined functions (UDF) and procedures. This project is developing a set of UDF supporting genealogy graphs. The development is done in Java 11 (Neo4j requirement). I am using Maven. The pom.xml contained the required dependencies and build parameters. The class (java) files will be added as they are developed. In setting up Maven, use the pom.xml and add the desired java files; then compile to produce your jar file. The default name of the jar is wai.neo4j.gen-##, where ## is the version number. The end user jar will need to be moderated, a role I will manage initially. That is, do NOT add jar files to the jar directory. Rather, add java classes and notify me when they are mature. I can then add then to the project and regenerate the end user jar.<br>
         
         

<br><br>
         -------------------------------------------<br>
         David A Stumpf, MD., PhD<br>
         Professional Genealogist<br>
         Professor Emeritus, Northwestern University<br>
         email: <a href="mailto:dave@wai.md?subject=Neo4j Genealogy User Defined Function">dave@wai.md</a><br>
         &copy;2021<br>
         <img src="https://blobswai.blob.core.windows.net/wai/WAI.jpg" width="10%" height="10%"/>
