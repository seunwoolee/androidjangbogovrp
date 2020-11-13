package com.example.jangbogovrp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jangbogovrp.R;
import com.example.jangbogovrp.http.HttpService;
import com.example.jangbogovrp.http.RetrofitClient;
import com.example.jangbogovrp.model.User;
import com.google.android.material.snackbar.Snackbar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private View parent_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        parent_view = findViewById(android.R.id.content);
        TextView username = findViewById(R.id.username);
        TextView password = findViewById(R.id.password);
        ProgressBar progressBar = findViewById(R.id.progress_bar);

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm realm = Realm.getInstance(config);

        HttpService httpService = RetrofitClient.getHttpService(null);

        ((View) findViewById(R.id.login_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                Call<User> call = httpService.login(username.getText().toString(), password.getText().toString());
                Callback<User> callback = new Callback<User>() {

                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Log.d(TAG, "성공");
                        User user = response.body();

                        if(user == null) {
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(parent_view, "ID, PASSWORD를 확인해주세요", Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        Log.d(TAG, user.getKey().toString());
                        realm.beginTransaction();
                        realm.copyToRealm(user);
                        realm.commitTransaction();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "실패");
                    }
                };
                call.enqueue(callback);
            }
        });
    }
}