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

package org.onap.modeling.yangkit.comparator;

/**
 * the abstract class for yang compare result serializer.
 *
 * @author lllyfeng
 * @since 2022-06-01
 */
public abstract class YangCompareResultSerializer<T> {
    private final YangCompareResult yangCompareResult;

    /**
     * constructor.
     *
     * @param yangCompareResult result
     */
    public YangCompareResultSerializer(YangCompareResult yangCompareResult) {
        this.yangCompareResult = yangCompareResult;
    }

    /**
     * serialize result.
     *
     * @return the result of T format.
     */
    public abstract T serialize();

    /**
     * serialize result.
     *
     * @param needChangeType    whether need change type
     * @param needMetaPath      whether need meta path
     * @param needMeta          whether need meta
     * @param needCompatibility whether need compatibility.
     * @return the result of T format.
     */
    public abstract T serialize(boolean needChangeType, boolean needMetaPath, boolean needMeta,
                                boolean needCompatibility);

    /**
     * get compare result.
     *
     * @return result.
     */
    public YangCompareResult getYangCompareResult() {
        return yangCompareResult;
    }

    /**
     * get xml serializer.
     *
     * @param yangCompareResult result
     * @return xml serializer
     */
    public static YangCompareResultSerializer getXmlSerializer(YangCompareResult yangCompareResult) {
        return new YangCompareResultXmlSerializer(yangCompareResult);
    }
}
