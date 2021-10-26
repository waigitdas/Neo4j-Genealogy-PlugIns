<h1>Neo4j Genealogy PlugIn</h1>

<h3>Introducing Graphs for Genealogists</h3>

Most genealogists are using data tools such as Excel, relational databases, various note taking software and commercial consumer genealogy software. Graph mehods have been difficult for genealogist to use because of the steep learning curve. This effort attempts to address this hurdle by providing a plugin, specifically designed for genealogists. It enhances the capabilities of an industry leading native graph database, Neo4j. A step-by-step guide should allow you to get your genealogy and DNA data into Neo4j within a short time frame and see some immediate value. You don't need to know the mechanics of graph methods to get started. If we can get a sufficient number of geeks engaged, the tolkit of easy to use tools should expand over time. Your <a href="mailto:dave@wai.md?subject=Neo4j Genealogy User Defined Function">feedback</a> is encouraged. Part of the inital feedback was to create a user friendly interface to allow users to get quickly to results. There is now a Graphs for Genealogist application available that runs on your PC and uses the Neo4j PlugIn and its functions. <br>

Graphs for Genealogists allow the user to load GEDCOM, DNA result, reference data and their curate data into a robust graph database schema optimized for genetic genealogy analytics. The initial capabilities focus on triangulation groups, at-haplotrees, graph visualiztions linking different types of graphs, and discovering clues about distant ancestors. These topics, at present, differentiate this prject from several other excellent tools available to genetic genealogist. Once you experience the power and beauty of graph analytics, we hope you will see the opportunities to expand the capabilities.<br> 

<b>Get Involved</b><br>

There are three major audiences: alpha testers, developers and cheer leaders. We are looking for a limited set <a href="mailto:dave@wai.md?subject=Alpha testing of Graphs for Genealogists">alpha-testers</a> who have a tolerance for bugs and a desire to help polish the app for a broader audience. You'll be acknowledged! Coders and graph data scientists will be key to further extending the app's capabilities and usability. If you have <a href="mailto:dave@wai.md?subject=Developer Interest in Graphs for Genealogists">interest as a developer</a>, please communicate it with the link. The current capabilities were designed to be flexible and scalable, including modularity that encourages division of work and collaborative development. GitHub is a solid platform to support that approach. There are many citizen scientist who are very competent genealogists and genetic genealogists. You don't need to be a developer or tester; we need observers to bring ideas, wish lists, conundra and passion for advancing genetic genealogy. Soon we'll have a social media presence for you. But stay tuned here as well.
<br><br>
<b>Some more details</b><br><br>
Earlier background is avaialble in a series of blog posts at <a href="http://gfg.md/gfg_blog_list/" target="new">http://gfg.md/gfg_blog_list/</a>. <br>

This GitHub site has several elements. <a href="https://github.com/waigitdas/Neo4j-Genealogy-PlugIns/tree/main/user_software/html">HTML documents</a> used in the help portions of the software explain how to install Neo4j, prepare genealogy data (GEDCOM and DNA results), include reference data, optimize the graph schema for analytics and -- very soon -- run reports. The effort involved and data required is not trivial, so your adviced to read the high level view at <a href="https://blobswai.blob.core.windows.net/gfg-software/Help_Startup.html">from out Azure blob container</a>.<br><br>

<b>Developer Notes</b>
         
Neo4j supports plugins for user defined functions and procedures. This project is developing a set of UDF supporting genealogy graphs. The development is done in Java 11 (Neo4j requirement). I am using Maven. The pom.xml contained the required dependencies and build parameters. The class (java) files will be added as they are developed. In setting up Maven, use the pom.xml and add the desired java files; then compile to produce your jar file. The default name of the jar is wai.neo4j.gen-##, where ## is the version number. The end user jar will need to be moderated, a role I will manage initially. That is, do NOT add jar files to the jar directory. Rather, add java classes and notify me when they are mature. I can then add then to the project and regenerate the end user jar.<br>
         
The project priorities will depend, in part, on developer interests and requests from end users. These are summarized at <a href="http://gfg.md/blogpost/7" target="new">http://gfg.md/blogpost/7</a>, which will be updated periodically. For project management by developers we will use GitHub functionalities.
         
         

<br><br>
         -------------------------------------------<br>
         David A Stumpf, MD., PhD<br>
         Professional Genealogist<br>
         Professor Emeritus, Northwestern University<br>
         email: <a href="mailto:dave@wai.md?subject=Neo4j Genealogy User Defined Function">dave@wai.md</a><br>
         &copy;2021<br>
         <img src="https://blobswai.blob.core.windows.net/wai/WAI.jpg" width="10%" height="10%"/>
