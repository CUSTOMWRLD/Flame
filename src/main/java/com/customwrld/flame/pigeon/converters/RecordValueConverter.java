package com.customwrld.flame.pigeon.converters;

import com.customwrld.flame.util.RecordValue;
import com.customwrld.pigeon.Converter;
import com.customwrld.pigeon.converters.DateConverter;
import com.customwrld.pigeon.converters.StringConverter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

public class RecordValueConverter extends Converter<RecordValue> {

    public RecordValueConverter() { super(RecordValue.class, true, false); }

    @Override
    public RecordValue convertToValue(Type fieldType, JsonElement element) {
        return Utils.convertToValue(element);
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, RecordValue value) {
        return Utils.convertToSimple(value);
    }

    public static class Utils {
        public static RecordValue convertToValue(JsonElement element) {
            var data = element.getAsJsonObject();

            return new RecordValue(
                    DateConverter.Utils.convertToValue(data.get("date")),
                    StringConverter.Utils.convertToValue(data.get("value"))
            );
        }

        public static JsonElement convertToSimple(RecordValue value) {
            JsonObject data = new JsonObject();

            data.add("date", DateConverter.Utils.convertToSimple(value.getDate()));
            data.add("value", StringConverter.Utils.convertToSimple(value.getValue()));

            return data;
        }
    }

}
