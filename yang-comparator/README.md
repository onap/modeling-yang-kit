# YANG Comparator

Yang comparator is a tool which can compare two versions of yang releases. It can help users to identify the differences of the two versions.
It can work as a standalone application or as a plugin of [YANG compiler](https://github.com/onap/modeling-yang-kit/tree/master/yang-compiler).

Yang comparator provides three main functions between two versions:

- compare statements
- compare tree
- check the compatibility

## Compare statements
Compare the statements of two yang release versions.

It will identify the statements which could be added, changed or deleted for every yang files between the previous version and current version.
These differences are all textual differences, not the effective differences. For example:

Previous statements:

```yang
leaf foo {
  type string;
}
```

Current statements:

```yang
leaf foo {
   type string;
   mandatory false;
}
```

The difference will be "mandatory false" is added, even though the previous leaf foo is 'mandatory false' by default.

## Compare tree

Compare the schema tree of two yang release versions.

It will identify which schema node paths are added, changed or deleted, and which schema node paths are changed to be deprecated or obsolete.
These differences are effective differences:

- All config missed will be treated to be default value
- All status missed will be treated to be current
- All mandatory missed will be treated to be false
- All min-elements missed will be treated to be 0
- All max-elements missed will be treated to be unbounded
- All ordered-by missed will be treated to be system

For example:

Previous statements:

```yang
  leaf foo {
    type string;
  }
```

Current statements:

```yang
  leaf foo {
    type string;
    mandatory true;
  }
```

The difference is changing 'mandatory' from false to true even though the previous statements have no 'mandatory'.

## Check compatibility

This function will output the compatibility results after comparing two yang release versions.

It allows users to define their own compatible-check rules, if no self-defined rule is provide, it will use the default one.

compatible-check rule is an XML file, every rule MUST be defined by the XML tags listed below:

- rules: the root element of the rule XML file
- rule: a container for rule, it will define a compatibility rule
- rule-id: the identifier for a rule
- statements: the statements what the rule is applied
- statement: the statement what the rule is applied
- condition: the change type of statement to be matched. The change type maybe the values listed below:
    1. [x] added: any sub statement is added.
    2. [x] deleted: any sub statement is deleted.
    3. [x] changed: the meaning has been changed,for example, builtin-type changed for type,value changed for enumeration.
    4. [x] mandatory-added: add mandatory schema node.
    5. [x] sequence-changed: sequence-changed.
    6. [x] expand: expand the scope, for range,it means larger range, for length, it means larger length, for fraction-digits,
         it means a lower value, for min-elements, it means a lower value, for max-elements, it means a higher value,
         for mandatory, it means from true to false, for config, it means from false to true
         for unique, it means one or more attributes are deleted.
    7. [x] reduce: reduce the scope, for range,it means smaller range, for length, it means smaller length, for fraction-digits,
       it means a higher value, for min-elements, it means a higher value, for max-elements, it means a lower value,
       for mandatory, it means from false to true, for config, it means from true to false,
       for unique, it means new attributes are added.
    8. [x] integer-type-changed: for example type from int8 to int16,it is treated non-backward-compatible by default.
    9. [x] any: match any changes
    10. [x] ignore: ignore any changes, it means backward-compatibility for any changes.

- except-condition: condition what will be not matched.
- compatible: compatibility conclusion, non-backward-compatible or backward-compatible are accepted value.
- description: the description of this rule.

## Installation

### Prerequisites

JDK or JRE 1.8 or above

### Obtain code

```shell
git clone "https://gerrit.onap.org/r/modeling/yang-kit"
```

### Build code

```shell
cd yang-kit/yang-comparator/
mvn clean install
```

It will generate yang-comparator-x.y.z-SNAPSHOT.jar and libs directory under the directory target.

Copy yang-comparator-x.y.z-SNAPSHOT.jar and libs to anywhere in your computer.

## Usage

### Standalone application

```shell
java -jar yang-comparator-x.y.z-SNAPSHOT.jar _arguments_

usage: -left --y {yang file or dir]} [--dep {dependency file or dir}] [--cap {capabilities.xml}]
       -right --y {yang file or dir]} [--dep {dependency file or dir}] [--cap {capabilities.xml}]
        -o {output.xml}
        {-tree | -stmt [--filter filter.xml] | -compatible-check [--rule rule.xml] [--filter filter.xml]}
```

#### Example

Download yang files of network-router from https://github.com/Huawei/yang.
There are many different versions of yang files in this repo and you can choose any two versions to do the comparation.

```shell
# Get statement differences
java -jar yang-comparator-x.y.z-SNAPSHOT.jar -left --y yang/8.20.10 -right --y yang/8.21.0 -o out/diff_stmt.xml -stmt

# Get schema node path differences
java -jar yang-comparator-x.y.z-SNAPSHOT.jar -left --y yang/8.20.10 -right --y yang/8.21.0 -o out/diff_tree.xml -tree

# Get compatibility results
java -jar yang-comparator-x.y.z-SNAPSHOT.jar -left --y yang/8.20.10 -right --y yang/8.21.0 -o out/compatibility.xml -compatible-check

# Get compatibility with rule result
java -jar yang-comparator-x.y.z-SNAPSHOT.jar -left --y yang/8.20.10 -right --y yang/8.21.0 -o out/compatibility_rule.xml -compatible-check --rule rules.xml
```

### Plugin of YANG compiler

1. Copy `src/resources/plugins.json` to YANG compiler application directory and modify the plugins.json to
   indicate the class-path of YANG comparator (the class-path MUST point to the directory where yang-comparator-x.y.z-SNAPSHOT.jar lives in).

2. Set the build option (in build.json). The description of plugin parameters can be found in plugins.json. e.g.

```json
{
  "build": {
    "yang": "yang/new",
    "plugin": [
      {
        "name": "yang_comparator",
        "parameter": [
          {
            "name": "old-yang",
            "value": "yang/old"
          },
          {
            "name": "compare-type",
            "value": "compatible-check"
          },
          {
            "name": "result",
            "value": "yang/result_tree.xml"
          }
        ]
      }
    ]
  }
}
```

3. Run compiler, see [YANG compiler](https://github.com/onap/modeling-yang-kit/tree/master/yang-compiler).

