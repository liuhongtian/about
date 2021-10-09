package main

import (
	"fmt"
	"io/ioutil"

	"github.com/tidwall/sjson"
)

// "errors"
func main() {

	ssdBytes, err := ioutil.ReadFile("ssd.json")
	if err != nil {
		// fmt.Println(err)
		return
	}
	//fmt.Println(ssdString)

	// value := gjson.GetBytes(ssdBytes, "a.b.e.2")
	// if err != nil {
	// 	// fmt.Println(err)
	// 	return
	// }
	// //fmt.Println(reflect.TypeOf(value))
	// fmt.Println(value)

	dataBytes, err := ioutil.ReadFile("data.json")
	if err != nil {
		// fmt.Println(err)
		return
	}

	ssdBytesNew, err := sjson.SetRawBytes(ssdBytes, "a.b.d.1", dataBytes)
	if err != nil {
		// fmt.Println(err)
		return
	}
	fmt.Println(string(ssdBytesNew))
}
