package com.example.yun.meetup.managers;

import android.content.SharedPreferences;

import com.example.yun.meetup.models.APIResult;
import com.example.yun.meetup.models.Event;
import com.example.yun.meetup.models.EventList;
import com.example.yun.meetup.models.UserInfo;
import com.example.yun.meetup.providers.ApiProvider;
import com.example.yun.meetup.requests.CreateEventRequest;
import com.example.yun.meetup.requests.EventListRequest;
import com.example.yun.meetup.requests.LoginRequest;
import com.example.yun.meetup.requests.RegistrationRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class NetworkManager {

    private static final String GOOGLE_API_GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String GOOGLE_API_KEY = "AIzaSyAcujkeJzWDW0S31u_tCf2o9B3K0e15Z-U";
    private static ApiProvider apiProvider = new ApiProvider();

    public APIResult login(LoginRequest loginRequest) {

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson .toJson(loginRequest);

        APIResult apiResult = new APIResult(false, "Login failed: please try again", null);

        try{

            String response = apiProvider.sendRequest("/user/login", "POST", json);

            JSONObject responseJSON = new JSONObject(response);
            if (!responseJSON.isNull("data")) {

                UserInfo userInfo = gson.fromJson(responseJSON.getJSONObject("data").toString(), UserInfo.class);

                apiResult = new APIResult(true, APIResult.RESULT_SUCCESS, userInfo);
            }
            else if (!responseJSON.isNull("err") && responseJSON.getString("err").equals("User not found")){
                apiResult = new APIResult(false, "Wrong email/password. Please try again", null);
            }
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    public APIResult register(RegistrationRequest registrationRequest) {

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson .toJson(registrationRequest);

        APIResult apiResult = new APIResult(false, "Registration failed: please try again", null);

        try {

            String response = apiProvider.sendRequest("/user/register", "POST", json);

            JSONObject responseJSON = new JSONObject(response);

            if (!responseJSON.isNull("data")) {

                UserInfo userInfo = gson.fromJson(responseJSON.getJSONObject("data").toString(), UserInfo.class);

                apiResult = new APIResult(true, APIResult.RESULT_SUCCESS, userInfo);

            }
            else if (!responseJSON.isNull("err") && responseJSON.getString("err").equals("Email is already used")){
                apiResult = new APIResult(false, "Email is already used: please try again", null);
            }
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    public APIResult validateEventAddress(CreateEventRequest createEventRequest) {

        APIResult apiResult = new APIResult(false, "Failed converting address to location: please try again", null);

        try {
            String response = apiProvider.sendRequest(GOOGLE_API_GEOCODE_URL + "?key=" + GOOGLE_API_KEY + "&address=" + URLEncoder.encode(createEventRequest.getAddress(), "UTF-8"), "GET", null);

            JSONObject responseJSON = new JSONObject(response);

            if (!responseJSON.isNull("status")) {

                if (responseJSON.getString("status").equals("OK")) {
                    JSONObject result = responseJSON.getJSONArray("results").getJSONObject(0);
                    createEventRequest.setAddress(result.getString("formatted_address"));
                    createEventRequest.setLatitude(Float.parseFloat(result.getJSONObject("geometry").getJSONObject("location").getString("lat")));
                    createEventRequest.setLongitude(Float.parseFloat(result.getJSONObject("geometry").getJSONObject("location").getString("lng")));
                    apiResult = new APIResult(true, APIResult.RESULT_SUCCESS, createEventRequest);
                }
                else if (responseJSON.getString("status").equals("ZERO_RESULTS")) {
                    apiResult = new APIResult(false, "No address location found. Please enter a valid address!", null);
                }

            }
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    public APIResult createEvent(CreateEventRequest createEventRequest) {

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson .toJson(createEventRequest);

        try {

            String response = apiProvider.sendRequest("/event", "POST", json);

            JSONObject responseJSON = new JSONObject(response);

            if (!responseJSON.isNull("data")) {

                Event event = gson.fromJson(responseJSON.getJSONObject("data").toString(), Event.class);

                return new APIResult(true, APIResult.RESULT_SUCCESS, event);

            }
            else if (!responseJSON.isNull("err")) {
                return new APIResult(false, responseJSON.getString("err"), null);
            }
        }
        catch (JSONException e) {
            return new APIResult(false, e.getMessage(), null);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return new APIResult(false, "Fatal error, please contact the admin staff!", null);
    }

    public APIResult listEvents(EventListRequest eventListRequest) {

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(eventListRequest);
        ArrayList<Event> listOfEvents = new ArrayList<Event>();
        Event ev = null;
        UserInfo ui = new UserInfo();
        ui.setName("Test");


        APIResult apiResult = new APIResult(false, "Listing of Events failed: Please try again", null);

        try {

            String response = apiProvider.sendRequest("/host_event", "POST", json);


            JSONObject responseJSON = new JSONObject(response);


            if (!responseJSON.isNull("data")) {

                JSONArray responseJSONArray = responseJSON.optJSONArray("data");

                //
                //listOfEvents = gson.fromJson(responseJSONArray.toString(),new TypeToken<List<Event>>(){}.getType());

                //BELOW METHOD FROM: http://techlovejump.com/android-poplulating-list-view-from-json/
                for (int i = 0; i < responseJSONArray.length(); i++) {
                    JSONObject jsonEventObject = responseJSONArray.getJSONObject(i);
                    String _id = jsonEventObject.optString("_id");
                    String host_id = jsonEventObject.optString("host_id");
                    String title = jsonEventObject.optString("title");
                    String address = jsonEventObject.optString("address", "default");
                    String date = jsonEventObject.optString("date", "01/01/2020");


                    ev = new Event();
                    ev.set_id(_id);
                    ev.setHost_id(host_id);
                    ev.setTitle(title);
                    ev.setUserInfo(ui);
                    ev.setAddress(address);


                    listOfEvents.add(ev);


                }


                // UserInfo userInfo = gson.fromJson(responseJSON.getJSONObject("data").toString(), UserInfo.class);

                apiResult = new APIResult(true, APIResult.RESULT_SUCCESS, listOfEvents);
            } else if (!responseJSON.isNull("err") && responseJSON.getString("err").equals("Data not found in database")) {
                apiResult = new APIResult(false, "There are no events from this host ID", null);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return apiResult;
    }


}
