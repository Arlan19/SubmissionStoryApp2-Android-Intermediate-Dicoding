package com.arlanallacsta.submissionstoryapp.story

import androidx.core.util.Pair
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arlanallacsta.submissionstoryapp.R
import com.arlanallacsta.submissionstoryapp.database.Story
import com.arlanallacsta.submissionstoryapp.databinding.ItemStoryUserBinding
import com.arlanallacsta.submissionstoryapp.detail.DetailActivity
import com.arlanallacsta.submissionstoryapp.utils.DateFormater
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.*

class StoryAdapter : PagingDataAdapter<Story, StoryAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: ItemStoryUserBinding) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(context: Context,story: Story) {
            binding.apply {
                tvNamaPengguna.text = story.name
                tvDescription.text = story.description
                val options = RequestOptions()
                    .placeholder(R.drawable.ic_baseline_refresh_24)
                    .error(R.drawable.ic_baseline_error_outline_24)
                Glide.with(context).load(story.photoUrl).apply(options).into(imgGambarUser)
                tvDate.text = DateFormater.formatDate(story.createdAt, TimeZone.getDefault().id)


                root.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            root.context as Activity,
                            Pair(imgGambarUser, "image"),
                            Pair(tvNamaPengguna, "name"),
                            Pair(tvDescription, "description")
                        )

                    Intent(context, DetailActivity::class.java).also { intent ->
                        intent.putExtra(DetailActivity.EXTRA_DATA, story)
                        context.startActivity(intent, optionsCompat.toBundle())
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(holder.itemView.context, story)
        }
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }

}