<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xfm="http://www.w3.org/2000/12/xforms">
<xsl:import href="../base/mainstyles.xsl"/>
<xsl:import href="../extendedbase/report.xsl"/>

	<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 

	<xsl:template match="detail/file">
		<xsl:element name="img">
			<xsl:attribute name="width">640</xsl:attribute>
			<xsl:attribute name="height">640</xsl:attribute>
			<xsl:attribute name="src">http://maps.google.com/staticmap?size=640x640&amp;key=ABQIAAAA96UxdNfP49qQXZAQrzAr4xRQcO_29PtO2VLMg-jasO_03seXqhQziGQMUbFHIG_HywbipTL1wMHtfw&amp;sensor=false&amp;markers=<xsl:for-each select="Booking/data/detail/file/BookingDetail"><xsl:call-template name="markers">
			<xsl:with-param name="type"><xsl:value-of select="BookingDetail.ProductType" /></xsl:with-param>
			</xsl:call-template></xsl:for-each>&amp;path=rgb:0x0000ff,weight:5|<xsl:for-each select="Booking/data/detail/file/BookingDetail"><xsl:call-template name="paths">
			<xsl:with-param name="type"><xsl:value-of select="BookingDetail.ProductType" /></xsl:with-param>
			</xsl:call-template></xsl:for-each>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
  
    <xsl:template name="markers">
		<xsl:param name="type">Land</xsl:param>
		<xsl:if test="BookingDetail.ProductType='Land' or BookingDetail.ProductType='Hotel' or BookingDetail.ProductType='Tour' or BookingDetail.ProductType='Cruise'">
			<xsl:variable name="color">
			<xsl:choose>
			  <xsl:when test="$type='Hotel'">green</xsl:when>
			  <xsl:when test="$type='Land'">blue</xsl:when>
			  <xsl:otherwise>yellow</xsl:otherwise>
			</xsl:choose>
			</xsl:variable>
			        <xsl:variable name="tag">
			<xsl:choose>
			  <xsl:when test="$type='Hotel'">h</xsl:when>
			  <xsl:when test="$type='Land'">l</xsl:when>
			  <xsl:otherwise>o</xsl:otherwise>
			</xsl:choose>
	        </xsl:variable>
    	    <xsl:if test="City.Latitude != '' and City.Longitude != '' and City.Latitude != '0.00'"><xsl:value-of select="City.Latitude" />,<xsl:value-of select="City.Longitude" />,<xsl:value-of select="$color" /><xsl:value-of select="$tag" />|</xsl:if>
        </xsl:if>
    </xsl:template>

    <xsl:template name="paths">
		<xsl:param name="type">Land</xsl:param>
        <xsl:if test="City.Latitude != '' and City.Longitude != '' and City.Latitude != '0.00'"><xsl:value-of select="City.Latitude" />,<xsl:value-of select="City.Longitude" />|</xsl:if>
        <xsl:if test="ToCity.Latitude != '' and ToCity.Longitude != '' and ToCity.Latitude != '0.00'"><xsl:value-of select="ToCity.Latitude" />,<xsl:value-of select="ToCity.Longitude" />|</xsl:if>
    </xsl:template>

	<xsl:template match="footing/file">
	</xsl:template>

    <xsl:template match="heading/file">
        <h1><center><xsl:value-of select="Tour/Tour.Description" /></center></h1>
    </xsl:template>

	<xsl:template match="BookingLine">
	</xsl:template>

	<xsl:template match="BookingPax">
	</xsl:template>

    <xsl:template match="top-menu">
    </xsl:template>

</xsl:stylesheet>
