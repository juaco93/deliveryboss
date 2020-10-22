package com.bodegaslarioja.app.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bodegaslarioja.app.data.api.model.Venta;
import com.google.gson.Gson;
import com.bodegaslarioja.app.R;

/**
 * Created by Joaquin on 29/11/2017.
 */

public class OrdenInfoEstadoFragment extends DialogFragment {

    Button btnCerrar;
    TextView txtOrdenNumeroOrden;
    TextView txtOrdenLocal;
    TextView txtOrdenFecha;
    TextView txtOrdenFechaEstado;
    TextView txtOrdenEstado;
    TextView txtOrdenEstadoInfoEstado;
    private Context mContext;
    Venta orden;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_orden_estado_dialog, null);

        mContext = getActivity();

        // Get your views by using view.findViewById() here and do your listeners.
        btnCerrar = (Button) view.findViewById(R.id.btnOrdenEstadoCerrar);
        txtOrdenNumeroOrden = (TextView) view.findViewById(R.id.lbOrdenEstadoId);
        txtOrdenLocal = (TextView) view.findViewById(R.id.fragtxtEstadoLocal);
        txtOrdenFecha = (TextView) view.findViewById(R.id.fragtxtEstadoFechaOrden);
        txtOrdenFechaEstado = (TextView) view.findViewById(R.id.fragtxtEstadoFechaEstado);
        txtOrdenEstado = (TextView) view.findViewById(R.id.fragtxtEstado);
        txtOrdenEstadoInfoEstado = (TextView) view.findViewById(R.id.fragtxtOrdenEstadoInfoEstado);


        if(getArguments().getString("orden")!=null){
            orden = (new Gson()).fromJson((getArguments().getString("orden")), Venta.class);
            txtOrdenNumeroOrden.setText("Detalle del Pedido #"+((Venta) orden).getIdventa());
            txtOrdenLocal.setText(orden.getNombre_bodega());
            txtOrdenFecha.setText(orden.getFecha_hora());
            txtOrdenFechaEstado.setText(orden.getFecha_hora());
            txtOrdenEstado.setText(orden.getVenta_estado());
            txtOrdenEstadoInfoEstado.setText(orden.getNota_bodega());
            if(orden.getVenta_estado().equals("Confirmado"))txtOrdenEstado.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrdenConfirmada));
            if(orden.getVenta_estado().equals("Pendiente"))txtOrdenEstado.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrdenPendiente));
            if(orden.getVenta_estado().equals("Rechazado"))txtOrdenEstado.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrdenCancelada));

        }

        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // Set the dialog layout
        builder.setView(view);

        return builder.create();
    }
}
