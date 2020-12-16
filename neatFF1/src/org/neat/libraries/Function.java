package org.neat.libraries;

public interface Function<T, R> {
	
	public R apply(T value);
	
	public default String name() {
		return "Function";
	}
	
}
