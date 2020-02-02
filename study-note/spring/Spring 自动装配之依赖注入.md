## Spring 自动装配之依赖注入

### 一、依赖注入发生的时间

#### 1.1 注入时机

​	在 **Spring IOC容器初始化流程.md** 文章中分析到, Spring IOC经过 定位、加载、注册之后, Bean信息以BeanDefinition格式保存, 并存储在ConcurrentHashMap<String, BeanDefinition> 中。文章对Bean注入流程没有讲解, 那么Bean什么时候注入相关属性的呢 ？

- Bean的配置属性 lazy-init = false 时, 懒加载为false(饿汉模式), 让容器在解析bean的时候进行预初始化, 触发依赖注入
- 第一次调用 getBean() 方法时候 , IOC容器触发依赖注入

​      这片文章将会讲解第一次调用getBean()方法时, IOC触发依赖注入的逻辑(lazy-init = false方式这里暂时不分析)。

#### 1.2 类结构图

![BeanFactory类结构图](./SpringIOC Container initialization/BeanFactory类结构图.png)

<center>图一、BeanFactory 类结构图</center>
### 二、获取Bean的入口

#### 2.1 顶级父类BeanFactory

​	从图一中可以看出, BeanFactory接口是Bean操作的顶级父类, 里面定义了一系列对Bean操作的方法, 其中包括getBean()方法, 下面的代码是具体代码

```java
public interface BeanFactory {
  // 省略部分其它方法
	Object getBean(String name) throws BeansException;
	<T> T getBean(String name, Class<T> requiredType) throws BeansException;
	<T> T getBean(Class<T> requiredType) throws BeansException;
	<T> ObjectProvider<T> getBeanProvider(Class<T> requiredType);
	boolean containsBean(String name);
	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;
	boolean isPrototype(String name) throws NoSuchBeanDefinitionException;
	boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;
	@Nullable
	Class<?> getType(String name) throws NoSuchBeanDefinitionException;
	String[] getAliases(String name);
}
```

​	BeanFactory定义了Spring IOC容器基本功能和规范, 接口中定义的若干getBean()方法, 是调用者在获取Bean对象时需要调用的方法

#### 2.2 底层子类DefaultListBeanFactory

​	DefaultListableBeanFactory 是最底层类, 它实现了BeanFactory、Registry相关接口的所有方法, DefaultListableBeanFactory相关源码如下, 它定义了查询的入口基本逻辑

- 从已经解析过的对象中查询当前对象, 如果有则返回
- 如果没有解析过, 父类AbstractBeanFactory执行getBean()方法获取Bean对象

```java
public <T> T getBean(Class<T> requiredType, @Nullable Object... args) throws BeansException {
  	// 从已经解析过的Bean中查找
		NamedBeanHolder<T> namedBean = resolveNamedBean(requiredType, args);
		if (namedBean != null) {
			return namedBean.getBeanInstance();
		}
  	// 查找获取bean对象的父类, 这里parent是AbstractBeanFactory
		BeanFactory parent = getParentBeanFactory();
		if (parent != null) {
			return (args != null ? parent.getBean(requiredType, args) : parent.getBean(requiredType)); // 调用AbstractBeanFactory类的getBean()方法获取Bean对象
		}
		throw new NoSuchBeanDefinitionException(requiredType);
	}

```

#### 2.3 getBean的实际入口

​	从2.2中我们知道, getBean()方法的处理逻辑在AbstractBeanFactory中, 相关源码如下

```java
//获取IOC容器中指定名称的Bean
	@Override
	public Object getBean(String name) throws BeansException {
		//doGetBean才是真正向IoC容器获取被管理Bean的过程
		return doGetBean(name, null, null, false);
	}

	//获取IOC容器中指定名称和类型的Bean
	@Override
	public <T> T getBean(String name, @Nullable Class<T> requiredType) throws BeansException {
		//doGetBean才是真正向IoC容器获取被管理Bean的过程
		return doGetBean(name, requiredType, null, false);
	}

	//获取IOC容器中指定名称和参数的Bean
	@Override
	public Object getBean(String name, Object... args) throws BeansException {
		//doGetBean才是真正向IoC容器获取被管理Bean的过程
		return doGetBean(name, null, args, false);
	}

	//获取IOC容器中指定名称、类型和参数的Bean
	public <T> T getBean(String name, @Nullable Class<T> requiredType, @Nullable Object... args)
			throws BeansException {
		//doGetBean才是真正向IoC容器获取被管理Bean的过程
		return doGetBean(name, requiredType, args, false);
	}
```

​	从代码中, getBean()方法实际调用的是doGetBean()方法, 即doGetBean(String name, Class<?> requiredType, Objects[] args, boolean typeCheckOnly)才是真正获取Bean, 并进行依赖注入的入口, 相关源码如下

```java
//真正实现向IOC容器获取Bean的功能，也是触发依赖注入功能的地方
	protected <T> T doGetBean(final String name, @Nullable final Class<T> requiredType,
			@Nullable final Object[] args, boolean typeCheckOnly) throws BeansException {

		//根据指定的名称获取被管理Bean的名称，剥离指定名称中对容器的相关依赖
		//如果指定的是别名，将别名转换为规范的Bean名称
		final String beanName = transformedBeanName(name);
		Object bean;

		// Eagerly check singleton cache for manually registered singletons.
		//先从缓存中取是否已经有被创建过的单态类型的Bean
		//对于单例模式的Bean整个IOC容器中只创建一次，不需要重复创建
		Object sharedInstance = getSingleton(beanName);
		//IOC容器创建单例模式Bean实例对象
		if (sharedInstance != null && args == null) {
			//获取给定Bean的实例对象，主要是完成FactoryBean的相关处理
			//注意：BeanFactory是管理容器中Bean的工厂，而FactoryBean是
			//创建创建对象的工厂Bean，两者之间有区别
			bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
		}

		else {
			//对IOC容器中是否存在指定名称的BeanDefinition进行检查，首先检查是否
			//能在当前的BeanFactory中获取的所需要的Bean，如果不能则委托当前容器
			//的父级容器去查找，如果还是找不到则沿着容器的继承体系向父级容器查找
			BeanFactory parentBeanFactory = getParentBeanFactory();
			//当前容器的父级容器存在，且当前容器中不存在指定名称的Bean
			if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
				// Not found -> check parent.
				//解析指定Bean名称的原始名称
				String nameToLookup = originalBeanName(name);
				if (parentBeanFactory instanceof AbstractBeanFactory) {
					return ((AbstractBeanFactory) parentBeanFactory).doGetBean(
							nameToLookup, requiredType, args, typeCheckOnly);
				}
				else if (args != null) {
					// Delegation to parent with explicit args.
					//委派父级容器根据指定名称和显式的参数查找
					return (T) parentBeanFactory.getBean(nameToLookup, args);
				}
				else {
					// No args -> delegate to standard getBean method.
					//委派父级容器根据指定名称和类型查找
					return parentBeanFactory.getBean(nameToLookup, requiredType);
				}
			}

			try {
				//根据指定Bean名称获取其父级的Bean定义
				//主要解决Bean继承时子类合并父类公共属性问题
				final RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
				checkMergedBeanDefinition(mbd, beanName, args);

				// Guarantee initialization of beans that the current bean depends on.
				//获取当前Bean所有依赖Bean的名称
				String[] dependsOn = mbd.getDependsOn();
				//如果当前Bean有依赖Bean
				if (dependsOn != null) {
					for (String dep : dependsOn) {
						//递归调用getBean方法，获取当前Bean的依赖Bean
						registerDependentBean(dep, beanName);
						//把被依赖Bean注册给当前依赖的Bean
						getBean(dep);
					}
				}

				// Create bean instance.
				//创建单例模式Bean的实例对象
				if (mbd.isSingleton()) {
					//这里使用了一个匿名内部类，创建Bean实例对象，并且注册给所依赖的对象
					sharedInstance = getSingleton(beanName, () -> {
						try {
							//创建一个指定Bean实例对象，如果有父级继承，则合并子类和父类的定义
							return createBean(beanName, mbd, args);
						}
						catch (BeansException ex) {
							//显式地从容器单例模式Bean缓存中清除实例对象
							destroySingleton(beanName);
							throw ex;
						}
					});
					//获取给定Bean的实例对象
					bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
				}
				//IOC容器创建原型模式Bean实例对象
				else if (mbd.isPrototype()) {
					// It's a prototype -> create a new instance.
					//原型模式(Prototype)是每次都会创建一个新的对象
					Object prototypeInstance = null;
					try {
						//回调beforePrototypeCreation方法，默认的功能是注册当前创建的原型对象
						beforePrototypeCreation(beanName);
						//创建指定Bean对象实例
						prototypeInstance = createBean(beanName, mbd, args);
					}
					finally {
						//回调afterPrototypeCreation方法，默认的功能告诉IOC容器指定Bean的原型对象不再创建
						afterPrototypeCreation(beanName);
					}
					//获取给定Bean的实例对象
					bean = getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
				}

				//要创建的Bean既不是单例模式，也不是原型模式，则根据Bean定义资源中
				//配置的生命周期范围，选择实例化Bean的合适方法，这种在Web应用程序中
				//比较常用，如：request、session、application等生命周期
				// ... 省略这部分代码
			}
			catch (BeansException ex) {
				cleanupAfterBeanCreationFailure(beanName);
				throw ex;
			}
		}

		// Check if required type matches the type of the actual bean instance.
		// ... 省略对创建的Bean实例对象进行类型检查的代码
		
		return (T) bean;
	}
```

​	通过doGetBean()方法分析, 可以看到Spring中, 

- 如果Bean是单例模式(Singletone), 则容器在获取bean对象之前会先从缓存中查找, 以确保整个容器只有一个bean对象, 
- 如果Bean是原型模式(Prototype), 则容器每次都会创建一个新的对象
- Bean的scope除了Sigletone、Prototype还有request、session、globalSession
- doGetBean()方法定义了不同情况下, 创建Bean对象不同的策略, 具体的创建过程由实现了ObjectFactory接口匿名内部类的createBean()方法创建(createBean方法在类中是抽象方法)
- createBean() 使用了委派模式, 具体的Bean创建过程由子类AbstractAutowireCapableBeanFactory完成

### 三、实例化创建对象

​	AbstractAutowireCapableBeanFactory类创建Bean对象的入口是createBean()方法, 实际执行创建的方法是doCreateBean()方法, 在创建Bean对象过程中, 除了对象的创建还需要进行初始化操作, 创建Bean对象的源码如下

```java
protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final @Nullable Object[] args)
			throws BeanCreationException {
		// Instantiate the bean.
		//封装被创建的Bean对象
		BeanWrapper instanceWrapper = null;
		if (mbd.isSingleton()) {
			instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
		}
		if (instanceWrapper == null) {
			instanceWrapper = createBeanInstance(beanName, mbd, args);
		}
		final Object bean = instanceWrapper.getWrappedInstance();
		//获取实例化对象的类型
		Class<?> beanType = instanceWrapper.getWrappedClass();
		if (beanType != NullBean.class) {
			mbd.resolvedTargetType = beanType;
		}
		// ... 省略部分代码
		
		//向容器中缓存单例模式的Bean对象，以防循环引用
		boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
				isSingletonCurrentlyInCreation(beanName));
		if (earlySingletonExposure) {
			//这里是一个匿名内部类，为了防止循环引用，尽早持有对象的引用
			addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
		}

		// Initialize the bean instance.
		//Bean对象的初始化，依赖注入在此触发
		//这个exposedObject在初始化完成之后返回作为依赖注入完成后的Bean
		Object exposedObject = bean;
		try {
			//将Bean实例对象封装，并且Bean定义中配置的属性值赋值给实例对象
			populateBean(beanName, mbd, instanceWrapper);
			//初始化Bean对象
			exposedObject = initializeBean(beanName, exposedObject, mbd);
		}
		catch (Throwable ex) {
			throw ex ;
		}

		if (earlySingletonExposure) {
			//获取指定名称的已注册的单例模式Bean对象
			Object earlySingletonReference = getSingleton(beanName, false);
			if (earlySingletonReference != null) {
				//根据名称获取的已注册的Bean和正在实例化的Bean是同一个
				if (exposedObject == bean) {
					//当前实例化的Bean初始化完成
					exposedObject = earlySingletonReference;
				}
				//当前Bean依赖其他Bean，并且当发生循环引用时不允许新创建实例对象
				else if (!this.allowRawInjectionDespiteWrapping && hasDependentBean(beanName)) {
					String[] dependentBeans = getDependentBeans(beanName);
					Set<String> actualDependentBeans = new LinkedHashSet<>(dependentBeans.length);
					//获取当前Bean所依赖的其他Bean
					for (String dependentBean : dependentBeans) {
						//对依赖Bean进行类型检查
						if (!removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
							actualDependentBeans.add(dependentBean);
						}
					}
			}
		}

		// Register bean as disposable.
		//注册完成依赖注入的Bean
		try {
			registerDisposableBeanIfNecessary(beanName, bean, mbd);
		}
		catch (BeanDefinitionValidationException ex) {
			throw new BeanCreationException(
					mbd.getResourceDescription(), beanName, "Invalid destruction signature", ex);
		}

		return exposedObject;
	}
```

​	通过上面的源码, 具体的依赖注入在下面两个方法中

- createBeanInstance(String name, RootBeanDefinition mdb, Object[] args)
- populateBean(String name, RootBeanDefinition mdb, BeanWrapper bw)

### 四、创建Bean对象核心逻辑

​	在 createBeanInstance()方法中，根据指定的初始化策略，使用简单工厂、工厂方法或者容器的自动装 配特性生成 Java 实例对象，分为下面两种方式

- JDK Proxy
- CGLIB



​	跟踪代码, 主要核心逻辑查看下面源代码

- 对使用 工厂方法和自动装配特性的Bean，调用相应的构造方法进行实例化
- 对无参构造方法, 使用JDK Proxy或CGLIB方式进行初始化

```java
public Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) {
		// Don't override the class with CGLIB if no overrides.
		//如果Bean定义中没有方法覆盖，则就不需要CGLIB父类类的方法
		if (!bd.hasMethodOverrides()) {
			Constructor<?> constructorToUse;
			synchronized (bd.constructorArgumentLock) {
				//获取对象的构造方法或工厂方法
				constructorToUse = (Constructor<?>) bd.resolvedConstructorOrFactoryMethod;
				//如果没有构造方法且没有工厂方法
				if (constructorToUse == null) {
					//使用JDK的反射机制，判断要实例化的Bean是否是接口
					final Class<?> clazz = bd.getBeanClass();
					if (clazz.isInterface()) {
						throw new BeanInstantiationException(clazz, "Specified class is an interface");
					}
					try {
						if (System.getSecurityManager() != null) {
							//这里是一个匿名内置类，使用反射机制获取Bean的构造方法
							constructorToUse = AccessController.doPrivileged(
									(PrivilegedExceptionAction<Constructor<?>>) () -> clazz.getDeclaredConstructor());
						}
						else {
							constructorToUse =	clazz.getDeclaredConstructor();
						}
						bd.resolvedConstructorOrFactoryMethod = constructorToUse;
					}
					catch (Throwable ex) {
						throw new BeanInstantiationException(clazz, "No default constructor found", ex);
					}
				}
			}
			//使用BeanUtils实例化，通过反射机制调用”构造方法.newInstance(arg)”来进行实例化
			return BeanUtils.instantiateClass(constructorToUse);
		}
		else {
			// Must generate CGLIB subclass.
			//使用CGLIB来实例化对象
			return instantiateWithMethodInjection(bd, beanName, owner);
		}
	}
```

​	查看上面的源码, 如果没有方法覆盖, 那么使用JDK Proxy进行初始化, 否则使用 CGLIB 的方式初始化对象, 根据Jdk Proxy和CGLIB的区别, JDK Proxy必须 实例化接口， 而CGLIB是方法覆盖的方式, Spring 就是根据这个来确定使用那种方式进行实例化。

### 五、准备依赖注入

​	



