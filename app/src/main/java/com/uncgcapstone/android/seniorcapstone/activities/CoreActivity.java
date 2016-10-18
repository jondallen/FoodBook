package com.uncgcapstone.android.seniorcapstone.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

/**
 * Created by jon on 8/31/2016.
 *
 * This class is responsible for managing the progress dialog animation and is the base class
 * of the LogInActivity
 */
public class CoreActivity extends AppCompatActivity {
    private AlertDialog mAlertDialog;

    public void showProgressDialog() {
        if (mAlertDialog == null) {
            mAlertDialog = new SpotsDialog(this);
            mAlertDialog.setCancelable(false);
        }

        mAlertDialog.show();
    }

    public void hideProgressDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
