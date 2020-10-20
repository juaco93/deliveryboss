package com.bodegaslarioja.app.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bodegaslarioja.app.R;
import com.bodegaslarioja.app.data.api.model.Calificacion;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CalificacionesAdapter extends RecyclerView.Adapter<CalificacionesAdapter.ViewHolder> {
    private Context context;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.calificacion_item_list, parent, false);
        this.context = mContext;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Calificacion calificacion = mItems.get(position);
        holder.usuario.setText(calificacion.getUsuario());
        holder.fecha_hora.setText(calificacion.getFecha_hora());
        holder.calificacion_usuario.setText(calificacion.getCalificacion_general());
        if(calificacion.getComentario()!=null && !calificacion.getComentario().isEmpty())
        {
            holder.comentario.setText(calificacion.getComentario());
        }else{
            holder.comentario.setText(calificacion.getComentario());
            /*RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.fecha_hora.getLayoutParams();
            params.setMargins(40,80,0,0);
            holder.fecha_hora.setLayoutParams(params);*/
        }


        if(calificacion.getImagen()!=null && !calificacion.getImagen().isEmpty()){
            Picasso
                    .with(context)
                    .load(calificacion.getImagen())
                    .fit() // will explain later
                    .into(holder.imagen);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private List<Calificacion> mItems;
    private Context mContext;

    public CalificacionesAdapter(Context context, List<Calificacion> items) {
        mItems = items;
        mContext = context;
    }

    private OnItemClickListener mOnItemClickListener;

    interface OnItemClickListener {
        void onItemClick(Calificacion clickedCalificacion);
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView usuario;
        public TextView fecha_hora;
        public TextView calificacion_usuario;
        public TextView comentario;
        public CircleImageView imagen;


        public ViewHolder(View itemView) {
            super(itemView);
            usuario = (TextView) itemView.findViewById(R.id.txtCalificacionNombreUsuario);
            fecha_hora = (TextView) itemView.findViewById(R.id.txtCalificacionFechaHora) ;
            calificacion_usuario = (TextView) itemView.findViewById(R.id.txtCalificacionUsuario);
            comentario = (TextView) itemView.findViewById(R.id.txtCalificacionComentario);
            imagen = (CircleImageView) itemView.findViewById(R.id.imgCalificacionUsuario);
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

    public void swapItems(List<Calificacion> calificaciones) {
        if (calificaciones == null) {
            mItems = new ArrayList<>(0);
        } else {
            mItems = calificaciones;
        }
        notifyDataSetChanged();
    }
}