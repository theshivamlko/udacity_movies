package com.navoki.megamovies.Utils;

import com.navoki.megamovies.BuildConfig;

/**
 * Created by Name name on 6/5/2018.
 */
public class AppConstants {
    // API urls
    public static final String API_MOVIE_POPULAR_LIST = BuildConfig.BASE_URL + "movie/popular";
    public static final String API_MOVIE_HIGH_RATE_LIST = BuildConfig.BASE_URL + "movie/top_rated";
    public static final String API_MOVIE_DETAILS = BuildConfig.BASE_URL + "movie";
    public static final String API_MOVIE_CAST = BuildConfig.BASE_URL + "movie";

    // Intent keys
    public static final String EXTRA_IMAGE_PATH = "imagePath";
    public static final String EXTRA_MOVIE_ID = "id";
    public static final String EXTRA_MOVIE_TITLE = "title";

}
