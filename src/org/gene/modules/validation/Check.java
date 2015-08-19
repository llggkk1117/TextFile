package org.gene.modules.validation;

import java.lang.reflect.Constructor;
import java.util.Vector;


public class Check
{
	private static IllegalArgumentException getExceptionInstance(Class<?> exceptionClass, String message)
	{
		IllegalArgumentException exception = null;
		try
		{
			Constructor<?> exceptionConstructor = exceptionClass.getConstructor(String.class);
			if((message == null)||("".equals(message)))
			{
				exception = (IllegalArgumentException) exceptionConstructor.newInstance();
			}
			else
			{
				exception = (IllegalArgumentException) exceptionConstructor.newInstance(message);
			}
		}
		catch(Exception e)
		{
			exception = new IllegalArgumentException();
		}

		return exception;
	}

	private static void raiseException(Class<?> exceptionClass, String message)
	{
		IllegalArgumentException exception = getExceptionInstance(exceptionClass, message);
		throw exception;
	}


	public static void checkCore(Class<?> exceptionClass, String message, boolean condition)
	{
		if(condition)
		{
			raiseException(exceptionClass, message);
		}
	}



	private static Object[] normalize(Object... args)
	{
		Object[] result = null;
		if(args != null)
		{
			boolean containsArray = false;
			for(int i=0; i<args.length; ++i)
			{
				if(args[i] instanceof Object[])
				{
					containsArray = true;
					break;
				}
			}

			if(containsArray)
			{
				Vector<Object> queue = new Vector<Object>();
				for(int i=0; i<args.length; ++i)
				{
					queue.add(args[i]);
				}

				for(int i=0; i<queue.size(); ++i)
				{
					if(queue.elementAt(i) instanceof Object[])
					{
						Object[] element = (Object[])queue.remove(i);
						for(int j=element.length-1; j>=0; --j)
						{
							queue.add(i, element[j]);
						}
						i--;
					}
				}

				result = new Object[queue.size()];
				queue.toArray(result);
			}
			else
			{
				result = args;
			}
		}

		return result;
	}



	// checks if any element is blank
	public static boolean anyBlank(Object... elements)
	{
		boolean blank = true;
		if(elements != null)
		{
			blank = false;
			elements = normalize(elements);
			for(int i=0; i<elements.length; ++i)
			{
				if((elements[i] == null)||((elements[i] instanceof String)&&("".equals((String)elements[i]))))
				{
					blank = true;
					break;
				}
			}
		}

		return blank;
	}

	// checks if every elements is not blank
	public static boolean isNotBlank(Object... elements)
	{
		return !anyBlank(elements);
	}

	public static void notBlankWithMessage(Class<?> exceptionClass, String message, Object... args)
	{
		checkCore(exceptionClass, message, anyBlank(args));
	}

	public static void notBlankWithMessage(String message, Object... args)
	{
		checkCore(IllegalArgumentException.class, message, anyBlank(args));
	}

	public static void notBlank(Class<?> exceptionClass, Object... args)
	{
		checkCore(exceptionClass, null, anyBlank(args));
	}

	public static void notBlank(Object... args)
	{
		checkCore(IllegalArgumentException.class, null, anyBlank(args));
	}











	public static void allTrue(Class<?> exceptionClass, String message, boolean condition)
	{
		checkCore(exceptionClass, message, !condition);
	}

	public static void allTrue(String message, boolean condition)
	{
		checkCore(IllegalArgumentException.class, message, !condition);
	}

	public static void allTrue(Class<?> exceptionClass, boolean condition)
	{
		checkCore(exceptionClass, null, !condition);
	}

	public static void allTrue(boolean condition)
	{
		checkCore(IllegalArgumentException.class, null, !condition);
	}



	// checks if any elements is null
	public static boolean anyNull(Object... elements)
	{
		boolean anyNull = true;
		if(elements != null)
		{
			anyNull = false;
			elements = normalize(elements);
			for(int i=0; i<elements.length; ++i)
			{
				if(elements[i] == null)
				{
					anyNull = true;
					break;
				}
			}
		}

		return anyNull;
	}


	// checks if every elements is not null
	public static boolean isNotNull(Object... elements)
	{
		return !anyNull(elements);
	}




	public static void notNullWithMessage(Class<?> exceptionClass, String message, Object... obj)
	{
		checkCore(exceptionClass, message, anyNull(obj));
	}

	public static void notNullWithMessage(String message, Object... obj)
	{
		checkCore(IllegalArgumentException.class, message, anyNull(obj));
	}

	public static void notNull(Class<?> exceptionClass, Object... obj)
	{
		checkCore(exceptionClass, null, anyNull(obj));
	}

	public static void notNull(Object... obj)
	{
		checkCore(IllegalArgumentException.class, null,anyNull(obj));
	}




	public static boolean anyEmptyArray(Object[]... array)
	{
		boolean emptyArray = true;
		if((array != null)&&(array.length > 0))
		{
			emptyArray = false;
			for(int i=0; i<array.length; ++i)
			{
				if(array[i] == null || array[i].length == 0)
				{
					emptyArray = true;
					break;
				}
			}
		}
		return emptyArray;
	}







	// checks if any string in str1 contains any string in str2
	public static boolean isContainingAny(String[] str1, String... str2)
	{
		boolean contains = false;
		if(str1 != null && str2 != null)
		{
			for(int i=0; i<str1.length; ++i)
			{
				for(int j=0; j<str2.length; ++j)
				{
					if(str1[i].contains(str2[j]))
					{
						contains = true;
						break;
					}
				}
			}
		}
		return contains;
	}

	public static void notContainingAnyWithMessage(Class<?> exceptionClass, String message, String[] str1, String... str2)
	{
		checkCore(exceptionClass, message, isContainingAny(str1, str2));
	}

	public static void notContainingAnyWithMessage(String message, String[] str1, String... str2)
	{
		checkCore(IllegalArgumentException.class, message, isContainingAny(str1, str2));
	}

	public static void notContainingAny(Class<?> exceptionClass, String[] str1, String... str2)
	{
		checkCore(exceptionClass, null, isContainingAny(str1, str2));
	}

	public static void notContainingAny(String[] str1, String... str2)
	{
		checkCore(IllegalArgumentException.class, null,isContainingAny(str1, str2));
	}



	public static boolean isContainingAny(Object[] str1, String... str2)
	{
		return isContainingAny(toStringArray(str1), str2);
	}

	public static void notContainingAnyWithMessage(Class<?> exceptionClass, String message, Object[] str1, String... str2)
	{
		checkCore(exceptionClass, message, isContainingAny(str1, str2));
	}

	public static void notContainingAnyWithMessage(String message, Object[] str1, String... str2)
	{
		checkCore(IllegalArgumentException.class, message, isContainingAny(str1, str2));
	}

	public static void notContainingAny(Class<?> exceptionClass, Object[] str1, String... str2)
	{
		checkCore(exceptionClass, null, isContainingAny(str1, str2));
	}

	public static void notContainingAny(Object[] str1, String... str2)
	{
		checkCore(IllegalArgumentException.class, null,isContainingAny(str1, str2));
	}




	public static boolean isContainingAny(String[] str1, Object... str2)
	{
		return isContainingAny(str1, toStringArray(str2));
	}

	public static void notContainingAnyWithMessage(Class<?> exceptionClass, String message, String[] str1, Object... str2)
	{
		checkCore(exceptionClass, message, isContainingAny(str1, str2));
	}

	public static void notContainingAnyWithMessage(String message, String[] str1, Object... str2)
	{
		checkCore(IllegalArgumentException.class, message, isContainingAny(str1, str2));
	}

	public static void notContainingAny(Class<?> exceptionClass,String[] str1, Object... str2)
	{
		checkCore(exceptionClass, null, isContainingAny(str1, str2));
	}

	public static void notContainingAny(String[] str1, Object... str2)
	{
		checkCore(IllegalArgumentException.class, null,isContainingAny(str1, str2));
	}






	public static boolean isContainingAny(Object[] str1, Object... str2)
	{
		return isContainingAny(toStringArray(str1), toStringArray(str2));
	}

	public static void notContainingAnyWithMessage(Class<?> exceptionClass, String message, Object[] str1, Object... str2)
	{
		checkCore(exceptionClass, message, isContainingAny(str1, str2));
	}

	public static void notContainingAnyWithMessage(String message, Object[] str1, Object... str2)
	{
		checkCore(IllegalArgumentException.class, message, isContainingAny(str1, str2));
	}

	public static void notContainingAny(Class<?> exceptionClass,Object[] str1, Object... str2)
	{
		checkCore(exceptionClass, null, isContainingAny(str1, str2));
	}

	public static void notContainingAny(Object[] str1, Object... str2)
	{
		checkCore(IllegalArgumentException.class, null,isContainingAny(str1, str2));
	}






	public static boolean isContaining(String str1, String... str2)
	{
		boolean contains = false;
		if(str1 != null && str2 != null)
		{
			for(int i=0; i<str2.length; ++i)
			{
				if(str1.contains(str2[i]))
				{
					contains = true;
					break;
				}
			}
		}
		return contains;
	}





	private static String[] toStringArray(Object[] args)
	{
		String[] converted = null;
		if(args != null)
		{
			if(args instanceof String[])
			{
				converted = (String[]) args;
			}
			else
			{
				converted = new String[args.length];
				for(int i=0; i<args.length; ++i)
				{
					converted[i] = args[i]+"";
				}
			}
		}

		return converted;
	}



	public static boolean isNumeric(Object object)
	{
		boolean numeric = false;

		if(object != null)
		{
			numeric = true;
			try
			{
				Double.parseDouble(object+"");
			}
			catch(Throwable e)
			{
				numeric = false;
			}
		}

		return numeric;
	}



	public static void main(String[] args) throws Exception
	{
		anyBlank("", "s");
	}
}