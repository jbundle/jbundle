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

  <xsl:template match="paragraph">
    <fo:block font-size="13pt" font-family="serif" space-before.optimum="12pt" text-align="justify"><xsl:apply-templates/></fo:block>
  </xsl:template>

<xsl:template match="list">
	<fo:list-block>
	<xsl:for-each select="item">
	<fo:list-item>
	<fo:list-item-label>
	<fo:block>
	</fo:block>
	</fo:list-item-label>
	<fo:list-item-body>
	<fo:block font-size="13pt" font-family="serif" space-before.optimum="12pt" text-align="justify">
		- <xsl:apply-templates select="." />
	</fo:block>
	</fo:list-item-body>
	</fo:list-item>
	</xsl:for-each>
	</fo:list-block>
</xsl:template>

  <xsl:template match="link">
    <fo:block color="blue">
	 <xsl:apply-templates select="name" />
	</fo:block>
  </xsl:template>

</xsl:stylesheet>
