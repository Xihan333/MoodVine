
export default {
  logger: {
    quiet: false,
    stats: true
  },
  mini: {},
  h5: {},
  compiler: {
    type: 'webpack5',
    prebundle: {
      enable: false,
      force: true,
    },
  },
  cache: {
    enable: false // Webpack 持久化缓存配置，建议开启。默认配置请参考：https://docs.taro.zone/docs/config-detail#cache
  },
}
