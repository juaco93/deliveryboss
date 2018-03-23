package com.deliveryboss.app.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deliveryboss.app.R;
import com.deliveryboss.app.data.api.model.CategoriaEmpresa;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joaquin on 19/03/2018.
 */

public class CategoriaEmpresasAdapter extends RecyclerView.Adapter<CategoriaEmpresasAdapter.ViewHolder>{
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

        CategoriaEmpresa categoriaEmpresa = mItems.get(position);

        holder.categoria.setText(categoriaEmpresa.getCategoria());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private List<CategoriaEmpresa> mItems;
    private Context mContext;

    public CategoriaEmpresasAdapter(Context context, List<CategoriaEmpresa> items) {
        mItems = items;
        mContext = context;
    }

    private OnItemClickListener mOnItemClickListener;

    interface OnItemClickListener {
        void onItemClick(CategoriaEmpresa clickedCategoria);
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView categoria;

        public ViewHolder(View itemView) {
            super(itemView);

            categoria = (TextView) itemView.findViewById(R.id.txtNombre);


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

    public void swapItems(List<CategoriaEmpresa> categoria) {
        if (categoria == null) {
            mItems = new ArrayList<>(0);
        } else {
            mItems = categoria;
        }
        notifyDataSetChanged();
    }



}