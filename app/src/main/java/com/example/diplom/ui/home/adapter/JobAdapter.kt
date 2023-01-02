package com.example.diplom.ui.home.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.R
import com.example.diplom.databinding.ItemJobBinding
import com.example.diplom.domain.entity.Job
import com.example.diplom.ui.utils.visible
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class JobAdapter(
    private val onEditJobListener: (Job) -> Unit,
    private val onRemoveJobListener: (Job) -> Unit,
    private val onOpenJobLinkListener: (Job) -> Unit,
) : ListAdapter<Job, JobAdapter.JobViewHolder>(JobDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ItemJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(
            binding,
            onEditJobListener,
            onRemoveJobListener,
            onOpenJobLinkListener,
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = getItem(position)
        holder.bind(job)
    }

    class JobViewHolder(
        private val binding: ItemJobBinding,
        private val onEditJobListener: (Job) -> Unit,
        private val onRemoveJobListener: (Job) -> Unit,
        private val onOpenJobLinkListener: (Job) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(job: Job) {
            binding.jobNameTextview.text = job.name
            binding.positionTextview.text = job.position
            if (job.finish != null) {
                binding.jobPeriodTextview.text = "${convertDateFromStringTime(job.start)} - ${convertDateFromStringTime(job.finish)}"
            } else {
                binding.jobPeriodTextview.text = "${convertDateFromStringTime(job.start)} - по настоящее время"
            }

            binding.jobLinkImageview.visible(!job.link.isNullOrEmpty())

            initListeners(job)
        }

        private fun initListeners(job: Job) {
            binding.jobMenuImageview.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.action_edit -> onEditJobListener.invoke(job)
                            else -> onRemoveJobListener.invoke(job)
                        }
                        true
                    }
                }.show()
            }
            binding.jobLinkImageview.setOnClickListener {
                onOpenJobLinkListener.invoke(job)
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun convertDateFromIsoToDateTime(dateTimeString: String, pattern: String = "yyyy-MM-dd'T'HH:mm:ss'Z'"): String? {
            return try {
                val localDateTime: LocalDateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern(pattern))
                val currentZoneId: ZoneId = ZoneId.systemDefault()
                val currentZonedDateTime: ZonedDateTime = localDateTime.atZone(currentZoneId)
                val format: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                format.format(currentZonedDateTime)
            } catch (e: Exception) {
                null
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun convertDateFromStringTime(dateTimeString: String): String {
            return convertDateFromIsoToDateTime(dateTimeString, "yyyy-MM-dd'T'HH:mm:ss'Z'") ?: convertDateFromIsoToDateTime(
                dateTimeString,
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"
            ).orEmpty()
        }
    }

    class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
        override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
            return oldItem == newItem
        }
    }
}