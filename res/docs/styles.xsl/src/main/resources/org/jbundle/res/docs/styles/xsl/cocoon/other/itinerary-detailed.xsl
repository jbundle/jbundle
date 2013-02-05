<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xfm="http://www.w3.org/2000/12/xforms">

<xsl:import href="itinerary.xsl"/>

<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 

<!-- Display a detail item -->
  <xsl:template name="bookingdetail">
		<xsl:param name="type">Land</xsl:param>	<!-- Popup desc (alt) -->
        <xsl:variable name="productfile">
       		<xsl:value-of select="$type" /><xsl:if test="$type='Tour'">Header</xsl:if>
        </xsl:variable>

		<xsl:element name="a">
			<xsl:attribute name="href">
				<xsl:value-of select="/full-screen/params/baseURL" />?command=Form&amp;record=com.jbundle.tour.booking.detail.db.BookingDetail&amp;objectID=<xsl:value-of select="*[name()='BookingDetail.ID']" />
			</xsl:attribute>
            <b>
                <xsl:value-of select="BookingDetail.DetailDate" /> - 
                <xsl:value-of select="*[name()=concat($productfile, '.Description')]" />
            </b>
		</xsl:element>
        <br/>
        <table><tr><td style="text-align: justify"> <!-- To keep the picture with the text -->
        <xsl:choose>
        <xsl:when test="*[name()=concat($productfile, '.ItineraryDesc')]/description != ''">
            <xsl:if test="*[name()=concat($productfile, '.ItineraryDesc')]/image != '[none]'">
                <xsl:apply-templates select="*[name()=concat($productfile, '.ItineraryDesc')]/image" />
            </xsl:if>
            <xsl:apply-templates select="*[name()=concat($productfile, '.ItineraryDesc')]/description" />
        </xsl:when>
        <xsl:otherwise>
            <xsl:apply-templates select="*[name()=concat($productfile, '.ItineraryDesc')]" />
        </xsl:otherwise>
        </xsl:choose>
        </td></tr></table>
        <br/>
  </xsl:template>

<!-- Display an Air detail item -->
  <xsl:template name="bookingair">
		<xsl:param name="type">Air</xsl:param>	<!-- Popup desc (alt) -->
        <table width="100%">
        <tr>
        <td vertical-align="top" align="left">
          Leave <b><xsl:apply-templates select="BookingDetail.CityDesc" /></b>
        </td>
        <td vertical-align="center" align="center">
		  <xsl:value-of select="BookingDetail.Carrier" />
        </td>
        <td align="right">
          <b><xsl:apply-templates select="BookingDetail.DetailDate" /></b>
        </td>
        </tr>
        <tr>
        <td vertical-align="top" align="left">
          Arrive <b><xsl:apply-templates select="BookingDetail.ArriveDesc" /></b>
        </td>
        <td align="center">
		  <img src="http://www.jbundle.com/images/tour/buttons/Air.gif"/>
		  <xsl:value-of select="BookingDetail.Flight" />
        </td>
        <td align="right">
          <b><xsl:apply-templates select="BookingDetail.ArriveTime" /></b>
        </td>
        </tr>
        </table>
        <br/>
  </xsl:template>

  <xsl:template match="image">
  <table align="right">
      <tr>
        <td align="center">
            <xsl:element name="img">
                <xsl:attribute name="src">
                	<xsl:if test="not(starts-with(@src,'http:')) and not(starts-with(@src,'/'))">
                		<xsl:value-of select="/full-screen/params/baseURL" />
                	</xsl:if>
                	<xsl:apply-templates select="@src" />
                </xsl:attribute>
                <xsl:attribute name="style">vertical-align: text-bottom; border: 0; padding: 0px 5px 0px 5px</xsl:attribute>
                <xsl:attribute name="alt"><xsl:apply-templates select="@desc" /></xsl:attribute>
            </xsl:element>
        </td>
      </tr>
      <xsl:if test="@desc != ''">
        <tr>
            <td align="center"><i><xsl:apply-templates select="@desc" /></i></td>
        </tr>
      </xsl:if>
  </table>
  </xsl:template>

</xsl:stylesheet>
