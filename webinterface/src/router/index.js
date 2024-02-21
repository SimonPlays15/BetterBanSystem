/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import {createRouter, createWebHistory} from 'vue-router'
import HomeView from '../views/HomeView.vue'
import ErrorPage from '../views/404.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: HomeView
        },
        {
            path: '/dashboard',
            name: 'dashboard',
            component: () => import('../views/DashboardView.vue'),
        },
        {
            path: "/:patchMatch(.*)*",
            name: "not-found",
            component: ErrorPage
        }
    ]
})
export default router
