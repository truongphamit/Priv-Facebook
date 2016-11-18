package org.truongpq.frivfacebook.tools;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import org.truongpq.frivfacebook.R;

/**
 * Created by truongpq on 04/11/2016.
 */

public class StaticTools {
    public static void showConfirmDialog(Context c, String title, DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setPositiveButton(c.getString(R.string.exit), positiveListener);
        alertDialogBuilder.setNegativeButton(c.getString(R.string.cancel), null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
}
