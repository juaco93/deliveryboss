package com.jadevelopment.deliveryboss1.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jadevelopment.deliveryboss1.R;
import com.jadevelopment.deliveryboss1.data.api.model.EmpresasBody;
import com.jadevelopment.deliveryboss1.data.api.model.MessageEvent;
import com.jadevelopment.deliveryboss1.data.api.model.Orden_detalle;
import com.jadevelopment.deliveryboss1.data.api.model.Producto;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Joaquin on 29/7/2017.
 */

public class AgregarProductoFragment extends DialogFragment {

    TextView nombreProducto;
    NumberPicker cantidadProducto;
    Button btnAceptar;
    Button btnCancelar;
    Producto producto;
    Orden_detalle ordenDetalle;
    Orden_detalle ModOrden_detalle;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_add_producto, null);

        // Get your views by using view.findViewById() here and do your listeners.
        nombreProducto = (TextView) view.findViewById(R.id.txtDialogoNombreProducto);
        cantidadProducto = (NumberPicker) view.findViewById(R.id.npCantidadProducto);
        cantidadProducto.setMaxValue(48);
        cantidadProducto.setMinValue(1);
        btnAceptar = (Button) view.findViewById(R.id.btnDialogoAceptar);

        //Log.d("productoJSON",getArguments().getString("producto"));


        // SI VENIMOS DESDE EL MENU
        if(getArguments().getString("producto")!=null){
            producto = (new Gson()).fromJson((getArguments().getString("producto")),Producto.class);
            nombreProducto.setText("¿Cantidad de " + producto.getProducto()+"?");
            cantidadProducto.setMinValue(1);
        }


        // SI VENIMOS DESDE EL CARRITO (PARA MODIFICAR ALGO)
        if(getArguments().getString("orden_detalle")!=null){
            ModOrden_detalle = (new Gson()).fromJson((getArguments().getString("orden_detalle")),Orden_detalle.class);
            nombreProducto.setText("¿Cantidad de " + ModOrden_detalle.getProducto_nombre()+"?");
            cantidadProducto.setMinValue(0);
        }

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // SI ES QUE VENIMOS DEL MENU (FLUJO NORMAL)
                if(ModOrden_detalle==null) {
                    String subtotal = String.valueOf(Float.valueOf(producto.getPrecio()) * cantidadProducto.getValue());
                    ordenDetalle = new Orden_detalle("", "", String.valueOf(cantidadProducto.getValue()), producto.getIdproducto(), producto.getProducto(), producto.getPrecio(), producto.getProducto_rubro_idproducto_rubro(), subtotal);
                    Intent i = new Intent()
                            .putExtra("detalle", (new Gson()).toJson(ordenDetalle));
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                    dismiss();
                }
                else if(ModOrden_detalle!=null){
                    String subtotal = String.valueOf(Float.valueOf(ModOrden_detalle.getProducto_precio()) * cantidadProducto.getValue());
                    ordenDetalle = new Orden_detalle("", "", String.valueOf(cantidadProducto.getValue()), ModOrden_detalle.getProducto_idproducto(), ModOrden_detalle.getProducto_nombre(), ModOrden_detalle.getProducto_precio(), ModOrden_detalle.getProducto_rubro(), subtotal);

                    EventBus.getDefault().post(new MessageEvent("5",(new Gson()).toJson(ordenDetalle)));
                    dismiss();
                }
            }
        });

        btnCancelar = (Button) view.findViewById(R.id.btnDialogoCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
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
