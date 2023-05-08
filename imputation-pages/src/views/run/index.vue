<template>
  <!-- Content -->
  <div class="flex-lg-1 h-screen overflow-y-lg-auto " style="margin-top: 6rem">
    <!-- Main -->
    <main class="py-6 bg-surface-secondary">
      <div class="container">
        <div class="form-group form-row">
          <div class="col-sm-2 col-form-label">
          </div>
          <button class="btn btn-primary" type="submit" data-loading-text="Uploading" @click="submit"><i class="far fa-play-circle"></i> Submit Job</button>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import {jobs} from "@/api"
import doCookie from "@/utils/cookie"
export default {
    name:"run",
    methods: {
      submit(){
        let subData = {
          jobName:"test",
          userName:doCookie.getCookie("imputation-username"),
          jobJson:""
        }
        jobs.submit(subData)
      }
    }
}
</script>

<style>

</style>