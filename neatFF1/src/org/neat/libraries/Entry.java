package org.neat.libraries;

public class Entry<K, V> implements java.util.Map.Entry {
	
	private Class keyClass;
	private Class valueClass;
	
	private K key;
	private V value;
	
	public Entry(K key, V value) {
		this.key = key;
		this.value = value;
		keyClass = key.getClass();
		valueClass = value.getClass();
	}
	
	public K getKey() {
		return key;
	}
	public V getValue() {
		return value;
	}
	
	public Object setValue(Object value) {
		V pre = this.value;
		this.value = (V)value;
		return pre;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Entry) {
			Entry other = (Entry) obj;
			if (other.keyClass == this.keyClass && other.valueClass == this.valueClass) {
				if (this.key.equals(other.key)) {
					return true;
				}
			}
		}
		if (obj.getClass() == keyClass) {
			if (((K)obj) == key) {
				return true;
			}
		}
		if (obj.getClass() == valueClass) {
			if (((V)obj) == value) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return key+": "+value;
	}
	
}
