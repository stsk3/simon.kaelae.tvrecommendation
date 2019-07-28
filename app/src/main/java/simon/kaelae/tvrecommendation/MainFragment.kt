package simon.kaelae.tvrecommendation

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.leanback.app.BrowseFragment
import androidx.leanback.widget.*

class MainFragment : BrowseFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupUIElements()
        loadRows()
        setupEventListeners()
    }

    private fun setupUIElements() {
        title = getString(R.string.app_name)
        showTitle(true)
    }

    private fun loadRows() {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val cardPresenter = CardPresenter()

        val listRowAdapter = ArrayObjectAdapter(cardPresenter)
        val listRowAdapter2 = ArrayObjectAdapter(cardPresenter)
        val listRowAdapter3 = ArrayObjectAdapter(cardPresenter)


        listRowAdapter.add(MovieList.list[1])
        listRowAdapter.add(MovieList.list[2])
        listRowAdapter.add(MovieList.list[0])
        listRowAdapter2.add(MovieList.list[4])
        listRowAdapter2.add(MovieList.list[5])
        listRowAdapter2.add(MovieList.list[3])
        listRowAdapter3.add(MovieList.list[6])
        listRowAdapter3.add(MovieList.list[7])


        val header = HeaderItem(0, "PCCW")
        val header2 = HeaderItem(1, "i-Cable")
        val header3 = HeaderItem(2, "RTHK")


        rowsAdapter.add(ListRow(header, listRowAdapter))
        rowsAdapter.add(ListRow(header2, listRowAdapter2))
        rowsAdapter.add(ListRow(header3, listRowAdapter3))


        adapter = rowsAdapter
    }

    private fun setupEventListeners() {
        onItemViewClickedListener = ItemViewClickedListener()
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder,
            item: Any,
            rowViewHolder: RowPresenter.ViewHolder,
            row: Row
        ) {
            if (item is Movie) {
                val intent = Intent(activity, PlaybackActivity::class.java)
                intent.putExtra(DetailsActivity.MOVIE, item)
                startActivity(intent)
            } else if (item is String) {
                Toast.makeText(activity, item, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
