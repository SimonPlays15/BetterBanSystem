/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import {SET_CURRENTVIEW} from "@/store/storeconstants.js";

export default {
    [SET_CURRENTVIEW](state, view) {
        state.currentView = view;
    }
}