package rezaei.mohammad.plds.formBuilder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.LayoutRes

class SearchAdapter(private val searchableItems: List<String>, @LayoutRes private val spinnerItem: Int) :
    BaseAdapter(),
    Filterable {

    private var newItemList = searchableItems

    override fun getCount(): Int {
        return newItemList.size
    }

    override fun getItem(position: Int): Any? {
        return newItemList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return getDropDownView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view = LayoutInflater.from(parent.context)
            .inflate(spinnerItem, parent, false) as TextView
        view.text = newItemList[position]
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(filter: CharSequence?): FilterResults {
                return FilterResults().apply {
                    if (filter.isNullOrEmpty()) {
                        count = searchableItems.size
                        values = searchableItems
                    } else {
                        searchableItems.filter { it.contains(filter.toString(), true) }
                            .let {
                                count = it.size
                                values = it
                            }
                    }
                }
            }

            override fun publishResults(p0: CharSequence?, result: FilterResults?) {
                newItemList = result?.values as List<String>
                notifyDataSetChanged()
            }
        }
    }
}