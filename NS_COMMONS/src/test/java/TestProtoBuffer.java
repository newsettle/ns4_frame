import io.protostuff.Input;
import io.protostuff.MapSchema;
import io.protostuff.Output;
import io.protostuff.Pipe;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.creditease.framework.util.ProtoStuffSerializeUtil;


public class TestProtoBuffer {
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException 
	{
		Map<String,String> mm = new HashMap<String,String>();
		mm.put("2222", "3233423");
		System.out.printf("mm:%s\n",mm);
		byte[] bs = ProtoStuffSerializeUtil.serializeForCommon(mm);
		System.out.println("bslength:"+bs.length);
		Map<String,String> map= (Map<String,String>)ProtoStuffSerializeUtil.unSerializeForCommon(bs);
		System.out.printf("mm1:%s\n",map);
		
		bs =	ProtoStuffSerializeUtil.serializeForCommon(TestEnum.AAAA);
		TestEnum testEnum = (TestEnum)ProtoStuffSerializeUtil.unSerializeForCommon(bs);
		System.out.println(testEnum.equals(TestEnum.AAAA));
		System.out.println(testEnum.equals(TestEnum.BBBB));
		
		
		
		
	}
	
	static enum TestEnum
	{
		AAAA("wo我223"),
		BBBB("呸呸呸呸我223");
		private String message;
		private TestEnum(String message) {
			this.message = message;
		}
	}
}
