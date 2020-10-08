package com.deliveryboss.app.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.deliveryboss.app.data.api.model.BodegasBody;
import com.deliveryboss.app.data.api.model.Usuario_direccion;
import com.deliveryboss.app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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

        BodegasBody empresa = mItems.get(position);

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

        holder.nombreEmpresa.setText(empresa.getNombre());
        holder.ciudad.setText(empresa.getCiudad());
        holder.direccion.setText(empresa.getDireccion());
        holder.tipoEntrega.setText(empresa.getTipo_entrega());

        if(empresa.getIdempresa_rubro()!=null){
            String texto="";
            if(empresa.getRubro1()!=null){
                texto+=empresa.getRubro1();
            }
            if(empresa.getRubro2()!=null){
                texto+=","+empresa.getRubro2();
            }
            if(empresa.getRubro3()!=null){
                texto+=","+empresa.getRubro3();
            }
            if(texto.length()>33) {
                String textoCorto = texto.substring(0, 29) + "...";
                holder.rubro_elegido.setText(textoCorto);
            }else{
                holder.rubro_elegido.setText(texto);
            }
        }


        //Carga de los logos de las empresas con Picasso
        if(empresa.getImagen()!=null && !empresa.getImagen().isEmpty()){
            Picasso
                    .with(context)
                    .load(empresa.getImagen())
                    .fit() // will explain later
                    .into(holder.logoEmpresa);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private List<BodegasBody> mItems;
    private Usuario_direccion mDireccionUsuario;
    private Context mContext;

    public EmpresasAdapter(Context context, List<BodegasBody> items, Usuario_direccion direccionUsuario) {
        mItems = items;
        mContext = context;
        mDireccionUsuario = direccionUsuario;
    }

    private OnItemClickListener mOnItemClickListener;

    interface OnItemClickListener {
        void onItemClick(BodegasBody clickedEmpresa);
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
        public TextView direccion;
        public TextView precioDelivery;
        public TextView rubro_elegido;
        public TextView ciudad;
        public TextView tipoEntrega;
        public View statusIndicator;

        public ViewHolder(View itemView) {
            super(itemView);

            //statusIndicator = itemView.findViewById(R.id.indicator_appointment_status);
            logoEmpresa = (ImageView) itemView.findViewById(R.id.img_logo_empresa);
            nombreEmpresa = (TextView) itemView.findViewById(R.id.txtNombreEmpresa);
            direccion = (TextView) itemView.findViewById(R.id.txtDireccion);
            rubro_elegido = (TextView) itemView.findViewById(R.id.txtRubroElegido);
            ciudad  = (TextView) itemView.findViewById(R.id.txtCiudad);
            tipoEntrega = (TextView) itemView.findViewById(R.id.txtTipoEntrega);

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

    public void swapItems(List<BodegasBody> empresas, Usuario_direccion direccionUsuario) {
        if (empresas == null) {
            mItems = new ArrayList<>(0);
        } else {
            mItems = empresas;
            mDireccionUsuario= direccionUsuario;
        }
        notifyDataSetChanged();
    }
}
