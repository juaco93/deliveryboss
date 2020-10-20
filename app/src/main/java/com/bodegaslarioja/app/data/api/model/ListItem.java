package com.bodegaslarioja.app.data.api.model;

/**
 * Created by Joaquin on 27/06/2018.
 */

public abstract class ListItem {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    abstract public int getType();

}
