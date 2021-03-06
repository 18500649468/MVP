package com.example.ll.mvp.movies;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ll.mvp.BuildConfig;
import com.example.ll.mvp.OWLoadingView;
import com.example.ll.mvp.R;
import com.example.ll.mvp.api.DoubanManager;
import com.example.ll.mvp.beans.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;



/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment implements MoviesContract.View{
//    @BindView(R.id.tv_movie)
//    TextView mTvMovie;
    @BindView(R.id.rcv_movie)
    RecyclerView mRcvMovie;
    @BindView(R.id.swiprl)
    SwipeRefreshLayout swipeRefreshLayout;
    Unbinder unbinder;
    private List<Movie> mMovieList = new ArrayList<>();
    private MovieAdapter adapter;
    private MoviesContract.Presenter moviePresenter;
    private View mNoMoiveView;

//    private Handler mHandler = new Handler()
//    {
//        public void handleMessage(android.os.Message msg)
//        {
//            switch (msg.what)
//            {
//                case 1001:
//                    swipeRefreshLayout.setRefreshing(false);
//                    break;
//
//            }
//        }
//    };
    public MovieFragment() {
        // Required empty public constructor
    }
    public static MovieFragment newInstance(){
        return new MovieFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (moviePresenter!=null){
            moviePresenter.start();
        }else {
            moviePresenter = new MoviePresenter(DoubanManager.creatDoubanService(),MovieFragment.this);
            moviePresenter.start();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }
    public void stopRefresh(){
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                moviePresenter.pullrefresh();
//                Observable.just("1").delay(2, TimeUnit.SECONDS)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeOn(Schedulers.io())
//                        .subscribe(new Action1<String>() {
//                    @Override
//                    public void call(String s) {
//                        swipeRefreshLayout.setRefreshing(false);
//                    }
//                });
//                mHandler.sendEmptyMessageDelayed(1001, 2000);
            }
        });
        if (mRcvMovie!=null){
            mRcvMovie.setHasFixedSize(true);
            GridLayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(),3);
            mRcvMovie.setLayoutManager(layoutManager);
            adapter = new MovieAdapter(getActivity(),mMovieList);
            mRcvMovie.setAdapter(adapter);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setPresenter(MoviesContract.Presenter presenter) {
        moviePresenter = presenter;

    }
    @Override
    public void showMovies(List<Movie> movies) {
        adapter.setData(movies);
    }

    @Override
    public void showNoMovies() {
        mRcvMovie.setVisibility(View.GONE);
    }


    @Override
    public void setLoadingIndicator(boolean active) {
        if (getView()==null)return;
        final ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.pb_loading);
        final OWLoadingView owLoadingView= (OWLoadingView) getView().findViewById(R.id.owloadingview);
        if (active){
            owLoadingView.startAnim();
            progressBar.setVisibility(View.VISIBLE);
        }else{
            owLoadingView.stopAnim();
            progressBar.setVisibility(View.GONE);
        }
    }

    class MovieAdapter extends RecyclerView.Adapter<MoviesViewHolder>{
        private List<Movie> movies;
        private Context context;
//        @LayoutRes
//        private int layoutResId;
        public MovieAdapter(Context context,List<Movie> movieData){
            this.context = context;
            this.movies = movieData;
        }
        public void setData(List<Movie> data){
            this.movies = data;
            notifyDataSetChanged();
        }

        @Override
        public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_movie,parent,false);
            return new MoviesViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MoviesViewHolder holder, int position) {
            if (holder==null)return;
            holder.updataMovie(movies.get(position));
        }

        @Override
        public int getItemCount() {
            return movies.size();
        }
    }
     class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivMovie;
        TextView tvTitle;
        TextView tvRatingAverage;
        RatingBar movieRatingBar;
        Movie movie;
        public MoviesViewHolder(View itemView) {
            super(itemView);
            ivMovie = (ImageView) itemView.findViewById(R.id.iv_item_movie);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_movie_title);
            tvRatingAverage = (TextView) itemView.findViewById(R.id.tv_movie_rating_average);
            movieRatingBar = (RatingBar) itemView.findViewById(R.id.rt_movie_star);
            itemView.setOnClickListener(this);
        }

        public void updataMovie(Movie movie){
            if (movie==null) return;
            this.movie = movie;
            Context context = itemView.getContext();
            Picasso.with(context)
                    .load(movie.getImages().getLarge())
                    .placeholder(context.getResources().getDrawable(R.mipmap.ic_launcher))
                    .into(ivMovie);
            tvTitle.setText(movie.getTitle());
            final  double average = movie.getRating().getAverage();
            if (average==0.0){
                movieRatingBar.setVisibility(View.GONE);
                tvRatingAverage.setText(context.getResources().getString(R.string.no_rating));
            }else{
                movieRatingBar.setVisibility(View.VISIBLE);
                tvRatingAverage.setText(String.valueOf(average));
                movieRatingBar.setStepSize(0.5f);
                movieRatingBar.setRating((float)movie.getRating().getAverage()/2);
            }

        }
        @Override
        public void onClick(View v) {
            if (movie==null)return;
            if (itemView==null)return;
//            Context context = itemView.getContext();
//            if (context==null)return;
            Intent intent = new Intent(getActivity(),MovieDetilActivity.class);
            intent.putExtra("movie",movie);
//            if (context instanceof Activity){
//                Activity activity = (Activity) context;
//                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),ivMovie,"cover").toBundle();
//                ActivityCompat.startActivity(getActivity(),intent,bundle);
//            }
            ActivityCompat.startActivity(getActivity(),intent,null);

        }

    }
}
