<!--
  - Copyright (c) SimonPlays15 2024. All Rights Reserved
  -->

<template>
  <div class="container">
    <div class="py-5 h-100">
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
                  <div class="col mt-3 w-100">
                    <button :disabled="!validInputs" class="btn btn-outline-success btn-lg w-100" type="button"
                            @click="login">Login
                    </button>
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

import {GET_USERNAME, SET_AUTHENTICATION} from "@/store/storeconstants.js";
import {addAlert} from "@/assets/js/globalmethods.js"
import axios from "axios";

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
  computed: {
    validInputs() {
      return this.input.username && this.input.password
    }
  },
  methods: {
    getUsername() {
      return this.$store.getters[`auth/${GET_USERNAME}`];
    },
    login() {
      if (this.input.username !== "" || this.input.password !== "") {
        const postCall = axios.get("http://localhost:8080/api", {
          headers: {
            "Content-Type": "application/json"
          },
          auth: {
            username: this.input.username,
            password: this.input.password
          }
        }).catch(error => {
          console.log("Error", error)
        }).then(result => {
          console.log("Result", result)
        }).finally(() => {
          console.log("Finally:", postCall);
        })
      } else {
        this.$store.commit(`auth/${SET_AUTHENTICATION}`, false);
        addAlert("danger", "Failed to login", "Username and password field cannot be empty.")
      }
    }
  }
}
</script>
