<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<xsl:import href="mainstyles-fo.xsl"/>
	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="ClassInfo">
	<fo:block font-size="20pt" font-family="serif" line-height="30pt">
		<xsl:value-of select="ClassDesc" />
	</fo:block>
	<fo:block text-align="justify" font-weight="bold">
   <fo:table space-before.optimum="6pt" text-align="centered">
    <fo:table-column column-width="150pt"/>
    <fo:table-column column-width="150pt"/>   
    <fo:table-body>
     <fo:table-row space-before.optimum="6pt">
      <fo:table-cell>
       <fo:block>Description:</fo:block>
      </fo:table-cell>
      <fo:table-cell>
       <fo:block><xsl:apply-templates select="ClassExplain"/></fo:block>
      </fo:table-cell>
     </fo:table-row>
     <fo:table-row space-before.optimum="6pt">
      <fo:table-cell>
       <fo:block>Operation:</fo:block>
      </fo:table-cell>
      <fo:table-cell>
       <fo:block><xsl:apply-templates select="ClassHelp" /></fo:block>
      </fo:table-cell>
     </fo:table-row>
    </fo:table-body>
   </fo:table>
	</fo:block>
	</xsl:template>

	<xsl:template match="operation">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="item-list">
		<ul>
			<xsl:for-each select="item">
				<li>
					<xsl:value-of select="."/>
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
				<span style="font-style: italic">
					<xsl:value-of select="comment"/>
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
