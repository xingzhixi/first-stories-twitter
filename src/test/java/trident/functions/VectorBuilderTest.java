package trident.functions;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import backtype.storm.Config;
import backtype.storm.tuple.Values;
import entities.SparseVector;
import entities.Tweet;

public class VectorBuilderTest extends TestCase {
	
	VectorBuilder vb;
    public VectorBuilderTest(String vbr)
    {
        super(vbr);
        vb = new VectorBuilder();
        Properties prop = new Properties();
        try {
			prop.load(new FileInputStream("config.properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Config conf = new Config();
        conf.put("PATH_TO_OOV_FILE", "oov.txt");
        conf.put("UNIQUE_WORDS_EXPECTED",
				prop.getProperty("UNIQUE_WORDS_EXPECTED"));
        vb.prepare(conf, null);
    }
    
    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( VectorBuilderTest.class );
    }
    
    public void testNormalizeVector(){
    	Tweet t = new Tweet(123L, "Test Normalize v3ctor");
    	double[] values = {1.0, 2.0};
    	SparseVector sp = new SparseVector(values);
    	SparseVector normalized = vb.normalizeVector(sp);
    	assertEquals(0.4472135954999579, normalized.get(0));
    	assertEquals(0.8944271909999159, normalized.get(1));
    }
    
    public void testGetValues() throws Exception{
    	Method method = vb.getClass().getDeclaredMethod("getValues",Tweet.class, String[].class);
    	method.setAccessible(true);
    	Tweet t = new Tweet(1L, "cat jumps over");
    	double[] values = {1.0, 2.0};
    	t.setSparseVector(new SparseVector(values));
    	String[] words = {"cat", "jumps", "over"};
    	Values v = (Values) method.invoke(vb, t, words);
    	assertEquals(3, v.get(1));	//unique words increase
    }

}
