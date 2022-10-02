<h1>Read Me</h1>

    Neo4j-Genealogy-PlugIns
  
Please use the most recently numbered file. This is placed in the Neo4j Import folder and the database restarted. <br><br>

The Neo4j configuration file does not need to be changed with updates <b>IF</b> you enabled it with gen.* white-listing. While white-listing can specify a specific version, you will find it easier to not specify this because you need not update the config file.<br><br>

<b>History</b>

<ol>
<li><b>v 1.1.7</b> Oct 1, 2022</li>
<ul>
<li>fix bugs in setup workflows
</uL>
<li><b>v 1.1.5</b> Sept 29, 2022</li>
 <ul>
    <li>Y-DNA enhancements</li>
        <ul>
            <li>function to upload FTDNA STR and SNP data</li>
            <li>function to computer Y-haplogroups of patrilineal paths in a family tree</li>
            <li>Function to list all Y-haplogroups in a project</li>
            <li>improved report on haplogroups and matches on a clade and its branches. Includes visualization query for Neo4j Browser</li>
        </ul>
 
 </ul>

<li><b>v 1.1.4</b> Sept 14, 2022
<ul>
    <li>adjustments to accomodate new menu items in GFG software v 1.2.4</li>
</ul>
</li>

<li><b>v 1.1.3</b> Sept 13, 2022
    <ul>
        <li>added inferred segments for avatars</li>
        <li>added function to distinguish aunt/uncles from nibling. Used in inferred segments; aunt/uncles cannot be used.</li>
        <li>added function to get the full path to the Neo4j dbms folder</li>
    </ul>
</li>

<li><b>v 1.1.0</b>  August 20, 2022</li>
    <ul>
        <li>reorganized file structure to improve usability</li> 
        <li>simlified Neo4j setup using code to create folders with required file and configuring plugins and Neo4j behavior</li>
        <li>added chromosome browser function which is used in a few current functions; more will be added in next release.</li> 
        <li>added avatars: virtual in silico ancestors and relatives</li> 
            <ul>
                <li>augmented monophyletic segment discovery</li>
                <li>creation of avatars (virtual ancestors and relatives)</li>
                <li>populate avatars with reconstructed DNA monophyletic segments</li>
                <li>merged  segments and calculating their cm using a modified HapMap</li>
                <li>DNA matches of avatars to real world DNA testers</li>
                <li>avatar reports on segments, matches, etc.</li>
            </ul>
         <li>implemented modified HapMap to speed segment cM calculations; automatically uploaded to user Neo4j database with other reference data</li> 
         <li>simplified data loading into single functions that call specific loading functions in sequence</li> 
         <li>added half double cousin report to the double cousin report</li> 
      </ul>
<li><b>v 1.0.17</b>. July 11, 2022
   <ul>
   <li>Added monophyletic segment report, including their finding of new matches in a family line.
   <li>Added Y-DNA descendancy haplotree with Y-matches.
   <li>added capability to find matches to surnames in a person's direct family tree lines.
   <li>added ancestor descendant monophyletic segment searches for new matches.
   <li>fixed x_gen_dist which was missin a few relationships.
   <li>added Y- and mt-HG to ancestral surname report
   <li>new function to add parental side of p and m to match_segment relationship
   <li>New function to report parental origin of segments: paternal, maternal or unknown; some generations done, but still in development.
    </ul>
  <li><b>v 1.0.16</b>. July 11, 2022
   <ul>
   <li>Added Leiden community detection algorithm, which is an improven on the Louvain algorithm. It provides reliable generation of intermediary communities.
   <li>Enhance DNA coverage algorithm. 
   <li>Fix to ancestor reconstruction algorithm's generation of a DNA Painter file.
    <li>Update degree centrality to conform to Neo4j Graph Data Science updates
   <li>Fix in surname search function.
</ul>
    <li><b>v 1.0.15</b>. Added function to add DNA testers whose DNA is not in the project. Supported by new Person property at_DNA_tester.
   <li><b>v 1.0.14</b>. Added enforcing creation of the HapMap before loading other data. Added a new function for computing DNA coverage for any ancestor who has descendant DNA testers. 
  <li><b>v 1.0.11</b>. New reports identifying dual matches for both autosomal and mitochondrial DNA. Robust triangulation report have been improved. Also upgrade to <a href="https://www.wai.md/product-page/gfg-software">GFG software v 1.0.2</a> to capitalize on its menu driven access to new reporting capabilities.
<li><b>v 1.0.10</b>. Restored match_segment relationship properties describing the relationship between the match-pair including their relationship (1C, H3C, etc), the correlation of relationship (cor), an the common ancestor. Added a new function -- gen.dna.shared_mt_haplogroup -- to find matches who are both at- and mt-DNA matches. </li>
  
</ol> 
  

