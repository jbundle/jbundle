<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:template match="/">
	<html>
		<head>
			<title><xsl:value-of select="root/subject" /></title>
		</head>
		<body>
			<xsl:apply-templates/>
		</body>
	</html>
</xsl:template>

<xsl:template match="root">
	<h2><xsl:value-of select="header/subject" /></h2>
	Please confirm the following booking:
	<xsl:for-each select="productMessage">
		<br/>Hotel:  <b><xsl:value-of select="productDesc" /></b>
		<br/>Hotel room class:  <b><xsl:value-of select="rateClassDesc" /></b>
		<br/>Hotel room rate:  <b><xsl:value-of select="rateTypeDesc" /></b>
		<br/>Check-in date:  <b><xsl:value-of select="DetailDate" /></b>
		<br/>Number of nights:  <b><xsl:value-of select="Nights" /></b>
		<br/>Total passengers:  <b><xsl:value-of select="Pax" /></b>
		<xsl:if test="roomType1 != ''"><br/>Single rooms:  <b><xsl:value-of select="roomType1" /></b></xsl:if>
		<xsl:if test="roomType2 != ''"><br/>Double rooms:  <b><xsl:value-of select="roomType2 div 2" /></b></xsl:if>
		<xsl:if test="roomType3 != ''"><br/>Triple rooms:  <b><xsl:value-of select="roomType3 div 3" /></b></xsl:if>
		<xsl:if test="roomType4 != ''"><br/>Quad rooms:  <b><xsl:value-of select="roomType4 div 4" /></b></xsl:if>
		<xsl:if test="roomType5 != ''"><br/>Children:  <b><xsl:value-of select="roomType5" /></b></xsl:if>
	</xsl:for-each>
	<br/><table border="1">
		<tr><th>Room type</th><th>Passenger name</th></tr>
		<xsl:for-each select="passengerMessage/paxDetail">
		<tr>
			<td><xsl:value-of select="Description" /></td> <td><xsl:value-of select="Name" /></td>
		</tr>
		</xsl:for-each>
	</table>
    <p/>You may respond in one of the following ways:
    <br/>Reply to this message without changing this message or subject and fill in the price on the following line:
	<br/>productResponse/ConfirmationNo:
	<br/>productResponse/ConfirmedBy:
	<br/>trxID: <xsl:value-of select="header/trxID" />
	<br/>dataStatus: 5
	<xsl:if test="header/formReply != ''">
		<br/><b>or</b> Fill out this box and press submit:
		<br/>
			<xsl:element name="form">
				<xsl:attribute name="action"><xsl:value-of select="/root/header/baseURL" />message?trxType=<xsl:value-of select="header/formreply" /></xsl:attribute>
				<xsl:attribute name="method">post</xsl:attribute>
				<xsl:element name="input">
					<xsl:attribute name="type">hidden</xsl:attribute>
					<xsl:attribute name="name">trxID</xsl:attribute>
					<xsl:attribute name="value"><xsl:value-of select="header/trxID" /></xsl:attribute>
				</xsl:element>
				Confirmation no: <input type="text" name="ConfirmationNo" size="30" maxlength="30" value="" />
				<br/>Confirmed by: <input type="text" name="ConfirmedBy" size="30" maxlength="30" value="" />
				<br/><input type="submit" name="command" value="Submit" />
				<input type="Reset" />
			</xsl:element>
	</xsl:if>
	<xsl:if test="header/emailReply != ''">
		<br/><b>or</b> Click this link to enter the confirmation on our site:
		<br/>
			<xsl:element name="a">
				<xsl:attribute name="href"><xsl:value-of select="/root/header/baseURL" />message?trxType=<xsl:value-of select="header/emailReply" />&amp;trxID=<xsl:value-of select="header/trxID" /><xsl:if test="header/contactUser != ''">&amp;user=<xsl:value-of select="header/contactUser" /></xsl:if></xsl:attribute>
				Enter the confirmation information.
			</xsl:element>
	</xsl:if>
		<br/>
		<br/>
        <b>or</b> Click this line to view your pending messages:
		<br/>
			<xsl:element name="a">
				<xsl:attribute name="href"><xsl:value-of select="/root/header/baseURL" />?record=.tour.acctpay.db.Vendor&amp;command=Message%20Log&amp;ContactType=Vendor&amp;ContactID=<xsl:value-of select="header/contactID" /><xsl:if test="header/contactUser != ''">&amp;user=<xsl:value-of select="header/contactUser" /></xsl:if></xsl:attribute>
				View your pending messages.
			</xsl:element>
</xsl:template>

</xsl:stylesheet>
