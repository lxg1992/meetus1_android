package com.example.yun.meetup.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 12/10/2017.
 */

public class EventList {

    public List<Event> eventList;
    public Event[] eventArray = new Event[10];

    public EventList(int i) {
        List<Event> eventList = new ArrayList<>();
        Event[] eventArray = new Event[i];
    }

    public static Event[] eventListToArray(List<Event> list) {
        Event[] eArray = (Event[]) list.toArray(new Event[list.size()]);
        return eArray;
    }

    public static List<Event> arrayToEventList(Event[] evArray) {
        List<Event> lstEvent = new ArrayList<Event>(Arrays.asList(evArray));
        return lstEvent;
    }


}
