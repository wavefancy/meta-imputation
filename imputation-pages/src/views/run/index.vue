<template>
  <!-- Content -->
  <div class="flex-lg-1 h-screen overflow-y-lg-auto " style="margin-top:6rem">
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
    methods:{
      submit(){
        let paramsJson = JSON.stringify(
          {
            "Imputation.multi_sample_vcf": "/disk/project/imputation/benchmark/2.1kg_extract/2.subset_as_gsaID_40sample_chrall_delchr.vcf.gz",
            "Imputation.multi_sample_vcf_index": "/disk/project/imputation/benchmark/2.1kg_extract/2.subset_as_gsaID_40sample_chrall_delchr.vcf.gz.tbi",
            "Imputation.ref_dict": "/disk/project/imputation/reference/imputation_Homo_sapiens_assembly38.dict",
            "Imputation.reference_panel_path": "/disk/project/imputation/reference/broad_reference/HG38/sort/",
            "Imputation.genetic_maps_eagle": "/data/reference/imputation/genetic_map_hg38_withX.txt.gz",
            "Imputation.output_callset_name": "scientific_test",
            "Imputation.split_output_to_single_sample": true,
            "Imputation.perform_extra_qc_steps": false,
            "Imputation.optional_qc_max_missing": 0.05,
            "Imputation.optional_qc_hwe": 0.000001
          }
        )
        let subData = {
          jobName:"test",
          userName:doCookie.getCookie("imputation-username"),
          jobJson:paramsJson,
        }
        jobs.submit(subData)
      }
    }
}
</script>

<style>

</style>