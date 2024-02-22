<!--
  - Copyright (c) SimonPlays15 2024. All Rights Reserved
  -->

<script setup>
import {GET_CURRENTVIEW, GET_USERNAME} from "@/store/storeconstants.js";
import {RouterView} from 'vue-router';
import DashboardComponent from "@/components/DashboardComponent.vue";
import ConsoleComponent from "@/components/ConsoleComponent.vue";
import router from "@/router/index.js";
import {logout} from "@/assets/js/globalmethods.js";

console.log(router.currentRoute)
</script>

<script>
import {
  GET_CURRENTVIEW,
  GET_USERNAME, IS_USER_AUTHENTICATED,
  SET_AUTHENTICATION,
  SET_CURRENTVIEW,
  SET_TOKEN,
  SET_USERNAME
} from "@/store/storeconstants.js";
import router from "@/router/index.js";

export default {
  methods: {
    changeView(view) {
      this.$store.commit(`navigation/${SET_CURRENTVIEW}`, view);
    },
    isActiveLink(view) {
      const currentView = this.$store.getters[`navigation/${GET_CURRENTVIEW}`];
      if (currentView === view) {
        return "nav-link active";
      }
      return "nav-link";
    }
  },
  computed: {
    isLoggedIn() {
      // TODO REMOVE COMMENT
      return true;
      //return this.$store.getters[`auth/${GET_USERNAME}`] && this.$store.getters[`auth/${IS_USER_AUTHENTICATED}`];
    }
  },
}
</script>

<template>
  <div v-if="isLoggedIn">
    <nav class="navbar navbar-expand-lg bg-body-tertiary">
      <div class="container-fluid">
        <a class="navbar-brand" href="#">BetterBanSystem</a>
        <button aria-controls="navbar" aria-expanded="false" aria-label="Toggle navigation" class="navbar-toggler"
                data-bs-target="#navbar"
                data-bs-toggle="collapse" type="button">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div id="navbar" class="collapse navbar-collapse">
          <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <li class="nav-item">
              <a :class="isActiveLink('DashboardComponent')"
                 :href="router.currentRoute.value.name === 'dashboard' ? '#' : '/dashboard'"
                 @click="changeView('DashboardComponent')">Dashboard</a>
            </li>
            <li class="nav-item">
              <a :class="isActiveLink('PlayerTableComponent')"
                 :href="router.currentRoute.value.name === 'dashboard' ? '#' : '/dashboard'"
                 @click="changeView('PlayerTableComponent')">Players</a>
            </li>
            <li class="nav-item">
              <a :class="isActiveLink('ConsoleComponent')"
                 :href="router.currentRoute.value.name === 'dashboard' ? '#' : '/dashboard'" class="nav-link"
                 @click="changeView('ConsoleComponent')">Console</a>
            </li>
          </ul>
          <div class="d-flex">
            <img :src="'https://mc-heads.net/avatar/'+$store.getters[`auth/${GET_USERNAME}`]+'/40'"
                 alt="" class="rounded">
            <span class="navbar-text" style="margin-right: 5px; margin-left: 5px;">{{
                $store.getters[`auth/${GET_USERNAME}`] ? $store.getters[`auth/${GET_USERNAME}`] : "UNDEFINED"
              }}</span>
            <button class="btn btn-outline-danger btn-sm" type="button" @click="logout($store)">Logout</button>
          </div>
        </div>
      </div>
    </nav>
  </div>
  <div class="container mt-3">
    <div class="container-fluid">
      <div id="alert-container">
        <!-- Alerts -->
      </div>
    </div>
    <RouterView/>
  </div>
</template>