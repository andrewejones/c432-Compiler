.class public test
.super java/lang/Object

.field private static _runTimer LRunTimer;
.field private static _standardIn LPascalTextIn;


.method public <init>()V

	aload_0
	invokenonvirtual	java/lang/Object/<init>()V
	return

.limit locals 1
.limit stack 1
.end method

.method private static factorial(I)I

.var 2 is counter I
.var 1 is fact I
.var 0 is num I
.var 3 is factorial I


.line 13
	iconst_1
	istore_1
.line 14
L001:
	iload_0
	iconst_1
	if_icmpgt	L003
	iconst_0
	goto	L004
L003:
	iconst_1
L004:
	iconst_1
	ixor
	ifne	L002
.line 15
	iload_1
	iload_0
	imul
	istore_1
.line 16
	iload_0
	iconst_1
	isub
	istore_0
	goto	L001
L002:
.line 18
	iload_1
	istore_3
.line 18
.line 19
	iconst_0
	istore_3
.line 19

	iload_3
	ireturn

.limit locals 4
.limit stack 2
.end method

.method private static print(II)V

.var 1 is fact I
.var 0 is num I


.line 23
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n"
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 24
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"F"
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 25
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"a"
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 26
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"c"
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 27
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"t"
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 28
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"o"
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 29
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"r"
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 30
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"i"
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 31
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"a"
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 32
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"l"
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 33
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	" "
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 34
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"o"
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 35
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"f"
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 36
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	" "
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 37
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%d"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	iload_0
	invokestatic	java/lang/Integer.valueOf(I)Ljava/lang/Integer;
	aastore
	invokestatic	java/lang/String/format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 38
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	":"
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 39
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	" "
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 40
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%d\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	iload_1
	invokestatic	java/lang/Integer.valueOf(I)Ljava/lang/Integer;
	aastore
	invokestatic	java/lang/String/format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V

	return

.limit locals 2
.limit stack 7
.end method

.method private static main()V

.var 1 is fact I
.var 0 is num I


.line 6
	bipush	8
	istore_0
.line 7
	iload_0
	invokestatic	test/factorial(I)I
	istore_1
.line 8
	iload_0
	iload_1
	invokestatic	test/print(II)V

	return

.limit locals 2
.limit stack 2
.end method

.method public static main([Ljava/lang/String;)V

	new	RunTimer
	dup
	invokenonvirtual	RunTimer/<init>()V
	putstatic	test/_runTimer LRunTimer;
	new	PascalTextIn
	dup
	invokenonvirtual	PascalTextIn/<init>()V
	putstatic	test/_standardIn LPascalTextIn;


.line 4
	invokestatic	test/main()V

	getstatic	test/_runTimer LRunTimer;
	invokevirtual	RunTimer.printElapsedTime()V

	return

.limit locals 1
.limit stack 3
.end method
