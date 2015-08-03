# Getters and setters: mapping to methods #

As of Xmappr 0.9.3 it's possible to map XML nodes to Class methods. A small example:

```
<root a="2.2"/>
```
mapped to:
```
@RootElement
public static class Root {
    
  private float a;

  @Attribute
  public float getA(){
    return a;
  }

  @Attribute
  public void setA(float param){
    this.a = param;
  }
}
```

In this example getA and setA methods will be used when marshalling/unmarshalling.

The rules of mapping to methods are the following:

  1. Any XML node can be mapped: element, text, attribute. So `@Element`, `@Text` and `@Attribute` may be used on methods.
  1. Methods can have any name, but then you must provide the `name` parameter to annotation
  1. If `name` parameter is not provided, then method names must follow getXYZ/setXYZ naming convention, where XYZ is the name of XML node to which a method is mapped.
  1. Getters must take no parameter and return an appropriate type, setters must take one parameter and return void.

All other rules are the same as mapping to fields. The usual annotation attributes can be used: `targetType`, `converter`, `defaultValue`, `format`, etc..