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
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

public class TfsMatcherMap {
    private static final Logger LOG = LoggerFactory.getLogger(TfsMatcherMap.class);
    private Map<String, String> map = new HashMap<String, String>();

    private String localProjectPath = Paths.get(".").toAbsolutePath().normalize().toString()
            .replaceAll("\\\\", "/");

    public TfsMatcherMap(){
        populateMap();
    }
    
    public String getMatch(String localPath) {
        localPath = localPath.replaceAll("/", "\\\\");
        if (map.get(localPath) != null) {
            return map.get(localPath);
        } else {
            return "";
        }
    }

    public Map<String, String> getMap() {
        return map;
    }

    public String getLocalProjectPath() {
        return localProjectPath;
    }
    
    private void populateMap(){
        TfsMatcherRunner runner = new TfsMatcherRunner(
                TfsMatcherPlugin.MATCHER_RESULTS_DIR);
        map = runner.getTfsMatchFromLocalPaths();
    }
}