package com.androiddevelopernanodegree.nahla.popularmoviesstage2.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androiddevelopernanodegree.nahla.popularmoviesstage2.R;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.TrailersResult;

import java.util.List;

/**
 * Created by Bolla on 12/28/2017.
 */

public class RecyclerViewTrailerAdapter extends RecyclerView.Adapter<RecyclerViewTrailerAdapter.TrailerHolder> {

    private Context context;
    private List<TrailersResult> trailersResults;
    private int position;



    public RecyclerViewTrailerAdapter(Context context, List<TrailersResult> trailersResults) {
        this.context = context;
        this.trailersResults = trailersResults;
    }

    @Override
    public TrailerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_item, parent, false);

        return new TrailerHolder(itemView);

    }

    @Override
    public void onBindViewHolder(TrailerHolder holder, int position) {
        holder.trailerNumTV.setText(trailersResults.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return trailersResults.size();
    }

    public class TrailerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView playTrailerIV;
        TextView trailerNumTV;

        public TrailerHolder(View itemView) {
            super(itemView);
            playTrailerIV = itemView.findViewById(R.id.trailer_watch_imageView);
            trailerNumTV = itemView.findViewById(R.id.trailer_name_textView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == itemView.getId()){
                position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailersResults.get(position).getId()));
                    intent.putExtra("VIDEO_ID",trailersResults.get(position).getId());
                    context.startActivity(intent);

                }
            }
        }
    }
}
