package applisem.client;

/**
 * Unused Class implementing a blocking Queue
 * 
 * THis is unused because of the Existance of the same Container in standard java fact unknown by MME author of this class
 * 
 * @author LBO
 */
public class BlockingQueue extends Object {
  private Object[] queue;
  private int capacity;
  private int size;
  private int head;
  private int tail;

  public BlockingQueue(int capacity) {
  this.capacity = Math.max(1, capacity);
  queue = new Object[capacity];
  head = 0; 
  tail = 0;	
  size = 0;
  }

  public int getCapacity() { return capacity; }
  public synchronized int getSize(){ return size;}
  
  public synchronized boolean isEmpty(){return size==0;}
  public synchronized boolean isFull(){return size==capacity;}
    
  /**----------------------------------------------------*/
  /** ajouter un nouveau thread a la fin de la file      */
  /**----------------------------------------------------*/
  public synchronized void addTask(Object obj) 
  throws InterruptedException {
  //tant que la file est pleine, on peut pas ajouter,il faut attendre
  waitWhileFull();
  queue[head] = obj;   
  head =(head+1)%capacity;
  size++;
  notifyAll();
  }

  /**----------------------------------------------------*/
  /** ajouter plusieurs threads(empiler)                 */
  /**----------------------------------------------------*/
  public synchronized void addTasks(Object[] tasks) 
  throws InterruptedException {
  for (int i=0; i<tasks.length; i++){
    addTask(tasks[i]);
    }
  }
	
  /**----------------------------------------------------
   * elle bloque (wait()) en attente d�un notify() ou    *
   * notifyAll(). lorsqu'elle le recoit elle retouren    *
   * le premier                                          *
   *----------------------------------------------------*/
  public synchronized Object removeTask() 
  throws InterruptedException {
  //tant que la file est vide, on ne peut pas supprimer, il faut attendre
  waitWhileEmpty();   
  Object task = queue[tail]; 
  queue[tail] = null;  //appel GC 
  tail = (tail+1)%capacity;
  size--;
  notifyAll(); 
  return task;
  }

  /**----------------------------------------------------*/
  /** supprimer et retourner tous les threads            */
  /**----------------------------------------------------*/
  public synchronized Object[] removeAllTasks() 
  throws InterruptedException {
  Object[] rm_tasks = new Object[size]; 
  for (int i=0; i<rm_tasks.length; i++){
	rm_tasks[i] = removeTask();
	}
  return rm_tasks; 
  }

  /**----------------------------------------------------*/
  /** supprimer au moins un thread                       */
  /**----------------------------------------------------*/
  public synchronized Object[] removeAtLeastOne() 
  throws InterruptedException {
  waitWhileEmpty(); 
  return removeAllTasks();
  }
   
  /**----------------------------------------------------*/
  /**  mettre le thread courant en attende.              */
  /**  mais seulement un timeout                         */
  /**----------------------------------------------------*/
  public synchronized boolean waitUntilEmpty(long timeout) 
  throws InterruptedException {
  if(timeout==0L){
    waitUntilEmpty();
	return true;
	}
  long end_time = System.currentTimeMillis() + timeout;
  long remaining = timeout;
  while(!isEmpty()&&(remaining > 0L)) {
	wait(remaining);
	remaining = end_time - System.currentTimeMillis();
	}
  return isEmpty();
  }

  /**----------------------------------------------------*/
  /** mettre le thread courant en attente tant que la    */
  /** file n'est pas encore vide.                        */
  /**----------------------------------------------------*/
  public synchronized void waitUntilEmpty() 
  throws InterruptedException {
  while (!isEmpty()){
	wait();// mettre le thread courant en attente 
	}
  }

  /**----------------------------------------------------*/
  /** mettre le thread courant en attente tant que la    */
  /** file est vide                                      */
  /**----------------------------------------------------*/
  public synchronized void waitWhileEmpty() 
  throws InterruptedException {
  while (isEmpty()){
    wait();
	}
  }
	
  /**----------------------------------------------------*/
  /** mettre en attente le thread courant tant que       */ 
  /** la file n'est pas encore pleine.                   */
  /**----------------------------------------------------*/
  public synchronized void waitUntilFull() 
  throws InterruptedException {
  while (!isFull()) {
	wait();
	}
  }

  /**----------------------------------------------------*/
  /** mettre le thread courant en attent tant que        */
  /** la file est est pleine                             */
  /**----------------------------------------------------*/
  public synchronized void waitWhileFull() 
  throws InterruptedException {
  while(isFull()) {
	wait();
	}
  }
   
  /**----------------------------------------------------*/
  /** on considere qu'une file bloquante peut etre fermee*/ 
  /** (close). Toute operation addTask ou RemoveTask sur */ 
  /** une file fermeee leve l'exception ClosedException. */
  /**----------------------------------------------------*/
  //public synchronized void close ();
}

