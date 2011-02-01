<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 

	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="ClassInfo">
		<table border="0" cellpadding="5" width="100%">
			<tr>
				<td colspan="2" style="text-align: center; font-weight: bold; font-size: x-large">
					<xsl:value-of select="ClassDesc" />
				</td>
			</tr>
			<tr>
				<td style="text-align: right; vertical-align: baseline; font-weight: bold">Description</td>
				<td style="text-align: left; vertical-align: baseline; width: 100%; font-style: italic"><xsl:apply-templates select="ClassExplain"/></td>
			</tr>
			<tr>
				<td style="text-align: right; vertical-align: baseline; font-weight: bold">Operation</td>
				<td style="text-align: left; vertical-align: baseline; width: 100%"><xsl:apply-templates select="ClassHelp" /></td>
			</tr>
			<optional field="SeeAlso"></optional>
			<optional field="TechnicalInfo"></optional>
		</table>
	</xsl:template>

	<xsl:template match="operation">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="help-button">
		<xsl:call-template name="button_link">
			<xsl:with-param name="name"><xsl:value-of select="name" /></xsl:with-param>
			<xsl:with-param name="description"><xsl:value-of select="description" /></xsl:with-param>
			<xsl:with-param name="link">
				<xsl:if test="count(link)>0">?help=&amp;<xsl:value-of select="link" /></xsl:if>
			</xsl:with-param>
			<xsl:with-param name="image">
				<xsl:if test="count(image)>0"><xsl:value-of select="image" /></xsl:if>
				<xsl:if test="count(image)=0">[none]</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="style">background: #CCFFFF;</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

    <!-- deprecated - try to use button-link since it works in all styles - remember to change link to include ?help= -->
	<xsl:template match="help-link">
		<xsl:call-template name="button_link">
			<xsl:with-param name="name">
				<xsl:if test="count(name)>0"><xsl:value-of select="name" /></xsl:if>
				<xsl:if test="count(name)=0">[none]</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="description"><xsl:value-of select="description" /></xsl:with-param>
			<xsl:with-param name="link">
				<xsl:if test="count(link)>0">?help=&amp;<xsl:value-of select="link" /></xsl:if>
			</xsl:with-param>
			<xsl:with-param name="image">
				<xsl:if test="count(image)>0"><xsl:value-of select="image" /></xsl:if>
				<xsl:if test="count(image)=0">[none]</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="item-list">
		<ul>
			<xsl:for-each select="item">
				<li>
					<xsl:apply-templates select="."/>
				</li>
			</xsl:for-each>
		</ul>
	</xsl:template>

	<!-- G/L Account transaction -->
	<xsl:template match="acct-trx">
		<table cellspacing="1px" cellpadding="0" style="border: 0; padding: 0px 5px 0px 5px; background-color: #DDDDDD; width: 100%">
			<xsl:for-each select="credit|debit">
				<tr>
					<td width="15%">
						<xsl:if test="position() = 1">
							<xsl:value-of select="../date"/>
						</xsl:if>
					</td>
					<xsl:apply-templates select="." />
				</tr>
			</xsl:for-each>
			<tr>
				<td></td>
				<td width="100%" colspan="4">
				<span style="font-style: italic; font-size: 10pt;">
					<xsl:apply-templates select="comment"/>
				</span>
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="debit">
		<td colspan="2">
			<xsl:value-of select="description"/>
		</td>
		<td align="right">
			<xsl:value-of select="amount"/>
		</td>
		<td>
		</td>
	</xsl:template>

	<xsl:template match="credit">
		<td width="5%">
		</td>
		<td width="50%">
			<xsl:value-of select="description"/>
		</td>
		<td width="15%">
		</td>
		<td  width="15%" align="right">
			<xsl:value-of select="amount"/>
		</td>
	</xsl:template>

	<xsl:template match="info-table">
		<table border="0" cellpadding="2">
			<xsl:for-each select="table-row">
				<tr>
					<td style="text-align: right; vertical-align: baseline">
						<xsl:apply-templates select="name" />
					</td>
					<td style="vertical-align: baseline">
						<xsl:apply-templates select="description" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>

</xsl:stylesheet>
