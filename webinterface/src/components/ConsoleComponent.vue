<!--
  - Copyright (c) SimonPlays15 2024. All Rights Reserved
  -->

<script setup>

</script>

<template>
  <div class="row row-cols-1 mt-4">
    <div class="col">
        <textarea class="form-control form-control-lg bg-black text-white"
                  cols="100" disabled readonly rows="20"
                  style="font-size: 15px;font-family: 'Lucida Console',Arial, sans-serif">Loading content...</textarea>
    </div>
    <div class="col">
      <form>
        <div class="row row-cols-auto">
          <div class="col col-auto w-100">
            <div class="input-group">
              <input class="form-control" placeholder="/command..." type="text">
              <button class="input-group-text btn btn-outline-primary" type="button">Send</button>
            </div>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>

</style>