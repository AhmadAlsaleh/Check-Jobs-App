package com.crazy_iter.checkjobs.StaticsAndAPIs;

/**
 * Created by CrazyITer on 3/23/2018.
 */

public interface API_URLs {

    String MAIN_URL =
            "http://checkjobs.navarastore.com/api/";
//            "http://localhost:27513/api/";

    String MAIN_IMAGES = "http://checkjobs.navarastore.com/images/";

    String USER_URL = MAIN_URL + "user/";
    String PLACE_URL = MAIN_URL + "places/";
    String SPOT_URL = MAIN_URL + "Spots/";
    String REPORT_URL = MAIN_URL + "Report/";
    String SEND_REPORT = REPORT_URL + "AddReport/";

    String SIGN_UP = USER_URL + "RegisterUser/";
    String SIGN_IN = USER_URL + "loginUser/";
    String EDIT_INFO = USER_URL + "editUserInfo/";
    String EDIT_PASSWORD = USER_URL + "editUserPassword/";
    String SIGN_IN_FACEBOOK = USER_URL + "LoginWithFacebook/";
    String UPGRADE_TO_PRO = USER_URL + "UpgradeToPro/";
    String EDIT_IMAGE = USER_URL + "editUserImage/";

    String DELETE_PLACE = PLACE_URL + "deletePlace/";
    String ADD_PLACE = PLACE_URL + "addPlace/";
    String EDIT_PLACE = PLACE_URL + "editPlace/";
    String GET_MY_ALL_PLACES = PLACE_URL + "GetMyAllPlaces/";
    String GET_MY_MAP_PLACES = PLACE_URL + "GetMyMapPlaces/";
    String GET_MY_STARRED_PLACES = PLACE_URL + "GetMyStarredPlaces/";
    String GET_ALL_PLACES = PLACE_URL + "GetAllPlaces";

    String GET_SPOTS_BY_PLACE = SPOT_URL + "getSpotsByPlace/";
    String GET_MY_SPOTS = SPOT_URL + "GetMySpots/";
    String ADD_SPOT = SPOT_URL + "addSpot/";
    String EDIT_SPOT = SPOT_URL + "EditSpot/";
    String DELETE_SPOT = SPOT_URL + "deleteSpot/";
    String DONE_SPOT = SPOT_URL + "ToggleDoneSpot/";
    String TOGGLE_STARED_SPOT = SPOT_URL + "ToggleStaredSpot/";
    String GET_FILTER_PLACES = PLACE_URL + "GetFilterredPlaces/";

    String mapAPI = "http://maps.googleapis.com/maps/api/geocode/json?latlng=";
}
