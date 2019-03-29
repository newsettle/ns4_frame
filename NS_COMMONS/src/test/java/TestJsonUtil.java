import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.creditease.framework.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;


public class TestJsonUtil {
	
	public static void main(String[] args) throws Exception 
	{
//		String json = JsonUtil.jsonFromObject(new AAA());
//		System.out.println("json:"+json);
//		Map<String,Object> map = JsonUtil.objectFromJson(json,new TypeReference<HashMap<String, Object>>() {
//		});
//		
//		
//		Iterator<String> it =  map.keySet().iterator();
//		while (it.hasNext()) 
//		{
//			String string = (String) it.next();
//			Object o = map.get(string);
//			
//			
//			String ss = JsonUtil.jsonFromObject(o);
//			System.out.println(ss);
//			
//		}
		
		
		String s = "{\"retCode\":\"A000\", \"retInfo\":\"开用户,账户成功！\",\"data\":{\"accList\":[{\"accountAttr\":\"1\",\"accountId\":\"CE000011115210200000358\",\"status\":0,\"executeTime\":\"20151216164649\",\"cifId\":\"52102\",\"currency\":\"CNY\"}],\"extend1\":\"扩展1_19183\",\"extend3\":\"扩展3_19183\",\"extend2\":\"扩展2_19183\",\"nsUserId\":\"CE000011112015121600000105\"}}";
		
		Map map	= JsonUtil.objectFromJson(s,HashMap.class);
		System.out.println(map);
		
		
	}
	
	static class AAA
	{
		private String aa = "1";
		private int bb;
		private List<String> lst = new ArrayList<String>();
		private Map<String,String> map = new HashMap<String, String>();
		
		public AAA() {
			// TODO Auto-generated constructor stub
			
			lst.add("32342");
			lst.add("34kj345lwesrfk");
			map.put("1", "1");
			map.put("2", "2");
		}
	}
}


