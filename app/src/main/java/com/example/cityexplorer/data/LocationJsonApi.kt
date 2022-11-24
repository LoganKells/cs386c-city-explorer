package com.example.cityexplorer.data

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * This interface reads a JSON file (String) and converts it to a List<Location>.
 * */
interface LocationJsonApi {
    companion object {
        fun create(): LocationJsonApi {
            // TODO - remove the HTTP retrofit because we do not use this.
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
        private fun createWithUrl(httpUrl: HttpUrl): LocationJsonApi {
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
                .create(LocationJsonApi::class.java)
        }

        fun convertToJson(locationResponse: LocationResponse): String {
            val gson = GsonBuilder()
                .registerTypeAdapter(LocationResponse::class.java, Deserializer())
                .create()
            val json: String = gson.toJson(locationResponse)
            // Log.d("LocationJsonApi", "writeToJson: $json")
            return json
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