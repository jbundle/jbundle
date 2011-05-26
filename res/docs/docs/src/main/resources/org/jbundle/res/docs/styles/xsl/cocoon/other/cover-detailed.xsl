<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xfm="http://www.w3.org/2000/12/xforms">
	<xsl:import href="itinerary-detailed.xsl" />

	<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 

	<xsl:template match="/">
		<xsl:for-each
			select="full-screen/content-area/data/detail/file/Booking">
			<xsl:call-template name="cover-letter"></xsl:call-template>
		</xsl:for-each>
		<xsl:apply-templates />
	</xsl:template>

	<!-- This is the cover letter -->
	<xsl:template name="cover-letter">
		<table>
			<tr>
				<td>
					<img
						src="http://www.tourapp.com/images/coolart/javacup.gif" />
				</td>
				<td vertical-align="bottom">
					<b>
						ABC Tour Operator
						<br />
						1234 First Street
						<br />
						Los Angeles, CA 90012
					</b>
				</td>
			</tr>
		</table>
		<p />
		<xsl:value-of select="Booking.GenericName" />
		<br />
		<xsl:value-of select="Booking.AddressLine1" />
		<br />
		<xsl:value-of select="Booking.AddressLine2" />
		<br />
		<xsl:value-of select="Booking.CityOrTown" />
		,
		<xsl:value-of select="Booking.StateOrRegion" />
		<xsl:value-of select="Booking.PostalCode" />
		<br />
		<xsl:value-of select="Booking.Country" />
		<p />
		Dear
		<xsl:value-of select="Booking.Contact" />
		,
		<p />
		Thank you for choosing ABC. Blah blah blah blah blah blah blah
		blah blah blah. Blah blah blah blah blah blah blah blah blah
		blah. Blah blah blah blah blah blah blah blah blah blah.
		<p />
		Blah blah blah blah blah blah blah blah blah blah. Blah blah
		blah blah blah blah blah blah blah blah.
		<p />
		Enclosed is a quote for the following passengers:
		<p />
		<table border="0" cellspacing="0" cellpadding="5">
			<tr>
				<th>Room types</th>
				<th>Passenger names</th>
			</tr>
			<xsl:for-each select="detail/file/BookingPax">
				<tr>
					<td>
						<xsl:value-of select="BookingPax.PaxClass" />
					</td>
					<td>
						<xsl:value-of select="BookingPax.FirstName" />
						&#160;
						<xsl:value-of select="BookingPax.SurName" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<p />
		Blah blah blah blah blah blah blah blah blah blah. Blah blah
		blah blah blah blah blah blah blah blah. Blah blah blah blah
		blah blah blah blah blah blah.
		<p />
		Blah blah blah blah blah blah blah blah blah blah. Blah blah
		blah blah blah blah blah blah blah blah.
		<p />
		Enclosed is a quote for the following passengers:
		<p />
		<table border="0" cellspacing="0" cellpadding="5">
			<tr>
				<th>Description</th>
				<th>Price</th>
				<th>Quantity</th>
				<th>Extension</th>
			</tr>
			<xsl:for-each select="detail/file/BookingLine">
				<tr>
					<td>
						<xsl:value-of select="BookingLine.Description" />
					</td>
					<td align="right">
						<xsl:value-of select="BookingLine.Price" />
					</td>
					<td align="right">
						<xsl:value-of select="BookingLine.Quantity" />
					</td>
					<td align="right">
						<xsl:value-of select="BookingLine.Gross" />
					</td>
				</tr>
			</xsl:for-each>
			<td>Total:</td>
			<td align="right"></td>
			<td align="right"></td>
			<td align="right">
				<b>
					<xsl:value-of select="Booking.Gross" />
				</b>
			</td>
		</table>
		<p />
		Blah blah blah blah blah blah blah blah blah blah. Blah blah
		blah blah blah blah blah blah blah blah. Blah blah blah blah
		blah blah blah blah blah blah. Blah blah blah blah blah blah
		blah blah blah blah. Blah blah blah blah blah blah blah blah
		blah blah.
		<p />
		Enclosed is a quote for the following passengers:
		<p />
		Thank you.
		<p />
		Sincerely,
		<p />
		<xsl:value-of select="Booking.EmployeeModID" />
		<p />
		<p />
		<p />
	</xsl:template>
</xsl:stylesheet>
