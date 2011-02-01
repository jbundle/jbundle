<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xfm="http://www.w3.org/2000/12/xforms">

<xsl:import href="../base/mainstyles.xsl"/>
<xsl:import href="../extendedbase/report.xsl"/>

	<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 

	<xsl:template match="detail/file">
			<xsl:for-each select="Booking/detail/file">
				<xsl:apply-templates select="*"/><br/>
			</xsl:for-each>
	</xsl:template>

	<xsl:template match="footing/file">
	</xsl:template>

    <xsl:template match="heading/file">
			<xsl:for-each select="Tour/detail/file">
				<xsl:apply-templates select="*"/><br/>
			</xsl:for-each>
    </xsl:template>

	<xsl:template match="ApTrx">
		<center><h2>Tour Voucher</h2></center>
		<center><h4><bold>ABC Tours</bold><br/>
		1234 South Street<br/>
		Los Angeles, CA  90012</h4></center>
		For: <bold><xsl:value-of select="Vendor.VendorName" /></bold><br/>
		<xsl:value-of select="Vendor.AddressLine1" /><br/>
		<xsl:if test="Vendor.AddressLine2 != ''"><xsl:value-of select="Vendor.AddressLine2" /><br/></xsl:if>
		<xsl:value-of select="Vendor.CityOrTown" />,&#160;<xsl:value-of select="Vendor.StateOrRegion" />&#160;<xsl:value-of select="Vendor.PostalCode" />&#160;<xsl:value-of select="Vendor.Country" /><br/>
		<xsl:if test="Vendor.Tel != ''">Tel: <xsl:value-of select="Vendor.Tel" /><br/></xsl:if>
		<xsl:if test="Vendor.Fax != ''">Fax: <xsl:value-of select="Vendor.Fax" /><br/></xsl:if>
		<xsl:if test="Vendor.Email != ''">Email: <xsl:value-of select="Vendor.Email" /><br/></xsl:if>
		<p></p>For the following passengers:<br/>
		<table border="0" cellspacing="0" cellpadding="5">
			<tr>
				<th>Room types</th>
				<th>Passenger names</th>
			</tr>
			<xsl:for-each select="../../../../../../detail/file/Booking/detail/file/BookingPax">
			<tr>
				<td><xsl:value-of select="BookingPax.PaxClass" /></td>
				<td><xsl:value-of select="BookingPax.FirstName" />&#160;<xsl:value-of select="BookingPax.SurName" /></td>
			</tr>
			</xsl:for-each>
		</table>

		<p></p>For the following services:<br/>
		<xsl:apply-templates select="detail/file/BookingDetail"/><br/>
	</xsl:template>

	<xsl:template match="BookingLine">
	</xsl:template>

	<xsl:template match="file/BookingPax">
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

<!-- Display a detail item -->
  <xsl:template name="bookingdetail">
		<xsl:param name="type">Land</xsl:param>	<!-- Popup desc (alt) -->
        <xsl:variable name="productfile">
       		<xsl:value-of select="$type" /><xsl:if test="$type='Tour'">Header</xsl:if>
        </xsl:variable>

		<xsl:element name="a">
			<xsl:attribute name="href">?command=Form&amp;record=com.tourapp.tour.booking.detail.db.BookingDetail&amp;objectID=<xsl:value-of select="*[name()='BookingDetail.ID']" /></xsl:attribute>
            <b>
				<xsl:element name="img">
					<xsl:attribute name="src"><xsl:value-of select="/full-screen/params/baseURL" />/images/tour/buttons/<xsl:value-of select="$type" />.gif</xsl:attribute>
					<xsl:attribute name="border">0</xsl:attribute>
				</xsl:element>
                &#160;
                <xsl:value-of select="BookingDetail.DetailDate" /> - 
                <xsl:value-of select="*[name()=concat($productfile, '.Description')]" />
            </b>
		</xsl:element>
        <br/>
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
