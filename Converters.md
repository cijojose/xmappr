

# About converters #

**NOTE: all example mappings shown in this section are made with annotations. You can make equivalent mappings via XML configuration as described in [quick start](QuickStart.md).**

Converters are used to convert XML nodes to Java types. Xmappr automatically chooses the right converter based on the field type to which given XML node is mapped. There are two abstract converter types: `ValueConverter` and `ElementConverter`. Each concrete converter extends one of them. Value converters take a simple string value and convert it to object of target type. Element converters are more complex: they convert whole XML sub-elements. They take the XML parser as input, traverse the whole XML sub-element and create an object of target type. Value converters are used to convert attribute and text values, while element converters are used to convert whole sub-element trees.

But don’t fret: Xmappr chooses the right converter automatically based on the target field type:
```
@RootElement
public static class Root {

    @Attribute
    public float a;

    @Element
    public Integer node;

    @Text
    public String text;
}
```
In this example field ‘a’ will be automatically handled by `FloatConverter`, field ‘node’ by `IntConverter` and ‘text’ by `StringConverter`. Putting right annotation on the field is all that user needs to do for this to happen.

# Built-in converters #

Xmappr comes with a basic set of value converters that handle most common Java types: `String, int, long, short, boolean, float, double, byte, char, BigDecimal, BigInteger, byte array (byte[]) and Date`. There are also a few basic element converter types: `ClassConverter`, `CollectionConverter` and a few others used internally. `ClassConverter` is the most complex of converters and it’s task is to handle users classes. `CollectionConverter` is used when target field is a `java.util.Collection`.

# Custom converters #

While built-in converters are sufficient for most common situations, there will be times when you’ll need to roll your own. To do this you’ll need to extend either `ValueConverter` or `ElementConverter`. If your target field maps to XML attribute, XML text value or a “simple” XML sub-element, than you need to extend `ValueConverter`. “Simple” in this case means that XML subelement only contains a text value.

But if your target field maps to XML sub-element that contains attributes or other sub-elements, then you need to extend the `ElementConverter`.

Let’s look at this XML for example:

```
<root a='aaa'>
  some text
  <simple>5</simple>
  <complex c='ccc'>
    more text
   <subsub>even more</subsub>
  </complex>
</root>
```

If we wanted to convert all elements of this XML via custom converters we’d need to write both a value converter and an element converter. XML attribute ‘a’ and text value ’some text’ can be converted by value converter. Also XML sub-element `<simple>` can be converted via value converter, because it only contains a simple text value. On the other hand, sub-element `<complex>` contains text value, an attribute and another sub-element. As such it must be converted via an element converter.

**NOTE:** You need to write a custom converter only if your field’s type is a compiled class, i.e. you don’t have the source to it. If a target type is  one of your own classes, just annotate them (or write XML mapping configuration) and they will be handled by Xmappr automatically.

Once you write your custom converter, you have to register it with Xmappr:

## Using custom converter configuration-wide ##

Custom converters can be added to a top-level configuration so they will be automatically used on all appropriate types. (This is not Xmappr global  – you can create many different Xmappr instances and use them independently. They do not share configuration settings).

```
Xmappr xmappr = new Xmappr(MyClass.class);
xmappr.addConverter(new MyCustomConverter());
```

When converters are added to Xmappr instance they will be picked up automatically for all Java types that they declare to be able to convert.

All `Converter` implementations must implement Converter.canConvert(Class type) method and decide for themselves which classes they can convert. Xmappr calls this method on all converters at configuration time to decide which converter can convert the target class.

Multiple custom converters can be configured like this:

```
xmappr.addConverter(new MyCustomConverter());
xmappr.addConverter(new MyOtherConverter());
```

NOTE: order of declaring converters is important! Xmappr will probe declared converters in LIFO order: last added converter will be probed first. Custom converters before built-in ones. In case two converters declare to be able to convert the same type, the one that was added last will be used.

## Assigning converter to a field ##

Converters can be assigned directly to a field via annotations . In this case converter is fixed and is not inferred from field type:
```
public static class Root {

    @Attribute(converter = ExternalConverter.class)
    public ExternalClass a;

    @Element
    public Integer node;

    @Text
    public String text;
}
```

# Writing custom converters #
## Extending `ValueConverter` ##

Writing your own `ValueConverter` is pretty trivial – just extend `ValueConverter` class. An example is Xmappr’s own `IntConverter`:
```
public class IntConverter extends ValueConverter {

    // Indicates if this converter can convert given target type.
    // This is queried during Xmappr configuration.
    public boolean canConvert(Class type) {
        return type.equals(int.class) || Integer.class.isAssignableFrom(type);
    }

    public Object fromValue(String xmlValue, String format) {
        return Integer.valueOf(xmlValue);
    }

    public String toValue(Object object, String format) {
        return ((Integer) object).toString();
    }
}
```

## Extending `ElementConverter` ##

Writing your own element converter is a bit more complex. Element converters are responsible for parsing XML on their own and producing an instance of the target type.  Please take a look at the `DOMelementConverter` class for example.

(this section will be expanded in the future. In the mean time, if you need help with custom converters, as a question on Xmappr forum)

## Thread safety ##

Xmappr is thread safe, i.e. it can be safely invoked from various threads concurrently. This means that also all converters are thread safe. To achieve this they adhere to one of this rules:

  1. Converters must be immutable, which means that they have no internal state or their internal state is also immutable (doesn’t change after initialization).
  1. If they must have internal state which is mutable they should initialize it on first use and save it to `ThreadLocal`.

One of the converters which does this (point 2.) is `DateConverter`. Internally it uses `java.text.DateFormat`, which is slow to initialize (so we don’t want to do it on every invocation) and is also not thread safe. So we initialize it on first use and save it to `ThreadLocal`.

For efficient use of `ThreadLocal` and fast multithreading it’s best to use thread pools. More about it in section on [safe multi-threaded use](ThreadSafety.md).