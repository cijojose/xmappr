

# `@RootElement` #

## Using ##
`@RootElement` is used to map a root class to a root XML element. Root class is a class at the top of the object hierarchy (not class/inheritance hierarchy).

## attribute: name ##
Name attribute defines the name of the root XML element to which this class will be mapped. Allowed forms are: `@Element("element-name")`, `@Element(name="element-name")` or `@Element(value="element-name")`.
If name is not defined, i.e. just plain `@Element` is used, than the XML element name will be derived from class name:
```
@RootElement
class Root{
  .. 
}
```
In this case class `Root` will be mapped to the XML element of the same name: `<root>`.

## attribute: converter ##
Converter attribute is used to override usual converter selection.
Normally a converter is selected based on the field type, unless converter attribute is defined. Class defined as converter must extend either `ValueConverter` or `ElementConverter`.
```
@RootElement(converter=MyCustomConverter.class)
class Root{
}
```

# `@Attribute` #
## Using ##
`@Attribute` is used to define mapping between a class field and a XML attribute.

## attribute: defaultValue ##
Default value is an optional attribute. If defined, it used when XML attribute is missing. Default value does not apply when attribute is empty (`attr=""`) In this case empty value is passed to converter. Most built-in converters will throw an exception in this situation.

## attribute: targetType ##
Target type is used to decide which converter will be used. Target type

## attribute: converter ##
## attribute: format ##

# `@Attributes` #


# `@Element` #



# `@Elements` #



# `@Text` #


# `@Namespaces` #