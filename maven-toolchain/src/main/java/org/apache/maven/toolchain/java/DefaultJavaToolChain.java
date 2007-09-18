/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.toolchain.java;

import java.io.File;
import java.util.Map;
import org.apache.maven.toolchain.DefaultToolchain;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.Os;

/**
 * @author Milos Kleint
 */
public class DefaultJavaToolChain extends DefaultToolchain implements JavaToolChain
{
    private String javaHome;
    public static final String KEY_JAVAHOME = "jdkHome"; //NOI18N

    public DefaultJavaToolChain() {
        super("jdk");
    }
    
    public String getJavaHome() {
        return javaHome;
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    public String toString() {
        return "JDK[" + getJavaHome() + "]";
    }
    
    
    public String findTool(String toolName) {
        File toRet = findTool(toolName, new File(FileUtils.normalize(getJavaHome())));
        if (toRet != null) {
            return toRet.getAbsolutePath();
        }
        return null;
    }

    public Map getData() {
        Map map = super.getData();
        map.put(KEY_JAVAHOME, javaHome);
        return map;
    }

    public void setData(Map data) {
        super.setData(data);
        javaHome = (String)data.get(KEY_JAVAHOME);
    }

    private static File findTool (String toolName, File installFolder) {
        File bin = new File(installFolder, "bin"); //NOI18N
        if (bin.exists()) {
            File tool = new File (bin, toolName + (Os.isFamily("windows") ? ".exe" : ""));    //NOI18N
            if (tool.exists()) {
                return tool;
            }
        }
        return null;
    }
    
}
