package transport.util;

public abstract class Assert {

	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isTrue(boolean expression) {
		isTrue(expression, "[Assertion failed] - this expression must be true");
	}

	public static void isNull(Object object, String message) {
		if (object != null) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isNull(Object object) {
		isNull(object, "[Assertion failed] - the object argument must be null");
	}

	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notNull(Object object) {
		notNull(object, "[Assertion failed] - this argument is required; it must not be null");
	}

	public static void hasLength(String text, String message) {
		if (text==null || text.isEmpty()) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void hasLength(String text) {
		hasLength(text, "[Assertion failed] - this String argument must have length; it must not be null or empty");
	}

	public static void hasText(String text, String message) {
		if (text==null || text.isEmpty())  {
			throw new IllegalArgumentException(message);
		}
	}

	public static void hasText(String text) {
		hasText(text, "[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
	}

	public static void isAssignable(Class<?> superType, Class<?> subType) {
		isAssignable(superType, subType, "");
	}

	public static void isAssignable(Class<?> superType, Class<?> subType, String message) {
		notNull(superType, "Type to check against must not be null");
		if (subType == null || !superType.isAssignableFrom(subType)) {
			throw new IllegalArgumentException(message + subType + " is not assignable to " + superType);
		}
	}


	public static void state(boolean expression, String message) {
		if (!expression) {
			throw new IllegalStateException(message);
		}
	}

	public static void state(boolean expression) {
		state(expression, "[Assertion failed] - this state invariant must be true");
	}

}

