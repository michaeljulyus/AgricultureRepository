/**
 * Created by Michael on 21/11/2017.
 */

package com.michael.apps.agriculturerepository.model;

public class Config {
    public static final String LOGIN_URL = "";
    public static final String REGISTER_URL = "";
    public static final String UPDATE_PROFILE_URL = "";
    public static final String UPDATE_PASSWORD_URL = "";
    public static final String RESET_URL = "";
    public static final String INFO_URL = "";
    public static final String GET_NAME_URL = "";
    public static final String GET_DATA_URL = "";

    //Keys for email and password as defined in our $_POST['key'] in login.php
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_DATE_OF_BIRTH = "date_of_birth";
    public static final String KEY_OLD_PASSWORD = "old_password";
    public static final String KEY_NEW_PASSWORD = "new_password";
    public static final String KEY_NAME = "name";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_IMAGE = "foto";

    //If server response is equal to this that means login is successful
    public static final String LOGIN_SUCCESS = "success";

    //Keys for Sharedpreferences
    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "myloginapp";

    //This would be used to store the username of current logged in user
    public static final String USERNAME_SHARED_PREF = "username";

    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";
}