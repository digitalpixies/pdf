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
    Select a single slide from the output of filter-slop-output.xsl
    $Id: select-slide.xsl 433543 2006-08-22 06:22:54Z crossley $
-->
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

    <!-- which slide to select -->
    <xsl:param name="slideId"/>

    <!-- By default copy everything -->
    <xsl:template match="*">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <!-- add position attributes for next/previous navigation -->
    <xsl:template match="slide-ref">
        <xsl:copy>
            <xsl:copy-of select="@*"/>

            <xsl:attribute name="offset-from-current">
                <xsl:value-of select="@slide-id - $slideId"/>
            </xsl:attribute>

            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <!-- omit slides, except for the selected one -->
    <xsl:template match="slide[not(@slide-id = $slideId)]"/>

</xsl:stylesheet>
