package com.jadevelopment.deliveryboss1.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jadevelopment.deliveryboss1.R;
import com.jadevelopment.deliveryboss1.data.api.model.EmpresasBody;
import com.jadevelopment.deliveryboss1.data.api.model.Orden;
import com.jadevelopment.deliveryboss1.data.api.model.Orden_detalle;
import com.jadevelopment.deliveryboss1.data.api.model.Producto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joaquin on 29/7/2017.
 */

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.ViewHolder> {
    private Context context;
    private List<Integer> selectedIds = new ArrayList<>();


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.carrito_item_list, parent, false);
        this.context = mContext;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Orden_detalle orden = mItems.get(position);
            holder.cantidadProducto.setText(orden.getCantidad());
            holder.nombreProducto.setText(orden.getProducto_nombre());
            if(orden.getProducto_precio()!=null && orden.getCantidad()!=null){
                Float subtot = Float.valueOf(orden.getProducto_precio()) * Integer.valueOf(orden.getCantidad());
                String subtotSt = String.format("%.2f", subtot);
                holder.subtotal.setText("$"+subtotSt);
            }

        if (orden.getSelected()){
            //if item is selected then,set foreground color of FrameLayout.
            holder.rootView.setBackgroundResource(R.color.colorSelect);
        }
        else {
            //else remove selected item color.
            holder.rootView.setBackgroundResource(android.R.color.transparent);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private List<Orden_detalle> mItems;
    private Context mContext;

    public CarritoAdapter(Context context, List<Orden_detalle> items) {
        mItems = items;
        mContext = context;
    }

    private CarritoAdapter.OnItemClickListener mOnItemClickListener;
    private CarritoAdapter.OnLongItemClickListener mOnLongItemClickListener;
    private CarritoAdapter.btnEditClickListener mOnBtnEditClickListener;

    interface OnItemClickListener {
        void onItemClick(Orden_detalle clickedItemCarrito);
    }

    interface OnLongItemClickListener {
        void onLongItemClick(Orden_detalle clickedItemCarrito);
    }

    interface btnEditClickListener {
        void onBtnEditClick(Orden_detalle clickedItemCarrito);
    }

    interface btnDeleteClickListener {
        void onBtnDeleteClick(Orden_detalle clickedItemCarrito);
    }

    public CarritoAdapter.OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }



    public void setOnItemClickListener(CarritoAdapter.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
    public void setOnBtnEditClickListener(CarritoAdapter.btnEditClickListener onBtnEditClickListener) {
        mOnBtnEditClickListener = onBtnEditClickListener;
    }
    public void setOnLongItemClickListener(CarritoAdapter.OnLongItemClickListener onLongItemClickListener) {
        mOnLongItemClickListener = onLongItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView nombreProducto;
        public TextView cantidadProducto;
        public TextView subtotal;
        public ImageView btnEdit;
        public RelativeLayout rootView;


        public ViewHolder(final View itemView) {
            super(itemView);
            nombreProducto = (TextView) itemView.findViewById(R.id.txtCarritoNombreProducto);
            cantidadProducto = (TextView) itemView.findViewById(R.id.txtCarritoCantidad) ;
            subtotal = (TextView) itemView.findViewById(R.id.txtCarritoSubtotal);
            btnEdit = (ImageView) itemView.findViewById(R.id.btnCarritoEdit);
            rootView = (RelativeLayout) itemView.findViewById(R.id.rootView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mOnLongItemClickListener.onLongItemClick(mItems.get(position));
                        itemView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        notifyDataSetChanged();
                    }
                    return  false;
                }
            });

                    btnEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                mOnBtnEditClickListener.onBtnEditClick(mItems.get(position));
                            }
                        }
                    });
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                mOnItemClickListener.onItemClick(mItems.get(position));
            }
        }


    }


    public void swapItems(List<Orden_detalle> ordenes) {
        if (ordenes == null) {
            mItems = new ArrayList<>(0);
        } else {
            mItems = ordenes;
        }
        notifyDataSetChanged();
    }
}
