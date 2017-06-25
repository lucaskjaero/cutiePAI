import jep.Jep;
import jep.JepException;


public class Predictor {
	private Jep jep;
	
	public Predictor() {
	    try {
	    	this.jep = new Jep(false);
			jep.eval("from model import Model");
			jep.eval("predictor = Model()");
		} catch (JepException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	    
	
	public String predict() {
	    try {
			this.jep.eval("result = predictor.predict()");
			String result = (String) this.jep.getValue("result");
		    return result;
		} catch (JepException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
	
	public static void main() {
		Predictor model = new Predictor();
		String prediction = model.predict();
		System.out.println(prediction);
	}
}
