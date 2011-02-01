<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- The base stylesheet for java HTMLPanel(s) (no nested tables) -->

	<xsl:import href="mainstyles-base.xsl"/>

	<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 
	
	<xsl:template match="full-screen">
		<html>
			<head>
				<link rel="stylesheet" type="text/css" href="docs/styles/css/style.css" title="basicstyle" />
				<title>
					<xsl:value-of select="title"/>
				</title>
			</head>
			<body marginwidth="0" marginheight="0" style="background-image: url(/images/backgrounds/worldmapalpha.gif);
					background-color: #eeeeff;">
							<xsl:apply-templates select="content-area" />
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>
