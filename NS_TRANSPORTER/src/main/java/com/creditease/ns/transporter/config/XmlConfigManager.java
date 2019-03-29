package com.creditease.ns.transporter.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.creditease.framework.util.FileUtils;
import com.creditease.framework.util.XMLUtil;
import com.creditease.framework.work.Worker;
import com.creditease.ns.chains.context.GlobalScope;
import com.creditease.ns.chains.def.CatalogDef;
import com.creditease.ns.chains.def.ElementDef;
import com.creditease.ns.framework.spring.SpringPlugin;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.transporter.constants.TransporterConstants;
import com.creditease.ns.transporter.context.XmlAppTransporterContext;

public class XmlConfigManager implements LifeCycle, ConfigManager {
    private static NsLog frameLog = NsLog.getFramLog("Transport", "XmlConfigManager");
    private boolean isStarted;
    private boolean isLoaded;
    private String resourcePath;
    private Map<String, InQueueInfo> queueNameToQueueInfos = new LinkedHashMap<String, InQueueInfo>();
    private final static String DEFAULT_RESOURCE_LOCATION = "nstransporter.xml";
    public static int DEFAULT_BUFFERSIZE = 100;
    public static int DEFAULT_HANDLER_NUM = 10;
    private Document doc;

    private static XmlConfigManager self = new XmlConfigManager();

    private XmlAppTransporterContext xmlAppTransporterContext = null;
    private boolean isSpring;

    public void init() {
        self = this;
    }

    @Override
    public synchronized void loadConfig() {
        long startTime = System.currentTimeMillis();
        if (!isLoaded) {
            if (resourcePath == null) {
                resourcePath = DEFAULT_RESOURCE_LOCATION;
            }

            String configPath = FileUtils.convertToAbsolutePath(resourcePath);

            try {
                doc = XMLUtil.load(configPath);
                try {
                    loadInQueueInfos();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
                frameLog.debug("# 加载配置文件成功 resourcePath:{} configPath:{} #",
                        resourcePath, configPath, System.currentTimeMillis() - startTime);
            } catch (Exception e) {
                frameLog.error("# 加载配置文件失败 出现异常 resourcePath:{} configPath:{} #",
                        resourcePath, configPath, System.currentTimeMillis() - startTime, e);
                throw new RuntimeException("加载配置文件" + resourcePath + "出错");
            }

            isLoaded = true;
        }

    }

    @Override
    public synchronized void startUp() {
        if (!isStarted) {
            loadConfig();
            isStarted = true;
        }

    }

    public XmlConfigManager() {

    }

    public static ConfigManager getInstance() {
        return self;
    }

    /**
     * <queues>
     * <prefix></prefix>
     * <launchers>
     * <launcher>
     * <class name="类的全名" method="method方法名" property="" />
     * <class name="类的全名" static-method="method方法名" />
     * </launcher>
     * </launchers>
     * <exchangekeys>
     * <exchangekey></exchangekey>
     * <exchangekey></exchangekey>
     * <exchangekeys/>
     * <inqueues>
     * <queue>
     * <name></name>
     * <fetchernum>1</fetchernum>
     * <buffersize></buffersize>
     * <handlersize></handlersize>
     * <serviceClass></serviceClass>
     * <sendernum>1</sendernum>
     * <exceptionListener></exceptionListener>
     * </queue>
     * <queue>
     * <name></name>
     * <buffersize></buffersize>
     * <handlersize></handlersize>
     * <serviceClass></serviceClass>
     * </queue>
     * </inqueues>
     * </queues>
     *
     * @return
     * @throws Exception
     */

    private void loadInQueueInfos() throws Exception {
        Element root = doc.getDocumentElement();
        //解析prefix
        Element prefix = XMLUtil.getStrictChildByName(root, "prefix");
        String queuePrefix = "";
        if (prefix != null) {
            XMLUtil.getText(prefix, null);
        }

        Element launchersElement = XMLUtil.getStrictChildByName(root, "launchers");
        SpringPlugin springPlugin = null;
        if (launchersElement != null) {
            Element[] launchers = XMLUtil.getChildrenByName(launchersElement, "launcher");

            if (launchers.length == 0) {
                frameLog.error("# 解析配置文件失败 launchers元素下必须配置至少一个launcher元素 root:{} launchersElement:{} #", root, launchersElement);
                throw new Exception("配置文件配置错误，必须在launchers下配置launcher的信息!");
            }

            for (int i = 0; i < launchers.length; i++) {
                Element launcherElement = launchers[i];

                if (launcherElement == null) {
                    frameLog.error("# 解析配置文件失败,launcher信息配置有误 root:{} launchersElement:{} #", root, launchersElement);
                    throw new Exception("配置文件配置错误，必须在launchers下配置完整的launcher的信息!");
                }

                Element classElement = XMLUtil.getStrictChildByName(launcherElement, "class");

                if (classElement == null) {
                    frameLog.error("# 解析配置文件失败,launcher元素下必须指定一个class元素 root:{} launchersElement:{} #", root, launchersElement);
                    throw new Exception("配置文件配置错误，必须在launcher下配置完整的class的信息!");
                }

                String launcherClassName = classElement.getAttribute("name");

                if (launcherClassName == null || launcherClassName.trim().length() < 1) {
                    frameLog.error("# 解析配置文件失败,class元素必须指定name属性 root:{} launchersElement:{} launcherElement:{} #", root, launchersElement, launcherElement);
                    throw new Exception("配置文件配置错误，launcher必须指定对应的class!");
                }

                Class cl = null;
                try {
                    cl = Class.forName(launcherClassName);
                } catch (ClassNotFoundException e) {
                    frameLog.error("# 解析配置文件失败,没有找到对应的class root:{} launchersElement:{} launcherElement:{} className:{} #", root, launchersElement, launcherElement, launcherClassName);
                    throw new Exception("配置文件配置错误，没有找到对应的class" + launcherClassName + "!");
                }


                String methodName = classElement.getAttribute("method");
                if (methodName == null || methodName.trim().length() < 1) {
                    methodName = classElement.getAttribute("static-method");
                    if (methodName == null || methodName.trim().length() < 1) {
                        frameLog.error("# 解析配置文件失败,class元素必须指定method属性或者static-method属性 root:{} launchersElement:{} launcherElement:{} className:{} #", root, launchersElement, launcherElement, launcherClassName);
                        throw new Exception("配置文件配置错误，没有找到对应的class" + launcherClassName + "的配置的method!");
                    }

                    checkAndExecuteMethod(root, launchersElement, launcherElement,
                            launcherClassName, cl, methodName, null);
                } else {
                    Object o = null;
                    try {
                        Constructor[] constructors = cl.getDeclaredConstructors();
                        for (Constructor constructor : constructors) {
                            Class[] cls = constructor.getParameterTypes();
                            if (cls == null || cls.length < 1) {
                                constructor.setAccessible(true);
                                o = constructor.newInstance();
                                break;
                            }
                        }

                        if (o == null) {
                            throw new Exception(cl.getName() + " 无参构造函数!");
                        }

                        if (o instanceof SpringPlugin) {
                            isSpring = true;
                            springPlugin = (SpringPlugin) o;
                        }
                    } catch (Exception e) {
                        frameLog.error("# 解析配置文件失败,指定的class没有对应的无参构造函数 root:{} launchersElement:{} launcherElement:{} className:{} #", root, launchersElement, launcherElement, launcherClassName, e);
                        throw new Exception("没有找到对应的class" + launcherClassName + "的无参构造函数!");
                    }


                    checkAndExecuteMethod(root, launchersElement, launcherElement,
                            launcherClassName, cl, methodName, o);
                }


            }
        }


        Element inqueuesElement = XMLUtil.getStrictChildByName(root, "inqueues");

        if (inqueuesElement == null) {
            frameLog.error("# 解析配置文件失败,没有配置inqueues元素 root:{} inqueuesElement:{} #", root, inqueuesElement);
            throw new Exception("配置文件配置错误，必须配置inqueues元素的信息!");
        }

        Element[] inqueues = XMLUtil.getChildrenByName(inqueuesElement, "queue");
        if (inqueues.length == 0) {
            frameLog.error("# 解析配置文件失败,inqueues元素下必须配置至少一个queue元素 root:{} inqueuesElement:{} #", root, inqueuesElement);
            throw new Exception("配置文件配置错误，必须配置inqueue的信息!");
        }

        for (Element e : inqueues) {
            String refCatalogId = e.getAttribute("refCatalog");

            Element nameElement = XMLUtil.getStrictChildByName(e, "name");

            if (nameElement == null) {
                frameLog.error("# 解析配置文件失败,queue元素下必须指明name元素 root:{} inqueuesElement:{} e:{} #", root, inqueuesElement, e);
                throw new Exception("配置文件配置错误，必须配置对应queue的名称!");
            }
            String queueName = XMLUtil.getValueAsString(nameElement, null);
            if (queuePrefix != null && queuePrefix.trim().length() > 0) {
                queueName = queuePrefix + "_" + queueName;
            }
            if (queueName == null || queueName.trim().length() < 1) {
                frameLog.error("# 解析配置文件失败,name元素必须有值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} #", root, inqueuesElement, e, nameElement, queueName);
                throw new Exception("配置文件配置错误，必须配置对应queue的名称!");
            }


            if (queueNameToQueueInfos.containsKey(queueName)) {
                frameLog.error("# 解析配置文件失败,发现name元素值重复 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} #", root, inqueuesElement, e, nameElement, queueName);
                throw new Exception("配置文件配置错误，队列名称" + queueName + "不能重复!");
            }

            int buffersize = DEFAULT_BUFFERSIZE;
            Element bufferSizeElement = XMLUtil.getStrictChildByName(e, "buffersize");

            if (bufferSizeElement != null) {
                String buffsize = XMLUtil.getValueAsString(bufferSizeElement, null);
                if (buffsize != null && buffsize.trim().length() > 0) {
                    try {
                        buffersize = Integer.parseInt(buffsize);

                        if (buffersize > 1000000) {
                            buffersize = 1000000;
                            frameLog.debug("# 解析配置文件,解析buffersize元素 出现大于1000000的buffersize配置 使用最大值1000000 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} buffsize:{} buffersize:{} #", root, inqueuesElement, e, nameElement, queueName, buffsize, buffersize);
                        } else {
                            if (buffersize <= 0) {
                                buffersize = DEFAULT_BUFFERSIZE;
                                frameLog.debug("# 解析配置文件,解析buffersize元素 出现小于1的buffersize配置 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} buffsize:{} buffersize:{} #", root, inqueuesElement, e, nameElement, queueName, buffsize, buffersize);
                            }
                        }
                    } catch (Exception e2) {
                        frameLog.debug("# 解析配置文件失败,解析buffersize元素出现异常 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} buffsize:{} buffersize:{} #", root, inqueuesElement, e, nameElement, queueName, buffsize, buffersize, e2);
                    }
                } else {
                    frameLog.debug("# 解析配置文件,没有配置buffersize 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} buffsize:{} buffersize:{} #", root, inqueuesElement, e, nameElement, queueName, buffsize, buffersize);
                }
            } else {
                frameLog.debug("# 解析配置文件,没有配置buffersize 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} buffersize:{} #", root, inqueuesElement, e, nameElement, queueName, buffersize);
            }

            int handlerSize = DEFAULT_HANDLER_NUM;
            Element handlerSizeElement = XMLUtil.getStrictChildByName(e, "handlersize");

            if (handlerSizeElement != null) {
                String handlesize = XMLUtil.getValueAsString(handlerSizeElement, null);
                if (handlesize != null && handlesize.trim().length() > 0) {
                    handlerSize = Integer.parseInt(handlesize);
                    try {

                        if (handlerSize > 100) {
                            handlerSize = 100;
                            frameLog.debug("# 解析配置文件,解析handlersize元素 出现大于100的handlerSize配置 使用最大值100 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} handlesize:{} handlerSize:{} #", root, inqueuesElement, e, nameElement, queueName, handlesize, handlerSize);
                        } else {
                            if (handlerSize <= 0) {
                                handlerSize = DEFAULT_HANDLER_NUM;
                                frameLog.debug("# 解析配置文件,解析handlersize元素 出现小于1的buffersize配置 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} handlesize:{} handlerSize:{} #", root, inqueuesElement, e, nameElement, queueName, handlesize, handlerSize);
                            }
                        }
                    } catch (Exception e2) {
                        frameLog.debug("# 解析配置文件,解析handlersize元素 失败 出现异常 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} handlesize:{} handlersize:{} #", root, inqueuesElement, e, nameElement, queueName, handlesize, handlerSize, e2);
                    }
                } else {
                    frameLog.debug("# 解析配置文件,解析handlersize元素 没有配置handlersize元素 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} handlesize:{} handlersize:{} #", root, inqueuesElement, e, nameElement, queueName, handlesize, handlerSize);
                }
            } else {
                frameLog.debug("# 解析配置文件 没有配置handlersize 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} handlersize:{} #", root, inqueuesElement, e, nameElement, queueName, handlerSize);
            }

            //解析fetcherNum和senderNum
            Element fetcherNumElement = XMLUtil.getStrictChildByName(e, "fetchernum");
            int fetcherNum = 1;
            if (fetcherNumElement != null) {
                String fetchernum = XMLUtil.getValueAsString(fetcherNumElement, null);
                if (fetchernum != null && fetchernum.trim().length() > 0) {
                    fetcherNum = Integer.parseInt(fetchernum);
                    try {

                        if (fetcherNum > 100) {
                            fetcherNum = 100;
                            frameLog.debug("# 解析配置文件 解析fetchernum元素 出现大于100的fetchernum配置 使用最大值100 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} fetchernum:{} fetcherNum:{} #", root, inqueuesElement, e, nameElement, queueName, fetchernum, fetcherNum);
                        } else {
                            if (fetcherNum <= 0) {
                                fetcherNum = 1;
                                frameLog.debug("# 解析配置文件 解析fetchernum元素 出现小于1的buffersize配置 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} fetchernum:{} fetcherNum:{} #", root, inqueuesElement, e, nameElement, queueName, fetchernum, fetcherNum);
                            }
                        }
                    } catch (Exception e2) {
                        frameLog.debug("# 解析配置文件 解析fetchernum元素 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} fetchernum:{} fetcherNum:{} #", root, inqueuesElement, e, nameElement, queueName, fetchernum, fetcherNum, e2);
                    }
                } else {
                    frameLog.debug("# 解析配置文件 解析fetchernum元素 配置异常 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} fetchernum:{} fetcherNum:{} #", root, inqueuesElement, e, nameElement, queueName, fetchernum, fetcherNum);
                }
            } else {
                frameLog.debug("# 解析配置文件 没有配置fetchernum 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} fetchernum:{} #", root, inqueuesElement, e, nameElement, queueName, fetcherNum);
            }


            Element senderNumElement = XMLUtil.getStrictChildByName(e, "sendernum");
            int senderNum = 1;
            if (senderNumElement != null) {
                String sendernum = XMLUtil.getValueAsString(senderNumElement, null);
                if (sendernum != null && sendernum.trim().length() > 0) {
                    senderNum = Integer.parseInt(sendernum);
                    try {

                        if (senderNum > 100) {
                            senderNum = 100;
                            frameLog.debug("# 解析配置文件 解析sendernum元素 出现大于100的senderNum配置 使用最大值100 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} sendernum:{} senderNum:{} #", root, inqueuesElement, e, nameElement, queueName, sendernum, senderNum);
                        } else {
                            if (senderNum <= 0) {
                                senderNum = 1;
                                frameLog.debug("# 解析配置文件 解析sendernum元素 出现小于1的buffersize配置 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} sendernum:{} senderNum:{} #", root, inqueuesElement, e, nameElement, queueName, sendernum, senderNum);
                            }
                        }
                    } catch (Exception e2) {
                        frameLog.debug("# 解析配置文件 解析sendernum元素 失败 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} sendernum:{} senderNum:{} #", root, inqueuesElement, e, nameElement, queueName, sendernum, senderNum, e2);
                    }
                } else {
                    frameLog.debug("# 解析配置文件 解析sendernum元素 配置异常 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} sendernum:{} senderNum:{} #", root, inqueuesElement, e, nameElement, queueName, sendernum, senderNum);
                }
            } else {
                frameLog.debug("# 解析配置文件 没有配置sendernum 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} sendernum:{} #", root, inqueuesElement, e, nameElement, queueName, senderNum);
            }


            String serviceClassName = TransporterConstants.DEFAULT_TRANSPORTER_SERVICECLASSNAME;    //当使用spring时，由于默认的service是没有配置spring的此时在handler中获取实例的时候是获取不到的
            Element serviceClassElement = XMLUtil.getStrictChildByName(e, "serviceClass");

            if (refCatalogId == null || refCatalogId.trim().length() < 1) {
                if (serviceClassElement == null) {
                    frameLog.error("# 解析配置文件失败,没有配置serviceClass元素 root:{} inqueuesElement:{} e:{} #", root, inqueuesElement, e);
                    throw new Exception("配置文件配置错误，必须配置对应queue的serviceClass!");
                }
                serviceClassName = XMLUtil.getValueAsString(serviceClassElement, null);

                if (serviceClassName == null || serviceClassName.trim().length() < 1) {
                    frameLog.error("# 解析配置文件失败,没有指定serviceClass元素的值 root:{} inqueuesElement:{} e:{} nameElement:{} serviceClass:{} #", root, inqueuesElement, e, serviceClassElement, serviceClassName);
                    throw new Exception("配置文件配置错误，必须配置对应serviceClass的名称!");
                }
            } else {
                if (serviceClassElement != null) {

                    frameLog.error("# 解析配置文件失败,指定了refCatalog不能配置serviceClass元素 root:{} inqueuesElement:{} e:{} #", root, inqueuesElement, e);
                    throw new Exception("配置文件配置错误，指定了refCatalog就不能配置自定义的serviceClass!");
                }

                //检查catalogId是否存在
                checkCatalogId(refCatalogId);

            }

//			Element exceptionListenerClassNameElement = XMLUtil.getStrictChildByName(e, "exceptionListener");
//			String exceptionClassName = null;
//			if (exceptionListenerClassNameElement != null) 
//			{
//				exceptionClassName = XMLUtil.getValueAsString(exceptionListenerClassNameElement, null);
//				if (exceptionClassName == null || exceptionClassName.trim().length() < 1) 
//				{
//					frameLog.error("加载inqueue信息 失败 没有配置exceptionClass的名称 root:{} inqueuesElement:{} e:{} nameElement:{} serviceClass:{}", root,inqueuesElement,e,serviceClassElement,serviceClassName);
//					throw new Exception("配置文件配置错误，必须配置对应exceptionClass的名称!");
//				}
//				
//				//检查class是否存在
//				Class cl = null;
//				try
//				{
//					Class.forName(exceptionClassName);
//				}
//				catch (ClassNotFoundException e1) {
//					frameLog.error("检查配置的exceptionClass 失败 没有对应的class queueName:{} exceptionListenerClassName:{}", queueName,exceptionClassName,e1);
//					throw new RuntimeException("配置的exceptionClass"+exceptionClassName+"没有对应的classs");
//				}
//				
//				//检查是否实现了exceptionListener
//				boolean isExceptionListener =	ExceptionListener.class.isAssignableFrom(cl);
//				if (!isExceptionListener) 
//				{
//					frameLog.error("检查配置的exceptionClass 失败 没有实现ExceptionListener接口 queueName:{} exceptionListenerClassName:{}", queueName,exceptionClassName);
//					throw new RuntimeException("配置的exceptionClass"+exceptionClassName+"必须实现ExceptionListener接口");
//				}
//				
//				boolean hasNoParamConstrutctor = false;
//				Constructor constructors = cl.getConstructors();
//				for (int i = 0; i < constructors.length; i++) 
//				{
//					Class cls = constructorsi.getParameterTypes();
//					if (cls.length == 0) 
//					{
//						hasNoParamConstrutctor = true;
//						break;
//					}
//				}
//				
//				if (!hasNoParamConstrutctor) 
//				{
//					frameLog.error("检查配置的exceptionClass 失败 没有无参构造函数 queueName:{} exceptionClass:{}", queueName,exceptionClassName);
//					throw new RuntimeException("exceptionClass"+exceptionClassName+"没有默认的无参构造函数，请添加!");
//				}
//				
//				ExceptionListener listener = (ExceptionListener)cl.newInstance();
//				xmlAppTransporterContext.registerExceptionListener(queueName, listener);
//				
//			}


            String key = queueName;

            InQueueInfo inQueueInfo = new InQueueInfo();
            inQueueInfo.setQueueName(queueName);
            inQueueInfo.setBufferSize(buffersize);
            inQueueInfo.setHandlerNum(handlerSize);
            inQueueInfo.setServiceClassName(serviceClassName.trim());
            inQueueInfo.setFetcherNum(fetcherNum);
            inQueueInfo.setSenderNum(senderNum);
            if (refCatalogId != null && refCatalogId.trim().length() > 0) {
                inQueueInfo.setRefCatalogId(refCatalogId);
            }
//			if (exceptionClassName != null) 
//			{
//				inQueueInfo.setExceptionListenerClassName(exceptionClassName);
//			}

            if (springPlugin != null) {
                inQueueInfo.setSpringPlugin(springPlugin);
            }

            queueNameToQueueInfos.put(key, inQueueInfo);
            //检查所有的service的注解和构造函数和注解的方法中是否有servicemessage参数
            checkServiceClass();

            frameLog.debug("# 解析配置文件 加载队列{}配置 成功 buffersize:{} handlersize:{} serviceClassName:{} #", queueName, buffersize, handlerSize, serviceClassName);

        }


    }

    private void checkAndExecuteMethod(Element root, Element launchersElement,
                                       Element launcherElement, String launcherClassName, Class cl,
                                       String methodName, Object target) throws Exception, IllegalAccessException,
            InvocationTargetException {

        Method[] methods = cl.getDeclaredMethods();
        Method targetMethod = null;
        for (Method m : methods) {
            if (methodName.equals(m.getName())) {
                if (targetMethod == null) {
                    targetMethod = m;
                } else {
                    frameLog.error("# 解析配置文件失败,launcher的class的启动方法不能有重名 root:{} launchersElement:{} launcherElement:{} className:{} method:{} #", root, launchersElement, launcherElement, launcherClassName, methodName);
                    throw new Exception("配置文件配置错误，class" + launcherClassName + "的配置的method" + methodName + "有重名方法!");
                }
            }
        }

        if (targetMethod == null) {
            frameLog.error("# 解析配置文件失败,launcher的class中没有找到启动方法 root:{} launchersElement:{} launcherElement:{} className:{} method:{} #", root, launchersElement, launcherElement, launcherClassName, methodName);
            throw new Exception("配置文件配置错误，class" + launcherClassName + "的配置的method" + methodName + "没有找到!");
        }

        targetMethod.setAccessible(true);
        targetMethod.invoke(target);
    }


    private void checkServiceClass() {
        //检查所有的service的注解和构造函数和注解的方法中是否有servicemessage参数
        for (Iterator<String> iterator = queueNameToQueueInfos.keySet().iterator(); iterator.hasNext(); ) {
            String queueName = iterator.next();

            InQueueInfo inQueueInfo = queueNameToQueueInfos.get(queueName);
            String serviceName = inQueueInfo.getServiceClassName();
            try {
                boolean hasNoParamConstrutctor = false;
                Class cl = Class.forName(serviceName);
                Constructor[] constructors = cl.getConstructors();
                for (int i = 0; i < constructors.length; i++) {
                    Class[] cls = constructors[i].getParameterTypes();
                    if (cls.length == 0) {
                        hasNoParamConstrutctor = true;
                        break;
                    }
                }

                if (!hasNoParamConstrutctor) {
                    frameLog.error("检查配置的Service 失败 没有无参构造函数 queueName:{} serviceName:{}", queueName, serviceName);
                    throw new RuntimeException("service:" + serviceName + "没有默认的无参构造函数，请添加!");
                }

                //检查是否实现了defaultWorker接口
                if (!Worker.class.isAssignableFrom(cl)) {
                    frameLog.error("检查配置的Service 失败 serviceClass没有实现worker接口 queueName:{} serviceName:{}", queueName, serviceName);
                    throw new RuntimeException("service:" + serviceName + "没有实现defaultWorker接口，请实现!");
                }

            } catch (ClassNotFoundException e) {
                frameLog.error("检查配置的Service 失败 没有对应的class queueName:{} serviceName:{}", queueName, serviceName, e);
                throw new RuntimeException("配置的serviceclass:" + serviceName + "没有对应的classs");
            }


        }
    }

    private void checkCatalogId(String catalogId) throws Exception {
        //获取globalscope
        GlobalScope globalScope = GlobalScope.getInstance();
        ElementDef elementDef = globalScope.getGlobalScopeElement(catalogId);
        if (elementDef == null) {
            throw new Exception("配置的" + catalogId + "不存在在当前的任何chains的配置文件中,可能是nschains没有启动或者没有配置此catalog");
        }

        if (!(elementDef instanceof CatalogDef)) {
            throw new Exception("配置的" + catalogId + "是" + elementDef.getClass().getName() + "元素,不是catalog,配置只能引用catalog元素");
        }

    }

    public Map<String, InQueueInfo> getQueueNameToQueueInfos() {
        return Collections.unmodifiableMap(queueNameToQueueInfos);
    }

    public void setQueueNameToQueueInfos(
            Map<String, InQueueInfo> queueNameToQueueInfos) {
        this.queueNameToQueueInfos = queueNameToQueueInfos;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getServiceClass(String queueName) {
        String serviceClass = this.queueNameToQueueInfos.get(queueName).getServiceClassName();
        return serviceClass;
    }

    @Override
    public void destroy() {
        long startTime = System.currentTimeMillis();
        frameLog.info("DefaultConfigManager stoping");

        this.queueNameToQueueInfos.clear();
        this.queueNameToQueueInfos = null;
        this.doc = null;
        this.resourcePath = null;
        this.xmlAppTransporterContext = null;
        isStarted = false;
        isSpring = false;
        isLoaded = false;

        frameLog.info("DefaultConfigManager stoped cost: {}ms", (System.currentTimeMillis() - startTime));
    }

    public XmlAppTransporterContext getXmlAppTransporterContext() {
        return xmlAppTransporterContext;
    }

    public void setXmlAppTransporterContext(
            XmlAppTransporterContext xmlAppTransporterContext) {
        this.xmlAppTransporterContext = xmlAppTransporterContext;
    }

    public boolean isSpring() {
        return isSpring;
    }

    public void setSpring(boolean isSpring) {
        this.isSpring = isSpring;
    }


}
