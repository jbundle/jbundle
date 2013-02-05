<?xml version="1.0"?>
<!-- WARNING: Since Chrome does not support the import tag, you must run the flattening utility after changing -->
<xsl:stylesheet
 version="1.0"
 xmlns:xfm="http://www.w3.org/2000/12/xforms"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../../cocoon/base/mainstyles-base.xsl"/>

	<xsl:template match="full-screen">
		<html>
			<xsl:call-template name="html-head" />
			<body class="tundra">
				<script type="text/javascript" src="http://java.com/js/deployJava.js"></script>			
				<script type="text/javascript">dojo.back.init(jbundle.java.hashChange);</script>
				<xsl:apply-templates select="top-menu" />
				<table id="top-table" cellspacing="0" cellpadding="0">
					<tr valign="top">
							<xsl:apply-templates select="navigation-menu" />
						<td valign="top">
							<xsl:apply-templates select="content-area" />
						</td>
					</tr>
						<xsl:call-template name="navigation-corner" />
				</table>
				<xsl:apply-templates select="trailer" />
			</body>
		</html>
	</xsl:template>

	<xsl:template name="html-head">
		<head>
			<title>
				<xsl:value-of select="title"/>
			</title>
		<xsl:if test="meta-keywords!=''">
			<xsl:element name="meta">
				<xsl:attribute name="name">keywords</xsl:attribute>
				<xsl:attribute name="content">
					<xsl:value-of select="meta-keywords"/>
				</xsl:attribute>
			</xsl:element>
		</xsl:if>
			<xsl:element name="meta">
				<xsl:attribute name="name">description</xsl:attribute>
				<xsl:attribute name="content">
					<xsl:value-of select="meta-description"/>
				</xsl:attribute>
			</xsl:element>
		<xsl:if test="meta-redirect!=''">
			<xsl:element name="meta">
				<xsl:attribute name="http-equiv">REFRESH</xsl:attribute>
				<xsl:attribute name="content">0; URL=<xsl:value-of select="meta-redirect"/></xsl:attribute>
			</xsl:element>
		</xsl:if>
			
			<script type="text/javascript">
				djConfig = {
					isDebug: false,
					preventBackButtonFix: false
				};
			</script>

			<!-- script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/dojo/1.3/dojo/dojo.xd.js" djConfig="parseOnLoad: true"></script-->
			<script type="text/javascript" src="docs/styles/js/dojo/dojo.js" djConfig="parseOnLoad: true"></script>
			<style type="text/css">
		        @import "docs/styles/js/dijit/themes/tundra/tundra.css";
			</style>
			<link rel="stylesheet" type="text/css" href="org/jbundle/res/docs/styles/css/style.css" title="basicstyle" />
			<xsl:if test="/full-screen/content-area/Menus/Params/properties/entry[@key='css']!=''">
					<xsl:element name="link">
					<xsl:attribute name="rel">stylesheet</xsl:attribute>
					<xsl:attribute name="type">text/css</xsl:attribute>
					<xsl:attribute name="href"><xsl:value-of select="/full-screen/content-area/Menus/Params/properties/entry[@key='css']"></xsl:value-of></xsl:attribute>
					<xsl:attribute name="title">basicstyle</xsl:attribute>
					</xsl:element>
			</xsl:if>

			<script src="org/jbundle/res/docs/styles/js/jbundle/back.js" type="text/javascript"></script>

			<script src="org/jbundle/res/docs/styles/js/jbundle/main.js" type="text/javascript"></script>
			<script src="org/jbundle/res/docs/styles/js/jbundle/classes.js" type="text/javascript"></script>
			<script src="org/jbundle/res/docs/styles/js/jbundle/remote.js" type="text/javascript"></script>
			<script src="org/jbundle/res/docs/styles/js/jbundle/gui.js" type="text/javascript"></script>
			<script src="org/jbundle/res/docs/styles/js/jbundle/xml.js" type="text/javascript"></script>
			<script src="org/jbundle/res/docs/styles/js/jbundle/util.js" type="text/javascript"></script>
			<script src="org/jbundle/res/docs/styles/js/jbundle/java.js" type="text/javascript"></script>
			<script src="docs/styles/js/sha/sha1.js" type="text/javascript"></script>

			<script type="text/javascript">
			dojo.require("dijit.form.Button");
			dojo.require("dijit.form.TextBox");
			dojo.require("dijit.form.Textarea");
			dojo.require("dijit.form.CheckBox");
			dojo.require("dijit.form.ComboBox");
			dojo.require("dojo.parser");
			dojo.require("dojo.back");
			</script>  
		</head>
	</xsl:template>
	
	<!-- An icon button displays a linked button with an icon to the left -->
	<xsl:template name="button_command">
		<xsl:param name="name"></xsl:param>			<!-- Button name -->
		<xsl:param name="id"></xsl:param>			<!-- Button id -->
		<xsl:param name="description"></xsl:param>	<!-- Popup desc (alt) -->
		<xsl:param name="link"></xsl:param>			<!-- The command -->
		<xsl:param name="image"></xsl:param>		<!-- Image filename -->
		<xsl:param name="style"></xsl:param>		<!-- css -->
		<xsl:param name="class"></xsl:param>		<!-- css class -->
		<xsl:param name="height"></xsl:param>
		<xsl:param name="width"></xsl:param>
		<xsl:param name="type">Submit</xsl:param>	<!-- Submit/Reset/button(command) -->
		<xsl:param name="xform"></xsl:param>		<!-- Text color -->

		<xsl:element name="button">
			<xsl:if test="$id!=''">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$link!=''">
				<xsl:attribute name="onClick">
					jbundle.util.doButton('<xsl:value-of select="$link"/>');
				</xsl:attribute>
			</xsl:if>
 			<xsl:attribute name="dojoType">dijit.form.Button</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:choose>
 					<xsl:when test="$class!=''"><xsl:value-of select="$class"/></xsl:when>
 					<xsl:otherwise>button</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="type">button</xsl:attribute>
			<xsl:if test="$style!=''"><xsl:attribute name="style"><xsl:value-of select="$style"/></xsl:attribute></xsl:if>
	 		<xsl:if test="$image != '[none]'">
			<xsl:call-template name="image">
				<xsl:with-param name="image"><xsl:value-of select="$image"/></xsl:with-param>
				<xsl:with-param name="width"><xsl:value-of select="$width"/></xsl:with-param>
				<xsl:with-param name="height"><xsl:value-of select="$height"/></xsl:with-param>
				<xsl:with-param name="description"><xsl:value-of select="$description"/></xsl:with-param>
				<xsl:with-param name="type"><xsl:value-of select="$type"/></xsl:with-param>
			</xsl:call-template>
	 		</xsl:if>
	 		<xsl:if test="$name != '[none]'">
				<xsl:element name="span">
					<xsl:attribute name="class">desc</xsl:attribute>
					<xsl:choose>
					<xsl:when test="$name != ''">
						<xsl:value-of select="$name"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$image"/>
					</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<!-- An icon button displays a linked button with an icon to the left -->
	<xsl:template name="button_link">
		<xsl:param name="name"></xsl:param>			<!-- Button name -->
		<xsl:param name="id"></xsl:param>			<!-- Button id -->
		<xsl:param name="description"></xsl:param>	<!-- Popup desc (alt) -->
		<xsl:param name="link">.</xsl:param>		<!-- a href link -->
		<xsl:param name="image">Help</xsl:param>	<!-- Image filename -->
		<xsl:param name="style"></xsl:param>		<!-- css Style -->
		<xsl:param name="class"></xsl:param>		<!-- css class -->
		<xsl:param name="height"></xsl:param>
		<xsl:param name="width"></xsl:param>
		<xsl:param name="type">buttons</xsl:param>
		<xsl:param name="button">true</xsl:param>

		<xsl:element name="button">
			<xsl:if test="$id!=''">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$link!=''">
				<xsl:attribute name="onClick">
					jbundle.util.doLink('<xsl:value-of select="$link"/>');
				</xsl:attribute>
			</xsl:if>
 			<xsl:attribute name="dojoType">dijit.form.Button</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:choose>
 					<xsl:when test="$class!=''"><xsl:value-of select="$class"/></xsl:when>
 					<xsl:when test="$type!='button'">link</xsl:when>
 					<xsl:otherwise>button</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="type">button</xsl:attribute>
			<xsl:if test="$style!=''"><xsl:attribute name="style"><xsl:value-of select="$style"/></xsl:attribute></xsl:if>
	 		<xsl:if test="$image != '[none]'">
			<xsl:call-template name="image">
				<xsl:with-param name="image"><xsl:value-of select="$image"/></xsl:with-param>
				<xsl:with-param name="width"><xsl:value-of select="$width"/></xsl:with-param>
				<xsl:with-param name="height"><xsl:value-of select="$height"/></xsl:with-param>
				<xsl:with-param name="description"><xsl:value-of select="$description"/></xsl:with-param>
				<xsl:with-param name="type"><xsl:value-of select="$type"/></xsl:with-param>
			</xsl:call-template>
	 		</xsl:if>
	 		<xsl:if test="$name != '[none]'">
				<xsl:element name="span">
					<xsl:attribute name="class">desc</xsl:attribute>
					<xsl:choose>
					<xsl:when test="$name != ''">
						<xsl:value-of select="$name"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$image"/>
					</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	
	<!-- An icon image displays a button or icon with a link -->
	<xsl:template name="image_link">
		<xsl:param name="id"></xsl:param>			<!-- Button id -->
		<xsl:param name="description"></xsl:param>	<!-- Popup desc (alt) -->
		<xsl:param name="link">.</xsl:param>		<!-- a href link -->
		<xsl:param name="image"></xsl:param>	<!-- Image filename -->
		<xsl:param name="style"></xsl:param>		<!-- css Style -->
		<xsl:param name="class"></xsl:param>		<!-- css class -->
		<xsl:param name="height"></xsl:param>
		<xsl:param name="width"></xsl:param>
		<xsl:param name="type"></xsl:param>

		<xsl:element name="button">
			<xsl:if test="$id!=''">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$link!=''">
				<xsl:attribute name="onClick">
					jbundle.util.doLink('<xsl:value-of select="$link"/>');
				</xsl:attribute>
			</xsl:if>
 			<xsl:attribute name="dojoType">dijit.form.Button</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:choose>
 					<xsl:when test="$class!=''"><xsl:value-of select="$class"/></xsl:when>
 					<xsl:when test="$type!='button'">link</xsl:when>
 					<xsl:otherwise>button</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="type">button</xsl:attribute>
			<xsl:if test="$style!=''"><xsl:attribute name="style"><xsl:value-of select="$style"/></xsl:attribute></xsl:if>
	 		<xsl:if test="$image != '[none]'">
				<xsl:call-template name="image">
					<xsl:with-param name="image"><xsl:value-of select="$image" /></xsl:with-param>
					<xsl:with-param name="width"><xsl:value-of select="$width"/></xsl:with-param>
					<xsl:with-param name="height"><xsl:value-of select="$height"/></xsl:with-param>
					<xsl:with-param name="description"><xsl:value-of select="$description"/></xsl:with-param>
					<xsl:with-param name="type"><xsl:value-of select="$type"/></xsl:with-param>
				</xsl:call-template>
	 		</xsl:if>
		</xsl:element>

	</xsl:template>

	<!-- A menu button -->
	<xsl:template match="menu-item">
		<xsl:call-template name="button_link">
			<xsl:with-param name="name"><xsl:value-of select="name" /></xsl:with-param>
			<xsl:with-param name="id"><xsl:value-of select="name" /></xsl:with-param>
			<xsl:with-param name="description"><xsl:value-of select="description" /></xsl:with-param>
			<xsl:with-param name="link"><xsl:value-of select="link" /></xsl:with-param>
			<xsl:with-param name="image"><xsl:value-of select="image" /></xsl:with-param>
			<xsl:with-param name="type">button</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
