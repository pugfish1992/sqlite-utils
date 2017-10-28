package com.pugfish1992.sqliteutils.example;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements EditFeedDialog.OnFinishEditingListener {

    private FeedAdapter mFeedAdapter;
    private FeedCRUDer mFeedCRUDer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditFeedDialog(null);
            }
        });

        mFeedCRUDer = new FeedCRUDer(getApplicationContext());
        mFeedAdapter = new FeedAdapter(mFeedCRUDer.getAll());
        mFeedAdapter.setOnFeedCardClickListener(new FeedAdapter.OnFeedCardClickListener() {
            @Override
            public void onFeedCardClick(int position) {
                showEditFeedDialog(mFeedAdapter.getFeedAt(position));
            }
        });

        RecyclerView feedList = (RecyclerView) findViewById(R.id.recyc_feed_list);
        feedList.setLayoutManager(new LinearLayoutManager(this));
        feedList.setAdapter(mFeedAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                mFeedCRUDer.deleteFeed(mFeedAdapter.getFeedAt(position));
                mFeedAdapter.removeFeedAt(position);
            }
        };
        new ItemTouchHelper(callback).attachToRecyclerView(feedList);
    }

    private void showEditFeedDialog(Feed feed) {
        EditFeedDialog dialog = EditFeedDialog.newInstance(feed);
        dialog.show(getSupportFragmentManager(), null);
    }

    /**
     * INTERFACE IMPL -> EditFeedDialog.OnFinishEditingListener
     * ---------- */

    @Override
    public void onFinishEditing(@Nullable Feed oldFeed, @NonNull Feed newFeed) {
        // Check if it is a new feed or not
        if (oldFeed == null) {
            newFeed.id = mFeedCRUDer.insert(newFeed);
        } else {
            mFeedCRUDer.update(newFeed);
        }

        mFeedAdapter.addFeed(newFeed);
    }

    /* ------------------------------------------------------------------------------- *
     * ADAPTER CLASS
     * ------------------------------------------------------------------------------- */

    private static class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder> {

        interface OnFeedCardClickListener {
            void onFeedCardClick(int position);
        }

        private SortedList<Feed> mFeeds;
        private OnFeedCardClickListener mOnFeedCardClickListener;

        FeedAdapter(List<Feed> feeds) {
            mFeeds = new SortedList<>(Feed.class,
                    new SortedList.Callback<Feed>() {
                        @Override
                        public int compare(Feed o1, Feed o2) {
                            return Long.valueOf(o2.id).compareTo(o1.id);
                        }

                        @Override
                        public void onChanged(int position, int count) {
                            notifyItemRangeChanged(position, count);
                        }

                        @Override
                        public boolean areContentsTheSame(Feed oldItem, Feed newItem) {
                            return oldItem.equals(newItem);
                        }

                        @Override
                        public boolean areItemsTheSame(Feed item1, Feed item2) {
                            return item1.id == item2.id;
                        }

                        @Override
                        public void onInserted(int position, int count) {
                            notifyItemRangeInserted(position, count);
                        }

                        @Override
                        public void onRemoved(int position, int count) {
                            notifyItemRangeRemoved(position, count);
                        }

                        @Override
                        public void onMoved(int fromPosition, int toPosition) {
                            notifyItemMoved(fromPosition, toPosition);
                        }
                    });

            if (feeds != null) {
                mFeeds.addAll(feeds);
            }
        }

        void setOnFeedCardClickListener(OnFeedCardClickListener onFeedCardClickListener) {
            mOnFeedCardClickListener = onFeedCardClickListener;
        }

        @Override
        public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.feed_card, parent, false);
            final FeedViewHolder holder = new FeedViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnFeedCardClickListener.onFeedCardClick(holder.getAdapterPosition());
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(FeedViewHolder holder, int position) {
            Feed feed = mFeeds.get(position);
            holder.titleView.setText(feed.title);
            holder.subTitleView.setText(feed.subTitle);
        }

        @Override
        public int getItemCount() {
            return mFeeds.size();
        }

        void addFeed(Feed feed) {
            mFeeds.add(feed);
        }

        Feed getFeedAt(int position) {
            return mFeeds.get(position);
        }

        void removeFeedAt(int position) {
            mFeeds.removeItemAt(position);
        }
    }

    /* ------------------------------------------------------------------------------- *
     * VIEW HOLDER CLASS
     * ------------------------------------------------------------------------------- */

    private static class FeedViewHolder extends RecyclerView.ViewHolder {

        TextView titleView;
        TextView subTitleView;

        FeedViewHolder(View view) {
            super(view);
            titleView = view.findViewById(R.id.txt_title);
            subTitleView = view.findViewById(R.id.txt_sub_title);
        }
    }
}
