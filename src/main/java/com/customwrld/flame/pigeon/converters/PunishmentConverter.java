package com.customwrld.flame.pigeon.converters;

import com.customwrld.flame.profile.punishment.Punishment;
import com.customwrld.pigeon.Converter;

public class PunishmentConverter extends Converter<Punishment> {
    public PunishmentConverter() { super(Punishment.class, false); }

    @Override
    public Punishment convertToValue(Class<?> fieldType, Object simple) {
        return (Punishment) simple;
    }

    @Override
    public Object convertToSimple(Class<?> fieldType, Punishment value) {
        return value;
    }
}
