#include "stdio.h"
#include "stdlib.h"
#include "my402list.h"
#include "string.h"


int My402ListInit(My402List *list){

	list->anchor.next = &(list->anchor);
	list->anchor.prev = &(list->anchor);
	list->anchor.obj = NULL;
	list->num_members = 0;
	return TRUE;

}



int  My402ListAppend(My402List *list, void *value){

	My402ListElem *elem = NULL;
	elem = malloc(sizeof(My402ListElem));
	
	if(elem == NULL)
		return FALSE;
	if((My402ListEmpty(list)) == TRUE){
		
		elem->prev = &(list->anchor);
		elem->next = &(list->anchor);
		list->anchor.next = elem;
		
		
	}else{
		
		My402ListElem *last = NULL;
		last = My402ListLast(list);
		

		last->next = elem;
		
		elem->prev = last;
		
		elem->next = &(list->anchor);
		
		
	}
	list->anchor.prev = elem;
	elem->obj = value;
	list->num_members = list->num_members + 1;
	return TRUE;
	
}


int  My402ListLength(My402List *list){
	
	return list->num_members;
}

int  My402ListEmpty(My402List *list){

	if((list->num_members) == 0){
		return TRUE;
	}else
		return FALSE;
}



My402ListElem *My402ListFirst(My402List *list){

	if((My402ListEmpty(list)) == TRUE){
		return NULL;
	}else
		return (list->anchor.next);

}

My402ListElem *My402ListLast(My402List* list){

	if((My402ListEmpty(list)) == TRUE){
		
		return NULL;
	}else
		return (list->anchor.prev);

}

My402ListElem *My402ListNext(My402List* list, My402ListElem* elem){

	if((My402ListLast(list)) == elem){
		
		return NULL;
	}else
		return (elem->next);

}

My402ListElem *My402ListPrev(My402List* list, My402ListElem* elem){

	if((My402ListFirst(list)) == elem){
		return NULL;
	}else
		return (elem->prev);

}


My402ListElem *My402ListFind(My402List *list, void *value){
	My402ListElem *elem=NULL;

        for (elem=My402ListFirst(list);
                elem != NULL;
                elem=My402ListNext(list, elem)){
        	if(value == elem->obj){
        		return elem;
        	}
        }
        return NULL;
}





int  My402ListPrepend(My402List *list, void *value){
	
	My402ListElem *elem = NULL;
	My402ListElem *tmp = NULL;
	elem = malloc(sizeof(My402ListElem));
	if(elem == NULL)
		return FALSE; 
	tmp = My402ListFirst(list);
	if(tmp == NULL){
		elem->prev = &(list->anchor);
		elem->next = &(list->anchor);
		list->anchor.next = elem;
		list->anchor.prev = elem;
	}else{
		elem->prev = &(list->anchor);
		elem->next = tmp;
		tmp->prev = elem;
		list->anchor.next = elem;
	}
	
	
	elem->obj = value;
	list->num_members = list->num_members + 1;
	return TRUE;
}

void My402ListUnlink(My402List *list, My402ListElem *elem){

	if(list->anchor.next == elem){
		elem->next->prev = &(list->anchor);
		list->anchor.next = elem->next;
	}else if (list->anchor.prev == elem)
	{
		elem->prev->next = &(list->anchor);
		list->anchor.prev = elem->prev;
	}
	else{
		elem->prev->next = elem->next;
		elem->next->prev = elem->prev;
	}
	list->num_members = list->num_members - 1;
	free(elem);
}

void My402ListUnlinkAll(My402List* list){
	int len = 0;
	int i = 0;
	My402ListElem *elem = NULL;
	len = list->num_members;
	for(i = 1; i <= len; i++){
		elem = My402ListFirst(list);
		My402ListUnlink(list,elem);
	}
}

int  My402ListInsertAfter(My402List *list, void *value, My402ListElem *elem){
	
	if(elem == NULL){

		
		return My402ListAppend(list,value);

	}else{
		
		My402ListElem *tmp = malloc(sizeof(My402ListElem));
		if(tmp == NULL)
			return FALSE;
		elem->next->prev = tmp;
		tmp->next = elem->next;
		
		tmp->prev = elem;
		
		elem->next = tmp; 
		tmp->obj = value;
		list->num_members = list->num_members + 1;
		
		return TRUE;

	}
	

}

int  My402ListInsertBefore(My402List *list, void *value, My402ListElem *elem){
	if(elem == NULL){

		return My402ListPrepend(list,value);
	}else{

		My402ListElem *tmp = NULL;
		tmp = malloc(sizeof(My402ListElem));
		if(tmp == NULL)
			return FALSE;
		tmp->next = elem;
		tmp->prev = elem->prev;
		elem->prev->next = tmp;
		elem->prev = tmp; 
		tmp->obj = value;
		list->num_members = list->num_members + 1;
		return TRUE;
	}
	
}


	



