<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<xsl:apply-templates />
	</xsl:template>
	<xsl:template match="full-screen">

		<html>
			<head>
			</head>
				<link rel="stylesheet" type="text/css" href="org/jbundle/res/docs/styles/css/style.css" title="basicstyle" />
				<title>
					<xsl:value-of select="title"/>
				</title>
				<xsl:element name="meta">
					<xsl:attribute name="name">
						keywords
					</xsl:attribute>
					<xsl:attribute name="content">
						<xsl:value-of select="meta-keywords"/>
					</xsl:attribute>
				</xsl:element>
				<xsl:element name="meta">
					<xsl:attribute name="name">
						description
					</xsl:attribute>
					<xsl:attribute name="content">
						<xsl:value-of select="meta-description"/>
					</xsl:attribute>
				</xsl:element>
			<!-- ToDo Get PICS rating
				<meta http-equiv="PICS-Label" content='(PICS-1.0 "http://www.classify.org/safesurf/" l gen t for "http://www.tourapp.com/" r (SS~~000 1 SS~~100 1))' />
				<meta http-equiv="PICS-Label" 
    			    content='(PICS-1.1 "http://www.rsac.org/ratingsv01.html" 
            	    l gen true comment "RSACi North America Server" 
            	    for "http://www.tourapp.com" 
            	    on "1996.04.16T08:15-0500" 
            	    r (n 0 s 0 v 0 l 0))' />
			-->
			<body marginwidth="0" marginheight="0">
				<xsl:apply-templates select="top-menu" />

				<table border="0" width="100%" height="50%" cellspacing="0" cellpadding="5">
					<tr valign="top">
						<td width="20%" background="images/graphics/NavBack.gif">
							<xsl:apply-templates select="navigation-menu" />
						</td>
						<td background="images/graphics/NavHShadow.gif" width="8">
							<img src="images/graphics/1ptrans.gif" width="1" height="1" />
						</td>
						<td valign="top">
							<xsl:apply-templates select="content-area" />
						</td>
					</tr>
					<tr height="8">
						<td background="images/graphics/NavVShadow.gif">
							<img src="images/graphics/1ptrans.gif" width="1" height="1" />
						</td>
						<td background="images/graphics/NavSECorner.gif" width="8">
							<img src="images/graphics/1ptrans.gif" width="1" height="1" />
						</td>
					</tr>
				</table>

			</body>
		</html>
	</xsl:template>

	<xsl:template match="top-menu">
		<table bgcolor="blue" width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<xsl:element name="a">
						<xsl:attribute name="href">
							<xsl:value-of select="link"/>
						</xsl:attribute>
						<xsl:element name="img">
							<xsl:attribute name="src">
								images/nav/<xsl:value-of select="image"/>.gif
							</xsl:attribute>
							<xsl:attribute name="alt">
								<xsl:value-of select="description"/>
							</xsl:attribute>
							<xsl:attribute name="border">
								0
							</xsl:attribute>
							<xsl:attribute name="height">
								75
							</xsl:attribute>
							<xsl:attribute name="width">
								21
							</xsl:attribute>
						</xsl:element>
					</xsl:element>
				</td>
				<td width="100%" align="center">
					<font face="Arial, Helvetica, SanSerif" color="white"><strong>
						<xsl:value-of select="title" />
					</strong></font>
				</td>
				<td align="right">
					<a href="?help=">
						<img src="images/nav/Help.gif" alt="Main Help Menu" border="0" width="75" height="21" />
					</a>
				</td>
			</tr>
			<tr>
				<td bgcolor="black" background="" width="100%" height="1" colspan="5">
					<img src="images/graphics/1ptrans.gif" width="1" height="1" alt="" border="0"/>
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="navigation-menu">
		<div align="right">
		<a href="?menu=&amp;navmenus=Icons+Only&amp;preferences=">
			<img src="images/buttons/Contract.gif" alt="Change the menu pane to Icons only" border="0" width="16" height="16" />
		</a>
		<a href="com.tourapp.Servlet?menu=&amp;navmenus=No&amp;preferences=">
			<img src="images/buttons/Close.gif" alt="Close the menu pane" border="0" width="16" height="16" />
		</a>
			<strong>Navigation menu</strong><br/><hr/>
			<xsl:for-each select="navigation-item">
						<xsl:element name="a">
							<xsl:attribute name="href">
								<xsl:value-of select="link"/>
							</xsl:attribute>
							<xsl:attribute name="alt">
								<xsl:value-of select="description"/>
							</xsl:attribute>
							<xsl:attribute name="align">
								center
							</xsl:attribute>
							<xsl:attribute name="border">
								0
							</xsl:attribute>
							<xsl:attribute name="width">
								16
							</xsl:attribute>
							<xsl:attribute name="height">
								16
							</xsl:attribute>
							<xsl:element name="img">
								<xsl:attribute name="src">
									images/buttons/<xsl:value-of select="image"/>
								</xsl:attribute>
							</xsl:element>
							<xsl:value-of select="description"/>
						</xsl:element>
						<br/>
			</xsl:for-each>
		</div>
	</xsl:template>

	<xsl:template match="content-area">
		<table border="0" cellpadding="5" width="100%">
			<tr>
				<td align="center" colspan="2"><font size="+2" style="bold">
					General Ledger Chart of Accounts
				</font></td>
			</tr>
			<tr>
				<td valign="top" align="right"><font style="bold" size="+1">
					Description:
				</font></td>
				<td valign="top" align="left" width="100%"><i>
					<xsl:value-of select="description"/>
				</i></td>
			</tr>
			<tr>
				<td valign="top" align="right"><font style="bold" size="+1">
					Operation:
				</font></td>
				<td valign="top" align="left" width="100%">
					<xsl:apply-templates select="operation" />
				</td>
			</tr>
			<optional field="SeeAlso"></optional>
			<optional field="TechnicalInfo"></optional>
		</table>
	</xsl:template>

	<xsl:template match="operation">
					<br/>
					Optionally, you can enter:
					<ul>
						<li>An account number to make selecting accounts easier.</li>
						<li>The typical balance, so negative balances can be highlighted in reports.</li>
						<li>A section sub-total flag indicating that this account is the last in a group on the Trial Balance and Worksheet.</li>
						<li>A counter balance account to make entering paired accounts easier to enter. (For Example, Accumulated Depreciation and Depreciation Expense)</li>
						<li>An auto-distribution code to automatically distribute amounts in Journal entries.</li>
						<li>Whether to close this account at year-end (ie., Expense or Income accounts).</li>
						<li>Set this account as discontinued (To phase out an account that may still have active postings).</li>
					</ul>
					<p>
						To display the transaction detail for an account, click on the
							<a href="?screen=AcctDetailGridScreen&amp;help=">
								<img src="images/buttons/Detail.gif"/> Detail (Account Inquiry)
							</a> button
					</p>
	</xsl:template>

</xsl:stylesheet>

