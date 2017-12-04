%{
#include<stdlib.h>
#include<string.h>
#include<math.h>
#include "y.tab.h"

typedef struct{
	int id1;
	int id2;
}PIF;

typedef struct{
	char name[10];
}TSiden;

int pifLength = 0;
int constLength = 0;
int identifierLength = 0;
int identifierCode = 0;
int constCode = 0;

PIF pifTable[300];
TSiden identifierTable[30];
TSiden constantsTable[30];

void addPIF(char* id1,int id2){
  if(strcmp(id1, "id") == 0){
    pifTable[pifLength].id1 = 0;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, "define") == 0){
    pifTable[pifLength].id1 = 1;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, "(") == 0){
    pifTable[pifLength].id1 = 2;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, "real") == 0){
    pifTable[pifLength].id1 = 3;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, ")") == 0){
    pifTable[pifLength].id1 = 4;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, ";") == 0){
    pifTable[pifLength].id1 = 5;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, "int") == 0){
    pifTable[pifLength].id1 = 6;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, "input") == 0){
    pifTable[pifLength].id1 = 7;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, ",") == 0){
    pifTable[pifLength].id1 = 8;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, "=") == 0){
    pifTable[pifLength].id1 = 9;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, "*") == 0){
    pifTable[pifLength].id1 = 10;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, "+") == 0){
    pifTable[pifLength].id1 = 11;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, "-") == 0){
    pifTable[pifLength].id1 = 12;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, "/") == 0){
    pifTable[pifLength].id1 = 13;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, "%") == 0){
    pifTable[pifLength].id1 = 14;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, "output") == 0){
    pifTable[pifLength].id1 = 15;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, "while") == 0){
    pifTable[pifLength].id1 = 16;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, "==") == 0){
    pifTable[pifLength].id1 = 16;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, "!=") == 0){
    pifTable[pifLength].id1 = 18;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, "<=") == 0){
    pifTable[pifLength].id1 = 19;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, ">=") == 0){
    pifTable[pifLength].id1 = 20;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, "<") == 0){
    pifTable[pifLength].id1 = 21;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, ">") == 0){
    pifTable[pifLength].id1 = 22;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, "begin") == 0){
    pifTable[pifLength].id1 = 23;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, ":") == 0){
    pifTable[pifLength].id1 = 24;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, "end") == 0){
    pifTable[pifLength].id1 = 25;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, "if") == 0){
    pifTable[pifLength].id1 = 26;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
  else if(strcmp(id1, ".") == 0){
    pifTable[pifLength].id1 = 27;
    pifTable[pifLength].id2 = id2;
    ++pifLength;
  }
}

void addConst(char* atom){
  int found = 0;
	int i;
	for(i=0;i < constLength;i++){
		if(strcmp(constantsTable[i].name,atom) == 0){
			found = 1;
			addPIF("id",i);
      break;
		}
	}
	if(found == 0){
		strcpy(constantsTable[constLength].name,atom);
		addPIF("id",constLength);
		constLength++;
	}
}

void addIdentifier(char *atom){
	int found = 0;
	int i;
  if(strlen(atom) > 8){
    printf("Invalid identifier %s\n", atom);
    return;
  }
	for(i=0;i < identifierLength;i++){
		if(strcmp(identifierTable[i].name,atom) == 0){
			found = 1;
			addPIF("id",i);
      break;
		}
	}
	if(found == 0){
		strcpy(identifierTable[identifierLength].name,atom);
		addPIF("id",identifierLength);
		identifierLength++;
	}
}

void showPIF(){
	printf("PIF:\n");
	int t;
	for(t = 0; t < pifLength;++t){
		if(pifTable[t].id1 != identifierCode && pifTable[t].id1 != constCode){
			printf(" %d|- ",pifTable[t].id1);
		}
		else{
			printf(" %d|%d ",pifTable[t].id1,pifTable[t].id2);
		}
	}
}

void showConst(){
	printf("Constants: \n");
  int i;
	for(i=0;i<constLength;++i){
		printf(" %s|%d",constantsTable[i].name, i);
	}
	printf("\n");
}

void showId(){
    printf("Identifier\n");
    int i;
    for(i = 0 ;i<identifierLength;++i)
        printf(" %s|%d", identifierTable[i].name, i);
    printf("\n");
}

void show(){
	showConst();
	showId();
	showPIF();
}
%}

%option noyywrap

digit	[0-9]
non_zero_digit [1-9]
number {non_zero_digit}{digit}*|0
constant {number}|PI
id [a-zA-Z]+
keyword define|\(|real|\)|;|int|input|,|=|\*|\+|-|\/|%|output|while|==|!=|\<=|\>=|\<|\>|begin|:|end|if|\.

%%
print show();
define {addPIF(yytext, 0); return DEFINE_TOKEN;}
\( {addPIF(yytext, 0); return yytext[0];}
real {addPIF(yytext, 0); return TYPE_TOKEN;}
\) {addPIF(yytext, 0); return yytext[0];}
; {addPIF(yytext, 0); return yytext[0];}
int {addPIF(yytext, 0); return TYPE_TOKEN;}
input {addPIF(yytext, 0); return INPUT_TOKEN;}
, {addPIF(yytext, 0); return yytext[0];}
= {addPIF(yytext, 0); return ASSIGNMENT_TOKEN;}
\* {addPIF(yytext, 0); return MULT;}
\+ {addPIF(yytext, 0); return ADD;}
- {addPIF(yytext, 0); return SUBTRACT;}
\% {addPIF(yytext, 0); return REMAINDER;}
output {addPIF(yytext, 0); return OUTPUT_TOKEN;}
while {addPIF(yytext, 0); return WHILE_TOKEN;}
== {addPIF(yytext, 0); return BOOL_OP;}
!= {addPIF(yytext, 0); return BOOL_OP;}
\<= {addPIF(yytext, 0); return BOOL_OP;}
\>= {addPIF(yytext, 0); return BOOL_OP;}
\< {addPIF(yytext, 0); return BOOL_OP;}
\> {addPIF(yytext, 0); return BOOL_OP;}
begin {addPIF(yytext, 0); return BEGIN_TOKEN;}
: {addPIF(yytext, 0); return yytext[0];}
end {addPIF(yytext, 0); return END_TOKEN;}
if {addPIF(yytext, 0); return IF_TOKEN;}
\. {addPIF(yytext, 0); return DOT;}

{constant} { addConst(yytext); return CONST_TOKEN; }
{id} { addIdentifier(yytext); return ID_TOKEN; }

[ \t\n] printf("");

. printf("Invalid identifier %s!\n", yytext);
%%
