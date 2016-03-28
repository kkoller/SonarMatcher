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
import java.util.Map;

import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;

import com.google.common.base.Throwables;

public class TfsMatcherSensor implements Sensor {
    
    private final FileSystem fs;
    private TfsMatcherMap map;
    private SensorContext context;
    
    public TfsMatcherSensor(FileSystem fs){
        this.fs = fs;
        TfsMatcherWriter writer = new TfsMatcherWriter("matcher-input.xml");
        writer.writeFileListXml(filesToAnalyze());

    }
    
    @Override
    public boolean shouldExecuteOnProject(Project project) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void analyse(Project project, SensorContext context) {
        this.context = context;
        map = new TfsMatcherMap();
        TfsMatcherWriter reportWriter = 
                new TfsMatcherWriter("matcher-report.xml");
        reportWriter.startReportXmlFile();
        analyzeFiles(reportWriter);
        reportWriter.endMatcherReportXmlFile();
    }
    
    private void analyzeFiles(TfsMatcherWriter reportWriter){
        for(Map.Entry<String, String> entry : map.getMap().entrySet()){
            String localPath = entry.getKey();
            InputFile inputFile = fs.inputFile(fs.predicates().hasAbsolutePath(localPath));
            String tfsServerPath = map.getMatch(inputFile.absolutePath());
            String sonarResource = context.getResource(inputFile).getEffectiveKey();
            saveServerPath(inputFile, tfsServerPath);
            reportWriter.writeReportXmlLine(localPath, tfsServerPath, sonarResource);
        }
    }
    
    private void saveServerPath(InputFile inputFile, String tfsServerPath){
        if(inputFile != null){
            if(!tfsServerPath.isEmpty()){
                Measure measure = 
                        new Measure(TfsMatcherMetrics.TFSSERVERPATH, tfsServerPath);
                context.saveMeasure(inputFile, measure);

            }
        }
    }
    
    private Iterable<File> filesToAnalyze() {
        return fs.files(fs.predicates().
                and(fs.predicates().hasType(Type.MAIN),
                        fs.predicates().or(
                                fs.predicates().hasLanguage(TfsMatcherPlugin.LANGUAGE_KEY_ONE),
                                fs.predicates().hasLanguage(TfsMatcherPlugin.LANGUAGE_KEY_TWO))));
      }
}