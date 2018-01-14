%{
#include <stdio.h>
#include <string.h>

int yyerror(const char *s);
extern int yylex(void);
extern FILE *yyin;
extern void show();
char* BYTES = "2";
FILE *fout = fopen("program1.asm", "w");
%}

%union{
  char* sval;
}

%start	program

%type <sval> expr
%type <sval> var_declarations
%type <sval> var_declaration
%type <sval> statements
%type <sval> statement
%type <sval> var_list
%type <sval> assign_stmt
%type <sval> input_stmt
%type <sval> output_stmt
%type <sval> conditional_stmt

%token <sval> ID_TOKEN
%token <sval> CONST_TOKEN
%token DEFINE_TOKEN
%token TYPE_TOKEN
%token INPUT_TOKEN
%token ASSIGNMENT_TOKEN
%token OUTPUT_TOKEN
%token WHILE_TOKEN
%token BEGIN_TOKEN
%token END_TOKEN
%token IF_TOKEN

%left	ADD SUBTRACT
%left	MULT DIVISION
%left REMAINDER
%left BOOL_OP
%left DOT

%%
program: user_type_def ';' var_declarations ';' statements ';' {}
  | var_declarations ';' statements ';'  {fprintf(fout, "; Input:\n; ESI = pointer to the string to convert\n; ECX = number of digits in the string (must be > 0)\n; Output:\n; EAX = integer value\nstring_to_int:\n\txor ebx,ebx    ; clear ebx\n.next_digit:\n\tmovzx eax,byte[esi]\n\tinc esi\n\tsub al,'0'    ; convert from ASCII to number\n\timul ebx,10\n\tadd ebx,eax   ; ebx = ebx*10 + eax\n\tloop .next_digit  ; while (--ecx)\n\tmov eax,ebx\n\tret\n\n; Input:\n; EAX = integer value to convert\n; ESI = pointer to buffer to store the string in (must have room for at least 10 bytes)\n; Output:\n; EAX = pointer to the first character of the generated string\nint_to_string:\n\tadd esi,9\n\tmov byte [esi],STRING_TERMINATOR\n\n\tmov ebx,10\n.next_digit:\n\txor edx,edx         ; Clear edx prior to dividing edx:eax by ebx\n\tdiv ebx             ; eax /= 10\n\tadd dl,'0'          ; Convert the remainder to ASCII \n\tdec esi             ; store characters in reverse order\n\tmov [esi],dl\n");
  fprintf(fout, "\ttest eax,eax\n\tjnz .next_digit ; Repeat until eax==0\n\tmov eax,esi\n\tret\nsection .text\n\tglobal _start\n\textern _scanf\n\textern _printf\n_start:\n%s\n\tmov eax,1\n\tmov ebx, 0\n\tint 80h\nsection .data\n%s\n\tSTRING_TERMINATOR equ 0\nsection .bss\n\tbuffer resb 5\n", $3, $1);}
  | var_declarations ';' {}
;
var_declarations: var_declaration {$$ = $1} | var_declarations ';' var_declaration {char* tmpVal = new char[strlen($1) + strlen($3) + 1]; strcpy(tmpVal, $1); strcat(tmpVal, $3); $$ = tmpVal}
;
var_declaration: TYPE_TOKEN var_list {$$ = $2 } | user_defined_type var_list  {}
;
var_list: ID_TOKEN  {
  char* tmpVal = new char[strlen($1) + strlen(" DW 0\n") + 3];
  strcpy(tmpVal, "\t");
  strcat(tmpVal, $1);
  strcat(tmpVal, " DW 0\n");
  $$ = tmpVal
}
  | ID_TOKEN ',' var_list {
    char* tmpVal = new char[strlen($1) + strlen(" DW 0\n") + 3 + strlen($3)];
    strcpy(tmpVal, "\t");
    strcat(tmpVal, $1);
    strcat(tmpVal, " DW 0\n");
    strcat(tmpVal, $3);
    $$ = tmpVal
  }
;
user_type_def: DEFINE_TOKEN user_defined_type  '(' var_declarations ')' {}
;
user_defined_type: ID_TOKEN
;
statements: statement {$$ = $1} | statements ';' statement {
  char* tmpVal = new char[strlen($1) + strlen($3) + 1];
  strcpy(tmpVal, $1);
  strcat(tmpVal, $3);
  $$ = tmpVal
}
;
statement: assign_stmt {$$ = $1}
  | input_stmt {$$ = $1}
  | output_stmt {$$ = $1}
  | loop_stmt {$$ = "loop_stmt"}
  | conditional_stmt {$$ = "conditional_stmt"}
;
assign_stmt: ID_TOKEN ASSIGNMENT_TOKEN expr {
  char* tmpVal = new char[strlen($1) + strlen($3) + 2 + strlen("\tMOV , eax\n")];
  strcpy(tmpVal, $3);
  strcat(tmpVal, "\tMOV [");
  strcat(tmpVal, $1);
  strcat(tmpVal, "], eax\n");
  $$ = tmpVal;}
;
expr: ID_TOKEN {
  char* tmpVal = new char[strlen($1) + strlen("\tMOV eax, []\n") + 1];
  strcpy(tmpVal, "\tMOV eax, [");
  strcat(tmpVal, $1);
  strcat(tmpVal, "]\n");
  $$ = tmpVal;
}
  | CONST_TOKEN {
    char* tmpVal = new char[strlen($1) + strlen("\tMOV eax, \n") + 1];
    strcpy(tmpVal, "\tMOV eax, ");
    strcat(tmpVal, $1); strcat(tmpVal, "\n");
    $$ = tmpVal;
  }
  | ID_TOKEN operator expr {
    char* tmpVal = new char[strlen($3) + 2 + strlen("\tADD eax, []\n") + strlen($1)];
    strcat(tmpVal, $3);
    strcat(tmpVal, "\tADD eax, [");
    strcat(tmpVal, $1);
    strcat(tmpVal, "]\n");
    $$ = tmpVal
  }
  | CONST_TOKEN operator expr {
    char* tmpVal = new char[strlen($3) + 2 + strlen("\tADD eax, \n") + strlen($1)];
    strcat(tmpVal, $3);
    strcat(tmpVal, "\tADD eax, ");
    strcat(tmpVal, $1);
    strcat(tmpVal, "\n");
    $$ = tmpVal
  }
;
operator: MULT | ADD | DIVISION | SUBTRACT | REMAINDER | DOT
;
input_stmt: INPUT_TOKEN '(' ID_TOKEN ')' {
  char* tmpVal = new char[3 * strlen($3) + strlen("\tmov eax, 3\n\tmov ebx, 0\n\tmov ecx, \n\tmov edx, 5\n\tint 80h\n\tlea esi, []\n\tmov ecx,4\n\tcall string_to_int\n\tmov [], eax\n") + 1];
  strcpy(tmpVal, "\tmov eax, 3\n\tmov ebx, 0\n\tmov ecx, ");
  strcat(tmpVal, $3);
  strcat(tmpVal, "\n\tmov edx, ");
  strcat(tmpVal, BYTES);
  strcat(tmpVal, "\n\tint 80h");
  strcat(tmpVal, "\n\tlea esi, [");
  strcat(tmpVal, $3);
  strcat(tmpVal, "]\n\tmov ecx, ");
  strcat(tmpVal, BYTES);
  strcat(tmpVal, "\n\tcall string_to_int\n\tmov [");
  strcat(tmpVal, $3);
  strcat(tmpVal, "], eax\n");
  $$ = tmpVal;
}
;
output_stmt: OUTPUT_TOKEN '(' expr ')'  {char* tmpVal = new char[3*strlen($3) + strlen("\tlea esi, eax\n\tcall int_to_string\n\tmov ecx, eax\tmov eax, 3\n\tmov ebx, 2\n\tmov edx, 5\n\tint 80h\n") + 1];
  strcpy(tmpVal, $3);
  strcat(tmpVal, "\tmov [buffer], eax\n\tlea esi, [buffer]\n\tcall int_to_string");
  strcat(tmpVal, "\n\tmov ecx, eax\n\tmov eax, 4\n\tmov ebx, 1");
  strcat(tmpVal, "\n\tmov edx, ");
  strcat(tmpVal, BYTES);
  strcat(tmpVal, "\n\tint 80h\n");
  $$ = tmpVal;
}
;
loop_stmt: WHILE_TOKEN '(' logical_expr ')' BEGIN_TOKEN ':' statements ';' END_TOKEN  {printf("Loop statement\n");}
;
logical_expr: ID_TOKEN BOOL_OP ID_TOKEN {printf("Logical expression\n");} | ID_TOKEN BOOL_OP CONST_TOKEN {printf("Logical expression\n");} | CONST_TOKEN BOOL_OP ID_TOKEN {printf("Logical expression\n");} | CONST_TOKEN BOOL_OP CONST_TOKEN {printf("Logical expression\n");}
;
conditional_stmt: IF_TOKEN '(' logical_expr ')' BEGIN_TOKEN ':' statements ';' END_TOKEN {printf("Conditional statement\n");}

%%



int main(int argc, char** argv) {

    // open a file handle to a particular file:
    FILE *myfile = fopen("program1.txt", "r");
    // make sure it is valid:
    if (!myfile) {
        printf( "I can't open file!\n");
        return -1;
    }
    // set flex to read from it instead of defaulting to STDIN:
    yyin = myfile;
    // parse through the input until there is no more:
    do {
        yyparse();
    } while (!feof(yyin));
    show();
}



int yyerror(const char *s) {
    printf("Parse error!  Message: %s\n",s);
    return -1;
}
