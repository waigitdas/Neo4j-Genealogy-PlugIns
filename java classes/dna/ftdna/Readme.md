<h1>Functions for DNA Analytics</h1>

A primary purpose of this prject is facilitating end user analytics of their DNA. UDFs subserving this function are in this, but also other folders. <br>

The UDF gen.dna.ftdna.load_ftdna_csv_files loads Family Tree DNA result files into Neo4j. This is a complex process made easy for end users. The end user downloads the files from FTDNA placing them in a root directory and subdirectories for each kit. Then the function is run without any other fuss. Well, the is a little prior fussing. The GEDCOM should be loaded before this because the DNA data is linked to Persons in the family tree who have tested. Another prerequisites is creation of a curated file that also that enables this linking of the GEDCOM individuals to the testers in the FTDNA results files.<br>

Triangulation groups, which are presently manually curate work along side the segment data from the FTDNA data. These UDF are discussed <a href="https://github.com/waigitdas/Neo4j-Genealogy-PlugIns/tree/main/java%20classes/tgs">under tgs</a><br>
