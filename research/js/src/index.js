/*
 * @Author: majianwen
 * @LastEditors: majianwen
 * @LastEditTime: 2021-10-08 15:18:51
 * @FilePath: /dnalogel/index.js
 * @Description: 文件描述
 */

var uri_string = "a/b/d";
var data = {"e":"e1","f":"f1"};
var ssd = {
  a: {
    b: {
      c: 'c1',
      d: ['d1', 'd2', 'd3'],
    },
  },
}

function transSSD(source, target, value) {
  if (!source || !target) return false
  if (!value) return source
  let targetArr = target.split('/')

  if (source[targetArr[0]] && targetArr.length > 1) {
    let newtargetarr = JSON.parse(JSON.stringify(targetArr))
    newtargetarr.shift()
    //递归
    transSSD(source[targetArr[0]], newtargetarr.join('/'), value)
  } else if (!source[targetArr[0]] || targetArr.length == 1) {
    source[targetArr[0]] = value
  }
  return ssd
}

let res = transSSD(ssd, uri_string, data)
console.log('res', res)
