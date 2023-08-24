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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;


public class Proxy {
    private final String hostName;
    private final int port;
    private Authentication authentication;

    /**
     * the constructor.
     *
     * @param hostName the host name
     * @param port     the port number
     */
    public Proxy(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    /**
     * the constructor.
     *
     * @param hostName the host name.
     */

    public Proxy(String hostName) {
        this.hostName = hostName;
        this.port = 80;
    }

    /**
     * get the host name.
     *
     * @return host name.
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * get the port number.
     *
     * @return port number.
     */
    public int getPort() {
        return port;
    }

    /**
     * get the authentication information.
     *
     * @return authentication information
     */
    public Authentication getAuthentication() {
        return authentication;
    }

    /**
     * set the authentication information.
     *
     * @param authentication authentication information.
     */
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    /**
     * create a proxy instance from url.
     *
     * @param url url
     * @return proxy
     */
    public static Proxy create(URL url) {
        return new Proxy(url.getHost(), url.getPort() == -1 ? url.getDefaultPort() : url.getPort());
    }

    /**
     * parse proxy from json.
     *
     * @param jsonElement json element
     * @return proxy
     */
    public static Proxy parse(JsonElement jsonElement) {
        Proxy proxy = null;
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonElement urlElement = jsonObject.get("url");
        if (urlElement != null) {
            String urlString = urlElement.getAsString();
            try {
                URL url = URI.create(urlString).toURL();
                proxy = Proxy.create(url);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        } else {
            JsonElement hostElement = jsonObject.get("host");
            if (hostElement != null) {
                String host = hostElement.getAsString();
                int port = -1;
                JsonElement portElement = jsonObject.get("port");
                if (portElement != null) {
                    port = portElement.getAsInt();
                }
                if (port != -1) {
                    proxy = new Proxy(host, port);
                } else {
                    proxy = new Proxy(host);
                }
            }
        }
        JsonElement authenticationElement = jsonObject.get("authentication");
        if (authenticationElement != null) {
            Authentication authentication = Authentication.parse(authenticationElement);
            if (proxy != null) {
                proxy.setAuthentication(authentication);
            }

        }
        return proxy;
    }
}
