package com.example.moviesapp.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.moviesapp.Adapters.ActorsListAdapter;
import com.example.moviesapp.Adapters.CategoryEachFilmAdapter;
import com.example.moviesapp.Domain.FilmItem;
import com.example.moviesapp.R;
import com.google.gson.Gson;

public class DetailActivity extends AppCompatActivity {
    private StringRequest mStringRequest;
    private RequestQueue mRequestQueue;
    private ProgressBar progressBar;
    private TextView titletxt, movieRateTxt, movieTimeTxt, movieSummaryInfo, movieActorsInfo;
    private int idFilm;
    private ImageView pic2, backImg;
    private RecyclerView.Adapter adapterActorList, adapterCategory;
    private RecyclerView recyclerViewActors, recyclerViewCategory;
    private NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Mendapatkan id film dari intent
        idFilm = getIntent().getIntExtra("id", 0);

        // Inisialisasi tampilan
        initView();

        // Mengirim request untuk mendapatkan detail film
        sendRequest();
    }

    private void sendRequest() {
        // Inisialisasi RequestQueue
        mRequestQueue = Volley.newRequestQueue(this);

        // Menampilkan progress bar dan menyembunyikan scroll view
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        // Membuat StringRequest
        mStringRequest = new StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/movies/" + idFilm, response -> {
                    Gson gson = new Gson();
                    progressBar.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);

                    FilmItem item = gson.fromJson(response, FilmItem.class);

                    // Memuat gambar menggunakan Glide
                    Glide.with(DetailActivity.this)
                            .load(item.getPoster())
                            .into(pic2);

                    // Menampilkan detail film
                    titletxt.setText(item.getTitle());
                    movieRateTxt.setText(item.getImdbRating());
                    movieTimeTxt.setText(item.getRuntime());
                    movieSummaryInfo.setText(item.getPlot());
                    movieActorsInfo.setText(item.getActors());

                    // Mengatur adapter untuk recyclerViewActors
                    if (item.getImages() != null) {
                        adapterActorList = new ActorsListAdapter(item.getImages());
                        recyclerViewActors.setAdapter(adapterActorList); // Pastikan menggunakan recyclerViewActors
                    }

                    // Mengatur adapter untuk recyclerViewCategory (genres)
                    if (item.getGenres() != null) {
                        adapterCategory = new CategoryEachFilmAdapter(item.getGenres());
                        recyclerViewCategory.setAdapter(adapterCategory);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                });

        // Menambahkan request ke RequestQueue
        mRequestQueue.add(mStringRequest);
    }

    private void initView() {
        titletxt = findViewById(R.id.movieNameTxt);
        progressBar = findViewById(R.id.progressBarDetail);
        scrollView = findViewById(R.id.scrollView2);
        pic2 = findViewById(R.id.picDetail);
        movieRateTxt = findViewById(R.id.movieStar);
        movieTimeTxt = findViewById(R.id.movieTime);
        movieSummaryInfo = findViewById(R.id.movieSummery);
        movieActorsInfo = findViewById(R.id.movieActorInfo);
        backImg = findViewById(R.id.backimg);
        recyclerViewCategory = findViewById(R.id.genreView);
        recyclerViewActors = findViewById(R.id.imagerRecycler); // Pastikan ID ini benar

        // Mengatur LayoutManager untuk RecyclerView
        recyclerViewActors.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Mengatur klik listener untuk tombol kembali
        backImg.setOnClickListener(v -> finish());
    }
}
