<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xfm="http://www.w3.org/2000/12/xforms">
<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 

	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="navigation-menu">
	</xsl:template>
	<xsl:template name="navigation-corner">
	</xsl:template>

	<xsl:template match="form">
	</xsl:template>

	<xsl:template match="data">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="detail/file">
		<table class="toolbars">
			<tr>
				<xsl:for-each select="//toolbars/*">
					<td><xsl:apply-templates select="." /></td>
				</xsl:for-each>
			</tr>
		</table>
		<table border="1" cellpadding="5" width="100%" class="gridscreen" id="gridscreen">
			<xsl:variable name="DetailName" select="name(*)"/>
			<tr>
				<xsl:for-each select="//form//detail[attribute::name=$DetailName]/*">
					<xsl:variable name="recordtype" select="." />
						<xsl:if test="name($recordtype)!='heading' and name($recordtype)!='footing' and name($recordtype)!='detail'">
							<th>
								<xsl:value-of select="xfm:caption"/>
							</th>
						</xsl:if>
				</xsl:for-each>
			</tr>
			<xsl:for-each select="*">
			<xsl:variable name="record" select="." />
			<xsl:if test="$record/heading/*!=''">
				<tr>
					<td colspan="10">
						<xsl:apply-templates select="$record/heading/file" />
					</td>
				</tr>
			</xsl:if>

				<xsl:element name="tr">
					<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
					<xsl:for-each select="//form//detail[attribute::name=$DetailName]/*">
						<xsl:variable name="ref"><xsl:value-of select="@ref"/></xsl:variable>
						<xsl:if test="name(.)!='heading' and name(.)!='footing' and name(.)!='detail'">
							
								<xsl:if test="local-name()!='trigger'">
									<td><xsl:value-of select="$record/*[name()=$ref]"/></td>
								</xsl:if>
								<xsl:if test="local-name()='trigger'">
									<td class="button">
		 							<xsl:call-template name="button_link">
										<xsl:with-param name="name"><xsl:if test="xfm:caption!=''"><xsl:value-of select="xfm:caption"/></xsl:if><xsl:if test="xfm:caption=''">[none]</xsl:if></xsl:with-param>
										<xsl:with-param name="description"><xsl:value-of select="xfm:hint"/></xsl:with-param>
										<xsl:with-param name="image"><xsl:value-of select="@name"/></xsl:with-param>
										<xsl:with-param name="link">?command=<xsl:value-of select="@command" /><xsl:value-of select="$record/*[name()=$ref]"/></xsl:with-param>
										<xsl:with-param name="type">button</xsl:with-param>
										<xsl:with-param name="style"><xsl:value-of select="@style" /></xsl:with-param>
										<xsl:with-param name="xform"><xsl:value-of select="@xform" /></xsl:with-param>
									</xsl:call-template>
									</td>
								</xsl:if>
							
						</xsl:if>
					</xsl:for-each>
				</xsl:element>
			
			<xsl:if test="$record/data/detail/*!=''">
				<tr>
					<td colspan="10">
						<xsl:apply-templates select="$record/data/detail/file" />
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="$record/footing/*!=''">
				<tr>
					<td colspan="10">
						<xsl:apply-templates select="$record/footing/file" />
					</td>
				</tr>
			</xsl:if>
			</xsl:for-each>
		</table>
	</xsl:template>

	<xsl:template match="heading/file|footing/file">
		<xsl:variable name="type"><xsl:value-of select="local-name(..)" /></xsl:variable>
		<xsl:variable name="parent"><xsl:value-of select="local-name(../..)" /></xsl:variable>
		<table border="0" cellpadding="5" width="100%">
			<xsl:for-each select="*">
			<tr>
			<xsl:variable name="record" select="."/>
			<xsl:if test="$parent='data'">	<!-- Report heading/footing -->
				<xsl:for-each select="//form/xform/*[name()=$type]/*">
					<xsl:variable name="ref"><xsl:value-of select="@ref"/></xsl:variable>
					<td style="text-align: right; vertical-align: baseline">
						<b><xsl:value-of select="xfm:caption"/>:</b>
					</td>
					<td style="text-align: left; vertical-align: baseline">
						<xsl:value-of select="$record/*[name()=$ref]"/>
					</td>
				</xsl:for-each>
			</xsl:if>
			<xsl:if test="$parent!='data'">	<!-- Detail heading/footing -->
				<xsl:for-each select="//form/xform/*[name()='detail'][attribute::name=$parent]/*">
				</xsl:for-each>
				
				<xsl:for-each select="//form/xform/*[name()='detail'][attribute::name=$parent]/*[name()=$type]/form/*">
					<xsl:variable name="ref"><xsl:value-of select="@ref"/></xsl:variable>
					<td style="text-align: right; vertical-align: baseline">
						<b><xsl:value-of select="xfm:caption"/>:</b>
					</td>
					<td style="text-align: left; vertical-align: baseline">
						<xsl:value-of select="$record/*[name()=$ref]"/>
					</td>
				</xsl:for-each>
			</xsl:if>
			</tr>
			</xsl:for-each>
		</table>
	</xsl:template>

</xsl:stylesheet>

