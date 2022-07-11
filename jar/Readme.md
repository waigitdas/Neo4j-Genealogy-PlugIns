<h1>Read Me</h1>

    Neo4j-Genealogy-PlugIns
  
Please use the most recently numbered file. This is placed in the Neo4j Import folder and the database restarted. <br><br>

The Neo4j configuration file does not need to be changed with updates <b>IF</b> you enabled it with gen.* white-listing. While white-listing can specify a specific version, you will find it easier to not specify this because you need not update the config file.<br><br>

<b>History</b>

<ol>
  <li><b>v 1.0.16</b>.
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
  

