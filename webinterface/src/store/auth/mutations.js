/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */
import {SET_AUTHENTICATION, SET_TOKEN, SET_USERID, SET_USERNAME} from "@/store/storeconstants.js";

export default {
    [SET_AUTHENTICATION](state, authenticated) {
        state.authenticated = authenticated
    },
    [SET_USERNAME](state, username) {
        state.username = username;
    },
    [SET_TOKEN](state, token) {
        state.token = token;
    },
    [SET_USERID](state, userid) {
        state.userid = userid;
    }
}