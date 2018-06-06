package com.navoki.megamovies.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.navoki.megamovies.BuildConfig;
import com.navoki.megamovies.Callbacks.OnAdapterListener;
import com.navoki.megamovies.Models.MovieData;
import com.navoki.megamovies.R;
import com.navoki.megamovies.Utils.Util;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MovieListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<MovieData> movieList;
    private final Context context;
    private final OnAdapterListener adapterListener;

    private class MovieHolder extends RecyclerView.ViewHolder {
        private final ImageView imgPoster;
        private final TextView tvTitle;
        private final TextView tvYear;

        private MovieHolder(View view) {
            super(view);
            imgPoster = view.findViewById(R.id.img_poster);
            tvTitle = view.findViewById(R.id.tv_title);
            tvYear = view.findViewById(R.id.tv_year);
        }
    }


    public MovieListAdapter(Context context, ArrayList<MovieData> movieList) {
        this.movieList = movieList;
        this.context = context;
        adapterListener = (OnAdapterListener) context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = View.inflate(context, R.layout.item_movie_layout, null);
        return new MovieHolder(view);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final MovieHolder movieHolder = (MovieHolder) holder;
        final MovieData movieData = movieList.get(position);

        movieHolder.tvTitle.setText(movieData.getTitle());
        Picasso.get().load((BuildConfig.POSTER_BASE_URL + movieData.getPoster_path()).trim())
                .placeholder(R.drawable.placeholder_banner)
                .into(movieHolder.imgPoster);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date date = simpleDateFormat.parse(movieData.getRelease_date());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            movieHolder.tvYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        movieHolder.imgPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterListener.moveToDetailsScreen(movieHolder.imgPoster,
                        (BuildConfig.POSTER_BASE_URL + movieData.getPoster_path()).trim(), movieData.getId(), movieData.getTitle());
            }
        });
        if (position >= movieList.size() - 2)
            adapterListener.getNextPagingData();
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
