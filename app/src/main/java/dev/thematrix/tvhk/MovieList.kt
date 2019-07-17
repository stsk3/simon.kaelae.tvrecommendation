/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package dev.thematrix.tvhk

object MovieList {
    val MOVIE_CATEGORY = arrayOf(
        "TV"
    )

    val list: List<Movie> by lazy {
        setupMovies()
    }
    private var count: Long = 0

    private fun setupMovies(): List<Movie> {
        val title = arrayOf(
            "ViuTV 99台",
            "NOW 332 新聞台",
            "NOW 331 直播台",
            "港台電視 31",
            "港台電視 32",
            "有線新聞台"
        )

        val cardImageUrl = arrayOf(
            "https://static.viu.tv/public/images/amsUpload/201701/1484127151250_ChannelLogo99.jpg",
            "https://news.now.com/revamp2014/images/logo.png",
            "https://news.now.com/revamp2014/images/logo.png",
            "https://www.rthk.hk/assets/rthk/images/tv/player/500x281.jpg",
            "https://www.rthk.hk/assets/rthk/images/tv/player/500x281.jpg",
            "https://vignette.wikia.nocookie.net/evchk/images/f/f8/Cablelogo.gif/revision/latest"
        )

        val videoUrl = arrayOf(
            "",
            "",
            "",
            "https://www.rthk.hk/feeds/dtt/rthktv31_https.m3u8",
            "https://www.rthk.hk/feeds/dtt/rthktv32_https.m3u8",
            ""
        )

        val func = arrayOf(
            "viutv99",
            "nowtv332",
            "nowtv331",
            "",
            "",
            "cabletv"
        )

        val list = title.indices.map {
            buildMovieInfo(
                title[it],
                cardImageUrl[it],
                videoUrl[it],
                func[it]
            )
        }

        return list
    }

    private fun buildMovieInfo(
        title: String,
        cardImageUrl: String,
        videoUrl: String,
        func: String
    ): Movie {
        val movie = Movie()
        movie.id = count++
        movie.title = title
        movie.cardImageUrl = cardImageUrl
        movie.videoUrl = videoUrl
        movie.func = func

        return movie
    }
}