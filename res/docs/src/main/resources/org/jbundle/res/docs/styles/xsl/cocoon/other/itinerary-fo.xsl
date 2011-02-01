<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<xsl:import href="../extendedbase/report-fo.xsl" />

	<xsl:template match="detail/file">
		<xsl:for-each select="Booking/data/detail/file">
			<xsl:apply-templates select="*" />
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="footing/file"></xsl:template>

	<xsl:template match="BookingDetail">
		<xsl:variable name="type" select="BookingDetail.ProductType" />
		<xsl:if test="$type='Land'">
			<xsl:call-template name="land">
				<xsl:with-param name="type">
					<xsl:value-of select="$type" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="$type='Air'">
			<xsl:call-template name="air">
				<xsl:with-param name="type">
					<xsl:value-of select="$type" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- The following are itinerary paragraph formatting codes -->
	<xsl:template match="description">
		<xsl:apply-templates />
	</xsl:template>

	<!-- Display a Land or Transportation detail item -->
	<xsl:template name="land">
		<xsl:param name="type">Land</xsl:param><!-- Popup desc (alt) -->
		<fo:block font-size="14pt" font-weight="bold" color="blue"
			space-before.optimum="14pt" text-align="left">
			<xsl:element name="fo:external-graphic">
				<xsl:attribute name="src">
					<xsl:value-of select="/full-screen/params/baseURL" />images/tour/buttons/<xsl:value-of select="$type" />.gif
				</xsl:attribute>
			</xsl:element>
			&#160;
			<xsl:value-of select="BookingDetail.DetailDate" />
			-
			<xsl:value-of
				select="*[name()=concat($type, '.Description')]" />
		</fo:block>
		<fo:block font-size="12pt" space-before.optimum="12pt"
			text-align="justify" wrap-option="wrap">
			<xsl:if
				test="*[name()=concat($type, '.ItineraryDesc')]/description = ''">
				<xsl:apply-templates
					select="*[name()=concat($type, '.ItineraryDesc')]" />
			</xsl:if>
			<xsl:if
				test="*[name()=concat($type, '.ItineraryDesc')]/description != ''">
				<xsl:apply-templates
					select="*[name()=concat($type, '.ItineraryDesc')]/description" />
			</xsl:if>
		</fo:block>
	</xsl:template>

	<!-- Display an Air detail item -->
	<xsl:template name="air">
		<xsl:param name="type">Air</xsl:param><!-- Popup desc (alt) -->
		<fo:block font-size="12pt" space-before.optimum="12pt"
			background-color="yellow">

			<fo:table width="100%" inline-progression-dimension="100%"
				table-layout="fixed">
				<fo:table-column column-number="1"
					column-width="proportional-column-width(1)">
				</fo:table-column>
				<fo:table-column column-number="2"
					column-width="proportional-column-width(1)">
				</fo:table-column>
				<fo:table-column column-number="3"
					column-width="proportional-column-width(1)">
				</fo:table-column>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell column-number="1"
							display-align="top">
							<fo:block>
								Leave
								<xsl:apply-templates
									select="BookingDetail.CityDesc" />
							</fo:block>
						</fo:table-cell>
						<fo:table-cell column-number="2"
							display-align="center" text-align="center">
							<fo:block>
								<xsl:value-of
									select="BookingDetail.Carrier" />
							</fo:block>
						</fo:table-cell>
						<fo:table-cell column-number="3"
							text-align="right">
							<fo:block>
								<xsl:apply-templates
									select="BookingDetail.DetailDate" />
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell column-number="1"
							display-align="top">
							<fo:block>
								Arrive
								<xsl:apply-templates
									select="BookingDetail.ArriveDesc" />
							</fo:block>
						</fo:table-cell>
						<fo:table-cell column-number="2"
							display-align="center" text-align="center">
							<fo:block>
								<xsl:element name="fo:external-graphic">
									<xsl:attribute name="src">
										<xsl:value-of select="/full-screen/params/baseURL" />images/tour/buttons/Air.gif
									</xsl:attribute>
								</xsl:element>
								<xsl:value-of
									select="BookingDetail.Flight" />
							</fo:block>
						</fo:table-cell>
						<fo:table-cell column-number="3"
							text-align="right">
							<fo:block>
								<xsl:apply-templates
									select="BookingDetail.ArriveTime" />
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>

	</xsl:template>

	<xsl:template match="heading/file">
		<fo:block font-size="24pt" font-family="serif"
			line-height="30pt" space-before.optimum="24pt" text-align="center">
			<xsl:value-of select="Tour/Tour.Description" />
		</fo:block>
	</xsl:template>

	<xsl:template match="top-menu"></xsl:template>

</xsl:stylesheet>
