package com.bodegaslarioja.app.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bodegaslarioja.app.R;
import com.bodegaslarioja.app.data.api.model.Producto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joaquin on 26/6/2017.
 */

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ViewHolder> {
    private Context context;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.producto_item_list, parent, false);
        this.context = mContext;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("joaco","CARGAMOS LOS PRODUCTOS EN EL ADAPTER");
        Producto producto = mItems.get(position);
        holder.detalleproducto.setText(producto.getDescripcion());
        holder.precio.setText("$" + producto.getPrecio1());

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private List<Producto> mItems;
    private Context mContext;

    public ProductosAdapter(Context context, List<Producto> items) {
        mItems = items;
        mContext = context;
    }

    private OnItemClickListener mOnItemClickListener;

    interface OnItemClickListener {
        void onItemClick(Producto clickedProducto);
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imagenProducto;
        public TextView producto;
        public TextView detalleproducto;
        public TextView precio;


        public ViewHolder(View itemView) {
            super(itemView);
            imagenProducto = (ImageView) itemView.findViewById(R.id.img_producto);
            producto = (TextView) itemView.findViewById(R.id.txtNombreProducto);
            detalleproducto = (TextView) itemView.findViewById(R.id.txtProductoDetalle) ;
            precio = (TextView) itemView.findViewById(R.id.txtPrecio);
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

    public void swapItems(List<Producto> productos) {
        if (productos == null) {
            mItems = new ArrayList<>(0);
        } else {
            mItems = productos;
        }
        notifyDataSetChanged();
    }
}
