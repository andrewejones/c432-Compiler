int factorial(int num);
void print(int num, int fact);

void main() {
	int num, fact;
	num = 8;
	fact = factorial(num);
	print(num, fact);
}

int factorial(int num) {
	int fact, counter;
	fact = 1;
	while (num > 1) {
		fact = fact * num;
		num = num - 1;
	}
	return fact;
}

void print(int num, int fact) {
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