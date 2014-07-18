package plm.universe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.Semaphore;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import lessons.lightbot.universe.LightBotEntity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import plm.core.PythonExceptionDecipher;
import plm.core.model.Game;
import plm.core.model.ProgrammingLanguage;
import plm.core.model.lesson.ExecutionProgress;

/* Entities cannot have their own org.xnap.commons.i18n.I18n, use the static Game.i18n instead.
 * 
 * This is because we have to pass the classname to the I18nFactory, but it seems to break 
 * stuff that our code generate new package names. This later case being forced by our use 
 * of the compiler, we cannot initialize an I18n stuff. 
 * 
 * Instead, the solution is to use the static field Game.i18n, as it is done in AbstractBuggle::diffTo().
 */

public abstract class Entity extends Observable {
	protected String name = "(noname)";

	protected World world;

	private Semaphore oneStepSemaphore = new Semaphore(0);

	public Entity() {}

	public Entity(String name) {
		this.name=name;
	}
	public Entity(String name, World w) {
		this.name=name;
		if (w != null)
			w.addEntity(this);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}	

	public World getWorld() {
		return world;
	}

	/** Ideally, this should be used only from world.addEntity() */
	protected void setWorld(World world) {
		this.world = world;
	}

	/* This is to allow exercise to forbid the use by students of some functions 
	 * which are mandatory for core mechanism. See welcome.ArrayBuggle to see how it forbids setPos(int,int)  
	 */
	private boolean inited = false;
	public boolean isInited() {
		return inited;
	}
	public void initDone() {
		inited = true;		
	}

	public void allowOneStep() {
		this.oneStepSemaphore.release();
	}

	/** Delays the entity to let the user understand what's going on.
	 *  
	 * Calls to this function should be placed in important operation of the entity. There e.g. one such call in BuggleEntity.forward().  
	 */
	protected void stepUI() {		
		fireStackListener();
		world.notifyWorldUpdatesListeners();
		if (world.isDelayed()) {
			if (Game.getInstance().stepModeEnabled()) {
				this.oneStepSemaphore.acquireUninterruptibly();
			} else {	
				try {
					if (world.getDelay()>0) // seems that sleep(0) takes time (yield thread?)
						Thread.sleep(world.getDelay());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}		
	}

	/** Copy fields of the entity passed in argument */
	public void copy(Entity other) {
		setName(other.getName());
		setWorld(other.getWorld()); // FIXME: killme? I guess that we always reset the world after copy.
	}

	/* Stuff related to tracing mechanism.
	 * 
	 * This is the ability to highlight the current instruction in step-by-step execution. 
	 * 
	 * Right now, this is only used for LightBot because I'm not sure of how to retrieve the current point of execution in java or scripting
	 */
	ArrayList<IEntityStackListener> stackListeners = new ArrayList<IEntityStackListener>();
	public void addStackListener(IEntityStackListener l) {
		stackListeners.add(l);
	}
	public void removeStackListener(IEntityStackListener l) {
		stackListeners.remove(l);
	}
	public void fireStackListener() {
		StackTraceElement[] trace = getCurrentStack();
		for (IEntityStackListener l:stackListeners)
			l.entityTraceChanged(this, trace);		
	}
	public StackTraceElement[] getCurrentStack() {
		return Thread.currentThread().getStackTrace();
	}

	/** Retrieve one parameter from the world */
	public Object getParam(int i) {
		return world.parameters[i];
	}	
	protected int getParamsAmount() {
		return world.parameters.length;
	}

	/** Returns whether this is the entity selected in the interface */
	public boolean isSelected() {
		return this == Game.getInstance().getSelectedEntity();
	}

	/** Run this specific entity, encoding the student logic to solve a given exercise. 
	 * 
	 *  This method is redefined by the leafs of the inheritance tree (the entities involved in exercises)   
	 *   
	 *  @see #runIt() that execute an entity depending on the universe and programming language
	 *    
	 */
	protected abstract void run() throws Exception;

	/*
	 * GIANNINI
	 * Cette methode permet aux sous Entity de pouvoir communiquer avec des programmes externes
	 * Pour l'instant c'est fait avec le C (ou en train d'etre fait)
	 * 
	 */
	protected abstract void command(String command, BufferedWriter out);


	/** Make the entity run, according to the used universe and programming language.
	 * 
	 * This task is not trivial given that it depends on the universe and the programming language:
	 *  * In most universes, the active part is the entity itself. But in the Bat universe, the 
	 *    student-provided method (that is not a real entity but part of the world directly) 
	 *    is run against all testcase, that are not real worlds either.
	 *    
	 *  * Java entities are launched by just executing their {@link #run()} method that 
	 *    was redefined by the student (possibly with some templating)
	 *  * LightBot entities are launched by executing the {@link LightBotEntity#run()} method, 
	 *    that is NOT defined by the student, but interprets the code of the students.
	 *  * Python (and other scripting language) entities are launched by injecting the 
	 *    student-provided code within a {@link ScriptEngine}. 
	 *    In this later case, the java entity is injected within the scripting world so that it 
	 *    can forward the student commands to the world. 
	 * 
	 *  @see #run() that encodes the student logic in Java
	 */
	public void runIt(ExecutionProgress progress) {
		ProgrammingLanguage progLang = Game.getProgrammingLanguage();
		ScriptEngine engine = null;
		if (progLang.equals(Game.JAVA)||progLang.equals(Game.SCALA)||progLang.equals(Game.LIGHTBOT)) {
			try {
				run();
			} catch (Exception e) {
				String msg = Game.i18n.tr("The execution of your program raised a {0} exception: {1}\n" + 
						" Please fix your code.\n",e.getClass().getName(),e.getLocalizedMessage());

				for (StackTraceElement elm : e.getStackTrace())
					msg+= "   at "+elm.getClassName()+"."+elm.getMethodName()+" ("+elm.getFileName()+":"+elm.getLineNumber()+")"+"\n";

				System.err.println(msg);
				progress.setCompilationError(msg);
				e.printStackTrace();
			}
		} else if(progLang.equals(Game.C)){
			Runtime runtime = Runtime.getRuntime();
			final Entity entityThis = this;
			final StringBuffer resCompilationErr = new StringBuffer();

			try {

				String tempdir = System.getProperty("java.io.tmpdir")+"/plmTmp";
				File saveDir = new File(tempdir+"/bin");

				int nb=(int)(Math.random()*1000);
				final File randomFile = new File(tempdir+"/tmp_"+nb+".txt");
				System.out.println("tmp file : "+randomFile.getAbsolutePath());
				if(!randomFile.createNewFile()){
					//TODO GIANNINI add error message
					System.out.println("ERREUR CREATE TMPFILE");
				}

				final File valgrindFile=new File(tempdir+"/valgrind_"+nb+".xml");

				String extension="";
				String arg1[];
				String os = System.getProperty("os.name").toLowerCase();
				final StringBuffer valgrind=new StringBuffer("");
				if (os.indexOf("win") >= 0) {
					extension=".exe";
					arg1 = new String[3];
					arg1[0]="cmd.exe";
					arg1[1]="/c";
					arg1[2]=saveDir.getAbsolutePath()+"/prog"+extension+" "+randomFile.getAbsolutePath();
				} else {
					//test if valgrind exist
					Runtime r = Runtime.getRuntime();
					try {
						r.exec("valgrind --version");
						if(!valgrindFile.createNewFile()){
							//TODO GIANNINI add error message
							System.out.println("ERREUR CREATE TMPFILE");
						}
						valgrind.append("valgrind --xml=yes --xml-file=\""+valgrindFile.getAbsolutePath()+"\"");
					} catch (IOException e) {
						//TODO GIANNINI error message
						System.out.println("Vous ne disposez pas de valgrind");
					}
					arg1 = new String[3];
					arg1[0]="/bin/sh";
					arg1[1]="-c";
					arg1[2]=valgrind+" "+saveDir.getAbsolutePath()+"/prog"+extension+" "+randomFile.getAbsolutePath();
				}

				File exec = new File(saveDir.getAbsolutePath()+"/prog"+extension);
				if(!exec.exists()){
					//TODO GIANNINI add error message if the binary isn't here
					System.out.println("NO BINARY FOUND");
					return;
				}else if(!exec.canExecute() || !exec.isFile()){
					//TODO GIANNINI add error message if the file is not a file or not executable
					System.out.println("PROG IS NOT EXECUTABLE OR A FILE");
					return;
				}

				final Process process = runtime.exec(arg1);

				final BufferedWriter bwriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
				Thread reader = new Thread() {
					public void run() {
						try {
							BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
							try {
								int truc;
								String str = "";
								while((truc=reader.read())!=-1){
									if(truc!=10){
										str+=(char)truc;
									}else{
										entityThis.command(str, bwriter);
										str="";
									}
								}

							} finally {
								reader.close();
							}
						} catch(IOException ioe) {
							ioe.printStackTrace();
						}
					}
				};


				Thread error = new Thread() {
					public void run() {
						try {
							InputStreamReader isr = new InputStreamReader(process.getErrorStream());
							BufferedReader err = new BufferedReader(isr);

							if(valgrind.length()>0){
								try {
									process.waitFor();
									DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
									DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
									Document doc = dBuilder.parse(valgrindFile);
									doc.getDocumentElement().normalize();

									NodeList nodes = doc.getElementsByTagName("error");
									int nbError=nodes.getLength();
									if(nbError>0){
										resCompilationErr.append("========= "+nbError+" Error"+(nbError>1?"s":"")+" =========\n");
										for (int i = 0; i < nbError; i++) {
											Node node = nodes.item(i);
											if(node!=null){
												String space="";
												resCompilationErr.append("========= Error "+(i+1)+" =========\n");
												resCompilationErr.append("cause : "+getValue("kind", ((Element)node))+"\n");
												resCompilationErr.append("details : "+getValue("auxwhat", ((Element)node))+"\n");
												if (node.getNodeType() == Node.ELEMENT_NODE) {
													for(int j=0;j<node.getChildNodes().getLength();j++){
														Node node2 = node.getChildNodes().item(j);
														if (node2!=null && node2.getNodeType() == Node.ELEMENT_NODE) {
															if(node2.getNodeName().toLowerCase().equals("stack")){
																for(int k=0;k<node2.getChildNodes().getLength();k++){
																	Node node3 = node2.getChildNodes().item(k);
																	if(node3!=null && node3.getNodeType() == Node.ELEMENT_NODE){
																		Element element = (Element) node3;
																		if(!getValue("fn", element).toLowerCase().equals("main")){
																			resCompilationErr.append(space+"fonction: " + getValue("fn", element)+"\n");
																			resCompilationErr.append(space+"line: " + getValue("line", element)+"\n");
																			space+="    ";
																		}
																	}
																}
															}else if(node2.getNodeName().toLowerCase().equals("xwhat")){
																for(int k=0;k<node2.getChildNodes().getLength();k++){
																	Node node3 = node2.getChildNodes().item(k);
																	if(node3!=null && node3.getNodeType() == Node.ELEMENT_NODE){
																		Element element = (Element) node3;
																		//if(!getValue("fn", element).toLowerCase().equals("main")){
																			resCompilationErr.append(space+"fonction: " + getValue("fn", element)+"\n");
																			resCompilationErr.append(space+"line: " + getValue("line", element)+"\n");
																			space+="    ";
																		//}
																	}
																}
															}
														}
													}
												}

												resCompilationErr.append("======= End Error "+(i+1)+" =======\n");
											}
										}
									}
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}else{
								String line = "";
								while((line = err.readLine()) != null) {
									if(line.contains("<")){
										resCompilationErr.append(line+"\n");
									}
									System.out.println("error : "+line);
								}
							}

						} catch(IOException ioe) {
							ioe.printStackTrace();	
						}

					}
				};

				final StringBuffer continu = new StringBuffer("");
				Thread print = new Thread() {
					public void run() {
						try {
							InputStream ips=new FileInputStream(randomFile.getAbsolutePath()); 
							InputStreamReader ipsr=new InputStreamReader(ips);
							BufferedReader br=new BufferedReader(ipsr);
							String line = "";
							try {
								int truc;
								String str = "";
								while(continu.length()==0){
									truc=br.read();
									if(truc!=-1){
										if(((char)truc)!='\n'){
											str+=(char)truc;
										}else{
											System.out.println(str);
											str="";
										}
									}
								}
								while((truc=br.read())!=-1){
									if(truc!=10){
										str+=(char)truc;
									}else{
										entityThis.command(str, bwriter);
										str="";
									}
								}
							} finally {
								br.close();
							}
						} catch(IOException ioe) {
							ioe.printStackTrace();
						}
					}
				};


				reader.start();
				error.start();
				print.start();
				process.waitFor();
				reader.join();
				error.join();
				continu.append("fin");
				print.join();

				bwriter.close();


				randomFile.delete();

				if(valgrindFile.exists()){
					valgrindFile.delete();
				}

				if(resCompilationErr.length()>0){
					System.err.println(resCompilationErr.toString());
					progress.setCompilationError(resCompilationErr.toString());
				}


			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				String msg = Game.i18n.tr("The execution of your program raised a {0} exception: {1}\n" + 
						" Please fix your code.\n",e.getClass().getName(),e.getLocalizedMessage());

				for (StackTraceElement elm : e.getStackTrace())
					msg+= "   at "+elm.getClassName()+"."+elm.getMethodName()+" ("+elm.getFileName()+":"+elm.getLineNumber()+")"+"\n";
				System.err.println(msg);
				progress.setCompilationError(msg);
				e.printStackTrace();
			}
		}else{
			try {
				ScriptEngineManager manager = new ScriptEngineManager();       
				engine = manager.getEngineByName(progLang.getLang().toLowerCase());
				if (engine==null)
					throw new RuntimeException(Game.i18n.tr("No ScriptEngine for {0}. Please check your classpath and similar settings.",progLang.getLang()));

				/* Inject the entity into the scripting world so that it can forward script commands to the world */
				engine.put("entity", this);


				/* Inject commands' wrappers that forward the calls to the entity */
				this.getWorld().setupBindings(progLang,engine);

				/* getParam is in every Entity, so put it here to not request the universe to call super.setupBinding() */
				if (progLang.equals(Game.PYTHON)) 
					engine.eval(
							"def getParam(i):\n"+
									"  return entity.getParam(i)\n" +
									"def cted():\n" +
							"  return entitisSeley.isSelected()\n");		

				String script = getScript(progLang);

				if (script == null) { 
					System.err.println(Game.i18n.tr("No {0} script source for entity {1}. Please report that bug against PLM.",progLang,this));
					return;
				}
				if (progLang.equals(Game.PYTHON)) {
					/* that's not really clean to get the output working when we 
					 * redirect to the graphical console, but it works. */
					setScriptOffset(progLang, getScriptOffset(progLang)+7);
					script= "import sys;\n" +
							"import java.lang;\n" +
							"class PLMOut:\n" +
							"  def write(obj,msg):\n" +
							"    java.lang.System.out.print(str(msg))\n" +
							"sys.stdout = PLMOut()\n" +
							"sys.stderr = PLMOut()\n" +
							script;
				}
				engine.eval(script);

			} catch (ScriptException e) {
				if (Game.getInstance().isDebugEnabled()) 
					System.err.println("Here is the script in "+progLang.getLang()+" >>>>"+script+"<<<<");
				if (Game.getInstance().canPython && PythonExceptionDecipher.isPythonException(e))
					PythonExceptionDecipher.handlePythonException(e,this,progress);
				else {
					System.err.println(Game.i18n.tr("Received a ScriptException that does not come from Python.\n")+e);
					e.printStackTrace();
				}

			} catch (Exception e) {
				String msg = Game.i18n.tr("Script evaluation raised an exception that is not a ScriptException but a {0}.\n"+
						" Please report this as a bug against PLM, with all details allowing to reproduce it.\n" +
						"Exception message: {1}\n",e.getClass(),e.getLocalizedMessage());
				System.err.println(msg);
				for (StackTraceElement elm : e.getStackTrace()) 
					msg += elm.toString()+"\n";

				progress.setCompilationError(msg);
				e.printStackTrace();
			}
		}
	}

	private static String getValue(String tag, Element element) {
		if(element.getElementsByTagName(tag)!=null && element.getElementsByTagName(tag).item(0)!=null){
			NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
			Node node = (Node) nodes.item(0);
			return node.getNodeValue();
		}else{
			return "";
		}
	}

	private Map<ProgrammingLanguage,String> script = new HashMap<ProgrammingLanguage, String>(); /* What to execute when running a scripting language */
	public void setScript(ProgrammingLanguage lang, String s) {
		script.put(lang,  s);
	}
	public String getScript(ProgrammingLanguage lang) {
		return script.get(lang);
	}

	private Map<ProgrammingLanguage,Integer> scriptOffset = new HashMap<ProgrammingLanguage, Integer>(); /* the offset to apply to error messages */
	public void setScriptOffset(ProgrammingLanguage lang, int offset) {
		scriptOffset.put(lang,  offset);
	}
	public Integer getScriptOffset(ProgrammingLanguage lang) {
		Integer res = scriptOffset.get(lang);
		return res == null ? 0:res;
	}
}
