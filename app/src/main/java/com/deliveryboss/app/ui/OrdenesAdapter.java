package com.deliveryboss.app.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.google.gson.Gson;
import com.deliveryboss.app.R;
import com.deliveryboss.app.data.api.model.Orden;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        final Orden orden = mItems.get(position);
        holder.idorden.setText("Orden #" + orden.getIdorden());
        holder.nombreEmpresa.setText(orden.getNombre_empresa());
        holder.fecha.setText(orden.getFecha_hora());
        holder.estado.setText(orden.getEstado());

        if(orden.getEstado().equals("Confirmada")||orden.getEstado().equals("Terminada")||orden.getEstado().equals("Enviada"))holder.estado.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOrdenConfirmada));
        if(orden.getEstado().equals("Pendiente"))holder.estado.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOrdenPendiente));
        if(orden.getEstado().equals("Cancelada")||orden.getEstado().equals("Anulada"))holder.estado.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOrdenCancelada));
        if(orden.getEstado().equals("Entregada"))holder.estado.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOrdenEntregada));
        //holder.info_estado.setText(orden.getInfo_estado());

        int i = 0;
        String previo = "";
        while (i < orden.getOrden_detalle().size()) {
            previo += orden.getOrden_detalle().get(i).getCantidad() + " " + orden.getOrden_detalle().get(i).getProducto_nombre() + "\n";
            i++;
        }
        holder.orden_detalle.setText(previo);


        //METODO PARA QUE PUEDA LLAMAR UNICAMENTE CUANDO ESTE DENTRO DE LAS 2H SIGUIENTES A REALIZADO EL PEDIDO
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fechaOrden = null;
        try {
           fechaOrden = formatter.parse(orden.getFecha_hora());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar limiteLlamada = Calendar.getInstance();
        Calendar limiteCalificacion = Calendar.getInstance();
        limiteCalificacion.setTime(fechaOrden);
        limiteCalificacion.add(Calendar.HOUR,168);    //limite para calificar 168h = 1 semana
        limiteLlamada.setTime(fechaOrden);
        limiteLlamada.add(Calendar.HOUR, 24);         //agregamos 24h a partir de realizada la orden como nuestro "limite" para llamar
        Calendar ahora = Calendar.getInstance();

        int diffLlamada = ahora.compareTo(limiteLlamada); // da 0 si es igual, menor a 0 si now es menor a su argumento y mayor a 0 si es mayor
        final int diffCalificacion = ahora.compareTo(limiteCalificacion);
        if(diffLlamada<0){
            holder.calificar.setTextColor(Color.BLACK);
            holder.llamar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    //Log.d("telefono",orden.getTelefono().toString());
                    callIntent.setData(Uri.parse("tel:"+orden.getTelefono()));
                    context.startActivity(callIntent);
                }
            });
        }else{
            holder.llamar.setTextColor(Color.LTGRAY);
        }

        //METODO PARA BOTON CALIFICAR
        if (orden.getCalificado() == null) {
            holder.calificar.setText("CALIFICAR");
            holder.calificar.setTextColor(Color.BLACK);
            holder.calificar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(diffCalificacion<0) {
                        showDialog((new Gson()).toJson(orden));
                    }else{
                        holder.calificar.setTextColor(Color.LTGRAY);
                    }
                }
            });
        } else {
            holder.calificar.setText("YA CALIFICASTE");
            holder.calificar.setTextColor(Color.LTGRAY);
        }

        //METODO PARA INFORMACION DEL ESTADO DE LA ORDEN
        holder.btnOrdenInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoEstadoOrden((new Gson()).toJson(orden));
            }
        });


    }

    public void showDialog(String orden) {
        FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
        CalificacionDialogFragment newFragment = new CalificacionDialogFragment();

        Bundle args = new Bundle();
        if(!orden.isEmpty() && !orden.equals(""))args.putString("orden", orden);
        newFragment.setArguments(args);

        newFragment.show(fragmentManager.beginTransaction(), "Calificar tu orden");

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

    private List<Orden> mItems;
    private Context mContext;

    public OrdenesAdapter(Context context, List<Orden> items) {
        mItems = items;
        mContext = context;
    }

    private OnItemClickListener mOnItemClickListener;

    interface OnItemClickListener {
        void onItemClick(Orden clickedOrden);
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
        public TextView calificar;
        public TextView llamar;


        public ViewHolder(View itemView) {
            super(itemView);
            idorden = (TextView) itemView.findViewById(R.id.txtOrdenIdOrden);
            nombreEmpresa = (TextView) itemView.findViewById(R.id.txtOrdenNombreEmpresa);
            fecha = (TextView) itemView.findViewById(R.id.txtOrdenFechaHora) ;
            orden_detalle = (TextView) itemView.findViewById(R.id.txtOrdenDetalle);
            estado = (TextView) itemView.findViewById(R.id.txtOrdenEstado);
            //info_estado = (TextView) itemView.findViewById(R.id.txtOrdenInfoEstado);
            btnOrdenInfo = (ImageView) itemView.findViewById(R.id.btnOrdenInfo);
            calificar = (TextView) itemView.findViewById(R.id.txtOrdenCalificar);
            llamar = (TextView) itemView.findViewById(R.id.txtOrdenLlamar);

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

    public void swapItems(List<Orden> ordenes) {
        if (ordenes == null) {
            mItems = new ArrayList<>(0);
        } else {
            mItems = ordenes;
        }
        notifyDataSetChanged();
    }
}
