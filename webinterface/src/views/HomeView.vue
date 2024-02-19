<!--
  - Copyright (c) SimonPlays15 2024. All Rights Reserved
  -->

<template>
  <div class="container">
    <div class="container py-5 h-100">
      <div class="row d-flex justify-content-center align-items-center h-100">
        <div class="col-12 col-md-8 col-lg-6 col-xl-5">
          <div class="card" style="border-radius: 1rem;">
            <div class="card-body p-5 text-center">
              <div class="mb-md-5 mt-md-4 pb-5">
                <h2 class="fw-bold mb-2 text-uppercase">Login</h2>
                <p class="text-white-50 mb-5">Please enter your username and password to login</p>
                <form>
                  <div class="col">
                    <label class="form-label" for="username">Username</label>
                    <input id="username" v-model="input.username" class="form-control"
                           placeholder="Username"
                           required type="text">
                  </div>
                  <div class="col">
                    <label class="form-label" for="password">Password</label>
                    <input id="password" v-model="input.password" class="form-control"
                           placeholder="Password"
                           required type="password">
                  </div>
                  <div class="col">
                    <button class="btn btn-outline-info btn-lg" type="button" @click="login">Login</button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

import {GET_USERNAME, SET_AUTHENTICATION, SET_USERNAME} from "@/store/storeconstants.js";
import {addAlert} from "@/assets/js/globalmethods.js"

export default {
  data() {
    return {
      input: {
        username: "",
        password: ""
      },
      output: ""
    }
  },
  methods: {
    getUsername() {
      return this.$store.getters[`auth/${GET_USERNAME}`];
    },
    login() {
      if (this.input.username !== "" || this.input.password !== "") {
        this.$store.commit(`auth/${SET_AUTHENTICATION}`, true);
        this.$store.commit(`auth/${SET_USERNAME}`, this.input.username);
        this.output = `Authentication complete. Welcome ${this.getUsername()}`;
      } else {
        this.$store.commit(`auth/${SET_AUTHENTICATION}`, false);
        addAlert("danger", "Failed to login", "Password or Username is wrong please check your credentials")
      }
    }
  }
}
</script>
