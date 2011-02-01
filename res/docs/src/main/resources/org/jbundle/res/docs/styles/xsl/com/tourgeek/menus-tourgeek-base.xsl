<?xml version="1.0"?>

<!-- The base menu stylesheet for all html transformations -->
<!-- NOTE: This stylesheet should not be imported from any stylesheets except menus -->
<!-- NOTE: You MUST also import the correct 'mainstyles' stylesheet before importing this one -->

<xsl:stylesheet version="1.0" xmlns:xfm="http://www.w3.org/2000/12/xforms" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="menu_list">
		<table style="border: 0; width: 100%;" cellspacing="10">
			<xsl:for-each select="Menus">
				<xsl:variable name="pos"><xsl:value-of select="position()" /></xsl:variable>
				<tr>
					<td align="left" valign="top">
						<xsl:if test="position()>1"><hr /></xsl:if>
						<xsl:apply-templates select="../*[position()=$pos]" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>

</xsl:stylesheet>
