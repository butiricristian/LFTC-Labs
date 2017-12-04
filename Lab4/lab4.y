%{
#include <stdio.h>

int yyerror(const char *s);
extern int yylex(void);
extern FILE *yyin;
extern void show();
%}

%union{
  char* 	sval;
}

%start	program

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
program: user_type_def ';' var_declarations ';' statements ';' {printf("Program with user def\n");} | var_declarations ';' statements ';'  {printf("Program without user def\n");}
;
var_declarations: var_declaration | var_declarations ';' var_declaration {printf("Multiple var declarations\n");}
;
var_declaration: TYPE_TOKEN var_list {printf("Predefined Var declaration\n");} | user_defined_type var_list  {printf("User type Var declaration\n");}
;
var_list: ID_TOKEN  {printf("single var in var list\n");} | ID_TOKEN ',' var_list {printf("multiple vars in var list\n");}
;
user_type_def: DEFINE_TOKEN user_defined_type  '(' var_declarations ')' {printf("User type definition\n");}
;
user_defined_type: ID_TOKEN
;
statements: statement {printf("Single statement\n");} | statements ';' statement {printf("multiple statements\n");}
;
statement: assign_stmt {printf("Assign\n");} | input_stmt {printf("Input\n");} | output_stmt {printf("Output\n");} | loop_stmt {printf("Loop\n");} | conditional_stmt {printf("Conditional\n");}
;
assign_stmt: ID_TOKEN ASSIGNMENT_TOKEN expr {printf("Assign statement\n");}
;
expr: ID_TOKEN {printf("Expression\n");} | CONST_TOKEN {printf("Expression\n");} | ID_TOKEN operator expr {printf("Expression\n");} | CONST_TOKEN operator expr {printf("Expression\n");}
;
operator: MULT | ADD | DIVISION | SUBTRACT | REMAINDER | DOT
;
input_stmt: INPUT_TOKEN '(' expr ')' {printf("Input statement\n");}
;
output_stmt: OUTPUT_TOKEN '(' expr ')'  {printf("Output statement\n");}
;
loop_stmt: WHILE_TOKEN '(' logical_expr ')' BEGIN_TOKEN ':' statements ';' END_TOKEN  {printf("Loop statement\n");}
;
logical_expr: ID_TOKEN BOOL_OP ID_TOKEN {printf("Logical expression\n");} | ID_TOKEN BOOL_OP CONST_TOKEN {printf("Logical expression\n");} | CONST_TOKEN BOOL_OP ID_TOKEN {printf("Logical expression\n");} | CONST_TOKEN BOOL_OP CONST_TOKEN {printf("Logical expression\n");}
;
conditional_stmt: IF_TOKEN '(' logical_expr ')' BEGIN_TOKEN ':' statements ';' END_TOKEN {printf("Conditional statement\n");}

%%



int main(int argc, char** argv) {

    // open a file handle to a particular file:
    FILE *myfile = fopen("program3.txt", "r");
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
