package com.goga133.prog_gifs.business_logic

import com.goga133.prog_gifs.data.Gif
import com.goga133.prog_gifs.data.ResponseWrapper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Интерфейс для взаимодействия с интернет-ресурсом.
 */
interface Api {

    /**
     * Метод для получения гиф изображений по разделу.
     *
     * @param - раздел.
     * @param - страница раздела.
     */
    @GET("/{section}/{page}?json=true")
    fun getSectionGIFs(
        @Path("section") section: String,
        @Path("page") page: Int,
    ): Call<ResponseWrapper>

    /**
     * Метод для получения случайного гиф изображения.
     */
    @GET("/random?json=true")
    fun getRandomGif(): Call<Gif>
}