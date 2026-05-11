package com.inclusive.bdd.abilities;

import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

public class CallBackendApi {

    public static Ability at(String baseUrl) {
        return CallAnApi.at(baseUrl);
    }
}
