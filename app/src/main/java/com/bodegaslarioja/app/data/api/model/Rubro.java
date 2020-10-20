package com.bodegaslarioja.app.data.api.model;

import android.support.annotation.NonNull;

/**
 * Created by Joaquin on 26/06/2018.
 */

public abstract class Rubro extends ListItem{

    @NonNull
    private String rubro;

    public Rubro(@NonNull String rubro) {
        this.rubro = rubro;
    }

    @NonNull
    public String getRubro() {
        return rubro;
    }

    // here getters and setters
    // for title and so on, built
    // using date

    public int getType() {
        return TYPE_HEADER;
    }
}
