<h1>Triangulation Group Analytics</h1>

A triangulation groups (TGs) is composed of DNA matches who share a specific chromosome segment and a common ancestor or ancestor couple. The Gen-UDF package contains functions to facilitate the identification of triangulation groups and then analytics leveraging the TGs.<br>

It is important to recognize the intent of the TG analytics in Gen-UDF. The focus is on identifying <b>distant ancestors</b>. Several factors play into optimizing for this end goal. Smaller segments are preferred because they contain less noise when large segment matches are excluded. The filtering enforces the requirement that matches have segment boundaries exlusively within the TG boundaries, excluding matches who have overlapping segments. Thus a propsitus and 1C may match to a large segment that subsumes the TG, but because it is too large, they do not make it into the filtered result by this rule. However, they may make in in if they also match with a more distance relative at a segment within the TG boundaries. <br>

The TG report is a set of worksheets packaged into a single Excel workbook for the TG. The worksheets are:
<ol>
  <li>Tab 1: list the matches in a TG. They use a specified propositus (typically the end user) so that the common ancestor of these two is computed along with their relationship and the Ahnentafel path from the propositus to the common ancestor. There will be more than one line per match if there is more than one common ancestor or more than one sub-segment within the TG range. This report utilizes all the kits entered into the analytics. Even though the span of a segment is the same, FTDNA may report different SNP counts and CM; so minimum and maximums are reported. Not everyone in list report was identified as a match by FTDNA. THat is a match to a match is evaluated as a match even though it is not directly reported. While this carries some risk of a false positive, this is minimized by virtue of the small CM of the segment and the shared ancestor(s) on a distant twig of the  family tree.
  <li>Tab 2: same as above with the addition of names and places of the ancestors in each limb (propositus and match) of the family tree leading to the common ancestor.
  <li>Tab 3: This report rolls up the matches to give a less clutter summary.  
  <li>Tab 4: Phased list of matches. This report evaluates every pairing of matches from the prior reports (Match1 and Match2). THe number of lines is defined by the permutations of the original match list. Each line (or permutation) shows the common ancestor(s) triangulation points, which may be a couple. We use the putative common ancestor to render a listing that distinguishes an ancestor as being in the path between the propositus and the common ancestor (phased) or not (not phased).
</ol>