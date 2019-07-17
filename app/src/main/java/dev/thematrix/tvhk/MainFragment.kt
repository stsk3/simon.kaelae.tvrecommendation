package dev.thematrix.tvhk

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
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
        title = getString(R.string.browse_title)

        headersState = BrowseFragment.HEADERS_HIDDEN
        isHeadersTransitionOnBackEnabled = false
    }

    private fun loadRows() {
        val list = MovieList.list

        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val cardPresenter = CardPresenter()

        val listRowAdapter = ArrayObjectAdapter(cardPresenter)
        for (j in 0 until list.count()) {
            listRowAdapter.add(list[j])
        }

        val header = HeaderItem(0, MovieList.MOVIE_CATEGORY[0])
        rowsAdapter.add(ListRow(header, listRowAdapter))

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
