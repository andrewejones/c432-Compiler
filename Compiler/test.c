void negativeerror();
void printfactorial(int num, int fact);

int factorial(int num) {
	int fact;
	int counter;
	fact = 1;
	while (num > 1) {
		fact = fact * num;
		num = num - 1
	}
	return fact;
	writeln(999999);
}

void main() {
	int num, fact;
	num = 7;
	fact = factorial(num);
	if (num >= 0) {
		printfactorial(num, fact)
	} else
		negativeerror();
}

void negativeerror() {
	writeln('');
	write('N');
	write('e');
	write('g');
	write('a');
	write('t');
	write('i');
	write('v');
	write('e');
	write(' ');
	write('E');
	write('r');
	write('r');
	write('o');
	write('r');
	write('!');
	return;
	writeln('!');
}

void printfactorial(int num, int fact) {
	writeln('');
	write('F');
	write('a');
	write('c');
	write('t');
	write('o');
	write('r');
	write('i');
	write('a');
	write('l');
	write(' ');
	write('o');
	write('f');
	write(' ');
	write(num);
	write(':');
	write(' ');
	writeln(fact);
}