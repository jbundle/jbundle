<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xfm="http://www.w3.org/2000/12/xforms">

<xsl:import href="../base/mainstyles.xsl"/>
<xsl:import href="../extendedbase/report.xsl"/>

	<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 

	<!-- HTML Heading area -->
	<xsl:template name="html-head">
		<head>
			<link rel="stylesheet" type="text/css" href="org/jbundle/res/docs/styles/css/style.css" title="basicstyle" />
			<link rel="stylesheet" type="text/css" href="org/jbundle/res/docs/styles/css/tour-style.css" title="basicstyle" />
			<xsl:if test="/full-screen/content-area/Menus/Params/properties/entry[@key='css']!=''">
					<xsl:element name="link">
					<xsl:attribute name="rel">stylesheet</xsl:attribute>
					<xsl:attribute name="type">text/css</xsl:attribute>
					<xsl:attribute name="href"><xsl:value-of select="/full-screen/content-area/Menus/Params/properties/entry[@key='css']"></xsl:value-of></xsl:attribute>
					<xsl:attribute name="title">basicstyle</xsl:attribute>
					</xsl:element>
			</xsl:if>
			<title>
				<xsl:value-of select="title"/>
			</title>
		<xsl:if test="(meta-keywords!='')">
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
		<xsl:if test="(meta-redirect!='')">
			<xsl:element name="meta">
				<xsl:attribute name="http-equiv">REFRESH</xsl:attribute>
				<xsl:attribute name="content">0; URL=<xsl:value-of select="meta-redirect"/></xsl:attribute>
			</xsl:element>
		</xsl:if>
			<xsl:call-template name="more-heading" />
		</head>
	</xsl:template>
	
	<xsl:template match="detail/file">
		<table border="0" cellpadding="5" width="100%" class="gridscreen" id="gridscreen">
			<xsl:variable name="DetailName" select="name(*)"/>
			<tr>
				<th>New<br/>Booking</th>
				<th>Display<br/>Itinerary</th>
				<th><xsl:value-of select="//form//detail[attribute::name=$DetailName]/*[attribute::ref='TourHeader.Description']/xfm:caption"/></th>
				<th><xsl:value-of select="//form//detail[attribute::name=$DetailName]/*[attribute::ref='TourHeader.StartDate']/xfm:caption"/></th>
				<th><xsl:value-of select="//form//detail[attribute::name=$DetailName]/*[attribute::ref='TourHeader.EndDate']/xfm:caption"/></th>
				<th><xsl:value-of select="//form//detail[attribute::name=$DetailName]/*[attribute::ref='TourHeader.Nights']/xfm:caption"/></th>
			</tr>

			<xsl:for-each select="*">
			<xsl:variable name="record" select="." />

				<xsl:element name="tr">
					<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
					
					<td class="button">
						<xsl:call-template name="button_link">
							<xsl:with-param name="name">[none]</xsl:with-param>
							<xsl:with-param name="image">tour/buttons/Booking</xsl:with-param>
							<xsl:with-param name="link">/app?applet=org.jbundle.thin.base.screen.BaseApplet&amp;userid=<xsl:value-of select="//full-screen/params/userid"/>&amp;webStart=thin&amp;screen=com.jbundle.thin.app.booking.entry.TourAppScreen&amp;objectID=<xsl:value-of select="$record/@id"/>&amp;productType=Tour</xsl:with-param>
							<xsl:with-param name="type">button</xsl:with-param>
							<xsl:with-param name="target">_blank</xsl:with-param>
						</xsl:call-template>
					</td>					
					<xsl:if test="$record/TourHeader.Code!=''">
					<td class="button">
						<script language="javascript">
							url = parent.location.href;
							url = url.substring(0, url.lastIndexOf('/') + 1);
							url = url + "./brochures/" + "<xsl:value-of select='$record/TourHeader.Code'/>" + ".pdf";
							desc = '<img src="/images/tour/buttons/Itinerary.gif"></img>';
							document.write('<a href="' + url + '">' + desc + '</a>');
						</script>
					</td>
					</xsl:if>
					<xsl:if test="$record/TourHeader.Code=''">
					<td class="button">
						<xsl:call-template name="image">
							<xsl:with-param name="image">Blank</xsl:with-param>
							<xsl:with-param name="type">button</xsl:with-param>
						</xsl:call-template>
					</td>
					</xsl:if>
						<td><xsl:value-of select="$record/*[name()='TourHeader.Description']"/></td>
						<td><xsl:value-of select="$record/*[name()='TourHeader.StartDate']"/></td>
						<td><xsl:value-of select="$record/*[name()='TourHeader.EndDate']"/></td>
						<td><xsl:value-of select="$record/*[name()='TourHeader.Nights']"/></td>

				</xsl:element>
			</xsl:for-each>
		</table>
	</xsl:template>

	<xsl:template match="footing/file">
	</xsl:template>

    <xsl:template match="top-menu">
    </xsl:template>

</xsl:stylesheet>
