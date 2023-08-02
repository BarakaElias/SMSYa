package courses.pluralsight.com.smsya.services

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class BasicAuthInterceptor(username: String, password: String): Interceptor {
    private var credentials: String = Credentials.basic(username, password)


    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        var request = chain.request()
        request = request.newBuilder().header("Authorization", credentials).build()
        return chain.proceed(request)
    }
}
object ServiceBuilder {
    private val username : String = "basic auth username"
    private val password : String = "basic auth password"

    val auth = "Basic "+ Base64.getEncoder().encodeToString(("${username}:${password}").toByteArray())

    val logger : HttpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    val mOkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(logger)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", auth)
                .build()
            chain.proceed(request)
        }
        .build()


    val retrofit: Retrofit = retrofit2.Retrofit.Builder()
        .baseUrl("https://apisms.beem.africa/public/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(mOkHttpClient)
        .build()

    fun <T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }


}
