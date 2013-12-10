void negativeerror();
void printfactorial(int num, int fact);

int factorial(int num) {
	int fact;
	int counter;
	fact = 1;
	while (num > 1) {
		fact = fact * num;
		num = num - 1;
	}
	return fact;
	writeln("Should never get here...");
}

int main() {
	int num, fact;
	num = 7;
	fact = factorial(num);
	if (num >= 0) {
		printfactorial(num, fact)
	} else
		negativeerror();
}

void negativeerror() {
	write("Error: only non-negative numbers allowed.");
}

void printfactorial(int num, int fact) {
	write("Factorial of ");
	write(num);
	write(": ");
	write(fact);
}