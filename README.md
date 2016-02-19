#DevBricks

[![License](https://poser.pugx.org/dreamfactory/dreamfactory/license.svg)](http://www.apache.org/licenses/LICENSE-2.0) [![API](https://img.shields.io/badge/API-13%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=8) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.dailystudio/devbricks/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.dailystudio/devbricks)


DevBricks provides several classes which will be usually  used in daily Android development. With these "bricks", your development will become:

- **Efficient** : The classes provided by DevBricks almost cover all of the aspect in daily devevoplment, from low-end databaes to user interface. You do not need to waste your time on those repeating work.
- **Reliable** :  More than 60% code has related Unit test. Your work will stand on stable foundation. 
- **Consistent** : DevBricks includes unified logging system, database accessing, UI elements and styles. This make all of your applications has consistency at primary impression.

## Quick Setup
To use DevBircks Library, follow these steps.

### Step 1: Include the Library
**Maven dependency:**
``` xml
<dependency>
	<groupId>com.github.dailystudio</groupId>
	<artifactId>devbricks</artifactId>
	<version>1.0.0</version>
</dependency>
```
or
**Gradle dependency:**
``` groovy
compile 'com.github.dailystudio:devbricks:1.0.0'
```

### Step 2: Application initialization
Extends youre main Application from DevBricks Application:
``` java
public class MyApplication extends DevBricksApplication {
	/* your own code about application */
}
```
Then declare it in your **`AndroidMenifest.xml`**:
``` xml
<manifest>
	...
	<application
        android:name=".MyApplication">
		...
	</application>
	...
</manifest>
```
**`DevBricksApplication`** does two things for you:
- Bind and Unbind a global context with your Application Context.
- Disable or Enable Logging accroding to your build types and runtime environment.

You will know more about these two topic in following chapters. After you understand well with these, you can do it by yourself without derving your **`Application`** from **`DevBricksApplication`**

## Global Context

## Logging

## Database
Database facilities in DevBricks provides a efficient way to convert between *In-Memory Data Structures* and *SQLite Database Records*。 

- **`DatabaseObject`** represents object in memory which could be easily store in permanent database through Database read/write facility classes.
- **`Column`** describe how to map a field of a In-Memory class to a column of database record.
- **`Template`** contains a set of **`Column`** which is usually used to describe how to convert a **`DatabaseObject`** to database record.
- **`Query`** is used to describe query parameters when loading objects from databases. It converts most parts of common SQL select statement into Java language. 
- **`DatabaseReader`** is a shortcut class to reading obejcts from database.
- **`DatabaseWriter`** is a shortcut class to saving objects into database.

With these classes, even you do not have any knowledge about SQL or Androiud Content Provider, you can easily bind data in your application with permanent database storage.

### Define an Object
For example, if you have a class named **`People`**, which represent a people data structure in memory. It is defined as below:
```java
public class People {
	private String mName;
	private int mAge;
	private float mWeight;
	private int mHeight;
	private boolean mMarried;
}
```
You want each people will be stored as one record in database, like this:

ID   | Name    | Age  | Weight | Height | Married 
:--- | :-------| :--: | :--:   | :--:   | :--:   
1    | David   | 34   | 69     | 175    | 1       
2    | Lucy    | 33   | 48.5   | 165    | 0
...  | ...     | ..   | ..     | ...    | .

To map a **`People`** to a database record, you need to derive **`People`** from **`DatabaseObject`** firstly,  then define a template and bind them together:

```java
public class People extends DatabaseObject {

	public static final Column COLUMN_ID = new IntegerColumn("_id", false, true);
	public static final Column COLUMN_NAME = new StringColumn("name");
	public static final Column COLUMN_AGE = new IntegerColumn("age");
	public static final Column COLUMN_WEIGHT = new DoubleColumn("weight");
	public static final Column COLUMN_HEIGHT = new IntegerColumn("height");
	public static final Column COLUMN_MARRIED = new IntegerColumn("married");

	private final static Column[] COLUMNS = {
		COLUMN_ID,
		COLUMN_AGE,
		COLUMN_NAME,
		COLUMN_WEIGHT,
		COLUMN_HEIGHT,
		COLUMN_MARRIED,
	};
	
	public People(Context context) {
		super(context);
		
		final Template templ = getTemplate();
		templ.addColumns(COLUMNS);
	}
	
	pubic void setId(int id) {
		setValue(COLUMN_ID, id)
	}
	
	public int getId() {
		return getIntegerValue(COLUMN_ID);
	}

...
	
	pubic void setMarried(boolean married) {
		setValue(COLUMN_MARRIED, (married ? 1 : 0))
	}
	
	public boolean isMarried() {
		return (getIntegerValue(COLUMN_MARRIED) == 1);
	}

}
```

###Saving or loading objects
Before moving forward, you need to understand a little more implementation behind the interface. Database manipulation in DevBricks is basing on **`ContentProvider`**, which is an important component on Android platform. Even you do not need to know more about this concept, you have to declare things in your **`AndroidManifest.xml`** before you start to use these interfaces. Firstly, you need to declare a **`ContentProvider`** in the **`AndroidManifest.xml`** of your project:
```xml
<application
	android:icon="@drawable/ic_app"
    android:label="@string/app_name">
    ...
	<provider
	    android:name=".AppConnectivityProvider"
        android:authorities="com.yourdomain.youapp" />
    ...
</application>
```
Class **`AppConnectivityProvider`** is derived from **`DatabaseConnectivityProvider`**. Keep it implementation empty is enough.
```java
public class AppConnectivityProvider extends DatabaseConnectivityProvider {

}
```
Usually, you only need one provider like this to handle all the database operations in your application. Defining the authority of this provider same as your package name will make everything easy. When you create a **`DatabaseReader`** or **`DatabaseWriter`**,  you can use a shortcut creator, like this:
```java
DatabaseReader<People> reader = new DatabaseReader(context, People.class);
DatabaseWriter<People> writer = new DatabaseWriter(context, People.class);
...
```
But sometimes, you need to handle more complicated cases. You may need to define two providers. One is using inside application, while the other one is using to share data with other applications. In this case, you need to declare another provider with a different authority:
```xml
<application
	android:icon="@drawable/ic_app"
    android:label="@string/app_name">
    ...
	<provider
	    android:name=".ExternConnectivityProvider"
        android:authorities="com.yourdomain.external" />
    ...
</application>
```
At the same time, when you want to use **`DatabaseReader`** or **`DatabaseWriter`** on this provider, you need to pass the authority as second parameter in creator:
```java
DatabaseReader<People> reader = new DatabaseReader(context, "com.yourdomain.external", People.class);
DatabaseWriter<People> writer = new DatabaseWriter(context, "com.yourdomain.external", People.class);
...
```

Now, when you finish these steps above, you can easily use database read/write facilites to save or load **`People`** objects between memory and database. 

**`DatabaseWriter`** is a shortcut class to save im-memory obejcts to database.  For example, add a **`People`** to database:

```java
DatabaseWriter<People> writer = new DatabaseWriter(context, People.class);

People p = new People();
p.setName("David");
p.setAge(33);
p.setWeight(69);
p.setHeight(175);
p.setMarried(true);

writer.insert(p);
```

**`DatabaseReader`** is a shortcut class to load database records into memory.  For example, query all **`People`** from database:

```java
DatabaseReader<People> reader = new DatabaseReader(context, People.class);

List<People> people = reader.query(new Query(People.class));

for (People p: people) {
	/* process each people */
}

```
Sometimes, you may not want to retrieve all the columns from the database or you want to retrieve some calculated columns, like count(), sum() in SQLite. Another query interface will help you on this case. Before using the interface, you need to defined an projection class. 

Here is example, which includes basic information about people and related BMI.
> BMI stands for Body Mass Index.  BMI is used as  one measure to gauge risk for overall health problem. The standard range of BMI is from 18.5 to 24. The formula of BMI calculation is: 
> 
> *`BMI = weight (kg) / height ^ 2 (m)`*

The class **`PeopleBmi`** is defined as:
```java
public class PeopleBmi extends DatabaseObject {

	public static final Column COLUMN_AGE = new IntegerColumn("age");
	public static final Column COLUMN_BMI = new DoubleColumn(People.COLUMN_WEIGHT.divide(People.COLUMN_HEIGHT.multiple(People.COLUMN_HEIGHT)).toString());
	
	private final static Column[] COLUMNS = {
		People.COLUMN_ID,
		People.COLUMN_NAME,
		COLUMN_BMI,
	};

	public PeopleBmi(Context context) {
		super(context);
		
		final Template templ = getTemplate();
		templ.addColumns(COLUMNS);
	}
	
	public int getId() {
		return getIntegerValue(People.COLUMN_ID);
	}
	
	public String getName() {
		return getTextValue(People.COLUMN_NAME);
	}
	
	public double getBMI() {
		return getDoubleValue(COLUMN_BMI);
	}

}
```
Then you pass this class as second parameters of query interfaces and cast returned result to **`PeopleBmi`** objects:
```java
DatabaseReader<People> reader = new DatabaseReader(context, People.class);

List<DatabaseObject> bmiList = reader.query(new Query(People.class), PeopleBmi.class);

PeopleBmi bmi;
for (DatabaseObject obj: bmiList) {
	if (object instanceof PeopleBmi == false) {
		/* usually, this will not happen */
		continue;
	}
	
	bmi = (PeopleBmi)obj;
	/* process each people BMI */
}

```

When you are using the **`DatabaseReader`**, the **`Query`** will become a much more important helper class. You need to rely on this helper class to describe all of your query on the database.
A **`Query`** object combines the following  **`ExpressionToken`**  together to define a query. Each kind of these **`ExpressionToken`** correspond to a related SQLite statement:

Expression Token | SQLite Statement
:--              | :--
Selection Token  | where 
GroupBy Token    | group by
OrderBy Token    | order by
Having Token     | having
Limit Token      | limit 

Well known  *binary operators* can be performed on a **`ExpressionToken`**, including:

Op function        | SQLite Equivalent  | Explanation
:--                | :--                | :--
`.plus()`          | +                  | a + b
`.minus()`         | -                  | a - b
`.multiple()`      | *                  | a * b
`.divide()`        | /                  | a / b
`.modulo()`        | %                  | a % b

Besides, *logical operations* can  between combine two **`ExpressionToken`** together:

Op function        | SQLite Equivalent  | Explanation
:--                | :--                | :--
`.and()`           | &&                 | a && b
`.or()`            | \|\|               | a \|\| b
`.eq()`            | ==                 | a == b
`.neq()`           | <>                 | a <> b
`.gt()`            | >                  | a > b
`.gte()`           | >=                 | a >= b
`.lt()`            | <                  | a < b
`.lte()`           | <=                 | a <= b
`.in()`            | >= and <=          | a >= b && a <= c
`.out()`           | < or >             | a < b \|\| a > c

Here is a real case to demonstrate how to convert a SQLite query statement into a Query object. Taking People as example, we want to find out a set of people who is older than 30 and their BMI is not in standard range:

```sql
SELECT * FROME People WHERE (age > 30) AND (weight / (height * height) > 24) OR (weight / (height * height) < 18.5);
```
To describe this query with Query object, here is the snippet:
```java
Query query = new Query(People.class);
ExpressionToken bmiToken = People.COLUMN_WEIGHT.divide(People.COLUMN_HEIGHT.multiple(People.COLUMN_HEIGHT));
ExpressionToken selToken = People.COLUMN_AGE.gt(30).and（bmiToken.outOf(18.5, 24)）

query.setSelection(selToken);
```

Last but not the least, accessing the database may be high latency operations. It is better to move these kind of operations out of main UI thread. To handle this, you can move forward to the next chapter - *Loaders and AsyncTasks*.

## Loaders and AsyncTasks
**`Loader`** and **`AsyncTask`** are both designed to be helper classes around *Thread* and *Handler* in *Android framework*. **`Loader`** is better integrated with **`Activity`** and **`Fragment`**.  As mentioned in the last chapter, accessing the database should not be frequently used in main UI thread. To easily use  database classes and facilities in your applications,  DevBricks also provides you a set of helper classes to combine **`Loader`** and **`AsyncTask`** with **`DatabaseObject`**.

###Loaders
Breifly, DevBricks provides two helper classes for you to access database asynchronously, **`DatabaseObjectsLoader`** and **`DatabaseCursorLoader`**. The main difference between these two classes is the returned value. **`DatabaseObjectsLoader`** will return a list of **`DatabaseObject`**, while **`DatabaseCursorLoader`** will directly return the **`Cursor`**. 
The advantage of returning a list of objects is you can add more properties to the objects in memory. For example, the portrait of a person. You could not save the entire image of the portrait in database.  Usually, you only save the URI in database and save the resolved image in the same data structure in memory. After you load a list of objects from database, you will traverse the list and resolve each URI of portrait and then attach to the related object. In this case, using **`DatabaseCursorLoader`** will be more complicated. Because you could not attach anything on the return cursor. The solutions is creating an extra map to holds the relationship between images and database objects. 
Obviously, the **`DatabaseCursorLoader`** has its own applications, saving the memory. If you have thousands records in the database, loading them all to the memory may not be good choice. **`DatabaseCursorLoader`** will only return a cursor. You can use the cursor to traverse the entire database, but there is only a small piece of memory used to keep active content of database. 
**`DatabaseObjectsLoader`** and **`DatabaseCursorLoader`** are abstract classes. You need to implement the only abstract interface **`getObjectClass()`** before using them. Here is an example:
```java
public class PeopleObjectsLoader extends DatabaseObjectsLoader<People> {

	public PeopleObjectsLoader(Context context) {
		super(context);
	}

	protected Class<People> getObjectClass() {
		return People.class;
	}

}

public class PeopleCursorLoader extends DatabaseCursorLoader {

	public PeopleCursorLoader(Context context) {
		super(context);
	}

	protected Class<People> getObjectClass() {
		return People.class;
	}

}
```
The retrieved data will be return in onLoaderFinished() callback. For **`PeopleObjectsLoader`**, a list of People objects will be passed as second parameter. For **`PeopleCursorLoader`**, a **`Cursor`** will be passed and you can use **`fillValuesFromCursor()`** of **`DatabaseObject`** to convert **`Cursor`** to a **`DatabaseObject`**.

**`DatabaseObjectsLoader`** has an advanced classe: **`ProjectedDatabaseObjectsLoader`**. **`ProjectedDatabaseObjectsLoader`** is used to handle cases that the returned data are projections of original database.  Taking the class **`PeopleBmi`** shown in last chapter as example, you need to override on more interface of **`ProjectedDatabaseObjectsLoader`**:

```java
public class PeopleBmisLoader extends ProjectedDatabaseObjectsLoader<People, PeopleBmi> {

	public PeopleBmisLoader(Context context) {
		super(context);
	}

	protected Class<People> getObjectClass() {
		return People.class;
	}
	
	@Override
	protected Class<PeopleBmi> getProjectionClass() {
		return PeopleBmi.class;
	}

}

```
If you want a more complicated customized query, you can also override the protected function **`getQuery()`** for all the loaders above. For example, we only need people who is older than 30:
```java
...

	@Override
	protected Query getQuery(Class<People> klass) {
		Query query = super.getQuery(klass);

		ExpressionToken selToken = People.COLUMN_AGE.gt(30);
		query.setSelection(selToken);

		return query;
	}
	
...
```
Don't forget that if your authority of **`ContentProvider`** is not same as the package name of your application, you may need to override **`getDatabaseConnectivity()`** to define a special **`DatabaseConnectivity`**:
```java
...

	@Override
	protected DatabaseConnectivity getDatabaseConnectivity(
			Class<? extends DatabaseObject> objectClass) {
		return new DatabaseConnectivity(getContext(), "com.yourdomain.external", objectClass);
	}
	
...
```

###AsyncTask
AsyncTask is quite same as Loader, **`DatabaseObjectsAsyncTask`** is the most used class to retrieve data from database , while **`ProjectedDatabaseObjectsAsyncTask`** is its advance version which support projection of database content. Most interfaces mentioned above are also available in AsyncTask. Here is an example about **`ProjectedDatabaseObjectsAsyncTask`** which is calculate the BMI of people who is older than 30:
```java
public class PeopleBmisAsyncTask extends ProjectedDatabaseObjectsAsyncTask<People, PeopleBmi> {

	public PeopleBmisAsyncTask(Context context) {
		super(context);
	}

	protected Class<People> getObjectClass() {
		return People.class;
	}
	
	@Override
	protected Class<PeopleBmi> getProjectionClass() {
		return PeopleBmi.class;
	}

	@Override
	protected Query getQuery(Class<People> klass) {
		Query query = super.getQuery(klass);

		ExpressionToken selToken = People.COLUMN_AGE.gt(30);
		query.setSelection(selToken);

		return query;
	}

	@Override
	protected DatabaseConnectivity getDatabaseConnectivity(
			Class<? extends DatabaseObject> objectClass) {
		return new DatabaseConnectivity(getContext(), "com.yourdomain.external", objectClass);
	}

}
```

All the **`Loader`** in DevBricks are drived from **`android.support.v4.content.Loader`**, while the **`AsyncTask`** are drived from **`android.os.AsyncTask`**. How to use a **`Loader`** or **`AsyncTask`** is not covered in this document, you can refer to detailed guides on offical  [Android Devloper](http://developer.android.com/index.html) website. But if you want to save your energy to save the world, please move on to read the following chapter - *Fragments*. 

## Fragments
A **`Fragment`** is a piece of an application's user interface or behavior that can be placed in an **`Activity`**. DevBricks provide you some classes derived from **`Fragment`** and well integrated the concept mentioned in previous chapters. With these pre-defined classes, you can easily use **`DatabaseObject`** and **`Loader`** in your own application. 

The first class you should know is **`BaseIntentFragment`**, this class provides an interface **`bindIntent()`** which will be call on the host **`Activity`** is created or the host **`Activity`** receives *New Intent* event, when **`onNewIntent()`** of host **`Activity`** is called. This class the base of the classes you will in following paragraphs. 

The next class will be **`AbsLoaderFragment`**, which defines four **`Loader`** related interfaces. Three of them are abstracted, you need to implement them before using the loader. Taking the **`PeopleBmisLoader`** as an example,  here is a simple exmaple how to implement **`AbsLoaderFragment`**:
```java
public class PeopleBmisFragment extends AbsLoaderFragment<List<PeopleBmi>> {
	
	private final static int LOADER_PEOPLE_BMI_ID = 0x100;
	
    @Override
    public void onLoadFinished(Loader<List<PeopleBmi>> loader, List<PeopleBmi> data) {
		/* bind your data with UI */
    }

    @Override
    public void onLoaderReset(Loader<List<PeopleBmi>> loader) {
		/* reset your UI */
    }
    
	@Override
    public Loader<List<PeopleBmi>> onCreateLoader(int id, Bundle args) {
        return new PeopleBmisLoader(getActivity());
    }

	@Override
	protected int getLoaderId() {
		return LOADER_PEOPLE_BMI_ID;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return new Bundle();
	}

}
```
In **`onCreateLoader()`**, you need to create the loader which will load data used in this **`Fragment`** asynchronously. **`AbsLoaderFragment`** is defined as a template class.  Template T is a type abstraction of data passing through from **`Loader`** to **`Fragment`**. In the code above, **`PeopleBmisLoader`** will passing a list of retrieved **`PeopleBmi`** objects to the callback, so you need to declare T as **`List<PeopleBmi>`**.
In **`getLoaderId()`**, you need to return an unique integer in your application scope as an identifier of the loader.
In **`createLoaderArguments()`**,  each time you call **`restartLoader()`**, this function will be called. You can create different arguments **`Bundle`** according to your needs.
Besides these abstract methods,  **`AbsLoaderFragment`** also provides a method named **`restartLoader()`**, which can restart the loader at anytime you want.  Same as **`bindIntent()`** in its parents class, the **`restartLoader()`** is also automatically called when the host **`Activity`** is created or the host **`Activity`** receives an *New Intent* event.  Due to **`restartLoader()`** is called after **`bindIntent()`**, you are at ease about creating your loader and its argments according the **`Intent`** which is passed to the fragment.

As two successor of **`AbsLoaderFragment`**,  **`AbsArrayAdapterFragment`** and **`AbsCursorAdapterFragment`** add perfect integration with **`ListView`** and **`GridView`**. **`AbsArrayAdapterFragment`** is used for the loader which is dervied from **`DatabaseObjectsLoader`**, while **`AbsCursortAdapterFragment`** is used for the loader which is dervied from **`DatabaseCursorLoader`**. Due to deriving from **`AbsLoaderFragment`**, **`onCreateLoader()`**, **`createLoaderArguments()`** and **`getLoderId()`** must be implemented before using. But one more interface you must implement **`onCreateAdapter()`**. In this funtion, you need to create an **`ArrayAdapter`** or **`CursorAdapter`** according to which **`Fragment`** you want to use. The created adapter will bind with the  **`ListView`** or **`GridView`** in the **`Fragment`**. How does **`Loader`**, **`Adapter`** and **`Fragment`** well bound together is the responsiblity of DevBricks, you do not need to care about. One important difference between these two successors and **`AbsLoaderFragment`** is the template declaration. As we talked about above, the type declaration in **`AbsLoaderFragment`** is the type of data passed between **`Loader`** and **`Fragment`**. But in these two successor, the type declaration is the type of the item used in **`ListView`** or **`GridView`**. For the **`AbsCursorAdapterFragment`**, it has already declared the template as **`Cursor`** and you needn't to anything else. For **`AbsArrayAdapterFragment`**, here is an example:
```java
public class PeopleBmisAdapterFragment extends AbsArrayFragment<PeopleBmi> {
	
	private final static int LOADER_PEOPLE_BMI_ID = 0x100;
	
	@Override
    public Loader<List<PeopleBmi>> onCreateLoader(int id, Bundle args) {
        return new PeopleBmisLoader(getActivity());
    }

	@Override
	protected int getLoaderId() {
		return LOADER_PEOPLE_BMI_ID;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return new Bundle();
	}

	@Override
	protected BaseAdapter onCreateAdapter() {
		return new PeopleBmisAdapter(getActivity());
	}

}
```

>Copyright
>2010-2016 by Daily Studio.
