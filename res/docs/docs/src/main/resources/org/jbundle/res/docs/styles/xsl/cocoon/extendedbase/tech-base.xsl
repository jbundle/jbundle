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
				<td style="text-align: right; vertical-align: baseline; font-weight: bold">
					Description:
				</td>
				<td style="text-align: left; vertical-align: baseline; width: 100%; font-style: italic">
					<xsl:apply-templates select="ClassExplain"/>
				</td>
			</tr>
			<tr>
				<td style="text-align: right; vertical-align: baseline; font-weight: bold">
					Operation:
				</td>
				<td style="text-align: left; vertical-align: baseline; width: 100%">
					<xsl:apply-templates select="ClassHelp" />
				</td>
			</tr>
			<tr>
				<td style="text-align: right; vertical-align: baseline; font-weight: bold">
					Links:
				</td>
				<td style="text-align: left; vertical-align: baseline; width: 100%">
					<xsl:element name="a">
						<xsl:attribute name="href">./docs/programmer/api/org/jbundle/<xsl:value-of select="translate(ClassPackage, '.', '/')" />/<xsl:value-of select="ClassName" />.html</xsl:attribute>
						Javadoc
					</xsl:element>
					<br />
					<xsl:element name="a">
						<xsl:attribute name="href">./src/org/jbundle/<xsl:value-of select="translate(ClassPackage, '.', '/')"/>/<xsl:value-of select="ClassName" />.java</xsl:attribute>
						Source code
					</xsl:element>
				</td>
			</tr>
			<tr>
				<td style="text-align: right; vertical-align: baseline; font-weight: bold">
					Internal:
				</td>
				<td style="text-align: left; vertical-align: baseline; width: 100%">
					Class: <span style="font-weight:bold">org.jbundle.<xsl:value-of select="ClassPackage" />.<xsl:value-of select="ClassName" /></span><br />
					Class Name: <xsl:value-of select="ClassName" /><br />
					Base Class: <xsl:value-of select="BaseClassName" /><br />
					Package: <xsl:value-of select="ClassPackage" /><br />
					Type: <xsl:value-of select="ClassType" /><br />
					Implements: <xsl:value-of select="ClassImplements" /><br />
					SeeAlso <xsl:value-of select="SeeAlso" />
				</td>
			</tr>
			<tr>
				<td style="text-align: right; vertical-align: baseline; font-weight: bold">
					Tech Info:
				</td>
				<td style="text-align: left; vertical-align: baseline; width: 100%">
					<xsl:value-of select="TechnicalInfo" />
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="FileHdr">
		<table border="0" cellpadding="5" width="100%">
			<tr>
				<td colspan="2" style="text-align: center; font-weight: bold; font-size: large">
					<xsl:value-of select="FileName" />
				</td>
			</tr>
			<tr>
				<td style="text-align: right; vertical-align: baseline; font-weight: bold">
					Description:
				</td>
				<td style="text-align: left; vertical-align: baseline; width: 100%">
					<xsl:value-of select="FileDesc"/>
				</td>
			</tr>
			<tr>
				<td style="text-align: right; vertical-align: baseline; font-weight: bold">
					Other:
				</td>
				<td style="text-align: left; vertical-align: baseline; width: 100%">
					Main Filename: <xsl:value-of select="FileMainFilename"/><br />
					Type: <xsl:value-of select="Type"/><br />
					Database Name: <xsl:value-of select="DatabaseName"/><br />
					Record called: <xsl:value-of select="FileRecCalled"/><br />
					Display Screen: <xsl:value-of select="DisplayClass"/><br />
					Maint Screen: <xsl:value-of select="MaintClass"/><br />
				</td>
			</tr>
			<tr>
				<td style="text-align: right; vertical-align: baseline; font-weight: bold">
					Notes:
				</td>
				<td style="text-align: left; vertical-align: baseline; width: 100%">
					<xsl:value-of select="FileNotes"/>
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="field_list">
		<table border="1" cellpadding="5" width="100%">
			<tr>
				<td colspan="10" style="text-align: center; font-weight: bold; font-size: large">
					<xsl:value-of select="../FileHdr/FileName" />
				</td>
			</tr>
			<tr>
				<td colspan="10">
					<xsl:apply-templates select="../key_list"/>
				</td>
			</tr>
			<tr>
				<td style="text-align: center; vertical-align: baseline; font-weight: bold">Field Name</td>
				<td style="text-align: left; vertical-align: baseline; font-weight: bold">Class</td>
				<td style="text-align: left; vertical-align: baseline; font-weight: bold">Base Field</td>
				<td style="text-align: left; vertical-align: baseline; font-weight: bold">Max</td>
				<td style="text-align: left; vertical-align: baseline; font-weight: bold">Default</td>
				<td style="text-align: left; vertical-align: baseline; font-weight: bold; width: 50%">Description</td>
			</tr>
			<xsl:for-each select="FieldData">
			<tr>
				<td style="text-align: left; vertical-align: baseline; font-weight: bold">
					<xsl:value-of select="FieldName"/>
				</td>
				<td style="text-align: left; vertical-align: baseline">
					<xsl:value-of select="FieldClass"/>
				</td>
				<td style="text-align: left; vertical-align: baseline">
					<xsl:value-of select="BaseFieldName"/>
				</td>
				<td style="text-align: left; vertical-align: baseline">
					<xsl:value-of select="MaximumLength"/>
				</td>
				<td style="text-align: left; vertical-align: baseline">
					<xsl:value-of select="DefaultValue"/> <xsl:value-of select="InitialValue"/>
				</td>
				<td style="text-align: left; vertical-align: baseline; width: 50%">
					<xsl:value-of select="FieldDescription"/>
				</td>
			</tr>
			</xsl:for-each>
		</table>
	</xsl:template>

	<xsl:template match="key_list">
			<xsl:for-each select="KeyInfo">
				<span style="text-align: left; vertical-align: baseline; font-weight: bold">
					<xsl:value-of select="KeyName"/>
					(<xsl:value-of select="KeyType"/>) - 
				</span>
				<xsl:value-of select="KeyField1"/>
				<xsl:if test="KeyField2!=''">
					, <xsl:value-of select="KeyField2"/>
				</xsl:if>
				<xsl:if test="KeyField3!=''">
					, <xsl:value-of select="KeyField3"/>
				</xsl:if>
				<xsl:if test="KeyField4!=''">
					, <xsl:value-of select="KeyField4"/>
				</xsl:if>
				<xsl:if test="KeyField5!=''">
					, <xsl:value-of select="KeyField5"/>
				</xsl:if>
				<xsl:if test="KeyField6!=''">
					, <xsl:value-of select="KeyField6"/>
				</xsl:if>
				<xsl:if test="KeyField7!=''">
					, <xsl:value-of select="KeyField7"/>
				</xsl:if>
				<xsl:if test="KeyField8!=''">
					, <xsl:value-of select="KeyField8"/>
				</xsl:if>
				<xsl:if test="KeyField9!=''">
					, <xsl:value-of select="KeyField9"/>
				</xsl:if>
				<br />
			</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>

