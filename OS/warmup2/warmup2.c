#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include "cs402.h"
#include "my402list.h"
#include <unistd.h>
#include <sys/stat.h>
#include <errno.h>
#include <sys/time.h>
#include <ctype.h>
#include <signal.h>
#include <string.h>
#include <getopt.h>
#include <math.h>

typedef struct tagPacket{
	int packet_no;
	
	int no_of_tokens;
	double inter_packet_time;
	double service_time;

	struct timeval arrival;
	struct timeval enter_Q1;
	struct timeval enter_Q2;
	struct timeval leave_Q1;
	struct timeval leave_Q2;
	struct timeval enter_service;
	struct timeval leave_service;




}Packet;

struct timeval emulationStart;
struct timeval emulationEnd;
 struct timeval emulationReached2;
FILE *fp = NULL;
int no_of_packets =20;
int depth =10;
int p=3;
int TokenBucket;
int tokenNo;
int droppedToken =0;
double lambda = 0.5;
double r =1.5;
double mu=0.35;
My402List queue1;
My402List queue2;


pthread_mutex_t mutexVar;
pthread_cond_t q2_notEmpty_cv;
pthread_t packet_arrival;
pthread_t token_deposit;
pthread_t server;
sigset_t new;
struct sigaction act;
int interrupted =0;
int no_packet_processed =0;
int no_packet_left =0;

int trace = 0;
int packet_arrival_ended =0;
int packet_dropped = 0;
int packet_removed =0;
int packet_completed =0;
int packet_produced =0;
double total_inter_packet_time=0;
double total_time_Q1 =0.0;
double total_time_Q2 =0.0;
double total_time_S =0.0;
double time_in_system=0.0;
double square_time_in_system =0.0;
 

double gettimeinms(struct timeval emultime,struct timeval time){
	double d;
	d = (double)(((time.tv_sec*1000000+time.tv_usec)-(emultime.tv_sec*1000000+emultime.tv_usec))/1000);
	return d;
}

void moveFromQ1toQ2(){
	
	My402ListElem *elem1 = My402ListFirst(&queue1);
	Packet *pckt = (Packet *)elem1->obj;
	TokenBucket = TokenBucket-pckt->no_of_tokens;
	My402ListUnlink(&queue1,elem1);
	gettimeofday(&pckt->leave_Q1,NULL);
	fprintf(stdout, "%012.3fms: p%d leaves Q1,time in Q1 = %.3fms, token bucket now has %d token\n",gettimeinms(emulationStart,pckt->leave_Q1),pckt->packet_no,gettimeinms(pckt->enter_Q1,pckt->leave_Q1),TokenBucket );
	(void)My402ListAppend(&queue2,pckt);
	gettimeofday(&pckt->enter_Q2,NULL);
	fprintf(stdout, "%012.3fms: p%d enters Q2\n",gettimeinms(emulationStart,pckt->enter_Q2),pckt->packet_no);
}




void readTraceFile(Packet *p){
	char buf[1026];
	//char *start;
	//char *start = buf;
	//int len = strlen(buf);
	/*char *last = buf[len];*/
	//char *last = strchr(buf,'\0');
	//last--;
	if(!feof(fp)) {
		if(fgets(buf, sizeof(buf),fp) != NULL){
			/*if (isspace(start) || strcmp(start,"\t")==0 || isspace(last) || strcmp(last,"\t")==0)
			{
				fprintf(stderr, "The tsfile contains trailing spaces\n");
				exit(1);
			}*/
			/*start = buf;
			if (isspace(*start))
			{
				fprintf(stderr, "The tsfile contains trailing spaces\n");
				exit(1);
			}*/
			char *token = strtok(buf," ");
			if (token != NULL)
				p->inter_packet_time = (atoi(token))*1000;
			
			token = strtok(NULL," ");
			if (token != NULL)
				p->no_of_tokens =atoi(token);

			token = strtok(NULL," ");
			if(token != NULL)
				p->service_time = (atoi(token))*1000;


		}
	}

}

void *packet_arrival_subroutine(){
	int n=0;
	struct timeval emulaionReached;
	
	//emulaionReached2 = emulationStart;
	
	while(n<no_of_packets){
		Packet *pckt =NULL;
		n++;
		pckt = malloc(sizeof(Packet));
		pckt->packet_no = n;
		
		if(trace == TRUE){
			readTraceFile(pckt);
		}else{
			pckt->inter_packet_time = ((1/lambda)*1000000);
			pckt->service_time = ((1/mu)*1000000);
			pckt->no_of_tokens = p;
		}

			gettimeofday(&emulaionReached,NULL);
			double d = gettimeinms(emulationReached2,emulaionReached);
			if((d*1000) < pckt->inter_packet_time)	
				usleep(pckt->inter_packet_time - (d*1000));

		gettimeofday(&pckt->arrival,NULL);
		//gettimeofday(&emulationReached2,NULL);
		emulationReached2 = pckt->arrival;
		if(pckt->no_of_tokens <= depth)
			fprintf(stdout, "%012.3fms: p%d arrives,needs %d tokens,inter-arrival time %.3fms\n",gettimeinms(emulationStart,pckt->arrival),pckt->packet_no,pckt->no_of_tokens,pckt->inter_packet_time/1000);
		total_inter_packet_time =total_inter_packet_time + pckt->inter_packet_time;
		packet_produced++;
		if(pckt->no_of_tokens > depth){
			fprintf(stdout, "%012.3fms: p%d arrives,needs %d tokens,inter-arrival time %.3fms,dropped\n",gettimeinms(emulationStart,pckt->arrival),pckt->packet_no,pckt->no_of_tokens,pckt->inter_packet_time/1000);
			packet_dropped++;
			continue;
		}
		pthread_testcancel();
		pthread_cleanup_push(pthread_mutex_unlock, (void *) &mutexVar);
		pthread_mutex_lock(&mutexVar);
		
		int isEmpty = My402ListEmpty(&queue2);
		pthread_testcancel();
		(void)My402ListAppend(&queue1, pckt);
		gettimeofday(&pckt->enter_Q1,NULL);
		fprintf(stdout,"%012.3fms: p%d enters Q1 \n",gettimeinms(emulationStart,pckt->enter_Q1),pckt->packet_no);
		pthread_testcancel();
		if(TokenBucket>=pckt->no_of_tokens)
				moveFromQ1toQ2();
		
		if(isEmpty == TRUE && My402ListEmpty(&queue2)!=TRUE){
			pthread_cond_signal(&q2_notEmpty_cv);
		}
			
		pthread_mutex_unlock(&mutexVar);
		pthread_cleanup_pop(0);
	}
	packet_arrival_ended = 1;
	if(packet_produced == packet_dropped){
		no_packet_processed =1;
		pthread_cond_signal(&q2_notEmpty_cv);
	}

	pthread_exit(NULL);
	
}

void *token_deposit_subroutine(){
	Packet *pckt;
	while(TRUE){
		
		usleep((1/r)*1000000);
		pthread_cleanup_push(pthread_mutex_unlock, (void *) &mutexVar);
		pthread_mutex_lock(&mutexVar);
		
		tokenNo++;
		struct timeval tokenarrival;
		gettimeofday(&tokenarrival,NULL);
		pthread_testcancel();
		if(TokenBucket <depth){
			TokenBucket++;
			if(TokenBucket ==1 || TokenBucket ==0)
				fprintf(stdout,"%012.3fms: Token t%d arrives, token bucket now has %d token\n",gettimeinms(emulationStart,tokenarrival),tokenNo,TokenBucket);
			else
				fprintf(stdout,"%012.3fms: Token t%d arrives, token bucket now has %d tokens\n",gettimeinms(emulationStart,tokenarrival),tokenNo,TokenBucket);
		}else{
			fprintf(stdout,"%012.3fms: Token t%d arrives,dropped\n",gettimeinms(emulationStart,tokenarrival),tokenNo);
			droppedToken++;
		}
		int isEmpty = My402ListEmpty(&queue2);
		int isEmpty1 = My402ListEmpty(&queue1);
		if (isEmpty1 != TRUE){
			My402ListElem *elem1 = My402ListFirst(&queue1);
			pckt = (Packet *)elem1->obj;
			if(TokenBucket>=pckt->no_of_tokens){
				pthread_testcancel();
				moveFromQ1toQ2();
			}
					
		}
		if(isEmpty == TRUE && My402ListEmpty(&queue2) != TRUE){
			pthread_cond_signal(&q2_notEmpty_cv);
		}
		if(packet_arrival_ended == 1 && (My402ListEmpty(&queue1) == TRUE)){

			pthread_mutex_unlock(&mutexVar);
			//break;
			if(My402ListEmpty(&queue2) == TRUE)
				pthread_cond_signal(&q2_notEmpty_cv);
			pthread_exit(NULL);
		}
		pthread_mutex_unlock(&mutexVar);
		pthread_cleanup_pop(0);
	}
	pthread_exit(NULL);

}

void *server_subroutine(){
	Packet *pckt;
	int n;
	while(TRUE){
	pthread_testcancel();
	pthread_mutex_lock(&mutexVar);
	//pthread_cleanup_push(pthread_mutex_unlock, (void *) &mutexVar);
	if(packet_arrival_ended ==1 && My402ListEmpty(&queue1) == TRUE && My402ListEmpty(&queue2) == TRUE){
		break;
	}
	
	//int isEmpty = My402ListEmpty(&queue2);
	while(My402ListEmpty(&queue2) == TRUE){
		pthread_cond_wait(&q2_notEmpty_cv,&mutexVar);
	
	
	if (no_packet_processed ==1)
	{
		pthread_mutex_unlock(&mutexVar);
		//break;
		
		pthread_exit(NULL);
	}
	if(packet_arrival_ended ==1 && My402ListEmpty(&queue1) == TRUE && My402ListEmpty(&queue2) == TRUE){
		no_packet_left = 1;
		pthread_mutex_unlock(&mutexVar);
		pthread_exit(NULL);
	}
}

/*if(no_packet_processed ==1)
	break;*/
	pthread_testcancel();
	if(My402ListEmpty(&queue2) != TRUE){
		My402ListElem *elem1 = My402ListFirst(&queue2);
		pckt = (Packet *)elem1->obj;
		My402ListUnlink(&queue2,elem1);
		gettimeofday(&pckt->leave_Q2,NULL);
		fprintf(stdout, "%012.3fms: p%d leaves Q2,time in Q2 = %.3fms\n",gettimeinms(emulationStart,pckt->leave_Q2),pckt->packet_no,gettimeinms(pckt->enter_Q2,pckt->leave_Q2));
		
		pthread_mutex_unlock(&mutexVar);
	}
	pthread_testcancel();
	gettimeofday(&pckt->enter_service,NULL);
	
	fprintf(stdout, "%012.3fms: p%d begins service at S, requesting %.3fms of service\n",gettimeinms(emulationStart,pckt->enter_service),pckt->packet_no,pckt->service_time/1000);
	pthread_setcancelstate(PTHREAD_CANCEL_DISABLE, NULL);
	usleep(pckt->service_time);
	gettimeofday(&pckt->leave_service,NULL);
	fprintf(stdout,"%012.3fms: p%d departs from S, service time = %.3fms, time in system = %.3fms\n",gettimeinms(emulationStart,pckt->leave_service),pckt->packet_no,gettimeinms(pckt->enter_service,pckt->leave_service),gettimeinms(pckt->arrival,pckt->leave_service));
	packet_completed++;
	total_time_Q1 = total_time_Q1+gettimeinms(pckt->enter_Q1,pckt->leave_Q1);
	total_time_Q2 = total_time_Q2+gettimeinms(pckt->enter_Q2,pckt->leave_Q2);
	total_time_S = total_time_S+gettimeinms(pckt->enter_service,pckt->leave_service);
	double d = gettimeinms(pckt->arrival,pckt->leave_service);
	time_in_system = time_in_system + d;
	square_time_in_system = square_time_in_system + pow(d,2);

	n =pckt->packet_no;
	if(interrupted == 1){
		gettimeofday(&emulationEnd,NULL);
		fprintf(stdout, "%012.3fms: emulation ends\n",gettimeinms(emulationStart,emulationEnd));
	}
	pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);
	pthread_testcancel();
}
gettimeofday(&emulationEnd,NULL);
fprintf(stdout, "%012.3fms: emulation ends\n",gettimeinms(emulationStart,emulationEnd));

pthread_exit(NULL);
}

void checkForCorrectFileParameters(const char *filePath){
	//FILE *fptr =NULL;
	struct stat st_buf;
	stat (filePath, &st_buf);
	 	if (access(filePath,F_OK) == -1)
	 		{
	 			fprintf(stderr, "Input File %s does not exist.\n",filePath);
				exit(1);
	 	}

	 	if (S_ISDIR (st_buf.st_mode)) {
        		printf ("%s is a directory.\n", filePath);
        		exit(1);
    		}
	 	
	 	if((fp  = fopen(filePath,"r")) == NULL){
			fprintf(stderr, "Input file %s cannot be opened - access denies\n",filePath);
			exit(1);
		}
		char buf[1026];
		char *start; 
		if(fgets(buf, sizeof(buf),fp) != NULL){
			start = buf;
			if (isspace(*start))
			{
				fprintf(stderr, "The tsfile contains trailing spaces\n");
				exit(1);
			}
			no_of_packets = atoi(start);
			if (no_of_packets == 0)
			{
				fprintf(stderr, "error:Incorrect file format\n");
				exit(0);
			}
		}
}

void print_statistics(){
	double total_time = gettimeinms(emulationStart,emulationEnd);
	int no_packet_cond =0;
	if ((packet_produced == packet_dropped) || (packet_produced == packet_removed) || (packet_produced == (packet_removed+packet_dropped)) || packet_completed==0)
	{
		no_packet_cond = 1;
	}
	fprintf(stdout, "Statistics:\n");
	if(no_packet_cond == 1){
		fprintf(stdout, "\taverage packet inter-arrival time = no packets arrived\n");	
		fprintf(stdout, "\taverage packet service time = no packets arrived\n");
	}else{
		fprintf(stdout, "\taverage packet inter-arrival time = %.6g sec\n",(double)(total_inter_packet_time/packet_completed)/1000000);
		fprintf(stdout, "\taverage packet service time = %.6g sec\n",(double)(total_time_S/packet_completed)/1000);
	}
	


	fprintf(stdout, "\taverage number of packets in Q1 = %.6g \n",total_time_Q1/total_time);
	fprintf(stdout, "\taverage number of packets in Q2 = %.6g \n",total_time_Q2/total_time);
	fprintf(stdout, "\taverage number of packets at S = %.6g \n",total_time_S/total_time);

	if(no_packet_cond == 1){
		fprintf(stdout, "\taverage time a packet spent in system = no packets arrived\n");
		fprintf(stdout, "\tstandard deviation for time spent in system = no packets arrived\n");
	}else{
		double t = time_in_system/(double)(packet_completed*1000);
		fprintf(stdout, "\taverage time a packet spent in system = %.6g sec\n",t);
		double t2=sqrt((double)((square_time_in_system / packet_completed) / 1000000 - pow(((time_in_system / packet_completed) / 1000), 2)));
		fprintf(stdout, "\tstandard deviation for time spent in system = %.6g\n",t2);

	}
	
	if(tokenNo == 0)
		fprintf(stdout, "\ttoken drop probability = no tokens arrived\n");
	else
		fprintf(stdout, "\ttoken drop probability = %.6g\n",(double)droppedToken/(double)tokenNo);
	if(packet_produced == 0)
		fprintf(stdout, "\tpacket drop probability = no packets arrived\n");
	else
		fprintf(stdout, "\tpacket drop probability = %.6g\n",(double)packet_dropped/(double)packet_produced);

	
}

void interrupt(int);

int main(int argc, char **argv){
	
	
	emulationReached2 =(struct timeval){0};
	sigemptyset(&new);
	sigaddset(&new, SIGINT);
	pthread_sigmask(SIG_BLOCK, &new, NULL);
	if (!My402ListInit(&queue1) && !My402ListInit(&queue2)){
	
		fprintf(stderr,"List could not be initialised \n");
            exit(1);
	}
	int c;
	char *filepath;
	while (1)
    {
      static struct option long_options[] =
        {
          
          {"lambda",required_argument,NULL,'l'},
          {"mu",required_argument,NULL,'m'},
          {0, 0, 0, 0}
          
        };
      /* getopt_long stores the option index here. */
      int option_index = 0;
      c = getopt_long_only(argc, argv,"r:B:P:n:t:",long_options, &option_index);
      /* Detect the end of the options. */
      if (c == -1)
        break;

      switch (c)
        {
        
        case 'l':
        	
            lambda = atof(optarg);
            //fprintf(stdout, "%g\n",lambda );
            break;

        case 'm':
            mu = atof(optarg);
            //fprintf(stdout, "%g\n",mu );
            break;

        case 'r':
            r = atof(optarg);
            //fprintf(stdout, "%g\n",r );
            break;

        case 'B':
            depth = atoi(optarg);
            //fprintf(stdout, "%d\n",depth );
            break;

        case 'P':
            p = atoi(optarg);
            //fprintf(stdout, "%d\n",p );
            break;

        case 'n':
            no_of_packets = atoi(optarg);
            //fprintf(stdout, "%d\n",no_of_packets );
            break;

        case 't':
        	filepath = optarg;
        	trace =1;
        	break;

        case '?':
            //if(optopt == 'n')
            //fprintf (stderr,"Unknown option character `\\x%x'.\n",optopt);
            //fprintf(stderr, "no arg gven\n");
        	exit(0);

        default:
          fprintf(stderr,"wrong input");
          exit(0);
        }
    }

  

  /* Print any remaining command line arguments (not options). */
  if (optind < argc)
    {
      //fprintf (stderr,"incorrect input: %s",argv[optind++]);
    fprintf (stderr,"incorrect input format: Please enter in this format warmup2 [-lambda lambda] [-mu mu] [-r r] [-B B] [-P P] [-n num] [-t tsfile] ");
      exit(0);
      
    }
    if(trace == 1)
		checkForCorrectFileParameters(filepath);
	fprintf(stdout, "Emulation parameters\n");
	fprintf(stdout, "\tnumber to arrive = %d\n",no_of_packets);
	if(trace != 1){
		fprintf(stdout, "\tlambda = %lf\n",lambda);
		if((1/lambda)>10)
			lambda = 0.1;
		fprintf(stdout, "\tmu = %lf\n",mu);
		if((1/mu)>10)
			mu = 0.1;
	}
		
	fprintf(stdout, "\tr = %lf\n",r);
	if((1/r)>10){
		r =0.1;
	}
	fprintf(stdout, "\tB = %d\n",depth);
	if(trace != 1)
		fprintf(stdout, "\tP = %d\n",p);
	if(trace == 1)
		fprintf(stdout, "\ttsfile = %s\n",filepath);
	
	pthread_mutex_init(&mutexVar, NULL);
	pthread_cond_init (&q2_notEmpty_cv, NULL);
	
	gettimeofday(&emulationStart,NULL);
	fprintf(stdout,"%012.3fms: emulation begins\n",gettimeinms(emulationStart,emulationStart));
	emulationReached2 = emulationStart;
		Packet *pckt =NULL;
		pckt = malloc(sizeof(Packet));
	     
	     int res1 = pthread_create(&packet_arrival,NULL,packet_arrival_subroutine,NULL);
         int res2 = pthread_create(&token_deposit,NULL,token_deposit_subroutine,NULL);
         int res3 = pthread_create(&server,NULL,server_subroutine,NULL);
         if (res1 || res2 || res3){
         fprintf(stdout,"ERROR; return code from pthread_create()\n");
         exit(-1);
     }

     	act.sa_handler = interrupt;
		sigaction(SIGINT, &act, NULL);
		pthread_sigmask(SIG_UNBLOCK, &new, NULL);

         pthread_join(packet_arrival,NULL);
         pthread_join(token_deposit,NULL);
         pthread_join(server,NULL);

         gettimeofday(&emulationEnd,NULL);
         if(no_packet_processed == 1 || no_packet_left == 1)
			fprintf(stdout, "%012.3fms: emulation ends\n",gettimeinms(emulationStart,emulationEnd));
         print_statistics();
      
	pthread_exit(NULL);
	return 0;
}

void interrupt(int sig){
	struct timeval remove_time;
	interrupted = 1;
	pthread_cancel(packet_arrival);
	pthread_cancel(token_deposit);
	pthread_cancel(server);
	
	while(My402ListEmpty(&queue1) != TRUE){
		My402ListElem *elem1 = My402ListFirst(&queue1);
		Packet *pckt = (Packet *)elem1->obj;
		My402ListUnlink(&queue1,elem1);
		gettimeofday(&remove_time,NULL);
		fprintf(stdout, "%012.3fms: p%d removed from Q1\n",gettimeinms(emulationStart,remove_time),pckt->packet_no );
		packet_removed++;
	}
	while(My402ListEmpty(&queue2) != TRUE){
		My402ListElem *elem1 = My402ListFirst(&queue2);
		Packet *pckt = (Packet *)elem1->obj;
		My402ListUnlink(&queue2,elem1);
		gettimeofday(&remove_time,NULL);
		fprintf(stdout, "%012.3fms: p%d removed from Q2\n",gettimeinms(emulationStart,remove_time),pckt->packet_no );
		packet_removed++;
	}

}




