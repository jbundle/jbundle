<?xml version="1.0"?>
<!-- WARNING: Since Chrome does not support the import tag, you must run the flattening utility after changing -->
<xsl:stylesheet
 version="1.0"
 xmlns:xfm="http://www.w3.org/2000/12/xforms"
 xmlns:fo="http://www.w3.org/1999/XSL/Format"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="mainstyles-ajax.xsl"/>
	<xsl:import href="../../cocoon/extendedbase/form-base.xsl"/>

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
			<xsl:attribute name="onsubmit">return jbundle.util.doButton('Submit');</xsl:attribute>
		
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
	
<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 
	<!-- An icon button displays a linked button with an icon to the left -->
	<xsl:template name="text_box">
		<xsl:param name="type">text</xsl:param>		<!-- Control type -->
		<xsl:param name="name"></xsl:param>			<!-- Popup desc (alt) -->
		<xsl:param name="size">50</xsl:param>		<!-- a href link -->
		<xsl:param name="maxlength">50</xsl:param>	<!-- Image filename -->
		<xsl:param name="fieldname"></xsl:param>	<!-- Text color -->
		<xsl:param name="style"></xsl:param>		<!-- css -->
		<xsl:param name="xform"></xsl:param>		<!-- Text color -->
		<xsl:param name="richtype"></xsl:param>		<!-- Rich control type -->
		<xsl:element name="input">
			<xsl:attribute name="type">
				<xsl:value-of select="$type" />
			</xsl:attribute>
			<xsl:choose>
				<xsl:when test="$type='checkbox'">
					<xsl:attribute name="dojoType">dijit.form.CheckBox</xsl:attribute>
 					</xsl:when>
					<xsl:otherwise>
					<xsl:attribute name="dojoType">dijit.form.TextBox</xsl:attribute>
		 		</xsl:otherwise>
			</xsl:choose>
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
		<xsl:param name="type">text</xsl:param>			<!-- Button name -->
		<xsl:param name="name"></xsl:param>	<!-- Popup desc (alt) -->
		<xsl:param name="size">50</xsl:param>		<!-- columns -->
		<xsl:param name="rows">6</xsl:param>		<!-- rows -->
		<xsl:param name="maxlength">32000</xsl:param>	<!-- Max text length -->
		<xsl:param name="fieldname"></xsl:param>	<!-- Name -->
		<xsl:param name="style"></xsl:param>	<!-- css -->
		<xsl:param name="xform"></xsl:param>	<!-- Text color -->
		<xsl:param name="richtype"></xsl:param>		<!-- Rich control type -->
		
		<xsl:variable name="dojotype"><xsl:if test="$richtype!='Editor'">dijit.form.Textarea</xsl:if><xsl:if test="$richtype='Editor'">dijit.Editor</xsl:if></xsl:variable>

		<xsl:if test="$richtype!='Editor'">
		<xsl:element name="textarea">
			<xsl:attribute name="type">
				<xsl:value-of select="$type" />
			</xsl:attribute>
			<xsl:attribute name="dojoType"><xsl:value-of select="$dojotype"/></xsl:attribute>
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
		</xsl:if>
		
		<xsl:if test="$richtype='Editor'">
		<xsl:element name="div">
			<xsl:attribute name="dojoType"><xsl:value-of select="$dojotype"/></xsl:attribute>
			<xsl:attribute name="name">
				<xsl:value-of select="$name"/>
			</xsl:attribute>
			<xsl:attribute name="class">jbundle.Editor</xsl:attribute>
				<xsl:apply-templates select="//xform[@id=$xform]/../../data/*[name()=$fieldname]" />
		</xsl:element>
		</xsl:if>
		
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
			<xsl:attribute name="dojoType">dijit.form.ComboBox</xsl:attribute>
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
