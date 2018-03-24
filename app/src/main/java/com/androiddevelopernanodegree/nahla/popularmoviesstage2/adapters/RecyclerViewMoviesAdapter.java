package com.androiddevelopernanodegree.nahla.popularmoviesstage2.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androiddevelopernanodegree.nahla.popularmoviesstage2.R;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.activities.MovieDataActivity;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.Result;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.Result.IMAGE_BASE_URL;

/**
 * Created by Nahla on 11/27/2017.
 */

public class RecyclerViewMoviesAdapter extends RecyclerView.Adapter<RecyclerViewMoviesAdapter.MyViewHolder> {

    private List<Result> moviesList;


    public RecyclerViewMoviesAdapter(List<Result> moviesList){
        this.moviesList=moviesList;
    }

    @Override
    public RecyclerViewMoviesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        if(moviesList.get(position).getPosterPath()!=null){
            String url = IMAGE_BASE_URL + moviesList.get(position).getPosterPath();
            Picasso.with(holder.itemView.getContext()).
                load(url)
                .into(holder.posterImageView);
        Log.d("Poster: ",moviesList.get(position).getOriginalTitle());
        }
        holder.titleTextView.setText(moviesList.get(position).getOriginalTitle());

    }

    @Override
    public int getItemCount() {
        Log.d("List size:", String.valueOf(moviesList.size()));
        return moviesList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView posterImageView;
        public TextView titleTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            posterImageView = (ImageView) itemView.findViewById(R.id.movie_poster_image_view_recycler);
            titleTextView = (TextView) itemView.findViewById(R.id.movie_textView_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == itemView.getId()){
                int position = getAdapterPosition();
                Result chosenMovie = moviesList.get(position);
                Intent intent = new Intent(itemView.getContext(), MovieDataActivity.class);
                intent.putExtra("MovieData",chosenMovie);
                itemView.getContext().startActivity(intent);
            }
        }
    }
}
