package io.github.stack07142.rxjava_android_samples;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RVLoggerAdapter extends ListAdapter<String, RVLoggerAdapter.LoggerItemVH> {
    private RecyclerView rvLogger;
    private List<String> logList;
    private int maxCnt;

    public static final DiffUtil.ItemCallback<String> DIFF_CALLBACK = new DiffUtil.ItemCallback<String>() {
        @Override
        public boolean areItemsTheSame(String oldItem, String newItem) {
            return TextUtils.equals(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(String oldItem, String newItem) {
            return TextUtils.equals(oldItem, newItem);
        }
    };

    public RVLoggerAdapter(int maxCnt) {
        super(DIFF_CALLBACK);
        this.logList = new ArrayList<>(maxCnt);
        this.maxCnt = maxCnt;
    }

    @Override
    public void submitList(List<String> list) {
        super.submitList(list != null ? new ArrayList<>(list) : null);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        rvLogger = recyclerView;
    }

    @NonNull
    @Override
    public LoggerItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView view = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
        return new LoggerItemVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoggerItemVH holder, int position) {
        holder.textView.setText(getItem(position));
    }

    public void add(String msg) {
        if (logList.size() > maxCnt) {
            logList.remove(0);
        }
        logList.add(getThreadName() + " | " + msg);
        submitList(logList);
        rvLogger.scrollToPosition(getItemCount() - 1);
    }

    public void addAll(List<String> msgList) {
        logList.addAll(msgList);

        submitList(logList);
        rvLogger.scrollToPosition(getItemCount() - 1);
    }

    public void clear() {
        logList.clear();
        submitList(logList);
    }

    private String getThreadName() {
        String threadName = Thread.currentThread().getName();
        if (threadName.length() > 30) {
            threadName = threadName.substring(0, 30) + "...";
        }
        return threadName;
    }

    static class LoggerItemVH extends RecyclerView.ViewHolder {
        TextView textView;

        public LoggerItemVH(TextView itemView) {
            super(itemView);
            textView = itemView;
        }
    }
}
