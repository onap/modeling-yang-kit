/*
Copyright 2023 Huawei Technologies

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.onap.modeling.yangkit.compiler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.onap.modeling.yangkit.compiler.plugin.YangCompilerPlugin;


@SuppressWarnings("ALL")
public class PluginInfo {
    private final String pluginName;
    private final YangCompilerPlugin plugin;

    private String description;
    private final List<PluginParameterInfo> parameters = new ArrayList<>();

    /**
     * the constructor.
     *
     * @param pluginName plugin name
     * @param plugin     plugin
     */
    public PluginInfo(String pluginName, YangCompilerPlugin plugin) {
        this.pluginName = pluginName;
        this.plugin = plugin;
    }

    /**
     * get the name of plugin.
     *
     * @return name
     */
    public String getPluginName() {
        return pluginName;
    }

    /**
     * get the plugin.
     *
     * @return plugin
     */
    public YangCompilerPlugin getPlugin() {
        return plugin;
    }

    /**
     * get the description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * set the description.
     *
     * @param description description string
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * get the parameters of the plugin.
     *
     * @return the parameters
     */
    public List<PluginParameterInfo> getParameters() {
        return parameters;
    }

    /**
     * add a parameter to plugin.
     *
     * @param parameter parameter
     */
    public void addParameter(PluginParameterInfo parameter) {
        parameters.add(parameter);
    }

    /**
     * parse plugin information from json.
     *
     * @param pluginFile  plugins.json File handler
     * @param jsonElement json element
     * @return plugin information
     */
    public static PluginInfo parse(File pluginFile, JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String pluginName = jsonObject.get("name").getAsString();
        String classPath = null;
        JsonElement classPathElement = jsonObject.get("class-path");
        if (classPathElement != null) {
            classPath = classPathElement.getAsString();
        }
        String className = jsonObject.get("class").getAsString();
        try {
            Class<? extends YangCompilerPlugin> pluginClass = null;
            if (classPath != null && !classPath.trim().isEmpty()) {
                Path path = Paths.get(classPath);
                File file;
                if (!path.isAbsolute()) {
                    file = new File(pluginFile.getParentFile(), classPath);
                } else {
                    file = new File(classPath);
                }
                if (!file.exists()) {
                    System.out.println("[ERROR]the class-path:" + file.getAbsolutePath() + " is not found.");
                    return null;
                }
                URL[] cp = {file.toURI().toURL()};
                try (URLClassLoader classLoader = new URLClassLoader(cp)) {
                    pluginClass = (Class<? extends YangCompilerPlugin>) classLoader.loadClass(className);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                pluginClass = (Class<? extends YangCompilerPlugin>) Class.forName(className);
            }

            Constructor<? extends YangCompilerPlugin> constructor = pluginClass.getConstructor();
            YangCompilerPlugin yangCompilerPlugin = constructor.newInstance();
            PluginInfo pluginInfo = new PluginInfo(pluginName, yangCompilerPlugin);
            if (jsonObject.get("description") != null) {
                String description = jsonObject.get("description").getAsString();
                pluginInfo.setDescription(description);
            }
            JsonElement parasElement = jsonObject.get("parameter");
            if (parasElement != null) {
                JsonArray paraArray = parasElement.getAsJsonArray();
                for (int i = 0; i < paraArray.size(); i++) {
                    JsonElement paraElement = paraArray.get(i);
                    JsonObject para = paraElement.getAsJsonObject();
                    String name = para.get("name").getAsString();
                    PluginParameterInfo parameter = new PluginParameterInfo(name);
                    if (para.get("description") != null) {
                        String description = para.get("description").getAsString();
                        parameter.setDescription(description);
                    }
                    pluginInfo.addParameter(parameter);
                }
            }
            return pluginInfo;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
