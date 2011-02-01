<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xfm="http://www.w3.org/2000/12/xforms" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output indent="yes" method="xml"/>


<xsl:template match="/*">

<fo:root font-family="Times Roman" font-size="12pt" text-align="justify" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xfm="http://www.w3.org/2000/12/xforms">
<xsl:copy-of select="xfm:xform"/>
<xsl:copy-of select="script"/>

<fo:layout-master-set>
<fo:simple-page-master margin-bottom="25pt" margin-left="50pt" margin-right="50pt" margin-top="75pt" master-name="right" page-height="{/*/@height}">
   <fo:region-body margin-bottom="50pt"/>
   <fo:region-after extent="25pt"/>
</fo:simple-page-master>


<fo:page-sequence-master master-name="psmOddEven">
   <fo:repeatable-page-master-alternatives>
      <fo:conditional-page-master-reference master-name="right" page-position="first"/>
   </fo:repeatable-page-master-alternatives>
</fo:page-sequence-master>

</fo:layout-master-set>

<fo:page-sequence id="N2528" master-name="psmOddEven">

<fo:static-content flow-name="xsl-region-after">
   <fo:block font-size="10pt" text-align-last="center">
      <fo:page-number/>
   </fo:block>
</fo:static-content>

<fo:flow flow-name="xsl-region-body">

<xsl:apply-templates/>

</fo:flow>
</fo:page-sequence>
</fo:root>
</xsl:template>

<xsl:template match="xfm:xform"/>
<xsl:template match="xfm:checkbox|xfm:submit|xfm:exclusiveSelect|xfm:multipleSelect|xfm:inclusiveSelect|xfm:textbox|xfm:button|xfm:output">
   <fo:instream-foreign-object>
      <xsl:copy-of select="."/>
   </fo:instream-foreign-object>
</xsl:template>


</xsl:stylesheet>
