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
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Files;

public class TfsMatcherWriter{
    
    private String filename;
    private StringBuilder sb = new StringBuilder();
    
    public TfsMatcherWriter(String filename){
        this.filename = filename;
    }
    
    public void writeFileListXml(Iterable<File> files){
        appendLine(sb, "  <Files>");
        for (File file : files) {
          appendLine(sb, "    <File>" + file.getAbsolutePath() + "</File>");
        }
        appendLine(sb, "  </Files>");
        try{
            Files.write(sb, new File(filename), Charsets.UTF_8);
        }catch(IOException e){
            throw Throwables.propagate(e);
        }
    }
    
    public void startReportXmlFile(){
        appendLine(sb, "  <SonarReports>");
    }
    
    public void writeReportXmlLine(String localPath, String serverPath, String sonarResource){
        appendLine(sb, "    <SonarReport>");
        
        appendLine(sb, "      <localPath>" + localPath + "</localPath>");
        appendLine(sb, "      <serverPath>" + serverPath + "</serverPath>");
        appendLine(sb, "      <sonarResource>" + sonarResource + "</sonarResource>");
        
        appendLine(sb, "    </SonarReport>");
    }
    
    public void endMatcherReportXmlFile(){
        appendLine(sb, "  </SonarReports>");
        try{
            Files.write(sb, new File(filename), Charsets.UTF_8);
        }catch(IOException e){
            throw Throwables.propagate(e);
        }
    }
    
    private void appendLine(StringBuilder sb, String line) {
        sb.append(line);
        sb.append("\r\n");
      }
}