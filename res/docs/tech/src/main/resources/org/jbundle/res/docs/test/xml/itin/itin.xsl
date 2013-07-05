<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format">

  <xsl:template match="/">
   <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
   
    <fo:layout-master-set>
     <fo:simple-page-master master-name="page"
                  page-height="29.7cm" 
                  page-width="21cm"
                  margin-top="1cm" 
                  margin-bottom="2cm" 
                  margin-left="2.5cm" 
                  margin-right="2.5cm">
       <fo:region-before extent="3cm"/>
       <fo:region-body margin-top="3cm"/>
       <fo:region-after extent="1.5cm"/>
     </fo:simple-page-master>

     <fo:page-sequence-master master-name="all">
       <fo:repeatable-page-master-alternatives>
	     <fo:conditional-page-master-reference master-reference="page" page-position="first"/>
       </fo:repeatable-page-master-alternatives>
     </fo:page-sequence-master>
    </fo:layout-master-set>

    <fo:page-sequence master-reference="all">
      <fo:static-content flow-name="xsl-region-after">
	    <fo:block text-align="center" 
	          font-size="10pt" 
		  font-family="serif" 
		  line-height="14pt">page <fo:page-number/></fo:block>
      </fo:static-content> 

      <fo:flow flow-name="xsl-region-body">
        <xsl:apply-templates/>
      </fo:flow>
    </fo:page-sequence>
   </fo:root>
  </xsl:template>

  <xsl:template match="title">
    <fo:block font-size="24pt" space-before.optimum="24pt" text-align="center">
	 <xsl:apply-templates/>
	</fo:block>
  </xsl:template>

  <xsl:template match="product">
    <fo:block font-size="14pt" font-weight="bold" color="blue" space-before.optimum="14pt" text-align="left">
	 <xsl:apply-templates select="date"/> -
	 <xsl:apply-templates select="desc"/>
	</fo:block>
	<xsl:apply-templates select="para"/>
  </xsl:template>

  <xsl:template match="para">
    <fo:block font-size="12pt" space-before.optimum="12pt" text-align="justify"><xsl:apply-templates/></fo:block>
  </xsl:template>

  <xsl:template match="air">
  <fo:block font-size="12pt" space-before.optimum="12pt"  background-color="yellow">
  
<fo:table width="100%" inline-progression-dimension="100%" table-layout="fixed">
  <fo:table-column column-number="1" column-width="proportional-column-width(1)">
  </fo:table-column>
  <fo:table-column column-number="2" column-width="proportional-column-width(1)">
  </fo:table-column>
  <fo:table-column column-number="3" column-width="proportional-column-width(1)">
  </fo:table-column>
  <fo:table-body>
    <fo:table-row>
      <fo:table-cell column-number="1" display-align="top">
        <fo:block>Leave <xsl:apply-templates select="depart/city"/></fo:block>
      </fo:table-cell>
      <fo:table-cell column-number="2" display-align="center" text-align="center">
        <fo:block>
		 <xsl:apply-templates select="depart/airline"/></fo:block>
      </fo:table-cell>
      <fo:table-cell column-number="3" text-align="right">
        <fo:block><xsl:apply-templates select="depart/time"/></fo:block>
      </fo:table-cell>
    </fo:table-row>
    <fo:table-row>
      <fo:table-cell column-number="1" display-align="top">
        <fo:block>Arrive <xsl:apply-templates select="arrive/city"/></fo:block>
      </fo:table-cell>
      <fo:table-cell column-number="2" display-align="center" text-align="center">
        <fo:block>
		 <fo:external-graphic src="http://www.tourgeek.com/images/tour/buttons/Air.gif"/>
		 <xsl:apply-templates select="depart/flight"/></fo:block>
      </fo:table-cell>
      <fo:table-cell column-number="3" text-align="right">
        <fo:block><xsl:apply-templates select="arrive/time"/></fo:block>
      </fo:table-cell>
    </fo:table-row>
  </fo:table-body>
</fo:table>
</fo:block>

  </xsl:template>

</xsl:stylesheet>
