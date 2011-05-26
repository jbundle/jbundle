<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xfm="http://www.w3.org/2000/12/xforms"
                 version="1.0">

<xsl:template match="/">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="form">
<html>
<body>

<form action="formtest.xml" method="get">
<table>
<xsl:apply-templates />
</table>
<input type="submit"/>
</form>
</body>
</html>
</xsl:template>

<!-- Don't output the xform's data, use it in your forms -->
<xsl:template match="xform">
</xsl:template>

<xsl:template match="h1">
<h1><xsl:apply-templates /></h1>
</xsl:template>

	<!-- Translate an XFORMS textbox to an HTML textbox -->
	<xsl:template match="xfm:textbox">
		<tr>
		<td align="right" valign="top">
		<xsl:value-of select="xfm:caption" />
		</td>
		<td align="baseline">
							<xsl:choose>
								<xsl:when test="@rows!=''">
		<xsl:call-template name="text_area">
			<xsl:with-param name="type">text</xsl:with-param>
			<xsl:with-param name="name"><xsl:value-of select="name" /></xsl:with-param>
			<xsl:with-param name="size"><xsl:value-of select="@cols" /></xsl:with-param>
			<xsl:with-param name="maxlength"><xsl:value-of select="@cols" /></xsl:with-param>
			<xsl:with-param name="rows"><xsl:value-of select="@rows" /></xsl:with-param>
			<xsl:with-param name="fieldname"><xsl:value-of select="@ref" /></xsl:with-param>
			<xsl:with-param name="style"><xsl:value-of select="@style" /></xsl:with-param>
			<xsl:with-param name="xform"><xsl:value-of select="@xform" /></xsl:with-param>
		</xsl:call-template>
  								</xsl:when>
 								<xsl:otherwise>
		<xsl:call-template name="text_box">
			<xsl:with-param name="type">text</xsl:with-param>
			<xsl:with-param name="name"><xsl:value-of select="name" /></xsl:with-param>
			<xsl:with-param name="size"><xsl:value-of select="@cols" /></xsl:with-param>
			<xsl:with-param name="maxlength"><xsl:value-of select="@cols" /></xsl:with-param>
			<xsl:with-param name="fieldname"><xsl:value-of select="@ref" /></xsl:with-param>
			<xsl:with-param name="style"><xsl:value-of select="@style" /></xsl:with-param>
			<xsl:with-param name="xform"><xsl:value-of select="@xform" /></xsl:with-param>
		</xsl:call-template>
			 					</xsl:otherwise>
							</xsl:choose>
		</td>
		</tr>
	</xsl:template>

	<!-- Translate an XFORMS combobox to an HTML combobox -->
	<xsl:template match="xfm:exclusiveSelect">
		<tr>
		<td align="right" valign="top">
		<xsl:value-of select="xfm:caption" />
		</td>
		<td align="baseline">
		<xsl:call-template name="combo_box">
			<xsl:with-param name="name"><xsl:value-of select="name" /></xsl:with-param>
			<xsl:with-param name="fieldname"><xsl:value-of select="@ref" /></xsl:with-param>
			<xsl:with-param name="style"><xsl:value-of select="@style" /></xsl:with-param>
			<xsl:with-param name="xform"><xsl:value-of select="@xform" /></xsl:with-param>
		</xsl:call-template>
		</td>
		</tr>
	</xsl:template>

	<!-- Translate an XFORMS combobox to an HTML combobox -->
	<xsl:template match="xfm:button">
		<tr>
		<td align="right" valign="top">
		
		</td>
		<td align="baseline">
		<xsl:call-template name="button_command">
			<xsl:with-param name="name"><xsl:value-of select="name" /></xsl:with-param>
			<xsl:with-param name="fieldname"><xsl:value-of select="@ref" /></xsl:with-param>
			<xsl:with-param name="style"><xsl:value-of select="@style" /></xsl:with-param>
			<xsl:with-param name="xform"><xsl:value-of select="@xform" /></xsl:with-param>
		</xsl:call-template>
		</td>
		</tr>
	</xsl:template>

	<!-- Translate an XFORMS password to an HTML password -->
	<xsl:template match="xfm:secret">
		<tr>
		<td align="right" valign="top">
		<xsl:value-of select="xfm:caption" />
		</td>
		<td align="baseline">
		<xsl:call-template name="text_box">
			<xsl:with-param name="type">password</xsl:with-param>
			<xsl:with-param name="name"><xsl:value-of select="name" /></xsl:with-param>
			<xsl:with-param name="size"><xsl:value-of select="@cols" /></xsl:with-param>
			<xsl:with-param name="maxlength"><xsl:value-of select="@cols" /></xsl:with-param>
			<xsl:with-param name="fieldname"><xsl:value-of select="@ref" /></xsl:with-param>
			<xsl:with-param name="xform"><xsl:value-of select="@xform" /></xsl:with-param>
		</xsl:call-template>
		</td>
		</tr>
	</xsl:template>

	<!-- Translate an XFORMS checkbox to an HTML checkbox -->
	<xsl:template match="xfm:checkbox">
		<tr>
		<td align="right" valign="top">
		<xsl:value-of select="xfm:caption" />
		</td>
		<td align="baseline">
		<xsl:call-template name="text_box">
			<xsl:with-param name="type">checkbox</xsl:with-param>
			<xsl:with-param name="name"><xsl:value-of select="name" /></xsl:with-param>
			<xsl:with-param name="fieldname"><xsl:value-of select="@ref" /></xsl:with-param>
			<xsl:with-param name="xform"><xsl:value-of select="@xform" /></xsl:with-param>
		</xsl:call-template>
		</td>
		</tr>
	</xsl:template>

	<!-- An icon button displays a linked button with an icon to the left -->
	<xsl:template name="text_box">
		<xsl:param name="type">text</xsl:param>			<!-- Button name -->
		<xsl:param name="name"></xsl:param>	<!-- Popup desc (alt) -->
		<xsl:param name="size">30</xsl:param>		<!-- a href link -->
		<xsl:param name="maxlength">30</xsl:param>	<!-- Image filename -->
		<xsl:param name="fieldname"></xsl:param>	<!-- Text color -->
		<xsl:param name="style"></xsl:param>	<!-- css -->
		<xsl:param name="xform"></xsl:param>	<!-- Text color -->
		<xsl:element name="input">
			<xsl:attribute name="type">
				<xsl:value-of select="$type" />
			</xsl:attribute>
			<xsl:attribute name="name">
				<xsl:value-of select="$name"/>
			</xsl:attribute>
			<xsl:attribute name="size">
				<xsl:choose>
					<xsl:when test="$size!=''">
						<xsl:value-of select="$size"/>
  					</xsl:when>
 					<xsl:otherwise>
						30
			 		</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="maxlength">
				<xsl:choose>
					<xsl:when test="$maxlength!=''">
						<xsl:value-of select="$maxlength"/>
  					</xsl:when>
 					<xsl:otherwise>
						30
			 		</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="$style!=''">
				<xsl:attribute name="style">
					<xsl:value-of select="$style"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="value">
				<xsl:value-of select="//xform[@id=$xform]/instance/*[name()=$fieldname]" />
			</xsl:attribute>
			<xsl:if test="$type='checkbox'">
				<xsl:if test="//xform[@id=$xform]/instance/*[name()=$fieldname]='true'">
					<xsl:attribute name="checked">
					</xsl:attribute>
				</xsl:if>
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<!-- An icon button displays a linked button with an icon to the left -->
	<xsl:template name="text_area">
		<xsl:param name="type">text</xsl:param>			<!-- Button name -->
		<xsl:param name="name"></xsl:param>	<!-- Popup desc (alt) -->
		<xsl:param name="size">30</xsl:param>		<!-- a href link -->
		<xsl:param name="rows">3</xsl:param>		<!-- a href link -->
		<xsl:param name="maxlength">30</xsl:param>	<!-- Image filename -->
		<xsl:param name="fieldname"></xsl:param>	<!-- Text color -->
		<xsl:param name="style"></xsl:param>	<!-- css -->
		<xsl:param name="xform"></xsl:param>	<!-- Text color -->
		<xsl:element name="textarea">
			<xsl:attribute name="type">
				<xsl:value-of select="$type" />
			</xsl:attribute>
			<xsl:attribute name="name">
				<xsl:value-of select="$name"/>
			</xsl:attribute>
			<xsl:attribute name="size">
				<xsl:choose>
					<xsl:when test="$size!=''">
						<xsl:value-of select="$size"/>
  					</xsl:when>
 					<xsl:otherwise>
						30
			 		</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="maxlength">
				<xsl:choose>
					<xsl:when test="$maxlength!=''">
						<xsl:value-of select="$maxlength"/>
  					</xsl:when>
 					<xsl:otherwise>
						30
			 		</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="rows">
				<xsl:value-of select="$rows" />
			</xsl:attribute>
			<xsl:if test="$style!=''">
				<xsl:attribute name="style">
					<xsl:value-of select="$style"/>
				</xsl:attribute>
			</xsl:if>
				<xsl:value-of select="//xform[@id=$xform]/instance/*[name()=$fieldname]" />
		</xsl:element>
	</xsl:template>

	<!-- An icon button displays a linked button with an icon to the left -->
	<xsl:template name="combo_box">
		<xsl:param name="name"></xsl:param>	<!-- Popup desc (alt) -->
		<xsl:param name="fieldname"></xsl:param>	<!-- Text color -->
		<xsl:param name="style"></xsl:param>	<!-- css -->
		<xsl:param name="xform"></xsl:param>	<!-- Text color -->
		<xsl:element name="select">
			<xsl:attribute name="name">
				<xsl:value-of select="@ref"/>
			</xsl:attribute>
			<xsl:if test="@style!=''">
				<xsl:attribute name="style">
					<xsl:value-of select="@style"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="value">
				<xsl:value-of select="//xform[@id=$xform]/instance/*[name()=$fieldname]" />
			</xsl:attribute>

			<xsl:for-each select="xfm:item">
			<xsl:element name="option">
				<xsl:if test="//xform[@id=$xform]/instance/*[name()=$fieldname]=@value">
					<xsl:attribute name="selected">
					</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="."/>
			</xsl:element>
			</xsl:for-each>

		</xsl:element>
	</xsl:template>

	<!-- An icon button displays a linked button with an icon to the left -->
	<xsl:template name="button_command">
		<xsl:param name="name"></xsl:param>	<!-- Popup desc (alt) -->
		<xsl:param name="fieldname"></xsl:param>	<!-- Text color -->
		<xsl:param name="style"></xsl:param>	<!-- css -->
		<xsl:param name="xform"></xsl:param>	<!-- Text color -->
		<xsl:element name="input">
			<xsl:attribute name="type">
				submit
			</xsl:attribute>
			<xsl:attribute name="name">
				<xsl:value-of select="@ref"/>
			</xsl:attribute>
			<xsl:if test="@style!=''">
				<xsl:attribute name="style">
					<xsl:value-of select="@style"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="value">
				<xsl:value-of select="//xform[@id=$xform]/instance/*[name()=$fieldname]" />
			</xsl:attribute>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>

