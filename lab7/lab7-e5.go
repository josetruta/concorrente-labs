package main

import (
	"fmt"
	"math/rand/v2"
)

func genRandNumb(out chan<- int) {
	for {
		out <- rand.IntN(100)
	}
}

func readNumb(in <-chan int, idx int) {
	var num int = 0
	for {
		num = <-in
		if isGreaterThan50(num) {
			fmt.Printf("Consumidor %d recebeu %d\n", idx, num)
		}
	}
}

func isGreaterThan50(num int) bool {
	return num > 50
}

func main() {
	num := make(chan int, 100)

	go genRandNumb(num)
	go genRandNumb(num)
	for i := 0; i < 5; i++ {
		go readNumb(num, i)
	}

	ch := make(chan int)
	<-ch
}