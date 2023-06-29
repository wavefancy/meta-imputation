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
          target: "http://39.107.228.251:18081",
          // target: "http://119.78.66.78:80",
          changOrigin:true,
          pathRewrite: { '^/dev-api': '' },
        },
        "/prd-api": {
          // target: "http://119.78.66.78:80",
          target: "http://39.107.228.251:18081",
          changOrigin:true,
          pathRewrite: { '^/prd-api': '' },
        },
        "/dev-job-api": {
          target: "http://localhost:9080",
          changOrigin:true,
          pathRewrite: { '^/dev-job-api': '' },
        },
        "/prd-job-api": {
          target: "http://39.107.228.251:19080",
          changOrigin:true,
          pathRewrite: { '^/prd-job-api': '' },
        },
      }
    }
}


