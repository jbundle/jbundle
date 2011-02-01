<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<xsl:import href="mainstyles-fo.xsl"/>

	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="Menus">
		<xsl:apply-templates select="menu_list" />
	</xsl:template>

	<xsl:template match="menu_list">
		<table border="0" cellspacing="10" width="100%">
			<xsl:for-each select="Menus">
				<xsl:param name="pos"><xsl:value-of select="position()" /></xsl:param>
				<xsl:if test="(position() mod 3)=1">
			<tr>
				<td align="left" valign="top">
					<xsl:if test="position()>3"><hr /></xsl:if>
					<xsl:apply-templates select="../*[position()=$pos]" />
				</td>
				<td align="left" valign="top">
					<xsl:if test="position()>3"><hr /></xsl:if>
					<xsl:apply-templates select="../*[position()=$pos+1]" />
				</td>
				<td align="left" valign="top">
					<xsl:if test="position()>3"><hr /></xsl:if>
					<xsl:apply-templates select="../*[position()=$pos+2]" />
				</td>
			</tr>
				</xsl:if>
			</xsl:for-each>
		</table>
	</xsl:template>
	
	<xsl:template match="menu_list/Menus">
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

						<xsl:element name="a">
							<xsl:attribute name="href">
								<xsl:value-of select="link"/>
							</xsl:attribute>
								<xsl:value-of select="Name" />
						</xsl:element>
						<br />
						<span style="font-size: 10pt">
							<xsl:apply-templates select="Description"/>
						</span>
	</xsl:template>

</xsl:stylesheet>
