package simon.kaelae.tvrecommendation


object MovieList {


    val CATEGORY = arrayOf(
        "Now TV",
        "CableTV",
        "RTHK TV"
    )

    val list: List<Movie> by lazy {
        setupMovies()
    }

    private var count: Int = 0

    private fun setupMovies(): List<Movie> {
        val title = arrayOf(
            "ViuTV",
            "now新聞台",
            "now直播台",
            "香港開電視",
            "有線新聞台",
            "有線直播台",
            "港台電視31",
            "港台電視32"
        )

        val description = arrayOf(
            "Only allow IP Address in HK",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        )

        val cardImageUrl = arrayOf(
            "http://i.imgur.com/xTtwwzS.jpg",
            "http://i.imgur.com/ucm7n4h.jpg",
            "http://i.imgur.com/dydKNhm.jpg",
            "http://i.imgur.com/a2yFl08.jpg",
            "http://i.imgur.com/a7rFbnX.png",
            "http://i.imgur.com/ManpvmP.png",
            "http://i.imgur.com/wnl8bSg.jpg",
            "http://i.imgur.com/1pvaI8X.jpg"
        )

        val videoUrl = arrayOf(
            "",
            "",
            "",
            "http://media.fantv.hk/m3u8/archive/channel2.m3u8",
            "",
            "",
            "https://www.rthk.hk/feeds/dtt/rthktv31_https.m3u8",
            "https://www.rthk.hk/feeds/dtt/rthktv32_https.m3u8"
        )

        val func = arrayOf(
            "viutv99",
            "nowtv332",
            "nowtv331",
            "",
            "cabletv109",
            "cabletv110",
            "",
            ""
        )

        val list = title.indices.map {
            buildMovieInfo(
                title[it],
                description[it],
                cardImageUrl[it],
                videoUrl[it],
                func[it]
            )
        }

        return list
    }

    private fun buildMovieInfo(
        title: String,
        description: String,
        cardImageUrl: String,
        videoUrl: String,
        func: String
    ): Movie {
        val movie = Movie()
        movie.id = count++
        movie.title = title
        movie.description = description
        movie.cardImageUrl = cardImageUrl
        movie.videoUrl = videoUrl
        movie.func = func

        return movie
    }
}