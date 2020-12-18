public class test implements Runnable{
	private static int value;
	private static int id;
	
	test(int i){
		this.id = i;
	}
	    public static void main(String[] args){
	    	test tst1 = new test(1);
	    	test tst2 = new test(2);
	    	test tst3 = new test(3);
	    	
			new Thread(tst1).start();
			new Thread(tst2).start();
			new Thread(tst3).start();
		    
			Thread.sleep(3);
	    }

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("");
		}
	     
}
