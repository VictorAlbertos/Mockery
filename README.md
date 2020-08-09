⚠️ This repository is no longer mantained ⚠️

[![Build Status](https://travis-ci.org/VictorAlbertos/Mockery.svg?branch=master)](https://travis-ci.org/VictorAlbertos/Mockery)
<a href="http://www.methodscount.com/?lib=com.github.VictorAlbertos.Mockery%3Acore%3A0.0.3"><img src="https://img.shields.io/badge/Methods count-core: 194 | deps: 727-e91e63.svg"/></a>
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Mockery-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/3910)

# Android and Java library for mocking and testing Retrofit interfaces. 

Tired of writing over and over the same unit tests for testing [Retrofit](https://github.com/square/retrofit) interfaces? Exhausted of asserting for success and failure response depending on the request? Irritated of implementing manually every Retrofit interface to mock server's behaviour? One step away from kill yourself just to get some sort of freedom?

Probably not, because you don't give a shit about all this. You don't test your network layer, neither you don't mock it, and of course you never listen to your mother. And that's good. That's fair enough. You should not do anything that could kill your delicate and delightful spirit. 

You should write a library and that library should do it for you. A library should exists and this library should generate unit tests and mock server responses based on simple rules. That was the thought, and the thought became nothing just like most good ideas.

But I wrote that library -for you, for me, for all that children that cry at harsh nights when not unit tests come. That library is this library, and it is called Mockery and it generates all that crappy boring tests for you, and it mocks the server behaviour  too. You just need to decorate the old good Retrofit interfaces with a bunch of annotations. It's not so hard. 

Mockery is designed for **testing and mocking networking layers**, helping to mock **DTO**s and **auto-generating unit tests** to ensure that the contract between the client application and API is fulfilled. For that, Mockery operates as follows: 

* **Mock server responses** using Java `interfaces` and `annotations`. 
* **Validate server responses** using Java `interfaces`, `annotations` and [JUnit](https://github.com/junit-team/junit4).


## Setup

Add [JitPack](https://github.com/jitpack/jitpack.io/) repository in your build.gradle (top level module):

```gradle
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```
Depending on your needs, **add one of the next dependencies** in the *build.gradle* script of your target module:

Mockery supporting Retrofit with responses of type `Call<T>`:

```gradle
dependencies {
    compile 'com.github.VictorAlbertos.Mockery:extension_retrofit:1.0.2'
}
```

Mockery supporting Retrofit with responses of type `Single<T>`, `Single<Response<T>>` and `Completable`:

```gradle
dependencies {
    compile 'com.github.VictorAlbertos.Mockery:extension_rx2_retrofit:1.0.2'
}
```

Once selected the dependency for mocking responses, **add next dependency** using [android-apt plugin](https://bitbucket.org/hvisser/android-apt) **so Mockery can generate the unit tests** that will stand up as the contract between your client application and the API:

Root *build.gradle* script:

```gradle
dependencies {
     classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
 }
```

Target module *build.gradle* script:

```gradle
apply plugin: 'com.neenbedankt.android-apt'

dependencies {
    apt 'com.github.VictorAlbertos.Mockery:test_compiler:1.0.2'
  	provided 'com.github.VictorAlbertos.Mockery:test_runtime:1.0.2'
  	provided 'org.glassfish:javax.annotation:10.0-b28'
  	provided 'junit:junit:4.12'
}
```

## Usage

Create an `interface` with as much methods as needed to gather the API endpoints. This `interface` needs to be annotated with one of the following [@Interceptor](https://github.com/VictorAlbertos/Mockery/blob/master/core/src/main/java/io/victoralbertos/mockery/api/Interceptor.java) annotations:
* @Retrofit
* @Rx2Retrofit

### @Retrofit Interceptor.
Next `interface` is decorated with [@Retrofit](https://github.com/VictorAlbertos/Mockery/blob/master/extension_retrofit/src/main/java/io/victoralbertos/mockery/api/built_in_interceptor/Retrofit.java) `annotation`, which induces Mockery to take care of every aspect related with mocking/validating responses created by Retrofit when using `Call<T>` type. Mockery behaves the same way as an instance of Retrofit does, regarding *threading and http exceptions*. Actually, the `interface` supplied to Retrofit builder should be the same that the one supplied to Mockery (you can [thanks to Jake Wharton](https://github.com/square/retrofit/issues/1828) for this).

```java
@Retrofit(delay = 2500, failurePercent = 15)
public interface RestApi {

  @GET("/users/{username}")
  @DTOArgs(UserDTO.class)
  Call<User> getUserByName(@Valid(STRING) @Path("username") String username);

  @GET("/users")
  @DTOArgs(UsersDTO.class)
  Call<List<User>> getUsers(@Optional @Query("since") int lastIdQueried,
      @Optional @Query("per_page") int perPage);

  @GET("/users/{username}/repos")
  @DTO(ReposDTO.class)
  Call<List<Repo>> getRepos(
      @Valid(STRING) @Path("username") String username,
      @Enum({"all", "owner", "member"}) @Query("type") String type,
      @Enum({"asc", "desc"}) @Query("direction") String direction);

}
```

@Retrofit `annotation` accepts 4 optional params to **configure the network behaviour**:
* **delay**: set the network's round trip delay in milliseconds
* **failurePercent**: set the percentage of calls to fail.
* **variancePercentage**: set the plus-or-minus variancePercentage percentage of the network round trip delay
* **errorResponseAdapter**: adapt the error message from a failure response to mimic the expected one returned by the server.

As long as the @Retrofit `annotation`, the previous interface has been decorated with *Mockery annotations* to define the way Mokcery mocks and tests the endpoint ([more here](#mockery_annotations)).

For a complete Retrofit example using `Call<T>`, there is an [android module](https://github.com/VictorAlbertos/Mockery/tree/master/example_retrofit) dedicated to it.

### @Rx2Retrofit Interceptor.
Next `interface` is decorated with [@Rx2Retrofit](https://github.com/VictorAlbertos/Mockery/blob/master/extension_rx2_retrofit/src/main/java/io/victoralbertos/mockery/api/built_in_interceptor/Rx2Retrofit.java) `annotation`, which inducts Mockery to behave in a similar way that it does when it is annotated with @Retrofit `annotation`, but with the difference that the response type is encapsulated in an `Single<T>`, `Single<Response<T>>` or `Completable`.


```java
@Rx2Retrofit(delay = 2500, failurePercent = 15)
public interface RestApi {

  @PUT("/users/{username}")
  @NoDTO Completable addUser(@Valid(value = STRING) @Path("username") String username);

  @GET("/users/{username}")
  @DTOArgs(UserDTO.class)
  Single<Response<User>> getUserByName(@Valid(STRING) @Path("username") String username);

  @GET("/users")
  @DTOArgs(UsersDTO.class)
  Single<List<User>> getUsers(@Optional @Query("since") int lastIdQueried,
      @Optional @Query("per_page") int perPage);

  @GET("/users/{username}/repos")
  @DTO(ReposDTO.class)
  Single<Response<List<Repo>>> getRepos(@Valid(STRING) @Path("username") String username,
      @Enum({"all", "owner", "member"}) @Query("type") String type,
      @Enum({"asc", "desc"}) @Query("direction") String direction);

}
```

As @Retrofit annotation, @Rx2Retrofit accepts the same values to **configure the behaviour of the server responses**.

For a complete Retrofit example using RxJava2, there is another [android module](https://github.com/VictorAlbertos/Mockery/tree/master/example_rx2_retrofit) dedicated to it.

### Running mockery on production environment or how to mock server responses.

After being done with decorating the RestApi `interface`, instantiate it using `Mockery.Builder<T>`:

```java
if (BuildConfig.DEBUG) {
  restApi = new Mockery.Builder<RestApi>()
      .mock(RestApi.class)
      .build();
} else {
  restApi = //real implementation, probably using Retrofit.
}
```

### Running mockery on testing environment or how to test server responses.

For every `interface` annotated with *Mockery annotations* a new **java `class` is generated with as much unit tests as needed** to fulfil the requirements expressed by the *Mockery annotations*.

The name of the test generated is the same as the `interface` from which is generated, but appending a *Test_* suffix. So, an interface called *RestApi*, generates a class called *RestApiTest_*. This class is `abstract`, from which you need to extend and implement the only one `abstract` method to provide an instance with the real `interface` implementation.

For example, this `interface`:

```java
@Retrofit
public interface RestApi {

  @GET("/users/{username}")
  @DTOArgs(UserDTO.class) Call<User> getUserByName(
      @Valid(value = STRING, legal = "google") @Path("username") String username);

}
```

Generates this `abstract` class:

```java
@RunWith(OrderedRunner.class)
public abstract class RestApiTest_ {
  @Rule
  public final ExpectedException exception = ExpectedException.none();

  protected abstract RestApi restApi();

  @Test
  @Order(0)
  public void When_Call_getUserByName_With_Illegal_username_Then_Get_Exception() {
    // Init robot tester
    Robot robot = RobotBuilder
                .test(RestApi.class)
                .onMethod("getUserByName")
                .build();

     // Declare value params
    String username = robot.getIllegalForParam(0);

     // Perform and validate response
    Call<User> response = restApi().getUserByName(username);
    exception.expect(AssertionError.class);
    robot.validateResponse(response);
  }

  @Test
  @Order(0)
  public void When_Call_getUserByName_Then_Get_Response() {
    // Init robot tester
    Robot robot = RobotBuilder
                .test(RestApi.class)
                .onMethod("getUserByName")
                .build();

     // Declare value params
    String username = robot.getLegalForParam(0);

     // Perform and validate response
    Call<User> response = restApi().getUserByName(username);
    robot.validateResponse(response);
  }
}
```

And to use it, you just need to extend from it and run the test, as follows:

```java
public final class RestApiTest extends RestApiTest_ {

  @Override protected RestApi restApi() {
    return new Retrofit.Builder()
        .baseUrl("whatever")
        .build().create(RestApi.class);
  }

}
```

The generated code hides its internal details using some sort of [Robot pattern](https://realm.io/news/kau-jake-wharton-testing-robots/), which provides a cleaner legibility of the generated code. Plus, the order in which the tests are executed is based on the position at which the methods were declared in the original `interface`.

If for some reason an specific method requires to not generate its companion test, just annotate it with `@SkipTest` and Mockery will skip it.  

```java
@Retrofit
public interface RestApi {

  @GET("/users/{username}")
  @SkipTest
  @DTOArgs(UserDTO.class) Call<User> getUserByName(
      @Valid(value = STRING, legal = "google") @Path("username") String username);

}
```

## <a name="mockery_annotations"></a> Mockery annotations.

To configure Mockery for mocking and validating server responses, you need to decorate the `interface` with *Mockery annotations*. Either using the [built-in ones](#built_in_mockery_annotations) or creating a [custom one](#custom_annotations).

Every *Mockery annotation* has two functions: **supply mock objects and validate that some data meets certain criteria**. And these functions are expressed in two dimensions: the **production code**, where **Mockery mimics the server behaviour** validating the parameter values received and serving a response; and the **test code**, where **Mockery tests the server behaviour** sending the parameter values and validating its responses.

## <a name="built_in_mockery_annotations"></a> Built-in Mockery annotations.

### Common mockery annotations:
Following *Mockery annotations* are accessible as built-in parts of Mockery's core.

#### @NoDTO
* **Target**: `method`.
* **Arguments**: none.
* **When to use**: when performing calls with `Completable`.
* **How to use**: decorate the method/param with `@NoDTO`.
* **Usage example**:

```java
  //Decorate a method with @DTO.
  @NoDTO Completable putUser(String username);
```

#### @DTO
* **Target**: `method` and `param`.
* **Arguments**: a `Class` which implements [DTO.Behaviour](https://github.com/VictorAlbertos/Mockery/blob/master/core/src/main/java/io/victoralbertos/mockery/api/built_in_mockery/DTO.java) `interface`.
* **Supported types**: the specified generic `type` of `DTO.Behaviour<T>` `interface`, including parameterized types like `List<T>`, `Map<K,V>`, and so on.
* **When to use**: to mock or validate a custom DTO.
* **How to use**: decorate the method/param with `@DTO` supplying a `DTO.Behaviour<T>` implementation.
* **Usage example**:

```java
  //Implement DTO.Behaviour<T> parameterizing the desired model.
  public class ReposDTO implements DTO.Behaviour<List<Repo>> {
    @Override public List<Repo> legal() {
      //Populate list
    }

    @Override public void validate(List<Repo> candidate) throws AssertionError {
      //Validate list
    }
  }

  //Decorate a method or param with @DTO.
  @DTO(ReposDTO.class)
  List<Repo> getRepos(@DTO(ReposDTO.class) List<Repo>);
```

#### @DTOArgs
* **Target**: `method`.
* **Arguments**: a `Class` which implements [DTOArgs.Behaviour](https://github.com/VictorAlbertos/Mockery/blob/master/core/src/main/java/io/victoralbertos/mockery/api/built_in_mockery/DTOArgs.java) `interface`.
* **Supported types**: the specified generic type of `DTOArgs.Behaviour<T>` `interface`, including parameterized types like `List<T>`, `Map<K,V>`, and so on.
* **When to use**: to mock or validate a custom DTO and it is required to access the array of objects representing the values supplied in the method call invocation.
* **How to use**: decorate the method with `@DTOArgs` supplying a `DTOArgs.Behaviour<T>` implementation.
* **Usage example**:

```java
  //Implement DTOArgs.Behaviour<T> parameterizing the desired model.
  public class ReposDTO implements DTOArgs.Behaviour<List<Repo>> {
    @Override public List<Repo> legal(Object[] args) {
      int perPage = (int) args[0];
      //Populate list
    }

    @Override public void validate(List<Repo> candidate) throws AssertionError {
      //Validate list
    }
  }

  //Decorate a method with @DTOArgs.
  @DTOArgs(ReposDTO.class)
  List<Repo> getRepos(int perPage);
```

#### @DTOJson
* **Target**: `param`.
* **Arguments**: a `Class` which implements `DTO.Behaviour<T>` interface.
* **Supported types**: `String`.
* **When to use**: to mock or validate a custom DTO that is serialized as a json `String` representation.
* **How to use**: @DTOJson serialize-deserialize the associated object from-to json, and because of that, it requires that the interface using it is annotated with [JsonConverter](https://github.com/VictorAlbertos/Mockery/blob/master/core/src/main/java/io/victoralbertos/mockery/api/JsonConverter.java), which accepts a [JolyglotGeneric](https://github.com/VictorAlbertos/Jolyglot) instance. To use it, decorate the param with `@DTOJson`, supplying a `DTO.Behaviour<T>` implementation.
* **Usage example**:

```java
  //Implement DTO.Behaviour<T> parameterizing the desired model.
  public class ReposDTO implements DTO.Behaviour<List<Repo>> {
    @Override public List<Repo> legal() {
      //Populate list
    }

    @Override public void validate(List<Repo> candidate) throws AssertionError {
      //Validate list
    }
  }

  //Decorate the interface with @JsonConverter and the param with @DTOJson.
  @JsonConverter(GsonSpeaker.class)
  interface RestApi {
    ...(@DTOJson(ReposDTO.class) String reposJson);
  }
```

#### @Enum
* **Target**: `param`.
* **Arguments**:
  * *value*: an `String[]` containing the enumerated values.
  * *legal* (optional): an `String` which sets the value to send to the server when running the associated unit test asserting for a success response. If not set, a random value from one of the defined enumerations is used.
  * *illegal* (optional): an `String` which sets the value to send to the server when running the associated unit test asserting for a failure response. If not set, an empty string or a 0 value is used, depending on the associated `type` param.
* **Supported types**: `String`, `Character`, `double`, `Double`, `float`, `Float`, `int`, `Integer`, `long`, `Long`.
* **When to use**: to mock or validate a serie of enumerated values.
* **How to use**: decorate the param with `@Enum` supplying an String[] containing the enumerated sequence.
* **Usage example**:

```java
  ...(@Enum({"all", "owner", "member"}) String type);

  ...(@Enum({"0", "1", "2"}) int type);

  ...(@Enum(value = {"all", "owner", "member"}, legal = "owner", illegal = "whatever") String type);

  ...(@Enum(value = {"0", "1", "2"}, legal = "1", illegal = "0") int type);
```

#### <a name="@valid"></a> @Valid
* **Target**: `method` and `param`.
* **Arguments**:
  * *value*: a `String` containing a regular expression. Either use one of the available regular expressions listed in [Valid.Template](source) or supply a custom one.
  * *legal* (optional): a `String` which sets the value to send to the server when running the associated unit test asserting for a success response. If not set, a random value that matches the supplied regular expression is used.
  * *illegal* (optional): a `String` which sets the value to send to the server when running the associated unit test asserting for a failure response. If not set, an empty string or a 0 value is used, depending on the associated `type` param.
* **Supported types**: `String`, `Character`, `double`, `Double`, `float`, `Float`, `int`, `Integer`, `long`, `Long`.
* **When to use**: to mock or validate values that match certain regular expressions, like email, phone, id and so on.
* **How to use**: decorate the method/param with `@Valid` supplying the regular expression.
* **Usage example**:

```java
  import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.EMAIL;
  import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.NUMBER;
  import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.ID;

  @Valid(NUMBER)
  int getCredits(@Valid(EMAIL) String email);

  @Valid(ID)
  int getId(@Valid(EMAIL) String email);
```

#### @Optional
* **Target**: `param`.
* **Arguments**: `none`.
* **Supported types**: The same as the `type` of the param.
* **When to use**: for those params which don't need to be mocked or validated because they are described as optionals on the API specs.
* **How to use**: decorate the param with `@Optional`.
* **Usage example**:

```java
  ...(@Optional String email, @Optional Model model);
```

#### @RequestBodyDTO
* **Target**: `param`.
* **Arguments**: a `Class` which implements `DTO.Behaviour<T>` interface.
* **Supported types**: `RequestBody`.
* **When to use**: to mock or validate a custom DTO that is serialized as a json String and encapsulated it in the content of a `RequestBody`.
* **How to use**: decorate the param with `@RequestBodyDTO` supplying a `DTO.Behaviour<T>` implementation.
* **Usage example**:

```java
  //Implement DTO.Behaviour<T> parameterizing the desired model.
  public class ReposDTO implements DTO.Behaviour<List<Repo>> {
    @Override public List<Repo> legal() {
      //Populate list
    }

    @Override public void validate(List<Repo> candidate) throws AssertionError {
      //Validate list
    }
  }

  //Decorate the param with @RequestBodyDTO.
  ...(@RequestBodyDTO(ReposDTO.class) RequestBody reposJson);
```

#### @RequestBodyValid
* **Target**: `param`.
* **Arguments**: same as [@Valid](#@valid) annotation.
* **Supported types**: `RequestBody`.
* **When to use**: to mock or validate values which match certain regular expressions serialized as the content of a `RequestBody`.
* **How to use**: decorate the param with `@RequestBodyValid` supplying the regular expression.
* **Usage example**:

```java
    import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.EMAIL;
  import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.ID;

  ...(@RequestBodyValid(EMAIL) RequestBody email,
        @RequestBodyValid(ID) RequestBody id);
```

## <a name="custom_annotations"></a> Extending Mockery API with custom annotations.

Mockery offers a very extensible API to provide custom mocking/validation specs as such as support for other networking libraries.

### <a name="mockery_annotation"></a> Mockery annotations for creating new mockery and validations specs.
First create the desired `annotation`. This `annotation` has to be annotated with [@Mockery](https://github.com/VictorAlbertos/Mockery/blob/master/core/src/main/java/io/victoralbertos/mockery/api/Mockery.java) `annotation`, which tells to Mockery that this `annotation` has to be processed as a new component of the library.

```java
@Retention(RUNTIME)
@Target(PARAMETER)
@Mockery(CustomMockery.class)
public @interface Custom {

}
```
@Mockery `annotation` demands as argument a `class` which implements `Mockery.Behaviour<A>`:

```java
/**
 * Define how the annotation should behave.
 * @param <A> the type of the associated annotation.
 */
public final class CustomMockery implements Mockery.Behaviour<Custom> {

  @Override public Object legal(Metadata<Custom> metadata) {
    //Given some criteria, returns a legal value which conforms with that criteria
  }

  @Override public Object illegal(Metadata<Custom> metadata) {
    //Given some criteria, returns an illegal value which does not conform with that criteria
  }

  @Override public void validate(Metadata<Custom> metadata, Object candidate)
      throws AssertionError {
      //Validate if the current object meets some criteria. If not, an AssertionError must be thrown
  }

  @Override public Type[] supportedTypes(Metadata<Custom> metadata) {
    //Return an array containing the supported types for this specific implementation. For instance, String.class
  }

  @Override public boolean isOptional() {
    //If true, no unit test asserting for failure would be generated for the param annotated with this annotation;
  }

}
```
Once you have created the `annotation` and implemented its behaviour with `Mockery.Behaviour<A>`, the `annotation` is ready to be used as any other built-in mockery `annotation`.

```java
  ...(@Custom String email);
```

But before creating any custom `annotation`, think about opening an issue to warning about this missing functionality in order to check if Mockery should support this feature as a built-in annotation.


### Interceptor annotation to add support for new networking libraries.

The process of creating a **custom Interceptor** is very similar to the process of creating a new [Mockery annotation](#mockery_annotation). You have to create the desired `annotation` and decorate it with [@Interceptor](https://github.com/VictorAlbertos/Mockery/blob/master/core/src/main/java/io/victoralbertos/mockery/api/Interceptor.java) `annotation`, which demands as argument a class which implements [Interceptor.Behaviour](https://github.com/VictorAlbertos/Mockery/blob/master/core/src/main/java/io/victoralbertos/mockery/api/Interceptor.java) to define how the annotation should behave.

But the process to support a new networking library should be done both carefully and with proper testing. For that reason, in case you were willing to add support for a new networking library; please open an issue requesting support and we will try to integrate it as a new built-in extension for Mockery. That way the library will grow in new features to natively support other people's demands.

## Author
**Víctor Albertos**

* <https://twitter.com/_victorAlbertos>
* <https://linkedin.com/in/victoralbertos>
* <https://github.com/VictorAlbertos>

Another author's libraries:
----------------------------------------
* [RxCache](https://github.com/VictorAlbertos/RxCache): Reactive caching library for Android and Java.
* [RxActivityResult](https://github.com/VictorAlbertos/RxActivityResult): A reactive-tiny-badass-vindictive library to break with the OnActivityResult implementation as it breaks the observables chain.
* [RxSocialConnect](https://github.com/VictorAlbertos/RxSocialConnect-Android): OAuth RxJava extension for Android.
