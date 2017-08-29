<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="1.0" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" /><br/>
	<xsl:template match="/">
		<entries>
			<xsl:apply-templates />
		</entries>
	</xsl:template>
	<xsl:template match="entry">
		<entry field="{field}">
		</entry>
	</xsl:template>
</xsl:stylesheet>