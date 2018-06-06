package com.navoki.megamovies.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.navoki.megamovies.Adapters.CastListAdapter;
import com.navoki.megamovies.BuildConfig;
import com.navoki.megamovies.Models.CastModel;
import com.navoki.megamovies.Models.GenreModel;
import com.navoki.megamovies.Models.MovieData;
import com.navoki.megamovies.R;
import com.navoki.megamovies.Utils.AppConstants;
import com.navoki.megamovies.Utils.Global;
import com.navoki.megamovies.Utils.Util;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {
    private TextView tvGenre;
    private TextView tvDate;
    private TextView tvRate;
    private TextView tvDescription;
    private TextView tvTitle;
    private String movieId;
    private Context context;
    private Global global;
    private MovieData movieData;
    private final String TAG = DetailsActivity.class.getSimpleName();
    private RecyclerView castList;
    private ImageView imgBanner;
    private ImageView imgPoster;
    private android.support.v7.widget.Toolbar toolbar;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        context = this;
        global = Global.getAppInstance();
        toolbar = findViewById(R.id.toolbar);
        imgPoster = findViewById(R.id.img_poster);
        tvTitle = findViewById(R.id.tv_title);
        tvGenre = findViewById(R.id.tv_genre);
        tvDate = findViewById(R.id.tv_date);
        tvRate = findViewById(R.id.tv_rate);
        tvDescription = findViewById(R.id.tv_description);
        castList = findViewById(R.id.castList);
        imgBanner = findViewById(R.id.img_banner);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (getIntent() != null) {
            Intent intent = getIntent();
            bundle = intent.getExtras();
            initialize();
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        castList.setLayoutManager(gridLayoutManager);
        getMovieDetails();
    }

    private void initialize() {
        Picasso.get().load(getIntent().getStringExtra(AppConstants.EXTRA_IMAGE_PATH))
                .placeholder(R.drawable.placeholder_banner)
                .into(imgPoster);
        movieId = bundle.getString(AppConstants.EXTRA_MOVIE_ID);
        tvTitle.setText(bundle.getString(AppConstants.EXTRA_MOVIE_TITLE));
    }

    private void populateUI() {
        Picasso.get().load((BuildConfig.POSTER_BASE_URL_HIGH + movieData.getBackdrop_path()).trim())
                .placeholder(R.drawable.placeholder_banner)
                .into(imgBanner);
        StringBuilder stringBuilder = new StringBuilder();
        for (GenreModel genreModel : movieData.getGenreList()) {
            stringBuilder.append(genreModel.getName());
            stringBuilder.append(",");
        }
        tvGenre.setText(stringBuilder.toString().substring(0, stringBuilder.length() - 1));
        tvDate.setText(movieData.getRelease_date());
        tvRate.setText(movieData.getVote_average());
        tvDescription.setText(movieData.getOverview());
    }


    /*
     * get movie details
     * */
    private void getMovieDetails() {
        Uri.Builder params = new Uri.Builder();
        params.appendEncodedPath(movieId);
        params.appendQueryParameter(getString(R.string.key_api_key), BuildConfig.API_KEY);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConstants.API_MOVIE_DETAILS + params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                movieData = gson.fromJson(response, MovieData.class);
                getMovieCast();
                populateUI();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Util.showError(error, context);
            }
        });

        global.addToRequestQueue(stringRequest);
    }

    /*
     * get list of cast in movies
     * */
    private void getMovieCast() {
        Uri.Builder params = new Uri.Builder();
        params.appendEncodedPath(movieId);
        params.appendEncodedPath(getString(R.string.key_casts));
        params.appendQueryParameter(getString(R.string.key_api_key), BuildConfig.API_KEY);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConstants.API_MOVIE_CAST
                + params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray dataArray = jsonObject.getJSONArray(getString(R.string.key_cast));
                    if (dataArray.length() > 0) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<CastModel>>() {
                        }.getType();
                        ArrayList<CastModel> list = gson.fromJson(dataArray.toString(), listType);
                        CastListAdapter castListAdapter = new CastListAdapter(context, list);
                        castList.setAdapter(castListAdapter);
                    } else
                        Toast.makeText(context, getString(R.string.somethingGoneWrong), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Util.showError(error, context);
            }
        });

        global.addToRequestQueue(stringRequest);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        bundle=savedInstanceState;
        initialize();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(AppConstants.EXTRA_MOVIE_ID, movieId);
        outState.putString(AppConstants.EXTRA_IMAGE_PATH, bundle.getString(AppConstants.EXTRA_IMAGE_PATH));
        outState.putString(AppConstants.EXTRA_MOVIE_TITLE, bundle.getString(AppConstants.EXTRA_MOVIE_TITLE));
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
