package com.bodegaslarioja.app.ui;

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

import com.bodegaslarioja.app.data.api.model.Venta_detalle;
import com.google.gson.Gson;
import com.bodegaslarioja.app.R;
import com.bodegaslarioja.app.data.api.model.MessageEvent;
import com.bodegaslarioja.app.data.api.model.Producto;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by Joaquin on 29/7/2017.
 */

public class AgregarProductoFragment extends DialogFragment {

    TextView nombreProducto;
    NumberPicker cantidadProducto;
    Button btnAceptar;
    Button btnCancelar;
    Producto producto;
    Venta_detalle ordenDetalle;
    Venta_detalle ModOrden_detalle;


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
            ModOrden_detalle = (new Gson()).fromJson((getArguments().getString("orden_detalle")),Venta_detalle.class);
            nombreProducto.setText("¿Cantidad de " + ModOrden_detalle.getProducto()+"?");
            cantidadProducto.setMinValue(0);
        }

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // SI ES QUE VENIMOS DEL MENU (FLUJO NORMAL)
                if(ModOrden_detalle==null) {
                    String subtotal = String.valueOf(Float.valueOf(producto.getPrecio1()) * cantidadProducto.getValue());
                    //ordenDetalle = new Venta_detalle("", "", String.valueOf(cantidadProducto.getValue()), producto.getIdproducto(), producto.getProducto(), producto.getPrecio1(), producto.getProducto_rubro(), subtotal);
                    ordenDetalle = new Venta_detalle("", "", producto.getIdbodega(),String.valueOf(cantidadProducto.getValue()),producto.getIdproducto(),producto.getProducto(),producto.getPrecio1(),subtotal,"Si");
                    Intent i = new Intent()
                            .putExtra("detalle", (new Gson()).toJson(ordenDetalle));
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                    dismiss();
                }
                else if(ModOrden_detalle!=null){
                    String subtotal = String.valueOf(Float.valueOf(ModOrden_detalle.getPrecio()) * cantidadProducto.getValue());

                    Log.d("joaco_prod",(new Gson()).toJson(producto));

                    ordenDetalle = new Venta_detalle("", "", ModOrden_detalle.getProducto(),String.valueOf(cantidadProducto.getValue()),ModOrden_detalle.getIdproducto(),ModOrden_detalle.getProducto(),ModOrden_detalle.getPrecio(),subtotal,"Si");

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
