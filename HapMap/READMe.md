The condensed HapMap data was provided by Jonny Perl and DNA Painter and used with their permission. GFG transformed <a href="https://github.com/dnapainter/apis?fbclid=IwAR0tTmkifa0uK-gFJeDdjpc0GNmlIbVlgoYu6a3oOUfM-nINORjKQZ1WjvU" target="new">their file</a> into the csv file genetmap.csv which is suitable for importing into Neo4j using this query: 

LOAD CSV WITH HEADERS FROM 'file:///genetmap.csv' as line FIELDTERMINATOR ',' create (c:cHapMap{chr:toInteger(line.chr),pos:toInteger(line.pos),cm:toFloat(line.cm)})

The file must be in the import directory before running the query. This process is automated for users of Graphs for Genealogy. The file will be downloaded from here and imported using GFG code.

The "condensed" HapMap is dissed further by <a href="https://hapi-dna.org/2020/11/minimal-viable-genetic-maps/?fbclid=IwAR3alJcth1Kpcn5WL8Cl_c-49jloJPSbyOb4TQw2PRvwNhjO-gRaTu_zx34" target="new">Amy Williams</a>.

DNA Painerhas an <a href="https://dnapainter.com/tools/cme?fbclid=IwAR26BX1h9pnXFuXE8qWGaUlGeOB0xqTOOB14GvS6Q2vuRzFuOFr5h-u4mgs" target="new">online rendering</a> of the condensd HaoMap where you can enter a segment and get the cM.

The full HapMap file set is in the folder.
