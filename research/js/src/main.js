var uri_string = "a/b/d";
var data = {"e":"e1","f":"f1"};
var ssd = {
  a: {
    b: {
      c: "c1",
      d: ["d1", "d2", "d3"],
    },
  },
};

var value = ssd;
var uri = uri_string.split("/");
for (var i = 0, len = uri.length; i < len; i++) {
  if (uri[i].slice(-1) == ")") {
    key = uri[i].slice(0, uri[i].indexOf("("));
    index = uri[i].slice(uri[i].indexOf("(") + 1, -1);
    value = value[key][index];
  } else {
    key = uri[i];
    value = value[key];
  }
}

value = data

console.log(ssd);
