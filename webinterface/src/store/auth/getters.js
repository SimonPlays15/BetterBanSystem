/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import {GET_USERNAME, IS_USER_AUTHENTICATED} from "../storeconstants";

export default {
    [IS_USER_AUTHENTICATED](state) {
        return state.authenticated;
    },

    [GET_USERNAME](state) {
        return state.username;
    }
}