package it.uniroma1.touchrecorder.data;

import android.util.Log;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import it.uniroma1.touchrecorder.io.NamesManager;

/**
 * Created by Andrea on 25/01/22.
 */

public class ItemDataSpeed {


    public final String date;
    public SessionData session_data;

    public int item_index;
    public String item;

    /**
     modifications from ItemData
     */

    public List<TimedDouble> speed_array = new ArrayList<>();
    /**
     public List<TimedComponentFloatPoint> touch_down_points = new ArrayList<>();
    public List<TimedComponentFloatPoint> touch_up_points = new ArrayList<>();
    public List<TimedComponentFloatPoint> movement_points = new ArrayList<>();
    public List<FloatPoint> sampled_points;
    */

    public ItemDataSpeed(ItemData itemData) {
        this.session_data = itemData.session_data;
        this.item_index = itemData.item_index;
        this.date = itemData.date;
        this.item = itemData.item;
        /* transforming from coordinates list to speed list */
        int point_index, touch_up_index = 0;
        int touch_down_index = 1;
        long t_next_touch_up;
        long t_movement;

        TimedComponentFloatPoint prev_point = itemData.touch_down_points.get(0);

        long step_time = 0;
        double step_value = 0;
        addTravel (new TimedDouble(step_time,step_value));

        for (point_index = 1; point_index < itemData.movement_points.size(); point_index++) {
            t_next_touch_up = itemData.touch_up_points.get(touch_up_index).time;
            t_movement = itemData.movement_points.get(point_index).time;
            if(t_movement < t_next_touch_up){
                if(prev_point.time != itemData.movement_points.get(point_index).time) {
                    step_time = t_movement;
                    step_value = calculateSpeed(prev_point, itemData.movement_points.get(point_index));
                    prev_point = itemData.movement_points.get(point_index);
                    addTravel(new TimedDouble(step_time, step_value));
                }
            }
            else{
                touch_up_index++;
                if(touch_down_index < itemData.touch_down_points.size()) {
                    /* if it isn't the last point on touch_up set speed to zero untill the next touch_down */

                    step_time = t_next_touch_up;
                    step_value = 0;
                    addTravel (new TimedDouble(step_time,step_value));
                    step_time = itemData.touch_down_points.get(touch_down_index).time;
                    step_value = 0;
                    addTravel (new TimedDouble(step_time,step_value));

                    //point_index--; /* i DONT need to check again this index otherwise i would skip it */
                    prev_point = itemData.touch_down_points.get(touch_down_index);
                    touch_down_index++;
                }
                else{
                    /* if its the last point it doesnt set speed to zero */
                }
            }
            //Log.d("test_single", String.valueOf(step));
        }
        //Log.d("test_full", String.valueOf(this.speed_array));
    }

    public double calculateSpeed(TimedComponentFloatPoint start, TimedComponentFloatPoint end) {
        /* the speed will be in pixels/milliseconds */
        double dist = Math.abs(Math.sqrt((end.y - start.y) * (end.y - start.y) + (end.y - start.x) * (end.y - start.x)));
        double speed = dist/(end.time - start.time);
        return speed;
    }

    public void addTravel(TimedDouble travel) {
        speed_array.add(travel);
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

}