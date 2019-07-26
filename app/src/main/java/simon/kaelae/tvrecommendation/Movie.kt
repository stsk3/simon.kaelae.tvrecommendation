package simon.kaelae.tvrecommendation

import java.io.Serializable

data class Movie(
    var id: Int = 0,
    var title: String = "",
    var description: String = "",
    var cardImageUrl: String = "",
    var videoUrl: String = "",
    var func: String = ""
) : Serializable {

    override fun toString(): String {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", cardImageUrl='" + cardImageUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                '}'
    }

    companion object {
        internal const val serialVersionUID = 727566175075960653L
    }
}
