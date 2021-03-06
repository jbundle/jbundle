<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<office:document-content
			xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
			xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0"
			xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
			xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
			xmlns:draw="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0"
			xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0"
			xmlns:xlink="http://www.w3.org/1999/xlink"
			xmlns:dc="http://purl.org/dc/elements/1.1/"
			xmlns:meta="urn:oasis:names:tc:opendocument:xmlns:meta:1.0"
			xmlns:number="urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0"
			xmlns:svg="urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0"
			xmlns:chart="urn:oasis:names:tc:opendocument:xmlns:chart:1.0"
			xmlns:dr3d="urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0"
			xmlns:math="http://www.w3.org/1998/Math/MathML"
			xmlns:form="urn:oasis:names:tc:opendocument:xmlns:form:1.0"
			xmlns:script="urn:oasis:names:tc:opendocument:xmlns:script:1.0"
			xmlns:ooo="http://openoffice.org/2004/office"
			xmlns:ooow="http://openoffice.org/2004/writer"
			xmlns:oooc="http://openoffice.org/2004/calc"
			xmlns:dom="http://www.w3.org/2001/xml-events"
			xmlns:xforms="http://www.w3.org/2002/xforms"
			xmlns:xsd="http://www.w3.org/2001/XMLSchema"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			office:version="1.0">
			<office:scripts />
			<office:font-face-decls>
				<style:font-face style:name="Arial1"
					svg:font-family="Arial" style:font-pitch="variable" />
				<style:font-face style:name="Lucidasans"
					svg:font-family="Lucidasans" style:font-pitch="variable" />
				<style:font-face style:name="Arial"
					svg:font-family="Arial" style:font-family-generic="swiss"
					style:font-pitch="variable" />
			</office:font-face-decls>
			<office:automatic-styles>
				<style:style style:name="co1"
					style:family="table-column">
					<style:table-column-properties
						fo:break-before="auto" style:column-width="0.8925in" />
				</style:style>
				<style:style style:name="ro1"
					style:family="table-row">
					<style:table-row-properties
						style:row-height="0.1681in" fo:break-before="auto"
						style:use-optimal-row-height="true" />
				</style:style>
				<style:style style:name="ta1" style:family="table"
					style:master-page-name="Default">
					<style:table-properties table:display="true"
						style:writing-mode="lr-tb" />
				</style:style>
			</office:automatic-styles>
			<office:body>
				<office:spreadsheet>
					<table:table table:name="Sheet1"
						table:style-name="ta1" table:print="false">
						<table:table-column table:style-name="co1"
							table:number-columns-repeated="2"
							table:default-cell-style-name="Default" />
						
						<table:table-row table:style-name="ro1">
						<xsl:for-each select="/file/*[1]">
						<xsl:for-each select="@*">
							<table:table-cell
								office:value-type="string">
							<text:p><xsl:value-of select="name()" /></text:p>
							</table:table-cell>
						</xsl:for-each>
						<xsl:for-each select="*">
							<table:table-cell
								office:value-type="string">
							<text:p><xsl:value-of select="name()" /></text:p>
							</table:table-cell>
						</xsl:for-each>
						</xsl:for-each>
						</table:table-row>

						<xsl:for-each select="/file/*">
						<table:table-row table:style-name="ro1">
						<xsl:for-each select="@*">
							<table:table-cell
								office:value-type="string">
							<text:p><xsl:value-of select="." /></text:p>
							</table:table-cell>
						</xsl:for-each>
						<xsl:for-each select="*">
							<table:table-cell
								office:value-type="string">
							<text:p><xsl:value-of select="." /></text:p>
							</table:table-cell>
						</xsl:for-each>
						</table:table-row>
						</xsl:for-each>

					</table:table>
					<table:table table:name="Sheet2"
						table:style-name="ta1" table:print="false">
						<table:table-column table:style-name="co1"
							table:default-cell-style-name="Default" />
						<table:table-row table:style-name="ro1">
							<table:table-cell />
						</table:table-row>
					</table:table>
					<table:table table:name="Sheet3"
						table:style-name="ta1" table:print="false">
						<table:table-column table:style-name="co1"
							table:default-cell-style-name="Default" />
						<table:table-row table:style-name="ro1">
							<table:table-cell />
						</table:table-row>
					</table:table>
				</office:spreadsheet>
			</office:body>
		</office:document-content>
	</xsl:template>
</xsl:stylesheet>