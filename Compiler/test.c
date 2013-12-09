int factorial(int num);
void negative();
void printfactorial(int num, int fact);

void main() {
	int num, fact;
	num = 5;
	fact = factorial(num);
	if (num >= 0) {
		printfactorial(num, fact);
	} else {
		negative();
	}
}

int factorial(int num) {
	int fact, counter;
	fact = 1;
	while (num > 1) {
		fact = fact * num;
		num = num - 1;
	}
	return fact;
	writeln(999999);
}

void negative() {
	writeln('');
	write('E');
	write('r');
	write('r');
	write('o');
	write('r');
	writeln('!');
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