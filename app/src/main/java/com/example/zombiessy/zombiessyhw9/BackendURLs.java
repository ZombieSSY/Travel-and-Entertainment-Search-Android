package com.example.zombiessy.zombiessyhw9;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class BackendURLs {
    static final String INPUTLOCATION = "http://zombiessyhw9-env.us-east-2.elasticbeanstalk.com/currLoc";
    static final String FIRSTPAGE = "http://zombiessyhw9-env.us-east-2.elasticbeanstalk.com/firstPage";
    static final String NEXTPAGE = "http://zombiessyhw9-env.us-east-2.elasticbeanstalk.com/nextPage";
    static final String PLACEDETAIL = "http://zombiessyhw9-env.us-east-2.elasticbeanstalk.com/placeDetails";
    static final String YELPMATCH = "http://zombiessyhw9-env.us-east-2.elasticbeanstalk.com/yelpMatch";
    static final String YELPREVIEW = "http://zombiessyhw9-env.us-east-2.elasticbeanstalk.com/yelpReviews";
    static final String TWITTER = "https://twitter.com/intent/tweet";
    static final String CURRLOC = "http://ip-api.com/json";
    static final String ROUTESURL = "https://maps.googleapis.com/maps/api/directions/json?";
    static final LatLngBounds BOUNDS = new LatLngBounds(new LatLng(24.7433195, -124.7844079), new LatLng(49.3457868, -66.9513812));
    static final String GOOGLE_API_KEY = "AIzaSyAv2K9VkXMRt6_aVjg5mLobkQ4bJ65gjN8";
}
