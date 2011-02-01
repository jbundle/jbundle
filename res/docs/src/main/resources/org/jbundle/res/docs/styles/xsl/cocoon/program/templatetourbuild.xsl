<?xml version="1.0" encoding="utf-8"?>
<!-- Remember to only modify the template, not the build.xml in the base directory -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="basetemplatebuild.xsl"/>
<xsl:output method="xml"/>

<xsl:template match="/">

<project name="Tour" default="pack" basedir=".">

    <property name="lib" value="${{basedir}}/target/${{artifactId}}-${{version}}/lib"/>
    <property name="pathtoroot" value="/../../../../.."/>
    <!-- property name="pack200classpath" value="=/usr/local/java/dev/pack200/Pack200Task.jar"/>
    <available property="pack200exists" file="${{pack200classpath}}" -->
	<available file="${{maven.plugin.classpath}}" property="pack200exists">
    </available>
        
    <target name="webapp">
        <mkdir dir="${{lib}}"/>

        <xsl:call-template name="jar">
        </xsl:call-template>
        
    </target>
    
    <target if="pack200exists" depends="webapp" name="pack">
        <taskdef classpath="${{pack200classpath}}" classname="com.sun.tools.apache.ant.pack200.Pack200Task" name="pack200">
        </taskdef>

        <xsl:call-template name="pack">
        </xsl:call-template>

    </target>

</project>

</xsl:template>

</xsl:stylesheet>
