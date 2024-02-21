/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import {GET_CURRENTVIEW} from "@/store/storeconstants.js";

export default {
    [GET_CURRENTVIEW](state) {
        return state.currentView;
    }

}