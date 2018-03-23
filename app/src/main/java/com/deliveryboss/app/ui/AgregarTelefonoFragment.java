package com.deliveryboss.app.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.deliveryboss.app.R;
import com.deliveryboss.app.data.api.model.MessageEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Joaquin on 22/03/2018.
 */

public class AgregarTelefonoFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_agregar_telefono, null);

        // Get your views by using view.findViewById() here and do your listeners.
        //ciudad = (EditText) view.findViewById(R.id.txtDireccionEditCiudad);

        builder.setMessage("")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        //mListener.onDialogPositiveClick(AgregarTelefonoFragment.this);
                        EventBus.getDefault().post(new MessageEvent("9", "Se habilita modificar el telefono"));
                    }
                });

        // Set the dialog layout
        builder.setView(view);
        return builder.create();
    }

}
