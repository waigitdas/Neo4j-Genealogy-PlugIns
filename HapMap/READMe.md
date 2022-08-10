The condensed HapMap data was provided by Jonny Perl and DNA Painter. GFG transformed this into the csv file genetmap.csv which is suitable for importing into Neo4j using this query: 

LOAD CSV WITH HEADERS FROM 'file:///genetmap.csv' as line FIELDTERMINATOR ',' create (c:cHapMap{chr:toInteger(line.chr),pos:toInteger(line.pos),cm:toFloat(line.cm)})

The file must be in the import directory before running the query. This process is automated for users of Graphs for Genealogy. The file will be downloaded from here and imported using GFG code.

The "condensed" HapMap is dissed further by<a href="https://hapi-dna.org/2020/11/minimal-viable-genetic-maps/?fbclid=IwAR3alJcth1Kpcn5WL8Cl_c-49jloJPSbyOb4TQw2PRvwNhjO-gRaTu_zx34" target="new">Ann Williams</a>

The full HapMap file set is in the folder.
