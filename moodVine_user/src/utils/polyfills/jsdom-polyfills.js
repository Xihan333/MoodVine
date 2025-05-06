// src/utils/polyfills/jsdom-polyfills.js
// 移除 node: 协议引用，改用 npm 包
const { TextDecoder, TextEncoder } = require("node-util") // 改为普通 util 包
const { ReadableStream } = require("web-streams-polyfill") // 新增依赖
const { Blob } = require("buffer") // 改为 buffer 包
const { File } = require("web-file-polyfill") // 新增依赖
const { fetch, Headers, FormData, Request, Response } = require("undici")

const applyPolyfills = () => {
  Object.defineProperties(globalThis, {
    TextDecoder: { value: TextDecoder },
    TextEncoder: { value: TextEncoder },
    ReadableStream: { value: ReadableStream },
    Blob: { value: Blob },
    File: { value: File },
    fetch: { value: fetch, writable: true },
    Headers: { value: Headers },
    FormData: { value: FormData },
    Request: { value: Request },
    Response: { value: Response }
  })
}

module.exports = applyPolyfills