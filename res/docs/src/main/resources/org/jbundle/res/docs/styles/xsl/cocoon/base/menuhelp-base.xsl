<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 

	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="Menus">
			<center>
				<span style="text-align: center; align: center; font-weight: bold; font-size: x-large">
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
					<xsl:element name="a">
						<xsl:attribute name="href">
							<xsl:value-of select="link"/>
						</xsl:attribute>
						<xsl:value-of select="Name" />
					</xsl:element>
				</span>
			</center>
				<span style="text-align: left; vertical-align: baseline; width: 100%; font-style: italic">
					<xsl:apply-templates select="Description" />
					<xsl:if test="MenusHelp!=''">
					<br/>
					<xsl:apply-templates select="MenusHelp" />
					</xsl:if>
				</span>

		<xsl:apply-templates select="menu_list" />

	</xsl:template>

	<xsl:template match="menu_list">
		<table border="0" cellpadding="5" width="100%">
			<tr>
				<th>Run</th>
				<th style="text-align: left; vertical-align: baseline">Description</th>
			</tr>
			<xsl:for-each select="Menus">
				<tr>
					<td style="text-align: center; vertical-align: baseline">
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
					</td>
					<td style="text-align: left; vertical-align: baseline">
						<xsl:call-template name="button_link">
							<xsl:with-param name="name">
								<xsl:value-of select="Name" />
							</xsl:with-param>
							<xsl:with-param name="link">
								<xsl:value-of select="helplink" />
							</xsl:with-param>
							<xsl:with-param name="image">Help</xsl:with-param>
							<xsl:with-param name="type">icon</xsl:with-param>
						</xsl:call-template>

						<xsl:apply-templates select="Description"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
	
</xsl:stylesheet>