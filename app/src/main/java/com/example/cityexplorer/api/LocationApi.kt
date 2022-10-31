package com.example.cityexplorer.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.lang.reflect.Type

interface LocationApi {
    companion object {
        fun create(): LocationApi {
            return createWithUrl(httpurl)
        }

        // Keep the base URL simple
        // example base URL is "https://geocode.maps.co/"
        var httpurl = HttpUrl.Builder()
            .scheme("https")
            .host("geocode.maps.co")
            .build()

        /**
         * The
         * @param httpUrl is the base URL for the API to get the Latitude and Longitude
         *  for a Location.
         * */
        private fun createWithUrl(httpUrl: HttpUrl): LocationApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor().apply {
                        // Enable basic HTTP logging to help with debugging.
                        this.level = HttpLoggingInterceptor.Level.BASIC
                    }
                ).build()

            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .build()
                .create(LocationApi::class.java)
        }
    }


    class Deserializer: JsonDeserializer<String> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): String {
            return json?.asString ?: ""
        }
    }

    /**
     * This class is used to deserialize the JSON response from the API.
     * */
    class LocationResponse(val locations: List<Location>)

}