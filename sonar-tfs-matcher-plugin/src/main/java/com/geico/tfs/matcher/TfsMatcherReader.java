/*
 * SonarQube :: TFS :: Matcher :: Plugin
 * Copyright (C) 2015 Zach Cranfill
 * zachary.cranfill@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.geico.tfs.matcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Closeables;

public class TfsMatcherReader {
    private File executable;
    private Map<String, String> output = new HashMap<String, String>();
    private XMLStreamReader stream;
    private String localProjectPath;
    
    public TfsMatcherReader(File file){
        parse(file);
    }
    
    private void parse(File file) {
        InputStreamReader reader = null;
        XMLInputFactory xmlFactory = XMLInputFactory.newInstance();

        try {
          reader = new InputStreamReader(new FileInputStream(file), Charsets.UTF_8);
          stream = xmlFactory.createXMLStreamReader(reader);

          while (stream.hasNext()) {
            if (stream.next() == XMLStreamConstants.START_ELEMENT) {
              String tagName = stream.getLocalName();

              if ("Match".equals(tagName)) {
                handleMatchTag();
              }
            }
          }
        } catch (IOException e) {
          throw Throwables.propagate(e);
        } catch (XMLStreamException e) {
          throw Throwables.propagate(e);
        } finally {
          closeXmlStream();
          Closeables.closeQuietly(reader);
        }

        return;
      }
    
    private void closeXmlStream() {
        if (stream != null) {
          try {
            stream.close();
          } catch (XMLStreamException e) {
            throw Throwables.propagate(e);
          }
        }
    }
    
    private void handleMatchTag() throws XMLStreamException{
        String localPath = "";
        String serverPath = "";
        while (stream.hasNext()) {
            int next = stream.next();

            if (next == XMLStreamConstants.END_ELEMENT && "Match".equals(stream.getLocalName())) {
              output.put(localPath, serverPath);
              break;
            } else if (next == XMLStreamConstants.START_ELEMENT) {
              String tagName = stream.getLocalName();

              if ("LocalPath".equals(tagName)) {
                  localPath = stream.getElementText();
              } else if ("TfsServerPath".equals(tagName)) {
                  serverPath = stream.getElementText();
              }
            }
          }
    }
    
    public Map<String, String> getMatchs(){
        return output;
    }
}