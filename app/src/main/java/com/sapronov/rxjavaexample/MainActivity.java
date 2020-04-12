package com.sapronov.rxjavaexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    private Disposable sbr;
    private ProgressBar bar;
    private TextView info;
    private boolean isActive = false;
    private int startAt;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        bar = findViewById(R.id.load);
        info = findViewById(R.id.info);
        boolean recreated = bundle != null;
        startAt = recreated ? bundle.getInt("progress", 0) : 0;
        info.setText(startAt + "%");
        Button loadButton = findViewById(R.id.start);
        Button cancelButton = findViewById(R.id.cancel);
        loadButton.setOnClickListener(view -> start());
        cancelButton.setOnClickListener(view -> cancel());
        if (recreated) {
            start();
        }
    }

    public void start() {
        if (!isActive) {
            this.sbr = Observable.interval(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(v -> {
                        info.setText(startAt + v.intValue() + "%");
                        bar.setProgress(startAt + v.intValue());
                    });
            isActive = true;
        }
    }

    public void cancel() {
        sbr.dispose();
        startAt = 0;
        info.setText(startAt + "%");
        bar.setProgress(startAt);
        isActive = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("progress", bar.getProgress());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.sbr != null) {
            this.sbr.dispose();
        }
    }
}
