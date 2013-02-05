<?xml version="1.0" encoding="utf-8"?>
<!-- Remember to only modify the template, not the build.xml in the base directory -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml"/>

<!-- pack -->
<xsl:template name="pack">
    <xsl:for-each select="full-screen/content-area/data/detail/file/Part">
		<xsl:element name="pack200">
		    <xsl:attribute name="src">${lib}/<xsl:value-of select="Part.Description"/>-${version}.jar</xsl:attribute>
		    <xsl:attribute name="destfile">${lib}/<xsl:value-of select="Part.Description"/>-${version}.jar</xsl:attribute>
		    <xsl:attribute name="repack">true</xsl:attribute>
		</xsl:element>
		<xsl:element name="pack200">
		    <xsl:attribute name="src">${lib}/<xsl:value-of select="Part.Description"/>-${version}.jar</xsl:attribute>
		    <xsl:attribute name="destfile">${lib}/<xsl:value-of select="Part.Description"/>-${version}.jar.pack.gz</xsl:attribute>
		    <xsl:attribute name="gzipoutput">true</xsl:attribute>
		    <xsl:attribute name="stripdebug">true</xsl:attribute>
		</xsl:element>
    </xsl:for-each>
</xsl:template>

<!-- jar -->
<xsl:template name="jar">
    <xsl:for-each select="full-screen/content-area/data/detail/file/Part">
      <xsl:if test="Part.PartType != 'manual'">    
		<xsl:element name="jar">
		    <xsl:attribute name="jarfile">${lib}/<xsl:value-of select="Part.Description"/>-${version}.jar</xsl:attribute>
              <xsl:for-each select="data/detail/file/Packages">
    			<xsl:element name="fileset">
                  <xsl:if test="../../../../Part.PartType != 'default'">
                    <xsl:attribute name="dir">${<xsl:value-of select="../../../../Part.PartType"/>}</xsl:attribute>
                  </xsl:if>
    			  <xsl:if test="../../../../Part.PartType='default'">
    			    <xsl:attribute name="dir">${basedir}${pathtoroot}/<xsl:value-of select="Packages.Path"/></xsl:attribute>
    			  </xsl:if>
    			  <xsl:attribute name="includes">
    				  <xsl:value-of select="translate(PackagesTree,'.','/')"/>/*<xsl:if test="Packages.Recursive = 'Y'">*</xsl:if>
    			  </xsl:attribute>
    			  <xsl:if test="ExcludePackages/excludes/exclude != ''">
    				  <xsl:attribute name="excludes">
    				<xsl:for-each select="ExcludePackages">
    				<xsl:for-each select="excludes">
    				<xsl:for-each select="exclude"><xsl:if test="position()>1">,</xsl:if><xsl:value-of select="translate(.,'.','/')"/>/**</xsl:for-each>
    				</xsl:for-each>
    				</xsl:for-each>
    				  </xsl:attribute>
    			  </xsl:if>
            </xsl:element>
          </xsl:for-each>
		</xsl:element>
      </xsl:if>
    </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
