package simon.kaelae.tvrecommendation

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import androidx.leanback.app.BrowseFragment
import androidx.leanback.widget.*
import androidx.core.view.KeyEventDispatcher.dispatchKeyEvent



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
        val sharedPreference = this.getActivity()?.getSharedPreferences("layout", Context.MODE_PRIVATE)
        sharedPreference?.getString("layout", "")
        //Toast.makeText(context,sharedPreference.getString("name", "")!!.split("`").size.toString(),Toast.LENGTH_SHORT).show()
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


        if(sharedPreference?.getString("name", "")!!.split("`").size >1){
            val listRowAdapter4 = ArrayObjectAdapter(cardPresenter)
            for (i in  0 until sharedPreference?.getString("name", "")!!.split("`").size) {
                listRowAdapter4.add(Movie(id= 9,
                    title = sharedPreference?.getString("name", "")!!.split("`")[i],description="",cardImageUrl="https://i.imgur.com/XQnIwzp.png",
                    videoUrl= sharedPreference?.getString("url", "")!!.split("`")[i],func=""))
            }
            listRowAdapter4.removeItems(0,1)


            val header4 = HeaderItem(3, "自選台")
            rowsAdapter.add(ListRow(header4, listRowAdapter4))
        }
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

        )


        {
            if (item is Movie) {
                val intent = Intent(activity, PlaybackActivity::class.java)
                intent.putExtra(DetailsActivity.MOVIE, item)
                intent.putExtra("id",item.id)
                startActivity(intent)
//                val playIntent: Intent = Uri.parse(item.videoUrl).let { uri->
//                    Intent(Intent.ACTION_VIEW, uri)
//                }
//                startActivity(playIntent)
            } else if (item is String) {
                Toast.makeText(activity, item, Toast.LENGTH_SHORT).show()
            }
        }
    }



}
