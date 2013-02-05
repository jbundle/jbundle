<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<xsl:template match="full-screen">
   <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
   
    <fo:layout-master-set>
     <fo:simple-page-master master-name="page"
                  page-height="29.7cm" 
                  page-width="21cm"
                  margin-top="1cm" 
                  margin-bottom="2cm" 
                  margin-left="2.5cm" 
                  margin-right="2.5cm">
       <fo:region-before extent="3cm"/>
       <fo:region-body margin-top="3cm"/>
       <fo:region-after extent="1.5cm"/>
     </fo:simple-page-master>

     <fo:page-sequence-master master-name="all">
       <fo:repeatable-page-master-alternatives>
	     <fo:conditional-page-master-reference master-reference="page" page-position="first"/>
       </fo:repeatable-page-master-alternatives>
     </fo:page-sequence-master>
    </fo:layout-master-set>

    <fo:page-sequence master-reference="all">
      <fo:static-content flow-name="xsl-region-after">
	    <fo:block text-align="center" 
	          font-size="10pt" 
		  font-family="serif" 
		  line-height="14pt">page <fo:page-number/></fo:block>
      </fo:static-content> 
			<fo:title>
				<xsl:value-of select="title"/>
			</fo:title>
			<fo:static-content flow-name="xsl-region-before">
				<fo:block><xsl:value-of select="title"/></fo:block>
			</fo:static-content>

      <fo:flow flow-name="xsl-region-body">
		<fo:block line-height="1pt">
			<!-- I must have a block in case of an empty content area -->
		</fo:block>
        <xsl:apply-templates select="content-area" />
      </fo:flow>
    </fo:page-sequence>
   </fo:root>
  </xsl:template>

	<xsl:template match="top">
		<fo:block font-size="20pt" font-family="serif"
            line-height="30pt">
			<xsl:value-of select="test"/>
		</fo:block>
	</xsl:template>

	<xsl:template match="top-menu">
	</xsl:template>

	<xsl:template match="navigation-menu">
	</xsl:template>

	<xsl:template match="paragraph">
		<fo:block>
			<xsl:apply-templates />
		</fo:block>
	</xsl:template>

	<xsl:template match="menu-item">
		<xsl:call-template name="button_link">
			<xsl:with-param name="name"><xsl:value-of select="name" /></xsl:with-param>
			<xsl:with-param name="description"><xsl:value-of select="description" /></xsl:with-param>
			<xsl:with-param name="link"><xsl:value-of select="link" /></xsl:with-param>
			<xsl:with-param name="image"><xsl:value-of select="image" /></xsl:with-param>
			<xsl:with-param name="style">color: white</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="navigation-item">
		<xsl:call-template name="button_link">
			<xsl:with-param name="name"><xsl:value-of select="name" /></xsl:with-param>
			<xsl:with-param name="description"><xsl:value-of select="description" /></xsl:with-param>
			<xsl:with-param name="link"><xsl:value-of select="link" /></xsl:with-param>
			<xsl:with-param name="image"><xsl:value-of select="image" /></xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- Usually the imported sytlesheet has a specific processor for this tag -->
	<xsl:template match="content-area">
		<xsl:apply-templates/>
	</xsl:template>

	<!-- html tag means optput the html as-is -->
	<xsl:template match="html">
		<xsl:copy-of select="." />
	</xsl:template>

	<xsl:template match="img">
		<xsl:copy-of select="."/>
	</xsl:template>

	<!-- An icon button displays a linked button with an icon to the left -->
	<xsl:template name="button_link">
		<xsl:param name="name"></xsl:param>			<!-- Button name -->
		<xsl:param name="description"></xsl:param>	<!-- Popup desc (alt) -->
		<xsl:param name="link">.</xsl:param>		<!-- a href link -->
		<xsl:param name="image">Help</xsl:param>	<!-- Image filename -->
		<xsl:param name="style"></xsl:param>		<!-- css style -->
		<xsl:param name="class"></xsl:param>		<!-- css class -->
		<xsl:param name="height"></xsl:param>
		<xsl:param name="width"></xsl:param>
		<xsl:param name="button">true</xsl:param>

			<xsl:element name="fo:inline">
				<xsl:if test="$button='true'">
					<xsl:attribute name="border-style">
						inset
					</xsl:attribute>
					<xsl:attribute name="border-width">
						1px
					</xsl:attribute>
				</xsl:if>
		<xsl:call-template name="image_link">
			<xsl:with-param name="link">
				<xsl:value-of select="$link" />
			</xsl:with-param>
			<xsl:with-param name="image">
				<xsl:value-of select="$image"/>
			</xsl:with-param>
			<xsl:with-param name="width">
				<xsl:value-of select="$width"/>
			</xsl:with-param>
			<xsl:with-param name="height">
				<xsl:value-of select="$height"/>
			</xsl:with-param>
			<xsl:with-param name="description">
				<xsl:value-of select="$description"/>
			</xsl:with-param>
		</xsl:call-template>
				<xsl:element name="fo:inline">
		<xsl:element name="fo:basic-link">
			<xsl:attribute name="external-destination">
				<xsl:value-of select="$link"/>
			</xsl:attribute>
				<xsl:choose>
 					<xsl:when test="$name != ''">
						<xsl:value-of select="$name"/>
  					</xsl:when>
 					<xsl:otherwise>
						<xsl:value-of select="$image"/>
 					</xsl:otherwise>
				</xsl:choose>
 				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<!-- An icon image displays a button or icon with a link -->
	<xsl:template name="image_link">
		<xsl:param name="description"></xsl:param>	<!-- Popup desc (alt) -->
		<xsl:param name="link">.</xsl:param>		<!-- a href link -->
		<xsl:param name="image">help</xsl:param>	<!-- Image filename -->
		<xsl:param name="height"></xsl:param>
		<xsl:param name="width"></xsl:param>
		<xsl:param name="type">buttons</xsl:param>

		<xsl:element name="basic-link">
			<xsl:attribute name="external-destination">
				<xsl:value-of select="$link"/>
			</xsl:attribute>
				<xsl:element name="external-graphic">
						<xsl:choose>
 							<xsl:when test="contains($image, '/') or contains($image, '.')">
								<xsl:attribute name="external-destination">
									<xsl:value-of select="$image" />
								</xsl:attribute>
								<xsl:if test="$width!=''">
									<xsl:attribute name="width">
										<xsl:value-of select="$width"/>
									</xsl:attribute>
								</xsl:if>
								<xsl:if test="$height!=''">
									<xsl:attribute name="height">
										<xsl:value-of select="$height"/>
									</xsl:attribute>
								</xsl:if>
  							</xsl:when>
 							<xsl:otherwise>
								<xsl:attribute name="external-destination">
									org/jbundle/res/images/<xsl:value-of select="$type" />/<xsl:value-of select="$image" />.gif
								</xsl:attribute>
								<xsl:attribute name="width">
									<xsl:choose>
	 									<xsl:when test="$width!=''">
											<xsl:value-of select="$width"/>
										</xsl:when>
 										<xsl:when test="$type='icons'">
											24
										</xsl:when>
 										<xsl:otherwise>
											16
 										</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
								<xsl:attribute name="height">
									<xsl:choose>
	 									<xsl:when test="$height!=''">
											<xsl:value-of select="$height"/>
										</xsl:when>
 										<xsl:when test="$type='icons'">
											24
										</xsl:when>
 										<xsl:otherwise>
											16
 										</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
 							</xsl:otherwise>
						</xsl:choose>
					<xsl:attribute name="alt">
						<xsl:value-of select="$description"/>
					</xsl:attribute>
					<xsl:attribute name="vertical-align">
						text-bottom
					</xsl:attribute>
					<xsl:attribute name="border">
						0
					</xsl:attribute>
					<xsl:attribute name="padding">
						0px 5px 0px 5px
					</xsl:attribute>
 				</xsl:element>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>
