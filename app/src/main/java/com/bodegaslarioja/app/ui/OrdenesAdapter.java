package com.bodegaslarioja.app.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bodegaslarioja.app.data.api.model.Venta;
import com.google.gson.Gson;
import com.bodegaslarioja.app.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Joaquin on 5/8/2017.
 */

public class OrdenesAdapter extends RecyclerView.Adapter<OrdenesAdapter.ViewHolder> {
    private Context context;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.orden_item_list, parent, false);
        this.context = mContext;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Venta orden = mItems.get(position);
        holder.idorden.setText("Pedido #" + orden.getIdventa());
        holder.nombreEmpresa.setText(orden.getNombre_bodega());
        holder.fecha.setText(orden.getFecha_hora());
        holder.estado.setText(orden.getVenta_estado());
        holder.importeTotal.setText("$" + orden.getImporte_total());

        if(orden.getVenta_estado().equals("Confirmado")||orden.getVenta_estado().equals("Terminada")||orden.getVenta_estado().equals("Enviada"))holder.estado.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOrdenConfirmada));
        if(orden.getVenta_estado().equals("Pendiente"))holder.estado.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOrdenPendiente));
        if(orden.getVenta_estado().equals("Rechazado")||orden.getVenta_estado().equals("Anulado"))holder.estado.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOrdenCancelada));
        //if(orden.getEstado().equals("Entregada"))holder.estado.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOrdenEntregada));

        int i = 0;
        String previo = "";
        String subtotales = "";
        while (i < orden.getVenta_detalle().size()) {
            previo += orden.getVenta_detalle().get(i).getCantidad() + "x " + orden.getVenta_detalle().get(i).getProducto() +"\n";
            subtotales += "$"+orden.getVenta_detalle().get(i).getImporte_subtotal() + "\n";
            i++;
        }
        holder.orden_detalle.setText(previo);
        holder.subtotales.setText(subtotales);


        //METODO PARA QUE PUEDA LLAMAR UNICAMENTE CUANDO ESTE DENTRO DE LAS 2H SIGUIENTES A REALIZADO EL PEDIDO
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fechaOrden = null;
        try {
            if(orden.getFecha_hora()!=null)
                fechaOrden = formatter.parse(orden.getFecha_hora());
        } catch (ParseException e) {
            e.printStackTrace();
        }

            holder.llamar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    //Log.d("telefono",orden.getTelefono().toString());
                    callIntent.setData(Uri.parse("tel:"+orden.getTelefono1_bodega()));
                    context.startActivity(callIntent);
                }
            });


        //METODO PARA INFORMACION DEL ESTADO DE LA ORDEN
        holder.btnOrdenInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoEstadoOrden((new Gson()).toJson(orden));
            }
        });


    }

    public void showInfoEstadoOrden(String orden) {
        FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
        OrdenInfoEstadoFragment newFragment = new OrdenInfoEstadoFragment();

        Bundle args = new Bundle();
        if(!orden.isEmpty() && !orden.equals(""))args.putString("orden", orden);
        newFragment.setArguments(args);

        newFragment.show(fragmentManager.beginTransaction(), "Estado de tu orden");

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private List<Venta> mItems;
    private Context mContext;

    public OrdenesAdapter(Context context, List<Venta> items) {
        mItems = items;
        mContext = context;
    }

    private OnItemClickListener mOnItemClickListener;

    interface OnItemClickListener {
        void onItemClick(Venta clickedOrden);
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView idorden;
        public TextView nombreEmpresa;
        public TextView fecha;
        public TextView orden_detalle;
        public TextView estado;
        //public TextView info_estado;
        public ImageView btnOrdenInfo;
        public TextView llamar;
        public TextView importeTotal;
        public TextView subtotales;


        public ViewHolder(View itemView) {
            super(itemView);
            idorden = (TextView) itemView.findViewById(R.id.txtOrdenIdOrden);
            nombreEmpresa = (TextView) itemView.findViewById(R.id.txtOrdenNombreEmpresa);
            fecha = (TextView) itemView.findViewById(R.id.txtOrdenFechaHora) ;
            orden_detalle = (TextView) itemView.findViewById(R.id.txtOrdenDetalle);
            estado = (TextView) itemView.findViewById(R.id.txtOrdenEstado);
            //info_estado = (TextView) itemView.findViewById(R.id.txtOrdenInfoEstado);
            btnOrdenInfo = (ImageView) itemView.findViewById(R.id.btnOrdenInfo);
            llamar = (TextView) itemView.findViewById(R.id.txtOrdenLlamar);
            importeTotal = (TextView) itemView.findViewById(R.id.txtOrdenDetalleTotal);
            subtotales = (TextView) itemView.findViewById(R.id.txtOrdenDetalleSubtotal);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                mOnItemClickListener.onItemClick(mItems.get(position));
            }
        }
    }

    public void swapItems(List<Venta> ordenes) {
        if (ordenes == null) {
            mItems = new ArrayList<>(0);
        } else {
            mItems = ordenes;
        }
        notifyDataSetChanged();
    }
}
