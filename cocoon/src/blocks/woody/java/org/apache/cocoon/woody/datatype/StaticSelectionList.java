/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cocoon.woody.datatype;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.apache.cocoon.woody.Constants;
import org.apache.cocoon.woody.datatype.convertor.Convertor;
import org.apache.cocoon.woody.datatype.convertor.DefaultFormatCache;
import org.apache.cocoon.xml.AttributesImpl;
import org.apache.excalibur.xml.sax.XMLizable;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

/**
 * An implementation of a SelectionList. Create instances of this class by using
 * the {@link SelectionListBuilder}. This implementation is called "Static" because
 * the items in the list are build once from its source, and then list items are
 * cached as part of this object. In contrast, the {@link DynamicSelectionList}
 * will retrieve its content from its source each time it's needed.
 * 
 * @version $Id: StaticSelectionList.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class StaticSelectionList implements SelectionList {
    /** The datatype to which this selection list belongs */
    private Datatype datatype;
    private List items = new ArrayList();

    public StaticSelectionList(Datatype datatype) {
        this.datatype = datatype;
    }

    public Datatype getDatatype() {
        return datatype;
    }

    public void generateSaxFragment(ContentHandler contentHandler, Locale locale) throws SAXException {
        Convertor.FormatCache formatCache = new DefaultFormatCache();
        contentHandler.startElement(Constants.WI_NS, SELECTION_LIST_EL, Constants.WI_PREFIX_COLON + SELECTION_LIST_EL, Constants.EMPTY_ATTRS);
        Iterator itemIt = items.iterator();
        while (itemIt.hasNext()) {
            SelectionListItem item = (SelectionListItem)itemIt.next();
            item.generateSaxFragment(contentHandler, locale, formatCache);
        }
        contentHandler.endElement(Constants.WI_NS, SELECTION_LIST_EL, Constants.WI_PREFIX_COLON + SELECTION_LIST_EL);
    }

    public List getItems() {
        return items;
    }

    /**
     * Adds a new item to this selection list.
     * @param value a value of the correct type (i.e. the type with which this selectionlist is associated)
     * @param label a SAX-fragment such as a {@link org.apache.cocoon.xml.SaxBuffer}, can be null
     */
    public void addItem(Object value, XMLizable label) {
        items.add(new SelectionListItem(value, label));
    }

    public final class SelectionListItem {
        private final Object value;
        private final XMLizable label;

        public SelectionListItem(Object value, XMLizable label) {
            this.value = value;
            this.label = label;
        }

        public Object getValue() {
            return value;
        }

        public void generateSaxFragment(ContentHandler contentHandler, Locale locale, Convertor.FormatCache formatCache)
                throws SAXException
        {
            AttributesImpl itemAttrs = new AttributesImpl();
            String stringValue;
            if (this.value == null) {
                // Null value translates into the empty string
                stringValue = "";
            } else {
                stringValue = datatype.getConvertor().convertToString(value, locale, formatCache);
            }
            itemAttrs.addCDATAAttribute("value", stringValue);
            contentHandler.startElement(Constants.WI_NS, ITEM_EL, Constants.WI_PREFIX_COLON + ITEM_EL, itemAttrs);
            contentHandler.startElement(Constants.WI_NS, LABEL_EL, Constants.WI_PREFIX_COLON + LABEL_EL, Constants.EMPTY_ATTRS);
            if (label != null) {
                label.toSAX(contentHandler);
            } else {
                contentHandler.characters(stringValue.toCharArray(), 0, stringValue.length());
            }
            contentHandler.endElement(Constants.WI_NS, LABEL_EL, Constants.WI_PREFIX_COLON + LABEL_EL);
            contentHandler.endElement(Constants.WI_NS, ITEM_EL, Constants.WI_PREFIX_COLON + ITEM_EL);
        }
    }
}
