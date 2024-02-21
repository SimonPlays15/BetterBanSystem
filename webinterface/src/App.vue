<!--
  - Copyright (c) SimonPlays15 2024. All Rights Reserved
  -->

<script setup>
import {GET_CURRENTVIEW, IS_USER_AUTHENTICATED} from "@/store/storeconstants.js";
import {RouterView} from 'vue-router';
import DashboardComponent from "@/components/DashboardComponent.vue";
import ConsoleComponent from "@/components/ConsoleComponent.vue";
import router from "@/router/index.js";

console.log(router.currentRoute)
</script>

<script>
import {GET_CURRENTVIEW, SET_CURRENTVIEW} from "@/store/storeconstants.js";

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
  }
}
</script>

<template>
  <div v-if="!$store.getters[`auth/${IS_USER_AUTHENTICATED}`]">
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
              <a :class="isActiveLink('ConsoleComponent')"
                 :href="router.currentRoute.value.name === 'dashboard' ? '#' : '/dashboard'" class="nav-link"
                 @click="changeView('ConsoleComponent')">Console</a>
            </li>
          </ul>
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