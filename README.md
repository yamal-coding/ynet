# ynet

[![](https://jitpack.io/v/yamal-coding/ynet.svg)](https://jitpack.io/#yamal-coding/ynet/0.0.1)

Small and straightforward lib built over OkHttp3 to make network calls.

To include this lib in your project add Jitpack as a dependency repository in your root build.gradle file.

```
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
        ...
    }
}
```
Then add this dependency in your module:

```
implementation 'com.github.yamal-coding:ynet:0.0.1'
```

## Dependencies

In order to use this lib, you will need to include Gson and OkHttp3 in your project.

```{gradle}
implementation 'com.google.code.gson::gson:2.8.5
implementation 'com.squareup.okhttp3:okhttp:4.2.2'
```

## Example

First declare the type of your request and response. In the request declaration you will be able to select the Http method, the segment paths of the request (relative to the endpoint base url) and a map of query params (optional).

**Note:** Body will be added in future version of the lib along with the rest of Http methods.

```kotlin
class MyRequest : ApiRequest<MyResponse> {
  
  override fun getResponseClass(): Class<MyResponse> = MyResponse::class.java
  
  override fun getMethod(): HttpMethod = HttpMethod.GET
  
  override fun getPathSegments(): String = "resources/items"
  
  override fun getQueryParams(): Map<String, String> = mapOf(
    "from" to "1000",
    "to" to "2000"
  )
  
}

data class MyResponse(
  @SerializedName("items") val cells: List<Int>
)

```

Second, instantiate an object of type `ApiClient` providing your endpoint base url, a `Gson` object where you could previously register your adpaters and an optional logger for API requests and responses (A `BasicApiLogger` is provided with this lib but you can implement your custom logger under `ApiLogger` interface).

```kotlin
val apiClient = ApiClient(
  baseUrl = "http://mydomain.com",
  gson = GsonBuilder().create(),
  logger = BasicApiLogger("MyTag")
)

```

Finally you will be able to make a request by doing the following:

**Note:** You will have to call this method from your own separated thread, ynet doesn't run this operation in a new thread.

```kotlin
apiClient.sendRequest(
  MydRequest(),
  onSuccess = { response ->
    // Whatever
  },
  onError = {
    // Whatever
  }
)
```
