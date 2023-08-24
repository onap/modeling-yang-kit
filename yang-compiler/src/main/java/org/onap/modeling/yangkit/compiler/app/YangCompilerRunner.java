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

import java.io.IOException;
import java.net.URISyntaxException;
import org.onap.modeling.yangkit.compiler.YangCompiler;


public class YangCompilerRunner {
    /**
     * the main method of yang compiler runner.
     * @param args arguments
     * @throws IOException io exception
     * @throws URISyntaxException uri syntax exception
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
        String yangdir = null;
        String settingsfile = null;
        boolean install = false;
        for (String arg : args) {
            String[] paras = arg.split("=");
            if (paras.length == 2) {
                String para = paras[0];
                String value = paras[1];
                if (para.equals("yang")) {
                    yangdir = value;
                } else if (para.equals("settings")) {
                    settingsfile = value;
                }
            } else {
                if (arg.equals("install")) {
                    install = true;
                }
            }
        }
        YangCompiler compiler = new YangCompiler();
        compiler.compile(yangdir, settingsfile, install);
    }
}
