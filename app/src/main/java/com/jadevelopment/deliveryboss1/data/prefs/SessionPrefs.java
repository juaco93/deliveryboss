package com.jadevelopment.deliveryboss1.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.jadevelopment.deliveryboss1.data.api.model.Usuario;

/**
 * Created by Joaquin on 23/6/2017.
 */

public class SessionPrefs {

    public static final String PREFS_NAME = "DELIVERYBOSS_PREFS";
    public static final String PREF_IDUSUARIO = "PREF_IDUSUARIO";
    public static final String PREF_REGID = "PREF_REGID";
    public static final String PREF_USUARIO_NOMBRE = "PREF_USUARIO_NOMBRE";
    public static final String PREF_USUARIO_APELLIDO = "PREF_USUARIO_APELLIDO";
    public static final String PREF_USUARIO_EMAIL = "PREF_USUARIO_EMAIL";
    public static final String PREF_USUARIO_TELEFONO = "PREF_USUARIO_TELEFONO";
    public static final String PREF_USUARIO_TOKEN = "PREF_USUARIO_TOKEN";
    public static final String PREF_USUARIO_CONTRASENA = "PREF_USUARIO_CONTRASENA";
    public static final String PREF_USUARIO_ULTIMA_DIRECCION = "PREF_USUARIO_ULTIMA_DIRECCION";
    public static final String PREF_USUARIO_IDFACEBOOK = "PREF_USUARIO_IDFACEBOOK";
    public static final String PREF_USUARIO_IMAGEN = "PREF_USUARIO_IMAGEN";
    public static final String PREF_USUARIO_IDCIUDAD = "PREF_USUARIO_IDCIUDAD";
    public static final String PREF_USUARIO_CIUDAD = "PREF_USUARIO_CIUDAD";

    private static SessionPrefs INSTANCE;
    private final SharedPreferences mPrefs;
    private boolean mIsLoggedIn = false;

    public static SessionPrefs get(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SessionPrefs(context);
        }
        return INSTANCE;
    }

    private SessionPrefs(Context context) {
        mPrefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        mIsLoggedIn = !TextUtils.isEmpty(mPrefs.getString(PREF_USUARIO_TOKEN, null));
    }


    public boolean isLoggedIn(){
        return mIsLoggedIn;
    }

    public void saveUsuario(Usuario usuario) {
        if (usuario != null) {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(PREF_IDUSUARIO, usuario.getIdusuario());
            editor.putString(PREF_USUARIO_APELLIDO, usuario.getApellido());
            editor.putString(PREF_USUARIO_EMAIL, usuario.getE_mail());
            editor.putString(PREF_USUARIO_TELEFONO, usuario.getTelefono());
            editor.putString(PREF_USUARIO_NOMBRE, usuario.getNombre());
            editor.putString(PREF_USUARIO_TOKEN, usuario.getToken());
            editor.putString(PREF_USUARIO_IMAGEN, usuario.getImagen());
            //editor.putString(PREF_USUARIO_CONTRASENA, usuario.getContrasena());
            //editor.putString(PREF_USUARIO_ULTIMA_DIRECCION, usuario.getUltima_direccion());
            editor.apply();

            mIsLoggedIn = true;
        }
    }

    public void modificarUsuario(Usuario usuario) {
        if (usuario != null) {
            SharedPreferences.Editor editor = mPrefs.edit();
            //editor.putString(PREF_IDUSUARIO, usuario.getIdusuario());
            editor.putString(PREF_USUARIO_APELLIDO, usuario.getApellido());
            editor.putString(PREF_USUARIO_EMAIL, usuario.getE_mail());
            editor.putString(PREF_USUARIO_TELEFONO, usuario.getTelefono());
            editor.putString(PREF_USUARIO_NOMBRE, usuario.getNombre());
            //editor.putString(PREF_USUARIO_TOKEN, usuario.getToken());
            editor.putString(PREF_USUARIO_IMAGEN, usuario.getImagen());
            //editor.putString(PREF_USUARIO_CONTRASENA, usuario.getContrasena());
            //editor.putString(PREF_USUARIO_ULTIMA_DIRECCION, usuario.getUltima_direccion());
            editor.apply();

            mIsLoggedIn = true;
        }
    }

    public void saveRegId(String regId) {
        if (regId != null) {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(PREF_REGID, regId);
            editor.apply();
        }
    }

    public void saveCiudad(String idciudad, String ciudad){
        if(idciudad!=null){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(PREF_USUARIO_IDCIUDAD, idciudad);
        editor.putString(PREF_USUARIO_CIUDAD, ciudad);
        editor.apply();
        }
    }


    public void logOut(){
        mIsLoggedIn = false;
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(PREF_IDUSUARIO, null);
        editor.putString(PREF_USUARIO_NOMBRE, null);
        editor.putString(PREF_USUARIO_APELLIDO, null);
        editor.putString(PREF_USUARIO_EMAIL, null);
        editor.putString(PREF_USUARIO_TELEFONO, null);
        editor.putString(PREF_USUARIO_TOKEN, null);
        editor.putString(PREF_USUARIO_CONTRASENA, null);
        editor.putString(PREF_USUARIO_ULTIMA_DIRECCION, null);
        editor.putString(PREF_USUARIO_IMAGEN, null);
        editor.putString(PREF_USUARIO_IDCIUDAD, null);
        editor.apply();
    }

    public String getPrefUsuarioToken(){
        return mPrefs.getString(PREF_USUARIO_TOKEN, null);
    }
    public String getPrefUsuarioNombreyApellido(){
        String nombre = mPrefs.getString(PREF_USUARIO_NOMBRE, null);
        String apellido = mPrefs.getString(PREF_USUARIO_APELLIDO, null);
        return (nombre + " " + apellido);
    }
    public String getPrefUsuarioNombre(){
        return mPrefs.getString(PREF_USUARIO_NOMBRE, null);
    }
    public String getPrefUsuarioApellido(){
        return mPrefs.getString(PREF_USUARIO_APELLIDO, null);
    }
    public String getPrefUsuarioEmail(){
        return mPrefs.getString(PREF_USUARIO_EMAIL, null);
    }
    public String getPrefUsuarioIdUsuario(){
        return mPrefs.getString(PREF_IDUSUARIO, null);
    }
    public String getPrefUsuarioImagen(){
        return mPrefs.getString(PREF_USUARIO_IMAGEN, null);
    }
    public String getPrefUsuarioIdCiudad(){
        return mPrefs.getString(PREF_USUARIO_IDCIUDAD, null);
    }
    public String getPrefUsuarioCiudad(){
        return mPrefs.getString(PREF_USUARIO_CIUDAD, null);
    }
    public String getPrefUsuarioRegId(){
        return mPrefs.getString(PREF_REGID, null);
    }

}