package com.crazy_iter.checkjobs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by CrazyITer on 5/16/2018.
 */

public class LoadingLayout {

    private View view;

    public LoadingLayout(@NonNull Context context) {
        this.view = View.inflate(context, R.layout.loading_layout, null);
        hideLoading();
    }

    public View getLoading() {
        return this.view;
    }

    public void hideLoading() {
        this.view.setVisibility(View.GONE);
    }

    public void showLoading() {
        this.view.setVisibility(View.VISIBLE);
    }

}
