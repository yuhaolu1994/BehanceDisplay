package com.example.yuhaolu.behancedisplay.view.project_detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yuhaolu.behancedisplay.R;
import com.example.yuhaolu.behancedisplay.model.Bucket;
import com.example.yuhaolu.behancedisplay.model.ProjectDetail;
import com.example.yuhaolu.behancedisplay.utils.ImageUtils;
import com.example.yuhaolu.behancedisplay.utils.ModelUtils;
import com.example.yuhaolu.behancedisplay.view.buckets_list.BucketListActivity;
import com.example.yuhaolu.behancedisplay.view.buckets_list.BucketListFragment;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_PROJECT_IMAGE = 0;
    private static final int VIEW_TYPE_PROJECT_DETAIL = 1;

    private final ProjectFragment projectFragment;
    private final ProjectDetail projectDetail;

    public ProjectAdapter(@NonNull ProjectFragment projectFragment, @NonNull ProjectDetail projectDetail) {
        this.projectFragment = projectFragment;
        this.projectDetail = projectDetail;
        projectDetail.bucketedName = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_PROJECT_IMAGE:
                view = LayoutInflater.from(getContext())
                        .inflate(R.layout.project_image, parent, false);
                return new ProjectImageViewHolder(view);
            case VIEW_TYPE_PROJECT_DETAIL:
                view = LayoutInflater.from(getContext())
                        .inflate(R.layout.project_detail, parent, false);
                return new ProjectDetailViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_TYPE_PROJECT_IMAGE:
                ImageUtils.loadShotDetailImage(projectDetail, ((ProjectImageViewHolder) holder).image);
                break;
            case VIEW_TYPE_PROJECT_DETAIL:
                final ProjectDetailViewHolder projectDetailViewHolder = (ProjectDetailViewHolder) holder;
                projectDetailViewHolder.viewCount.setText(String.valueOf(projectDetail.stats.views));
                projectDetailViewHolder.likeCount.setText(String.valueOf(projectDetail.stats.appreciations));
                projectDetailViewHolder.commentCount.setText(String.valueOf(projectDetail.stats.comments));

                projectDetailViewHolder.authorPicture.setImageURI(Uri.parse(projectDetail.owners.get(0).images._50));
                projectDetailViewHolder.title.setText(projectDetail.name);
                projectDetailViewHolder.authorName.setText(projectDetail.owners.get(0).displayName);

                projectDetailViewHolder.description.setText(TextUtils.isEmpty(projectDetail.description) ?
                        "(No description)" : projectDetail.description);
                //设置文本可点击
                //projectDetailViewHolder.description.setMovementMethod(LinkMovementMethod.getInstance());

                projectDetailViewHolder.commentCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), CommentActivity.class);
                        intent.putExtra(CommentFragment.KEY_PROJECT_ID, String.valueOf(projectDetail.id));
                        projectFragment.startActivity(intent);
                    }
                });

                List<Bucket> buckets = ModelUtils.read(projectFragment.getContext(),
                        BucketListFragment.CREATED_BUCKET, new TypeToken<List<Bucket>>(){});

                if (buckets != null) {
                    for (Bucket bucket : buckets) {
                        for (ProjectDetail project : bucket.bucketProjects) {
                            if (projectDetail.name.equals(project.name)) {
                                projectDetail.bucketed = true;
                                projectDetail.bucketedName.add(bucket.getBucketName());
                            }
                        }
                    }
                }

                if (projectDetail.bucketed) {
                    projectDetailViewHolder.bucket.setCompoundDrawablesWithIntrinsicBounds(
                            0, R.drawable.ic_inbox_blue_900_24dp, 0, 0);
                } else {
                    projectDetailViewHolder.bucket.setCompoundDrawablesWithIntrinsicBounds(
                            0, R.drawable.ic_inbox_black_24dp, 0, 0);
                }

                projectDetailViewHolder.bucket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        projectFragment.bucket();
                    }
                });

                break;
        }
    }

    @Override
    public int getItemCount() { return 2; }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_PROJECT_IMAGE;
        } else {
            return VIEW_TYPE_PROJECT_DETAIL;
        }
    }

    @NonNull
    private Context getContext() {
        return projectFragment.getContext();
    }

    public void setProjectBucketed() {
        projectDetail.setBucketed(true);
        notifyDataSetChanged();
    }

    public void setProjectUnBucketed() {
        projectDetail.setBucketed(false);
        notifyDataSetChanged();
    }

}
