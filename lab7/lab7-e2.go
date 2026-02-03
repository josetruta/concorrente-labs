package main

import (
	"fmt"
	"math/rand/v2"
)

func genRandNumb(out chan int) {
	for i := 0; i < 10000; i++ {
		out <- rand.IntN(100)
	}
	close(out)
}

func readNumb(in chan int, ch chan int) {
	for num := range in {
		if isGreaterThan50(num) {
			fmt.Println(num)
		}
	}
	close(ch)
}

func isGreaterThan50(num int) bool {
	return num > 50
}

func main() {
	num := make(chan int)
	ch := make(chan int)

	go genRandNumb(num)
	go readNumb(num, ch)

	<-ch
}