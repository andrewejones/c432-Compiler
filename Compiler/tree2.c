void print (char printChar);

int main () {
    char star, blank;
    int printBlank, printStar, tempPrint, numLines;
    star = '*';
    blank = ' ';
    numLines = 9;
    
    while (numLines != 0) {
        
        if (numLines >3) {            
            tempPrint = printBlank;
            while (tempPrint != 0) {
                print(blank);
                tempPrint = tempPrint-1;
            }
            
            tempPrint = printStar;
            while (tempPrint != 0) {
                print(star);
                tempPrint = tempPrint-1;
            }
        }
        else {
            printBlank = 4;
            printStar = 3;
            while (printBlank != 0) {
                print(blank);
                printBlank = printBlank -1;
            }
            while (printStar != 0) {
                print(star);
                printStar = printStar -1;
            }
        }
        
        writeln("");
        numLines = numLines -1;
        printBlank = printBlank -1;
        printStar = printStar + 2;
            
}

void print (char printChar) {
    write(printChar);
}