package org.hockey.hockeyware.client.setting;

import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.events.client.OptionChangeEvent;
import org.hockey.hockeyware.client.events.client.SettingEvent;

import java.awt.*;
import java.util.function.Supplier;

public class Setting<T> extends Observable<SettingEvent<T>> {
    private final String name;
    private final T defaultValue;
    private final Supplier<Boolean> visibility;
    private final Number min;
    public T value;
    private final Number max;

    public Setting(String name, T value) {
        this(name, value, null, null, null);
    }

    public Setting(String name, T value, Supplier<Boolean> visibility) {
        this(name, value, null, null, visibility);
    }

    public Setting(String name, T value, Number min, Number max) {
        this(name, value, min, max, null);
    }

    public Setting(String name, T value, Number min, Number max, Supplier<Boolean> visibility) {
        this.name = name;
        this.defaultValue = value;
        this.value = value;
        this.min = min;
        this.max = max;
        this.visibility = visibility;
    }

    public void setValue(T value, boolean withEvent) {
        if (withEvent) {
            SettingEvent<T> event = onChange(new SettingEvent<>(this, value));
            if (!event.isCanceled()) {
                this.value = event.getValue();
            }
        } else {
            this.value = value;
        }
    }

    public static int current(Enum clazz) {
        for (int i = 0; i < clazz.getClass().getEnumConstants().length; ++i) {
            Enum e = ((Enum[]) clazz.getClass().getEnumConstants())[i];
            if (e.name().equalsIgnoreCase(clazz.name())) {
                return i;
            }
        }

        return -1;
    }

    public static Enum increase(Enum clazz) {
        int index = current(clazz);

        for (int i = 0; i < clazz.getClass().getEnumConstants().length; ++i) {
            Enum e = ((Enum[]) clazz.getClass().getEnumConstants())[i];
            if (i == index + 1) {
                return e;
            }
        }

        return ((Enum[]) clazz.getClass().getEnumConstants())[0];
    }

    public static Enum<?> decrease(Enum<?> clazz) {
        int index = current(clazz);

        for (int i = 0; i < clazz.getClass().getEnumConstants().length; i++) {
            Enum<?> e = ((Enum<?>[]) clazz.getClass().getEnumConstants())[i];
            if (i == index - 1) {
                return e;
            }
        }

        return ((Enum<?>[]) clazz.getClass().getEnumConstants())[0];
    }

    // ONLY CAN BE USED WITH NUMBERS WILL PRODUCE CASTING EXCEPTION OTHERWISE
    public T numberToValue(Number number) {
        Class<? extends Number> type = (Class<? extends Number>) this.value.getClass();
        Object result = null;

        if (type == Integer.class) {
            result = number.intValue();
        } else if (type == Float.class) {
            result = number.floatValue();
        } else if (type == Double.class) {
            result = number.doubleValue();
        } else if (type == Short.class) {
            result = number.shortValue();
        } else if (type == Byte.class) {
            result = number.byteValue();
        } else if (type == Long.class) {
            result = number.longValue();
        }

        return (T) result;
    }

    public String getName() {
        return name;
    }


    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
        HockeyWare.EVENT_BUS.post(new OptionChangeEvent(this));
    }

    public Number getMin() {
        return min;
    }

    public Number getMax() {
        return max;
    }

    public boolean isBoolean() {
        return (getValue() instanceof Boolean);
    }

    public boolean isEnum() {
        return (getValue() instanceof Enum);
    }

    public boolean isColor() {
        return (getValue() instanceof Color);
    }

    public boolean isNumber() {
        return (getValue() instanceof Number);
    }

    public boolean getDefaultValue() {
        return false;
    }

    public Setting<T> setDescription(String s) {
        return null;
    }
}
