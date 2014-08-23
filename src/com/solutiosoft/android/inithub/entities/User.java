/**
 *   This file is part of InitHub-Android.
 *
 *   InitHub-Android is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   InitHub-Android is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with InitHub-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.solutiosoft.android.inithub.entities;

public class User {
	 
    //private variables
    private int _id;
    private String _email;
    private String _api_key;
 
    // Empty constructor
    public User(){
 
    }
    // constructor
    public User(int id, String email, String api_key){
        this._id = id;
        this._email = email;
        this._api_key = api_key;
    }
 
    // constructor
    public User(String email, String api_key){
        this._email = email;
        this._api_key = api_key;
    }
    // getting ID
    public int getID(){
        return this._id;
    }
 
    // setting id
    public void setID(int id){
        this._id = id;
    }
 
    // getting name
    public String getEmail(){
        return this._email;
    }
 
    // setting name
    public void setEmail(String email){
        this._email = email;
    }
 
    // getting phone number
    public String getApiKey(){
        return this._api_key;
    }
 
    // setting phone number
    public void setApiKey(String api_key){
        this._api_key = api_key;
    }
}