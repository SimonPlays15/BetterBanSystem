/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */


import mutations from './mutations';
import getters from './getters';

export default {
    namespaced: true,
    state() {
        return {
            currentView: "DashboardComponent"
        }
    },
    mutations,
    getters
}