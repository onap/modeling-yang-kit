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
import com.google.gson.JsonParser;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.onap.modeling.yangkit.catalog.ModuleInfo;


public class Settings {
    private URI remoteRepository = URI.create("https://yangcatalog.org/api/");
    private String localRepository = System.getProperty("user.home") + File.separator + ".yang";

    private Proxy proxy;

    private String token;
    private List<ModuleInfo> moduleInfos = new ArrayList<>();

    /**
     * get the remote repository.
     *
     * @return the URI
     */
    public URI getRemoteRepository() {
        return remoteRepository;
    }

    /**
     * set the remote repository.
     *
     * @param remoteRepository remote repository uri
     */
    public void setRemoteRepository(URI remoteRepository) {
        this.remoteRepository = remoteRepository;
    }

    /**
     * get the local repository.
     *
     * @return the path of local repository
     */
    public String getLocalRepository() {
        return localRepository;
    }

    /**
     * set the local repository.
     *
     * @param localRepository the path of local repository.
     */
    public void setLocalRepository(String localRepository) {
        this.localRepository = localRepository;
    }

    /**
     * get the information of modules.
     *
     * @return the list of module information
     */
    public List<ModuleInfo> getModuleInfos() {
        return moduleInfos;
    }

    /**
     * get the information of modules by module name.
     *
     * @param name module name
     * @return the list of matched module information
     */
    public List<ModuleInfo> getModuleInfos(String name) {
        List<ModuleInfo> matched = new ArrayList<>();
        for (ModuleInfo moduleInfo : moduleInfos) {
            if (moduleInfo.getName().equals(name)) {
                matched.add(moduleInfo);
            }
        }
        return matched;
    }

    /**
     * get the latest module information by specified name.
     *
     * @param name module name
     * @return the latest module information
     */
    public ModuleInfo getLatestModuleInfo(String name) {
        List<ModuleInfo> matched = getModuleInfos(name);
        ModuleInfo latest = null;
        for (ModuleInfo moduleInfo : matched) {
            if (latest == null) {
                latest = moduleInfo;
            } else {
                if (moduleInfo.getRevision().compareTo(latest.getRevision()) > 0) {
                    latest = moduleInfo;
                }
            }
        }
        return latest;
    }

    /**
     * get the module information by specified module name and revision.
     *
     * @param name     module name
     * @param revision module revision
     * @return the matched module information
     */
    public ModuleInfo getModuleInfo(String name, String revision) {
        for (ModuleInfo moduleInfo : moduleInfos) {
            if (moduleInfo.getName().equals(name)
                    && moduleInfo.getRevision().equals(revision)) {
                return moduleInfo;
            }
        }
        return null;
    }

    /**
     * get the proxy.
     *
     * @return proxy
     */
    public Proxy getProxy() {
        return proxy;
    }

    /**
     * set proxy.
     *
     * @param proxy proxy
     */
    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    /**
     * get token.
     *
     * @return token string
     */
    public String getToken() {
        return token;
    }

    /**
     * set token.
     *
     * @param token token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * parse settings from json string.
     *
     * @param str json string
     * @return settings
     */
    public static Settings parse(String str) {
        Settings settings = new Settings();
        JsonElement element = JsonParser.parseString(str);
        if (element == null) {
            return settings;
        }
        JsonObject jsonObject = element.getAsJsonObject();
        JsonObject settingInstance = jsonObject.get("settings").getAsJsonObject();
        JsonElement localElement = settingInstance.get("local-repository");
        if (localElement != null) {
            String localRepository = localElement.getAsString();
            if (null != localRepository) {
                settings.setLocalRepository(localRepository);
            }
        }
        JsonElement remoteElement = settingInstance.get("remote-repository");
        if (remoteElement != null) {
            String remoteRepository = remoteElement.getAsString();
            if (null != remoteRepository) {
                settings.setRemoteRepository(URI.create(remoteRepository));
            }
        }

        JsonElement proxyElement = settingInstance.get("proxy");
        if (proxyElement != null) {
            Proxy proxy = Proxy.parse(proxyElement);
            settings.setProxy(proxy);
        }
        JsonElement tokenElement = settingInstance.get("token");
        if (tokenElement != null) {
            settings.setToken(tokenElement.getAsString());
        }
        JsonElement moduleInfosElement = settingInstance.get("module-info");
        if (moduleInfosElement != null) {
            JsonArray moduleInfos = moduleInfosElement.getAsJsonArray();
            for (int i = 0; i < moduleInfos.size(); i++) {
                JsonElement moduleElement = moduleInfos.get(i);
                ModuleInfo moduleInfo = ModuleInfo.parse(moduleElement);
                settings.moduleInfos.add(moduleInfo);
            }
        }

        return settings;
    }
}
