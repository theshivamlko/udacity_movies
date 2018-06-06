package com.navoki.megamovies.Activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.navoki.megamovies.Adapters.MovieListAdapter;
import com.navoki.megamovies.BuildConfig;
import com.navoki.megamovies.Callbacks.OnAdapterListener;
import com.navoki.megamovies.Models.MovieData;
import com.navoki.megamovies.R;
import com.navoki.megamovies.Utils.AppConstants;
import com.navoki.megamovies.Utils.Global;
import com.navoki.megamovies.Utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity implements OnAdapterListener {

    private Context context;
    private Global global;
    private final String TAG = SplashScreen.class.getSimpleName();
    private int paging = 1;
    private ArrayList<MovieData> movieList;
    private RecyclerView rycMovieList;
    private MovieListAdapter movieListAdapter;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private String mainURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        context = this;
        global = Global.getAppInstance();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        rycMovieList = findViewById(R.id.ryc_movie_list);
        progressBar = findViewById(R.id.progressBar);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        rycMovieList.setLayoutManager(gridLayoutManager);

        movieList = new ArrayList<>();
        mainURL = AppConstants.API_MOVIE_POPULAR_LIST;

        getMovieList();
    }


    /*
     * get list of latest movies
     * */
    private void getMovieList() {

        progressDialog.show();
        Uri.Builder params = new Uri.Builder();
        params.appendQueryParameter(getString(R.string.key_api_key), BuildConfig.API_KEY);
        params.appendQueryParameter(getString(R.string.key_page_no), String.valueOf(paging));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, mainURL + params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray dataArray = jsonObject.getJSONArray(getString(R.string.key_results));

                    if (dataArray.length() > 0) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<MovieData>>() {
                        }.getType();

                        ArrayList<MovieData> list = gson.fromJson(dataArray.toString(), listType);
                        movieList.addAll(list);
                        if (movieListAdapter == null) {
                            movieListAdapter = new MovieListAdapter(context, movieList);
                            rycMovieList.setAdapter(movieListAdapter);
                        } else
                            movieListAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);

                    } else
                        Toast.makeText(context, getString(R.string.somethingGoneWrong), Toast.LENGTH_SHORT).show();

                    progressDialog.dismiss();
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(context, getString(R.string.somethingGoneWrong), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Util.showError(error, context);
            }
        });

        global.addToRequestQueue(stringRequest);
    }


    @Override
    public void getNextPagingData() {
        ++paging;
        progressBar.setVisibility(View.VISIBLE);
        getMovieList();
    }

    @Override
    public void moveToDetailsScreen(ImageView imageView, String imgPath, String id, String title) {

        Intent intent = new Intent(MovieListActivity.this, DetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.EXTRA_IMAGE_PATH, imgPath);
        bundle.putString(AppConstants.EXTRA_MOVIE_ID, id);
        bundle.putString(AppConstants.EXTRA_MOVIE_TITLE, title);
        intent.putExtras(bundle);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(MovieListActivity.this,
                        imageView,
                        getString(R.string.poster_transition_name));
        startActivity(intent, options.toBundle());
    }


    private final BroadcastReceiver internetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Util.checkConnection(context);
                getMovieList();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        movieList = new ArrayList<>();
        if (id == R.id.most_popular) {
            mainURL = AppConstants.API_MOVIE_POPULAR_LIST;
            paging = 1;
            movieListAdapter = null;
            getMovieList();
        } else if (id == R.id.high_rate) {
            mainURL = AppConstants.API_MOVIE_HIGH_RATE_LIST;
            paging = 1;
            movieListAdapter = null;
            getMovieList();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(internetReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(internetReceiver);
    }
}
