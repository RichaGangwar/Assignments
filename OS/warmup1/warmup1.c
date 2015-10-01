#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/time.h>
#include <ctype.h>
#include <time.h>
#include <unistd.h>
#include "cs402.h"
#include <errno.h>
#include <sys/stat.h>
#include "my402list.h"


typedef struct tagTrans {
	char mode;
	unsigned int time;
	int amount;
	char *desc;
} Trans;


void first_entry(char *data,Trans *elem){
	if((strcmp(data,"+")) == 0){
		 elem->mode = 'D';
	}else if ((strcmp(data,"-")) == 0){
		elem->mode = 'W';
	}
	else{
		if (strlen(data) == 1)
		{
			fprintf(stderr, "Invalid Input.Type of transaction is other than + or -\n");
			exit(1);
		}else{
			fprintf(stderr, "Input file is not in the right format\n");
			exit(1);
		}
		
	}
}

void second_entry(char *data,Trans *elem){
	int i = 0;
	int len = strlen(data);
	unsigned int t = 0; 
	
	char data_tmp[len];
	strcpy(data_tmp,data);
	if(len > 10){
		fprintf(stderr, "Invalid input.Bad Time Stamp\n");
		exit(1);
	}

	for(i =0;i<len;i++){
		int ascii = 0;
		ascii = (int)data_tmp[i];
		if(ascii<48||ascii>57){
			fprintf
			(stderr, "Invalid input.The time stamp contains elements other than digits.\n" );
			exit(1);
		}
		
	}
	t = strtoul(data_tmp,NULL,0);
	time_t timer=(time(NULL));
	time_t rtime = (time_t)t;
	if (rtime>timer)
	{
		fprintf(stderr, "Invalid input.The time stamp is a future timestamp.\n" );
		exit(1);
	}
	
	elem->time = t;
}

void third_entry(char *data,Trans *elem){

	
	char *temp2 = NULL;
	int len = strlen(data);
	char temp1[len];
	strcpy(temp1,data);

	int i=0;
	int l;

	if(len == 0){
		fprintf(stderr, " Invalid Input.The amount field is missing.\n");
		exit(1);
	}

	temp2 = strchr(temp1,'.');
	if (temp2 == NULL)
	{
		fprintf(stderr, " Invalid Input.The amount does not contain the decimal point.\n");
		exit(1);
	}
	*temp2 = '\0';
	temp2++;
	
	int len1 = strlen(temp1);
	int len2 = strlen(temp2);
	char temp3[len1+1];
	char temp4[len2+1];
	strcpy(temp3,temp1);
	strcpy(temp4,temp2);

	if (len1<1 || len2!=2)
	{
		fprintf(stderr, "Invalid input.The amount entered is invalid.Either there is no digit before decimal point or number of digits after decimal is not 2.\n");
		exit(1);
	}




	for(i =0;i<len1;i++){
		int ascii = 0;
		ascii = (int)temp3[i];
		if(ascii<48||ascii>57){
			fprintf(stderr, "Invalid input.The amount contains elements other than digits.\n" );
			exit(1);
		}
		
	}

	for(i =0;i<len2;i++){
		int ascii = 0;
		ascii = (int)temp4[i];
		if(ascii<48||ascii>57){
			fprintf(stderr, "Invalid input.The amount contains elements other than digits.\n" );
			exit(1);
		}
		
	}

	l = (atoi(temp1)*100) + atoi(temp2);
	if (len1 > 7)
	{
		if (l>=1000000000)
		{
			fprintf(stderr, "Invalid input.The amount entered is invalid\n");
			exit(1);
		}
	}
	elem->amount = l;
	
}

void fourth_entry(char *data,Trans *elem){
	int lenofdata =strlen(data);
	char *s = (char *)malloc(lenofdata);
	while(isspace(*data)){
		data++;
	}

	int len = strlen(data);
	if (len<1)
		{
			fprintf(stderr, "The description is null\n");
			exit(1);
		}
	
	
	snprintf(s,lenofdata,"%-.24s",data);
	elem->desc = s;	
		
		
}

int ReadInput(FILE *fptr,My402List *pList1){

	char buf[1026];
	char *token;
	while(!feof(fptr)) {
		Trans *trans = NULL;
		trans = malloc(sizeof(Trans));
		
		if(fgets(buf, sizeof(buf),fptr) != NULL){
			
			if (strlen(buf)>1023)
			{
				fprintf(stderr,"Invalid input.File contain more than 1024 characters.\n");
					exit(1);
			}
   			/* get the first token */
   			token = strtok(buf,"\t");
   			int counter =0;
   			/* walk through other tokens */
   			while( token != NULL ) 
   				{
      		
      			if(counter>4){
					fprintf(stderr,"Invalid input.File contain more than 3 tabs.\n");
					exit(1);
				}
				counter=counter + 1;
				switch(counter){
				case 1:
					first_entry(token,trans);
					break;
				case 2:
					second_entry(token,trans);
					break;
				case 3:
					third_entry(token,trans);
					break;
				case 4:
					fourth_entry(token,trans);
					break;
				default:
					fprintf(stderr,"Invalid input.File contain more than 3 tabs.\n");
					exit(1);
			}
			token = strtok(NULL,"\t");
			
   				}
   				(void)My402ListAppend(pList1, trans);
   
}


}
	return TRUE;
}

static
void BubbleForward(My402List *pList, My402ListElem **pp_elem1, My402ListElem **pp_elem2)
    /* (*pp_elem1) must be closer to First() than (*pp_elem2) */
{
    My402ListElem *elem1=(*pp_elem1), *elem2=(*pp_elem2);
    void *obj1=elem1->obj, *obj2=elem2->obj;
    My402ListElem *elem1prev=My402ListPrev(pList, elem1);
/*  My402ListElem *elem1next=My402ListNext(pList, elem1); */
/*  My402ListElem *elem2prev=My402ListPrev(pList, elem2); */
    My402ListElem *elem2next=My402ListNext(pList, elem2);

    My402ListUnlink(pList, elem1);
    My402ListUnlink(pList, elem2);
    if (elem1prev == NULL) {
        (void)My402ListPrepend(pList, obj2);
        *pp_elem1 = My402ListFirst(pList);
    } else {
        (void)My402ListInsertAfter(pList, obj2, elem1prev);
        *pp_elem1 = My402ListNext(pList, elem1prev);
    }
    if (elem2next == NULL) {
        (void)My402ListAppend(pList, obj1);
        *pp_elem2 = My402ListLast(pList);
    } else {
        (void)My402ListInsertBefore(pList, obj1, elem2next);
        *pp_elem2 = My402ListPrev(pList, elem2next);
    }
}

static
void BubbleSortForwardList(My402List *pList, int num_items)
{
    My402ListElem *elem=NULL;
    int i=0;

    if (My402ListLength(pList) != num_items) {
        fprintf(stderr, "List length is not %1d in BubbleSortForwardList().\n", num_items);
        exit(1);
    }
    for (i=0; i < num_items; i++) {
        int j=0, something_swapped=FALSE;
        My402ListElem *next_elem=NULL;

        for (elem=My402ListFirst(pList), j=0; j < num_items-i-1; elem=next_elem, j++) {
            Trans *t = (Trans*)elem->obj;
            int cur_val=(int)(t->time), next_val=0;

            next_elem=My402ListNext(pList, elem);
            Trans *next_t = (Trans*)next_elem->obj;
            next_val = (int)(next_t->time);

            if (cur_val > next_val) {
                BubbleForward(pList, &elem, &next_elem);
                something_swapped = TRUE;
            }
        }
        if (!something_swapped) break;
    }
}



char * formatDate(Trans *temp){
	
	char *s3 =(char *) malloc(16);
	char *s;
	char *s5;
	char s1[20];
	char s4[8];
	unsigned int time;
	int i =0;
	time = temp->time;
	time_t rtime = (time_t)time;
	s= (char *)(ctime(&rtime));
	/*int len = strlen(s)*/;
	strncpy(s1,s,10);
	s1[10]='\0';
	s5=strchr(s,'\0');
	for(i = 0;i<5;i++){
		--s5;
	}
	snprintf(s4,5,"%.5s",s5);
	sprintf(s3,"%s %s",s1,s4);
	/*s2 = strcat(s1,s5);
	strcpy(s3,s2);*/
	
	return s3;
	
	
}

char * formatDesc(Trans *temp){
	return (temp->desc);
}

char * formatMoney(int amt){
	char *dispAmt = (char *)malloc(13*(sizeof(char)));
	/*char *fin = (char *)malloc()*/
	memset(dispAmt,' ',12);
	/*char *dispAmt[13]*/
	dispAmt[12]='\0';
	dispAmt[9] = 46;
	
	int dollar =0;
	int cent = 0;
	int a,b;
	int counter;
	
	dollar=amt/100;
	cent = amt%100;
	 if(dollar >= 10000000){
	 	snprintf(dispAmt,13,"?,???,???.??");
	 	
	 	return dispAmt;
	 }

	 
	 
	 if(cent == 0){
	 	
	 	dispAmt[10]=48;
	 	dispAmt[11]=48;
	 }else{
	 	
	 	if(cent<10){
	 		dispAmt[10]=48;
	 		dispAmt[11]=cent+48;
	 	}else{
	 		int x,y;
	 		int m=11;
	 		while(cent!=0){
	 			x=cent/10;
	 			y=cent%10;
	 			dispAmt[m]=y+48;
	 			m--;
	 			cent =x;
	 		}
	 		
	 	}
	 }
	 
	 int i =8;
	 if (dollar == 0)
	 {
	 	dispAmt[i] = dollar+48;
	 }
	 counter =0;
	 
	 while(dollar != 0){

	 	if(counter!=0 && (counter%3)==0){
	 			dispAmt[i] =44;
	 			i--;
	 		}
	 	a =dollar /10;
	 	b = dollar % 10;
	 	
	 	dispAmt[i]=b+48;
	 	counter++;
	 	i--;
	 	
	 	dollar =a;
	 	
	 }
	/*printf("%s\n",dispAmt);*/

	return dispAmt;
}

char * computeAmount(Trans *temp){
	
	char *finAmt = (char *)malloc(15*sizeof(char)); 
	char * amt =formatMoney(labs(temp->amount));
	if (temp->mode == 'W')
	{
		int n =snprintf(finAmt,15,"(%s)",amt);
		if(n<0)
			fprintf(stderr, "Could not copy amount\n");
			}else{
				int n =snprintf(finAmt,15," %s ",amt);
				if(n<0)
					fprintf(stderr, "Could not copy amount\n");
			}
	return finAmt;

}

 int computeBalance(My402List *list1,My402ListElem *elem, int bal){
	
	Trans *temp = (Trans*)elem->obj;

			if (temp->mode == 'W')
			{
				bal = bal - temp->amount;
			}else if (temp->mode == 'D')
			{
				bal =bal + temp->amount;
			}	
		
	return bal;
		
}
char * formatBalance(int bal,Trans *tr){
	char *f = formatMoney(labs(bal));
	char *finAmt = (char *)(malloc(15*sizeof(char)));
		if (bal<0)
		{
		int n =snprintf(finAmt,15,"(%s)",f);
		if(n<0)
			fprintf(stderr, "Could not copy amount\n");
		}else{
				int n =snprintf(finAmt,15," %s ",f);
				if(n<0)
					fprintf(stderr, "Could not copy amount\n");
			}
		return finAmt;
}

void PrintStatement(My402List *pList){
	char line[82];
	
	My402ListElem *elem = NULL;
	
	int balance=0;

	
	fprintf(stdout,"+-----------------+--------------------------+----------------+----------------+\n");
	fprintf(stdout,"|       Date      | Description              |         Amount |        Balance |\n");
	fprintf(stdout,"+-----------------+--------------------------+----------------+----------------+\n");


	for (elem=My402ListFirst(pList);elem != NULL;elem=My402ListNext(pList, elem)){
		Trans *t = (Trans*)elem->obj;
		
		char *ban = (char *)malloc(15*(sizeof(char)));
		char *an = (char *)malloc(15*(sizeof(char)));
		
		char *date = formatDate(t);
		char *des = formatDesc(t);
		balance = computeBalance(pList,elem,balance);
		char *baln = formatBalance(balance,t);
		strcpy(ban, baln);

		char *amt = computeAmount(t);
		strcpy(an, amt);
		
		int n = snprintf(line,82,"| %s | %-24s | %s | %s |",date,des,an,ban);
		if(n<0){
			fprintf(stderr, "Error in writing the line.\n");
		}
		fprintf(stdout, "%s\n",line);
		free(ban);
		free(an);
		free(t);
		memset(line, 0 ,sizeof(line));

	}

	fprintf(stdout,"+-----------------+--------------------------+----------------+----------------+\n");

}

void CheckTimeStampSame(My402List *pList, int num_items){

	My402ListElem *elem1 = NULL;
	My402ListElem *elem = NULL;
	
	
	unsigned int t1=0;
	unsigned int t2=0;
	

	for (elem1=My402ListFirst(pList);elem1 != NULL;elem1 = My402ListNext(pList, elem1)){
		Trans *tFirst = (Trans*)elem1->obj;
		t1 = tFirst->time;

		for(elem = My402ListNext(pList, elem1);elem != NULL; elem =My402ListNext(pList,elem)){
			Trans *tNext = (Trans*)elem->obj;
			t2 = tNext->time;

			if(t2 ==t1){
			fprintf(stderr,"Invalid input.Two timestamps cannot be same.\n");
        	exit(1);
		}
		}
		
		
	}
}

int main(int argc, char const *argv[])
{
	My402List list;
	FILE *fp = NULL;
	
	struct stat st_buf;
	

	if (!My402ListInit(&list)){
	
		fprintf(stderr,"List could not be initialised \n");
            exit(1);
	}
	
	if ((argv[1] == NULL )|| (strcmp(argv[1],"sort") != 0))
	{
		fprintf(stderr,"Malformed command,Please use \"warmup1 sort [tfile]\"\n");
           exit(1);
	}
	
	if (argv[2] == NULL)
	 {
	 	
	 	fp = stdin;
	 } 
	 else{
	 	stat (argv[2], &st_buf);
	 	if (access(argv[2],F_OK) == -1)
	 		{
	 			fprintf(stderr, "Input File %s does not exist.\n",argv[2]);
				exit(1);
	 	}

	 	if (S_ISDIR (st_buf.st_mode)) {
        		printf ("%s is a directory.\n", argv[2]);
        		exit(1);
    		}
	 	
	 	if((fp  = fopen(argv[2],"r")) == NULL){
			fprintf(stderr, "Input file %s cannot be opened - access denies\n",argv[2]);
			exit(1);
		}


	 }
		 		
	 if (!ReadInput(fp, &list))
	 {
	 	fprintf(stderr, "Error in reading the input.\n");
	 	exit(1);
	 }
	 int len = My402ListLength(&list);
	 BubbleSortForwardList(&list,len);
	 CheckTimeStampSame(&list,len);
	 PrintStatement(&list);
	 return 0;
}


