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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;


import org.dom4j.DocumentException;
import org.onap.modeling.yangkit.catalog.ModuleInfo;
import org.onap.modeling.yangkit.catalog.YangCatalog;
import org.onap.modeling.yangkit.plugin.YangCompilerPlugin;
import org.onap.modeling.yangkit.plugin.YangCompilerPluginParameter;
import org.yangcentral.yangkit.base.YangBuiltinKeyword;
import org.yangcentral.yangkit.base.YangElement;
import org.yangcentral.yangkit.common.api.validate.ValidatorRecord;
import org.yangcentral.yangkit.common.api.validate.ValidatorResult;
import org.yangcentral.yangkit.common.api.validate.ValidatorResultBuilder;
import org.yangcentral.yangkit.model.api.schema.ModuleId;
import org.yangcentral.yangkit.model.api.schema.YangSchemaContext;
import org.yangcentral.yangkit.model.api.stmt.Module;
import org.yangcentral.yangkit.model.api.stmt.SubModule;
import org.yangcentral.yangkit.model.api.stmt.YangStatement;
import org.yangcentral.yangkit.parser.YangParser;
import org.yangcentral.yangkit.parser.YangParserEnv;
import org.yangcentral.yangkit.parser.YangParserException;
import org.yangcentral.yangkit.parser.YangYinParser;
import org.yangcentral.yangkit.utils.file.FileUtil;
import org.yangcentral.yangkit.utils.url.URLUtil;
import org.yangcentral.yangkit.writter.YangFormatter;
import org.yangcentral.yangkit.writter.YangWriter;


public class YangCompiler {

    private Settings settings;
    private File yang;

    private final Map<String, PluginInfo> pluginInfos = new ConcurrentHashMap<String, PluginInfo>();

    private Builder builder;

    /**
     * constructor.
     */
    public YangCompiler() {
    }

    /**
     * get the builder.
     *
     * @return builder class
     */
    public Builder getBuilder() {
        return builder;
    }

    /**
     * set builder.
     *
     * @param builder builder
     */
    public void setBuilder(Builder builder) {
        this.builder = builder;
    }

    /**
     * get plugin information.
     *
     * @param name plugin name
     * @return plugin information
     */
    public PluginInfo getPluginInfo(String name) {
        if (pluginInfos.isEmpty()) {
            return null;
        }
        return pluginInfos.get(name);
    }

    /**
     * add a new plugin.
     *
     * @param pluginInfo plugin information
     */
    public void addPluginInfo(PluginInfo pluginInfo) {
        if (pluginInfo == null) {
            return;
        }
        if (getPluginInfo(pluginInfo.getPluginName()) != null) {
            return;
        }
        pluginInfos.put(pluginInfo.getPluginName(), pluginInfo);
    }

    /**
     * get settings.
     *
     * @return settings
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * set settings.
     *
     * @param settings settings
     */
    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    /**
     * get yang directory file handler.
     *
     * @return yang directory file handler.
     */
    public File getYang() {
        return yang;
    }

    /**
     * set yang directory file handler.
     *
     * @param yang file handler
     */
    public void setYang(File yang) {
        this.yang = yang;
    }

    /**
     * get all dependencies of a specified module.
     *
     * @param module module
     * @return dependencies
     */
    private List<ModuleId> getDependencies(Module module) {
        List<ModuleId> dependencies = new ArrayList<>();
        if (module == null) {
            return dependencies;
        }
        List<YangStatement> importStatements = module.getSubStatement(YangBuiltinKeyword.IMPORT.getQName());
        for (YangStatement importStatement : importStatements) {
            String moduleName = importStatement.getArgStr();
            String revision = null;
            List<YangStatement> revisions = importStatement.getSubStatement(YangBuiltinKeyword.REVISIONDATE.getQName());
            if (revisions.isEmpty()) {
                revision = "";
            } else {
                revision = revisions.get(0).getArgStr();
            }
            ModuleId moduleId = new ModuleId(moduleName, revision);
            dependencies.add(moduleId);
        }
        List<YangStatement> includeStatements = module.getSubStatement(YangBuiltinKeyword.INCLUDE.getQName());
        for (YangStatement includeStatement : includeStatements) {
            String moduleName = includeStatement.getArgStr();
            String revision = null;
            List<YangStatement> revisions =
                    includeStatement.getSubStatement(YangBuiltinKeyword.REVISIONDATE.getQName());
            if (revisions.isEmpty()) {
                revision = "";
            } else {
                revision = revisions.get(0).getArgStr();
            }
            ModuleId moduleId = new ModuleId(moduleName, revision);
            dependencies.add(moduleId);
        }
        if (module instanceof SubModule) {
            List<YangStatement> belongsToStatements = module.getSubStatement(YangBuiltinKeyword.BELONGSTO.getQName());
            String moduleName = belongsToStatements.get(0).getArgStr();
            ModuleId moduleId = new ModuleId(moduleName, "");
            dependencies.add(moduleId);
        }
        return dependencies;
    }

    private String urlInvoke(String url) throws IOException {
        URL catalogUrl = URI.create(url).toURL();
        Proxy proxy = null;
        Authenticator authenticator = null;
        if (settings.getProxy() != null) {
            String protocol = catalogUrl.getProtocol();
            Proxy.Type proxyType = null;
            if (protocol.equalsIgnoreCase("http") || protocol.equalsIgnoreCase("https")) {
                proxyType = Proxy.Type.HTTP;
            } else {
                proxyType = Proxy.Type.SOCKS;
            }
            proxy = new Proxy(proxyType,
                    new InetSocketAddress(settings.getProxy().getHostName(), settings.getProxy().getPort()));
            if (settings.getProxy().getAuthentication() != null) {
                authenticator = new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(settings.getProxy().getAuthentication().getName(),
                                settings.getProxy().getAuthentication().getPassword().toCharArray()
                        );
                    }
                };
                Authenticator.setDefault(authenticator);
            }

        }
        return URLUtil.URLGet(catalogUrl, proxy, authenticator, settings.getToken());
    }

    private File getFromLocal(ModuleId moduleId) {
        File localRepository = new File(settings.getLocalRepository());
        if (!localRepository.exists()) {
            localRepository.mkdirs();
        }
        String prefix =
                moduleId.getModuleName() + (moduleId.getRevision().equals("") ? "" : "@" + moduleId.getRevision());
        String suffix = ".yang";
        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (dir != localRepository) {
                    return false;
                }
                if (name.startsWith(prefix) && name.endsWith(suffix)) {
                    String moduleDesc = name.substring(0, name.indexOf(suffix));
                    String[] moduleInfo = moduleDesc.split("@");
                    String moduleName = moduleInfo[0];
                    String revision = moduleInfo[1];
                    if (!moduleName.equals(moduleId.getModuleName())) {
                        return false;
                    }
                    if (moduleId.getRevision() != null && moduleId.getRevision().length() > 0) {
                        if (!revision.equals(moduleId.getRevision())) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
        };
        System.out.print("[INFO]finding module:" + moduleId.getModuleName()
                + (moduleId.getRevision().isEmpty() ? "" : moduleId.getRevision())
                + " from " + localRepository.getAbsolutePath()
                + " ...");
        File[] matched = localRepository.listFiles(filenameFilter);
        if (matched == null || matched.length == 0) {
            System.out.println("not found.");
            return null;
        }
        if (matched.length == 1) {
            System.out.println("get " + matched[0].getName());
            return matched[0];
        }
        File latest = null;
        if (matched.length > 1) {
            for (File file : matched) {
                if (latest == null) {
                    latest = file;
                } else {
                    if (file.getName().compareTo(latest.getName()) > 0) {
                        latest = file;
                    }
                }
            }
        }
        System.out.println("get " + latest.getName());
        return latest;
    }

    private File getFromRemote(ModuleId moduleId) throws IOException {
        ModuleInfo moduleInfo = null;
        if (moduleId.getRevision() == null || moduleId.getRevision().equals("")) {
            String url = settings.getRemoteRepository() + "search/name/" + moduleId.getModuleName();
            System.out.print("[INFO]downloading module info:" + moduleId.getModuleName() + " from " + url + " ...");
            YangCatalog yangCatalog = YangCatalog.parse(urlInvoke(url));
            moduleInfo = yangCatalog.getLatestModule(moduleId.getModuleName());
            System.out.println(moduleInfo == null ? " not found." : "revision=" + moduleInfo.getRevision());

        } else {
            String organization = moduleId.getModuleName().substring(0, moduleId.getModuleName().indexOf("-"));
            if (organization.equals("junos")) {
                organization = "juniper";
            }
            String url = settings.getRemoteRepository()
                    + "search/modules/"
                    + moduleId.getModuleName()
                    + ","
                    + moduleId.getRevision()
                    + ","
                    + organization;
            System.out.print("[INFO]downloading module info:"
                    + moduleId.getModuleName()
                    + " from "
                    + url
                    + " ...");
            moduleInfo = ModuleInfo.parse(urlInvoke(url));
            System.out.println(moduleInfo == null ? " not found." : "finished");

        }
        if (moduleInfo == null) {
            return null;
        }
        System.out.print("[INFO]downloading content of module:" + moduleInfo.getName() + "@" + moduleInfo.getRevision()
                + " from " + moduleInfo.getSchema().toString() + " ...");
        String yangString = urlInvoke(moduleInfo.getSchema().toString());
        System.out.println("finished.");
        File localRepositoryFile = new File(settings.getLocalRepository());
        if (!localRepositoryFile.exists()) {
            localRepositoryFile.mkdirs();
        }
        String yangfileName = moduleInfo.getName() + "@" + moduleInfo.getRevision() + ".yang";
        File localYangFile = new File(localRepositoryFile, yangfileName);
        FileUtil.writeUtf8File(yangString, localYangFile);
        System.out.println("[INFO]save module:"
                + moduleInfo.getName()
                + "@"
                + moduleInfo.getRevision()
                + " to "
                + localYangFile.getAbsolutePath());
        return localYangFile;
    }

    private File getFromSettings(ModuleId moduleId) throws IOException {
        ModuleInfo moduleInfo = null;
        if (moduleId.getRevision() == null || moduleId.getRevision().equals("")) {
            moduleInfo = settings.getLatestModuleInfo(moduleId.getModuleName());
            System.out.print("[INFO]getting module info:"
                    + moduleId.getModuleName()
                    + " from settings");
            System.out.println(moduleInfo == null ? " not found." : "revision="
                    + moduleInfo.getRevision());

        } else {
            System.out.print("[INFO]getting module info:"
                    + moduleId.getModuleName()
                    + " from settings.");
            moduleInfo = settings.getModuleInfo(moduleId.getModuleName(), moduleId.getRevision());
            System.out.println(moduleInfo == null ? " not found." : "finished");

        }
        if (moduleInfo == null) {
            return null;
        }
        System.out.print("[INFO]downloading content of module:"
                + moduleInfo.getName()
                + "@"
                + moduleInfo.getRevision()
                + " from "
                + moduleInfo.getSchema().toString()
                + " ...");
        String yangString = urlInvoke(moduleInfo.getSchema().toString());
        System.out.println("finished.");
        File localRepositoryFile = new File(settings.getLocalRepository());
        if (!localRepositoryFile.exists()) {
            localRepositoryFile.mkdirs();
        }
        String yangfileName = moduleInfo.getName()
                + "@"
                + moduleInfo.getRevision()
                + ".yang";
        File localYangFile = new File(localRepositoryFile, yangfileName);
        FileUtil.writeUtf8File(yangString, localYangFile);
        System.out.println("[INFO]save module:"
                + moduleInfo.getName()
                + "@"
                + moduleInfo.getRevision()
                + " to "
                + localYangFile.getAbsolutePath());
        return localYangFile;
    }

    private void buildDependencies(YangSchemaContext schemaContext, List<ModuleId> dependencies)
            throws IOException, YangParserException, YangCompilerException {
        for (ModuleId moduleId : dependencies) {
            if (schemaContext.getModule(moduleId).isPresent()) {
                continue;
            }
            //firstly,get yang file from local repository
            File file = getFromLocal(moduleId);
            if (file == null) {
                //secondly,if not found,get yang file from settings
                file = getFromSettings(moduleId);
                if (file == null) {
                    //thirdly, if not found, get yang file from remote repository
                    file = getFromRemote(moduleId);
                }
            }
            if (file == null) {
                throw new YangCompilerException("can not find the yang module named:"
                        + moduleId.getModuleName(),
                        moduleId);
            }

            YangParserEnv env = new YangParserEnv();
            env.setCurPos(0);
            env.setYangStr(FileUtil.readFile2String(file));
            env.setFilename(file.getAbsolutePath());
            YangParser yangParser = new YangParser();
            List<YangElement> yangElements = yangParser.parseYang(env.getYangStr(), env);
            schemaContext.getParseResult().put(env.getFilename(), yangElements);
            Module module = null;
            for (YangElement yangElement : yangElements) {
                if (yangElement instanceof Module) {
                    module = (Module) yangElement;
                    schemaContext.addImportOnlyModule(module);
                    break;
                }
            }
            if (module != null) {
                List<ModuleId> subDependencies = getDependencies(module);
                if (!subDependencies.isEmpty()) {
                    buildDependencies(schemaContext, subDependencies);
                }

            }
        }
    }

    /**
     * build yang schema context.
     *
     * @return yang schema context
     */
    public YangSchemaContext buildSchemaContext() {
        if (!yang.exists()) {
            return null;
        }
        try {
            YangSchemaContext schemaContext = YangYinParser.parse(yang);
            for (Module module : schemaContext.getModules()) {
                List<ModuleId> dependencies = getDependencies(module);
                buildDependencies(schemaContext, dependencies);
            }
            return schemaContext;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (YangParserException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (YangCompilerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * compile yang files.
     *
     * @param yangdir      yang directory
     * @param settingsfile the path of settings.json
     * @param install      whether install to local repository
     * @throws IOException        io exception
     * @throws URISyntaxException uri syntax exception
     */
    public void compile(@Nullable String yangdir, @Nullable String settingsfile, boolean install)
            throws IOException, URISyntaxException {
        String defaultSettingsfile =
                System.getProperty("user.home")
                        + File.separator
                        + ".yang"
                        + File.separator
                        + "settings.json";
        Settings settings = null;
        if (settingsfile != null) {
            settings = Settings.parse(FileUtil.readFile2String(settingsfile));
        } else {
            File defaultSettingsFile = new File(defaultSettingsfile);
            if (defaultSettingsFile.exists()) {
                settings = Settings.parse(FileUtil.readFile2String(defaultSettingsfile));
            } else {
                File programDir = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation()
                        .toURI());
                File programSettings = new File(programDir.getParentFile(), "settings.json");
                if (programSettings.exists()) {
                    settings = Settings.parse(FileUtil.readFile2String(programSettings));
                } else {
                    settings = new Settings();
                }

            }
        }

        setSettings(settings);
        File builderFile = new File("build.json");
        if (builderFile.exists()) {
            Builder builder = prepareBuilder(FileUtil.readFile2String(builderFile));
            setBuilder(builder);
            if (yangdir == null) {
                yangdir = builder.getYangDir();
            }
        }
        if (yangdir == null) {
            yangdir = "yang";
            //System.out.println("no yang directory!");
            //System.out.println("Usage: yang-compiler yang={yang directory} [settings={settings.json}] [install]");
            //return;
        }
        File yangDirFile = new File(yangdir);
        if (!yangDirFile.exists()) {
            System.out.println("yang directory:"
                    + yangdir
                    + " is not exist!");
            return;
        }
        setYang(new File(yangdir));
        Properties compilerProps = new Properties();
        compilerProps.setProperty("yang", yangdir);
        if (settings.getLocalRepository() != null) {
            compilerProps.setProperty("local-repository", settings.getLocalRepository());
        }

        if (settings.getRemoteRepository() != null) {
            compilerProps.setProperty("remote-repository", settings.getRemoteRepository().toString());
        }

        preparePlugins(this);

        YangSchemaContext schemaContext = buildSchemaContext();
        ValidatorResult validatorResult = schemaContext.validate();
        if (!validatorResult.isOk()) {
            System.out.println(validatorResult);
            return;
        }
        if (getBuilder() != null) {
            for (PluginBuilder pluginBuilder : getBuilder().getPlugins()) {
                PluginInfo pluginInfo = getPluginInfo(pluginBuilder.getName());
                if (null == pluginInfo) {
                    System.out.println("can not find a plugin named:"
                            + pluginBuilder.getName());
                    continue;
                }
                YangCompilerPlugin plugin = pluginInfo.getPlugin();
                try {
                    List<YangCompilerPluginParameter> parameters = new ArrayList<>();
                    if (!pluginBuilder.getParameters().isEmpty()) {
                        for (ParameterBuilder parameterBuilder : pluginBuilder.getParameters()) {
                            YangCompilerPluginParameter parameter = plugin.getParameter(compilerProps,
                                    parameterBuilder.getName(), parameterBuilder.getValue());
                            if (parameter != null) {
                                parameters.add(parameter);
                            }
                        }
                    }
                    System.out.println("[INFO]call plugin:"
                            + pluginInfo.getPluginName()
                            + " ...");
                    plugin.run(schemaContext, this, parameters);
                    System.out.println("ok.");
                } catch (YangCompilerException e) {
                    System.out.println("[ERROR]"
                            + e.getMessage());
                }
            }
        }
        ValidatorResultBuilder validatorResultBuilder = new ValidatorResultBuilder();
        List<ValidatorRecord<?, ?>> records = validatorResult.getRecords();
        for (ValidatorRecord<?, ?> record : records) {
            if (record.getBadElement() instanceof YangStatement) {
                YangStatement yangStatement = (YangStatement) record.getBadElement();
                if (schemaContext.getModules().contains(yangStatement.getContext().getCurModule())) {
                    validatorResultBuilder.addRecord(record);
                }
            }
        }
        validatorResult = validatorResultBuilder.build();
        if (install && validatorResult.isOk()) {
            for (Module module : schemaContext.getModules()) {
                List<YangElement> elements =
                        schemaContext.getParseResult().get(module.getElementPosition().getSource());
                StringBuilder sb = new StringBuilder();
                for (YangElement element : elements) {
                    String yangStr = YangWriter.toYangString(element, YangFormatter.getPrettyYangFormatter(), null);
                    sb.append(yangStr);
                    sb.append("\n");
                }
                String moduleDesc = module.getArgStr()
                        + (module.getCurRevisionDate().isPresent() ? "@" + module.getCurRevisionDate().get() : "");
                String fileName = settings.getLocalRepository()
                        + File.separator + moduleDesc;
                FileUtil.writeUtf8File(fileName, sb.toString());
                System.out.println("[INFO]install "
                        + moduleDesc
                        + " to "
                        + settings.getLocalRepository());

            }
        }
        System.out.println(validatorResult);
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
            System.out.println("[WARNING]plugins dir:"
                    + pluginsDir.getAbsolutePath()
                    + " is not exists");
            return;
        }
        File pluginsFile = new File(pluginsDir, "plugins.json");
        if (pluginsFile.exists()) {
            System.out.println("[INFO]reading the information of plugins from:"
                    + pluginsFile.getAbsolutePath());
            List<PluginInfo> pluginInfos = parsePlugins(pluginsFile, FileUtil.readFile2String(pluginsFile));
            for (PluginInfo pluginInfo : pluginInfos) {
                yangCompiler.addPluginInfo(pluginInfo);
            }
        }


    }

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

    private static Builder prepareBuilder(String str) {
        JsonElement jsonElement = JsonParser.parseString(str);
        return Builder.parse(jsonElement);
    }
}
