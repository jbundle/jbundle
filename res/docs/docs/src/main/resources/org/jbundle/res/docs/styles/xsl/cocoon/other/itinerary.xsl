<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xfm="http://www.w3.org/2000/12/xforms">

<xsl:import href="../base/mainstyles.xsl"/>
<xsl:import href="../extendedbase/report.xsl"/>

	<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 

	<xsl:template match="detail/file">
			<xsl:for-each select="Booking/data/detail/file">
				<xsl:apply-templates select="*"/><br/>
			</xsl:for-each>
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

	<xsl:template match="BookingDetail">
        <xsl:variable name="type" select="BookingDetail.ProductType"/>
        <xsl:if test="$type='Land' or $type='Hotel'">
            <xsl:call-template name="bookingdetail">
                <xsl:with-param name="type"><xsl:value-of select="$type" /></xsl:with-param>
            </xsl:call-template>
        </xsl:if>
        <xsl:if test="$type='Transportation' or $type='Cruise' or $type='Car' or $type='Item' or $type='Tour'">
            <xsl:call-template name="bookingdetail">
                <xsl:with-param name="type"><xsl:value-of select="$type" /></xsl:with-param>
            </xsl:call-template>
        </xsl:if>
        <xsl:if test="$type='Air'">
            <xsl:call-template name="bookingair">
                <xsl:with-param name="type"><xsl:value-of select="$type" /></xsl:with-param>
            </xsl:call-template>
        </xsl:if>
	</xsl:template>

<!-- The following are itinerary paragraph formatting codes -->
  <xsl:template match="description">
    <xsl:apply-templates/>
  </xsl:template>

<!-- Display a detail item -->
  <xsl:template name="bookingdetail">
		<xsl:param name="type">Land</xsl:param>	<!-- Popup desc (alt) -->
        <xsl:variable name="productfile">
       		<xsl:value-of select="$type" /><xsl:if test="$type='Tour'">Header</xsl:if>
        </xsl:variable>
        
		<xsl:element name="a">
			<xsl:attribute name="href"><xsl:value-of select="/full-screen/params/baseURL" />?command=Form&amp;record=com.tourapp.tour.booking.detail.db.BookingDetail&amp;objectID=<xsl:value-of select="*[name()='BookingDetail.ID']" /></xsl:attribute>
            <b>
				<xsl:element name="img">
					<xsl:attribute name="src"><xsl:value-of select="/full-screen/params/baseURL" />images/tour/buttons/<xsl:value-of select="$type" />.gif</xsl:attribute>
					<xsl:attribute name="border">0</xsl:attribute>
				</xsl:element>
                &#160;
                <xsl:value-of select="BookingDetail.DetailDate" /> - 
                <xsl:value-of select="*[name()=concat($productfile, '.Description')]" />
            </b>
		</xsl:element>
        <br/>
        <xsl:choose>
        <xsl:when test="*[name()=concat($productfile, '.ItineraryDesc')]/description != ''">
            <xsl:apply-templates select="*[name()=concat($productfile, '.ItineraryDesc')]/description" />
        </xsl:when>
        <xsl:otherwise>
            <xsl:apply-templates select="*[name()=concat($productfile, '.ItineraryDesc')]" />
        </xsl:otherwise>
        </xsl:choose>
        <p/>
  </xsl:template>

<!-- Display an air detail item -->
  <xsl:template name="bookingair">
		<xsl:param name="type">Air</xsl:param>	<!-- Popup desc (alt) -->
            <xsl:call-template name="bookingdetail">
                <xsl:with-param name="type"><xsl:value-of select="$type" /></xsl:with-param>
            </xsl:call-template>
  </xsl:template>

    <xsl:template match="top-menu">
    </xsl:template>

</xsl:stylesheet>
