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
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Closeables;

import static org.fest.assertions.Assertions.assertThat;

public class TfsMatcherWriterTest{
    
    private XMLStreamReader stream;
    
    @Test
    public void testWriteFileList(){
        List<File> files = new ArrayList<File>();
        
        File test1 = new File("test1.txt");
        File test2 = new File("test2.txt");
        
        files.add(test1);
        files.add(test2);
        
        String filename = "test-filelist.xml";
        
        TfsMatcherWriter writer = new TfsMatcherWriter(filename);
        writer.writeFileListXml(files);
        
        File result = new File(filename);
                
        Integer resultsCount = 0;
        
        InputStreamReader reader = null;
        XMLInputFactory xmlFactory = null;
        try{
            reader = new InputStreamReader(new FileInputStream(result), Charsets.UTF_8);
            xmlFactory = XMLInputFactory.newInstance();
            
            stream = xmlFactory.createXMLStreamReader(reader);
            
            while (stream.hasNext()) {
                if (stream.next() == XMLStreamConstants.START_ELEMENT) {
                  String tagName = stream.getLocalName();

                  if ("File".equals(tagName)) {
                    resultsCount++;
                  }
                }
            }
            
        }catch(IOException e){
            throw Throwables.propagate(e);
        }catch(XMLStreamException e){
            throw Throwables.propagate(e);
        }finally{
            closeXmlStream();
            Closeables.closeQuietly(reader);
        }
        
        assertThat(resultsCount).isEqualTo(2);
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
}