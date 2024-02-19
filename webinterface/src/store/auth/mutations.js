/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */
import {SET_AUTHENTICATION, SET_USERNAME} from "@/store/storeconstants.js";

export default {
    [SET_AUTHENTICATION](state, authenticated) {
        state.authenticated = authenticated
    },
    [SET_USERNAME](state, username) {
        state.username = username;
    }
}