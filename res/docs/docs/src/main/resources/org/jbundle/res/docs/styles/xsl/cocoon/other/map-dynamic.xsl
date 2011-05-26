<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xfm="http://www.w3.org/2000/12/xforms">
<xsl:import href="../base/mainstyles.xsl"/>
<xsl:import href="../extendedbase/report.xsl"/>

	<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 

	<xsl:template match="full-screen">
		<html>
			<xsl:call-template name="html-head" />
			<body onload="initialize()">
				<xsl:apply-templates select="top-menu" />
				<table style="border: 0; width: 100%; height: 50%;" cellspacing="0" cellpadding="5">
					<tr valign="top">
							<xsl:apply-templates select="navigation-menu" />
						<td valign="top">
							<xsl:apply-templates select="content-area" />
						</td>
					</tr>
						<xsl:call-template name="navigation-corner" />
				</table>
			</body>
		</html>
	</xsl:template>
		
<xsl:template name="more-heading">
<meta name="viewport" content="initial-scale=2.0, user-scalable=yes" />
<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false&amp;key=ABQIAAAA96UxdNfP49qQXZAQrzAr4xRQcO_29PtO2VLMg-jasO_03seXqhQziGQMUbFHIG_HywbipTL1wMHtfw"></script>
<script type="text/javascript">
  function initialize() {
    var latlng = new google.maps.LatLng(0,0);
 	<xsl:for-each select="/full-screen/content-area/data/detail/file/Booking/data/detail/file/BookingDetail">
<xsl:if test="City.Latitude != '' and City.Longitude != '' and City.Latitude != '0.00'">
 		latlng = new google.maps.LatLng(<xsl:value-of select="City.Latitude" />,<xsl:value-of select="City.Longitude" />);
</xsl:if>
 	</xsl:for-each>
    var myOptions = {
      zoom: 7,
      center: latlng,
      mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);

	    var marker = null;
		<xsl:for-each select="/full-screen/content-area/data/detail/file/Booking/data/detail/file/BookingDetail"><xsl:call-template name="markers">
			<xsl:with-param name="type"><xsl:value-of select="BookingDetail.ProductType" /></xsl:with-param>
			</xsl:call-template></xsl:for-each>
  }
</script>

</xsl:template>

	<xsl:template match="detail/file">
  <div id="map_canvas" style="width:100%; height:640px"></div>
	</xsl:template>
  
    <xsl:template name="markers">
		<xsl:param name="type">Land</xsl:param>
		<xsl:if test="BookingDetail.ProductType='Land' or BookingDetail.ProductType='Hotel' or BookingDetail.ProductType='Tour' or BookingDetail.ProductType='Cruise'">
    	    <xsl:if test="City.Latitude != '' and City.Longitude != '' and City.Latitude != '0.00'">
    marker = new google.maps.Marker({
        position: new google.maps.LatLng(<xsl:value-of select="City.Latitude" />,<xsl:value-of select="City.Longitude" />), 
        map: map, 
        title:"<xsl:value-of select="BookingDetail.Description"></xsl:value-of>",
        icon: "images/tour/buttons/<xsl:value-of select="BookingDetail.ProductType"></xsl:value-of>.gif"
    });
    	</xsl:if>
        </xsl:if>
    </xsl:template>

    <xsl:template name="paths">
    <!-- Add logic for paths -->
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
