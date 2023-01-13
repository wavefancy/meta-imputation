module.exports = {
    pages: {
      index: {
        //入口
        entry: 'src/main.js',
      },
    },
    lintOnSave:false, //关闭语法检查
    //开启代理服务器
    devServer: {
      proxy: {
        "/dev-api": {
          target: "http://123.56.217.25:9090",
          // target: "http://39.103.140.193:9090",
          changOrigin:true,
          pathRewrite: { '^/dev-api': '' },
        },
        "/prd-api": {
          // target: "http://123.56.217.25:9090",
          target: "http://39.103.140.193:9090",
          changOrigin:true,
          pathRewrite: { '^/prd-api': '' },
        },
      }
    }
}


