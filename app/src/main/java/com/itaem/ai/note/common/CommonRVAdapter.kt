package notecom.itaem.ai.note.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class CommonRVAdapter<T>(
    var items:List<T> = listOf(),
    private val layoutId:Int,
    private val setClickView:(CommonVH.()->View)?=null,
    private val bind:CommonVH.(item:T)->Unit
): RecyclerView.Adapter<CommonVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonVH {
        val view = LayoutInflater.from(parent.context).inflate(layoutId,parent,false)
        return CommonVH(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CommonVH, position: Int) {
        holder.bind(items[position])
        val view = setClickView?.invoke(holder)?:holder.itemView
        view.setOnClickListener{
            onItemClickListener?.invoke(holder)
        }
        view.setOnLongClickListener{
            onItemLongClickListener?.invoke(holder)
            true
        }
    }

    fun updateItems(list:List<T>){
        items = list
        notifyDataSetChanged()
    }

    private var onItemClickListener:((holder: CommonVH)->Unit)? = null
    fun setOnItemClickListener(onItemClickListener: (holder: CommonVH)->Unit){
        this.onItemClickListener = onItemClickListener
    }

    private var onItemLongClickListener:((holder: CommonVH)->Unit)?=null
    fun setOnItemLongClickListener(onItemLongClickListener: (holder: CommonVH) -> Unit){
        this.onItemLongClickListener = onItemLongClickListener
    }
}
class CommonVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var binding:ViewBinding? = null
    constructor(binding:ViewBinding):this(binding.root){
        this.binding = binding
    }
}