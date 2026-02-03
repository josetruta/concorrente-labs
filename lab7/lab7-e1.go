package main

import (
	"fmt"
	"math/rand/v2"
)

func genRandNumb(out chan int) {
	for {
		out <- rand.IntN(100)
	}
}

func readNumb(in chan int) {
	var num int = 0
	for {
		num = <-in
		if isGreaterThan50(num) {
			fmt.Println(num)
		}
	}
}

func isGreaterThan50(num int) bool {
	return num > 50
}

func main() {
	num := make(chan int)

	go genRandNumb(num)
	go readNumb(num)

	ch := make(chan int)
	<-ch
}