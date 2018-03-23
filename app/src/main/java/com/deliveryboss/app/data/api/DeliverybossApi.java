package com.deliveryboss.app.data.api;

import com.deliveryboss.app.data.api.model.ApiResponse;
import com.deliveryboss.app.data.api.model.ApiResponseCalificaciones;
import com.deliveryboss.app.data.api.model.ApiResponseCiudades;
import com.deliveryboss.app.data.api.model.ApiResponseDirecciones;
import com.deliveryboss.app.data.api.model.ApiResponseMantenimiento;
import com.deliveryboss.app.data.api.model.ApiResponseOrdenes;
import com.deliveryboss.app.data.api.model.ApiResponseProductos;
import com.deliveryboss.app.data.api.model.ApiResponseUsuario;
import com.deliveryboss.app.data.api.model.Calificacion;
import com.deliveryboss.app.data.api.model.CiudadSugerida;
import com.deliveryboss.app.data.api.model.EmpresaSugerida;
import com.deliveryboss.app.data.api.model.FbLoginBody;
import com.deliveryboss.app.data.api.model.FbRegisterBody;
import com.deliveryboss.app.data.api.model.LoginBody;
import com.deliveryboss.app.data.api.model.Orden;
import com.deliveryboss.app.data.api.model.Usuario;
import com.deliveryboss.app.data.api.model.ApiResponseEmpresas;
import com.deliveryboss.app.data.api.model.UsuarioRegisterBody;
import com.deliveryboss.app.data.api.model.Usuario_direccion;
import com.deliveryboss.app.data.api.model.regIdBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Joaquin on 23/6/2017.
 */

public interface  DeliverybossApi {

    public static final String BASE_URL = "http://api.deliveryboss.com.ar/v1/";

    @POST("usuarios/login")
    Call<Usuario> login(@Body LoginBody loginBody);

    @POST("usuarios/login")
    Call<Usuario> loginFb(@Body FbLoginBody fbLoginBody);

    @POST("usuarios/registro")
    Call<Usuario> registroFb(@Body FbRegisterBody fbRegisterBody);

    @POST("usuarios/registro")
    Call<ApiResponse> registroNormal(@Body UsuarioRegisterBody usuarioRegisterBody);

    @GET("usuarios/{idusuario}")
    Call<ApiResponseUsuario> obtenerUsuarioPorId(@Header("Authorization") String authorization,
                                                 @Path(value = "idusuario", encoded = true) String idusuario);

    @PUT("usuarios/{idusuario}")
    Call<ApiResponse> modificarUsuario(@Header("Authorization") String authorization,
                                       @Body Usuario usuario,
                                       @Path(value = "idusuario", encoded = true) String idusuario);

    @POST("usuarios/registrationId")
    Call<ApiResponse> registrarRegId(@Body regIdBody regIdBody);

    @GET("empresas/")
    Call<ApiResponseEmpresas> obtenerEmpresas(@Header("Authorization") String authorization);

    @GET("empresas/{idciudad}/{idrubro}")
    Call<ApiResponseEmpresas> obtenerEmpresasPorRubro(@Header("Authorization") String authorization,
                                                      @Path(value = "idciudad", encoded = true) String idciudad,
                                                      @Path(value = "idrubro", encoded = true) String idrubro
    );

    @GET("productos/{idempresa}")
    Call<ApiResponseProductos> obtenerProductos(@Header("Authorization") String authorization,
                                                @Path(value = "idempresa", encoded = true) String idempresa
    );

    // METODOS PARA CALIFICACIONES
    @POST("calificaciones/{idempresa}/{idusuario}")
    Call<ApiResponse> insertarCalificacion(@Header("Authorization") String authorization,
                                            @Path(value = "idempresa", encoded = true) String idempresa,
                                            @Path(value = "idusuario", encoded = true) String idusuario,
                                            @Body Calificacion calificacion
    );
    @GET("calificaciones/{idempresa}/{idusuario}")
    Call<ApiResponseCalificaciones> obtenerCalificacionUsuario(@Header("Authorization") String authorization,
                                                               @Path(value = "idempresa", encoded = true) String idempresa,
                                                               @Path(value = "idusuario", encoded = true) String idusuario
    );

    // METODOS PARA ORDENES
    @GET("ordenes/{idempresa}/{idusuario}")
    Call<ApiResponseOrdenes> obtenerOrdenesUsuario(@Header("Authorization") String authorization,
                                                        @Path(value = "idempresa", encoded = true) String idempresa,
                                                        @Path(value = "idusuario", encoded = true) String idusuario
    );

    @POST("ordenes/{idempresa}/{idusuario}")
    Call<ApiResponse> insertarOrden(@Header("Authorization") String authorization,
                              @Path(value = "idempresa", encoded = true) String idempresa,
                              @Path(value = "idusuario", encoded = true) String idusuario,
                              @Body Orden orden
    );


    ///// METODOS PARA DIRECCIONES
    @GET("direcciones/{idusuario}")
    Call<ApiResponseDirecciones> obtenerDireccionesUsuario(@Header("Authorization") String authorization,
                                                           @Path(value = "idusuario", encoded = true) String idusuario
    );

    @POST("direcciones/{idusuario}")
    Call<ApiResponse> insertarDireccionUsuario(@Header("Authorization") String authorization,
                                                           @Path(value = "idusuario", encoded = true) String idusuario,
                                                          @Body Usuario_direccion direccion
    );

    @PUT("direcciones/{idusuario}/{iddireccion}")
    Call<ApiResponse> modificarDireccionUsuario(@Header("Authorization") String authorization,
                                                          @Path(value = "idusuario", encoded = true) String idusuario,
                                                          @Path(value = "iddireccion", encoded = true) String iddireccion,
                                                          @Body Usuario_direccion direccion
    );

    @DELETE("direcciones/{idusuario}/{idusuario_direccion}")
    Call<ApiResponse> eliminarDireccionUsuario(@Header("Authorization") String authorization,
                                                           @Path(value = "idusuario", encoded = true) String idusuario,
                                                            @Path(value = "idusuario_direccion", encoded = true) String idusuario_direccion
    );


    // METODO PARA OBTENER CIUDADES
    @GET("ciudades/")
    Call<ApiResponseCiudades> obtenerCiudades(@Header("Authorization") String authorization
    );

    // METODO PARA OBTENER MODOS (mantenimiento, normal, etc)
    @GET("mantenimientos/")
    Call<ApiResponseMantenimiento> obtenerMantenimiento(@Header("Authorization") String authorization
    );

    // SUGERIR EMPRESA
    @POST("sugerir_empresas")
    Call<ApiResponse> sugerirEmpresa(@Header("Authorization") String authorization,
                                        @Body EmpresaSugerida empresaSugerida
    );

    // SUGERIR CIUDAD
    @POST("sugerir_ciudades")
    Call<ApiResponse> sugerirCiudad(@Header("Authorization") String authorization,
                                     @Body CiudadSugerida ciudadSugerida
    );


}
