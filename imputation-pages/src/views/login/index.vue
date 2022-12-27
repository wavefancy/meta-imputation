<template>
<b-container class="bv-example-row">
  <div class="container page mt p-5" style=""><h2>Sign in</h2>
    <br>
    <div class="form-group">
        <label for="loginUsername" class="control-label">Username:</label>
        <input id="loginUsername" name="loginUsername" type="text" class="form-control col-sm-3" v-model="form.username" ref="username" >
    </div>

    <div class="form-group">
        <label for="loginPassword" class="control-label">Password:</label>
        <input id="loginPassword" name="loginPassword" type="password" class="form-control col-sm-3" v-model="form.password" ref="password">
        <div class="invalid-feedback">
        This parameter is required.
        </div>
    </div>
    <div class="form-group">
        <button class="btn  btn-primary"   @click="signIn">Sign in</button>
    </div>
    <br>
    <p>New user? <router-link  to="/register">Sign up for free</router-link></p>
    </div>
</b-container>
</template>

<script>
import  {user} from '@/api'
import { isEmpty }  from "@/utils/validate"
import doCookie from "@/utils/cookie"
export default {
    name:'login',
    data () {
        return {
            form: {
                username:'',
                password:''
            },
            box:""
        }
    },
    methods: {
        signIn(){
            let username = this.form.username
            if(isEmpty(username)){
                this.showMsgBoxTwo( "Please enter Username!")
                this.$refs.username.focus()
                return;
            }
            if(isEmpty(this.form.password)){
                this.showMsgBoxTwo( "Please enter Password!")
                this.$refs.password.focus()
                return;
            }
            let subData ={
                username:username,
                password:this.form.password
            }
            user.login(subData).then((response) => {
                let code = response.code
                if(code === "0" || code === 0){
                    //修改登录状态
                    this.$bus.$emit('setLoginVal',true,username)
                    let data = response.data
                    if(!isEmpty(data)){
                        doCookie.setCookie("imputation-cookie",data,1)
                        doCookie.setCookie("imputation-username",username,1)
                    }
                    //跳转home页
                    this.$router.push({
                        name:'home'
                    });
                }
            })
        },
        showMsgBoxTwo(msg) {
            this.box = ''
            this.$bvModal.msgBoxOk(msg, {
                size: 'sm',
                buttonSize: 'sm',
                okVariant: 'success',
                headerClass: 'p-2 border-bottom-0',
                footerClass: 'p-2 border-top-0',
                centered: true
            })
            .then(value => {
                this.box = value
            })
            .catch(err => {
                console.error(err)
            })
        }
    }

}
</script>

<style>
.page {
    background: #fff;
    box-shadow: 0 2px 5px 0 rgb(0 0 0 / 16%), 0 2px 10px 0 rgb(0 0 0 / 12%);
    transition: box-shadow .25s;
    margin-top: 0;
    padding: 15px;
}
</style>