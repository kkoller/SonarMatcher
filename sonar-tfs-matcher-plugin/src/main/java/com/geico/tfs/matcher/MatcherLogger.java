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

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MatcherLogger{
    
    private static Logger LOG;
       
    public static void log(String msg){
        getLogger().info(msg);
    }
    
    private static Logger getLogger(){
        if(LOG == null){
            LOG = Logger.getLogger(MatcherLogger.class.getName());
            setUpLogger();
        }
        return LOG;
    }
    
    private static void setUpLogger(){
        try {
            Handler fh = new FileHandler("C:\\matcher-java.log", true);
            fh.setFormatter(new SimpleFormatter());
            LOG.addHandler(fh);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}