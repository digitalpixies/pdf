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
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:wi="http://apache.org/cocoon/woody/instance/1.0"
                exclude-result-prefixes="wi">
  <!--+
      | This stylesheet is designed to be included by 'woody-samples-styling.xsl'.
      | Version CVS $Id: woody-field-styling.xsl 433543 2006-08-22 06:22:54Z crossley $
      +-->

  <!-- Location of the resources directory, where JS libs and icons are stored -->
  <xsl:param name="resources-uri">resources</xsl:param>

  <xsl:template match="head" mode="woody-field">
    <script src="{$resources-uri}/woody-lib.js" type="text/javascript"/>
    <link rel="stylesheet" type="text/css" href="{$resources-uri}/woody.css"/>
  </xsl:template>

  <xsl:template match="body" mode="woody-field">
    <xsl:attribute name="onload">woody_onload(); <xsl:value-of select="@onload"/></xsl:attribute>
  </xsl:template>

  <!--+
      | Generic wi:field : produce an <input>
      +-->
  <xsl:template match="wi:field">
    <input name="{@id}" id="{@id}" value="{wi:value}" title="{wi:hint}">
      <xsl:apply-templates select="." mode="styling"/>
    </input>
    <xsl:apply-templates select="." mode="common"/>
  </xsl:template>

  <!--+
      | Common stuff like wi:validation-message, @required.
      +-->
  <xsl:template match="wi:*" mode="common">
    <!-- validation message -->
    <xsl:apply-templates select="wi:validation-message"/>
    <!-- required mark -->
    <xsl:if test="@required='true'">
      <span class="woody-field-required"> * </span>
    </xsl:if>
  </xsl:template>

  <!--+
      | Handling the common styling. You may only add attributes to the output
      | in this template as later processing might add attributes too, for
      | example @checked or @selected
      +-->
  <xsl:template match="wi:*" mode="styling">
    <xsl:apply-templates select="wi:styling/@*" mode="styling"/>

    <!--+ 
        | @listbox-size needs to be handled separately as even if it is not
        | specified some output (@size) must be generated.
        +-->
    <xsl:if test="self::wi:field[wi:selection-list][wi:styling/@list-type = 'listbox'] or
                  self::wi:multivaluefield[not(wi:styling/@list-type = 'checkbox')]">
      <xsl:variable name="size">
        <xsl:value-of select="wi:styling/@listbox-size"/>
        <xsl:if test="not(wi:styling/@listbox-size)">5</xsl:if>
      </xsl:variable>
      <xsl:attribute name="size">
        <xsl:value-of select="$size"/>
      </xsl:attribute>
    </xsl:if>
  </xsl:template>

  <xsl:template match="wi:styling/@*" mode="styling">
    <xsl:copy-of select="."/>
  </xsl:template>

  <xsl:template match="wi:styling/@submit-on-change" mode="styling">
    <xsl:if test=". = 'true'">
      <xsl:attribute name="onchange">woody_submitForm(this)</xsl:attribute>
    </xsl:if>
  </xsl:template>

  <xsl:template match="wi:styling/@list-type | wi:styling/@list-orientation |
                       wi:styling/@listbox-size | wi:styling/@format | wi:styling/@layout"
                mode="styling">
    <!--+
        | Ignore marker attributes so they don't go into the resuling HTML.
        +-->
  </xsl:template>

  <xsl:template match="wi:styling/@type" mode="styling">
    <!--+ 
        | Do we have a duplicate semantic usage of @type?
        | @type is only a marker for the stylesheet in general, but some of the
        | types must/should be in the HTML output too.
        +-->
    <xsl:variable name="validHTMLTypes"
                  select="'text hidden textarea checkbox radio password image reset submit'"/>
    <xsl:if test="normalize-space(.) and
                  contains(concat(' ', $validHTMLTypes, ' '), concat(' ', ., ' '))">
      <xsl:copy-of select="."/>
    </xsl:if>
  </xsl:template>

  <!--+
      |
      +-->
  <xsl:template match="wi:validation-message">
    <a href="#" class="woody-validation-message" onclick="alert('{normalize-space(.)}');return false;">&#160;!&#160;</a>
  </xsl:template>

  <!--+
      | Hidden wi:field : produce input with type='hidden'
      +-->
  <xsl:template match="wi:field[wi:styling/@type='hidden']" priority="2">
    <input type="hidden" name="{@id}" id="{@id}" value="{wi:value}">
      <xsl:apply-templates select="." mode="styling"/>
    </input>
  </xsl:template>

  <!--+
      | wi:field with a selection list and @list-type 'radio' : produce
      | radio-buttons oriented according to @list-orientation
      | ("horizontal" or "vertical" - default)
      +-->
  <xsl:template match="wi:field[wi:selection-list][wi:styling/@list-type='radio']" priority="2">
    <xsl:variable name="id" select="@id"/>
    <xsl:variable name="value" select="wi:value"/>
    <xsl:variable name="vertical" select="string(wi:styling/@list-orientation) != 'horizontal'"/>
    <xsl:choose>
      <xsl:when test="$vertical">
        <table cellpadding="0" cellspacing="0" border="0" title="{wi:hint}">
          <xsl:for-each select="wi:selection-list/wi:item">
            <tr>
              <td>
                <input type="radio" id="{generate-id()}" name="{$id}" value="{@value}">
                  <xsl:if test="@value = $value">
                    <xsl:attribute name="checked">checked</xsl:attribute>
                  </xsl:if>
                  <xsl:apply-templates select="." mode="styling"/>
                </input>
              </td>
              <td>
                <label for="{generate-id()}"><xsl:copy-of select="wi:label/node()"/></label>
              </td>
              <xsl:if test="position() = 1">
                <td rowspan="{count(../wi:item)}">
                  <xsl:apply-templates select="../.." mode="common"/>
                </td>
              </xsl:if>
            </tr>
          </xsl:for-each>
        </table>
      </xsl:when>
      <xsl:otherwise>
        <span title="{wi:hint}">
          <xsl:for-each select="wi:selection-list/wi:item">
            <input type="radio" id="{generate-id()}" name="{$id}" value="{@value}">
              <xsl:if test="@value = $value">
                <xsl:attribute name="checked">checked</xsl:attribute>
              </xsl:if>
              <xsl:apply-templates select="." mode="styling"/>
            </input>
            <label for="{generate-id()}"><xsl:copy-of select="wi:label/node()"/></label>
          </xsl:for-each>
        </span>
        <xsl:apply-templates select="." mode="common"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--+
      | wi:field with a selection list (not 'radio' style)
      | Rendering depends on the attributes of wi:styling :
      | - if @list-type is "listbox" : produce a list box with @listbox-size visible
      |   items (default 5)
      | - otherwise, produce a dropdown menu
      +-->
  <xsl:template match="wi:field[wi:selection-list]" priority="1">
    <xsl:variable name="value" select="wi:value"/>

    <!-- dropdown or listbox -->
    <select title="{wi:hint}" id="{@id}" name="{@id}">
      <xsl:apply-templates select="." mode="styling"/>
      <xsl:for-each select="wi:selection-list/wi:item">
        <option value="{@value}">
          <xsl:if test="@value = $value">
            <xsl:attribute name="selected">selected</xsl:attribute>
          </xsl:if>
          <xsl:copy-of select="wi:label/node()"/>
        </option>
      </xsl:for-each>
    </select>
    <xsl:apply-templates select="." mode="common"/>
  </xsl:template>

  <!--+
      | wi:field with a selection list and @type 'output'
      +-->
  <xsl:template match="wi:field[wi:selection-list][wi:styling/@type='output']" priority="3">
    <xsl:variable name="value" select="wi:value"/>
    <xsl:variable name="selected" select="wi:selection-list/wi:item[@value = $value]"/>
    <xsl:choose>
      <xsl:when test="$selected/wi:label">
        <xsl:apply-templates select="$selected/wi:label"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$value"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--+
      | wi:field with @type 'textarea'
      +-->
  <xsl:template match="wi:field[wi:styling/@type='textarea']">
    <textarea id="{@id}" name="{@id}" title="{wi:hint}">
      <xsl:apply-templates select="." mode="styling"/>
      <!-- remove carriage-returns (occurs on certain versions of IE and doubles linebreaks at each submit) -->
      <xsl:copy-of select="translate(wi:value/node(), '&#13;', '')"/>
    </textarea>
    <xsl:apply-templates select="." mode="common"/>
  </xsl:template>

  <!--+
      | wi:field with @type 'output' and wi:output are both rendered as text
      +-->
  <xsl:template match="wi:output | wi:field[wi:styling/@type='output']" priority="2">
    <xsl:copy-of select="wi:value/node()"/>
  </xsl:template>

  <!--+
      | Labels for form elements.
      +-->
  <xsl:template match="wi:*" mode="label">
    <label for="{@id}" title="{wi:hint}">
      <xsl:copy-of select="wi:label/node()"/>
    </label>
  </xsl:template>

  <!--+
      | Labels for pure outputs must not contain <label/> as there is no element to point to.
      +-->
  <xsl:template match="wi:output | wi:field[wi:styling/@type='output']" mode="label">
    <xsl:copy-of select="wi:label/node()"/>
  </xsl:template>

  <!--+
      | wi:booleanfield : produce a checkbox
      | A hidden booleanfield is not a checkbox, so 'value' contains 
      | the value and not the checked attribute
      +-->
  <xsl:template match="wi:booleanfield">
    <input id="{@id}" type="checkbox" value="true" name="{@id}" title="{wi:hint}">
      <xsl:apply-templates select="." mode="styling"/>
      <xsl:choose>
          <xsl:when test="./wi:styling[@type='hidden']">
          <xsl:if test="wi:value = 'false'">
            <xsl:attribute name="value">false</xsl:attribute>
          </xsl:if>
        </xsl:when>
        <xsl:otherwise>
          <xsl:if test="wi:value = 'true'">
            <xsl:attribute name="checked">checked</xsl:attribute>
          </xsl:if>
        </xsl:otherwise>
      </xsl:choose>
    </input>
    <xsl:apply-templates select="." mode="common"/>
  </xsl:template>

  <!--+
      | wi:booleanfield with @type 'output' : rendered as text
      +-->
  <xsl:template match="wi:booleanfield[wi:styling/@type='output']">
    <xsl:choose>
      <xsl:when test="wi:value = 'true'">
        yes
      </xsl:when>
      <xsl:otherwise>
        no
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--+
      | wi:action
      +-->
  <xsl:template match="wi:action">
    <input id="{@id}" type="submit" name="{@id}" title="{wi:hint}">
      <xsl:attribute name="value"><xsl:value-of select="wi:label/node()"/></xsl:attribute>
      <xsl:apply-templates select="." mode="styling"/>
    </input>
  </xsl:template>

  <!--+
      | wi:continuation-id : produce a hidden "continuation-id" input
      +-->
  <xsl:template match="wi:continuation-id">
    <xsl:variable name="name">
      <xsl:value-of select="@name"/>
      <xsl:if test="not(@name)">continuation-id</xsl:if>
    </xsl:variable>
    <input name="{$name}" type="hidden" value="{.}"/>
  </xsl:template>

  <!--+
      | wi:multivaluefield : produce a list of checkboxes
      +-->
  <xsl:template match="wi:multivaluefield[wi:styling/@list-type='checkbox']">
    <xsl:variable name="id" select="@id"/>
    <xsl:variable name="values" select="wi:values/wi:value/text()"/>

    <span title="{wi:hint}">
      <xsl:for-each select="wi:selection-list/wi:item">
        <xsl:variable name="value" select="@value"/>
        <input id="{generate-id()}" type="checkbox" value="{@value}" name="{$id}">
          <xsl:if test="$values[. = $value]">
            <xsl:attribute name="checked">checked</xsl:attribute>
          </xsl:if>
        </input>
        <label for="{generate-id()}"><xsl:copy-of select="wi:label/node()"/></label>
        <br/>
      </xsl:for-each>
    </span>
    <xsl:apply-templates select="." mode="common"/>
  </xsl:template>

  <!--+
      | wi:multivaluefield : produce a multiple-selection list
      +-->
  <xsl:template match="wi:multivaluefield">
    <xsl:variable name="id" select="@id"/>
    <xsl:variable name="values" select="wi:values/wi:value/text()"/>

    <span title="{wi:hint}">
      <select id="{@id}" name="{$id}" multiple="multiple">
        <xsl:apply-templates select="." mode="styling"/>
        <xsl:for-each select="wi:selection-list/wi:item">
          <xsl:variable name="value" select="@value"/>
          <option value="{$value}">
            <xsl:if test="$values[. = $value]">
              <xsl:attribute name="selected">selected</xsl:attribute>
            </xsl:if>
            <xsl:copy-of select="wi:label/node()"/>
          </option>
        </xsl:for-each>
      </select>
    </span>
    <xsl:apply-templates select="." mode="common"/>
  </xsl:template>

  <!--+
      | wi:upload
      +-->
  <xsl:template match="wi:upload">
    <xsl:choose>
      <xsl:when test="wi:value">
        <!-- Has a value (filename): display it with a change button -->
        <span title="{wi:hint}">
          [<xsl:value-of select="wi:value"/>] <input type="submit" id="{@id}" name="{@id}" value="..."/>
        </span>
      </xsl:when>
      <xsl:otherwise>
        <input type="file" id="{@id}" name="{@id}" title="{wi:hint}" accept="{@mime-types}"/>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates select="." mode="common"/>
  </xsl:template>

  <!--+
      | wi:repeater
      +-->
  <xsl:template match="wi:repeater">
    <input type="hidden" name="{@id}.size" value="{@size}"/>
    <table border="1">
      <tr>
        <xsl:for-each select="wi:headings/wi:heading">
          <th><xsl:value-of select="."/></th>
        </xsl:for-each>
      </tr>
      <xsl:apply-templates select="wi:repeater-row"/>
    </table>
  </xsl:template>

  <!--+
      | wi:repeater-row
      +-->
  <xsl:template match="wi:repeater-row">
    <tr>
      <xsl:for-each select="*">
        <td>
          <xsl:apply-templates select="."/>
        </td>
      </xsl:for-each>
    </tr>
  </xsl:template>

  <!--+
      | wi:repeater-size
      +-->
  <xsl:template match="wi:repeater-size">
    <input type="hidden" name="{@id}.size" value="{@size}"/>
  </xsl:template>

  <!--+
      | wi:form-template|wi:form-generated 
      +-->
  <xsl:template match="wi:form-template|wi:form-generated">
    <form>
      <xsl:copy-of select="@*"/>
      <xsl:attribute name="onsubmit">woody_onsubmit(); <xsl:value-of select="@onsubmit"/></xsl:attribute>
      <!-- hidden field to store the submit id -->
      <div><input type="hidden" name="woody_submit_id"/></div>
      <xsl:apply-templates/>
      
      <!-- TODO: consider putting this in the xml stream from the generator? -->
      <xsl:if test="self::wi:form-generated">
        <input type="submit"/>
      </xsl:if>
    </form>
  </xsl:template>

  <!--+
      | wi:form
      +-->
  <xsl:template match="wi:form">
    <table border="1">
      <xsl:for-each select="wi:children/*">
        <tr>
          <xsl:choose>
            <xsl:when test="self::wi:repeater">
              <td colspan="2">
                <xsl:apply-templates select="."/>
              </td>
            </xsl:when>
            <xsl:when test="self::wi:booleanfield">
              <td>&#160;</td>
              <td>
                <xsl:apply-templates select="."/>
                <xsl:text> </xsl:text>
                <xsl:copy-of select="wi:label"/>
              </td>
            </xsl:when>
            <xsl:otherwise>
              <td>
                <xsl:copy-of select="wi:label"/>
              </td>
              <td>
                <xsl:apply-templates select="."/>
              </td>
            </xsl:otherwise>
          </xsl:choose>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>

  <xsl:template match="wi:aggregatefield">
    <input id="{@id}" name="{@id}" value="{wi:value}" title="{wi:hint}">
      <xsl:apply-templates select="." mode="styling"/>
    </input>
    <xsl:apply-templates select="." mode="common"/>
  </xsl:template>

  <xsl:template match="wi:messages">
    <xsl:if test="wi:message">
      <xsl:copy-of select="wi:label/node()"/>:
      <ul>
        <xsl:for-each select="wi:message">
          <li><xsl:apply-templates/></li>
        </xsl:for-each>
      </ul>
    </xsl:if>
  </xsl:template>

  <xsl:template match="wi:validation-errors">
    <xsl:variable name="header">
      <xsl:choose>
        <xsl:when test="header">
          <xsl:copy-of select="header"/>
        </xsl:when>
        <xsl:otherwise>
          <p class="woody-validation-errors">The following errors have been detected (marked with !):</p>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="footer">
      <xsl:choose>
        <xsl:when test="footer">
          <xsl:copy-of select="footer"/>
        </xsl:when>
        <xsl:otherwise>
          <p class="woody-validation-errors">Please, correct them and re-submit the form.</p>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="frm" select="ancestor::wi:form-template"/>
    <xsl:if test="$frm and $frm//wi:validation-message">
      <xsl:copy-of select="$header"/>
      <ul>
        <xsl:for-each select="$frm//wi:validation-message">
          <li class="woody-validation-error">
            <xsl:if test="../wi:label">
              <xsl:value-of select="../wi:label"/><xsl:text>: </xsl:text>
            </xsl:if>
            <xsl:value-of select="."/>
          </li>
        </xsl:for-each>
      </ul>
      <xsl:copy-of select="$footer"/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
