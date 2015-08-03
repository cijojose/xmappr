

# Mapping multiple XML elements to a `Collection` #

Xmappr can map multiple XML elements to a subtype of `java.util.Collection`.

```
<root>
  <number>1</number>
  <number>2</number>
  <number>3</number>
</root>
```

can be mapped to a `java.util.Collection` like this:

```
@RootElement
class Root{

  @Element
  public List<Integer> number;
}
```

Here Xmappr will take every `<number>` element, convert it to `Integer` and add it to an instance of `ArrayList`.

**Note:** Since Xmappr 0.9.3 this is not needed any more - Xmappr now recognizes generic types:
~~Xmappr normally derives target type from field type, but since Java generics' type erasure removes runtime type information, an alternative way for providing type information has to be used. This is the job of `targetType` attribute of the `@Element` annotation. When a target field is a `Collection` this attribute must be provided, otherwise an exception is thrown.~~

## Mapping different elements to a `Collection` ##

Sometimes different XML elements must be mapped to the same `Collection`:

```
<root>
  <one>1</one>
  <two>2.2</two>
  <three>Three</three>
</root>
```

are mapped to:

```
@RootElement
class Root{

  @Elements({
    @Element(name="one", targetType=Integer.class),
    @Element(name="two", targetType=Double.class),
    @Element(name="three", targetType=String.class)
  })
  public List number;
}
```

The `@Elements` annotation wraps multiple `@Element` annotations so that multiple mappings can be defined on one `Collection`. Every `@Element` mapping can have it's own target type or converter defined.

## Wildcard mapping ##

When there are a lot of different XML subelements, they can all be mapped to a single `java.util.Collection` with one `@Element` line.

Given the above XML snippet a wildcard mapping can be done like this:

```
@RootElement
class Root{

  @Element("*")
  public List number;
}
```

Here all subelements of the `<root>` element will be mapped to the `number` field. When using wildcard element names the default converter is `DomElementConverter`, so all matched elements will be converted to `DomElement` instances.

**NOTE:** Wildcard name `"*"` is a special name and NOT a regular expression. Things like `"*a"` are not possible and will not work.

**NOTE2:** There can be only one wildcard mapping used per class. Otherwise Xmappr would not know to which mapping to assign the XML element and would throw an exception.

### Mixing different mappings ###

It's also possible to mix-and-match named `@Element`s and wildcard `@Element`s:

```
@RootElement
class Root{

  @Elements({
    @Element(name="one", targetType=Integer.class),
    @Element("*")
  })
  public List number;
}
```

Element `<one>` will be converted to `Integer`, while `<two>` and `<three>` will be converted to `DomElement`.

### Using custom converter ###
Custom target type or converter can be used also with wildcard mappings:

```
@RootElement
class Root{

  @Element(name="*", converter=MyCustomConverter.class)
  public List numbers;
}
```

In this case all XML elements will be converted to the type that `MyCustomConverter` is producing and added to the `numbers` list.

**NOTE:** If you use a custom converter with a wildcard mapping, then this custom converter must have a way of producing XML element names from Java objects. Take a look at `NumbersConverter` in [WildcardMappingTest.java](http://code.google.com/p/xmappr/source/browse/trunk/src/test/java/org/xmappr/WildcardMappingTest.java) to see how it can be done.

## Mapping XML elements and XML text to a Collection ##

XML element mappings can be mixed with XML text mappings.

```
<root>
  <one>1</one>
  first text
  <two>2</two>
  second text
</root>
```

This XML can be mapped to single `java.util.Collection`:

```
@RootElement
class Root{

  @Element("*")
  @Text
  public List elements;
}
```

List `elements` will contain the following elements in this order: `DomElement`, String("first text"), `DomElement` and String("second text").
**NOTE:** Mixing XML text and XML elements is useful when mapping XHTML.

# Mapping multiple XML attributes to a `Map` #

Multiple XML attributes can be mapped to `java.util.Map` much the same way as XML elements are mapped to a `Collection`, as described in the previous section.
There are however some differences and it's best to explain them on a big example:

```
<root a='alpha' b='10' c='gamma' d='delta' e='epsilon' i='iota' k='3.14' l='lambda' />
```
and corresponding mapping:
```
class Root {
  @Attributes({
    @Attribute(name = "a"),
    @Attribute(name = "b", targetType = Integer.class),
    @Attribute(name = "c"),
    @Attribute("*")})
  public Map firstMap;

   @Attributes({
     @Attribute("i"),
     @Attribute(name = "k", targetType = Double.class),
     @Attribute("l")})
   public Map secondMap;
}
```

The differences to XML element mappings are:
  1. `@Attribute` mappings must be used on subtype of `java.util.Map`, whereas `@Element` is used on `java.util.Collection`.
  1. Default target type for `@Attribute` mapping is `String`, whereas for `@Element` the default target type is `DomElement`.
  1. `@Attribute` mappings can not be mixed with `@Text` mappings, obviously.
  1. XML attributes do not have order, so they can be safely splitted between two `Map`s, as example shows. The same thing can be also done with XML elements, but this would break their order.