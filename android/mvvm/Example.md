# Live data, ViewModel, Retrofit Android Architecture Component

MVVM is one of the architectural patterns which enhances separation of concerns, and it allows separating the user interface logic from the business logic. Its target is to achieve Keeping UI code free and straightforward of app logic to make it easier to manage.

### Model
A model represents the data and business logic of the app. One of the recommended implementations of this is to expose its data through observable. Unlike a regular observable, **LiveData** respects the lifecycle of other app components, such as activities and fragments. We are using LiveData in **Repository class** that are listed below.
Since LiveData respects Android Lifecycle, this means it will not invoke its observer callback unless activity or fragment is in received onStart() but did not accept onStop() Adding to this, LiveData will also automatically remove the observer when its host receives onDestroy().

### ViewModel
ViewModel interacts with model and also prepares observable that can be observed by a View. One of the essential implementation strategies of this layer is to decouple it from the View. ViewModel should not be aware of the view which is interacting. ViewModel class is designed to store and manage UI-related data in a lifecycle conscious way. The ViewModel class allows data to survive configuration changes such as screen rotations. We are using ViewModel in HeadLineViewModel class that are listed below.
![ViewModel LifeCycle scope for data observable](https://miro.medium.com/max/1400/1*uWXunt0A6fKUFU8PsTLkfA.png)

### View
The view role in this pattern is to observe a ViewModel observable to get data to update UI elements accordingly. This part is implemented in our MainActivity class where we are observing data from ViewModel and set observed data on the adapter.

Here I am tried to explain about android architecture component LiveData, ViewModel with Retrofit2. In this post, I am simply hit an API using Retrofit and show data on activity using RecyclerView.

## 1. Add Dependency on the build.gradle file
These are the required dependency for using LiveData, ViewModel, and Retrofit.

```Groovy
apply plugin: 'com.android.application'
android {
    compileSdkVersion 28
    defaultConfig {
        applicationId "com.amit.mvvmnews"
        minSdkVersion 21
        targetSdkVersion 28
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility = '1.8'
        targetCompatibility = '1.8'
    }
}
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'androidx.appcompat:appcompat:1.1.0-alpha05'
    implementation 'androidx.constraintlayout:constraintlayout:2.0.0-beta1'
    implementation 'com.google.code.gson:gson:2.8.5'
    implementation 'com.squareup.retrofit2:retrofit:2.5.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.5.0'
    implementation 'com.squareup.picasso:picasso:2.71828'
    implementation 'com.squareup.okio:okio:2.2.2'
    implementation 'com.squareup.okhttp3:okhttp:3.14.1'
    implementation 'com.android.support:recyclerview-v7:28.0.0'
    implementation 'android.arch.lifecycle:viewmodel:1.1.1'
    implementation 'android.arch.lifecycle:extensions:1.1.1'
    annotationProcessor 'android.arch.lifecycle:compiler:1.1.1'
    testImplementation 'junit:junit:4.13-beta-3'
    androidTestImplementation 'androidx.test:runner:1.2.0-beta01'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0-beta01'
}
```

## 2. Model classes for the API response

```Java
package com.amit.mvvmnews.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("totalResults")
    private Integer totalResults;
    @SerializedName("articles")
    private List<NewsArticle> articles = null;

    // getters and setters
}
```

```Java
package com.amit.mvvmnews.model;

import com.google.gson.annotations.SerializedName;

public class NewsArticle {
    @SerializedName("source")
    private NewsSource source;
    @SerializedName("author")
    private String author;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("url")
    private String url;
    @SerializedName("urlToImage")
    private String urlToImage;
    @SerializedName("publishedAt")
    private String publishedAt;
    @SerializedName("content")
    private String content;

    // getters and setters
}
```

```Java
package com.amit.mvvmnews.model;

import com.google.gson.annotations.SerializedName;

public class NewsSource {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;

    // getters and setters
}
```

## 3. Implement Retrofit part

```Java
package com.amit.mvvmnews.networking;

import com.amit.mvvmnews.model.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApi {
    @GET("top-headlines")
    Call<NewsResponse> getNewsList(@Query("sources") String newsSource, @Query("apiKey") String apiKey);
}
```

```Java
package com.amit.mvvmnews.networking;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
```

## 4. Make a Repository class
This class provides Singleton network request for hitting API and using LiveData for observing API response. LiveData is an observable data holder class. Unlike a regular observable, LiveData respects the lifecycle of other app components, such as activities and fragments.

```Java
package com.amit.mvvmnews.networking;

import androidx.lifecycle.MutableLiveData;

import com.amit.mvvmnews.model.NewsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsRepository {

    private static NewsRepository newsRepository;

    public static NewsRepository getInstance() {
        if (newsRepository == null) {
            newsRepository = new NewsRepository();
        }
        return newsRepository;
    }

    private NewsApi newsApi;

    public NewsRepository() {
        newsApi = RetrofitService.cteateService(NewsApi.class);
    }

    public MutableLiveData<NewsResponse> getNews(String source, String key) {
        MutableLiveData<NewsResponse> newsData = new MutableLiveData<>();
        newsApi.getNewsList(source, key).enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful()){
                    newsData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                newsData.setValue(null);
            }
        });
        return newsData;
    }
}
```

## 5. Make a ViewModel class
The **ViewModel** class is designed to store and manage UI-related data in a lifecycle conscious way. The **ViewModel** class allows data to survive configuration changes such as screen rotations.

```Java
package com.amit.mvvmnews.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.amit.mvvmnews.model.NewsResponse;
import com.amit.mvvmnews.networking.NewsRepository;


public class NewsViewModel extends ViewModel {

    private MutableLiveData<NewsResponse> mutableLiveData;
    private NewsRepository newsRepository;

    public void init(){
        if (mutableLiveData == null) {
            newsRepository = NewsRepository.getInstance();
            mutableLiveData = newsRepository.getNews("google-news", "API_KEY");
        }
    }

    public LiveData<NewsResponse> getNewsRepository() {
        return mutableLiveData;
    }

}
```

## 6. MainActivity class
We use ViewModel for observing data.

```Java
package com.amit.mvvmnews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.amit.mvvmnews.adapters.NewsAdapter;
import com.amit.mvvmnews.model.NewsArticle;
import com.amit.mvvmnews.model.NewsResponse;
import com.amit.mvvmnews.viewmodels.NewsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayList<NewsArticle> articleArrayList = new ArrayList<>();
    NewsAdapter newsAdapter;
    RecyclerView rvHeadline;
    NewsViewModel newsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvHeadline = findViewById(R.id.rvNews);

        newsViewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
        newsViewModel.init();
        newsViewModel.getNewsRepository().observe(this, newsResponse -> {
            List<NewsArticle> newsArticles = newsResponse.getArticles();
            articleArrayList.addAll(newsArticles);
            newsAdapter.notifyDataSetChanged();
        });

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        if (newsAdapter == null) {
            newsAdapter = new NewsAdapter(MainActivity.this, articleArrayList);
            rvHeadline.setLayoutManager(new LinearLayoutManager(this));
            rvHeadline.setAdapter(newsAdapter);
            rvHeadline.setItemAnimator(new DefaultItemAnimator());
            rvHeadline.setNestedScrollingEnabled(true);
        } else {
            newsAdapter.notifyDataSetChanged();
        }
    }
}
```
