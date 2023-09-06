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

package org.onap.modeling.yangkit.compiler.app;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.onap.modeling.yangkit.compiler.BuildOption;
import org.onap.modeling.yangkit.compiler.PluginInfo;
import org.onap.modeling.yangkit.compiler.Settings;
import org.onap.modeling.yangkit.compiler.YangCompiler;
import org.yangcentral.yangkit.utils.file.FileUtil;

public class YangCompilerRunner {
    private static List<PluginInfo> parsePlugins(File pluginsFile, String str) {
        List<PluginInfo> pluginInfos = new ArrayList<>();
        JsonElement pluginsElement = JsonParser.parseString(str);
        JsonObject jsonObject = pluginsElement.getAsJsonObject();
        JsonObject pluginsObject = jsonObject.get("plugins").getAsJsonObject();
        JsonArray pluginList = pluginsObject.getAsJsonArray("plugin");
        for (int i = 0; i < pluginList.size(); i++) {
            JsonElement pluginElement = pluginList.get(i);
            PluginInfo pluginInfo = PluginInfo.parse(pluginsFile, pluginElement);
            pluginInfos.add(pluginInfo);
        }
        return pluginInfos;
    }

    private static void preparePlugins(YangCompiler yangCompiler) throws IOException, URISyntaxException {
        InputStream inputStream = yangCompiler.getClass().getResourceAsStream("/plugins.json");
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        String result = scanner.hasNext() ? scanner.next() : "";
        if (!result.isEmpty()) {
            List<PluginInfo> pluginInfos = parsePlugins(null, result);
            for (PluginInfo pluginInfo : pluginInfos) {
                yangCompiler.addPluginInfo(pluginInfo);
            }
        }
        File programDir = new File(yangCompiler.getClass().getProtectionDomain().getCodeSource().getLocation()
                .toURI());
        File pluginsDir = new File(programDir.getParentFile(), "plugins");
        if (!pluginsDir.exists()) {
            System.out.println("[WARNING]plugins dir:" + pluginsDir.getAbsolutePath() + " is not exists");
            return;
        }
        File pluginsFile = new File(pluginsDir, "plugins.json");
        if (pluginsFile.exists()) {
            System.out.println("[INFO]reading the information of plugins from:" + pluginsFile.getAbsolutePath());
            List<PluginInfo> pluginInfos = parsePlugins(pluginsFile, FileUtil.readFile2String(pluginsFile));
            for (PluginInfo pluginInfo : pluginInfos) {
                yangCompiler.addPluginInfo(pluginInfo);
            }
        }


    }

    /**
     * main function of yang compiler.
     *
     * @param args arguments
     * @throws IOException        io exception
     * @throws URISyntaxException URI syntax exception
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
        String option = null;
        boolean install = false;
        for (String arg : args) {
            String[] paras = arg.split("=");
            if (paras.length == 2) {
                String para = paras[0];
                String value = paras[1];
                if (para.equals("option")) {
                    option = value;
                }
            } else {
                if (arg.equals("install")) {
                    install = true;
                }
            }
        }
        if (option == null) {
            option = "build.json";
        }
        // get build option
        File optionFile = new File(option);
        if (!optionFile.exists()) {
            System.out.println("The option file:" + option + " is not found.");
            return;
        }
        JsonElement jsonElement = JsonParser.parseString(FileUtil.readFile2String(optionFile));
        BuildOption buildOption = BuildOption.parse(jsonElement);
        // get settings
        String settingsPath = buildOption.getSettings();
        if (settingsPath == null) {
            //if no settings is specified by user, get the settings.json from program directory
            File programDir = new File(YangCompilerRunner.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI());
            File programSettings = new File(programDir.getParentFile(), "settings.json");
            if (programSettings.exists()) {
                settingsPath = programSettings.getAbsolutePath();
            }
        }
        if (settingsPath == null) {
            //if settings.json is not found in program directory, try to find it from user.home
            settingsPath = System.getProperty("user.home")
                    + File.separator
                    + ".yang"
                    + File.separator
                    + "settings.json";
        }
        Settings settings = new Settings();
        File settingsfile = new File(settingsPath);
        if (settingsfile.exists()) {
            settings = Settings.parse(FileUtil.readFile2String(settingsfile));
        }
        YangCompiler compiler = new YangCompiler();
        compiler.setBuildOption(buildOption);
        compiler.setSettings(settings);
        compiler.setInstall(install);
        preparePlugins(compiler);
        compiler.compile();
    }
}
