package com.deliveryboss.app.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.deliveryboss.app.R;
import com.deliveryboss.app.data.api.model.MessageEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Joaquin on 27/03/2018.
 */

public class OrdenEnviadaFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_orden_enviada, null);

        // Get your views by using view.findViewById() here and do your listeners.
        builder.setMessage("")
                .setPositiveButton("Ir a mis ordenes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getActivity(),MisOrdenesActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });


        // Set the dialog layout
        builder.setView(view);
        return builder.create();
    }

}
