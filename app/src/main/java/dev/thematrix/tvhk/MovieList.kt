package dev.thematrix.tvhk

object MovieList {
    val list: List<Movie> by lazy {
        setupMovies()
    }

    private var count: Int = 0

    private fun setupMovies(): List<Movie> {
        val title = arrayOf(
            "ViuTV",
            "now 新聞台",
            "now 直播台",
            "有線 新聞台",
            "有線 直播新聞台",
            "港台電視 31",
            "港台電視 32"
        )

        val cardImageUrl = arrayOf(
            "https://static.viu.tv/public/images/amsUpload/201701/1484127151250_ChannelLogo99.jpg",
            "http://www.nettv.live/uploads/18/1-1PG11QQ2114.jpg",
            "http://www.nettv.live/uploads/18/1-1PG11I415a1.jpg",
            "http://cn.itver.cc/wp-content/uploads/2015/05/cable-tv.jpg",
            "http://cn.itver.cc/wp-content/uploads/2015/05/cable-tv.jpg",
            "http://rthk.hk/about/dtt/images/logo_tv31.jpg",
            "http://rthk.hk/about/dtt/images/logo_tv32.jpg"
        )

        val videoUrl = arrayOf(
            "",
            "",
            "",
            "",
            "",
            "https://www.rthk.hk/feeds/dtt/rthktv31_https.m3u8",
            "https://www.rthk.hk/feeds/dtt/rthktv32_https.m3u8"
        )

        val func = arrayOf(
            "viutv99",
            "nowtv332",
            "nowtv331",
            "cabletv109",
            "cabletv110",
            "",
            ""
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