<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xfm="http://www.w3.org/2000/12/xforms">

<xsl:import href="itinerary-detailed.xsl"/>

	<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 
	
	<xsl:template match="/">
        <xsl:for-each select="full-screen/content-area/data/detail/file/Booking">
            <xsl:call-template name="cover-letter">
            </xsl:call-template>
        </xsl:for-each>
	</xsl:template>

    <!-- This is the cover letter -->
	<xsl:template name="cover-letter">
        <table>
        <tr>
        <td><img src="http://www.jbundle.com/images/coolart/javacup.gif" /></td>
        <td vertical-align="bottom"><b>ABC Tour Operator<br/>
        1234 First Street<br/>
        Los Angeles, CA 90012</b></td>
        </tr>
        </table>
        <p/>
        <xsl:value-of select="Booking.GenericName" /><br/>
        <xsl:value-of select="Booking.AddressLine1" /><br/>
        <xsl:if test="Booking.AddressLine2 != ''">
            <xsl:value-of select="Booking.AddressLine2" /><br/>
        </xsl:if>
        <xsl:value-of select="Booking.CityOrTown" />, 
        <xsl:value-of select="Booking.StateOrRegion" />
        <xsl:value-of select="Booking.PostalCode" /><br/>
        <xsl:value-of select="Booking.Country" />
        <p/>
        Dear <xsl:value-of select="Booking.Contact" />,
        <p/>
        Thank you for choosing ABC. Blah blah blah blah blah blah blah blah blah blah.
        Blah blah blah blah blah blah blah blah blah blah. Blah blah blah blah blah blah blah blah blah blah.<p/>
        Blah blah blah blah blah blah blah blah blah blah. Blah blah blah blah blah blah blah blah blah blah.<p/>
        To view our proposed itinerary click this line:<p/>
		<xsl:element name="a">
			<xsl:attribute name="href">http://www.jbundle.org/jbundle.xml?screen=org.jbundle.tour.booking.itin.ItineraryReportScreen&amp;forms=display&amp;template=cover-detailed&amp;command=Submit&amp;Booking.ID=<xsl:value-of select="Booking.ID" /></xsl:attribute>
            <xsl:value-of select="Tour.Description" />
        </xsl:element>
        <p/>
        To view our proposed itinerary click this line:<p/>
		<xsl:element name="a">
			<xsl:attribute name="href">http://www.jbundle.org/jbundle.xml?screen=org.jbundle.tour.booking.itin.ItineraryReportScreen&amp;forms=display&amp;template=itinerary-fo&amp;command=Submit&amp;Booking.ID=<xsl:value-of select="Booking.ID" /></xsl:attribute>
            <xsl:value-of select="Tour.Description" />
        </xsl:element>
        (you must have Adobe Acrobat on your system)
        <p/>
        To view or change your itinerary click this line:<p/>
		<xsl:element name="a">
			<xsl:attribute name="href">http://www.jbundle.org/jbundle?applet=org.jbundle.thin.base.screen.BaseApplet&amp;jnlpjars=classes%2Fthinbase&amp;user=travelagent&amp;screen=.thin.app.booking.entry.tourappScreen&amp;background=backgrounds%2Fworldmapalpha.gif&amp;jnlpextensions=docs%2Fjnlp%2Fthin%2Cdocs%2Fjnlp%2Fthintour%2Cdocs%2Fjnlp%2Fresource&amp;backgroundcolor=%23eeeeff&amp;objectID=<xsl:value-of select="Booking.ID" /></xsl:attribute>
            <xsl:value-of select="Tour.Description" />
        </xsl:element>
        (you must have Java on your system)
        <p/>
        Thank you.<p/>
        Sincerely,<p/>
        <xsl:value-of select="Booking.EmployeeModID" />
        <p/>
        <p/>
        <p/>
    </xsl:template>
</xsl:stylesheet>
