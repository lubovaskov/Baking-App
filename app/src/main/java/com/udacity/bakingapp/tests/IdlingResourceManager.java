package com.udacity.bakingapp.tests;

//interface for managing idling resources needed for tests
public interface IdlingResourceManager {
    void setBusyIdlingResource();
    void setIdleIdlingResource();
}
