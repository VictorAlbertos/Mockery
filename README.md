# Android and Java library for mocking and testing server responses. 

Mockery is designed for **testing and mocking networking layers** helping to mock **DTO**s and **auto-generates unit tests** to ensure that the contract between a client application and an API is fulfilled. For that, Mockery operates as follows: 

* **Mock server responses** using Java `interfaces` and `annotations`. 
* **Validate server responses** using Java `interfaces`, `annotations` and [JUnit](https://github.com/junit-team/junit4).
* **Fully extensible API** to support any variety of networking libraries, mockery or validation specs; with **built-in support for** common validations and popular networking libraries such as **[Retrofit](https://github.com/square/retrofit)**. 


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

Mockery without support for any specific network library: 

```gradle
dependencies 
    compile 'com.github.VictorAlbertos.Mockery:core:0.0.2'
}
```

Mockery supporting Retrofit with responses of type `Call<T>`:

```gradle
dependencies {
    compile 'com.github.VictorAlbertos.Mockery:extension_retrofit:0.0.2'
}
```

Mockery supporting Retrofit with responses of type `Observable<T>` and `Observable<Response<T>>`:

```gradle
dependencies {
    compile 'com.github.VictorAlbertos.Mockery:extension_rx_retrofit:0.0.2'
}
```

Once selected the dependency for mocking responses, **add next dependency** using [android-apt plugin](https://bitbucket.org/hvisser/android-apt) **so Mockery can generate the unit tests** which stands up as the contract between your client application and the API:

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
    apt 'com.github.VictorAlbertos.Mockery:test_compiler:0.0.2'
  	provided 'com.github.VictorAlbertos.Mockery:test_runtime:0.0.2'
  	provided 'org.glassfish:javax.annotation:10.0-b28'
  	provided 'junit:junit:4.12'
}
```

## Usage

Create an `interface` with as much methods as needed to gather the API endpoints. This `interface` needs to be annotated with one of the following [@Interceptor](https://github.com/VictorAlbertos/Mockery/blob/master/core/src/main/java/io/victoralbertos/mockery/api/Interceptor.java) annotations:
* @Bypass
* @Retrofit
* @RxRetrofit

These interceptors act as extensions, they allow Mockery to adjust itself to specific networking libraries without coupling with any of them. 

### @Bypass interceptor.

Next `interface` shows a puristic usage of *Mockery annotations* without been coupling with any networking library. [@ByPass](https://github.com/VictorAlbertos/Mockery/blob/master/core/src/main/java/io/victoralbertos/mockery/api/built_in_interceptor/Bypass.java) is an `annotation` which allows Mockery to delegate the responsibility of handling responses to the underlying mockery annotations. 

```java
@Bypass
interface RestApi {
  @DTOArgs(UserDTO.class)
  User getUserByName(@Valid(STRING) String username);

  @DTOArgs(UsersDTO.class)
  List<User> getUsers(@Optional int lastIdQueried,
      @Optional int perPage);

  @DTO(ReposDTO.class)
  List<Repo> getRepos(
      @Valid(STRING)  String username,
      @Enum({"all", "owner", "member"}) String type,
      @Enum({"asc", "desc"})String direction);
}
```

This `interface` has to be decorated with *Mockery annotations* depending on your mocking and validating needs ([more here](#mockery_annotations)). But apart from configuring Mockery's behaviour, these annotations are design to be part of the documentation; as a way to inform about the contract between the client and the API. 

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

@Retrofit `annotation` accepts four optional params to **configure the network behaviour**:
* **delay**: set the network round trip delay in milliseconds
* **failurePercent**: set the percentage of calls to fail.
* **variancePercentage**: set the plus-or-minus variancePercentage percentage of the network round trip delay
* **errorResponseAdapter**: adapt the error message from a failure response to mimic the expected one returned by the server.

For a complete Retrofit example using `Call<T>`, there is an [android module](https://github.com/VictorAlbertos/Mockery/tree/master/example_retrofit) dedicated to it.


### @RxRetrofit Interceptor.
Next `interface` is decorated with [@RxRetrofit](https://github.com/VictorAlbertos/Mockery/blob/master/extension_rx_retrofit/src/main/java/io/victoralbertos/mockery/api/built_in_interceptor/RxRetrofit.java) `annotation`, which inducts Mockery to behave in a similar way that it does when it is annotated with @Retrofit `annotation`, but with the difference that the response type is encapsulated in an `Observable<T>` or `Observable<Response<T>>`. 


```java
@RxRetrofit(delay = 2500, failurePercent = 15)
public interface RestApi {

  @GET("/users/{username}")
  @DTOArgs(UserDTO.class)
  Observable<Response<User>> getUserByName(@Valid(STRING) @Path("username") String username);

  @GET("/users")
  @DTOArgs(UsersDTO.class)
  Observable<List<User>> getUsers(@Optional @Query("since") int lastIdQueried,
      @Optional @Query("per_page") int perPage);

  @GET("/users/{username}/repos")
  @DTO(ReposDTO.class)
  Observable<Response<List<Repo>>> getRepos(@Valid(STRING) @Path("username") String username,
      @Enum({"all", "owner", "member"}) @Query("type") String type,
      @Enum({"asc", "desc"}) @Query("direction") String direction);

}
```

As @Retrofit annotation, @Rxretrofit accepts the same values to **configure the behaviour of the server responses**. 

For a complete Retrofit example using `Observable<T>` and `Observable<Response<T>>`, there is another [android module](https://github.com/VictorAlbertos/Mockery/tree/master/example_rx_retrofit) dedicated to it.

### Running mockery on production environment or how to mock server responses.

After done with decorating the RestApi `interface`, instantiate it using `Mockery.Builder<T>` to use it as a normal instance.


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

For every `interface` annotated with *Mockery annotations* a new **java `class` is generated with as much unit tests as needed** to fulfil the requirements expressed by the *Mockery annotations* used to decorated every param method. 

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

The generated code hides its internals details using some sort of [Robot pattern](https://realm.io/news/kau-jake-wharton-testing-robots/), which provides a cleaner lecture for the generated code. Plus, the order in which the tests are executed is based on the position at which the methods were declared in the original `interface`.


## <a name="mockery_annotations"></a> Mockery annotations. 

To configure Mockery for mocking and validating server responses, you need to decorate the `interface` with *Mockery annotations*. Either using the [built-in ones](#built_in_mockery_annotations) or creating a [custom one](#custom_annotations). 

Every *Mockery annotations* has two functions: **supply mock objects and validate that some data meets certain criteria**. And these functions are expressed in two dimensions: the **production code**, where **Mockery mimics the server behaviour** validating the parameter values received and serving a response; and the **test code**, where **Mockery tests the server behaviour** sending the parameter values and validating its responses. 

To whom Mockery servers the data or from whom validates it, it's undefined until you choose where to place the annotation (that's why some annotations are restricted for methods, params or both of them). **The key to understand how Mockery works involves understanding to whom Mockery serves and validates data depending on the place `annotation` (method vs param) and the current environment (production code vs test code)**. 

1. **On a param during production code**, the `annotation` acts as a validator for the parameter value sent to the server, performing the same validations that a server would do to preserve the API requirements. 

2. **On a param during test code**, the `annotation` supplies a legal and an illegal value to test the response of the server. 

3. **On a method during production code**, the `annotation` mocks the response of the server, serving the same kind of data that the server would do to fulfil the API requirements. 

4. **On a method during test code**, the `annotation` validates the server response asserting for success and failure scenarios, depending on if the parameter value sent were a legal or an illegal one.

To sum up, Mockery is a close system of mocking and validating that mimics and tests the specs of your networking layer.

## <a name="built_in_mockery_annotations"></a> Built-in Mockery annotations. 

### Common mockery annotations:
Following *Mockery annotations* are accessible as built-in parts of Mockery's core.

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
  * *value*: an `String` containing a regular expression. Either use one of the available regular expressions listed in [Valid.Template](source) or supply a custom one. 
  * *legal* (optional): an `String` which sets the value to send to the server when running the associated unit test asserting for a success response. If not set, a random value that matches with the supplied regular expression is used. 
  * *illegal* (optional): an `String` which sets the value to send to the server when running the associated unit test asserting for a failure response. If not set, an empty string or a 0 value is used, depending on the associated `type` param.
* **Supported types**: `String`, `Character`, `double`, `Double`, `float`, `Float`, `int`, `Integer`, `long`, `Long`.
* **When to use**: to mock or validate values which match certain regular expressions, like email, phone, id and so on. 
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

### Retrofit mockery annotations:
Following *Mockery annotations* are only supported for those interfaces which has been annotated with `@Retrofit` or `@RxRetrofit` interceptor. They hide the complexity of mocking and validating common Retrofit types. 

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

The process of creating a **custom Interceptor** is very similar to the process of creating a new [Mockery annotation](#mockery_annotation). You have to create the desired `annotation` and decorated it with [@Interceptor](https://github.com/VictorAlbertos/Mockery/blob/master/core/src/main/java/io/victoralbertos/mockery/api/Interceptor.java) `annotation`, which demands as argument a class which implements [Interceptor.Behaviour](https://github.com/VictorAlbertos/Mockery/blob/master/core/src/main/java/io/victoralbertos/mockery/api/Interceptor.java) to define how the annotation should behave. 

But the process to support a new networking library should be done both carefully and properly tested. For that reason, in case you were willing to add support for a new networking library; please open an issue requesting support and we will try to integrate it as a new built-in extension for Mockery. That way the library would grow in new features to natively support other people demands. 


## Author
**Víctor Albertos**

* <https://twitter.com/_victorAlbertos>
* <https://linkedin.com/in/victoralbertos>
* <https://github.com/VictorAlbertos>

Another author's libraries:
----------------------------------------
* [RxCache](https://github.com/VictorAlbertos/RxCache): Reactive caching library for Android and Java.
* [RxActivityResult](https://github.com/VictorAlbertos/RxActivityResult): A reactive-tiny-badass-vindictive library to break with the OnActivityResult implementation as it breaks the observables chain. 
* [RxSocialConnect](https://github.com/FuckBoilerplate/RxSocialConnect-Android): OAuth RxJava extension for Android.
