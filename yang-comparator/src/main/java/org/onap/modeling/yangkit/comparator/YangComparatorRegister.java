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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.yangcentral.yangkit.base.YangBuiltinKeyword;


public class YangComparatorRegister {
    private static final YangComparatorRegister ourInstance = new YangComparatorRegister();
    private final Map<String, YangStatementComparator> policyMap = new ConcurrentHashMap<>();

    /**
     * get register instance.
     *
     * @return instance
     */
    public static YangComparatorRegister getInstance() {
        return ourInstance;
    }

    private YangComparatorRegister() {
        inlineRegister();
    }

    /**
     * register comparator.
     *
     * @param yangKeyword keyword
     * @param comparator  comparator
     */
    public void registerComparator(String yangKeyword, YangStatementComparator comparator) {
        policyMap.put(yangKeyword, comparator);
    }

    /**
     * get comparator by yang keyword.
     *
     * @param yangKeyword yang keyword
     * @return comparator
     */
    public YangStatementComparator getComparator(String yangKeyword) {
        if (yangKeyword == null) {
            return new CommonYangStatementComparator();
        }
        YangStatementComparator comparator = policyMap.get(yangKeyword);
        if (comparator == null) {
            return new CommonYangStatementComparator();
        }
        return comparator;
    }

    private void inlineRegister() {
        //config
        registerComparator(YangBuiltinKeyword.CONFIG.getKeyword(), new ConfigComparator());
        //enum,bit
        registerComparator(YangBuiltinKeyword.ENUM.getKeyword(), new EnumBitComparator());
        registerComparator(YangBuiltinKeyword.BIT.getKeyword(), new EnumBitComparator());
        //module
        registerComparator(YangBuiltinKeyword.MODULE.getKeyword(), new ModuleComparator());
        registerComparator(YangBuiltinKeyword.SUBMODULE.getKeyword(), new ModuleComparator());
        //namespace
        registerComparator(YangBuiltinKeyword.NAMESPACE.getKeyword(), new NamespaceComparator());
        //schema node
        registerComparator(YangBuiltinKeyword.CONTAINER.getKeyword(), new SchemaNodeComparator());
        registerComparator(YangBuiltinKeyword.LIST.getKeyword(), new SchemaNodeComparator());
        registerComparator(YangBuiltinKeyword.CHOICE.getKeyword(), new SchemaNodeComparator());
        registerComparator(YangBuiltinKeyword.CASE.getKeyword(), new SchemaNodeComparator());
        registerComparator(YangBuiltinKeyword.RPC.getKeyword(), new OperationComparator());
        registerComparator(YangBuiltinKeyword.ACTION.getKeyword(), new OperationComparator());
        registerComparator(YangBuiltinKeyword.INPUT.getKeyword(), new SchemaNodeComparator());
        registerComparator(YangBuiltinKeyword.OUTPUT.getKeyword(), new SchemaNodeComparator());
        registerComparator(YangBuiltinKeyword.NOTIFICATION.getKeyword(), new SchemaNodeComparator());
        registerComparator(YangBuiltinKeyword.LEAFLIST.getKeyword(), new SchemaNodeComparator());
        registerComparator(YangBuiltinKeyword.LEAF.getKeyword(), new SchemaNodeComparator());
        registerComparator(YangBuiltinKeyword.ANYDATA.getKeyword(), new SchemaNodeComparator());
        registerComparator(YangBuiltinKeyword.ANYXML.getKeyword(), new SchemaNodeComparator());
        registerComparator(YangBuiltinKeyword.AUGMENT.getKeyword(), new AugmentComparator());
        //type
        registerComparator(YangBuiltinKeyword.TYPE.getKeyword(), new TypeStatementComparator());
        //value
        registerComparator(YangBuiltinKeyword.VALUE.getKeyword(), new ValuePositionComparator());
        //position
        registerComparator(YangBuiltinKeyword.POSITION.getKeyword(), new ValuePositionComparator());
        //range
        registerComparator(YangBuiltinKeyword.RANGE.getKeyword(), new RangeLengthComparator());
        //length
        registerComparator(YangBuiltinKeyword.LENGTH.getKeyword(), new RangeLengthComparator());
        //pattern
        registerComparator(YangBuiltinKeyword.PATTERN.getKeyword(), new PatternComparator());
        //default
        registerComparator(YangBuiltinKeyword.DEFAULT.getKeyword(), new DefaultComparator());
        //units
        registerComparator(YangBuiltinKeyword.UNITS.getKeyword(), new UnitsComparator());
        //description
        registerComparator(YangBuiltinKeyword.DESCRIPTION.getKeyword(), new DescriptionComparator());
        //reference
        registerComparator(YangBuiltinKeyword.REFERENCE.getKeyword(), new ReferenceComparator());
        //when/must
        registerComparator(YangBuiltinKeyword.WHEN.getKeyword(), new WhenMustComparator());
        registerComparator(YangBuiltinKeyword.MUST.getKeyword(), new WhenMustComparator());
        //mandatory
        registerComparator(YangBuiltinKeyword.MANDATORY.getKeyword(), new MandatoryComparator());
        //max-elements
        registerComparator(YangBuiltinKeyword.MAXELEMENTS.getKeyword(), new MaxElementsComparator());
        //min-elements
        registerComparator(YangBuiltinKeyword.MINELEMENTS.getKeyword(), new MinElementsComparator());
        //base
        registerComparator(YangBuiltinKeyword.BASE.getKeyword(), new BaseComparator());
        //typedef,extension,feature,identity
        registerComparator(YangBuiltinKeyword.TYPEDEF.getKeyword(), new IdentifierComparator());
        registerComparator(YangBuiltinKeyword.EXTENSION.getKeyword(), new IdentifierComparator());
        registerComparator(YangBuiltinKeyword.FEATURE.getKeyword(), new IdentifierComparator());
        registerComparator(YangBuiltinKeyword.IDENTITY.getKeyword(), new IdentifierComparator());
        //if-feature
        registerComparator(YangBuiltinKeyword.IFFEATURE.getKeyword(), new IfFeatureComparator());
        //unique
        registerComparator(YangBuiltinKeyword.UNIQUE.getKeyword(), new UniqueComparator());

    }
}
