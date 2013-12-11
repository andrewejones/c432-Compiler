void print(int num, char ch);

void main() {
	int stars, spaces, counter;
	
	spaces = 5;
	counter = spaces;
	stars = 1;
	
	writeln("Happy Holidays!\n");
	while (counter > 0) {
		print(counter, ' ');
		print(stars, '*');
		writeln('');
		counter = counter - 1;
		stars = stars + 2;
	}
	if (spaces < 6) {
		print(spaces, ' ');
		writeln("*");
		print(spaces, ' ');
		writeln("*");
	} else {
		print(spaces-1, ' ');
		writeln("***");
		print(spaces-1, ' ');
		writeln("***");
	}
}

void print(int num, char ch) {
	while (num > 0) {
		write(ch);
		num = num - 1;
	}
}