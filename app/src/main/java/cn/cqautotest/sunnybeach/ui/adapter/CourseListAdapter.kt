package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.CourseListItemBinding
import cn.cqautotest.sunnybeach.http.glide.GlideApp
import cn.cqautotest.sunnybeach.ktx.isZero
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.model.course.Course

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/22
 * desc   : 课程列表的适配器
 */

class CourseListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<Course.CourseItem, CourseListAdapter.CourseViewHolder>(diffCallback) {

    private var mItemClickListener: (item: Course.CourseItem, position: Int) -> Unit = { _, _ -> }

    fun setOnItemClickListener(block: (item: Course.CourseItem, position: Int) -> Unit) {
        mItemClickListener = block
    }

    inner class CourseViewHolder(val binding: CourseListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onViewAttachedToWindow(holder: CourseViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CourseListAdapter.CourseViewHolder, position: Int) {
        val item = getItem(position) ?: return
        val itemView = holder.itemView
        val binding = holder.binding
        val context = itemView.context
        itemView.setFixOnClickListener {
            mItemClickListener.invoke(item, position)
        }
        GlideApp.with(context)
            .load(item.cover)
            .into(binding.ivCover)
        binding.tvTitle.text = item.title
        binding.ivAvatar.loadAvatar(false, item.avatar)
        binding.tvNickName.text = item.teacherName
        val price = item.price
        val isFree = price.isZero
        val slvPrice = binding.slvPrice
        slvPrice.text = if (isFree) "免费" else "¥ ${item.price}"
        slvPrice.setTextColor(Color.parseColor(if (isFree) "#48D044" else "#007BFF"))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CourseListItemBinding.inflate(inflater, parent, false)
        return CourseViewHolder(binding)
    }

    companion object {
        private val diffCallback =
            itemDiffCallback<Course.CourseItem>({ oldItem, newItem -> oldItem.id == newItem.id }) { oldItem, newItem -> oldItem == newItem }
    }
}