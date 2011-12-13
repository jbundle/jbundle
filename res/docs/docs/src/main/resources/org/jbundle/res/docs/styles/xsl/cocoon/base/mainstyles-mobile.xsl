<?xml version="1.0"?>
<!-- The base main stylesheet for Internet Explorer transformations -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="mainstyles-base.xsl"/>

    <xsl:template match="full-screen">
        <html>
            <xsl:call-template name="html-head" />
            <body>
                <xsl:apply-templates select="content-area" />
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
