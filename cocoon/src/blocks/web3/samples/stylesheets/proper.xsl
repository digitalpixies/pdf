<?xml version="1.0"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- 
     Author: Michael Gerzabek, michael.gerzabek@at.efp.cc, EFP Consulting �sterreich
     @version CVS $Id: proper.xsl 433543 2006-08-22 06:22:54Z crossley $
-->

<xsl:stylesheet 
    version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:rfc="http://apache.org/cocoon/Web3-Rfc/1.0">
    
	<xsl:template match="rfc:export">
		<export>
			<xsl:apply-templates/>
		</export>
	</xsl:template>
	<xsl:template match="rfc:tables">
		<tables>
			<xsl:apply-templates/>
		</tables>
	</xsl:template>
	<xsl:template match="rfc:row">
		<row>
			<xsl:attribute name="id"><xsl:number/></xsl:attribute>
			<xsl:apply-templates/>
		</row>
	</xsl:template>
	<xsl:template match="rfc:*">
		<xsl:element name="{@rfc:name}">
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="@*|*|text()|processing-instruction()" priority="-1">
		<xsl:copy>
			<xsl:apply-templates select="@*|*|text()|processing-instruction()"/>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
