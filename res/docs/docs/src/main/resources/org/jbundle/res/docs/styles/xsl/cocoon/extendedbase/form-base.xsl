<?xml version="1.0"?>
<xsl:stylesheet
 version="1.0"
 xmlns:xfm="http://www.w3.org/2000/12/xforms"
 xmlns:fo="http://www.w3.org/1999/XSL/Format"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 

	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="form">
		<xsl:element name="form">
			<xsl:attribute name="method">
				<xsl:value-of select="xform/submission/@method" />
			</xsl:attribute>
			<xsl:attribute name="action">
				<xsl:value-of select="xform/submission/@action" />
			</xsl:attribute>
			<xsl:attribute name="id">
				<xsl:value-of select="xform/submission/@id" />
			</xsl:attribute>
		
			<!-- note: the br is in this div due to an ie xslt bug -->
		    <xsl:element name="div">
		    	<xsl:attribute name="id">status-area</xsl:attribute>
				<xsl:if test="//status-text!=''">
			    	<xsl:attribute name="class"><xsl:value-of  select="//status-text/error" /></xsl:attribute>
				    	<xsl:value-of  select="//status-text/text" />
				</xsl:if>
				<br />
			</xsl:element>

			<xsl:if test="count(xform/*)>12">
				<table class="toolbars">
					<tr>
						<xsl:for-each select="toolbars/*">
							<td><xsl:apply-templates select="." /></td>
						</xsl:for-each>
					</tr>
				</table>
			</xsl:if>

			<xsl:for-each select="hidden-params/*">
				<xsl:element name="input">
					<xsl:attribute name="type">hidden</xsl:attribute>
					<xsl:attribute name="name"><xsl:value-of select="@name" /></xsl:attribute>
					<xsl:attribute name="value"><xsl:value-of select="." /></xsl:attribute>
					<xsl:if test="@name='objectID'">
						<xsl:attribute name="id">objectID</xsl:attribute>
					</xsl:if>
				</xsl:element>
			</xsl:for-each>
		
			<table>
				<xsl:for-each select="xform/*">
					<tr>
						<td align="right" valign="top">
							<xsl:if test="xfm:caption!='' and name(.)!='xfm:submit' and name(.)!='xfm:trigger'">
								<xsl:value-of select="xfm:caption" />
							</xsl:if>
						</td>
						<td align="baseline"><xsl:apply-templates select="." /></td>
					</tr>
				</xsl:for-each>
			</table>
		
			<table class="toolbars">
				<tr>
					<xsl:for-each select="toolbars/*">
						<td><xsl:apply-templates select="." /></td>
					</xsl:for-each>
				</tr>
			</table>
		
		</xsl:element>
	</xsl:template>
	
	<!-- Don't output the xform's data, use it in your forms -->
	<xsl:template match="xform">
	</xsl:template>
	<xsl:template match="hidden-params">
	</xsl:template>
	<xsl:template match="data">
	</xsl:template>

	<!-- Translate an icon to an HTML img -->
	<xsl:template match="fo:external-graphic">
		<xsl:call-template name="image_link">
			<xsl:with-param name="image"><xsl:value-of select="@src" /></xsl:with-param>
			<xsl:with-param name="height"><xsl:value-of select="@height" /></xsl:with-param>
			<xsl:with-param name="width"><xsl:value-of select="@width" /></xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- Translate an icon to an HTML img -->
	<xsl:template match="fo:block">
			<xsl:value-of select="text()" />
	</xsl:template>

	<!-- Translate an XFORMS textbox to an HTML textbox -->
	<xsl:template match="xfm:textbox">
	<xsl:choose>
		<xsl:when test="@rows!=''">
		<xsl:call-template name="text_area">
			<xsl:with-param name="type">text</xsl:with-param>
			<xsl:with-param name="name"><xsl:value-of select="@ref" /></xsl:with-param>
			<xsl:with-param name="size"><xsl:value-of select="@cols" /></xsl:with-param>
			<xsl:with-param name="maxlength"><xsl:value-of select="@cols" /></xsl:with-param>
			<xsl:with-param name="rows"><xsl:value-of select="@rows" /></xsl:with-param>
			<xsl:with-param name="fieldname"><xsl:value-of select="@ref" /></xsl:with-param>
			<xsl:with-param name="style"><xsl:value-of select="@style" /></xsl:with-param>
			<xsl:with-param name="xform"><xsl:value-of select="@xform" /></xsl:with-param>
			<xsl:with-param name="richtype"><xsl:value-of select="@type" /></xsl:with-param>
		</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
		<xsl:call-template name="text_box">
			<xsl:with-param name="type">text</xsl:with-param>
			<xsl:with-param name="name"><xsl:value-of select="@ref" /></xsl:with-param>
			<xsl:with-param name="size"><xsl:value-of select="@cols" /></xsl:with-param>
			<xsl:with-param name="maxlength"><xsl:value-of select="@cols" /></xsl:with-param>
			<xsl:with-param name="fieldname"><xsl:value-of select="@ref" /></xsl:with-param>
			<xsl:with-param name="style"><xsl:value-of select="@style" /></xsl:with-param>
			<xsl:with-param name="xform"><xsl:value-of select="@xform" /></xsl:with-param>
			<xsl:with-param name="richtype"><xsl:value-of select="@type" /></xsl:with-param>
		</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
	</xsl:template>

	<!-- Translate an XFORMS combobox to an HTML combobox -->
	<xsl:template match="xfm:exclusiveSelect">
		<xsl:call-template name="combo_box">
			<xsl:with-param name="name"><xsl:value-of select="@ref" /></xsl:with-param>
			<xsl:with-param name="fieldname"><xsl:value-of select="@ref" /></xsl:with-param>
			<xsl:with-param name="style"><xsl:value-of select="@style" /></xsl:with-param>
			<xsl:with-param name="xform"><xsl:value-of select="@xform" /></xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- Translate an XFORMS password to an HTML password -->
	<xsl:template match="xfm:secret">
		<xsl:call-template name="text_box">
			<xsl:with-param name="type">password</xsl:with-param>
			<xsl:with-param name="name"><xsl:value-of select="@ref" /></xsl:with-param>
			<xsl:with-param name="size"><xsl:value-of select="@cols" /></xsl:with-param>
			<xsl:with-param name="maxlength"><xsl:value-of select="@cols" /></xsl:with-param>
			<xsl:with-param name="fieldname"><xsl:value-of select="@ref" /></xsl:with-param>
			<xsl:with-param name="xform"><xsl:value-of select="@xform" /></xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- Translate an XFORMS checkbox to an HTML checkbox -->
	<xsl:template match="xfm:checkbox">
		<xsl:call-template name="text_box">
			<xsl:with-param name="type">checkbox</xsl:with-param>
			<xsl:with-param name="name"><xsl:value-of select="@ref" /></xsl:with-param>
			<xsl:with-param name="fieldname"><xsl:value-of select="@ref" /></xsl:with-param>
			<xsl:with-param name="xform"><xsl:value-of select="@xform" /></xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- TODO (don) xfm:image doesn't really exist in xforms -->
	<xsl:template match="xfm:image">
		<xsl:variable name="xform" select="@xform" />
		<xsl:variable name="ref" select="@ref" />
		<xsl:call-template name="image_link">
			<xsl:with-param name="image"><xsl:value-of select="//xform[@id=$xform]/../../data/*[name()=$ref]" /></xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- An icon button displays a linked button with an icon to the left -->
	<xsl:template name="text_box">
		<xsl:param name="type">text</xsl:param>		<!-- Control type -->
		<xsl:param name="name"></xsl:param>	<!-- Popup desc (alt) -->
		<xsl:param name="size">50</xsl:param>		<!-- a href link -->
		<xsl:param name="maxlength">50</xsl:param>	<!-- Image filename -->
		<xsl:param name="fieldname"></xsl:param>	<!-- Text color -->
		<xsl:param name="style"></xsl:param>	<!-- css -->
		<xsl:param name="xform"></xsl:param>	<!-- Text color -->
		<xsl:param name="richtype"></xsl:param>		<!-- Rich control type -->

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
						50
			 		</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="maxlength">
				<xsl:choose>
					<xsl:when test="$maxlength!=''">
						<xsl:value-of select="$maxlength"/>
  					</xsl:when>
 					<xsl:otherwise>
						50
			 		</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="$style!=''">
				<xsl:attribute name="style">
					<xsl:value-of select="$style"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="value">
				<xsl:value-of select="//xform[@id=$xform]/../../data/*[name()=$fieldname]" />
			</xsl:attribute>
			<xsl:if test="$type='checkbox'">
				<xsl:if test="//xform[@id=$xform]/../../data/*[name()=$fieldname]='true' or //xform[@id=$xform]/../../data/*[name()=$fieldname]='Y'">
					<xsl:attribute name="checked">
					</xsl:attribute>
				</xsl:if>
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<!-- An icon button displays a linked button with an icon to the left -->
	<xsl:template name="text_area">
		<xsl:param name="type">text</xsl:param>		<!-- Control type -->
		<xsl:param name="name"></xsl:param>	<!-- Popup desc (alt) -->
		<xsl:param name="size">50</xsl:param>		<!-- columns -->
		<xsl:param name="rows">6</xsl:param>		<!-- rows -->
		<xsl:param name="maxlength">32000</xsl:param>	<!-- Max text length -->
		<xsl:param name="fieldname"></xsl:param>	<!-- Name -->
		<xsl:param name="style"></xsl:param>	<!-- css -->
		<xsl:param name="xform"></xsl:param>	<!-- Text color -->
		<xsl:param name="richtype"></xsl:param>		<!-- Rich control type -->
		<xsl:element name="textarea">
			<xsl:attribute name="type">
				<xsl:value-of select="$type" />
			</xsl:attribute>
			<xsl:attribute name="name">
				<xsl:value-of select="$name"/>
			</xsl:attribute>
			<xsl:attribute name="cols">
				<xsl:choose>
					<xsl:when test="$size!=''">
						<xsl:value-of select="$size"/>
  					</xsl:when>
 					<xsl:otherwise>
						50
			 		</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="maxlength">
				<xsl:choose>
					<xsl:when test="$maxlength!=''">
						<xsl:value-of select="$maxlength"/>
  					</xsl:when>
 					<xsl:otherwise>
						32000
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
				<xsl:value-of select="//xform[@id=$xform]/../../data/*[name()=$fieldname]" />
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
				<xsl:value-of select="//xform[@id=$xform]/../../data/*[name()=$fieldname]" />
			</xsl:attribute>

			<xsl:for-each select="xfm:item">
				<xsl:element name="option">
					<xsl:attribute name="value"><xsl:value-of select="@value"/></xsl:attribute>
					<xsl:if test="//xform[@id=$xform]/../../data/*[name()=$fieldname]=@value">
						<xsl:attribute name="selected">
						</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>

		</xsl:element>
	</xsl:template>

</xsl:stylesheet>
