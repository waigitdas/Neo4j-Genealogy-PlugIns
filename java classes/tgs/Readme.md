<h1>Triangulation Group Analytics</h1>

A triangulation groups (TGs) is composed of DNA matches who share a specific chromosome segment and a common ancestor or ancestor couple. The Gen-UDF package contains functions to facilitate the identification of triangulation groups and then analytics leveraging the TGs.<br>

It is important to recognize the intent of the TG analytics in Gen-UDF. The focus is on identifying <b>distant ancestors</b>. Several factors play into optimizing for this end goal. Smaller segments are preferred because they contain less noise when large segment matches are excluded. The filtering enforces the requirement that matches have segment boundaries exlusively within the TG boundaries, excluding matches who have overlapping segments. Thus a propsitus and 1C may match to a large segment that subsumes the TG, but because it is too large, they do not make it into the filtered result by this rule. However, they may make in in if they also match with a more distance relative at a segment within the TG boundaries. <br>

The TG report is a set of worksheets packaged into a single Excel workbook for the TG. The worksheets are:
<ol>
  <li>Tab 1: list the matches in a TG. They use specified propositus (typically the end user) so that the common ancestor of these two is computed along with their relationship and the Ahnentafel path from the propositus to the common ancestor. There will be more than one line per match if there is more than one common ancestor or more than one sub-segment within the TG range. This report utilizes all the kits entered into the analytics. Even though the span of a segment is the same, FTDNA may report different SNP counts and CM; so minimum and maximums are reported. Not everyone in list report was identified as a match by FTDNA. 
  <li>Tab 2: same as above with the addition of names and places of the ancestors in each limb (propositus and match) of the family tree leading to the common ancestor.
  <li>Tab 3: Phased list of matches. Common ancestor triangulation points may be a couple. 
</ol>
