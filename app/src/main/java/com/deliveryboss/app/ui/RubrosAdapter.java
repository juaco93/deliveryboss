package com.deliveryboss.app.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.deliveryboss.app.R;
import com.deliveryboss.app.data.api.model.ListItem;
import com.deliveryboss.app.data.api.model.Producto;
import com.deliveryboss.app.data.api.model.Rubro;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RubrosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @NonNull
    private List<ListItem> items = Collections.emptyList();
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private Context context;

    public RubrosAdapter(@NonNull List<ListItem> items) {
        this.items = items;
    }

    interface OnItemClickListener {
        void onItemClick(Producto clickedProducto);
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        this.context = parent.getContext();

        switch (viewType) {
            case ListItem.TYPE_HEADER: {
                View itemView = inflater.inflate(R.layout.producto_item_separador, parent, false);
                return new HeaderViewHolder(itemView);
            }
            case ListItem.TYPE_ITEM: {
                View itemView = inflater.inflate(R.layout.producto_item_list, parent, false);
                return new EventViewHolder(itemView);
            }
            default:
                throw new IllegalStateException("unsupported item type");
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView txt_header;

        HeaderViewHolder(View itemView) {
            super(itemView);
            txt_header = (TextView) itemView.findViewById(R.id.txtRubroNombre);
        }

    }

    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView imagenProducto;
        TextView txt_title;
        TextView txt_detalle;
        TextView txt_precio;

        public EventViewHolder(View itemView) {
            super(itemView);
            imagenProducto = (ImageView) itemView.findViewById(R.id.img_producto);
            txt_title = (TextView) itemView.findViewById(R.id.txtNombreProducto);
            txt_detalle = (TextView) itemView.findViewById(R.id.txtProductoDetalle);
            txt_precio = (TextView) itemView.findViewById(R.id.txtPrecio);
            itemView.setOnClickListener(this);
           // Log.d("productosNuevo","Valor de this-->"+this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                //Log.d("productosNuevo","entre a constructor onClick");
                Producto producto = (Producto) items.get(position);
                mOnItemClickListener.onItemClick(producto);
            }
        }

    }




    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ListItem.TYPE_HEADER: {
                Rubro header = (Rubro) items.get(position);
                HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
                // your logic here
                holder.txt_header.setText(header.getRubro());
                break;
            }
            case ListItem.TYPE_ITEM: {
                final Producto producto = (Producto) items.get(position);
                EventViewHolder holder = (EventViewHolder) viewHolder;
                // your logic here
                //Carga de los logos de las empresas con Picasso
                if(producto.getImagen()!=null && !producto.getImagen().isEmpty()){
                    Picasso
                            .with(context)
                            .load(producto.getImagen())
                            .fit() // will explain later
                            .into(holder.imagenProducto);
                }
                if(producto.getProducto()!=null){
                    Log.d("joacoProd",producto.getProducto());
                    if(producto.getProducto().length()<=30){
                        holder.txt_title.setText(producto.getProducto());
                    }else{
                        String nombreCortado = producto.getProducto().substring(0,30) + "...";
                        holder.txt_title.setText(nombreCortado);
                    }
                }

                holder.txt_detalle.setText(producto.getDescripcion());
                holder.txt_precio.setText("$"+producto.getPrecio1());
                break;
            }
            default:
                throw new IllegalStateException("unsupported item type");
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    public void swapItems(List<ListItem> mItems) {
        if (mItems == null) {
            items = new ArrayList<>(0);
        } else {
            items = mItems;
        }
        notifyDataSetChanged();
    }

}



