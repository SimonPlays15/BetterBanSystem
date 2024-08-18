<!--
  - Copyright (c) SimonPlays15 2024. All Rights Reserved
  -->

<script>
import router from "@/router/index.js";
import {defineComponent} from "vue";

export default defineComponent({
  methods: {
    router() {
      return router
    }
  }
})

</script>

<template>
  <div class="text-center">
    <h1><span class="text-danger">404</span> not found</h1>
    <h3>It seems you're in the wrong page.</h3>
    <button class="btn btn-outline-primary btn-lg w-50" type="button" @click="router().back()">Go back</button>
  </div>
</template>