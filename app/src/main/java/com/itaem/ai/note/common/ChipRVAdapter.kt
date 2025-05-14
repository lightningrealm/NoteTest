package notecom.itaem.ai.note.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import notecom.itaem.ai.note.databinding.CheckItemLayoutBinding
import notecom.itaem.ai.note.model.entity.NoteCategory

class ChipRVAdapter(
    var items:List<NoteCategory> = listOf(),
): RecyclerView.Adapter<CommonVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonVH {
        val binding =
            CheckItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CommonVH(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CommonVH, position: Int) {
        val checkItemLayoutBinding = holder.binding as CheckItemLayoutBinding
        checkItemLayoutBinding.apply {
            checkItemTitle.text = items[position].name
            checkItemCheckbox.isChecked = items[position].isSelected
            checkItemCheckbox.setOnCheckedChangeListener {_,isChecked->
                items[position].isSelected = isChecked
                onItemCheck?.invoke(items[position],isChecked)
            }
        }
    }

    fun updateItems(list:List<NoteCategory>){
        items = list
    }

    private var onItemClickListener:((holder: CommonVH)->Unit)? = null
    fun setOnItemClickListener(onItemClickListener: (holder: CommonVH)->Unit){
        this.onItemClickListener = onItemClickListener
    }

    private var onItemLongClickListener:((holder: CommonVH)->Unit)?=null
    fun setOnItemLongClickListener(onItemLongClickListener: (holder: CommonVH) -> Unit){
        this.onItemLongClickListener = onItemLongClickListener
    }

    private var onItemCheck:((item: NoteCategory,isChecked:Boolean)->Unit)? = null
    fun setOnItemCheckListener(onItemCheck: (item: NoteCategory,isChecked:Boolean)->Unit){
        this.onItemCheck = onItemCheck
    }

}