package com.deliveryboss.app.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.deliveryboss.app.data.api.model.Delivery;
import com.deliveryboss.app.data.api.model.DeliveryRequest;
import com.deliveryboss.app.data.api.model.Usuario_direccion;
import com.deliveryboss.app.data.util.Utilidades;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.deliveryboss.app.R;
import com.deliveryboss.app.data.api.model.EmpresasBody;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Joaquin on 26/6/2017.
 */

public class EmpresasAdapter extends RecyclerView.Adapter<EmpresasAdapter.ViewHolder>{
    private Context context;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.empresa_item_list, parent, false);
        this.context = mContext;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        EmpresasBody empresa = mItems.get(position);

        //View statusIndicator = holder.statusIndicator;

        // estado: se colorea indicador según el estado
        /*
            switch (empresa.getTipo_entrega_idtipo_entrega()) {
                case "1":
                    // Si el tipo_entrega es 1
                    if(empresa.getPrecio_delivery().equals("0")) holder.precioDelivery.setText("Sólo delivery: ¡GRATIS!  ");
                    if(!empresa.getPrecio_delivery().equals("0")) holder.precioDelivery.setText("Sólo delivery: $"+empresa.getPrecio_delivery() + "  ");
                    break;
                case "2":
                    // Si el tipo_entrega es 2
                    holder.precioDelivery.setText("Sólo retiro en el local    ");
                    break;
                case "3":
                    // Si el tipo entrega es 3 tiene retiro en local y delivery
                    if(empresa.getPrecio_delivery().equals("0")) holder.precioDelivery.setText("Delivery: ¡GRATIS!  ");
                    if(!empresa.getPrecio_delivery().equals("0")) holder.precioDelivery.setText("Delivery: $"+empresa.getPrecio_delivery() + "  ");
                    break;
            }
            */

        holder.nombreEmpresa.setText(empresa.getNombre_empresa());

        if(empresa.getCalificacion_general()!=null) {
            holder.calificacion.setRating(Float.parseFloat(empresa.getCalificacion_general()));
            if(empresa.getCalificacion_general().length()>=3) holder.calificacionFloat.setText(empresa.getCalificacion_general().substring(0,3));
            if(empresa.getCalificacion_general().length()<3) holder.calificacionFloat.setText(empresa.getCalificacion_general());
        }else{
            holder.calificacion.setRating(0.0f);
            holder.calificacionFloat.setText("0.0");
        }
        holder.cantidad_calificacion.setText("("+empresa.getCantidad_calificacion()+")");


        if(empresa.getSubrubro().length()>34){
            String rubroSep = empresa.getSubrubro().replace(",", ", ");
            holder.rubro_elegido.setText(rubroSep.substring(0,31)+"...");
        }else {
            String rubroSep = empresa.getSubrubro().replace(",", ", ");
            holder.rubro_elegido.setText(rubroSep);
        }
        //holder.tiempo_delivery.setText(empresa.getTiempo_minimo_entrega() + "-" + empresa.getTiempo_maximo_entrega() + " minutos");
        holder.compra_minima.setText("Compra minima: $" + empresa.getCompra_minima());

        DeliveryRequest mDeliveryRequest;
        Delivery objeto;

        mDeliveryRequest = Utilidades.calcularPrecioDelivery(mDireccionUsuario,empresa);
        objeto = mDeliveryRequest.getDatos();

        // Si el calculo de distancias es exitoso
        if(mDeliveryRequest.getEstado()==1){
            holder.tiempo_delivery.setText("Distancia: "+Utilidades.formatearDistancia(objeto.getDistancia()));
            String precio = "";
            if(objeto.getPrecio()<=0){
                precio = "Delivery ¡GRATIS! ";
            }else{
                precio="Precio Delivery $"+(new DecimalFormat("#").format(objeto.getPrecio()))+" ";
            }
            holder.precioDelivery.setText(precio);
        }else{
            if(mDeliveryRequest.getEstado()==2){
                holder.tiempo_delivery.setText("");
                holder.precioDelivery.setText("No hay delivery a tu zona"+" ");
            }
            if(mDeliveryRequest.getEstado()==4){
                holder.tiempo_delivery.setText("No se calc");
                holder.precioDelivery.setText("No se calc"+" ");
            }
        }


        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        String diaActual = sdf.format(new Date());

        Log.d("dia", diaActual);

        Boolean abierto= false;
        String turno1desde = "";
        String turno2desde = "";
        String turno1hasta = "";
        String turno2hasta = "";

        int cantidad=empresa.getEmpresa_horario().size();
        for(int i=0;i<cantidad;i++){
            if(diaActual.equals(empresa.getEmpresa_horario().get(i).getDia())){
                // OBTENEMOS LOS TURNOS DEL DIA
                turno1desde=empresa.getEmpresa_horario().get(i).getTurno1_desde();
                turno2desde=empresa.getEmpresa_horario().get(i).getTurno2_desde();
                turno1hasta=empresa.getEmpresa_horario().get(i).getTurno1_hasta();
                turno2hasta=empresa.getEmpresa_horario().get(i).getTurno2_hasta();

                // SI TIENE LOS DOS TURNOS
                if(turno1desde!=null && turno2desde!=null) {
                    holder.horarios.setText("HOY de "+turno1desde+" a " +turno1hasta+" Y de "+turno2desde+" a "+turno2hasta);
                    abierto=true;
                }


                // SI TIENE SOLO TURNO 1
                if(turno1desde!=null && turno2desde==null){
                    holder.horarios.setText("HOY de "+turno1desde+" a " +turno1hasta);
                    abierto=true;
                }

                // SI TIENE SOLO TURNO 2
                if(turno2desde!=null && turno1desde==null){
                    holder.horarios.setText("HOY de "+turno2desde+" a " +turno2hasta);
                    abierto=true;
                }
            }
        }
        if(!abierto)holder.horarios.setText("HOY CERRADO");

        //Carga de los logos de las empresas con Picasso
        Picasso
                .with(context)
                .load(empresa.getLogo())
                .fit() // will explain later
                .into(holder.logoEmpresa);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private List<EmpresasBody> mItems;
    private Usuario_direccion mDireccionUsuario;
    private Context mContext;

    public EmpresasAdapter(Context context, List<EmpresasBody> items, Usuario_direccion direccionUsuario) {
        mItems = items;
        mContext = context;
        mDireccionUsuario = direccionUsuario;
    }

    private OnItemClickListener mOnItemClickListener;

    interface OnItemClickListener {
        void onItemClick(EmpresasBody clickedEmpresa);
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView logoEmpresa;
        public TextView nombreEmpresa;
        //public TextView direccion;
        public TextView precioDelivery;
        public TextView horarios;
        public SimpleRatingBar calificacion;
        public TextView rubro_elegido;
        public TextView tiempo_delivery;
        public TextView compra_minima;
        public TextView cantidad_calificacion;
        public TextView calificacionFloat;
        public View statusIndicator;

        public ViewHolder(View itemView) {
            super(itemView);

            //statusIndicator = itemView.findViewById(R.id.indicator_appointment_status);
            logoEmpresa = (ImageView) itemView.findViewById(R.id.img_logo_empresa);
            nombreEmpresa = (TextView) itemView.findViewById(R.id.txtNombreEmpresa);
            //direccion = (TextView) itemView.findViewById(R.id.txtDireccion);
            precioDelivery = (TextView) itemView.findViewById(R.id.txtPrecioDelivery);
            horarios = (TextView) itemView.findViewById(R.id.txtHorarios);
            calificacion = (SimpleRatingBar) itemView.findViewById(R.id.calificacion);
            rubro_elegido = (TextView) itemView.findViewById(R.id.txtRubroElegido);
            tiempo_delivery = (TextView) itemView.findViewById(R.id.txtTiempoDelivery);
            compra_minima = (TextView) itemView.findViewById(R.id.txtCompraMinima);
            cantidad_calificacion = (TextView) itemView.findViewById(R.id.txtCantidadCalificacion);
            calificacionFloat = (TextView) itemView.findViewById(R.id.txtCalificacionFloat);

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

    public void swapItems(List<EmpresasBody> empresas, Usuario_direccion direccionUsuario) {
        if (empresas == null) {
            mItems = new ArrayList<>(0);
        } else {
            mItems = empresas;
            mDireccionUsuario= direccionUsuario;
        }
        notifyDataSetChanged();
    }





}
