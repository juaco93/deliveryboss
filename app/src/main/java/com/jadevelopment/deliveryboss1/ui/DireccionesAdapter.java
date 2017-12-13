package com.jadevelopment.deliveryboss1.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jadevelopment.deliveryboss1.R;
import com.jadevelopment.deliveryboss1.data.api.DeliverybossApi;
import com.jadevelopment.deliveryboss1.data.api.model.ApiResponse;
import com.jadevelopment.deliveryboss1.data.api.model.MessageEvent;
import com.jadevelopment.deliveryboss1.data.api.model.Usuario_direccion;
import com.jadevelopment.deliveryboss1.data.prefs.SessionPrefs;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class DireccionesAdapter extends RecyclerView.Adapter<DireccionesAdapter.ViewHolder> {
    private Context context;
    private Retrofit mRestAdapter;
    private DeliverybossApi mDeliverybossApi;
    String authorization;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.direccion_item_list, parent, false);
        this.context = mContext;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Usuario_direccion direccion = mItems.get(position);
        holder.nombre_calle.setText("Calle: "+direccion.getCalle());
        holder.numero.setText("N°: "+direccion.getNumero());
        holder.habitacion.setText("Piso/depto: "+direccion.getHabitacion());
        holder.barrio.setText("Barrio: "+direccion.getBarrio());
        holder.telefono.setText("Teléfono: "+direccion.getTelefono());
        holder.ciudad.setText("Ciudad: "+direccion.getCiudad());
        holder.eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("¿Está seguro que desea eliminar la dirección?");
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        eliminarDireccion(direccion.getIdusuario_direccion());
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        holder.modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog((new Gson()).toJson(direccion));
            }
        });
    }

    public void showDialog(String direccion) {
        FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
        ModificarDireccionFragment newFragment = new ModificarDireccionFragment();

        Bundle args = new Bundle();
        if(!direccion.isEmpty() && !direccion.equals(""))args.putString("direccion", direccion);
        newFragment.setArguments(args);

        // setup link back to use and display
        //newFragment.setTargetActivity(this,FRAGMENTO_AGREGAR_DIRECCION);
        newFragment.show(fragmentManager.beginTransaction(), "Agregar direccion");

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private List<Usuario_direccion> mItems;
    private Context mContext;

    public DireccionesAdapter(Context context, List<Usuario_direccion> items) {
        mItems = items;
        mContext = context;
    }

    private OnItemClickListener mOnItemClickListener;

    interface OnItemClickListener {
        void onItemClick(Usuario_direccion clickedDireccion);
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nombre_calle;
        public TextView numero;
        public TextView habitacion;
        public TextView barrio;
        public TextView telefono;
        public TextView ciudad;
        public TextView modificar;
        public TextView eliminar;


        public ViewHolder(View itemView) {
            super(itemView);
            nombre_calle = (TextView) itemView.findViewById(R.id.txtDireccionNombreCalle);
            numero = (TextView) itemView.findViewById(R.id.txtDireccionNumero);
            habitacion = (TextView) itemView.findViewById(R.id.txtDireccionHabitacion) ;
            barrio = (TextView) itemView.findViewById(R.id.txtDireccionBarrio);
            telefono = (TextView) itemView.findViewById(R.id.txtDireccionTelefono);
            ciudad = (TextView) itemView.findViewById(R.id.txtDireccionCiudad);
            modificar = (TextView) itemView.findViewById(R.id.txtDireccionModificar);
            eliminar = (TextView) itemView.findViewById(R.id.txtDireccionEliminar);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                mOnItemClickListener.onItemClick(mItems.get(position));
            }
        }
    }

    public void swapItems(List<Usuario_direccion> direcciones) {
        if (direcciones == null) {
            mItems = new ArrayList<>(0);
        } else {
            mItems = direcciones;
        }
        notifyDataSetChanged();
    }


    private void eliminarDireccion(String idusuario_direccion){
        // Inicializar GSON
        Gson gson =
                new GsonBuilder()
                        .create();

        // Crear conexión al servicio REST
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(DeliverybossApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        // Crear conexión a la API de Deliveryboss
        mDeliverybossApi = mRestAdapter.create(DeliverybossApi.class);

        authorization = SessionPrefs.get(context).getPrefUsuarioToken();
        String idusuario = SessionPrefs.get(context).getPrefUsuarioIdUsuario();
        Log.d("gson", "Recuperando Direcciones desde el Server");

        // Realizar petición HTTP
        Call<ApiResponse> call = mDeliverybossApi.eliminarDireccionUsuario(authorization,idusuario, idusuario_direccion);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call,
                                   Response<ApiResponse> response) {
                if (!response.isSuccessful()) {
                    // Procesar error de API
                    String error = "Ha ocurrido un error. Contacte al administrador";
                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("json")) {

                        Log.d("gson", response.errorBody().toString());
                        //ApiError apiError = ApiError.fromResponseBody(response.errorBody());

                        //error = apiError.getMessage();
                        //Log.d(TAG, apiError.getDeveloperMessage());
                    } else {
                        Log.d("gson", response.errorBody().toString());
                        /*try {
                            // Reportar causas de error no relacionado con la API
                            Log.d(TAG, response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                    }
                    //showLoadingIndicator(false);
                    //showErrorMessage(error);
                    Log.d("gson", response.message());
                    Log.d("gson", response.raw().toString());
                    return;
                }

                String mensaje = response.body().getMensaje();
                Log.d("gson", "todo bien, mensaje del SV: " + response.body().getMensaje().toString());
                Toast.makeText(context,mensaje,Toast.LENGTH_LONG).show();
                EventBus.getDefault().post(new MessageEvent("4","Se elimino una direccion."));

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                //showLoadingIndicator(false);
                Log.d("gson", "Petición rechazada:" + t.getMessage());
                //showErrorMessage("Error de comunicación");
            }
        });
    }

}