![logo](misc/grappa-logo.png)

\[Original image courtesy of [clipartbest.com](http://www.clipartbest.com/clipart-Kin5EMyiq)\]

## What this is

This package allows you to write grammars (more precisely, parsing expression grammars, or
[PEG](http://en.wikipedia.org/wiki/Parsing_expression_grammar) for short) entirely in Java. Unlike,
for instance, ANTLR and JavaCC, parsers written with this package do **not** require a
pre-processing phase. Yes, this means you generate your parsers at _runtime_. And there is no DSL.
Pure Java!

License is Apache 2.0. See [here](https://github.com/parboiled1/project-info) for further
information.

This project has two dedicated Google groups:
[grappa-users](http://groups.google.com/d/forum/grappa-users) and
[grappa-devel](http://groups.google.com/d/forum/grappa-devel). They are also available to post by
email.

The current version is **1.0.0-beta.4** (released May 3, 2014). See below for more information.

## Motivation

This is a fork of the original [parboiled](https://github.com/sirthias/parboiled). Its author,
[Mathias Doenitz](https://github.com/sirthias), has since left the Java world for the Scala world
(with [parboiled2](https://github.com/sirthias/parboiled2)).

But parboiled does not deserve to be left stranded, nor its users. It is just too good. No other
package allows you to write grammars entirely in Java... And what is more, it performs really well.
This package is therefore dedicated to continuing its development.

Grappa requires Java 6 or later.

### And, uh, the name?

Well, you write <i>gra</i>mmars and can then generate <i>pa</i>rsers for them. (I also happen to
like the [drink of the same name](http://www.istitutograppa.org/))

## Gradle/maven artifacts

Grappa is available on Maven Central. With Gradle:

```groovy
repositories {
    mavenCentral();
}

dependencies {
    compile(group: "com.github.parboiled1", name: "grappa", version: "1.0.0-beta.4");
}
```

With Maven:

```xml
<dependency>
    <groupId>com.github.parboiled1</groupId>
    <artifactId>grappa</artifactId>
    <version>1.0.0-beta.4</version>
</dependency>
```

## Versioning, features

Grappa uses [semantic versioning](http://semver.org).

The current version is **1.0.0-beta.4**. The complete release notes can be found in file
[`RELEASE-NOTES.md` file](RELEASE-NOTES.md).

 The major new features compared to parboiled1 are:

* much more robust testing: mocks, AssertJ and [**mutation
  testing**](https://github.com/hcoles/pitest);
* Java 8 compatibility;
* generated bytecode is now 1.6 (was 1.5);
* Unicode support: rules for matching a single code point or a range of code points;
* `CharSequence` support for input buffers, which means you can use grappa on very large files using
  [largetext](https://github.com/fge/largetext);
* more basic rules: all rules defined by [RFC 5234, Appendix B,
  section1](https://tools.ietf.org/html/rfc5234#appendix-B.1) are now available by default (except
  for `LWSP`);
* a `join` rule (`join(someRule).using(otherRule).{times,max,min,range}(...)`);
* build system has been switched from sbt to [gradle](http://gradle.org);
* no Scala support anymore;
* possibility to obtain the bytecode of a generated parser;
* one bug fixed (see [here](https://github.com/parboiled1/parboiled-core/issues/1)).

## Example grammars

You can have a first taste by looking at the [examples
package](https://github.com/parboiled1/parboiled-examples). Here is a very simple grammar to match
double quoted strings and does nothing else (you can, of course, do [much
more](https://github.com/parboiled1/parboiled/wiki)):

```java
/*
 * Notes: uses the JoinParser available in 1.0.0-beta.4
 */
@BuildParseTree
public class DoubleQuotedString
    extends JoinParser<Void>
{
    Rule normal()
    {
        return noneOf("\\\"");
    }

    Rule special()
    {
        return string("\\\"");
    }

    Rule content()
    {
        return join(normal()).using(special()).min(0);
    }

    Rule mainRule()
    {
        return sequence('"', content(), '"', EOI);
    }
}
```

## How to build

You will need a JDK 6 or greater use this command to build and install into your local repository:

```
# Unix systems: Linux, MacOSX, *BSD
./gradlew clean test install
# Windows systems
gradlew.bat clean test install
```

