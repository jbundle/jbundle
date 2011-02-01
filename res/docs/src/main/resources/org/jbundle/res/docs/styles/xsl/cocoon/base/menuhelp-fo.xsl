<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<xsl:import href="mainstyles-fo.xsl"/>
	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="Menus">
	<fo:block align="center" text-align="center" font-weight="bold" font-size="x-large">
					<xsl:call-template name="image_link">
						<xsl:with-param name="image">
							<xsl:choose>
								<xsl:when test="IconResource != ''">
									<xsl:value-of select="IconResource"/>
  								</xsl:when>
 								<xsl:otherwise>
									<xsl:value-of select="Type"/>
			 					</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
						<xsl:with-param name="link">
							<xsl:value-of select="link" />
						</xsl:with-param>
						<xsl:with-param name="type">icon</xsl:with-param>
					</xsl:call-template>
					<xsl:element name="fo:basic-link">
						<xsl:attribute name="external-destination">
							<xsl:value-of select="link"/>
						</xsl:attribute>
						<xsl:value-of select="Name" />
					</xsl:element>
	</fo:block>
	<fo:block text-align="left" vertical-align="baseline" width="100" font-style="italic">
					<xsl:apply-templates select="Description"/>
	</fo:block>
		<xsl:apply-templates select="menu_list" />
	</xsl:template>

	<xsl:template match="menu_list">
	<fo:block text-align="left" vertical-align="baseline" width="100">
   <fo:table space-before.optimum="6pt" text-align="centered" border="0" cellpadding="5" width="100%">
    <fo:table-column column-width="50pt"/>
    <fo:table-column column-width="50pt"/>   
    <fo:table-column column-width="450pt"/>   
    <fo:table-body>
     <fo:table-row space-before.optimum="6pt">
      <fo:table-cell font-weight="bold" text-align="center" vertical-align="baseline">
       <fo:block>Help</fo:block>
      </fo:table-cell>
      <fo:table-cell font-weight="bold" text-align="center" vertical-align="baseline">
       <fo:block>Run</fo:block>
      </fo:table-cell>
      <fo:table-cell font-weight="bold" text-align="left" vertical-align="baseline">
       <fo:block>Description</fo:block>
      </fo:table-cell>
     </fo:table-row>
			<xsl:for-each select="Menus">
     <fo:table-row space-before.optimum="6pt">
      <fo:table-cell text-align="center" vertical-align="baseline">
       <fo:block>
	   						<xsl:call-template name="image_link">
							<xsl:with-param name="link">
								<xsl:value-of select="helplink" />
							</xsl:with-param>
							<xsl:with-param name="type">icon</xsl:with-param>
						</xsl:call-template>
		</fo:block>
      </fo:table-cell>
      <fo:table-cell text-align="center" vertical-align="baseline">
       <fo:block>
						<xsl:call-template name="image_link">
							<xsl:with-param name="link">
								<xsl:value-of select="link" />
							</xsl:with-param>
							<xsl:with-param name="image">
								<xsl:choose>
				 					<xsl:when test="IconResource != ''">
										<xsl:value-of select="IconResource"/>
  									</xsl:when>
 									<xsl:otherwise>
										<xsl:value-of select="Type"/>
				 					</xsl:otherwise>
								</xsl:choose>
							</xsl:with-param>
							<xsl:with-param name="type">icon</xsl:with-param>
						</xsl:call-template>

	   </fo:block>
      </fo:table-cell>
      <fo:table-cell text-align="justify" vertical-align="baseline">
       <fo:block font-weight="bold" font-size="large">
						<xsl:element name="a">
							<xsl:attribute name="href">
								<xsl:value-of select="link"/>
							</xsl:attribute>
								<xsl:value-of select="Name" /><br />
						</xsl:element>
       </fo:block>
       <fo:block>
						<xsl:apply-templates select="Description"/>

	   </fo:block>
      </fo:table-cell>
     </fo:table-row>
			</xsl:for-each>
    </fo:table-body>
   </fo:table>
		</fo:block>
	</xsl:template>
	
</xsl:stylesheet>
