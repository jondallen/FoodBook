package com.uncgcapstone.android.seniorcapstone.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;


import dmax.dialog.SpotsDialog;

/**
 * Created by jon on 8/31/2016.
 *
 * This class is responsible for managing the progress dialog animation and is the base class
 * of the LogInActivity and the MainActivity
 */
public class CoreActivity extends AppCompatActivity {
    private AlertDialog mAlertDialog;

    /**
     * This method pulls up the loading dialog
     */
    public void showProgressDialog() {
        if (mAlertDialog == null) {
            mAlertDialog = new SpotsDialog(this);
            mAlertDialog.setCancelable(false);
        }

        mAlertDialog.show();
    }

    /**
     * This method dismisses the loading dialog
     */
    public void hideProgressDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

}
