/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */
import '@/assets/main.css'
import "@/assets/css/fontawesome-all.min.css"


import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import {createStore} from "vuex";
import auth from "@/store/auth/index.js";
import navigation from "@/store/navigation/index.js";
import {IS_USER_AUTHENTICATED} from "@/store/storeconstants.js";

const app = createApp(App)
const store = createStore({
    state: {},
    getters: {},
    mutations: {},
    actions: {},
    modules: {
        auth,
        navigation
    }
});

app.config.errorHandler = ((error, instance, info) => {
    console.log("Global error:", error);
    console.log("Vue instance:", instance);
    console.log("Error info:", info);
})

router.beforeEach(async (to, from) => {
    if (!store.getters[`auth/${IS_USER_AUTHENTICATED}`] && to.name !== "home") {
        // TODO uncomment
        // return {name: 'home'}
    }
})

app.use(store);
app.use(router);
app.mount('#app')
