<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:param name="view-source"/>

  <xsl:template match="page">
   <html>
    <head>
     <title>
      <xsl:value-of select="title"/>
     </title>
    </head>
    <body bgcolor="#ffffff" alink="red" link="blue" vlink="blue">
     <xsl:apply-templates/>
    </body>
   </html>
  </xsl:template>

  <xsl:template match="title">
   <h2 style="color: navy; text-align: center">
      <xsl:if test="not($view-source)">
         <xsl:apply-templates/>
      </xsl:if>     
      <xsl:if test="$view-source">
      <A>
         <xsl:attribute name="HREF">../view-source?filename=/<xsl:value-of select="$view-source"/></xsl:attribute>
 		 <xsl:attribute name="TARGET">_blank</xsl:attribute>
         <xsl:apply-templates/>
      </A>
      </xsl:if>     
   </h2>
  </xsl:template>

  <xsl:template match="para">
    <p align="center">
      <i><xsl:apply-templates/></i>
    </p>
  </xsl:template>

  <!--
  
  <xsl:template match="form">
    <form method="POST" action="{@target}">
      <xsl:apply-templates/>
    </form>
  </xsl:template>


  <xsl:template match="input">
    <center>
      <xsl:value-of select="@title"/>
      <input type="{@type}" name="{@name}" value="{.}"/>
    </center><br/>
  </xsl:template>

-->

  <xsl:template match="linkbar">
    <!--
    <center>
      [
      <a href="login"> login </a>
      |
      <a href="protected"> protected </a>
      |
      <a href="do-logout"> logout </a>
      ]
    </center>
    -->
  </xsl:template>
  
  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
