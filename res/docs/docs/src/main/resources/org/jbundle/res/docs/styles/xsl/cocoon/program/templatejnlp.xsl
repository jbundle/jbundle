<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">

<jnlp spec="1.0+" codebase="$$context">
  <information>
    <title><xsl:apply-templates select="full-screen/content-area/data/heading/file/JnlpFile/JnlpFile.Title"/></title>
    <vendor>jbundle.com</vendor>
    <homepage href="index.html"/>
    <description kind="one-line"><xsl:apply-templates select="full-screen/content-area/data/heading/file/JnlpFile/JnlpFile.Description"/></description>
    <description kind="short"><xsl:apply-templates select="full-screen/content-area/data/heading/file/JnlpFile/JnlpFile.ShortDesc"/></description>
    <xsl:element name="icon">
        <xsl:attribute name="href"><xsl:apply-templates select="full-screen/content-area/data/heading/file/JnlpFile/JnlpFile.Icon"/></xsl:attribute>
    </xsl:element>
    <offline-allowed/>
	<keywords>tour</keywords>
  </information>
  <security>
  </security>
  <resources>
    <java version="1.6+" initial-heap-size="128m" max-heap-size="512m"/>
	<property name="jnlp.packEnabled" value="true"/>
        <xsl:apply-templates select="full-screen/content-area/data/detail/file"/>
  </resources>
  <component-desc/>
</jnlp>
</xsl:template>

<xsl:template match="file">
    <xsl:for-each select="Part">
        <xsl:apply-templates select="."/>
        <xsl:apply-templates select="data/detail/file"/>
    </xsl:for-each>
    <xsl:for-each select="Packages">
        <xsl:apply-templates select="."/>
    </xsl:for-each>
</xsl:template>

<xsl:template match="Part">
<xsl:element name="jar">
    <xsl:attribute name="href"><xsl:if test="Part.Path = ''">lib/<xsl:value-of select="Part.Description"/>-${project.version}.jar</xsl:if><xsl:if test="Part.Path != ''"><xsl:value-of select="Part.Path"/></xsl:if></xsl:attribute>
    <xsl:attribute name="part"><xsl:value-of select="Part.Description"/></xsl:attribute>
    <xsl:if test="Part.Kind != ''">
        <xsl:attribute name="download"><xsl:value-of select="Part.Kind"/></xsl:attribute>
  	</xsl:if>
</xsl:element>
</xsl:template>

<xsl:template match="Packages">
<xsl:element name="package">
    <xsl:attribute name="name"><xsl:value-of select="PackagesTree"/>.*</xsl:attribute>
    <xsl:attribute name="part"><xsl:value-of select="../../../../Part.Description"/></xsl:attribute>
    <xsl:if test="Packages.Recursive = 'Y'">
        <xsl:attribute name="recursive">true</xsl:attribute>
  	</xsl:if>
</xsl:element>
</xsl:template>

</xsl:stylesheet>
