<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xfm="http://www.w3.org/2000/12/xforms">

<xsl:import href="../extendedbase/report.xsl"/>

	<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 

	<xsl:template match="detail/file">
			<xsl:for-each select="*">
			<xsl:variable name="DetailName" select="name(*)"/>
			<p style="text-align: center; font-size: large; font-style: bold"><xsl:value-of select="FinStmt.StatementDesc" /></p>
			<p style="text-align: center; font-size: large; font-style: bold">
				<xsl:if test="FinStmt.StatementNumber = 'Net Change'">
					<xsl:value-of select="../../../heading/file/FinStmt/StartDate" /> through  <xsl:value-of select="../../../heading/file/FinStmt/EndDate" />
				</xsl:if>
				<xsl:if test="FinStmt.StatementNumber != 'Net Change'">
					<xsl:value-of select="../../../heading/file/FinStmt/EndDate" />
				</xsl:if>
			</p>
			
			<xsl:variable name="record" select="." />
			<xsl:if test="$record/data/detail/*!=''">
				<xsl:apply-templates select="$record/data/detail/file" />
			</xsl:if>
			</xsl:for-each>
	</xsl:template>

	<xsl:template match="heading/file|footing/file">
	</xsl:template>

	<xsl:template match="FinStmt/data/detail/file">
		<table border="0" cellpadding="1" width="100%">
			<xsl:variable name="DetailName" select="name(*)"/>

			<xsl:for-each select="*">
				<xsl:call-template name="Account">
					<xsl:with-param name="AccountNo"><xsl:value-of select="Account.AccountNo"/></xsl:with-param>
					<xsl:with-param name="Description"><xsl:value-of select="Account.Description"/></xsl:with-param>
					<xsl:with-param name="AccountDesc"><xsl:value-of select="FinStmtDetail.AccountDesc"/></xsl:with-param>
					<xsl:with-param name="Indent"><xsl:value-of select="FinStmtDetail.Indent"/></xsl:with-param>
					<xsl:with-param name="Invisible"><xsl:value-of select="FinStmtDetail.Invisible"/></xsl:with-param>
					<xsl:with-param name="TypicalBalance"><xsl:value-of select="FinStmtDetail.TypicalBalance"/></xsl:with-param>
					<xsl:with-param name="SubTotalLevel"><xsl:value-of select="FinStmtDetail.SubTotalLevel"/></xsl:with-param>
					<xsl:with-param name="DataColumn"><xsl:value-of select="FinStmtDetail.DataColumn"/></xsl:with-param>
					<xsl:with-param name="SpecialFormat"><xsl:apply-templates select="FinStmtDetail.SpecialFormat"/></xsl:with-param>
					<xsl:with-param name="NumberFormat"><xsl:apply-templates select="FinStmtDetail.NumberFormat"/></xsl:with-param>
					<xsl:with-param name="SpecialFunction"><xsl:apply-templates select="FinStmtDetail.SpecialFunction"/></xsl:with-param>
					<xsl:with-param name="StartBalance"><xsl:value-of select="StartBalance"/></xsl:with-param>
					<xsl:with-param name="BalanceChange"><xsl:value-of select="BalanceChange"/></xsl:with-param>
					<xsl:with-param name="EndBalance"><xsl:value-of select="EndBalance"/></xsl:with-param>
					<xsl:with-param name="TargetAmount"><xsl:value-of select="TargetAmount"/></xsl:with-param>
					<xsl:with-param name="RatioPercent"><xsl:value-of select="RatioPercent"/></xsl:with-param>
					<xsl:with-param name="StatementFormat"><xsl:value-of select="../../../FinStmt.StatementFormat"/></xsl:with-param>
				</xsl:call-template>
			</xsl:for-each>
		</table>
	</xsl:template>

	<xsl:template name="Account">
		<xsl:param name="AccountNo"></xsl:param>
		<xsl:param name="Description"></xsl:param>
		<xsl:param name="AccountDesc"></xsl:param>
		<xsl:param name="Indent"></xsl:param>
		<xsl:param name="Invisible"></xsl:param>
		<xsl:param name="TypicalBalance"></xsl:param>	<!-- Credit or Debit -->
		<xsl:param name="SubTotalLevel"></xsl:param>
		<xsl:param name="DataColumn"></xsl:param>
		<xsl:param name="SpecialFormat"></xsl:param>
		<xsl:param name="NumberFormat"></xsl:param>
		<xsl:param name="SpecialFunction"></xsl:param>
		<xsl:param name="StartBalance"></xsl:param>
		<xsl:param name="BalanceChange"></xsl:param>
		<xsl:param name="EndBalance"></xsl:param>
		<xsl:param name="TargetAmount"></xsl:param>
		<xsl:param name="RatioPercent"></xsl:param>
		<xsl:param name="StatementFormat"></xsl:param>
		<xsl:if test="$Invisible != 'Y'">
		<tr>

			<xsl:call-template name="acct-desc">
				<xsl:with-param name="Indent"><xsl:value-of select="$Indent"/></xsl:with-param>
				<xsl:with-param name="SpecialFormat"><xsl:value-of select="$SpecialFormat"/></xsl:with-param>
				<xsl:with-param name="AccountDesc"><xsl:if test="$AccountDesc != ''"><xsl:value-of select="$AccountDesc"/></xsl:if><xsl:if test="$AccountDesc = ''"><xsl:value-of select="$Description"/></xsl:if></xsl:with-param>
			</xsl:call-template>

			<xsl:if test="$StatementFormat = 'Ratios'">
				<xsl:call-template name="ratio-amount">
					<xsl:with-param name="DataColumn"><xsl:value-of select="$DataColumn"/></xsl:with-param>
					<xsl:with-param name="NumberFormat"><xsl:value-of select="$NumberFormat"/></xsl:with-param>
					<xsl:with-param name="TargetAmount"><xsl:value-of select="$TargetAmount"/></xsl:with-param>
					<xsl:with-param name="TypicalBalance"><xsl:value-of select="$TypicalBalance"/></xsl:with-param>
					<xsl:with-param name="RatioPercent"><xsl:value-of select="$RatioPercent"/></xsl:with-param>
					<xsl:with-param name="SpecialFunction"><xsl:value-of select="$SpecialFunction"/></xsl:with-param>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$StatementFormat != 'Ratios'">
				<xsl:call-template name="col-amount">
					<xsl:with-param name="DataColumn"><xsl:value-of select="$DataColumn"/></xsl:with-param>
					<xsl:with-param name="NumberFormat"><xsl:value-of select="$NumberFormat"/></xsl:with-param>
					<xsl:with-param name="TargetAmount"><xsl:value-of select="$TargetAmount"/></xsl:with-param>
					<xsl:with-param name="TypicalBalance"><xsl:value-of select="$TypicalBalance"/></xsl:with-param>
				</xsl:call-template>
			</xsl:if>

		</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template match="FinStmtDetail.SpecialFormat">
		<xsl:call-template name="std-formats">
			<xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="FinStmtDetail.NumberFormat">
		<xsl:call-template name="std-formats">
			<xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="std-formats">
		<xsl:param name="value"></xsl:param>
			<xsl:choose>
				<xsl:when test="contains($value, 'Underline')">
						text-decoration: underline;
				</xsl:when>
				<xsl:when test="contains($value, 'Double-Underline')">
						text-decoration: underline;
				</xsl:when>
				<xsl:otherwise>
						<xsl:value-of select="$value"/>
				</xsl:otherwise>
			</xsl:choose>
	</xsl:template>

	<xsl:template name="acct-desc">
		<xsl:param name="Indent"></xsl:param>
		<xsl:param name="SpecialFormat"></xsl:param>
		<xsl:param name="AccountDesc"></xsl:param>
			<xsl:if test="$Indent='2' or $Indent='3'">
				<td style="table-width: 5%">
				</td>
			</xsl:if>
			<xsl:if test="$Indent='3'">
				<td style="table-width: 5%">
				</td>
			</xsl:if>
			<xsl:element name="td">
				<xsl:attribute name="style">
					text-align: left; vertical-align: baseline; <xsl:value-of select="$SpecialFormat"/>
				</xsl:attribute>
				<xsl:if test="not($Indent='2') and not($Indent='3')">
					<xsl:attribute name="colspan">3</xsl:attribute>
				</xsl:if>
				<xsl:if test="$Indent='2'">
					<xsl:attribute name="colspan">2</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="$AccountDesc"/>
			</xsl:element>
	</xsl:template>

	<xsl:template name="col-amount">
		<xsl:param name="DataColumn"></xsl:param>
		<xsl:param name="NumberFormat"></xsl:param>
		<xsl:param name="TargetAmount"></xsl:param>
		<xsl:param name="TypicalBalance"></xsl:param>

			<xsl:if test="$DataColumn!='1'">
				<td>
				</td>
			</xsl:if>
			<xsl:if test="($DataColumn!='1') and ($DataColumn!='2')">
				<td>
				</td>
			</xsl:if>
			<xsl:if test="$DataColumn!='0'">
				<xsl:call-template name="amount-out">
					<xsl:with-param name="NumberFormat"><xsl:value-of select="$NumberFormat"/></xsl:with-param>
					<xsl:with-param name="TargetAmount"><xsl:value-of select="$TargetAmount"/></xsl:with-param>
					<xsl:with-param name="TypicalBalance"><xsl:value-of select="$TypicalBalance"/></xsl:with-param>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$DataColumn='1'">
				<td>
				</td>
			</xsl:if>
			<xsl:if test="$DataColumn!='3'">
				<td>
				</td>
			</xsl:if>
	</xsl:template>

	<xsl:template name="ratio-amount">
		<xsl:param name="DataColumn"></xsl:param>
		<xsl:param name="NumberFormat"></xsl:param>
		<xsl:param name="TargetAmount"></xsl:param>
		<xsl:param name="TypicalBalance"></xsl:param>
		<xsl:param name="RatioPercent"></xsl:param>
		<xsl:param name="SpecialFunction"></xsl:param>

		<xsl:if test="$DataColumn!='0'">

			<xsl:call-template name="amount-out">
				<xsl:with-param name="NumberFormat"><xsl:value-of select="$NumberFormat"/></xsl:with-param>
				<xsl:with-param name="TargetAmount"><xsl:value-of select="$TargetAmount"/></xsl:with-param>
				<xsl:with-param name="TypicalBalance"><xsl:value-of select="$TypicalBalance"/></xsl:with-param>
			</xsl:call-template>
			
			<td style="text-align: right">
				<xsl:if test="$SpecialFunction = '@base-ratio'">
					100%
				</xsl:if>
				<xsl:if test="$SpecialFunction = '@ratio'">
					<xsl:value-of select="$RatioPercent * 100" />%
				</xsl:if>
				<xsl:if test="$SpecialFunction != 'Ratio'">
				</xsl:if>
			</td>
			
		</xsl:if>

	</xsl:template>

	<xsl:template name="amount-out">
		<xsl:param name="NumberFormat"></xsl:param>
		<xsl:param name="TargetAmount"></xsl:param>
		<xsl:param name="TypicalBalance"></xsl:param>

			<xsl:element name="td">
			<xsl:choose>
				<xsl:when test="((translate($TargetAmount,',','') &gt; 0) and ($TypicalBalance='C')) or ((translate($TargetAmount,',','') &lt; 0) and ($TypicalBalance='D'))">
					<xsl:attribute name="style">
						text-align: right; vertical-align: baseline; color: red; <xsl:value-of select="$NumberFormat"/>
					</xsl:attribute>
						(<xsl:value-of select="translate($TargetAmount,'-','')"/>)
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="style">
						text-align: right; vertical-align: baseline; <xsl:value-of select="$NumberFormat"/>
					</xsl:attribute>
						<xsl:value-of select="translate($TargetAmount,'-','')"/>
				</xsl:otherwise>
			</xsl:choose>
			</xsl:element>
			
	</xsl:template>
</xsl:stylesheet>
