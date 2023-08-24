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


public class Authentication {
    private final String name;
    private final String password;

    /**
     * the constructor.
     * @param name user name
     * @param password password
     */
    public Authentication(String name, String password) {
        this.name = name;
        this.password = password;
    }

    /**
     * get the username.
     * @return user name
     */
    public String getName() {
        return name;
    }

    /**
     * get the password.
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * parse authentication from json element.
     * @param jsonElement json element
     * @return the structure of authentication
     */
    public static Authentication parse(JsonElement jsonElement) {
        JsonObject authenticationObject = jsonElement.getAsJsonObject();
        String name = authenticationObject.get("username").getAsString();
        String passwd = authenticationObject.get("password").getAsString();
        return new Authentication(name, passwd);
    }
}
