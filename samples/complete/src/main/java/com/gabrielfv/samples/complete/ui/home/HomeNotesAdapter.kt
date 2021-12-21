package com.gabrielfv.samples.complete.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gabrielfv.samples.complete.databinding.HomeNoteItemBinding

class HomeNotesAdapter(
  private val items: List<HomeNote>,
  private val onItemSelected: (HomeNote) -> Unit
) : RecyclerView.Adapter<HomeNotesAdapter.ViewHolder>() {

  override fun getItemCount(): Int = items.size

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val binding = HomeNoteItemBinding.inflate(inflater, parent, false)
    return ViewHolder(binding, onItemSelected)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(items[position])
  }


  class ViewHolder(
    private val binding: HomeNoteItemBinding,
    private val onItemSelected: (HomeNote) -> Unit
  ) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: HomeNote) = with(binding) {
      noteTitle.text = item.title
      notePreview.text = item.preview
      lastModified.text = item.lastModified
      root.setOnClickListener {
        onItemSelected(item)
      }
    }
  }
}
