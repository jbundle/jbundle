<?xml version="1.0"?>
<!-- WARNING: Since Chrome does not support the import tag, you must run the flattening utility after changing -->
<xsl:stylesheet
 version="1.0"
 xmlns:xfm="http://www.w3.org/2000/12/xforms"
 xmlns:fo="http://www.w3.org/1999/XSL/Format"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- Import inlined from: mainstyles-ajax.xsl - Do not modify this generated file.-->
<!-- WARNING: Since Chrome does not support the import tag, you must run the flattening utility after changing -->

<!-- Import inlined from: ../../cocoon/base/mainstyles-base.xsl - Do not modify this generated file.-->

<!-- The base main stylesheet for all html transformations -->
<!-- NOTE: This stylesheet should not be imported from any stylesheets except mainstyles -->

<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 


	<xsl:template match="top-menu">
		<xsl:if test="(not(//full-screen/params/menubars)) or (//full-screen/params/menubars!='No')">
		<!-- Put the top menu bar here -->
			<table cellspacing="0" cellpadding="0" class="top-menu" id="menubar">
				<tr>
					<td align="left">
						<table cellspacing="0" cellpadding="0" border="0">
						<tr>
						<xsl:for-each select="menu-item">
							<td>
							<xsl:apply-templates select="." />
							</td>
						</xsl:for-each>
						</tr>
						</table>
					</td>
					<td class="title" align="center">
						<span class="spantitle" id="title">
							<xsl:value-of select="/full-screen/title" />
						</span>
					</td>
					<td align="right">
						<table cellspacing="0" cellpadding="0" border="0">
						<tr>
						<td class="title">
							<span id="userName" class="spantitle">
								<xsl:value-of select="/full-screen/params/user" />
							</span>&#160;&#160;
						</td>
						<xsl:for-each select="help-item/menu-item">
							<td class="help-button">
							<xsl:apply-templates select="." />
							</td>
						</xsl:for-each>
						</tr>
						</table>
					</td>
				</tr>
				<tr style="background-color: black">
					<td style="width: 100%;" height="1" colspan="7">
					</td>
				</tr>
			</table>
		</xsl:if>
	</xsl:template>

	<!-- HTML Heading area -->
	
	<xsl:template name="more-heading">
	<!-- Override this to add more heading lines or script -->
	</xsl:template>

	<xsl:template match="navigation-menu">
		<xsl:if test="(count(/full-screen/params/navmenus)=0) or (/full-screen/params/navmenus!='No')">
		<!-- Put the side navigation bar here -->
		<xsl:element name="th">
			<xsl:attribute name="id">navStart</xsl:attribute>
			<xsl:attribute name="style">background-image: url(images/graphics/NavBack.gif);</xsl:attribute>
			<xsl:attribute name="class">nav<xsl:value-of select="/full-screen/params/navmenus" />Start</xsl:attribute>
			<xsl:call-template name="navigation-start">
			</xsl:call-template>
			<br/>
			<div class="title">Navigation<br/></div><hr/>
			<xsl:for-each select="navigation-item">
				<xsl:apply-templates select="." />
				<br/>
			</xsl:for-each>
		</xsl:element>
			<xsl:call-template name="navigation-end">
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="trailer">
		<xsl:if test="(count(/full-screen/params/trailers)=0) or (/full-screen/params/trailers!='No')">
		<xsl:variable name="domain"><xsl:value-of select="/full-screen/params/domain"/></xsl:variable>
		<xsl:variable name="dotcom">
	<xsl:choose>
		      <xsl:when test="contains(substring-after($domain,'.'), '.') and contains(substring-after(substring-after($domain,'.'),'.'), '.')"><xsl:value-of select="substring-after(substring-after($domain,'.'),'.')"/></xsl:when>
		      <xsl:when test="contains($domain, '.')"><xsl:value-of select="substring-after($domain,'.')"/></xsl:when>
		      <xsl:otherwise><xsl:value-of select="$domain"/></xsl:otherwise>
    </xsl:choose>
		</xsl:variable>
		<xsl:variable name="name">
	<xsl:choose>
      <xsl:when test="contains($dotcom, '.')"><xsl:value-of select="substring-before($dotcom,'.')"/></xsl:when>
      <xsl:otherwise><xsl:value-of select="$dotcom"/></xsl:otherwise>
    </xsl:choose>
		</xsl:variable>
<!-- Put the trailer here -->
<table width="100%" class="trailer">
	<tr valign="center">
		<td align="left" valign="center">
			<xsl:element name="a">
				<xsl:attribute name="href">http://<xsl:value-of select="$domain"/></xsl:attribute>
				<xsl:element name="img">
					<xsl:attribute name="src">http://<xsl:value-of select="$domain"/>/images/com/<xsl:value-of select="$name"/>/nameblack.gif</xsl:attribute>
					<xsl:attribute name="border">0</xsl:attribute>
				</xsl:element>
			</xsl:element>
			<br />
			<span style="font-size: 8pt; color: black; font-family: helvetica, arial, san-serif">© Copyright 2010 
			<xsl:element name="a">
				<xsl:attribute name="href">http://<xsl:value-of select="$domain"/></xsl:attribute>
				<xsl:value-of select="$name"/><span style="color: red; font-weight: bold">.</span>com
			</xsl:element>
			. All rights reserved.&#160;&#160;&#160;&#160;</span><br />
			<span style="font-size: 8pt; color: black; font-family: helvetica, arial, san-serif">
			<xsl:element name="a">
				<xsl:attribute name="href">mailto:webmaster@<xsl:value-of select="$dotcom"/></xsl:attribute>
				<xsl:element name="img">
					<xsl:attribute name="src"><xsl:value-of select="/full-screen/params/baseURL" />images/buttons/Mail.gif</xsl:attribute>
					<xsl:attribute name="width">16</xsl:attribute>
					<xsl:attribute name="height">16</xsl:attribute>
					<xsl:attribute name="border">0</xsl:attribute>
					&#160;e-mail the webmaster.
				</xsl:element>
			</xsl:element>
			</span>
		</td>
		<td align="right"></td>
		<td align="right" valign="top"><a href="./?menu=&amp;trailers=No&amp;preferences=">
		<xsl:element name="img">
			<xsl:attribute name="src"><xsl:value-of select="/full-screen/params/baseURL" />images/buttons/Close.gif</xsl:attribute>
			<xsl:attribute name="width">16</xsl:attribute>
			<xsl:attribute name="height">16</xsl:attribute>
			<xsl:attribute name="border">0</xsl:attribute>
			<xsl:attribute name="alt">Close the footer pane</xsl:attribute>
		</xsl:element>
		</a></td>
	</tr>
</table>
		</xsl:if>
	</xsl:template>

	<xsl:template name="navigation-start">
		<xsl:if test="(count(/full-screen/params/navmenus)=0) or (/full-screen/params/navmenus!='No')">
			<div align="right">
			<xsl:call-template name="image_link">
				<xsl:with-param name="description">Change the menu pane to Icons only</xsl:with-param>
				<xsl:with-param name="image">Contract</xsl:with-param>
				<xsl:with-param name="link">?menu=&amp;navmenus=IconsOnly&amp;preferences=</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="image_link">
				<xsl:with-param name="description">Close the menu pane</xsl:with-param>
				<xsl:with-param name="image">Close</xsl:with-param>
				<xsl:with-param name="link">?menu=&amp;navmenus=No&amp;preferences=</xsl:with-param>
			</xsl:call-template>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="navigation-end">
		<xsl:if test="(count(/full-screen/params/navmenus)=0) or (/full-screen/params/navmenus!='No')">
		<xsl:element name="th">
			<xsl:attribute name="id">navStartShadow</xsl:attribute>
			<xsl:attribute name="style">background-image: url(images/graphics/NavHShadow.gif);</xsl:attribute>
			<xsl:attribute name="class">nav<xsl:value-of select="/full-screen/params/navmenus" />StartShadow</xsl:attribute>
				<img src="images/graphics/1ptrans.gif" width="1" height="1" />
		</xsl:element>
		</xsl:if>
	</xsl:template>

	<xsl:template name="navigation-corner">
		<xsl:if test="(count(/full-screen/params/navmenus)=0) or (/full-screen/params/navmenus!='No')">
		<tr height="8">
			<xsl:element name="th">
				<xsl:attribute name="id">navStartVShadow</xsl:attribute>
				<xsl:attribute name="style">background-image: url(images/graphics/NavVShadow.gif);</xsl:attribute>
				<xsl:attribute name="class">nav<xsl:value-of select="/full-screen/params/navmenus" />Start</xsl:attribute>
					<img src="images/graphics/1ptrans.gif" width="1" height="1" />
			</xsl:element>
			<xsl:element name="th">
				<xsl:attribute name="id">navStartSECorner</xsl:attribute>
				<xsl:attribute name="style">background-image: url(images/graphics/NavSECorner.gif);</xsl:attribute>
				<xsl:attribute name="class">nav<xsl:value-of select="/full-screen/params/navmenus" />StartShadow</xsl:attribute>
				<img src="images/graphics/1ptrans.gif" width="1" height="1" />
			</xsl:element>
		</tr>
		</xsl:if>
	</xsl:template>

	<!-- A menu button -->

    <!-- deprecated - use button-link since it works in all styles -->
	<xsl:template match="navigation-item">
		<xsl:call-template name="button_link">
			<xsl:with-param name="name">
				<xsl:if test="count(name)>0"><xsl:value-of select="name" /></xsl:if>
				<xsl:if test="count(name)=0">[none]</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="description"><xsl:value-of select="description" /></xsl:with-param>
			<xsl:with-param name="link"><xsl:value-of select="link" /></xsl:with-param>
			<xsl:with-param name="image">
				<xsl:if test="count(image)>0"><xsl:value-of select="image" /></xsl:if>
				<xsl:if test="count(image)=0">[none]</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

    <!-- Display a button using the button_link template -->
	<xsl:template match="button-link">
		<xsl:call-template name="button_link">
			<xsl:with-param name="name">
				<xsl:if test="count(name)>0"><xsl:value-of select="name" /></xsl:if>
				<xsl:if test="count(name)=0">[none]</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="description"><xsl:value-of select="description" /></xsl:with-param>
			<xsl:with-param name="link"><xsl:value-of select="link" /></xsl:with-param>
			<xsl:with-param name="image">
				<xsl:if test="count(image)>0"><xsl:value-of select="image" /></xsl:if>
				<xsl:if test="count(image)=0">[none]</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>


	<!-- Usually the imported sytlesheet has a specific processor for this tag -->
	<!-- Note: IE xslt requires transformed docs to have a single root node -->
	<xsl:template match="content-area">
		<div id="content-area">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- Don't output the toolbar's data, the code in content-area outputs the controls -->
	<xsl:template match="toolbars">
	</xsl:template>

	<!-- Add this optional command to the link -->
	<xsl:template name="add_command">
		<xsl:param name="key"></xsl:param>			<!-- The key -->
		<xsl:param name="value"></xsl:param>			<!-- The value -->
		<xsl:if test="$value!=''">&amp;<xsl:value-of select='$key'></xsl:value-of>=<xsl:value-of select='$value'></xsl:value-of></xsl:if>
	</xsl:template>

	<!-- Fix the link -->
	<xsl:template name="fix_link">
		<xsl:param name="link"></xsl:param>			<!-- The command -->
		<xsl:if test="$link!=''">
		<xsl:value-of select='$link'></xsl:value-of><xsl:call-template name="add_command">
		<xsl:with-param name="key">menubars</xsl:with-param>
		<xsl:with-param name="value"><xsl:value-of select="/full-screen/params/menubars"></xsl:value-of></xsl:with-param>
		</xsl:call-template><xsl:call-template name="add_command">
		<xsl:with-param name="key">navmenus</xsl:with-param>
		<xsl:with-param name="value"><xsl:value-of select="/full-screen/params/navmenus"></xsl:value-of></xsl:with-param>
		</xsl:call-template><xsl:call-template name="add_command">
		<xsl:with-param name="key">logos</xsl:with-param>
		<xsl:with-param name="value"><xsl:value-of select="/full-screen/params/logos"></xsl:value-of></xsl:with-param>
		</xsl:call-template><xsl:call-template name="add_command">
		<xsl:with-param name="key">trailers</xsl:with-param>
		<xsl:with-param name="value"><xsl:value-of select="/full-screen/params/trailers"></xsl:value-of></xsl:with-param>
		</xsl:call-template></xsl:if>				
	</xsl:template>
	
	<!-- An button command displays a command button with an icon to the left (submit/reset/etc) -->

	<!-- An icon button displays a linked button with an icon to the left -->

	<!-- An icon image displays a button or icon with a link (no name/desc) -->

	<!-- An icon image displays a button or icon withOUT a link -->
	<xsl:template name="image">
		<xsl:param name="description"></xsl:param>	<!-- Popup desc (alt) -->
		<xsl:param name="image"></xsl:param>		<!-- Image filename -->
		<xsl:param name="height"></xsl:param>
		<xsl:param name="width"></xsl:param>
		<xsl:param name="type"></xsl:param>
			<xsl:element name="img">
				<xsl:choose>
					<xsl:when test="contains($image, '/') or contains($image, '.')">
						<xsl:attribute name="src"><xsl:if test="not(contains($image, '.')) and not(starts-with($image, '/')) and not(contains($image, '//'))">images/</xsl:if><xsl:value-of select="$image" /><xsl:if test="not(contains($image, '.'))">.gif</xsl:if></xsl:attribute>
						<xsl:if test="$width!=''">
							<xsl:attribute name="width"><xsl:value-of select="$width"/></xsl:attribute>
						</xsl:if>
						<xsl:if test="$height!=''">
							<xsl:attribute name="height"><xsl:value-of select="$height"/></xsl:attribute>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:if test="$type!='icon'">
							<xsl:attribute name="src">images/buttons/<xsl:value-of select="$image" />.gif</xsl:attribute>
						</xsl:if>
						<xsl:if test="$type='icon'">
							<xsl:attribute name="src">images/icons/<xsl:value-of select="$image" />.gif</xsl:attribute>
						</xsl:if>
						<xsl:attribute name="width">
							<xsl:choose>
								<xsl:when test="$width!=''"><xsl:value-of select="$width"/></xsl:when>
								<xsl:when test="$type='icon'">24</xsl:when>
								<xsl:otherwise>16</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="height">
							<xsl:choose>
								<xsl:when test="$height!=''"><xsl:value-of select="$height"/></xsl:when>
								<xsl:when test="$type='icon'">24</xsl:when>
								<xsl:otherwise>16</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			<xsl:attribute name="alt">
				<xsl:value-of select="$description"/>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>

	<xsl:template match="xfm:submit">
		<xsl:call-template name="button_command">
			<xsl:with-param name="name"><xsl:value-of select="xfm:caption" /></xsl:with-param>
			<xsl:with-param name="description"><xsl:value-of select="xfm:hint" /></xsl:with-param>
			<xsl:with-param name="image"><xsl:value-of select="xfm:image" /></xsl:with-param>
			<xsl:with-param name="link"><xsl:value-of select="@ref" /></xsl:with-param>
			<xsl:with-param name="type">Submit</xsl:with-param>
			<xsl:with-param name="style"><xsl:value-of select="@style" /></xsl:with-param>
			<xsl:with-param name="xform"><xsl:value-of select="@xform" /></xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="xfm:trigger">
		<xsl:call-template name="button_command">
			<xsl:with-param name="name"><xsl:value-of select="xfm:caption" /></xsl:with-param>
			<xsl:with-param name="description"><xsl:value-of select="xfm:hint" /></xsl:with-param>
			<xsl:with-param name="image"><xsl:value-of select="xfm:image" /></xsl:with-param>
			<xsl:with-param name="link"><xsl:value-of select="@ref" /></xsl:with-param>
			<xsl:with-param name="type">button</xsl:with-param>
			<xsl:with-param name="style"><xsl:value-of select="@style" /></xsl:with-param>
			<xsl:with-param name="xform"><xsl:value-of select="@xform" /></xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- html tag means optput the html as-is (without the html and [opt]body tags) -->
	<xsl:template match="html">
		<xsl:choose>
	 		<xsl:when test="body != ''">
				<xsl:copy-of select="body/child::node()" />
	  		</xsl:when>
	 		<xsl:otherwise>
				<xsl:copy-of select="./child::node()"/>
	 		</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="div">
		<xsl:copy-of select="./child::node()"/>
	</xsl:template>

	<xsl:template match="paragraph">
		<xsl:apply-templates />
		<p/>
	</xsl:template>

	<xsl:template match="img">
		<xsl:copy-of select="."/>
	</xsl:template>

	<xsl:template match="bold">
		<b><xsl:apply-templates /></b>
	</xsl:template>

	<xsl:template match="italic">
		<i><xsl:apply-templates /></i>
	</xsl:template>

	<xsl:template match="br">
		<xsl:apply-templates /><br />
	</xsl:template>


	<xsl:template match="full-screen">
		<html>
			<xsl:call-template name="html-head" />
			<body class="tundra">
				<script type="text/javascript" src="http://java.com/js/deployJava.js"></script>			
				<script type="text/javascript">dojo.back.init(tourapp.java.doJavaBrowserHashChange);</script>
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
			<link rel="stylesheet" type="text/css" href="docs/styles/css/style.css" title="basicstyle" />
			<xsl:if test="/full-screen/content-area/Menus/Params/properties/entry[@key='css']!=''">
					<xsl:element name="link">
					<xsl:attribute name="rel">stylesheet</xsl:attribute>
					<xsl:attribute name="type">text/css</xsl:attribute>
					<xsl:attribute name="href"><xsl:value-of select="/full-screen/content-area/Menus/Params/properties/entry[@key='css']"></xsl:value-of></xsl:attribute>
					<xsl:attribute name="title">basicstyle</xsl:attribute>
					</xsl:element>
			</xsl:if>

			<script src="docs/styles/js/tourapp/back.js" type="text/javascript"></script>

			<script src="docs/styles/js/tourapp/main.js" type="text/javascript"></script>
			<script src="docs/styles/js/tourapp/classes.js" type="text/javascript"></script>
			<script src="docs/styles/js/tourapp/remote.js" type="text/javascript"></script>
			<script src="docs/styles/js/tourapp/gui.js" type="text/javascript"></script>
			<script src="docs/styles/js/tourapp/xml.js" type="text/javascript"></script>
			<script src="docs/styles/js/tourapp/util.js" type="text/javascript"></script>
			<script src="docs/styles/js/tourapp/java.js" type="text/javascript"></script>
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
					tourapp.util.doButton('<xsl:value-of select="$link"/>');
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
					tourapp.util.doLink('<xsl:value-of select="$link"/>');
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
					tourapp.util.doLink('<xsl:value-of select="$link"/>');
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

<!-- Import inlined from: ../../cocoon/extendedbase/form-base.xsl - Do not modify this generated file.-->
<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 

	<xsl:template match="/">
		<xsl:apply-templates/>
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

	<!-- An icon button displays a linked button with an icon to the left -->

	<!-- An icon button displays a linked button with an icon to the left -->


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
			<xsl:attribute name="onsubmit">return tourapp.util.doButton('Submit');</xsl:attribute>
		
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
			<xsl:attribute name="class">tourapp.Editor</xsl:attribute>
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
