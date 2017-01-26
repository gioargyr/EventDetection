package utils.luceneService;

public class SingletonFuzzy {
	private static FuzzySearch fs = null;

	private static SingletonFuzzy instance = null;

	public static SingletonFuzzy getInstance() {
		
		if (instance == null) {
			String filePath = utils.Constants.outputFolder+"/gadm28.csv";
					
			try {
				fs = new FuzzySearch(filePath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			synchronized (SingletonFuzzy.class) {
				if (instance == null) {
					instance = new SingletonFuzzy();
				}
			}
		}
		return instance;
	}

	public FuzzySearch getFs() {
		return fs;
	}
	public static synchronized  void destroyFuzzy() {
		fs=null;
	}
}
