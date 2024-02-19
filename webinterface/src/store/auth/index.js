/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */


import mutations from './mutations';
import getters from './getters';

export default {
    namespaced: true,
    state() {
        return {
            authenticated: false,
            username: ""
        }
    },
    mutations,
    getters
}