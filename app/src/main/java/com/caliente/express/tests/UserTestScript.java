package com.caliente.express.tests;

import org.junit.FixMethodOrder;
import org.junit.Test;

import com.caliente.express.api.ApiService;
import com.caliente.express.tests.testsupport.TestLogic;

import org.junit.runners.MethodSorters;

/**
 * Created by John R. Kosinski on 27/1/2559.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserTestScript {

    @Test
    public void a_signUp()
    {
        ApiService.configure("http://192.168.1.36:31716/api/v/1/");

        TestLogic.signUp();
    }

    @Test
    public void b_logOut()
    {
        TestLogic.logOut();
    }

    @Test
    public void c_logIn()
    {
        TestLogic.logIn();
    }
}
