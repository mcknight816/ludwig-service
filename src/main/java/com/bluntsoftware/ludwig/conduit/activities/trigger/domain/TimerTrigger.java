package com.bluntsoftware.ludwig.conduit.activities.trigger.domain;

import com.bluntsoftware.ludwig.conduit.activities.input.domain.InputSettings;
import com.bluntsoftware.ludwig.conduit.utils.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TimerTrigger implements EntitySchema {

    String second;
    String minute;
    String hour;
    String day;
    String month;
    String weekday;

   public  JsonSchema getJsonSchema() {
        JsonSchema schema = JsonSchema.builder().title("Scheduler Properties").build();
        //every 1 seconds
        schema.getProperties().put("settings", InputSettings.builder().build().getJsonSchema());

        List<String> second = new ArrayList<>();
        second.add("Every Second");
        for(int x = 0;x<60;x++){
            second.add(x + "");
        }

        List<String> minute = new ArrayList<>();
        minute.add("Every Minute");
        for(int x = 0;x<60;x++){
            minute.add(x + "");
        }

        List<String> hour = new ArrayList<>();
        hour.add("Every Hour");
        hour.add("12 Midnight");
        for(int x = 0;x<12;x++){
            hour.add((x+1) + " AM");
        }
        for(int x = 0;x<11;x++){
            hour.add((x+1) + " PM");
        }

        List<String> day = new ArrayList<>();
        day.add("Every Day");
        for(int x = 0;x<30;x++){
            day.add((x+1) + "");
        }

        List<String> month = new ArrayList<>();
        month.add("Every Month");
        month.add("January");
        month.add("February");
        month.add("March");
        month.add("April");
        month.add("May");
        month.add("June");
        month.add("July");
        month.add("August");
        month.add("September");
        month.add("October");
        month.add("November");
        month.add("December");

        List<String> weekday = new ArrayList<>();
        weekday.add("Every Weekday");
        weekday.add("Sunday");
        weekday.add("Monday");
        weekday.add("Tuesday");
        weekday.add("Wednesday");
        weekday.add("Thursday");
        weekday.add("Friday");
        weekday.add("Saturday");
        schema.addEnum("second",second,"0");
        schema.addEnum("minute",minute,"Every Minute");
        schema.addEnum("hour",hour,"Every Hour");
        schema.addEnum("day",day,"Every Day");
        schema.addEnum("month",month,"Every Month");
        schema.addEnum("weekday",weekday,"Every Weekday");

        return schema;
    }

    public static String getCronExpression(Map<String, Object> input) {
        Map<String, Object> defaults = TimerTrigger.builder().build().getJsonSchema().getValue();
        defaults.putAll(input);
        String second = defaults.get("second").toString();
        String minute = defaults.get("minute").toString();
        String hour = defaults.get("hour").toString();
        String day = defaults.get("day").toString();
        String month = defaults.get("month").toString();
        String weekday = defaults.get("weekday").toString();

        if(second != null){
            if(second.equalsIgnoreCase("Every Second")){
                second = "*";
            } else{
                if(!second.equalsIgnoreCase("0")){
                    second = "0/" + second;
                }
            }
        }
        if(minute != null){
            if(minute.equalsIgnoreCase("Every Minute")){
                minute = "*";
            }
        }
        if(hour != null){
            if(hour.equalsIgnoreCase("Every Hour")){
                hour = "*";
            }else if(hour.contains(" AM")){
                hour = hour.replace(" AM","");
            }else if(hour.contains(" PM")){
                Integer hourInt = Integer.parseInt(hour.replace(" PM","")) +12;
                hour = hourInt.toString();
            }
        }
        if(day != null){
            if(day.equalsIgnoreCase("Every Day")){
                day = "*";
            }
        }
        if(month != null){
            if(month.equalsIgnoreCase("Every Month")){
                month = "*";
            }
        }

        String cronExpression =  second + " " + minute + " " + hour + " " + day + " " + month + " *";

        return cronExpression;
    }
}
