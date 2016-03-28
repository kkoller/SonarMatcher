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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.common.io.Resources;

public class TfsMatcherRunner {
    private static final Logger LOG = LoggerFactory.getLogger(TfsMatcherRunner.class);
    private File executable;
    private XMLStreamReader stream;
    private String resultsDir;

    public TfsMatcherRunner(String resultsDir){
        this.resultsDir = resultsDir;
        this.executable = getExecutable();
        runTfsMatcher();
    }
    
    public Map<String, String> getTfsMatchFromLocalPaths(){
        File results = getResultsFile();
        if(results.exists()){
            TfsMatcherReader reader = new TfsMatcherReader(results);
            return reader.getMatchs();
        }
        return new HashMap<String, String>();
    }
    
    private File getResultsFile(){
        File resultsFile = new File(resultsDir);
        return resultsFile;
    }

    private void runTfsMatcher(){
        Process process = null;
        try{
            LOG.info("LAUNCHING MATCHER");
            process = new ProcessBuilder(executable.getAbsolutePath()).start();
            process.waitFor();
        }catch(InterruptedException e){
            LOG.info(e.getMessage());
            throw Throwables.propagate(e);
        }catch (ArrayIndexOutOfBoundsException e) {
            LOG.info(e.getMessage());
            throw Throwables.propagate(e);
        } catch (IOException e) {
            LOG.info(e.getMessage());
            throw Throwables.propagate(e);
        } finally {
            if (process != null) {
                Closeables.closeQuietly(process.getErrorStream());
            }
        }
    }
    
    private File getExecutable() {
        File tempFile = new File("");
        try {
            tempFile = File.createTempFile(Long.toString(System.currentTimeMillis()),
                    "TfsMatcher.exe");
            tempFile.deleteOnExit();
            URL tfsMatcherURL = TfsMatcherRunner.class.getResource("/TfsMatcher.exe");
            Files.write(Resources.toByteArray(tfsMatcherURL), tempFile);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
        return tempFile;
    }
}