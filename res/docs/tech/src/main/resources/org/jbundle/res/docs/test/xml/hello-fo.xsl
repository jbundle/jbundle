<?xml version="1.0"?>
<?cocoon-format type="text/xslfo"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format">

<xsl:output indent="yes"/>
<xsl:template match="/">
    <xsl:processing-instruction name="cocoon-format">type="text/xslfo"</xsl:processing-instruction>
	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
		<fo:layout-master-set>
			<fo:simple-page-master master-name="only">
				<fo:region-body/>
			</fo:simple-page-master>
		</fo:layout-master-set>
		<fo:page-sequence master-name="only">
			<fo:flow flow-name="xsl-region-body">
				<xsl:apply-templates select="//top"/>
			</fo:flow>
		</fo:page-sequence>
	</fo:root>
</xsl:template>
	<xsl:template match="top">
		<fo:block font-size="20pt" font-family="serif"
            line-height="30pt">
			<xsl:value-of select="test"/>
		</fo:block>
	</xsl:template>
</xsl:stylesheet>

