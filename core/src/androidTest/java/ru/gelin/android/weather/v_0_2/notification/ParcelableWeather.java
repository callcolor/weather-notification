/*
 * Copyright 2010—2017 Denis Nelubin and others.
 *
 * This file is part of Weather Notification.
 *
 * Weather Notification is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Weather Notification is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Weather Notification.  If not, see http://www.gnu.org/licenses/.
 */

package ru.gelin.android.weather.v_0_2.notification;

import android.os.Parcel;
import android.os.Parcelable;
import ru.gelin.android.weather.v_0_2.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ParcelableWeather extends SimpleWeather implements Parcelable {

    public ParcelableWeather() {
    }

    /**
     *  Copy constructor.
     */
    public ParcelableWeather(Weather weather) {
        Location location = weather.getLocation();
        if (location == null) {
            setLocation(new SimpleLocation(null));
        } else {
            setLocation(new SimpleLocation(location.getText()));
        }
        Date time = weather.getTime();
        if (time == null) {
            setTime(new Date(0));
        } else {
            setTime(time);
        }
        UnitSystem unit = weather.getUnitSystem();
        if (unit == null) {
            setUnitSystem(UnitSystem.SI);
        } else {
            setUnitSystem(unit);
        }
        List<WeatherCondition> conditions = weather.getConditions();
        if (conditions == null) {
            return;
        }
        List<WeatherCondition> copyConditions = new ArrayList<WeatherCondition>();
        for (WeatherCondition condition : conditions) {
            SimpleWeatherCondition copyCondition = new SimpleWeatherCondition();
            copyCondition.setConditionText(condition.getConditionText());
            Temperature temp = condition.getTemperature();
            SimpleTemperature copyTemp = new SimpleTemperature(unit);
            if (temp != null) {
                copyTemp.setCurrent(temp.getCurrent(), unit);
                copyTemp.setLow(temp.getLow(), unit);
                copyTemp.setHigh(temp.getHigh(), unit);
            }
            copyCondition.setTemperature(copyTemp);
            copyCondition.setHumidityText(condition.getHumidityText());
            copyCondition.setWindText(condition.getWindText());
            copyConditions.add(copyCondition);
        }
        setConditions(copyConditions);
    }

    //@Override
    public int describeContents() {
        return 0;
    }

    //@Override
    public void writeToParcel(Parcel dest, int flags) {
        Location location = getLocation();
        if (location == null) {
            dest.writeString(null);
        } else {
            dest.writeString(location.getText());
        }
        Date time = getTime();
        if (time == null) {
            dest.writeLong(0);
        } else {
            dest.writeLong(time.getTime());
        }
        UnitSystem unit = getUnitSystem();
        if (unit == null) {
            dest.writeString(null);
        } else {
            dest.writeString(unit.toString());
        }
        if (getConditions() == null) {
            return;
        }
        for (WeatherCondition condition : getConditions()) {
            dest.writeString(condition.getConditionText());
            Temperature temp = condition.getTemperature();
            if (temp == null) {
                continue;
            }
            dest.writeInt(temp.getCurrent());
            dest.writeInt(temp.getLow());
            dest.writeInt(temp.getHigh());
            dest.writeString(condition.getHumidityText());
            dest.writeString(condition.getWindText());
        }
    }

    private ParcelableWeather(Parcel in) {
        setLocation(new SimpleLocation(in.readString()));
        setTime(new Date(in.readLong()));
        String unit = in.readString();
        try {
            setUnitSystem(UnitSystem.valueOf(unit));
        } catch (Exception e) {
            setUnitSystem(UnitSystem.SI);
        }
        List<WeatherCondition> conditions = new ArrayList<WeatherCondition>();
        while (in.dataAvail() > 6) {    //each condition takes 6 positions
            SimpleWeatherCondition condition = new SimpleWeatherCondition();
            condition.setConditionText(in.readString());
            SimpleTemperature temp = new SimpleTemperature(getUnitSystem());
            temp.setCurrent(in.readInt(), getUnitSystem());
            temp.setLow(in.readInt(), getUnitSystem());
            temp.setHigh(in.readInt(), getUnitSystem());
            condition.setTemperature(temp);
            condition.setHumidityText(in.readString());
            condition.setWindText(in.readString());
            conditions.add(condition);
        }
        setConditions(conditions);
    }

    public static final Creator<ParcelableWeather> CREATOR =
            new Creator<ParcelableWeather>() {
        public ParcelableWeather createFromParcel(Parcel in) {
            return new ParcelableWeather(in);
        }
        public ParcelableWeather[] newArray(int size) {
            return new ParcelableWeather[size];
        }
    };

}
