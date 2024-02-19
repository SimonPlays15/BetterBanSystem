/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import './assets/main.css'

import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import {createStore} from "vuex";
import auth from "@/store/auth/index.js";
import {IS_USER_AUTHENTICATED} from "@/store/storeconstants.js";

const app = createApp(App)
const store = createStore({
    state: {},
    getters: {},
    mutations: {},
    actions: {},
    modules: {
        auth
    }
});

router.beforeEach(async (to, from) => {
    if (!store.getters[`auth/${IS_USER_AUTHENTICATED}`] && to.name !== "home") {
        return {name: 'home'}
    }
})

app.use(store);
app.use(router);
app.mount('#app')

